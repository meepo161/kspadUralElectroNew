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
import ru.avem.kspad.communication.devices.m40.M40Model;
import ru.avem.kspad.communication.devices.pm130.PM130Model;
import ru.avem.kspad.communication.devices.trm201.TRM201Model;
import ru.avem.kspad.communication.devices.veha_t.VEHATModel;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;
import ru.avem.kspad.utils.Logger;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.DeviceController.M40_ID;
import static ru.avem.kspad.communication.devices.DeviceController.PM130_ID;
import static ru.avem.kspad.communication.devices.DeviceController.TRM201_ID;
import static ru.avem.kspad.communication.devices.DeviceController.VEHA_T_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment13Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Проверка работоспособности при изменении напряжения и частоты питающей сети";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int STATE_40_TO_5_MULTIPLIER = 40 / 5;
    private static final int STATE_5_TO_5_MULTIPLIER = 5 / 5;

    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.u_1_1_a)
    TextView mU11ACell;
    @BindView(R.id.i_1_1_a)
    TextView mI11ACell;
    @BindView(R.id.u_1_1_b)
    TextView mU11BCell;
    @BindView(R.id.i_1_1_b)
    TextView mI11BCell;
    @BindView(R.id.s_1_1)
    TextView mS11Cell;
    @BindView(R.id.p_1_1)
    TextView mP11Cell;
    @BindView(R.id.v_1_1)
    TextView mV11Cell;
    @BindView(R.id.m_1_1)
    TextView mM11Cell;
    @BindView(R.id.f_1_1)
    TextView mF11Cell;
    @BindView(R.id.temp_1_1)
    TextView mTemp11Cell;
    @BindView(R.id.t_1_1)
    TextView mT11Cell;
    @BindView(R.id.u_1_1_c)
    TextView mU11CCell;
    @BindView(R.id.i_1_1_c)
    TextView mI11CCell;
    @BindView(R.id.u_1_1_average)
    TextView mU11AverageCell;
    @BindView(R.id.i_1_1_average)
    TextView mI11AverageCell;

    @BindView(R.id.u_0_8_a)
    TextView mU08ACell;
    @BindView(R.id.i_0_8_a)
    TextView mI08ACell;
    @BindView(R.id.u_0_8_b)
    TextView mU08BCell;
    @BindView(R.id.i_0_8_b)
    TextView mI08BCell;
    @BindView(R.id.s_0_8)
    TextView mS08Cell;
    @BindView(R.id.p_0_8)
    TextView mP08Cell;
    @BindView(R.id.v_0_8)
    TextView mV08Cell;
    @BindView(R.id.m_0_8)
    TextView mM08Cell;
    @BindView(R.id.f_0_8)
    TextView mF08Cell;
    @BindView(R.id.temp_0_8)
    TextView mTemp08Cell;
    @BindView(R.id.t_0_8)
    TextView mT08Cell;
    @BindView(R.id.u_0_8_c)
    TextView mU08CCell;
    @BindView(R.id.i_0_8_c)
    TextView mI08CCell;
    @BindView(R.id.u_0_8_average)
    TextView mU08AverageCell;
    @BindView(R.id.i_0_8_average)
    TextView mI08AverageCell;

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

    private int mCurrentStage = 8;


    private float mSpecifiedU11;
    private float mSpecifiedU08;

    private int mSpecifiedT = 15;
    private float mSpecifiedFrequency;
    private int mIntSpecifiedFrequencyK100;
    private boolean mPlatformOneSelected;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mFRA800ObjectResponding;
    private boolean mFRA800ObjectReady;

    private boolean mPM130Responding;
    private float mPM130V1;
    private float mPM130V2;
    private float mPM130V3;
    private boolean m200to5State;
    private boolean m40to5State;
    private boolean m5to5State;
    private float mPM130I1;
    private float mPM130I2;
    private float mPM130I3;
    private float mPM130S;
    private float mPM130P;
    private float mPM130F;

    private boolean mM40Responding;
    private float mM;

    private boolean mVEHATResponding;
    private float mV;

    private boolean mTRM201Responding;
    private float mTemp;

    private float mU11A;
    private float mI11A;
    private float mU11B;
    private float mI11B;
    private float mS11;
    private float mP11;
    private float mV11;
    private float mM11;
    private float mF11;
    private float mTemp11;
    private float mT11;
    private float mU11C;
    private float mI11C;
    private float mU11Average;
    private float mI11Average;

    private float mU08A;
    private float mI08A;
    private float mU08B;
    private float mI08B;
    private float mS08;
    private float mP08;
    private float mV08;
    private float mM08;
    private float mF08;
    private float mTemp08;
    private float mT08;
    private float mU08C;
    private float mI08C;
    private float mU08Average;
    private float mI08Average;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment13);
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
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U) != 0) {
                float mSpecifiedU = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U);
                mSpecifiedU11 = mSpecifiedU * 1.1f;
                mSpecifiedU08 = mSpecifiedU * 0.8f;
            } else {
                throw new NullPointerException("Не передано specifiedU");
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

    private class ExperimentTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            setExperimentStart(true);
            setFRA800ObjectReady(false);
            mCurrentStage = 8;
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setM40Responding(true);
            setVEHATResponding(true);
            setFRA800ObjectResponding(true);
            setPM130Responding(true);
            setTRM201Responding(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            changeTextOfView(mStatus, "Испытание началось");
            mDevicesController.initDevicesFrom13Group();
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(1000);
            }
            while (isExperimentStart() && !isStartState()) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }

            if (isExperimentStart() && mStartState) {
                mDevicesController.initDevicesFrom13Group();
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
                mDevicesController.setObjectParams(100, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.startObject();
                sleep(2000);
            }

            while (isExperimentStart() && !isFRA800ObjectReady() && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь выйдет к заданным характеристикам");
            }


            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.setObjectFCur((int) (0.94 * 5000));
            }
            int lastLevel = regulation(100, 3 * 10, 3, mSpecifiedU08, 0.05, 1, 400, 1050);

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                pickUpState();
            }

            int experimentTime = mSpecifiedT;
            while (isExperimentStart() && (experimentTime > 0) && mStartState && isDevicesResponding()) {
                experimentTime--;
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время. Осталось: " + experimentTime);
                setT("" + experimentTime);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mCurrentStage = 11;
                stateToBack();
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.setObjectFCur((int) (1.03 * 5000));
            }

            lastLevel = regulation(lastLevel, 3 * 10, 3, mSpecifiedU11, 0.05, 1, 400, 1050);

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                pickUpState();
            }

            experimentTime = mSpecifiedT;
            while (isExperimentStart() && (experimentTime > 0) && mStartState && isDevicesResponding()) {
                experimentTime--;
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время. Осталось: " + experimentTime);
                setT("" + experimentTime);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mCurrentStage = 12;
                stateToBack();
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                for (int i = lastLevel; i > 0; i -= 40) {
                    if (i > 0) {
                        mDevicesController.setObjectUMax(i);
                    }
                }
                mDevicesController.setObjectUMax(0);
            }

            mDevicesController.stopObject();
            mDevicesController.offKMsFrom4And7And13Group();
            m200to5State = false;
            m40to5State = false;
            m5to5State = false;

            return null;
        }

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s%s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isM40Responding() ? "" : "Датчик момента, ",
                    isFRA800ObjectResponding() ? "" : "ЧП ОИ, ",
                    isPM130Responding() ? "" : "PM130, ",
                    isVEHATResponding() ? "" : "ВЕХА-Т, ",
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

    private void setT(String time) {
        TextView textView = null;
        switch (mCurrentStage) {
            case 11:
                textView = mT11Cell;
                break;
            case 8:
                textView = mT08Cell;
                break;
        }
        changeTextOfView(textView, time);
    }

    private void pickUpState() {
        if (mPM130I1 < 45) {
            mDevicesController.on40To5();
            m40to5State = true;
            m200to5State = false;
            m5to5State = false;
            sleep(3 * 1000);
            if (mPM130I1 < 6) {
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

    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isFRA800ObjectResponding() && isPM130Responding() && isTRM201Responding() && isM40Responding() && isVEHATResponding();
    }

    private int regulation(int start, int coarseStep, int fineStep, float end, double coarseLimit, double fineLimit, int coarseSleep, int fineSleep) {
        double coarseMinLimit = 1 - coarseLimit;
        double coarseMaxLimit = 1 + coarseLimit;
        while (isExperimentStart() && ((mPM130V1 < end * coarseMinLimit) || (mPM130V1 > end * coarseMaxLimit)) && mStartState && isDevicesResponding()) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mPM130V1);
            if (mPM130V1 < end * coarseMinLimit) {
                mDevicesController.setObjectUMax(start += coarseStep);
            } else if (mPM130V1 > end * coarseMaxLimit) {
                mDevicesController.setObjectUMax(start -= coarseStep);
            }
            sleep(coarseSleep);
            changeTextOfView(mStatus, "Выводим напряжение для получения заданного значения грубо");
        }
        while (isExperimentStart() && ((mPM130V1 < end - fineLimit) || (mPM130V1 > end + fineLimit)) && mStartState && isDevicesResponding()) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mPM130V1);
            if (mPM130V1 < end - fineLimit) {
                mDevicesController.setObjectUMax(start += fineStep);
            } else if (mPM130V1 > end + fineLimit) {
                mDevicesController.setObjectUMax(start -= fineStep);
            }
            sleep(fineSleep);
            changeTextOfView(mStatus, "Выводим напряжение для получения заданного значения точно");
        }
        return start;
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
                        setPM130V1((float) value);
                        break;
                    case PM130Model.V2_PARAM:
                        setPM130V2((float) value);
                        break;
                    case PM130Model.V3_PARAM:
                        setPM130V3((float) value);
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
                        setPM130I1(IA);
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
                        setPM130I2(IB);
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
                        setPM130I3(IC);
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
                        setPM130S(S);
                        break;
                    case PM130Model.P_PARAM:
                        float P = (float) value;
                        if (is200to5State()) {
                            P *= STATE_200_TO_5_MULTIPLIER;
                        } else if (is40to5State()) {
                            P *= STATE_40_TO_5_MULTIPLIER;
                        } else if (is5to5State()) {
                            P *= STATE_5_TO_5_MULTIPLIER;
                        }
                        setPM130P(P);
                        break;
                    case PM130Model.F_PARAM:
                        setPM130F((float) value);
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

    public boolean isStartState() {
        return mStartState;
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
        switch (mCurrentStage) {
            case 11:
                setM11(M);
                break;
            case 8:
                setM08(M);
                break;
        }
    }

    public boolean isVEHATResponding() {
        return mVEHATResponding;
    }

    public void setVEHATResponding(boolean VEHATResponding) {
        mVEHATResponding = VEHATResponding;
    }

    public void setV(float V) {
        mV = V;
        switch (mCurrentStage) {
            case 11:
                setV11(V);
                break;
            case 8:
                setV08(V);
                break;
        }
    }

    public boolean isFRA800ObjectResponding() {
        return mFRA800ObjectResponding;
    }

    public void setFRA800ObjectResponding(boolean FRA800ObjectResponding) {
        mFRA800ObjectResponding = FRA800ObjectResponding;
    }

    public boolean isFRA800ObjectReady() {
        return mFRA800ObjectReady;
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

    public void setPM130V1(float PM130V1) {
        mPM130V1 = PM130V1;
        switch (mCurrentStage) {
            case 11:
                setU11A(PM130V1);
                break;
            case 8:
                setU08A(PM130V1);
                break;
        }
    }

    public void setPM130V2(float PM130V2) {
        mPM130V2 = PM130V2;
        switch (mCurrentStage) {
            case 11:
                setU11B(PM130V2);
                break;
            case 8:
                setU08B(PM130V2);
                break;
        }
    }

    public void setPM130V3(float PM130V3) {
        mPM130V3 = PM130V3;
        switch (mCurrentStage) {
            case 11:
                setU11C(PM130V3);
                break;
            case 8:
                setU08C(PM130V3);
                break;
        }
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

    public void setPM130I1(float PM130I1) {
        mPM130I1 = PM130I1;
        switch (mCurrentStage) {
            case 11:
                setI11A(PM130I1);
                break;
            case 8:
                setI08A(PM130I1);
                break;
        }
    }

    public void setPM130I2(float PM130I2) {
        mPM130I2 = PM130I2;
        switch (mCurrentStage) {
            case 11:
                setI11B(PM130I2);
                break;
            case 8:
                setI08B(PM130I2);
                break;
        }
    }

    public void setPM130I3(float PM130I3) {
        mPM130I3 = PM130I3;
        switch (mCurrentStage) {
            case 11:
                setI11C(PM130I3);
                break;
            case 8:
                setI08C(PM130I3);
                break;
        }
    }

    public void setPM130S(float PM130S) {
        mPM130S = PM130S;
        switch (mCurrentStage) {
            case 11:
                setS11(PM130S);
                break;
            case 8:
                setS08(PM130S);
                break;
        }
    }

    public void setPM130P(float PM130P) {
        mPM130P = PM130P;
        switch (mCurrentStage) {
            case 11:
                setP11(PM130P);
                break;
            case 8:
                setP08(PM130P);
                break;
        }
    }

    public void setPM130F(float PM130F) {
        mPM130F = PM130F;
        switch (mCurrentStage) {
            case 11:
                setF11(PM130F);
                break;
            case 8:
                setF08(PM130F);
                break;
        }
    }

    public boolean isTRM201Responding() {
        return mTRM201Responding;
    }

    public void setTRM201Responding(boolean TRM201Responding) {
        mTRM201Responding = TRM201Responding;
    }

    public void setTemp(float temp) {
        mTemp = temp;
        switch (mCurrentStage) {
            case 11:
                setTemp11(temp);
                break;
            case 8:
                setTemp08(temp);
                break;
        }
    }

    public void setU11A(float u11A) {
        mU11A = u11A;
        changeTextOfView(mU11ACell, formatRealNumber(u11A));
    }

    public void setI11A(float i11A) {
        mI11A = i11A;
        changeTextOfView(mI11ACell, formatRealNumber(i11A));
    }

    public void setU11B(float u11B) {
        mU11B = u11B;
        changeTextOfView(mU11BCell, formatRealNumber(u11B));
    }

    public void setI11B(float i11B) {
        mI11B = i11B;
        changeTextOfView(mI11BCell, formatRealNumber(i11B));
    }

    public void setS11(float s11) {
        mS11 = s11;
        changeTextOfView(mS11Cell, formatRealNumber(s11));
    }

    public void setP11(float p11) {
        mP11 = p11;
        changeTextOfView(mP11Cell, formatRealNumber(p11));
    }

    public void setV11(float v11) {
        mV11 = v11;
        changeTextOfView(mV11Cell, formatRealNumber(v11));
    }

    public void setM11(float m11) {
        mM11 = m11;
        changeTextOfView(mM11Cell, formatRealNumber(m11));
    }

    public void setF11(float f11) {
        mF11 = f11;
        changeTextOfView(mF11Cell, formatRealNumber(f11));
    }

    public void setTemp11(float temp11) {
        mTemp11 = temp11;
        changeTextOfView(mTemp11Cell, formatRealNumber(temp11));
    }

    public void setT11(float t11) {
        mT11 = t11;
    }

    public void setU11C(float u11C) {
        mU11C = u11C;
        changeTextOfView(mU11CCell, formatRealNumber(u11C));
        setU11Average((mU11A + mU11B + u11C) / 3f);
    }

    public void setU11Average(float U11Average) {
        mU11Average = U11Average;
        changeTextOfView(mU11AverageCell, formatRealNumber(U11Average));
    }

    public void setI11C(float i11C) {
        mI11C = i11C;
        changeTextOfView(mI11CCell, formatRealNumber(i11C));
        setI11Average((mI11A + mI11B + i11C) / 3f);
    }

    public void setI11Average(float I11Average) {
        mI11Average = I11Average;
        changeTextOfView(mI11AverageCell, formatRealNumber(I11Average));
    }

    public void setU08A(float u08A) {
        mU08A = u08A;
        changeTextOfView(mU08ACell, formatRealNumber(u08A));
    }

    public void setI08A(float i08A) {
        mI08A = i08A;
        changeTextOfView(mI08ACell, formatRealNumber(i08A));
    }

    public void setU08B(float u08B) {
        mU08B = u08B;
        changeTextOfView(mU08BCell, formatRealNumber(u08B));
    }

    public void setI08B(float i08B) {
        mI08B = i08B;
        changeTextOfView(mI08BCell, formatRealNumber(i08B));
    }

    public void setS08(float s08) {
        mS08 = s08;
        changeTextOfView(mS08Cell, formatRealNumber(s08));
    }

    public void setP08(float p08) {
        mP08 = p08;
        changeTextOfView(mP08Cell, formatRealNumber(p08));
    }

    public void setV08(float v08) {
        mV08 = v08;
        changeTextOfView(mV08Cell, formatRealNumber(v08));
    }

    public void setM08(float m08) {
        mM08 = m08;
        changeTextOfView(mM08Cell, formatRealNumber(m08));
    }

    public void setF08(float f08) {
        mF08 = f08;
        changeTextOfView(mF08Cell, formatRealNumber(f08));
    }

    public void setTemp08(float temp08) {
        mTemp08 = temp08;
        changeTextOfView(mTemp08Cell, formatRealNumber(temp08));
    }

    public void setT08(float t08) {
        mT08 = t08;
    }

    public void setU08C(float u08C) {
        mU08C = u08C;
        changeTextOfView(mU08CCell, formatRealNumber(u08C));
        setU08Average((mU08A + mU08B + u08C) / 3f);
    }

    public void setU08Average(float U08Average) {
        mU08Average = U08Average;
        changeTextOfView(mU08AverageCell, formatRealNumber(U08Average));
    }

    public void setI08C(float i08C) {
        mI08C = i08C;
        changeTextOfView(mI08CCell, formatRealNumber(i08C));
        setI08Average((mI08A + mI08B + i08C) / 3f);
    }

    public void setI08Average(float I08Average) {
        mI08Average = I08Average;
        changeTextOfView(mI08AverageCell, formatRealNumber(I08Average));
    }

    private void clearCells() {
        changeTextOfView(mU11ACell, "");
        changeTextOfView(mI11ACell, "");
        changeTextOfView(mU11BCell, "");
        changeTextOfView(mI11BCell, "");
        changeTextOfView(mS11Cell, "");
        changeTextOfView(mP11Cell, "");
        changeTextOfView(mV11Cell, "");
        changeTextOfView(mM11Cell, "");
        changeTextOfView(mF11Cell, "");
        changeTextOfView(mTemp11Cell, "");
        changeTextOfView(mT11Cell, "");
        changeTextOfView(mU11CCell, "");
        changeTextOfView(mI11CCell, "");
        changeTextOfView(mU11AverageCell, "");
        changeTextOfView(mI11AverageCell, "");

        changeTextOfView(mU08ACell, "");
        changeTextOfView(mI08ACell, "");
        changeTextOfView(mU08BCell, "");
        changeTextOfView(mI08BCell, "");
        changeTextOfView(mS08Cell, "");
        changeTextOfView(mP08Cell, "");
        changeTextOfView(mV08Cell, "");
        changeTextOfView(mM08Cell, "");
        changeTextOfView(mF08Cell, "");
        changeTextOfView(mTemp08Cell, "");
        changeTextOfView(mT08Cell, "");
        changeTextOfView(mU08CCell, "");
        changeTextOfView(mI08CCell, "");
        changeTextOfView(mU08AverageCell, "");
        changeTextOfView(mI08AverageCell, "");
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
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE13U08A(mU08ACell.getText().toString());
        experiments.setE13I08A(mI08ACell.getText().toString());
        experiments.setE13U08B(mU08BCell.getText().toString());
        experiments.setE13I08B(mI08BCell.getText().toString());
        experiments.setE13S08(mS08Cell.getText().toString());
        experiments.setE13P08(mP08Cell.getText().toString());
        experiments.setE13V08(mV08Cell.getText().toString());
        experiments.setE13M08(mM08Cell.getText().toString());
        experiments.setE13F08(mF08Cell.getText().toString());
        experiments.setE13Temp08(mTemp08Cell.getText().toString());
        experiments.setE13T08(mT08Cell.getText().toString());
        experiments.setE13U08C(mU08CCell.getText().toString());
        experiments.setE13I08C(mI08CCell.getText().toString());
        experiments.setE13U08Average(mU08AverageCell.getText().toString());
        experiments.setE13I08Average(mI08AverageCell.getText().toString());
        experiments.setE13U11A(mU11ACell.getText().toString());
        experiments.setE13I11A(mI11ACell.getText().toString());
        experiments.setE13U11B(mU11BCell.getText().toString());
        experiments.setE13I11B(mI11BCell.getText().toString());
        experiments.setE13S11(mS11Cell.getText().toString());
        experiments.setE13P11(mP11Cell.getText().toString());
        experiments.setE13V11(mV11Cell.getText().toString());
        experiments.setE13M11(mM11Cell.getText().toString());
        experiments.setE13F11(mF11Cell.getText().toString());
        experiments.setE13Temp11(mTemp11Cell.getText().toString());
        experiments.setE13T11(mT11Cell.getText().toString());
        experiments.setE13U11C(mU11CCell.getText().toString());
        experiments.setE13I11C(mI11CCell.getText().toString());
        experiments.setE13U11Average(mU11AverageCell.getText().toString());
        experiments.setE13I11Average(mI11AverageCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
