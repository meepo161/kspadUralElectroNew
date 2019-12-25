package ru.avem.kspad.view;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import io.realm.Realm;
import ru.avem.kspad.R;
import ru.avem.kspad.communication.devices.DevicesController;
import ru.avem.kspad.communication.devices.beckhoff.BeckhoffModel;
import ru.avem.kspad.communication.devices.ikas.IKASModel;
import ru.avem.kspad.communication.devices.trm201.TRM201Model;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;
import ru.avem.kspad.utils.Logger;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.IKAS_ID;
import static ru.avem.kspad.communication.devices.DeviceController.TRM201_ID;
import static ru.avem.kspad.utils.Utils.RU_LOCALE;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment17Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Определение сопротивления обмоток при постоянном токе в горячем состоянии";

    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.r1)
    TextView mR1Cell;
    @BindView(R.id.r2)
    TextView mR2Cell;
    @BindView(R.id.r3)
    TextView mR3Cell;
    @BindView(R.id.extrapolated_r)
    TextView mExtrapolatedRCell;
    @BindView(R.id.temp)
    TextView mTempCell;
    @BindView(R.id.result)
    TextView mResultCell;
    @BindView(R.id.average_r_specified)
    TextView mAverageRSpecifiedCell;

    private DevicesController mDevicesController;
    private final Handler mHandler = new Handler();
    private OnBroadcastCallback mOnBroadcastCallback = new OnBroadcastCallback() {
        @Override
        public void onBroadcastUsbReceiver(BroadcastReceiver broadcastReceiver) {
            mBroadcastReceiver = broadcastReceiver;
        }
    };
    private BroadcastReceiver mBroadcastReceiver;

    private boolean mExperimentStart;
    private String mCause = "";
    private float mSpecifiedAverageR;
    private int mSpecifiedRType;
    private boolean mPlatformOneSelected;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mIKASResponding;
    private float mIKASReady;
    private float mMeasurable;
    private float mR1;
    private float mR2;
    private float mR3;
    private double mR1Time;
    private double mR2Time;
    private double mR3Time;
    private float mExtrapolatedR = -1f;

    private boolean mTRM201Responding;
    private float mTemp;

    private boolean mExperimentResult;

    private long mFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment17);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_NAME) != null) {
                String experimentName = extras.getString(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_NAME);
                if (!Objects.equals(experimentName, EXPERIMENT_NAME)) {
                    throw new IllegalArgumentException(String.format("Передано: %s. Требуется: %s.", experimentName, EXPERIMENT_NAME));
                }
            } else {
                throw new NullPointerException("Не передано " + MainActivity.OUTPUT_PARAMETER.EXPERIMENT_NAME);
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_R) != 0) {
                mSpecifiedAverageR = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_R);
                changeTextOfView(mAverageRSpecifiedCell, String.format("%s", mSpecifiedAverageR));
            } else {
                throw new NullPointerException("Не передано specifiedR");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_R_TYPE) != 0) {
                mSpecifiedRType = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_R_TYPE);
            } else {
                throw new NullPointerException("Не передано specifiedRType");
            }
            mPlatformOneSelected = extras.getBoolean(MainActivity.OUTPUT_PARAMETER.PLATFORM_ONE_SELECTED);
        } else {
            throw new NullPointerException("Не переданы параметры");
        }

        mDevicesController = new DevicesController(this, this, mOnBroadcastCallback, mPlatformOneSelected);
        mStatus.setText("В ожидании начала испытания");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        mDevicesController.setNeededToRunThreads(false);
    }

    @OnCheckedChanged(R.id.experiment_switch)
    public void onCheckedChanged(CompoundButton compoundButton) {
        if (compoundButton.isChecked()) {
            initExperiment();
        } else {
            setExperimentStart(false);
        }
    }

    private void initExperiment() {
        new ExperimentTask().execute();
    }

    public boolean isExperimentStart() {
        return mExperimentStart;
    }

    public void setExperimentStart(boolean experimentStart) {
        mExperimentStart = experimentStart;
    }

    private class ExperimentTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            setExperimentStart(true);
            mExtrapolatedR = -1f;
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setTRM201Responding(true);
            setIKASResponding(true);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            changeTextOfView(mStatus, "Испытание началось");
            mDevicesController.initDevicesFrom5And17Group();
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(100);
            }
            while (isExperimentStart() && !mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }
            if (isExperimentStart() && mStartState) {
                mDevicesController.initDevicesFrom5And17Group();
            }
            while (isExperimentStart() && !isDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, getNotRespondingDevicesString("Нет связи с устройствами"));
                sleep(100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom5And17Group();
            }

            while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 1f) && (mIKASReady != 101f) && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока ИКАС подготовится");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mFirstTime = System.currentTimeMillis();
                showCurrentTime("Начало R1");
                startMeasuring();
                sleep(2000);
            }

            while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 101f) && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока 1 измерение закончится");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                sleep(500);
                mR1Time = showCurrentTime("Конец R1");
                setR1(mMeasurable);
//                setR1(22);

                showCurrentTime("Начало R2");
                startMeasuring();
                sleep(2000);
            }

            while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 101f) && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока 2 измерение закончится");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                sleep(500);
                mR2Time = showCurrentTime("Конец R2");
                setR2(mMeasurable);
//                setR2(21);

                showCurrentTime("Начало R3");
                sleep(11500);
                startMeasuring();
                sleep(2000);
            }

            while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 101f) && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока 3 измерение закончится");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                sleep(500);
                mR3Time = showCurrentTime("Конец R3");
                setR3(mMeasurable);
//                setR3(20);

                setExperimentResult(true);
            }


            mDevicesController.offKMsFrom5And17Group();

            return null;
        }

        private void startMeasuring() {
            if (mSpecifiedRType == 1) {
                Logger.withTag("currentTime").log("AB");
                mDevicesController.startMeasuringAB();
            } else if (mSpecifiedRType == 2) {
                Logger.withTag("currentTime").log("BC");
                mDevicesController.startMeasuringBC();
            } else {
                Logger.withTag("currentTime").log("AC");
                mDevicesController.startMeasuringAC();
            }
        }

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isTRM201Responding() ? "" : "ТРМ, ",
                    isIKASResponding() ? "" : "ИКАС");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDevicesController.diversifyDevices();
            mExperimentSwitch.setChecked(false);
            if (!mCause.equals("")) {
                mStatus.setText(String.format("Испытание прервано по причине: %s", mCause));
                mMainLayout.setBackgroundColor(getResources().getColor(R.color.red));
            } else if (!isDevicesResponding()) {
                changeTextOfView(mStatus, getNotRespondingDevicesString("Потеряна связь с устройствами"));
                mMainLayout.setBackgroundColor(getResources().getColor(R.color.red));
            } else {
                mStatus.setText("Испытание закончено");
            }
        }
    }

    private int showCurrentTime(String s) {
        long lastTime = System.currentTimeMillis();
        int diffInSec = (int) ((lastTime - mFirstTime)); //  / 1000
        Logger.withTag("currentTime").log(String.format(RU_LOCALE, "%s: %d", s, diffInSec));
        return diffInSec;
    }

    private void changeTextOfView(final TextView view, final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.setText(text);
            }
        });
    }

    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isIKASResponding() && isTRM201Responding();
    }

    @Override
    public void update(Observable o, Object values) {
        int modelId = (int) (((Object[]) values)[0]);
        int param = (int) (((Object[]) values)[1]);
        Object value = (((Object[]) values)[2]);

        switch (modelId) {
            case BECKHOFF_CONTROL_ID:
                switch (param) {
                    case BeckhoffModel.RESPONDING_PARAM:
                        setBeckhoffResponding((boolean) value);
                        break;
                    case BeckhoffModel.START_PARAM:
                        setStartState((boolean) value);
                        break;
                    case BeckhoffModel.DOOR_S_TRIGGER_PARAM:
                        if ((boolean) value) {
                            mCause = "открылась дверь шкафа";
                            setExperimentStart(false);
                        }
                        break;
                    case BeckhoffModel.I_PROTECTION_OBJECT_TRIGGER_PARAM:
                        if ((boolean) value) {
                            mCause = "сработала токовая защита объекта испытания";
                            setExperimentStart(false);
                        }
                        break;
                    case BeckhoffModel.I_PROTECTION_VIU_TRIGGER_PARAM:
                        if ((boolean) value) {
                            mCause = "сработала токовая защита ВИУ";
                            setExperimentStart(false);
                        }
                        break;
                    case BeckhoffModel.I_PROTECTION_IN_TRIGGER_PARAM:
                        if ((boolean) value) {
                            mCause = "сработала токовая защита по входу";
                            setExperimentStart(false);
                        }
                        break;
                    case BeckhoffModel.DOOR_Z_TRIGGER_PARAM:
                        if ((boolean) value) {
                            mCause = "открылась дверь зоны";
                            setExperimentStart(false);
                        }
                        break;
                }
                break;
            case IKAS_ID:
                switch (param) {
                    case IKASModel.RESPONDING_PARAM:
                        setIKASResponding((boolean) value);
                        break;
                    case IKASModel.READY_PARAM:
                        setIKASReady((float) value);
                        break;
                    case IKASModel.MEASURABLE_PARAM:
                        setMeasurable((float) value);
                        break;
                }
                break;
            case TRM201_ID:
                switch (param) {
                    case TRM201Model.RESPONDING_PARAM:
                        setTRM201Responding((boolean) value);
                        break;
                    case TRM201Model.T_AMBIENT_PARAM:
                        setTemp((float) value);
                        break;
                }
                break;
        }
    }

    public boolean isBeckhoffResponding() {
        return mBeckhoffResponding;
    }

    public void setBeckhoffResponding(boolean beckhoffResponding) {
        mBeckhoffResponding = beckhoffResponding;
    }

    public void setStartState(boolean startState) {
        mStartState = startState;
    }

    public boolean isIKASResponding() {
        return mIKASResponding;
    }

    public void setIKASResponding(boolean IKASResponding) {
        mIKASResponding = IKASResponding;
    }

    public void setIKASReady(float IKASReady) {
        mIKASReady = IKASReady;
    }

    public void setMeasurable(float measurable) {
        mMeasurable = measurable;
    }

    public void setR1(float r1) {
        if (r1 < 10000) {
            mR1 = r1;
            changeTextOfView(mR1Cell, formatRealNumber(r1));
        } else {
            mR1 = -1f;
            changeTextOfView(mR1Cell, "Обрыв");
        }
    }

    public void setR2(float r2) {
        if (r2 < 10000) {
            mR2 = r2;
            changeTextOfView(mR2Cell, formatRealNumber(r2));
        } else {
            mR2 = -1f;
            changeTextOfView(mR2Cell, "Обрыв");
        }
    }

    public void setR3(float r3) {
        if (r3 < 10000) {
            mR3 = r3;
            changeTextOfView(mR3Cell, formatRealNumber(r3));
        } else {
            mR3 = -1f;
            changeTextOfView(mR3Cell, "Обрыв");
        }
        setExtrapolatedRCellR();
    }

    public void setExtrapolatedRCellR() {
        double mExtrapolatedRLog = (((0 - mR2Time) * (0 - mR3Time)) / ((mR1Time - mR2Time) * (mR1Time - mR3Time)) * Math.log10(mR1) +
                ((0 - mR1Time) * (0 - mR3Time)) / ((mR2Time - mR1Time) * (mR2Time - mR3Time)) * Math.log10(mR2) +
                ((0 - mR1Time) * (0 - mR2Time)) / ((mR3Time - mR1Time) * (mR3Time - mR2Time)) * Math.log10(mR3));
        mExtrapolatedR = (float) Math.pow(10, mExtrapolatedRLog);
        changeTextOfView(mExtrapolatedRCell, formatRealNumber(mExtrapolatedR));
    }

    public boolean isTRM201Responding() {
        return mTRM201Responding;
    }

    public void setTRM201Responding(boolean TRM201Responding) {
        mTRM201Responding = TRM201Responding;
    }

    public void setTemp(float temp) {
        mTemp = temp;
        changeTextOfView(mTempCell, formatRealNumber(temp));
    }

    public void setExperimentResult(boolean experimentResult) {
        mExperimentResult = experimentResult;
        changeTextOfView(mResultCell, experimentResult ? "Успешно" : "Неуспешно");
    }

    private void clearCells() {
        changeTextOfView(mR1Cell, "");
        changeTextOfView(mR2Cell, "");
        changeTextOfView(mR3Cell, "");
        changeTextOfView(mExtrapolatedRCell, "");
        changeTextOfView(mTempCell, "");
        changeTextOfView(mResultCell, "");
    }

    @Override
    public void onBackPressed() {
        setExperimentStart(false);
        returnValues();
        fillExperimentTable();
        finish();
    }

    private void returnValues() {
        Intent data = new Intent();
        data.putExtra(MainActivity.INPUT_PARAMETER.IKAS_R_HOT_R, mExtrapolatedR);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE17Ab(mR1Cell.getText().toString());
        experiments.setE17Bc(mR2Cell.getText().toString());
        experiments.setE17Ac(mR3Cell.getText().toString());
        experiments.setE17AverageR(mExtrapolatedRCell.getText().toString());
        experiments.setE17Temp(mTempCell.getText().toString());
        experiments.setE17Result(mResultCell.getText().toString());
        experiments.setE17AverageRSpecified(mAverageRSpecifiedCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
