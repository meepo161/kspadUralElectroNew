package ru.avem.kspad.view;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ScrollView;
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
import ru.avem.kspad.communication.devices.trm201.TRM201Model;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.DeviceController.PM130_ID;
import static ru.avem.kspad.communication.devices.DeviceController.TRM201_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment9Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Определение токов и потерь короткого замыкания";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int STATE_40_TO_5_MULTIPLIER = 40 / 5;
    private static final int STATE_5_TO_5_MULTIPLIER = 5 / 5;

    @BindView(R.id.main_layout)
    ScrollView mMainLayout;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.u_1_0_a)
    TextView mU10ACell;
    @BindView(R.id.i_1_0_a)
    TextView mI10ACell;
    @BindView(R.id.u_1_0_b)
    TextView mU10BCell;
    @BindView(R.id.i_1_0_b)
    TextView mI10BCell;
    @BindView(R.id.p_1_0)
    TextView mP10Cell;
    @BindView(R.id.cos_1_0)
    TextView mCos10Cell;
    @BindView(R.id.temp_ambient_1_0)
    TextView mTempAmbient10Cell;
    @BindView(R.id.temp_engine_1_0)
    TextView mTempEngine10Cell;
    @BindView(R.id.t_1_0)
    TextView mT10Cell;
    @BindView(R.id.u_1_0_c)
    TextView mU10CCell;
    @BindView(R.id.i_1_0_c)
    TextView mI10CCell;
    @BindView(R.id.u_1_0_average)
    TextView mU10AverageCell;
    @BindView(R.id.i_1_0_average)
    TextView mI10AverageCell;
    @BindView(R.id.u_0_9_a)
    TextView mU09ACell;
    @BindView(R.id.i_0_9_a)
    TextView mI09ACell;
    @BindView(R.id.u_0_9_b)
    TextView mU09BCell;
    @BindView(R.id.i_0_9_b)
    TextView mI09BCell;
    @BindView(R.id.p_0_9)
    TextView mP09Cell;
    @BindView(R.id.cos_0_9)
    TextView mCos09Cell;
    @BindView(R.id.temp_ambient_0_9)
    TextView mTempAmbient09Cell;
    @BindView(R.id.temp_engine_0_9)
    TextView mTempEngine09Cell;
    @BindView(R.id.t_0_9)
    TextView mT09Cell;
    @BindView(R.id.u_0_9_c)
    TextView mU09CCell;
    @BindView(R.id.i_0_9_c)
    TextView mI09CCell;
    @BindView(R.id.u_0_9_average)
    TextView mU09AverageCell;
    @BindView(R.id.i_0_9_average)
    TextView mI09AverageCell;
    @BindView(R.id.u_0_8_a)
    TextView mU08ACell;
    @BindView(R.id.i_0_8_a)
    TextView mI08ACell;
    @BindView(R.id.u_0_8_b)
    TextView mU08BCell;
    @BindView(R.id.i_0_8_b)
    TextView mI08BCell;
    @BindView(R.id.p_0_8)
    TextView mP08Cell;
    @BindView(R.id.cos_0_8)
    TextView mCos08Cell;
    @BindView(R.id.temp_ambient_0_8)
    TextView mTempAmbient08Cell;
    @BindView(R.id.temp_engine_0_8)
    TextView mTempEngine08Cell;
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
    @BindView(R.id.u_0_7_a)
    TextView mU07ACell;
    @BindView(R.id.i_0_7_a)
    TextView mI07ACell;
    @BindView(R.id.u_0_7_b)
    TextView mU07BCell;
    @BindView(R.id.i_0_7_b)
    TextView mI07BCell;
    @BindView(R.id.p_0_7)
    TextView mP07Cell;
    @BindView(R.id.cos_0_7)
    TextView mCos07Cell;
    @BindView(R.id.temp_ambient_0_7)
    TextView mTempAmbient07Cell;
    @BindView(R.id.temp_engine_0_7)
    TextView mTempEngine07Cell;
    @BindView(R.id.t_0_7)
    TextView mT07Cell;
    @BindView(R.id.u_0_7_c)
    TextView mU07CCell;
    @BindView(R.id.i_0_7_c)
    TextView mI07CCell;
    @BindView(R.id.u_0_7_average)
    TextView mU07AverageCell;
    @BindView(R.id.i_0_7_average)
    TextView mI07AverageCell;
    @BindView(R.id.u_0_6_a)
    TextView mU06ACell;
    @BindView(R.id.i_0_6_a)
    TextView mI06ACell;
    @BindView(R.id.u_0_6_b)
    TextView mU06BCell;
    @BindView(R.id.i_0_6_b)
    TextView mI06BCell;
    @BindView(R.id.p_0_6)
    TextView mP06Cell;
    @BindView(R.id.cos_0_6)
    TextView mCos06Cell;
    @BindView(R.id.temp_ambient_0_6)
    TextView mTempAmbient06Cell;
    @BindView(R.id.temp_engine_0_6)
    TextView mTempEngine06Cell;
    @BindView(R.id.t_0_6)
    TextView mT06Cell;
    @BindView(R.id.u_0_6_c)
    TextView mU06CCell;
    @BindView(R.id.i_0_6_c)
    TextView mI06CCell;
    @BindView(R.id.u_0_6_average)
    TextView mU06AverageCell;
    @BindView(R.id.i_0_6_average)
    TextView mI06AverageCell;

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

    private int mCurrentStage;


    private int mNumOfStages;
    private float mSpecifiedU10;
    private float mSpecifiedU09;
    private float mSpecifiedU08;
    private float mSpecifiedU07;
    private float mSpecifiedU06;

    private int mSpecifiedT;
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
    private float mPM130P;
    private float mPM130Cos;

    private boolean mTRM201Responding;
    private float mTempAmbient;
    private float mTempEngine;

    private float mU10A;
    private float mI10A;
    private float mU10B;
    private float mI10B;
    private float mP10 = -1f;
    private float mCos10;
    private float mTempAmbient10;
    private float mTempEngine10;
    private float mT10;
    private float mU10C;
    private float mI10C;
    private float mU10Average;
    private float mI10Average = -1f;

    private float mU09A;
    private float mI09A;
    private float mU09B;
    private float mI09B;
    private float mP09 = -1f;
    private float mCos09;
    private float mTempAmbient09;
    private float mTempEngine09;
    private float mT09;
    private float mU09C;
    private float mI09C;
    private float mU09Average;
    private float mI09Average = -1f;

    private float mU08A;
    private float mI08A;
    private float mU08B;
    private float mI08B;
    private float mP08 = -1f;
    private float mCos08;
    private float mTempAmbient08;
    private float mTempEngine08;
    private float mT08;
    private float mU08C;
    private float mI08C;
    private float mU08Average;
    private float mI08Average = -1f;

    private float mU07A;
    private float mI07A;
    private float mU07B;
    private float mI07B;
    private float mP07 = -1f;
    private float mCos07;
    private float mTempAmbient07;
    private float mTempEngine07;
    private float mT07;
    private float mU07C;
    private float mI07C;
    private float mU07Average;
    private float mI07Average = -1f;

    private float mU06A;
    private float mI06A;
    private float mU06B;
    private float mI06B;
    private float mP06 = -1f;
    private float mCos06;
    private float mTempAmbient06;
    private float mTempEngine06;
    private float mT06;
    private float mU06C;
    private float mI06C;
    private float mU06Average;
    private float mI06Average = -1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment9);
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
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_SC) != 0) {
                mNumOfStages = extras.getInt(MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_SC);
                if (mNumOfStages == 1) {
//                    setViewAndChildrenVisibility(mGraphPanel, View.GONE);
                } else if (mNumOfStages == 5) {
//                    setViewAndChildrenVisibility(mGraphPanel, View.VISIBLE);
                }
            } else {
                throw new NullPointerException("Не передано " + MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_SC);
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U) != 0) {
                mSpecifiedU10 = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U);
                mSpecifiedU09 = (float) (mSpecifiedU10 * 0.9);
                mSpecifiedU08 = (float) (mSpecifiedU10 * 0.8);
                mSpecifiedU07 = (float) (mSpecifiedU10 * 0.7);
                mSpecifiedU06 = (float) (mSpecifiedU10 * 0.6);
            } else {
                throw new NullPointerException("Не передано specifiedU");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_T) != 0) {
                mSpecifiedT = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_T);
            } else {
                throw new NullPointerException("Не передано specifiedT");
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
            mCurrentStage = 10;
            setFRA800ObjectReady(false);
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setFRA800ObjectResponding(true);
            setPM130Responding(true);
            setTRM201Responding(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            changeTextOfView(mStatus, "Испытание началось");
            mDevicesController.initDevicesFrom9Group();
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(1000);
            }
            while (isExperimentStart() && !mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }

            if (isExperimentStart() && mStartState) {
                mDevicesController.initDevicesFrom9Group();
            }

            while (isExperimentStart() && !isDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, getNotRespondingDevicesString("Нет связи с устройствами"));
                sleep(100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom8To9Group();
                m200to5State = true;
                sleep(500);
                mDevicesController.setObjectParams(100, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.startObject();
                sleep(2000);
            }
            while (isExperimentStart() && !mFRA800ObjectReady && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь выйдет к заданным характеристикам");
            }

            int lastLevel = regulation();

            if (mNumOfStages > 1) {
                mCurrentStage = 9;

                if (isExperimentStart() && mStartState && isDevicesResponding()) {
                    stateToBack();
                }

                regulation();

                mCurrentStage = 8;

                if (isExperimentStart() && mStartState && isDevicesResponding()) {
                    stateToBack();
                }

                regulation();

                mCurrentStage = 7;

                if (isExperimentStart() && mStartState && isDevicesResponding()) {
                    stateToBack();
                }

                regulation();

                mCurrentStage = 6;

                if (isExperimentStart() && mStartState && isDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = regulation();

                mCurrentStage = 5;
            } else {
                mCurrentStage = 5;
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
            mDevicesController.offKMsFrom8To9Group();
            m200to5State = false;
            m40to5State = false;
            m5to5State = false;

            return null;
        }

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isFRA800ObjectResponding() ? "" : "ЧП ОИ, ",
                    isPM130Responding() ? "" : "PM130, ",
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
            case 10:
                textView = mT10Cell;
                break;
            case 9:
                textView = mT09Cell;
                break;
            case 8:
                textView = mT08Cell;
                break;
            case 7:
                textView = mT07Cell;
                break;
            case 6:
                textView = mT06Cell;
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

    private int regulation() {
        int uInt = 0;
        if (isExperimentStart() && mStartState && isDevicesResponding()) {
            switch (mCurrentStage) {
                case 10:
                    uInt = ((int) (mSpecifiedU10 * 100)) / 10;
                    break;
                case 9:
                    uInt = ((int) (mSpecifiedU09 * 100)) / 10;
                    break;
                case 8:
                    uInt = ((int) (mSpecifiedU08 * 100)) / 10;
                    break;
                case 7:
                    uInt = ((int) (mSpecifiedU07 * 100)) / 10;
                    break;
                case 6:
                    uInt = ((int) (mSpecifiedU06 * 100)) / 10;
                    break;
            }

            mDevicesController.setObjectUMax(uInt);

            sleep(3000);

            uInt = uInt + (int) (uInt - mPM130V1 * 10) + 2;

            mDevicesController.setObjectUMax(uInt);
        }

        if (isExperimentStart() && mStartState && isDevicesResponding()) {
            pickUpState();
        }

        int experimentTime = mSpecifiedT;
        while (isExperimentStart() && (experimentTime > 0) && mStartState && isDevicesResponding()) {
            experimentTime--;
            sleep(1000);
            changeTextOfView(mStatus, "Ждём заданное T. Осталось: " + experimentTime);
            setT("" + experimentTime);
        }

        return uInt;
    }

    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isFRA800ObjectResponding() && isPM130Responding() && isTRM201Responding();
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
                    case PM130Model.COS_PARAM:
                        setPM130Cos((float) value);
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

    public void setPM130V1(float PM130V1) {
        mPM130V1 = PM130V1;
        switch (mCurrentStage) {
            case 10:
                setU10A(PM130V1);
                break;
            case 9:
                setU09A(PM130V1);
                break;
            case 8:
                setU08A(PM130V1);
                break;
            case 7:
                setU07A(PM130V1);
                break;
            case 6:
                setU06A(PM130V1);
                break;
        }
    }

    public void setPM130V2(float PM130V2) {
        mPM130V2 = PM130V2;
        switch (mCurrentStage) {
            case 10:
                setU10B(PM130V2);
                break;
            case 9:
                setU09B(PM130V2);
                break;
            case 8:
                setU08B(PM130V2);
                break;
            case 7:
                setU07B(PM130V2);
                break;
            case 6:
                setU06B(PM130V2);
                break;
        }
    }

    public void setPM130V3(float PM130V3) {
        mPM130V3 = PM130V3;
        switch (mCurrentStage) {
            case 10:
                setU10C(PM130V3);
                break;
            case 9:
                setU09C(PM130V3);
                break;
            case 8:
                setU08C(PM130V3);
                break;
            case 7:
                setU07C(PM130V3);
                break;
            case 6:
                setU06C(PM130V3);
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
            case 10:
                setI10A(PM130I1);
                break;
            case 9:
                setI09A(PM130I1);
                break;
            case 8:
                setI08A(PM130I1);
                break;
            case 7:
                setI07A(PM130I1);
                break;
            case 6:
                setI06A(PM130I1);
                break;
        }
    }

    public void setPM130I2(float PM130I2) {
        mPM130I2 = PM130I2;
        switch (mCurrentStage) {
            case 10:
                setI10B(PM130I2);
                break;
            case 9:
                setI09B(PM130I2);
                break;
            case 8:
                setI08B(PM130I2);
                break;
            case 7:
                setI07B(PM130I2);
                break;
            case 6:
                setI06B(PM130I2);
                break;
        }
    }

    public void setPM130I3(float PM130I3) {
        mPM130I3 = PM130I3;
        switch (mCurrentStage) {
            case 10:
                setI10C(PM130I3);
                break;
            case 9:
                setI09C(PM130I3);
                break;
            case 8:
                setI08C(PM130I3);
                break;
            case 7:
                setI07C(PM130I3);
                break;
            case 6:
                setI06C(PM130I3);
                break;
        }
    }

    public void setPM130P(float PM130P) {
        mPM130P = PM130P;
        switch (mCurrentStage) {
            case 10:
                setP10(PM130P);
                break;
            case 9:
                setP09(PM130P);
                break;
            case 8:
                setP08(PM130P);
                break;
            case 7:
                setP07(PM130P);
                break;
            case 6:
                setP06(PM130P);
                break;
        }
    }

    public void setPM130Cos(float PM130Cos) {
        mPM130Cos = PM130Cos;
        switch (mCurrentStage) {
            case 10:
                setCos10(PM130Cos);
                break;
            case 9:
                setCos09(PM130Cos);
                break;
            case 8:
                setCos08(PM130Cos);
                break;
            case 7:
                setCos07(PM130Cos);
                break;
            case 6:
                setCos06(PM130Cos);
                break;
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
        switch (mCurrentStage) {
            case 10:
                setTempAmbient10(tempAmbient);
                break;
            case 9:
                setTempAmbient09(tempAmbient);
                break;
            case 8:
                setTempAmbient08(tempAmbient);
                break;
            case 7:
                setTempAmbient07(tempAmbient);
                break;
            case 6:
                setTempAmbient06(tempAmbient);
                break;
        }
    }

    public void setTempEngine(float tempEngine) {
        mTempEngine = tempEngine;
        switch (mCurrentStage) {
            case 10:
                setTempEngine10(tempEngine);
                break;
            case 9:
                setTempEngine09(tempEngine);
                break;
            case 8:
                setTempEngine08(tempEngine);
                break;
            case 7:
                setTempEngine07(tempEngine);
                break;
            case 6:
                setTempEngine06(tempEngine);
                break;
        }
    }

    public void setU10A(float u10A) {
        mU10A = u10A;
        changeTextOfView(mU10ACell, formatRealNumber(u10A));
    }

    public void setI10A(float i10A) {
        mI10A = i10A;
        changeTextOfView(mI10ACell, formatRealNumber(i10A));
    }

    public void setU10B(float u10B) {
        mU10B = u10B;
        changeTextOfView(mU10BCell, formatRealNumber(u10B));
    }

    public void setI10B(float i10B) {
        mI10B = i10B;
        changeTextOfView(mI10BCell, formatRealNumber(i10B));
    }

    public void setP10(float p10) {
        mP10 = p10;
        changeTextOfView(mP10Cell, formatRealNumber(p10));
    }

    public void setCos10(float cos10) {
        mCos10 = cos10;
        changeTextOfView(mCos10Cell, formatRealNumber(cos10));
    }

    public void setTempAmbient10(float tempAmbient10) {
        mTempAmbient10 = tempAmbient10;
        changeTextOfView(mTempAmbient10Cell, formatRealNumber(tempAmbient10));
    }

    public void setTempEngine10(float tempEngine10) {
        mTempEngine10 = tempEngine10;
        changeTextOfView(mTempEngine10Cell, formatRealNumber(tempEngine10));
    }

    public void setT10(float t10) {
        mT10 = t10;
    }

    public void setU10C(float u10C) {
        mU10C = u10C;
        changeTextOfView(mU10CCell, formatRealNumber(u10C));
        setU10Average((mU10A + mU10B + u10C) / 3f);
    }

    public void setU10Average(float U10Average) {
        mU10Average = U10Average;
        changeTextOfView(mU10AverageCell, formatRealNumber(U10Average));
    }

    public void setI10C(float i10C) {
        mI10C = i10C;
        changeTextOfView(mI10CCell, formatRealNumber(i10C));
        setI10Average((mI10A + mI10B + i10C) / 3f);
    }

    public void setI10Average(float I10Average) {
        mI10Average = I10Average;
        changeTextOfView(mI10AverageCell, formatRealNumber(I10Average));
    }

    public void setU09A(float u09A) {
        mU09A = u09A;
        changeTextOfView(mU09ACell, formatRealNumber(u09A));
    }

    public void setI09A(float i09A) {
        mI09A = i09A;
        changeTextOfView(mI09ACell, formatRealNumber(i09A));
    }

    public void setU09B(float u09B) {
        mU09B = u09B;
        changeTextOfView(mU09BCell, formatRealNumber(u09B));
    }

    public void setI09B(float i09B) {
        mI09B = i09B;
        changeTextOfView(mI09BCell, formatRealNumber(i09B));
    }

    public void setP09(float p09) {
        mP09 = p09;
        changeTextOfView(mP09Cell, formatRealNumber(p09));
    }

    public void setCos09(float cos09) {
        mCos09 = cos09;
        changeTextOfView(mCos09Cell, formatRealNumber(cos09));
    }

    public void setTempAmbient09(float tempAmbient09) {
        mTempAmbient09 = tempAmbient09;
        changeTextOfView(mTempAmbient09Cell, formatRealNumber(tempAmbient09));
    }

    public void setTempEngine09(float tempEngine09) {
        mTempEngine09 = tempEngine09;
        changeTextOfView(mTempEngine09Cell, formatRealNumber(tempEngine09));
    }

    public void setT09(float t09) {
        mT09 = t09;
    }

    public void setU09C(float u09C) {
        mU09C = u09C;
        changeTextOfView(mU09CCell, formatRealNumber(u09C));
        setU09Average((mU09A + mU09B + u09C) / 3f);
    }

    public void setU09Average(float U09Average) {
        mU09Average = U09Average;
        changeTextOfView(mU09AverageCell, formatRealNumber(U09Average));
    }

    public void setI09C(float i09C) {
        mI09C = i09C;
        changeTextOfView(mI09CCell, formatRealNumber(i09C));
        setI09Average((mI09A + mI09B + i09C) / 3f);
    }

    public void setI09Average(float I09Average) {
        mI09Average = I09Average;
        changeTextOfView(mI09AverageCell, formatRealNumber(I09Average));
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

    public void setP08(float p08) {
        mP08 = p08;
        changeTextOfView(mP08Cell, formatRealNumber(p08));
    }

    public void setCos08(float cos08) {
        mCos08 = cos08;
        changeTextOfView(mCos08Cell, formatRealNumber(cos08));
    }

    public void setTempAmbient08(float tempAmbient08) {
        mTempAmbient08 = tempAmbient08;
        changeTextOfView(mTempAmbient08Cell, formatRealNumber(tempAmbient08));
    }

    public void setTempEngine08(float tempEngine08) {
        mTempEngine08 = tempEngine08;
        changeTextOfView(mTempEngine08Cell, formatRealNumber(tempEngine08));
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

    public void setU07A(float u07A) {
        mU07A = u07A;
        changeTextOfView(mU07ACell, formatRealNumber(u07A));
    }

    public void setI07A(float i07A) {
        mI07A = i07A;
        changeTextOfView(mI07ACell, formatRealNumber(i07A));
    }

    public void setU07B(float u07B) {
        mU07B = u07B;
        changeTextOfView(mU07BCell, formatRealNumber(u07B));
    }

    public void setI07B(float i07B) {
        mI07B = i07B;
        changeTextOfView(mI07BCell, formatRealNumber(i07B));
    }

    public void setP07(float p07) {
        mP07 = p07;
        changeTextOfView(mP07Cell, formatRealNumber(p07));
    }

    public void setCos07(float cos07) {
        mCos07 = cos07;
        changeTextOfView(mCos07Cell, formatRealNumber(cos07));
    }

    public void setTempAmbient07(float tempAmbient07) {
        mTempAmbient07 = tempAmbient07;
        changeTextOfView(mTempAmbient07Cell, formatRealNumber(tempAmbient07));
    }

    public void setTempEngine07(float tempEngine07) {
        mTempEngine07 = tempEngine07;
        changeTextOfView(mTempEngine07Cell, formatRealNumber(tempEngine07));
    }

    public void setT07(float t07) {
        mT07 = t07;
    }

    public void setU07C(float u07C) {
        mU07C = u07C;
        changeTextOfView(mU07CCell, formatRealNumber(u07C));
        setU07Average((mU07A + mU07B + u07C) / 3f);
    }

    public void setU07Average(float U07Average) {
        mU07Average = U07Average;
        changeTextOfView(mU07AverageCell, formatRealNumber(U07Average));
    }

    public void setI07C(float i07C) {
        mI07C = i07C;
        changeTextOfView(mI07CCell, formatRealNumber(i07C));
        setI07Average((mI07A + mI07B + i07C) / 3f);
    }

    public void setI07Average(float I07Average) {
        mI07Average = I07Average;
        changeTextOfView(mI07AverageCell, formatRealNumber(I07Average));
    }

    public void setU06A(float u06A) {
        mU06A = u06A;
        changeTextOfView(mU06ACell, formatRealNumber(u06A));
    }

    public void setI06A(float i06A) {
        mI06A = i06A;
        changeTextOfView(mI06ACell, formatRealNumber(i06A));
    }

    public void setU06B(float u06B) {
        mU06B = u06B;
        changeTextOfView(mU06BCell, formatRealNumber(u06B));
    }

    public void setI06B(float i06B) {
        mI06B = i06B;
        changeTextOfView(mI06BCell, formatRealNumber(i06B));
    }

    public void setP06(float p06) {
        mP06 = p06;
        changeTextOfView(mP06Cell, formatRealNumber(p06));
    }

    public void setCos06(float cos06) {
        mCos06 = cos06;
        changeTextOfView(mCos06Cell, formatRealNumber(cos06));
    }

    public void setTempAmbient06(float tempAmbient06) {
        mTempAmbient06 = tempAmbient06;
        changeTextOfView(mTempAmbient06Cell, formatRealNumber(tempAmbient06));
    }

    public void setTempEngine06(float tempEngine06) {
        mTempEngine06 = tempEngine06;
        changeTextOfView(mTempEngine06Cell, formatRealNumber(tempEngine06));
    }

    public void setT06(float t06) {
        mT06 = t06;
    }

    public void setU06C(float u06C) {
        mU06C = u06C;
        changeTextOfView(mU06CCell, formatRealNumber(u06C));
        setU06Average((mU06A + mU06B + u06C) / 3f);
    }

    public void setU06Average(float U06Average) {
        mU06Average = U06Average;
        changeTextOfView(mU06AverageCell, formatRealNumber(U06Average));
    }

    public void setI06C(float i06C) {
        mI06C = i06C;
        changeTextOfView(mI06CCell, formatRealNumber(i06C));
        setI06Average((mI06A + mI06B + i06C) / 3f);
    }

    public void setI06Average(float I06Average) {
        mI06Average = I06Average;
        changeTextOfView(mI06AverageCell, formatRealNumber(I06Average));
    }

    private void clearCells() {
        changeTextOfView(mU10ACell, "");
        changeTextOfView(mI10ACell, "");
        changeTextOfView(mU10BCell, "");
        changeTextOfView(mI10BCell, "");
        changeTextOfView(mP10Cell, "");
        changeTextOfView(mCos10Cell, "");
        changeTextOfView(mTempAmbient10Cell, "");
        changeTextOfView(mTempEngine10Cell, "");
        changeTextOfView(mT10Cell, "");
        changeTextOfView(mU10CCell, "");
        changeTextOfView(mI10CCell, "");
        changeTextOfView(mU10AverageCell, "");
        changeTextOfView(mI10AverageCell, "");
        changeTextOfView(mU09ACell, "");
        changeTextOfView(mI09ACell, "");
        changeTextOfView(mU09BCell, "");
        changeTextOfView(mI09BCell, "");
        changeTextOfView(mP09Cell, "");
        changeTextOfView(mCos09Cell, "");
        changeTextOfView(mTempAmbient09Cell, "");
        changeTextOfView(mTempEngine09Cell, "");
        changeTextOfView(mT09Cell, "");
        changeTextOfView(mU09CCell, "");
        changeTextOfView(mI09CCell, "");
        changeTextOfView(mU09AverageCell, "");
        changeTextOfView(mI09AverageCell, "");
        changeTextOfView(mU08ACell, "");
        changeTextOfView(mI08ACell, "");
        changeTextOfView(mU08BCell, "");
        changeTextOfView(mI08BCell, "");
        changeTextOfView(mP08Cell, "");
        changeTextOfView(mCos08Cell, "");
        changeTextOfView(mTempAmbient08Cell, "");
        changeTextOfView(mTempEngine08Cell, "");
        changeTextOfView(mT08Cell, "");
        changeTextOfView(mU08CCell, "");
        changeTextOfView(mI08CCell, "");
        changeTextOfView(mU08AverageCell, "");
        changeTextOfView(mI08AverageCell, "");
        changeTextOfView(mU07ACell, "");
        changeTextOfView(mI07ACell, "");
        changeTextOfView(mU07BCell, "");
        changeTextOfView(mI07BCell, "");
        changeTextOfView(mP07Cell, "");
        changeTextOfView(mCos07Cell, "");
        changeTextOfView(mTempAmbient07Cell, "");
        changeTextOfView(mTempEngine07Cell, "");
        changeTextOfView(mT07Cell, "");
        changeTextOfView(mU07CCell, "");
        changeTextOfView(mI07CCell, "");
        changeTextOfView(mU07AverageCell, "");
        changeTextOfView(mI07AverageCell, "");
        changeTextOfView(mU06ACell, "");
        changeTextOfView(mI06ACell, "");
        changeTextOfView(mU06BCell, "");
        changeTextOfView(mI06BCell, "");
        changeTextOfView(mP06Cell, "");
        changeTextOfView(mCos06Cell, "");
        changeTextOfView(mTempAmbient06Cell, "");
        changeTextOfView(mTempEngine06Cell, "");
        changeTextOfView(mT06Cell, "");
        changeTextOfView(mU06CCell, "");
        changeTextOfView(mI06CCell, "");
        changeTextOfView(mU06AverageCell, "");
        changeTextOfView(mI06AverageCell, "");
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
        data.putExtra(MainActivity.INPUT_PARAMETER.I10_SC_R, mI10Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P10_SC_R, mP10);
        data.putExtra(MainActivity.INPUT_PARAMETER.I09_SC_R, mI09Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P09_SC_R, mP09);
        data.putExtra(MainActivity.INPUT_PARAMETER.I08_SC_R, mI08Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P08_SC_R, mP08);
        data.putExtra(MainActivity.INPUT_PARAMETER.I07_SC_R, mI07Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P07_SC_R, mP07);
        data.putExtra(MainActivity.INPUT_PARAMETER.I06_SC_R, mI06Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P06_SC_R, mP06);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE9U10A(mU10ACell.getText().toString());
        experiments.setE9I10A(mI10ACell.getText().toString());
        experiments.setE9U10B(mU10BCell.getText().toString());
        experiments.setE9I10B(mI10BCell.getText().toString());
        experiments.setE9P10(mP10Cell.getText().toString());
        experiments.setE9Cos10(mCos10Cell.getText().toString());
        experiments.setE9TempAmbient10(mTempAmbient10Cell.getText().toString());
        experiments.setE9TempEngine10(mTempEngine10Cell.getText().toString());
        experiments.setE9T10(mT10Cell.getText().toString());
        experiments.setE9U10C(mU10CCell.getText().toString());
        experiments.setE9I10C(mI10CCell.getText().toString());
        experiments.setE9U10Average(mU10AverageCell.getText().toString());
        experiments.setE9I10Average(mI10AverageCell.getText().toString());
        experiments.setE9U09A(mU09ACell.getText().toString());
        experiments.setE9I09A(mI09ACell.getText().toString());
        experiments.setE9U09B(mU09BCell.getText().toString());
        experiments.setE9I09B(mI09BCell.getText().toString());
        experiments.setE9P09(mP09Cell.getText().toString());
        experiments.setE9Cos09(mCos09Cell.getText().toString());
        experiments.setE9TempAmbient09(mTempAmbient09Cell.getText().toString());
        experiments.setE9TempEngine09(mTempEngine09Cell.getText().toString());
        experiments.setE9T09(mT09Cell.getText().toString());
        experiments.setE9U09C(mU09CCell.getText().toString());
        experiments.setE9I09C(mI09CCell.getText().toString());
        experiments.setE9U09Average(mU09AverageCell.getText().toString());
        experiments.setE9I09Average(mI09AverageCell.getText().toString());
        experiments.setE9U08A(mU08ACell.getText().toString());
        experiments.setE9I08A(mI08ACell.getText().toString());
        experiments.setE9U08B(mU08BCell.getText().toString());
        experiments.setE9I08B(mI08BCell.getText().toString());
        experiments.setE9P08(mP08Cell.getText().toString());
        experiments.setE9Cos08(mCos08Cell.getText().toString());
        experiments.setE9TempAmbient08(mTempAmbient08Cell.getText().toString());
        experiments.setE9TempEngine08(mTempEngine08Cell.getText().toString());
        experiments.setE9T08(mT08Cell.getText().toString());
        experiments.setE9U08C(mU08CCell.getText().toString());
        experiments.setE9I08C(mI08CCell.getText().toString());
        experiments.setE9U08Average(mU08AverageCell.getText().toString());
        experiments.setE9I08Average(mI08AverageCell.getText().toString());
        experiments.setE9U07A(mU07ACell.getText().toString());
        experiments.setE9I07A(mI07ACell.getText().toString());
        experiments.setE9U07B(mU07BCell.getText().toString());
        experiments.setE9I07B(mI07BCell.getText().toString());
        experiments.setE9P07(mP07Cell.getText().toString());
        experiments.setE9Cos07(mCos07Cell.getText().toString());
        experiments.setE9TempAmbient07(mTempAmbient07Cell.getText().toString());
        experiments.setE9TempEngine07(mTempEngine07Cell.getText().toString());
        experiments.setE9T07(mT07Cell.getText().toString());
        experiments.setE9U07C(mU07CCell.getText().toString());
        experiments.setE9I07C(mI07CCell.getText().toString());
        experiments.setE9U07Average(mU07AverageCell.getText().toString());
        experiments.setE9I07Average(mI07AverageCell.getText().toString());
        experiments.setE9U06A(mU06ACell.getText().toString());
        experiments.setE9I06A(mI06ACell.getText().toString());
        experiments.setE9U06B(mU06BCell.getText().toString());
        experiments.setE9I06B(mI06BCell.getText().toString());
        experiments.setE9P06(mP06Cell.getText().toString());
        experiments.setE9Cos06(mCos06Cell.getText().toString());
        experiments.setE9TempAmbient06(mTempAmbient06Cell.getText().toString());
        experiments.setE9TempEngine06(mTempEngine06Cell.getText().toString());
        experiments.setE9T06(mT06Cell.getText().toString());
        experiments.setE9U06C(mU06CCell.getText().toString());
        experiments.setE9I06C(mI06CCell.getText().toString());
        experiments.setE9U06Average(mU06AverageCell.getText().toString());
        experiments.setE9I06Average(mI06AverageCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
