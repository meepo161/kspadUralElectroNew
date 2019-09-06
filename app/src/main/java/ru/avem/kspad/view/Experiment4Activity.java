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

import java.util.ArrayList;
import java.util.List;
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
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;
import ru.avem.kspad.utils.Logger;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.DeviceController.PM130_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.setNextValueAndReturnAverage;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment4Activity extends AppCompatActivity implements Observer {
    //region Константы
    private static final String EXPERIMENT_NAME = "Опыт проверки межвитковой изоляции";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int STATE_40_TO_5_MULTIPLIER = 40 / 5;
    private static final int STATE_5_TO_5_MULTIPLIER = 5 / 5;
    //endregion

    //region Виджеты
    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.u1)
    TextView mU1Cell;
    @BindView(R.id.u2)
    TextView mU2Cell;
    @BindView(R.id.u3)
    TextView mU3Cell;
    @BindView(R.id.i1)
    TextView mI1Cell;
    @BindView(R.id.i2)
    TextView mI2Cell;
    @BindView(R.id.i3)
    TextView mI3Cell;
    @BindView(R.id.result)
    TextView mResultCell;
    @BindView(R.id.t)
    TextView mTCell;
    @BindView(R.id.t_specified)
    TextView mTSpecifiedCell;
    //endregion

    //region Сервис
    private DevicesController mDevicesController;
    private final Handler mHandler = new Handler();
    private OnBroadcastCallback mOnBroadcastCallback = new OnBroadcastCallback() {
        @Override
        public void onBroadcastUsbReceiver(BroadcastReceiver broadcastReceiver) {
            mBroadcastReceiver = broadcastReceiver;
        }
    };
    private BroadcastReceiver mBroadcastReceiver;
    private boolean mThreadOn;
    //endregion

    //region Испытание
    private boolean mExperimentStart;
    private String mCause = "";
    private int mCurrentStage;

    private int mExperimentTime;
    private float mSpecifiedFrequency;
    private int mIntSpecifiedFrequencyK100;
    private boolean mPlatformOneSelected;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mFRA800ObjectResponding;
    private boolean mFRA800ObjectReady;

    private boolean mPM130Responding;

    private float mUA;
    private List<Float> mSeveralUA = new ArrayList<>();
    private float mUAAverage;

    private float mUB;
    private List<Float> mSeveralUB = new ArrayList<>();
    private float mUBAverage;

    private float mUC;
    private List<Float> mSeveralUC = new ArrayList<>();
    private float mUCAverage;

    private float mUAverage;

    private boolean m200to5State;
    private boolean m40to5State;
    private boolean m5to5State;

    private float mIA;
    private List<Float> mSeveralIA = new ArrayList<>();
    private float mIAAverage;

    private float mIB;
    private List<Float> mSeveralIB = new ArrayList<>();
    private float mIBAverage;

    private float mIC;
    private List<Float> mSeveralIC = new ArrayList<>();
    private float mICAverage;

    private float mIAverage;

    private float mU1 = -1f;
    private float mI1 = -1f;

    private float mU2 = -1f;
    private float mI2 = -1f;

    private float mU3 = -1f;
    private float mI3 = -1f;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment4);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_NAME) != null) {
                String experimentName = extras.getString(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_NAME);
                if (!Objects.equals(experimentName, EXPERIMENT_NAME)) {
                    throw new IllegalArgumentException(String.format("Передано: %s. Требуется: %s.",
                            experimentName, EXPERIMENT_NAME));
                }
            } else {
                throw new NullPointerException("Не передано " + MainActivity.OUTPUT_PARAMETER.EXPERIMENT_NAME);
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY) != 0) {
                mSpecifiedFrequency = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY);
                mIntSpecifiedFrequencyK100 = (int) (mSpecifiedFrequency * 100);
            } else {
                throw new NullPointerException("Не передано specifiedFrequency");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_TIME) != 0) {
                mExperimentTime = extras.getInt(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_TIME);
                changeTextOfView(mTSpecifiedCell, formatRealNumber(mExperimentTime));
            } else {
                throw new NullPointerException("Не передано experimentTime");
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
        if (compoundButton.isChecked() && !mThreadOn) {
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

    private class ExperimentTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            mCurrentStage = 1;
            setExperimentStart(true);
            mThreadOn = true;
            setFRA800ObjectReady(false);
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setFRA800ObjectResponding(true);
            setPM130Responding(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isExperimentStart()) {
                changeTextOfView(mStatus, "Испытание началось");
                mDevicesController.initDevicesFrom4Group();
            }
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(100);
            }
            while (isExperimentStart() && !mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }
            if (isExperimentStart() && mStartState) {
                mDevicesController.initDevicesFrom4Group();
            }
            while (isExperimentStart() && !isDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, getNotRespondingDevicesString("Нет связи с устройствами"));
                sleep(100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom4And7And13Group();
                m200to5State = true;
                sleep(500);
                mDevicesController.setObjectParams(10 * 10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
                mDevicesController.startObject();
            }
            while (isExperimentStart() && !mFRA800ObjectReady && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь ОИ выйдет к заданным характеристикам");
            }
            int t = 5;
            while (isExperimentStart() && (--t > 0) && mStartState && isDevicesResponding()) {
                sleep(1000);
            }

            int lastLevel = regulation(10 * 10, 20, 5, 380, 0.15, 2, 100, 200);
            int level380 = lastLevel;

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                pickUpState();
                sleep(5000);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mCurrentStage = 2;
                stateToBack();
            }

            lastLevel = regulation(lastLevel, 20, 5, 500, 0.15, 2, 100, 200);


            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                pickUpState();
                sleep(5000);
            }

            float lastI = mIAverage;
            int experimentTime = mExperimentTime;
            while (isExperimentStart() && (experimentTime > 0) && mStartState && isDevicesResponding()) {
                experimentTime--;
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
                if (mIAverage > lastI * 2) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCause = "Испытание закончено с ошибкой. Причина: резкое превышение тока.";
                            mExperimentSwitch.setChecked(false);
                        }
                    });
                } else {
                    lastI = mIAverage;
                }
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mCurrentStage = 3;
                stateToBack();

                for (int i = lastLevel; i > level380; i -= 100) {
                    mDevicesController.setObjectUMax(i);
                }
                mDevicesController.setObjectUMax(level380);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                sleep(5000);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                pickUpState();
                sleep(5000);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mCurrentStage = 4;
                stateToBack();
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                for (int i = level380; i > 0; i -= 40) {
                    if (i > 0) {
                        mDevicesController.setObjectUMax(i);
                    }
                }
                mDevicesController.setObjectUMax(0);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                compareAmperage();
            }

            mDevicesController.stopObject();
            mDevicesController.offKMsFrom4And7And13Group();
            m200to5State = false;
            m40to5State = false;
            m5to5State = false;

            return null;
        }

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isFRA800ObjectResponding() ? "" : "ЧП ОИ, ",
                    isPM130Responding() ? "" : "PM130");
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
            mThreadOn = false;
        }
    }

    private int regulation(int start, int coarseStep, int fineStep, int end, double coarseLimit, double fineLimit, int coarseSleep, int fineSleep) {
        double coarseMinLimit = 1 - coarseLimit;
        double coarseMaxLimit = 1 + coarseLimit;
        while (isExperimentStart() && ((mUA < end * coarseMinLimit) || (mUA > end * coarseMaxLimit)) && mStartState && isDevicesResponding()) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mUA);
            if (mUA < end * coarseMinLimit) {
                mDevicesController.setObjectUMax(start += coarseStep);
            } else if (mUA > end * coarseMaxLimit) {
                mDevicesController.setObjectUMax(start -= coarseStep);
            }
            sleep(coarseSleep);
            changeTextOfView(mStatus, "Выводим напряжение для получения заданного значения грубо");
        }
        while (isExperimentStart() && ((mUA < end - fineLimit) || (mUA > end + fineLimit)) && mStartState && isDevicesResponding()) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mUA);
            if (mUA < end - fineLimit) {
                mDevicesController.setObjectUMax(start += fineStep);
            } else if (mUA > end + fineLimit) {
                mDevicesController.setObjectUMax(start -= fineStep);
            }
            sleep(fineSleep);
            changeTextOfView(mStatus, "Выводим напряжение для получения заданного значения точно");
        }
        return start;
    }

    private void changeTextOfView(final TextView view, final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.setText(text);
            }
        });
    }

    private void pickUpState() {
        if (mIA < 45) {
            mDevicesController.on40To5();
            m40to5State = true;
            m200to5State = false;
            m5to5State = false;
            sleep(3 * 1000);
            if (mIA < 6) {
                mDevicesController.on5To5();
                m5to5State = true;
                m40to5State = false;
                m200to5State = false;
                sleep(3 * 1000);
            }
        }
    }

    private void stateToBack() {
        if (!m40to5State && !m200to5State) {
            mDevicesController.on40To5();
            m40to5State = true;
        }
        sleep(100);
        if (m40to5State) {
            mDevicesController.on200To5();
            m200to5State = true;
            m40to5State = false;
        }
    }

    private void compareAmperage() {
        float diff = Math.abs(mI1 - mI3);
        if ((diff / mI3) < 0.05) {
            changeTextOfView(mResultCell, "Выдержал");
        } else {
            changeTextOfView(mResultCell, "Не выдержал");
        }
    }

    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isFRA800ObjectResponding() && isPM130Responding();
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
                        break;
                    case PM130Model.V1_PARAM:
                        setUA((float) value);
                        break;
                    case PM130Model.V2_PARAM:
                        setUB((float) value);
                        break;
                    case PM130Model.V3_PARAM:
                        setUC((float) value);
                        break;
                    case PM130Model.I1_PARAM:
                        float IA = (float) value;
                        if (is200to5State()) {
                            IA *= STATE_200_TO_5_MULTIPLIER;
                        } else if (is40to5State()) {
                            IA *= STATE_40_TO_5_MULTIPLIER;
                        } else if (is5to5State()) {
                            IA *= STATE_5_TO_5_MULTIPLIER;
                        }
                        setIA(IA);
                        break;
                    case PM130Model.I2_PARAM:
                        float IB = (float) value;
                        if (is200to5State()) {
                            IB *= STATE_200_TO_5_MULTIPLIER;
                        } else if (is40to5State()) {
                            IB *= STATE_40_TO_5_MULTIPLIER;
                        } else if (is5to5State()) {
                            IB *= STATE_5_TO_5_MULTIPLIER;
                        }
                        setIB(IB);
                        break;
                    case PM130Model.I3_PARAM:
                        float IC = (float) value;
                        if (is200to5State()) {
                            IC *= STATE_200_TO_5_MULTIPLIER;
                        } else if (is40to5State()) {
                            IC *= STATE_40_TO_5_MULTIPLIER;
                        } else if (is5to5State()) {
                            IC *= STATE_5_TO_5_MULTIPLIER;
                        }
                        setIC(IC);
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

    public void setUA(float UA) {
        mUA = UA;
        setUAAverage(UA);
    }

    public void setUAAverage(float UAAverage) {
        float averageUA = setNextValueAndReturnAverage(mSeveralUA, UAAverage);
        if (averageUA != -1) {
            mUAAverage = averageUA;
        }
    }

    public void setUB(float UB) {
        mUB = UB;
        setUBAverage(UB);
    }

    public void setUBAverage(float UBAverage) {
        float averageUB = setNextValueAndReturnAverage(mSeveralUB, UBAverage);
        if (averageUB != -1) {
            mUBAverage = averageUB;
        }
    }

    public void setUC(float UC) {
        mUC = UC;
        setUCAverage(UC);
    }

    public void setUCAverage(float UCAverage) {
        float averageUC = setNextValueAndReturnAverage(mSeveralUC, UCAverage);
        if (averageUC != -1) {
            mUCAverage = averageUC;
            setUAverage();
        }
    }

    public void setUAverage() {
        mUAverage = (mUAAverage + mUBAverage + mUCAverage) / 3f;
        switch (mCurrentStage) {
            case 1:
                setU1(mUAverage);
                break;
            case 2:
                setU2(mUAverage);
                break;
            case 3:
                setU3(mUAverage);
                break;
        }
    }

    public void setU1(float u1) {
        mU1 = u1;
        changeTextOfView(mU1Cell, formatRealNumber(u1));
    }

    public void setU2(float u2) {
        mU2 = u2;
        changeTextOfView(mU2Cell, formatRealNumber(u2));
    }

    public void setU3(float u3) {
        mU3 = u3;
        changeTextOfView(mU3Cell, formatRealNumber(u3));
    }

    public boolean is200to5State() {
        return m200to5State;
    }

    public boolean is40to5State() {
        return m40to5State;
    }

    public boolean is5to5State() {
        return m5to5State;
    }

    public void setIA(float IA) {
        mIA = IA;
        setIAAverage(IA);
    }

    public void setIAAverage(float IAAverage) {
        float averageIA = setNextValueAndReturnAverage(mSeveralIA, IAAverage);
        if (averageIA != -1) {
            mIAAverage = averageIA;
        }
    }

    public void setIB(float IB) {
        mIB = IB;
        setIBAverage(IB);
    }

    public void setIBAverage(float IBAverage) {
        float averageIB = setNextValueAndReturnAverage(mSeveralIB, IBAverage);
        if (averageIB != -1) {
            mIBAverage = averageIB;
        }
    }

    public void setIC(float IC) {
        mIC = IC;
        setICAverage(IC);
    }

    public void setICAverage(float ICAverage) {
        float averageIC = setNextValueAndReturnAverage(mSeveralIC, ICAverage);
        if (averageIC != -1) {
            mICAverage = averageIC;
            setIAverage();
        }
    }

    public void setIAverage() {
        mIAverage = (mIAAverage + mIBAverage + mICAverage) / 3f;
        switch (mCurrentStage) {
            case 1:
                setI1(mIAverage);
                break;
            case 2:
                setI2(mIAverage);
                break;
            case 3:
                setI3(mIAverage);
                break;
        }
    }

    public void setI1(float i1) {
        mI1 = i1;
        changeTextOfView(mI1Cell, formatRealNumber(i1));
    }

    public void setI2(float i2) {
        mI2 = i2;
        changeTextOfView(mI2Cell, formatRealNumber(i2));
    }

    public void setI3(float i3) {
        mI3 = i3;
        changeTextOfView(mI3Cell, formatRealNumber(i3));
    }

    private void clearCells() {
        changeTextOfView(mU1Cell, "");
        changeTextOfView(mU2Cell, "");
        changeTextOfView(mU3Cell, "");
        changeTextOfView(mI1Cell, "");
        changeTextOfView(mI2Cell, "");
        changeTextOfView(mI3Cell, "");
        changeTextOfView(mResultCell, "");
        changeTextOfView(mTCell, "");
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
        data.putExtra(MainActivity.INPUT_PARAMETER.I_MVZ1_R, mI1);
        data.putExtra(MainActivity.INPUT_PARAMETER.I_MVZ2_R, mI2);
        data.putExtra(MainActivity.INPUT_PARAMETER.I_MVZ3_R, mI3);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE4U1(mU1Cell.getText().toString());
        experiments.setE4U2(mU2Cell.getText().toString());
        experiments.setE4U3(mU3Cell.getText().toString());
        experiments.setE4I1(mI1Cell.getText().toString());
        experiments.setE4I2(mI2Cell.getText().toString());
        experiments.setE4I3(mI3Cell.getText().toString());
        experiments.setE4Result(mResultCell.getText().toString());
        experiments.setE4T(mTCell.getText().toString());
        experiments.setE4TSpecified(mTSpecifiedCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
