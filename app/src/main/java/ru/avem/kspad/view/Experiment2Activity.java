package ru.avem.kspad.view;

import android.content.BroadcastReceiver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import ru.avem.kspad.communication.devices.m40.M40Model;
import ru.avem.kspad.communication.devices.pm130.PM130Model;
import ru.avem.kspad.communication.devices.trm201.TRM201Model;
import ru.avem.kspad.communication.devices.veha_t.VEHATModel;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_GENERATOR_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.DeviceController.M40_ID;
import static ru.avem.kspad.communication.devices.DeviceController.PM130_ID;
import static ru.avem.kspad.communication.devices.DeviceController.TRM201_ID;
import static ru.avem.kspad.communication.devices.DeviceController.VEHA_T_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.getSyncV;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment2Activity extends AppCompatActivity implements Observer {
    //region Константы
    private static final String EXPERIMENT_NAME = "Опыт перегрузки по моменту";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int STATE_40_TO_5_MULTIPLIER = 40 / 5;
    private static final int STATE_5_TO_5_MULTIPLIER = 5 / 5;

    private static final float NUM_OF_POINTS = 3f;
    //endregion

    //region Виджеты
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.i_a)
    TextView mIACell;
    @BindView(R.id.u_a)
    TextView mUACell;
    @BindView(R.id.i_b)
    TextView mIBCell;
    @BindView(R.id.u_b)
    TextView mUBCell;
    @BindView(R.id.s)
    TextView mSCell;
    @BindView(R.id.p1)
    TextView mP1Cell;
    @BindView(R.id.cos)
    TextView mCosCell;
    @BindView(R.id.m)
    TextView mMCell;
    @BindView(R.id.v)
    TextView mVCell;
    @BindView(R.id.p2)
    TextView mP2Cell;
    @BindView(R.id.nu)
    TextView mNuCell;
    @BindView(R.id.temp_ambient)
    TextView mTempAmbientCell;
    @BindView(R.id.temp_engine)
    TextView mTempEngineCell;
    @BindView(R.id.sk)
    TextView mSkCell;
    @BindView(R.id.t)
    TextView mTCell;
    @BindView(R.id.i_c)
    TextView mICCell;
    @BindView(R.id.u_c)
    TextView mUCCell;
    @BindView(R.id.i_average)
    TextView mIAverageCell;
    @BindView(R.id.u_average)
    TextView mUAverageCell;
    @BindView(R.id.s_average)
    TextView mSAverageCell;
    @BindView(R.id.p1_average)
    TextView mP1AverageCell;
    @BindView(R.id.cos_average)
    TextView mCosAverageCell;
    @BindView(R.id.m_average)
    TextView mMAverageCell;
    @BindView(R.id.v_average)
    TextView mVAverageCell;
    @BindView(R.id.p2_average)
    TextView mP2AverageCell;
    @BindView(R.id.nu_average)
    TextView mNuAverageCell;
    @BindView(R.id.sk_average)
    TextView mSkAverageCell;
    @BindView(R.id.temp_engine_average)
    TextView mTempEngineAverageCell;

    @BindView(R.id.i_specified)
    TextView mISpecifiedCell;
    @BindView(R.id.u_specified)
    TextView mUSpecifiedCell;
    @BindView(R.id.s_specified)
    TextView mSSpecifiedCell;
    @BindView(R.id.p1_specified)
    TextView mP1SpecifiedCell;
    @BindView(R.id.cos_specified)
    TextView mCosSpecifiedCell;
    @BindView(R.id.m_specified)
    TextView mMSpecifiedCell;
    @BindView(R.id.v_specified)
    TextView mVSpecifiedCell;
    @BindView(R.id.p2_specified)
    TextView mP2SpecifiedCell;
    @BindView(R.id.nu_specified)
    TextView mNuSpecifiedCell;
    @BindView(R.id.sk_specified)
    TextView mSkSpecifiedCell;
    @BindView(R.id.temp_engine_specified)
    TextView mTempEngineSpecifiedCell;
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
    private boolean mNeededToSave;

    private double mZ1;
    private double mZ2;
    private double mV1;
    private int mSyncV;
    private double mV2 = 1500.0;
    private float mSpecifiedAmperage;
    private float mSpecifiedTorque;
    private int mExperimentTimeIdle;
    private int mExperimentTime;
    private float mSpecifiedFrequency;
    private int mIntSpecifiedFrequencyK100;
    private int mSpecifiedU;
    private int mSpecifiedUK10;
    private float mSpecifiedP2;
    private float mSpecifiedEff;
    private float mSpecifiedSk;
    private boolean mPlatformOneSelected;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mM40Responding;

    private float mM;
    private List<Float> mSeveralM = new ArrayList<>();
    private float mMAverage;

    private boolean mVEHATResponding;

    private float mV;
    private List<Float> mSeveralV = new ArrayList<>();
    private float mVAverage;

    private boolean mFRA800ObjectResponding;
    private boolean mFRA800ObjectReady;

    private boolean mFRA800GeneratorResponding;
    private boolean mFRA800GeneratorReady;

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

    private float mP1;
    private List<Float> mSeveralP1 = new ArrayList<>();
    private float mP1Average;

    private float mS;
    private List<Float> mSeveralS = new ArrayList<>();
    private float mSAverage;

    private float mCos;
    private List<Float> mSeveralCos = new ArrayList<>();
    private float mCosAverage;

    private double mP2;
    private double mP2Average;
    private double mNu;
    private double mNuAverage;
    private double mSk;
    private double mSkAverage;

    private boolean mTRM201Responding;
    private float mTempAmbient;

    private float mTempEngine;
    private List<Float> mSeveralTempEngine = new ArrayList<>();
    private float mTempEngineAverage;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment2);
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
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.Z1) != 0) {
                mZ1 = extras.getInt(MainActivity.OUTPUT_PARAMETER.Z1);
            } else {
                throw new NullPointerException("Не передано Z1");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.Z2) != 0) {
                mZ2 = extras.getInt(MainActivity.OUTPUT_PARAMETER.Z2);
            } else {
                throw new NullPointerException("Не передано Z2");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY) != 0) {
                mSpecifiedFrequency = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY);
                mIntSpecifiedFrequencyK100 = (int) (mSpecifiedFrequency * 100);
            } else {
                throw new NullPointerException("Не передано specifiedFrequency");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.V1) != 0) {
                mV1 = extras.getInt(MainActivity.OUTPUT_PARAMETER.V1);
                mSyncV = getSyncV((int) mSpecifiedFrequency, (int) mV1);
                changeTextOfView(mVSpecifiedCell, formatRealNumber(mV1));
            } else {
                throw new NullPointerException("Не передано V1");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_AMPERAGE) != 0) {
                mSpecifiedAmperage = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_AMPERAGE);
                changeTextOfView(mISpecifiedCell, formatRealNumber(mSpecifiedAmperage));
                mSpecifiedAmperage *= 1.01;
            } else {
                throw new NullPointerException("Не передано specifiedAmperage");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_TORQUE) != 0) {
                mSpecifiedTorque = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_TORQUE);
                changeTextOfView(mMSpecifiedCell, formatRealNumber(mSpecifiedTorque));
            } else {
                throw new NullPointerException("Не передано specifiedTorque");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_TIME_IDLE) != 0) {
                mExperimentTimeIdle = extras.getInt(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_TIME_IDLE);
            } else {
                throw new NullPointerException("Не передано experimentTimeIdle");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_TIME) != 0) {
                mExperimentTime = extras.getInt(MainActivity.OUTPUT_PARAMETER.EXPERIMENT_TIME);
                changeTextOfView(mTSpecifiedCell, formatRealNumber(mExperimentTime));
            } else {
                throw new NullPointerException("Не передано experimentTime");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U) != 0) {
                mSpecifiedU = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U);
                changeTextOfView(mUSpecifiedCell, formatRealNumber(mSpecifiedU));
                mSpecifiedUK10 = mSpecifiedU * 10;
            } else {
                throw new NullPointerException("Не передано specifiedU");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_P2) != 0) {
                mSpecifiedP2 = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_P2);
                mSpecifiedP2 /= 1000f;
                changeTextOfView(mP2SpecifiedCell, formatRealNumber(mSpecifiedP2));
            } else {
                throw new NullPointerException("Не передано specifiedP2");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_EFF) != 0) {
                mSpecifiedEff = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_EFF);
                changeTextOfView(mNuSpecifiedCell, formatRealNumber(mSpecifiedEff));
            } else {
                throw new NullPointerException("Не передано specifiedEff");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_SK) != 0) {
                mSpecifiedSk = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_SK);
                changeTextOfView(mSkSpecifiedCell, formatRealNumber(mSpecifiedSk));
            } else {
                throw new NullPointerException("Не передано specifiedSk");
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

    public boolean isNeededToSave() {
        return mNeededToSave;
    }

    public void setNeededToSave(boolean neededToSave) {
        mNeededToSave = neededToSave;
    }

    private class ExperimentTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            setExperimentStart(true);
            setNeededToSave(true);
            mThreadOn = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isExperimentStart()) {
                changeTextOfView(mStatus, "Испытание началось");
                mDevicesController.initDevicesFrom1To3And10And12Group();
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
                mDevicesController.initDevicesFrom1To3And10And12Group();
            }
            while (isExperimentStart() && !isDevicesResponding()) {
                changeTextOfView(mStatus, "Нет связи с устройствами");
                sleep(100);
            }

            if (isExperimentStart() && mStartState) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom1To3And10And12Group();
                m200to5State = true;
                sleep(500);
                mDevicesController.setObjectParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);// TODO: 13.12.2017 заменить хардкод везде
                mDevicesController.startObject();
            }

            while (isExperimentStart() && !mFRA800ObjectReady && mStartState) {// TODO: 12.12.2017 добавить проверку на StartState везде
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь ОИ выйдет к заданным характеристикам");
            }

            int t = 5;
            while (isExperimentStart() && (--t > 0) && mStartState) {
                sleep(1000);
            }

            if (isExperimentStart() && mStartState) {
                mDevicesController.setObjectUMax(mSpecifiedUK10 + (int) (mSpecifiedUK10 - mUA * 10) + 15);// TODO: 12.12.2017 дорегулировать напряжение везде
            }

            double f2 = mV * mZ1 * mSpecifiedFrequency / mV2 / mZ2;
            int fCurGenerator = (int) (f2 * 100);

            if (isExperimentStart() && mStartState) {
                mDevicesController.setGeneratorParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, fCurGenerator);
                mDevicesController.startGenerator();
            }

            while (isExperimentStart() && !mFRA800GeneratorReady && mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь генератора выйдет к заданным характеристикам");
            }

            int experimentTime = mExperimentTimeIdle;
            while (isExperimentStart() && (experimentTime-- > 0) && mStartState) {
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время обкатки на ХХ. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
            }
            changeTextOfView(mTCell, "");

            if (isExperimentStart() && mStartState) {
                mDevicesController.onLoad();
            }
            sleep(500);
            int waits = 100;
            while (isExperimentStart() && (mM < 0) && (waits-- > 0) && mStartState) {
                sleep(50);
            }

            double limit = 2;
            while (isExperimentStart() && ((mM < mSpecifiedTorque * 0.8) || (mM > mSpecifiedTorque * 1.2)) && mStartState) {
                if (mM < mSpecifiedTorque * 0.8) {
                    mDevicesController.setGeneratorFCur(fCurGenerator -= 10);
                } else if (mM > mSpecifiedTorque * 1.2) {
                    mDevicesController.setGeneratorFCur(fCurGenerator += 10);
                }
                sleep(100);
                pickUpStateRoughly();
                changeTextOfView(mStatus, "Выводим частоту генератора для получения заданного момента * 0.9");
            }
            while (isExperimentStart() && ((mM < mSpecifiedTorque - limit) || (mM > mSpecifiedTorque + limit)) && mStartState) {
                if (mM < mSpecifiedTorque - limit) {
                    mDevicesController.setGeneratorFCur(fCurGenerator -= 5);
                } else if (mM > mSpecifiedTorque + limit) {
                    mDevicesController.setGeneratorFCur(fCurGenerator += 5);
                }
                sleep(200);
                pickUpStateRoughly();
                changeTextOfView(mStatus, "Выводим частоту генератора для получения заданного момента");
            }

            if (isExperimentStart() && mStartState) {
                pickUpState();
            }

            experimentTime = mExperimentTime;
            while (isExperimentStart() && (experimentTime-- > 0) && mStartState) {
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время под номинальной нагрузкой. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
                if (experimentTime < 5) {
                    setNeededToSave(false);
                }
            }
            setNeededToSave(false);

            mDevicesController.offLoad();
            sleep(500);
            mDevicesController.stopObject();
            mDevicesController.stopGenerator();
            mDevicesController.offKMsFrom1To3And10And12Group();
            m200to5State = false;
            m40to5State = false;
            m5to5State = false;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDevicesController.diversifyDevices();
            mExperimentSwitch.setChecked(false);
            mStatus.setText("Испытание закончено");
            mThreadOn = false;
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

    private void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

    private void pickUpState() {
        if (mIAverage < 45) {
            mDevicesController.on40To5();
            m40to5State = true;
            m200to5State = false;
            sleep(3 * 1000);
            if (mIAverage < 6) {
                mDevicesController.on5To5();
                m5to5State = true;
                m40to5State = false;
                sleep(3 * 1000);
            }
        }
    }

    private void pickUpStateRoughly() {
        if (m200to5State) {
            if (mIAverage < 32) {
                mDevicesController.on40To5();
                m40to5State = true;
                m200to5State = false;
                sleep(5 * 1000);
            }
        }
        if (m40to5State) {
            if (mIAverage > 48) {
                mDevicesController.on200To5();
                m200to5State = true;
                m40to5State = false;
                sleep(5 * 1000);
            }
            if (mIAverage < 4) {
                mDevicesController.on5To5();
                m5to5State = true;
                m40to5State = false;
                sleep(5 * 1000);
            }
        }
        if (m5to5State) {
            if (mIAverage > 5.5f) {
                mDevicesController.on40To5();
                m40to5State = true;
                m5to5State = true;
                sleep(5 * 1000);
            }
        }
    }

    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isM40Responding() && isFRA800ObjectResponding() &&
                isFRA800GeneratorResponding() && isPM130Responding() && isVEHATResponding()
                && isTRM201Responding();
    }

    @Override
    public void update(Observable o, Object values) {
        if (!isNeededToSave()) {
            return;
        }
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
                    case BeckhoffModel.DOOR_S_PARAM:
                        break;
                    case BeckhoffModel.I_PROTECTION_OBJECT_PARAM:
                        break;
                    case BeckhoffModel.I_PROTECTION_VIU_PARAM:
                        break;
                    case BeckhoffModel.I_PROTECTION_IN_PARAM:
                        break;
                    case BeckhoffModel.DOOR_Z_PARAM:
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
            case FR_A800_GENERATOR_ID:
                switch (param) {
                    case FRA800Model.RESPONDING_PARAM:
                        setFRA800GeneratorResponding((boolean) value);
                        break;
                    case FRA800Model.READY_PARAM:
                        setFRA800GeneratorReady((boolean) value);
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
                    case PM130Model.P_PARAM:
                        float P1 = (float) value;
                        if (is200to5State()) {
                            P1 *= STATE_200_TO_5_MULTIPLIER;
                        } else if (is40to5State()) {
                            P1 *= STATE_40_TO_5_MULTIPLIER;
                        } else if (is5to5State()) {
                            P1 *= STATE_5_TO_5_MULTIPLIER;
                        }
                        setP1(P1);
                        break;
                    case PM130Model.S_PARAM:
                        float S = (float) value;
                        if (is200to5State()) {
                            S *= STATE_200_TO_5_MULTIPLIER;
                        } else if (is40to5State()) {
                            S *= STATE_40_TO_5_MULTIPLIER;
                        } else if (is5to5State()) {
                            S *= STATE_5_TO_5_MULTIPLIER;
                        }
                        setS(S);
                        break;
                    case PM130Model.COS_PARAM:
                        setCos((float) value);
                        break;
                }
                break;
            case M40_ID:
                switch (param) {
                    case M40Model.RESPONDING_PARAM:
                        setM40Responding((boolean) value);
                        break;
                    case M40Model.TORQUE_PARAM:
                        setM((float) value);
                        break;
                }
                break;
            case VEHA_T_ID:
                switch (param) {
                    case VEHATModel.RESPONDING_PARAM:
                        setVEHATResponding((boolean) value);
                        break;
                    case VEHATModel.ROTATION_FREQUENCY_PARAM:
                        setV((float) value);
                        break;
                }
                break;
            case TRM201_ID:
                switch (param) {
                    case TRM201Model.RESPONDING_PARAM:
                        setTRM201Responding((boolean) value);
                        break;
                    case TRM201Model.T_AMBIENT_PARAM:
                        setTempAmbient((float) value);
                        break;
                    case TRM201Model.T_ENGINE_PARAM:
                        setTempEngine((float) value);
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

    public boolean isM40Responding() {
        return mM40Responding;
    }

    public void setM40Responding(boolean m40Responding) {
        mM40Responding = m40Responding;
    }

    public void setM(float M) {
        mM = M;
        changeTextOfView(mMCell, formatRealNumber(M));
        setMAverage(M);
        recountP2();
    }

    public void setMAverage(float MAverage) {
        float sumM = setNextValueAndReturnSum(mSeveralM, MAverage, NUM_OF_POINTS);
        if (sumM != -1) {
            mMAverage = sumM / NUM_OF_POINTS;
            changeTextOfView(mMAverageCell, formatRealNumber(mMAverage));
        }
        recountP2Average();
    }

    private void recountP2Average() {
        setP2Average(mMAverage * mVAverage * 2 * Math.PI / 60 / 1000);
    }

    public void setP2Average(double P2Average) {
        mP2Average = P2Average;
        changeTextOfView(mP2AverageCell, formatRealNumber(P2Average));
        recountNuAverage();
    }

    private void recountNuAverage() {
        setNuAverage(mP2Average / mP1Average);
    }

    public void setNuAverage(double nuAverage) {
        mNuAverage = nuAverage;
        changeTextOfView(mNuAverageCell, formatRealNumber(nuAverage));
    }

    private float setNextValueAndReturnSum(List<Float> list, float value, float numOfPoints) {
        if (list.size() < numOfPoints) {
            list.add(value);
            return -1f;
        } else {
            float sumV = 0;
            for (Float v : list) {
                sumV += v;
            }
            list.clear();
            return sumV;
        }
    }

    private void recountP2() {
        setP2(mM * mV * 2 * Math.PI / 60 / 1000);
    }

    public void setP2(double P2) {
        mP2 = P2;
        changeTextOfView(mP2Cell, formatRealNumber(P2));
        recountNu();
    }

    private void recountNu() {
        setNu(mP2 / mP1);
    }

    public void setNu(double nu) {
        mNu = nu;
        changeTextOfView(mNuCell, formatRealNumber(nu));
    }

    public boolean isVEHATResponding() {
        return mVEHATResponding;
    }

    public void setVEHATResponding(boolean VEHATResponding) {
        mVEHATResponding = VEHATResponding;
    }

    public void setV(float V) {
        mV = V;
        changeTextOfView(mVCell, formatRealNumber(V));
        setVAverage(V);
        recountP2();
        recountSk();
    }

    public void setVAverage(float VAverage) {
        float sumV = setNextValueAndReturnSum(mSeveralV, VAverage, NUM_OF_POINTS);
        if (sumV != -1) {
            mVAverage = sumV / NUM_OF_POINTS;
            changeTextOfView(mVAverageCell, formatRealNumber(mVAverage));
        }
        recountP2Average();
        recountSkAverage();
    }

    private void recountSkAverage() {
        setSkAverage(100 * (mSyncV - mVAverage) / mSyncV);
    }

    public void setSkAverage(double skAverage) {
        mSkAverage = skAverage;
        changeTextOfView(mSkAverageCell, formatRealNumber(skAverage));
    }

    private void recountSk() {
        setSk(100 * (mSyncV - mV) / mSyncV);
    }

    public void setSk(double sk) {
        mSk = sk;
        changeTextOfView(mSkCell, formatRealNumber(sk));
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

    public boolean isFRA800GeneratorResponding() {
        return mFRA800GeneratorResponding;
    }

    public void setFRA800GeneratorResponding(boolean FRA800GeneratorResponding) {
        mFRA800GeneratorResponding = FRA800GeneratorResponding;
    }

    public void setFRA800GeneratorReady(boolean FRA800GeneratorReady) {
        mFRA800GeneratorReady = FRA800GeneratorReady;
    }

    public boolean isPM130Responding() {
        return mPM130Responding;
    }

    public void setPM130Responding(boolean PM130Responding) {
        mPM130Responding = PM130Responding;
    }

    public void setUA(float UA) {
        mUA = UA;
        changeTextOfView(mUACell, formatRealNumber(UA));
        setUAAverage(UA);
    }

    public void setUAAverage(float UAAverage) {
        float sumUA = setNextValueAndReturnSum(mSeveralUA, UAAverage, NUM_OF_POINTS);
        if (sumUA != -1) {
            mUAAverage = sumUA / NUM_OF_POINTS;
        }
    }

    public void setUB(float UB) {
        mUB = UB;
        changeTextOfView(mUBCell, formatRealNumber(UB));
        setUBAverage(UB);
    }

    public void setUBAverage(float UBAverage) {
        float sumUB = setNextValueAndReturnSum(mSeveralUB, UBAverage, NUM_OF_POINTS);
        if (sumUB != -1) {
            mUBAverage = sumUB / NUM_OF_POINTS;
        }
    }

    public void setUC(float UC) {
        mUC = UC;
        changeTextOfView(mUCCell, formatRealNumber(UC));
        setUCAverage(UC);
    }

    public void setUCAverage(float UCAverage) {
        float sumUC = setNextValueAndReturnSum(mSeveralUC, UCAverage, NUM_OF_POINTS);
        if (sumUC != -1) {
            mUCAverage = sumUC / NUM_OF_POINTS;
            setUAverage();
        }
    }

    public void setUAverage() {
        mUAverage = (mUAAverage + mUBAverage + mUCAverage) / 3f;
        changeTextOfView(mUAverageCell, formatRealNumber(mUAverage));
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
        changeTextOfView(mIACell, formatRealNumber(IA));
        setIAAverage(IA);
    }

    public void setIAAverage(float IAAverage) {
        float sumIA = setNextValueAndReturnSum(mSeveralIA, IAAverage, NUM_OF_POINTS);
        if (sumIA != -1) {
            mIAAverage = sumIA / NUM_OF_POINTS;
        }
    }

    public void setIB(float IB) {
        mIB = IB;
        changeTextOfView(mIBCell, formatRealNumber(IB));
        setIBAverage(IB);
    }

    public void setIBAverage(float IBAverage) {
        float sumIB = setNextValueAndReturnSum(mSeveralIB, IBAverage, NUM_OF_POINTS);
        if (sumIB != -1) {
            mIBAverage = sumIB / NUM_OF_POINTS;
        }
    }

    public void setIC(float IC) {
        mIC = IC;
        changeTextOfView(mICCell, formatRealNumber(IC));
        setICAverage(IC);
    }

    public void setICAverage(float ICAverage) {
        float sumIC = setNextValueAndReturnSum(mSeveralIC, ICAverage, NUM_OF_POINTS);
        if (sumIC != -1) {
            mICAverage = sumIC / NUM_OF_POINTS;
            setIAverage();
        }
    }

    public void setIAverage() {
        mIAverage = (mIAAverage + mIBAverage + mICAverage) / 3f;
        changeTextOfView(mIAverageCell, formatRealNumber(mIAverage));
    }

    public void setP1(float P1) {
        mP1 = P1;
        changeTextOfView(mP1Cell, formatRealNumber(P1));
        setP1Average(P1);
    }

    public void setP1Average(float P1Average) {
        float sumP1 = setNextValueAndReturnSum(mSeveralP1, P1Average, NUM_OF_POINTS);
        if (sumP1 != -1) {
            mP1Average = sumP1 / NUM_OF_POINTS;
            changeTextOfView(mP1AverageCell, formatRealNumber(mP1Average));
        }
    }

    public void setS(float S) {
        mS = S;
        changeTextOfView(mSCell, formatRealNumber(S));
        setSAverage(S);
    }

    public void setSAverage(float SAverage) {
        float sumS = setNextValueAndReturnSum(mSeveralS, SAverage, NUM_OF_POINTS);
        if (sumS != -1) {
            mSAverage = sumS / NUM_OF_POINTS;
            changeTextOfView(mSAverageCell, formatRealNumber(mSAverage));
        }
    }

    public void setCos(float cos) {
        mCos = cos;
        changeTextOfView(mCosCell, formatRealNumber(cos));
        setCosAverage(cos);
    }

    public void setCosAverage(float CosAverage) {
        float sumCos = setNextValueAndReturnSum(mSeveralCos, CosAverage, NUM_OF_POINTS);
        if (sumCos != -1) {
            mCosAverage = sumCos / NUM_OF_POINTS;
            changeTextOfView(mCosAverageCell, formatRealNumber(mCosAverage));
        }
    }

    public boolean isTRM201Responding() {
        return mTRM201Responding;
    }

    public void setTRM201Responding(boolean TRM201Responding) {
        mTRM201Responding = TRM201Responding;
    }

    public void setTempAmbient(float tempAmbient) {
        mTempAmbient = tempAmbient;
        changeTextOfView(mTempAmbientCell, formatRealNumber(tempAmbient));
    }

    public void setTempEngine(float tempEngine) {
        mTempEngine = tempEngine;
        changeTextOfView(mTempEngineCell, formatRealNumber(tempEngine));
        setTempEngineAverage(tempEngine);
    }

    public void setTempEngineAverage(float tempEngineAverage) {
        float sumTempEngine = setNextValueAndReturnSum(mSeveralTempEngine, tempEngineAverage, NUM_OF_POINTS);
        if (sumTempEngine != -1) {
            mTempEngineAverage = sumTempEngine / NUM_OF_POINTS;
            changeTextOfView(mTempEngineAverageCell, formatRealNumber(mTempEngineAverage));
        }
    }

    private void clearCells() {
        changeTextOfView(mIACell, "");
        changeTextOfView(mUACell, "");
        changeTextOfView(mIBCell, "");
        changeTextOfView(mUBCell, "");
        changeTextOfView(mSCell, "");
        changeTextOfView(mP1Cell, "");
        changeTextOfView(mCosCell, "");
        changeTextOfView(mMCell, "");
        changeTextOfView(mVCell, "");
        changeTextOfView(mP2Cell, "");
        changeTextOfView(mNuCell, "");
        changeTextOfView(mTempAmbientCell, "");
        changeTextOfView(mTempEngineCell, "");
        changeTextOfView(mSkCell, "");
        changeTextOfView(mTCell, "");
        changeTextOfView(mICCell, "");
        changeTextOfView(mUCCell, "");
        changeTextOfView(mIAverageCell, "");
        changeTextOfView(mUAverageCell, "");
        changeTextOfView(mSAverageCell, "");
        changeTextOfView(mP1AverageCell, "");
        changeTextOfView(mCosAverageCell, "");
        changeTextOfView(mMAverageCell, "");
        changeTextOfView(mVAverageCell, "");
        changeTextOfView(mP2AverageCell, "");
        changeTextOfView(mNuAverageCell, "");
        changeTextOfView(mSkAverageCell, "");
        changeTextOfView(mTempEngineAverageCell, "");
    }

    @Override
    public void onBackPressed() {
        setExperimentStart(false);
        fillExperimentTable();
        finish();
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE2IA(mIACell.getText().toString());
        experiments.setE2UA(mUACell.getText().toString());
        experiments.setE2IB(mIBCell.getText().toString());
        experiments.setE2UB(mUBCell.getText().toString());
        experiments.setE2S(mSCell.getText().toString());
        experiments.setE2P1(mP1Cell.getText().toString());
        experiments.setE2Cos(mCosCell.getText().toString());
        experiments.setE2M(mMCell.getText().toString());
        experiments.setE2V(mVCell.getText().toString());
        experiments.setE2P2(mP2Cell.getText().toString());
        experiments.setE2Nu(mNuCell.getText().toString());
        experiments.setE2TempAmbient(mTempAmbientCell.getText().toString());
        experiments.setE2TempEngine(mTempEngineCell.getText().toString());
        experiments.setE2Sk(mSkCell.getText().toString());
        experiments.setE2T(mTCell.getText().toString());
        experiments.setE2IC(mICCell.getText().toString());
        experiments.setE2UC(mUCCell.getText().toString());
        experiments.setE2IAverage(mIAverageCell.getText().toString());
        experiments.setE2UAverage(mUAverageCell.getText().toString());
        experiments.setE2SAverage(mSAverageCell.getText().toString());
        experiments.setE2P1Average(mP1AverageCell.getText().toString());
        experiments.setE2CosAverage(mCosAverageCell.getText().toString());
        experiments.setE2MAverage(mMAverageCell.getText().toString());
        experiments.setE2VAverage(mVAverageCell.getText().toString());
        experiments.setE2P2Average(mP2AverageCell.getText().toString());
        experiments.setE2NuAverage(mNuAverageCell.getText().toString());
        experiments.setE2TempEngineAverage(mTempEngineAverageCell.getText().toString());
        experiments.setE2SkAverage(mSkAverageCell.getText().toString());
        experiments.setE2ISpecified(mISpecifiedCell.getText().toString());
        experiments.setE2USpecified(mUSpecifiedCell.getText().toString());
        experiments.setE2SSpecified(mSSpecifiedCell.getText().toString());
        experiments.setE2P1Specified(mP1SpecifiedCell.getText().toString());
        experiments.setE2CosSpecified(mCosSpecifiedCell.getText().toString());
        experiments.setE2MSpecified(mMSpecifiedCell.getText().toString());
        experiments.setE2VSpecified(mVSpecifiedCell.getText().toString());
        experiments.setE2P2Specified(mP2SpecifiedCell.getText().toString());
        experiments.setE2NuSpecified(mNuSpecifiedCell.getText().toString());
        experiments.setE2TempEngineSpecified(mTempEngineSpecifiedCell.getText().toString());
        experiments.setE2SkSpecified(mSkSpecifiedCell.getText().toString());
        experiments.setE2TSpecified(mTSpecifiedCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
