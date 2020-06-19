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
import ru.avem.kspad.communication.devices.FR_A800.FRA800Model;
import ru.avem.kspad.communication.devices.beckhoff.BeckhoffModel;
import ru.avem.kspad.communication.devices.pm130.PM130Model;
import ru.avem.kspad.communication.devices.voltmeter.VoltmeterModel;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;
import ru.avem.kspad.utils.Logger;

import static ru.avem.kspad.communication.devices.Device.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.Device.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.Device.PM130_ID;
import static ru.avem.kspad.communication.devices.Device.VOLTMETER_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment6Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Опыт ВИУ";

    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.u)
    TextView mUCell;
    @BindView(R.id.i)
    TextView mICell;
    @BindView(R.id.t)
    TextView mTCell;
    @BindView(R.id.result)
    TextView mResultCell;

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

    private boolean mBeckhoffResponding;
    private boolean mStartState;
    private boolean mProtectionVIU;

    private boolean mFRA800ObjectResponding;
    private boolean mFRA800ObjectReady;

    private boolean mVoltmeterResponding;
    private float mVoltmeterU;

    private boolean mPM130Responding;
    private float mPM130I1;

    private int mSpecifiedU;
    private int mExperimentTime;
    private float mSpecifiedI;
    private float mSpecifiedFrequency;
    private int mIntSpecifiedFrequencyK100;
    private boolean mPlatformOneSelected;

    private boolean mNeedToRefresh;
    private float mU = -1f;
    private float mI;
    private boolean mIsOverI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment6);
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
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U) != 0) {
                mSpecifiedU = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U);
            } else {
                throw new NullPointerException("Не передано specifiedU");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_TIME) != 0) {
                mExperimentTime = extras.getInt(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_TIME);
            } else {
                throw new NullPointerException("Не передано experimentTime");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_I) != 0) {
                mSpecifiedI = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_I);
            } else {
                throw new NullPointerException("Не передано specifiedI");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY) != 0) {
                mSpecifiedFrequency = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY);
                mIntSpecifiedFrequencyK100 = (int) (mSpecifiedFrequency * 100);
            } else {
                throw new NullPointerException("Не передано specifiedFrequency");
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

    private class ExperimentTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            mNeedToRefresh = true;
            setExperimentStart(true);
            setFRA800ObjectReady(false);
            mIsOverI = false;
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setFRA800ObjectResponding(true);
            setPM130Responding(true);
            setVoltmeterResponding(true);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int result = 0;
            changeTextOfView(mStatus, "Испытание началось");
            mDevicesController.initDevicesFrom6Group();
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(1000);
            }
            while (isExperimentStart() && !mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }
            mDevicesController.initDevicesFrom6Group();
            while (isExperimentStart() && !isDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, getNotRespondingDevicesString("Нет связи с устройствами"));
                sleep(100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom6Group();
                sleep(500);
                mDevicesController.setObjectParams(10 * 10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.startObject();
                sleep(2000);
            }
            while (isExperimentStart() && !mFRA800ObjectReady && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь выйдет к заданным характеристикам");
            }

            int lastLevel = regulation(10 * 10, 40, 10, mSpecifiedU, 0.15, 5, 50, 100);

            int experimentTime = mExperimentTime;
            while (isExperimentStart() && (experimentTime > 0) && !mProtectionVIU && mStartState && !mIsOverI && isDevicesResponding()) {
                experimentTime--;
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время испытания. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
                if (mI > mSpecifiedI) {
                    mIsOverI = true;
                    break;
                }
            }
            if (mProtectionVIU || mIsOverI) {
                result = 1;
            }
            mNeedToRefresh = false;

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                sleep(1000);

                for (int i = lastLevel; i > 0; i -= 40) {
                    if (i > 0) {
                        mDevicesController.setObjectUMax(i);
                    }
                }
                mDevicesController.setObjectUMax(0);
                mDevicesController.stopObject();
                sleep(1000);
                mDevicesController.offGround();
            }

            experimentTime = 60;
            while (isExperimentStart() && (experimentTime > 0) && mStartState && isDevicesResponding()) {
                experimentTime--;
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время разряда. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
            }

            mDevicesController.offKMsFrom6Group();

            return result;
        }

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isFRA800ObjectResponding() ? "" : "ЧП ОИ, ",
                    isPM130Responding() ? "" : "PM130, ",
                    isVoltmeterResponding() ? "" : "Вольтметр АВЭМ");
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mDevicesController.diversifyDevices();
            mExperimentSwitch.setChecked(false);
            if (!mCause.equals("")) {
                mStatus.setText(String.format("Испытание прервано по причине: %s", mCause));
                mMainLayout.setBackgroundColor(getResources().getColor(R.color.red));
                mResultCell.setText("Ошибка");
            } else if (!isDevicesResponding()) {
                changeTextOfView(mStatus, getNotRespondingDevicesString("Потеряна связь с устройствами"));
                mMainLayout.setBackgroundColor(getResources().getColor(R.color.red));
            } else {
                if (result == 0) {
                    mResultCell.setText("Выдержал");
                } else if (result == 1) {
                    mResultCell.setText("Не выдержал");
                }
                mStatus.setText("Испытание закончено");
            }
        }
    }

    private int regulation(int start, int coarseStep, int fineStep, int end, double coarseLimit, double fineLimit, int coarseSleep, int fineSleep) {
        double coarseMinLimit = 1 - coarseLimit;
        double coarseMaxLimit = 1 + coarseLimit;
        while (isExperimentStart() && ((mVoltmeterU < end * coarseMinLimit) || (mVoltmeterU > end * coarseMaxLimit)) && mStartState && !mProtectionVIU && isDevicesResponding()) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mVoltmeterU);
            if (mVoltmeterU < end * coarseMinLimit) {
                mDevicesController.setObjectUMax(start += coarseStep);
            } else if (mVoltmeterU > end * coarseMaxLimit) {
                mDevicesController.setObjectUMax(start -= coarseStep);
            }
            sleep(coarseSleep);
            changeTextOfView(mStatus, "Выводим напряжение для получения заданного значения грубо");
            if (mI > mSpecifiedI) {
                mIsOverI = true;
                break;
            }
        }
        while (isExperimentStart() && ((mVoltmeterU < end - fineLimit) || (mVoltmeterU > end + fineLimit)) && mStartState && !mIsOverI && !mProtectionVIU && isDevicesResponding()) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mVoltmeterU);
            if (mVoltmeterU < end - fineLimit) {
                mDevicesController.setObjectUMax(start += fineStep);
            } else if (mVoltmeterU > end + fineLimit) {
                mDevicesController.setObjectUMax(start -= fineStep);
            }
            sleep(fineSleep);
            changeTextOfView(mStatus, "Выводим напряжение для получения заданного значения точно");
            if (mI > mSpecifiedI) {
                mIsOverI = true;
                break;
            }
        }
        return start;
    }


    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isFRA800ObjectResponding() && isPM130Responding() && isVoltmeterResponding();
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
            case FR_A800_OBJECT_ID:
                switch (param) {
                    case FRA800Model.RESPONDING_PARAM:
                        setFRA800ObjectResponding((boolean) value);
                        break;
                    case FRA800Model.READY_PARAM:
                        setFRA800ObjectReady((boolean) value);
                        break;
                }
                break;
            case PM130_ID:
                switch (param) {
                    case PM130Model.RESPONDING_PARAM:
                        setPM130Responding((boolean) value);
                        Logger.withTag("DEVICES_TAG").log("V_PM130 " + ((boolean) value ? "" : "не ") + "отвечает");
                        break;
                    case PM130Model.I1_PARAM:
                        float I1 = (float) value;
                        I1 /= 5f;
                        setPM130I1(I1);
                        Logger.withTag("DEVICES_TAG").log("V_PM130 I1=" + I1);
                        break;
                }
                break;
            case VOLTMETER_ID:
                switch (param) {
                    case VoltmeterModel.RESPONDING_PARAM:
                        setVoltmeterResponding((boolean) value);
                        break;
                    case VoltmeterModel.U_PARAM:
                        setVoltmeterU((float) value);
                        break;
                }
                break;
        }
    }

    private void changeTextOfView(final TextView view, final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.setText(text);
            }
        });
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

    public void setProtectionVIU(boolean protectionVIU) {
        mProtectionVIU = protectionVIU;
    }

    public boolean isFRA800ObjectResponding() {
        return mFRA800ObjectResponding;
    }

    public void setFRA800ObjectResponding(boolean FRA800ObjectResponding) {
        mFRA800ObjectResponding = FRA800ObjectResponding;
    }

    public void setFRA800ObjectReady(boolean FRA800ObjectReady) {
        mFRA800ObjectReady = FRA800ObjectReady;
    }

    public boolean isPM130Responding() {
        return mPM130Responding;
    }

    public void setPM130Responding(boolean PM130Responding) {
        mPM130Responding = PM130Responding;
    }

    public void setPM130I1(float PM130I1) {
        mPM130I1 = PM130I1;
        if (mNeedToRefresh) {
            setI(PM130I1);
        }
    }


    public void setU(float U) {
        mU = U;
        changeTextOfView(mUCell, formatRealNumber(U));
    }

    public void setI(float I) {
        mI = I;
        changeTextOfView(mICell, formatRealNumber(I));
    }

    public boolean isVoltmeterResponding() {
        return mVoltmeterResponding;
    }

    public void setVoltmeterResponding(boolean voltmeterResponding) {
        mVoltmeterResponding = voltmeterResponding;
    }

    public void setVoltmeterU(float voltmeterU) {
        mVoltmeterU = voltmeterU;
        if (mNeedToRefresh) {
            setU(voltmeterU);
        }
    }

    private void clearCells() {
        changeTextOfView(mUCell, "");
        changeTextOfView(mICell, "");
        changeTextOfView(mTCell, "");
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
        data.putExtra(MainActivity.INPUT_PARAMETER.U_VIU_R, mU);
        data.putExtra(MainActivity.INPUT_PARAMETER.T_VIU_R, (float) mExperimentTime);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE6U(mUCell.getText().toString());
        experiments.setE6I(mICell.getText().toString());
        experiments.setE6T(mTCell.getText().toString());
        experiments.setE6Result(mResultCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
