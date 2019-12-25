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

public class Experiment5Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Определение сопротивления обмоток при постоянном токе в практически холодном состоянии";

    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.ab)
    TextView mABCell;
    @BindView(R.id.bc)
    TextView mBCCell;
    @BindView(R.id.ac)
    TextView mACCell;
    @BindView(R.id.average_r)
    TextView mAverageRCell;
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
    private boolean mPlatformOneSelected;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mIKASResponding;
    private float mIKASReady;
    private float mMeasurable;
    private float mAB;
    private float mBC;
    private float mAC;
    private float mAverageR = -1f;
    private float mR20 = -1f;
    private int mType = 1;

    private boolean mTRM201Responding;
    private float mTemp;

    private boolean mExperimentResult;

    private long mFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment5);
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
            mAverageR = -1f;
            mR20 = -1f;
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setIKASResponding(true);
            setTRM201Responding(true);
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
                showCurrentTime("Начало AB");
                mDevicesController.startMeasuringAB();
                sleep(2000);
            }

            while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 101f) && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока 1 измерение закончится");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                sleep(500);
                showCurrentTime("Конец AB");
                setAB(mMeasurable);

                showCurrentTime("Начало BC");
                mDevicesController.startMeasuringBC();
                sleep(2000);
            }

            while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 101f) && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока 2 измерение закончится");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                sleep(500);
                showCurrentTime("Конец BC");
                setBC(mMeasurable);

                showCurrentTime("Начало AC");
                mDevicesController.startMeasuringAC();
                sleep(2000);
            }

            while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 101f) && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока 3 измерение закончится");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                sleep(500);
                showCurrentTime("Конец AC");
                setAC(mMeasurable);

                if ((mAB / mBC >= 0.98) && (mAB / mAC >= 0.98) && (mBC / mAC >= 0.98) && (mAB / mBC <= 1.02) && (mAB / mAC <= 1.02) && (mBC / mAC <= 1.02)) {
                    setExperimentResult(true);
                    float ABtoAverageR = Math.abs(mAB - mAverageR);
                    float BCtoAverageR = Math.abs(mBC - mAverageR);
                    float ACtoAverageR = Math.abs(mAC - mAverageR);
                    if (ABtoAverageR <= BCtoAverageR && ABtoAverageR <= ACtoAverageR) {
                        mType = 1;
                    } else if (BCtoAverageR <= ABtoAverageR && BCtoAverageR <= ACtoAverageR) {
                        mType = 2;
                    } else {
                        mType = 3;
                    }

                    float averageRHalf = mAverageR / 2f;
                    mR20 = averageRHalf / (1 + 0.00393f * (mTemp - 20));
                } else {
                    setExperimentResult(false);
                }
            }


            mDevicesController.offKMsFrom5And17Group();

            return null;
        }

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isIKASResponding() ? "" : "ИКАС, ",
                    isTRM201Responding() ? "" : "ТРМ");
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

    private void showCurrentTime(String s) {
        long lastTime = System.currentTimeMillis();
        Logger.withTag("currentTime").log(String.format(RU_LOCALE, "%s: %d", s, (lastTime - mFirstTime) / 1000));
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

    public void setAB(float AB) {
        if (AB < 10000) {
            mAB = AB;
            changeTextOfView(mABCell, formatRealNumber(AB));
        } else {
            mAB = -1f;
            changeTextOfView(mABCell, "Обрыв");
        }
    }

    public void setBC(float BC) {
        if (BC < 10000) {
            mBC = BC;
            changeTextOfView(mBCCell, formatRealNumber(BC));
        } else {
            mBC = -1f;
            changeTextOfView(mBCCell, "Обрыв");
        }
    }

    public void setAC(float AC) {
        if (AC < 10000) {
            mAC = AC;
            changeTextOfView(mACCell, formatRealNumber(AC));
        } else {
            mAC = -1f;
            changeTextOfView(mACCell, "Обрыв");
        }
        setAverageR();
    }

    public void setAverageR() {
        mAverageR = (mAB + mBC + mAC) / 3f;
        changeTextOfView(mAverageRCell, formatRealNumber(mAverageR));
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
        changeTextOfView(mABCell, "");
        changeTextOfView(mBCCell, "");
        changeTextOfView(mACCell, "");
        changeTextOfView(mAverageRCell, "");
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
        data.putExtra(MainActivity.INPUT_PARAMETER.IKAS_R_COLD_R, mAverageR);
        data.putExtra(MainActivity.INPUT_PARAMETER.IKAS_R_20_R, mR20);
        data.putExtra(MainActivity.INPUT_PARAMETER.IKAS_R_TYPE_R, mType);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE5Ab(mABCell.getText().toString());
        experiments.setE5Bc(mBCCell.getText().toString());
        experiments.setE5Ac(mACCell.getText().toString());
        experiments.setE5AverageR(mAverageRCell.getText().toString());
        experiments.setE5Temp(mTempCell.getText().toString());
        experiments.setE5Result(mResultCell.getText().toString());
        experiments.setE5AverageRSpecified(mAverageRSpecifiedCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
