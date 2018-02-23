package ru.avem.kspad.view;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import ru.avem.kspad.communication.devices.trm201.TRM201Model;
import ru.avem.kspad.communication.devices.veha_t.VEHATModel;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;
import ru.avem.kspad.utils.Logger;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.DeviceController.PM130_ID;
import static ru.avem.kspad.communication.devices.DeviceController.TRM201_ID;
import static ru.avem.kspad.communication.devices.DeviceController.VEHA_T_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment7Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Определение токов и потерь холостого хода";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int STATE_40_TO_5_MULTIPLIER = 40 / 5;
    private static final int STATE_5_TO_5_MULTIPLIER = 5 / 5;

    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.u_1_3_a)
    TextView mU13ACell;
    @BindView(R.id.i_1_3_a)
    TextView mI13ACell;
    @BindView(R.id.u_1_3_b)
    TextView mU13BCell;
    @BindView(R.id.i_1_3_b)
    TextView mI13BCell;
    @BindView(R.id.p_1_3)
    TextView mP13Cell;
    @BindView(R.id.cos_1_3)
    TextView mCos13Cell;
    @BindView(R.id.v_1_3)
    TextView mV13Cell;
    @BindView(R.id.temp_1_3_ambient)
    TextView mTemp13AmbientCell;
    @BindView(R.id.temp_1_3_engine)
    TextView mTemp13EngineCell;
    @BindView(R.id.t_1_3)
    TextView mT13Cell;
    @BindView(R.id.u_1_3_c)
    TextView mU13CCell;
    @BindView(R.id.i_1_3_c)
    TextView mI13CCell;
    @BindView(R.id.u_1_3_average)
    TextView mU13AverageCell;
    @BindView(R.id.i_1_3_average)
    TextView mI13AverageCell;

    @BindView(R.id.u_1_2_a)
    TextView mU12ACell;
    @BindView(R.id.i_1_2_a)
    TextView mI12ACell;
    @BindView(R.id.u_1_2_b)
    TextView mU12BCell;
    @BindView(R.id.i_1_2_b)
    TextView mI12BCell;
    @BindView(R.id.p_1_2)
    TextView mP12Cell;
    @BindView(R.id.cos_1_2)
    TextView mCos12Cell;
    @BindView(R.id.v_1_2)
    TextView mV12Cell;
    @BindView(R.id.temp_1_2_ambient)
    TextView mTemp12AmbientCell;
    @BindView(R.id.temp_1_2_engine)
    TextView mTemp12EngineCell;
    @BindView(R.id.t_1_2)
    TextView mT12Cell;
    @BindView(R.id.u_1_2_c)
    TextView mU12CCell;
    @BindView(R.id.i_1_2_c)
    TextView mI12CCell;
    @BindView(R.id.u_1_2_average)
    TextView mU12AverageCell;
    @BindView(R.id.i_1_2_average)
    TextView mI12AverageCell;

    @BindView(R.id.u_1_1_a)
    TextView mU11ACell;
    @BindView(R.id.i_1_1_a)
    TextView mI11ACell;
    @BindView(R.id.u_1_1_b)
    TextView mU11BCell;
    @BindView(R.id.i_1_1_b)
    TextView mI11BCell;
    @BindView(R.id.p_1_1)
    TextView mP11Cell;
    @BindView(R.id.cos_1_1)
    TextView mCos11Cell;
    @BindView(R.id.v_1_1)
    TextView mV11Cell;
    @BindView(R.id.temp_1_1_ambient)
    TextView mTemp11AmbientCell;
    @BindView(R.id.temp_1_1_engine)
    TextView mTemp11EngineCell;
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
    @BindView(R.id.v_1_0)
    TextView mV10Cell;
    @BindView(R.id.temp_1_0_ambient)
    TextView mTemp10AmbientCell;
    @BindView(R.id.temp_1_0_engine)
    TextView mTemp10EngineCell;
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
    @BindView(R.id.v_0_9)
    TextView mV09Cell;
    @BindView(R.id.temp_0_9_ambient)
    TextView mTemp09AmbientCell;
    @BindView(R.id.temp_0_9_engine)
    TextView mTemp09EngineCell;
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
    @BindView(R.id.v_0_8)
    TextView mV08Cell;
    @BindView(R.id.temp_0_8_ambient)
    TextView mTemp08AmbientCell;
    @BindView(R.id.temp_0_8_engine)
    TextView mTemp08EngineCell;
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
    @BindView(R.id.v_0_7)
    TextView mV07Cell;
    @BindView(R.id.temp_0_7_ambient)
    TextView mTemp07AmbientCell;
    @BindView(R.id.temp_0_7_engine)
    TextView mTemp07EngineCell;
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
    @BindView(R.id.v_0_6)
    TextView mV06Cell;
    @BindView(R.id.temp_0_6_ambient)
    TextView mTemp06AmbientCell;
    @BindView(R.id.temp_0_6_engine)
    TextView mTemp06EngineCell;
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

    @BindView(R.id.u_0_5_a)
    TextView mU05ACell;
    @BindView(R.id.i_0_5_a)
    TextView mI05ACell;
    @BindView(R.id.u_0_5_b)
    TextView mU05BCell;
    @BindView(R.id.i_0_5_b)
    TextView mI05BCell;
    @BindView(R.id.p_0_5)
    TextView mP05Cell;
    @BindView(R.id.cos_0_5)
    TextView mCos05Cell;
    @BindView(R.id.v_0_5)
    TextView mV05Cell;
    @BindView(R.id.temp_0_5_ambient)
    TextView mTemp05AmbientCell;
    @BindView(R.id.temp_0_5_engine)
    TextView mTemp05EngineCell;
    @BindView(R.id.t_0_5)
    TextView mT05Cell;
    @BindView(R.id.u_0_5_c)
    TextView mU05CCell;
    @BindView(R.id.i_0_5_c)
    TextView mI05CCell;
    @BindView(R.id.u_0_5_average)
    TextView mU05AverageCell;
    @BindView(R.id.i_0_5_average)
    TextView mI05AverageCell;

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

    private int mCurrentStage = 10;

    private float mSpecifiedU13;
    private float mSpecifiedU12;
    private float mSpecifiedU11;
    private float mSpecifiedU10;
    private float mSpecifiedU09;
    private float mSpecifiedU08;
    private float mSpecifiedU07;
    private float mSpecifiedU06;
    private float mSpecifiedU05;

    private int mSpecifiedT1;
    private int mSpecifiedT2;
    private boolean mPlatformOneSelected;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mVEHATResponding;
    private float mV;

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

    private float mU13A;
    private float mI13A;
    private float mU13B;
    private float mI13B;
    private float mP13 = -1f;
    private float mCos13;
    private float mV13;
    private float mTemp13Ambient;
    private float mTemp13Engine;
    private float mT13;
    private float mU13C;
    private float mI13C;
    private float mU13Average;
    private float mI13Average = -1f;

    private float mU12A;
    private float mI12A;
    private float mU12B;
    private float mI12B;
    private float mP12 = -1f;
    private float mCos12;
    private float mV12;
    private float mTemp12Ambient;
    private float mTemp12Engine;
    private float mT12;
    private float mU12C;
    private float mI12C;
    private float mU12Average;
    private float mI12Average = -1f;

    private float mU11A;
    private float mI11A;
    private float mU11B;
    private float mI11B;
    private float mP11 = -1f;
    private float mCos11;
    private float mV11;
    private float mTemp11Ambient;
    private float mTemp11Engine;
    private float mT11;
    private float mU11C;
    private float mI11C;
    private float mU11Average;
    private float mI11Average = -1f;

    private float mU10A;
    private float mI10A;
    private float mU10B;
    private float mI10B;
    private float mP10 = -1f;
    private float mCos10;
    private float mV10;
    private float mTemp10Ambient;
    private float mTemp10Engine;
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
    private float mV09;
    private float mTemp09Ambient;
    private float mTemp09Engine;
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
    private float mV08;
    private float mTemp08Ambient;
    private float mTemp08Engine;
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
    private float mV07;
    private float mTemp07Ambient;
    private float mTemp07Engine;
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
    private float mV06;
    private float mTemp06Ambient;
    private float mTemp06Engine;
    private float mT06;
    private float mU06C;
    private float mI06C;
    private float mU06Average = -1f;
    private float mI06Average = -1f;

    private float mU05A;
    private float mI05A;
    private float mU05B;
    private float mI05B;
    private float mP05 = -1f;
    private float mCos05;
    private float mV05;
    private float mTemp05Ambient;
    private float mTemp05Engine;
    private float mT05;
    private float mU05C;
    private float mI05C;
    private float mU05Average = -1f;
    private float mI05Average = -1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment7);
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
                mSpecifiedU10 = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U);
                mSpecifiedU13 = (float) (mSpecifiedU10 * 1.3);
                mSpecifiedU12 = (float) (mSpecifiedU10 * 1.2);
                mSpecifiedU11 = (float) (mSpecifiedU10 * 1.1);
                mSpecifiedU09 = (float) (mSpecifiedU10 * 0.9);
                mSpecifiedU08 = (float) (mSpecifiedU10 * 0.8);
                mSpecifiedU07 = (float) (mSpecifiedU10 * 0.7);
                mSpecifiedU06 = (float) (mSpecifiedU10 * 0.6);
                mSpecifiedU05 = (float) (mSpecifiedU10 * 0.5);
            } else {
                throw new NullPointerException("Не передано specifiedU");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_T1) != 0) {
                mSpecifiedT1 = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_T1);
            } else {
                throw new NullPointerException("Не передано specifiedT1");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_T2) != 0) {
                mSpecifiedT2 = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_T2);
            } else {
                throw new NullPointerException("Не передано specifiedT2");
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
        if (!experimentStart) {
            mStatus.setText("В ожидании начала испытания");
        }
    }

    private void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

    private class ExperimentTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            setExperimentStart(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            changeTextOfView(mStatus, "Испытание началось");
            mDevicesController.initDevicesFrom7To8Group();
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(1000);
            }

            while (isExperimentStart() && !mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }
// TODO: 03.01.2018 проверки
            changeTextOfView(mStatus, "Инициализация...");
            mDevicesController.initDevicesFrom7To8Group();
            while (isExperimentStart() && !isDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, "Нет связи с устройствами");
                sleep(100);
            }
            mDevicesController.onKMsFrom4And7And13Group();
            m200to5State = true;
            sleep(500);
            mDevicesController.setObjectParams(100, 5000, 5000);

            mDevicesController.startObject();
            sleep(2000);
            while (isExperimentStart() && !mFRA800ObjectReady && mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь выйдет к заданным характеристикам");
            }

            int lastLevel = regulation(10 * 10, 3 * 10, 3, mSpecifiedU10, 0.05, 1, 400, 1050);

            pickUpState();

            int experimentTime = mSpecifiedT1;
            while (isExperimentStart() && (experimentTime > 0) && mStartState) {
                experimentTime--;
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время в ХХ. Осталось: " + experimentTime);
                setT("" + experimentTime);
            }

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            if (isExperimentStart() && mStartState) {
                sleep(2000);
            }

            mCurrentStage = 13;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU13);

            mCurrentStage = 12;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU12);

            mCurrentStage = 11;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU11);

            mCurrentStage = 10;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU10);

            mCurrentStage = 9;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU09);

            mCurrentStage = 8;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU08);

            mCurrentStage = 7;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU07);

            mCurrentStage = 6;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU06);

            mCurrentStage = 5;

            if (isExperimentStart() && mStartState) {
                stateToBack();
            }

            lastLevel = startStage(lastLevel, mSpecifiedU05);

            mCurrentStage = 4;


            if (isExperimentStart() && mStartState) {
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDevicesController.diversifyDevices();
            mExperimentSwitch.setChecked(false);
            mStatus.setText("Испытание закончено");
            mCurrentStage = 10;
        }
    }

    private int startStage(int lastLevel, float u) {
        lastLevel = regulation(lastLevel, 3 * 10, 3, u, 0.05, 1, 400, 1050);

        if (isExperimentStart() && mStartState) {
            pickUpState();
        }

        int experimentTime = mSpecifiedT2;
        while (isExperimentStart() && (experimentTime > 0) && mStartState) {
            experimentTime--;
            sleep(1000);
            changeTextOfView(mStatus, "Ждём заданное на ступень время. Осталось: " + experimentTime);
            setT("" + experimentTime);
        }
        return lastLevel;
    }

    private void setT(String time) {
        TextView textView = null;
        switch (mCurrentStage) {
            case 13:
                textView = mT13Cell;
                break;
            case 12:
                textView = mT12Cell;
                break;
            case 11:
                textView = mT11Cell;
                break;
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
            case 5:
                textView = mT05Cell;
                break;
        }
        changeTextOfView(textView, time);
    }

    private int regulation(int start, int coarseStep, int fineStep, float end, double coarseLimit, double fineLimit, int coarseSleep, int fineSleep) {
        double coarseMinLimit = 1 - coarseLimit;
        double coarseMaxLimit = 1 + coarseLimit;
        while (isExperimentStart() && ((mPM130V1 < end * coarseMinLimit) || (mPM130V1 > end * coarseMaxLimit)) && mStartState) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mPM130V1);
            if (mPM130V1 < end * coarseMinLimit) {
                mDevicesController.setObjectUMax(start += coarseStep);
            } else if (mPM130V1 > end * coarseMaxLimit) {
                mDevicesController.setObjectUMax(start -= coarseStep);
            }
            sleep(coarseSleep);
            changeTextOfView(mStatus, "Выводим значение для получения заданного значения грубо");
        }
        while (isExperimentStart() && ((mPM130V1 < end - fineLimit) || (mPM130V1 > end + fineLimit)) && mStartState) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mPM130V1);
            if (mPM130V1 < end - fineLimit) {
                mDevicesController.setObjectUMax(start += fineStep);
            } else if (mPM130V1 > end + fineLimit) {
                mDevicesController.setObjectUMax(start -= fineStep);
            }
            sleep(fineSleep);
            changeTextOfView(mStatus, "Выводим значение для получения заданного значения тонко");
        }
        return start;
    }

    private void pickUpState() {
        if (mPM130I1 < 45) {
            mDevicesController.on40To5();
            m40to5State = true;
            m200to5State = false;
            sleep(3000);
            if (mPM130I1 < 6) {
                mDevicesController.on5To5();
                m5to5State = true;
                m40to5State = false;
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
        return isBeckhoffResponding() && isVEHATResponding() && isFRA800ObjectResponding() && isPM130Responding() && isTRM201Responding();
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

    public boolean isVEHATResponding() {
        return mVEHATResponding;
    }

    public void setVEHATResponding(boolean VEHATResponding) {
        mVEHATResponding = VEHATResponding;
    }

    public void setV(float v) {
        mV = v;
        switch (mCurrentStage) {
            case 13:
                setV13(v);
                break;
            case 12:
                setV12(v);
                break;
            case 11:
                setV11(v);
                break;
            case 10:
                setV10(v);
                break;
            case 9:
                setV09(v);
                break;
            case 8:
                setV08(v);
                break;
            case 7:
                setV07(v);
                break;
            case 6:
                setV06(v);
                break;
            case 5:
                setV05(v);
                break;
        }
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
            case 13:
                setU13A(PM130V1);
                break;
            case 12:
                setU12A(PM130V1);
                break;
            case 11:
                setU11A(PM130V1);
                break;
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
            case 5:
                setU05A(PM130V1);
                break;
        }
    }

    public void setPM130V2(float PM130V2) {
        mPM130V2 = PM130V2;
        switch (mCurrentStage) {
            case 13:
                setU13B(PM130V2);
                break;
            case 12:
                setU12B(PM130V2);
                break;
            case 11:
                setU11B(PM130V2);
                break;
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
            case 5:
                setU05B(PM130V2);
                break;
        }
    }

    public void setPM130V3(float PM130V3) {
        mPM130V3 = PM130V3;
        switch (mCurrentStage) {
            case 13:
                setU13C(PM130V3);
                break;
            case 12:
                setU12C(PM130V3);
                break;
            case 11:
                setU11C(PM130V3);
                break;
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
            case 5:
                setU05C(PM130V3);
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
            case 13:
                setI13A(PM130I1);
                break;
            case 12:
                setI12A(PM130I1);
                break;
            case 11:
                setI11A(PM130I1);
                break;
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
            case 5:
                setI05A(PM130I1);
                break;
        }
    }

    public void setPM130I2(float PM130I2) {
        mPM130I2 = PM130I2;
        switch (mCurrentStage) {
            case 13:
                setI13B(PM130I2);
                break;
            case 12:
                setI12B(PM130I2);
                break;
            case 11:
                setI11B(PM130I2);
                break;
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
            case 5:
                setI05B(PM130I2);
                break;
        }
    }

    public void setPM130I3(float PM130I3) {
        mPM130I3 = PM130I3;
        switch (mCurrentStage) {
            case 13:
                setI13C(PM130I3);
                break;
            case 12:
                setI12C(PM130I3);
                break;
            case 11:
                setI11C(PM130I3);
                break;
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
            case 5:
                setI05C(PM130I3);
                break;
        }
    }

    public void setPM130P(float PM130P) {
        mPM130P = PM130P;
        switch (mCurrentStage) {
            case 13:
                setP13(PM130P);
                break;
            case 12:
                setP12(PM130P);
                break;
            case 11:
                setP11(PM130P);
                break;
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
            case 5:
                setP05(PM130P);
                break;
        }
    }

    public void setPM130Cos(float PM130Cos) {
        mPM130Cos = PM130Cos;
        switch (mCurrentStage) {
            case 13:
                setCos13(PM130Cos);
                break;
            case 12:
                setCos12(PM130Cos);
                break;
            case 11:
                setCos11(PM130Cos);
                break;
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
            case 5:
                setCos05(PM130Cos);
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
            case 13:
                setTemp13Ambient(tempAmbient);
                break;
            case 12:
                setTemp12Ambient(tempAmbient);
                break;
            case 11:
                setTemp11Ambient(tempAmbient);
                break;
            case 10:
                setTemp10Ambient(tempAmbient);
                break;
            case 9:
                setTemp09Ambient(tempAmbient);
                break;
            case 8:
                setTemp08Ambient(tempAmbient);
                break;
            case 7:
                setTemp07Ambient(tempAmbient);
                break;
            case 6:
                setTemp06Ambient(tempAmbient);
                break;
            case 5:
                setTemp05Ambient(tempAmbient);
                break;
        }
    }

    public void setTempEngine(float tempEngine) {
        mTempEngine = tempEngine;
        switch (mCurrentStage) {
            case 13:
                setTemp13Engine(tempEngine);
                break;
            case 12:
                setTemp12Engine(tempEngine);
                break;
            case 11:
                setTemp11Engine(tempEngine);
                break;
            case 10:
                setTemp10Engine(tempEngine);
                break;
            case 9:
                setTemp09Engine(tempEngine);
                break;
            case 8:
                setTemp08Engine(tempEngine);
                break;
            case 7:
                setTemp07Engine(tempEngine);
                break;
            case 6:
                setTemp06Engine(tempEngine);
                break;
            case 5:
                setTemp05Engine(tempEngine);
                break;
        }
    }

    public void setU13A(float u13A) {
        mU13A = u13A;
        changeTextOfView(mU13ACell, formatRealNumber(u13A));
    }

    public void setI13A(float i13A) {
        mI13A = i13A;
        changeTextOfView(mI13ACell, formatRealNumber(i13A));
    }

    public void setU13B(float u13B) {
        mU13B = u13B;
        changeTextOfView(mU13BCell, formatRealNumber(u13B));
    }

    public void setI13B(float i13B) {
        mI13B = i13B;
        changeTextOfView(mI13BCell, formatRealNumber(i13B));
    }

    public void setP13(float p13) {
        mP13 = p13;
        changeTextOfView(mP13Cell, formatRealNumber(p13));
    }

    public void setCos13(float cos13) {
        mCos13 = cos13;
        changeTextOfView(mCos13Cell, formatRealNumber(cos13));
    }

    public void setV13(float v13) {
        mV13 = v13;
        changeTextOfView(mV13Cell, formatRealNumber(v13));
    }

    public void setTemp13Ambient(float temp13Ambient) {
        mTemp13Ambient = temp13Ambient;
        changeTextOfView(mTemp13AmbientCell, formatRealNumber(temp13Ambient));
    }

    public void setTemp13Engine(float temp13Engine) {
        mTemp13Engine = temp13Engine;
        changeTextOfView(mTemp13EngineCell, formatRealNumber(temp13Engine));
    }

    public void setT13(float t13) {
        mT13 = t13;
    }

    public void setU13C(float u13C) {
        mU13C = u13C;
        changeTextOfView(mU13CCell, formatRealNumber(u13C));
        setU13Average((mU13A + mU13B + u13C) / 3f);
    }

    public void setU13Average(float U13Average) {
        mU13Average = U13Average;
        changeTextOfView(mU13AverageCell, formatRealNumber(U13Average));
    }

    public void setI13C(float i13C) {
        mI13C = i13C;
        changeTextOfView(mI13CCell, formatRealNumber(i13C));
        setI13Average((mI13A + mI13B + i13C) / 3f);
    }

    public void setI13Average(float I13Average) {
        mI13Average = I13Average;
        changeTextOfView(mI13AverageCell, formatRealNumber(I13Average));
    }

    public void setU12A(float u12A) {
        mU12A = u12A;
        changeTextOfView(mU12ACell, formatRealNumber(u12A));
    }

    public void setI12A(float i12A) {
        mI12A = i12A;
        changeTextOfView(mI12ACell, formatRealNumber(i12A));
    }

    public void setU12B(float u12B) {
        mU12B = u12B;
        changeTextOfView(mU12BCell, formatRealNumber(u12B));
    }

    public void setI12B(float i12B) {
        mI12B = i12B;
        changeTextOfView(mI12BCell, formatRealNumber(i12B));
    }

    public void setP12(float p12) {
        mP12 = p12;
        changeTextOfView(mP12Cell, formatRealNumber(p12));
    }

    public void setCos12(float cos12) {
        mCos12 = cos12;
        changeTextOfView(mCos12Cell, formatRealNumber(cos12));
    }

    public void setV12(float v12) {
        mV12 = v12;
        changeTextOfView(mV12Cell, formatRealNumber(v12));
    }

    public void setTemp12Ambient(float temp12Ambient) {
        mTemp12Ambient = temp12Ambient;
        changeTextOfView(mTemp12AmbientCell, formatRealNumber(temp12Ambient));
    }

    public void setTemp12Engine(float temp12Engine) {
        mTemp12Engine = temp12Engine;
        changeTextOfView(mTemp12EngineCell, formatRealNumber(temp12Engine));
    }

    public void setT12(float t12) {
        mT12 = t12;
    }

    public void setU12C(float u12C) {
        mU12C = u12C;
        changeTextOfView(mU12CCell, formatRealNumber(u12C));
        setU12Average((mU12A + mU12B + u12C) / 3f);
    }

    public void setU12Average(float U12Average) {
        mU12Average = U12Average;
        changeTextOfView(mU12AverageCell, formatRealNumber(U12Average));
    }

    public void setI12C(float i12C) {
        mI12C = i12C;
        changeTextOfView(mI12CCell, formatRealNumber(i12C));
        setI12Average((mI12A + mI12B + i12C) / 3f);
    }

    public void setI12Average(float I12Average) {
        mI12Average = I12Average;
        changeTextOfView(mI12AverageCell, formatRealNumber(I12Average));
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

    public void setP11(float p11) {
        mP11 = p11;
        changeTextOfView(mP11Cell, formatRealNumber(p11));
    }

    public void setCos11(float cos11) {
        mCos11 = cos11;
        changeTextOfView(mCos11Cell, formatRealNumber(cos11));
    }

    public void setV11(float v11) {
        mV11 = v11;
        changeTextOfView(mV11Cell, formatRealNumber(v11));
    }

    public void setTemp11Ambient(float temp11Ambient) {
        mTemp11Ambient = temp11Ambient;
        changeTextOfView(mTemp11AmbientCell, formatRealNumber(temp11Ambient));
    }

    public void setTemp11Engine(float temp11Engine) {
        mTemp11Engine = temp11Engine;
        changeTextOfView(mTemp11EngineCell, formatRealNumber(temp11Engine));
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

    public void setV10(float v10) {
        mV10 = v10;
        changeTextOfView(mV10Cell, formatRealNumber(v10));
    }

    public void setTemp10Ambient(float temp10Ambient) {
        mTemp10Ambient = temp10Ambient;
        changeTextOfView(mTemp10AmbientCell, formatRealNumber(temp10Ambient));
    }

    public void setTemp10Engine(float temp10Engine) {
        mTemp10Engine = temp10Engine;
        changeTextOfView(mTemp10EngineCell, formatRealNumber(temp10Engine));
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

    public void setV09(float v09) {
        mV09 = v09;
        changeTextOfView(mV09Cell, formatRealNumber(v09));
    }

    public void setTemp09Ambient(float temp09Ambient) {
        mTemp09Ambient = temp09Ambient;
        changeTextOfView(mTemp09AmbientCell, formatRealNumber(temp09Ambient));
    }

    public void setTemp09Engine(float temp09Engine) {
        mTemp09Engine = temp09Engine;
        changeTextOfView(mTemp09EngineCell, formatRealNumber(temp09Engine));
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

    public void setV08(float v08) {
        mV08 = v08;
        changeTextOfView(mV08Cell, formatRealNumber(v08));
    }

    public void setTemp08Ambient(float temp08Ambient) {
        mTemp08Ambient = temp08Ambient;
        changeTextOfView(mTemp08AmbientCell, formatRealNumber(temp08Ambient));
    }

    public void setTemp08Engine(float temp08Engine) {
        mTemp08Engine = temp08Engine;
        changeTextOfView(mTemp08EngineCell, formatRealNumber(temp08Engine));
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

    public void setV07(float v07) {
        mV07 = v07;
        changeTextOfView(mV07Cell, formatRealNumber(v07));
    }

    public void setTemp07Ambient(float temp07Ambient) {
        mTemp07Ambient = temp07Ambient;
        changeTextOfView(mTemp07AmbientCell, formatRealNumber(temp07Ambient));
    }

    public void setTemp07Engine(float temp07Engine) {
        mTemp07Engine = temp07Engine;
        changeTextOfView(mTemp07EngineCell, formatRealNumber(temp07Engine));
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

    public void setV06(float v06) {
        mV06 = v06;
        changeTextOfView(mV06Cell, formatRealNumber(v06));
    }

    public void setTemp06Ambient(float temp06Ambient) {
        mTemp06Ambient = temp06Ambient;
        changeTextOfView(mTemp06AmbientCell, formatRealNumber(temp06Ambient));
    }

    public void setTemp06Engine(float temp06Engine) {
        mTemp06Engine = temp06Engine;
        changeTextOfView(mTemp06EngineCell, formatRealNumber(temp06Engine));
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

    public void setU05A(float u05A) {
        mU05A = u05A;
        changeTextOfView(mU05ACell, formatRealNumber(u05A));
    }

    public void setI05A(float i05A) {
        mI05A = i05A;
        changeTextOfView(mI05ACell, formatRealNumber(i05A));
    }

    public void setU05B(float u05B) {
        mU05B = u05B;
        changeTextOfView(mU05BCell, formatRealNumber(u05B));
    }

    public void setI05B(float i05B) {
        mI05B = i05B;
        changeTextOfView(mI05BCell, formatRealNumber(i05B));
    }

    public void setP05(float p05) {
        mP05 = p05;
        changeTextOfView(mP05Cell, formatRealNumber(p05));
    }

    public void setCos05(float cos05) {
        mCos05 = cos05;
        changeTextOfView(mCos05Cell, formatRealNumber(cos05));
    }

    public void setV05(float v05) {
        mV05 = v05;
        changeTextOfView(mV05Cell, formatRealNumber(v05));
    }

    public void setTemp05Ambient(float temp05Ambient) {
        mTemp05Ambient = temp05Ambient;
        changeTextOfView(mTemp05AmbientCell, formatRealNumber(temp05Ambient));
    }

    public void setTemp05Engine(float temp05Engine) {
        mTemp05Engine = temp05Engine;
        changeTextOfView(mTemp05EngineCell, formatRealNumber(temp05Engine));
    }

    public void setT05(float t05) {
        mT05 = t05;
    }

    public void setU05C(float u05C) {
        mU05C = u05C;
        changeTextOfView(mU05CCell, formatRealNumber(u05C));
        setU05Average((mU05A + mU05B + u05C) / 3f);
    }

    public void setU05Average(float U05Average) {
        mU05Average = U05Average;
        changeTextOfView(mU05AverageCell, formatRealNumber(U05Average));
    }

    public void setI05C(float i05C) {
        mI05C = i05C;
        changeTextOfView(mI05CCell, formatRealNumber(i05C));
        setI05Average((mI05A + mI05B + i05C) / 3f);
    }

    public void setI05Average(float I05Average) {
        mI05Average = I05Average;
        changeTextOfView(mI05AverageCell, formatRealNumber(I05Average));
    }

    private void clearCells() {
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
        data.putExtra(MainActivity.INPUT_PARAMETER.I13_IDLE_R, mI13Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P13_IDLE_R, mP13);
        data.putExtra(MainActivity.INPUT_PARAMETER.I12_IDLE_R, mI12Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P12_IDLE_R, mP12);
        data.putExtra(MainActivity.INPUT_PARAMETER.I11_IDLE_R, mI11Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P11_IDLE_R, mP11);
        data.putExtra(MainActivity.INPUT_PARAMETER.I10_IDLE_R, mI10Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P10_IDLE_R, mP10);
        data.putExtra(MainActivity.INPUT_PARAMETER.I09_IDLE_R, mI09Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P09_IDLE_R, mP09);
        data.putExtra(MainActivity.INPUT_PARAMETER.I08_IDLE_R, mI08Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P08_IDLE_R, mP08);
        data.putExtra(MainActivity.INPUT_PARAMETER.I07_IDLE_R, mI07Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P07_IDLE_R, mP07);
        data.putExtra(MainActivity.INPUT_PARAMETER.U07_IDLE_R, mU07Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.I06_IDLE_R, mI06Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P06_IDLE_R, mP06);
        data.putExtra(MainActivity.INPUT_PARAMETER.U06_IDLE_R, mU06Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.I05_IDLE_R, mI05Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.P05_IDLE_R, mP05);
        data.putExtra(MainActivity.INPUT_PARAMETER.U05_IDLE_R, mU05Average);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE7U13A(mU13ACell.getText().toString());
        experiments.setE7I13A(mI13ACell.getText().toString());
        experiments.setE7U13B(mU13BCell.getText().toString());
        experiments.setE7I13B(mI13BCell.getText().toString());
        experiments.setE7P13(mP13Cell.getText().toString());
        experiments.setE7Cos13(mCos13Cell.getText().toString());
        experiments.setE7V13(mV13Cell.getText().toString());
        experiments.setE7Temp13Ambient(mTemp13AmbientCell.getText().toString());
        experiments.setE7Temp13Engine(mTemp13EngineCell.getText().toString());
        experiments.setE7T13(mT13Cell.getText().toString());
        experiments.setE7U13C(mU13CCell.getText().toString());
        experiments.setE7I13C(mI13CCell.getText().toString());
        experiments.setE7U13Average(mU13AverageCell.getText().toString());
        experiments.setE7I13Average(mI13AverageCell.getText().toString());
        experiments.setE7U12A(mU12ACell.getText().toString());
        experiments.setE7I12A(mI12ACell.getText().toString());
        experiments.setE7U12B(mU12BCell.getText().toString());
        experiments.setE7I12B(mI12BCell.getText().toString());
        experiments.setE7P12(mP12Cell.getText().toString());
        experiments.setE7Cos12(mCos12Cell.getText().toString());
        experiments.setE7V12(mV12Cell.getText().toString());
        experiments.setE7Temp12Ambient(mTemp12AmbientCell.getText().toString());
        experiments.setE7Temp12Engine(mTemp12EngineCell.getText().toString());
        experiments.setE7T12(mT12Cell.getText().toString());
        experiments.setE7U12C(mU12CCell.getText().toString());
        experiments.setE7I12C(mI12CCell.getText().toString());
        experiments.setE7U12Average(mU12AverageCell.getText().toString());
        experiments.setE7I12Average(mI12AverageCell.getText().toString());
        experiments.setE7U11A(mU11ACell.getText().toString());
        experiments.setE7I11A(mI11ACell.getText().toString());
        experiments.setE7U11B(mU11BCell.getText().toString());
        experiments.setE7I11B(mI11BCell.getText().toString());
        experiments.setE7P11(mP11Cell.getText().toString());
        experiments.setE7Cos11(mCos11Cell.getText().toString());
        experiments.setE7V11(mV11Cell.getText().toString());
        experiments.setE7Temp11Ambient(mTemp11AmbientCell.getText().toString());
        experiments.setE7Temp11Engine(mTemp11EngineCell.getText().toString());
        experiments.setE7T11(mT11Cell.getText().toString());
        experiments.setE7U11C(mU11CCell.getText().toString());
        experiments.setE7I11C(mI11CCell.getText().toString());
        experiments.setE7U11Average(mU11AverageCell.getText().toString());
        experiments.setE7I11Average(mI11AverageCell.getText().toString());
        experiments.setE7U10A(mU10ACell.getText().toString());
        experiments.setE7I10A(mI10ACell.getText().toString());
        experiments.setE7U10B(mU10BCell.getText().toString());
        experiments.setE7I10B(mI10BCell.getText().toString());
        experiments.setE7P10(mP10Cell.getText().toString());
        experiments.setE7Cos10(mCos10Cell.getText().toString());
        experiments.setE7V10(mV10Cell.getText().toString());
        experiments.setE7Temp10Ambient(mTemp10AmbientCell.getText().toString());
        experiments.setE7Temp10Engine(mTemp10EngineCell.getText().toString());
        experiments.setE7T10(mT10Cell.getText().toString());
        experiments.setE7U10C(mU10CCell.getText().toString());
        experiments.setE7I10C(mI10CCell.getText().toString());
        experiments.setE7U10Average(mU10AverageCell.getText().toString());
        experiments.setE7I10Average(mI10AverageCell.getText().toString());
        experiments.setE7U09A(mU09ACell.getText().toString());
        experiments.setE7I09A(mI09ACell.getText().toString());
        experiments.setE7U09B(mU09BCell.getText().toString());
        experiments.setE7I09B(mI09BCell.getText().toString());
        experiments.setE7P09(mP09Cell.getText().toString());
        experiments.setE7Cos09(mCos09Cell.getText().toString());
        experiments.setE7V09(mV09Cell.getText().toString());
        experiments.setE7Temp09Ambient(mTemp09AmbientCell.getText().toString());
        experiments.setE7Temp09Engine(mTemp09EngineCell.getText().toString());
        experiments.setE7T09(mT09Cell.getText().toString());
        experiments.setE7U09C(mU09CCell.getText().toString());
        experiments.setE7I09C(mI09CCell.getText().toString());
        experiments.setE7U09Average(mU09AverageCell.getText().toString());
        experiments.setE7I09Average(mI09AverageCell.getText().toString());
        experiments.setE7U08A(mU08ACell.getText().toString());
        experiments.setE7I08A(mI08ACell.getText().toString());
        experiments.setE7U08B(mU08BCell.getText().toString());
        experiments.setE7I08B(mI08BCell.getText().toString());
        experiments.setE7P08(mP08Cell.getText().toString());
        experiments.setE7Cos08(mCos08Cell.getText().toString());
        experiments.setE7V08(mV08Cell.getText().toString());
        experiments.setE7Temp08Ambient(mTemp08AmbientCell.getText().toString());
        experiments.setE7Temp08Engine(mTemp08EngineCell.getText().toString());
        experiments.setE7T08(mT08Cell.getText().toString());
        experiments.setE7U08C(mU08CCell.getText().toString());
        experiments.setE7I08C(mI08CCell.getText().toString());
        experiments.setE7U08Average(mU08AverageCell.getText().toString());
        experiments.setE7I08Average(mI08AverageCell.getText().toString());
        experiments.setE7U07A(mU07ACell.getText().toString());
        experiments.setE7I07A(mI07ACell.getText().toString());
        experiments.setE7U07B(mU07BCell.getText().toString());
        experiments.setE7I07B(mI07BCell.getText().toString());
        experiments.setE7P07(mP07Cell.getText().toString());
        experiments.setE7Cos07(mCos07Cell.getText().toString());
        experiments.setE7V07(mV07Cell.getText().toString());
        experiments.setE7Temp07Ambient(mTemp07AmbientCell.getText().toString());
        experiments.setE7Temp07Engine(mTemp07EngineCell.getText().toString());
        experiments.setE7T07(mT07Cell.getText().toString());
        experiments.setE7U07C(mU07CCell.getText().toString());
        experiments.setE7I07C(mI07CCell.getText().toString());
        experiments.setE7U07Average(mU07AverageCell.getText().toString());
        experiments.setE7I07Average(mI07AverageCell.getText().toString());
        experiments.setE7U06A(mU06ACell.getText().toString());
        experiments.setE7I06A(mI06ACell.getText().toString());
        experiments.setE7U06B(mU06BCell.getText().toString());
        experiments.setE7I06B(mI06BCell.getText().toString());
        experiments.setE7P06(mP06Cell.getText().toString());
        experiments.setE7Cos06(mCos06Cell.getText().toString());
        experiments.setE7V06(mV06Cell.getText().toString());
        experiments.setE7Temp06Ambient(mTemp06AmbientCell.getText().toString());
        experiments.setE7Temp06Engine(mTemp06EngineCell.getText().toString());
        experiments.setE7T06(mT06Cell.getText().toString());
        experiments.setE7U06C(mU06CCell.getText().toString());
        experiments.setE7I06C(mI06CCell.getText().toString());
        experiments.setE7U06Average(mU06AverageCell.getText().toString());
        experiments.setE7I06Average(mI06AverageCell.getText().toString());
        experiments.setE7U05A(mU05ACell.getText().toString());
        experiments.setE7I05A(mI05ACell.getText().toString());
        experiments.setE7U05B(mU05BCell.getText().toString());
        experiments.setE7I05B(mI05BCell.getText().toString());
        experiments.setE7P05(mP05Cell.getText().toString());
        experiments.setE7Cos05(mCos05Cell.getText().toString());
        experiments.setE7V05(mV05Cell.getText().toString());
        experiments.setE7Temp05Ambient(mTemp05AmbientCell.getText().toString());
        experiments.setE7Temp05Engine(mTemp05EngineCell.getText().toString());
        experiments.setE7T05(mT05Cell.getText().toString());
        experiments.setE7U05C(mU05CCell.getText().toString());
        experiments.setE7I05C(mI05CCell.getText().toString());
        experiments.setE7U05Average(mU05AverageCell.getText().toString());
        experiments.setE7I05Average(mI05AverageCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
