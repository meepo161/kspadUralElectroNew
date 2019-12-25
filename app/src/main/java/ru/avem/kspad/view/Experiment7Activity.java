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
import ru.avem.kspad.communication.devices.ikas.IKASModel;
import ru.avem.kspad.communication.devices.pm130.PM130Model;
import ru.avem.kspad.communication.devices.veha_t.VEHATModel;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;
import ru.avem.kspad.utils.Logger;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.DeviceController.IKAS_ID;
import static ru.avem.kspad.communication.devices.DeviceController.PM130_ID;
import static ru.avem.kspad.communication.devices.DeviceController.VEHA_T_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment7Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Определение токов и потерь холостого хода";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int STATE_40_TO_5_MULTIPLIER = 40 / 5;
    private static final int STATE_5_TO_5_MULTIPLIER = 5 / 5;

    @BindView(R.id.main_layout)
    ScrollView mMainLayout;
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
    @BindView(R.id.p_cop_1_3)
    TextView mPCop13Cell;
    @BindView(R.id.p_m_p_st_1_3)
    TextView mPmPst13Cell;
    @BindView(R.id.p_st_1_3)
    TextView mPst13Cell;
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
    @BindView(R.id.p_cop_1_2)
    TextView mPCop12Cell;
    @BindView(R.id.p_m_p_st_1_2)
    TextView mPmPst12Cell;
    @BindView(R.id.p_st_1_2)
    TextView mPst12Cell;
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
    @BindView(R.id.p_cop_1_1)
    TextView mPCop11Cell;
    @BindView(R.id.p_m_p_st_1_1)
    TextView mPmPst11Cell;
    @BindView(R.id.p_st_1_1)
    TextView mPst11Cell;
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
    @BindView(R.id.p_cop_1_0)
    TextView mPCop10Cell;
    @BindView(R.id.p_m_p_st_1_0)
    TextView mPmPst10Cell;
    @BindView(R.id.p_st_1_0)
    TextView mPst10Cell;
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
    @BindView(R.id.p_cop_0_9)
    TextView mPCop09Cell;
    @BindView(R.id.p_m_p_st_0_9)
    TextView mPmPst09Cell;
    @BindView(R.id.p_st_0_9)
    TextView mPst09Cell;
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
    @BindView(R.id.p_cop_0_8)
    TextView mPCop08Cell;
    @BindView(R.id.p_m_p_st_0_8)
    TextView mPmPst08Cell;
    @BindView(R.id.p_st_0_8)
    TextView mPst08Cell;
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
    @BindView(R.id.p_cop_0_7)
    TextView mPCop07Cell;
    @BindView(R.id.p_m_p_st_0_7)
    TextView mPmPst07Cell;
    @BindView(R.id.p_st_0_7)
    TextView mPst07Cell;
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
    @BindView(R.id.p_cop_0_6)
    TextView mPCop06Cell;
    @BindView(R.id.p_m_p_st_0_6)
    TextView mPmPst06Cell;
    @BindView(R.id.p_st_0_6)
    TextView mPst06Cell;
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
    @BindView(R.id.p_cop_0_5)
    TextView mPCop05Cell;
    @BindView(R.id.p_m_p_st_0_5)
    TextView mPmPst05Cell;
    @BindView(R.id.p_st_0_5)
    TextView mPst05Cell;
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

    @BindView(R.id.r)
    TextView mRCell;
    @BindView(R.id.p_mech)
    TextView mPMechCell;

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
    private float mSpecifiedU13;
    private float mSpecifiedU12;
    private float mSpecifiedU11;
    private float mSpecifiedU10;
    private float mSpecifiedU09;
    private float mSpecifiedU08;
    private float mSpecifiedU07;
    private float mSpecifiedU06;
    private float mSpecifiedU05;
    private int mSpecifiedRType;
    private float mSpecifiedAverageR;

    private int mSpecifiedT1;
    private int mSpecifiedT2;
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

    private boolean mIKASResponding;
    private float mIKASReady;
    private float mMeasurable;
    private float mR;

    private float mU13A;
    private float mI13A;
    private float mU13B;
    private float mI13B;
    private float mP13 = -1f;
    private float mCos13;
    private float mV13;
    private float mPCop13;
    private float mPmPst13;
    private double mPst13;
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
    private float mPCop12;
    private float mPmPst12;
    private double mPst12;
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
    private float mPCop11;
    private float mPmPst11;
    private double mPst11;
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
    private float mPCop10;
    private float mPmPst10;
    private double mPst10 = -1.;
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
    private float mPCop09;
    private float mPmPst09;
    private double mPst09;
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
    private float mPCop08;
    private float mPmPst08;
    private double mPst08;
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
    private float mPCop07;
    private float mPmPst07;
    private double mPst07;
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
    private float mPCop06;
    private float mPmPst06;
    private double mPst06;
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
    private float mPCop05;
    private float mPmPst05;
    private double mPst05;
    private float mT05;
    private float mU05C;
    private float mI05C;
    private float mU05Average = -1f;
    private float mI05Average = -1f;

    private double mPMech = -1.;

    private boolean mVEHATResponding;
    private float mV;

    private float mUTurn;


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
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_IDLE) != 0) {
                mNumOfStages = extras.getInt(MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_IDLE);
                if (mNumOfStages == 1) {
//                    setViewAndChildrenVisibility(mGraphPanel, View.GONE);
                } else if (mNumOfStages == 9) {
//                    setViewAndChildrenVisibility(mGraphPanel, View.VISIBLE);
                }
            } else {
                throw new NullPointerException("Не передано " + MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_IDLE);
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
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY) != 0) {
                mSpecifiedFrequency = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY);
                mIntSpecifiedFrequencyK100 = (int) (mSpecifiedFrequency * 100);
            } else {
                throw new NullPointerException("Не передано specifiedFrequency");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_R_TYPE) != 0) {
                mSpecifiedRType = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_R_TYPE);
            } else {
                throw new NullPointerException("Не передано specifiedRType");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_R) != 0) {
                mSpecifiedAverageR = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_R);
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

    private class ExperimentTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            setExperimentStart(true);
            mCurrentStage = 10;
            mUTurn = 0;
            setFRA800ObjectReady(false);
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setVEHATResponding(true);
            setFRA800ObjectResponding(true);
            setPM130Responding(true);
            setIKASResponding(true);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            changeTextOfView(mStatus, "Испытание началось");
            mDevicesController.initDevices7Group();
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(1000);
            }

            while (isExperimentStart() && !mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }

            changeTextOfView(mStatus, "Инициализация...");
            mDevicesController.initDevices7Group();
            while (isExperimentStart() && !isFirstDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, getNotRespondingFirstDevicesString("Нет связи с устройствами"));
                sleep(100);
            }
            mDevicesController.onKMsFrom4And7And13Group();
            m200to5State = true;
            sleep(500);
            mDevicesController.setObjectParams(100, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);

            mDevicesController.startObject();
            sleep(2000);
            while (isExperimentStart() && !mFRA800ObjectReady && mStartState && isFirstDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь выйдет к заданным характеристикам");
            }

            int lastLevel = regulation(10 * 10, 3 * 10, 3, mSpecifiedU10, 0.05, 1, 400, 1050);

            pickUpState();

            int experimentTime = mSpecifiedT1;
            while (isExperimentStart() && (experimentTime > 0) && mStartState && isFirstDevicesResponding()) {
                experimentTime--;
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время в ХХ. Осталось: " + experimentTime);
                setT("" + experimentTime);
            }

            if (mNumOfStages > 1) {
                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    sleep(5000);
                }

                mCurrentStage = 13;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU13);

                mCurrentStage = 12;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU12);

                mCurrentStage = 11;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU11);

                mCurrentStage = 10;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU10);

                mCurrentStage = 9;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU09);

                mCurrentStage = 8;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU08);

                mCurrentStage = 7;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU07);

                mCurrentStage = 6;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU06);

                mCurrentStage = 5;

                if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                    stateToBack();
                }

                lastLevel = startStage(lastLevel, mSpecifiedU05);

                mCurrentStage = 4;
            } else {
                mCurrentStage = 4;
            }
            Logger.withTag("IDLE").log("Начало опускания");
            if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
                for (int i = lastLevel; i > 0; i -= 40) {
                    mDevicesController.setObjectUMax(i);
                }
                mDevicesController.setObjectUMax(0);
            }
            Logger.withTag("IDLE").log("Конец опускания");

            mDevicesController.stopObject();
            Logger.withTag("IDLE").log("Остановили");
            sleep(5000);
            mDevicesController.offKMsFrom4And7And13Group();
            Logger.withTag("IDLE").log("Оффнули");
            m200to5State = false;
            m40to5State = false;
            m5to5State = false;

            if (!isFirstDevicesResponding()) {
                return 1;
            }

            if (mNumOfStages > 1) {
                Logger.withTag("IDLE").log("Начало ИКАСа");
                mDevicesController.diversifyDevices();
                if (isExperimentStart()) {
                    mDevicesController.initBeckhoff();
                    mDevicesController.initIKAS();
                }
                while (isExperimentStart() && !isSecondDevicesResponding()) {
                    changeTextOfView(mStatus, getNotRespondingSecondDevicesString("Нет связи с устройствами"));
                    sleep(100);
                }

                if (isExperimentStart() && mStartState && isSecondDevicesResponding()) {
                    changeTextOfView(mStatus, "Инициализация...");
                    Logger.withTag("IDLE").log("Сбор схемы ИКАСа");
                    mDevicesController.onKMsFrom5And17Group();
                }

                while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 1f) && (mIKASReady != 101f) && mStartState && isSecondDevicesResponding()) {
                    sleep(100);
                    changeTextOfView(mStatus, "Ожидаем, пока ИКАС подготовится");
                }

                if (isExperimentStart() && mStartState && isSecondDevicesResponding()) {
                    startMeasuring();
                    sleep(2000);
                }

                while (isExperimentStart() && (mIKASReady != 0f) && (mIKASReady != 101f) && mStartState && isSecondDevicesResponding()) {
                    sleep(100);
                    changeTextOfView(mStatus, "Ожидаем, пока измерение ИКАСа закончится");
                }

                if (isExperimentStart() && mStartState && isSecondDevicesResponding()) {
                    setR(mMeasurable);

                    setPCop13((float) (3 * mI13Average * mI13Average * (mR / 2.0) / 1000.));
                    setPCop12((float) (3 * mI12Average * mI12Average * (mR / 2.0) / 1000.));
                    setPCop11((float) (3 * mI11Average * mI11Average * (mR / 2.0) / 1000.));
                    setPCop10((float) (3 * mI10Average * mI10Average * (mR / 2.0) / 1000.));
                    setPCop09((float) (3 * mI09Average * mI09Average * (mR / 2.0) / 1000.));
                    setPCop08((float) (3 * mI08Average * mI08Average * (mR / 2.0) / 1000.));
                    setPCop07((float) (3 * mI07Average * mI07Average * (mR / 2.0) / 1000.));
                    setPCop06((float) (3 * mI06Average * mI06Average * (mR / 2.0) / 1000.));
                    setPCop05((float) (3 * mI05Average * mI05Average * (mR / 2.0) / 1000.));

                    setPmPst13(mP13 - mPCop13);
                    setPmPst12(mP12 - mPCop12);
                    setPmPst11(mP11 - mPCop11);
                    setPmPst10(mP10 - mPCop10);
                    setPmPst09(mP09 - mPCop09);
                    setPmPst08(mP08 - mPCop08);
                    setPmPst07(mP07 - mPCop07);
                    setPmPst06(mP06 - mPCop06);
                    setPmPst05(mP05 - mPCop05);

                    setPMech((mU05Average * mU05Average * mPmPst05 + mPmPst05 * mU06Average * mU06Average - mPmPst05 * mU05Average * mU05Average - mU05Average * mU05Average * mPmPst06) / (mU06Average * mU06Average - mU05Average * mU05Average));

                    setPst13((float) (mPmPst13 - mPMech));
                    setPst12((float) (mPmPst12 - mPMech));
                    setPst11((float) (mPmPst11 - mPMech));
                    setPst10(mPmPst10 - mPMech);
                    setPst09((float) (mPmPst09 - mPMech));
                    setPst08((float) (mPmPst08 - mPMech));
                    setPst07((float) (mPmPst07 - mPMech));
                    setPst06((float) (mPmPst06 - mPMech));
                    setPst05((float) (mPmPst05 - mPMech));
                }

                mDevicesController.offKMsFrom5And17Group();

                if (!isSecondDevicesResponding()) {
                    return 2;
                }
            }

            return 0;
        }

        private void startMeasuring() {
            if (mSpecifiedRType == 1) {
                mDevicesController.startMeasuringAB();
            } else if (mSpecifiedRType == 2) {
                mDevicesController.startMeasuringBC();
            } else {
                mDevicesController.startMeasuringAC();
            }
        }

        private String getNotRespondingFirstDevicesString(String mainText) {
            return String.format("%s %s%s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isFRA800ObjectResponding() ? "" : "ЧП ОИ, ",
                    isPM130Responding() ? "" : "PM130, ",
                    isVEHATResponding() ? "" : "ВЕХА-Т");
        }

        private String getNotRespondingSecondDevicesString(String mainText) {
            return String.format("%s %s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isIKASResponding() ? "" : "ИКАС");
        }

        @Override
        protected void onPostExecute(Integer result) {
            mDevicesController.diversifyDevices();
            mExperimentSwitch.setChecked(false);
            if (!mCause.equals("")) {
                mStatus.setText(String.format("Испытание прервано по причине: %s", mCause));
                mMainLayout.setBackgroundColor(getResources().getColor(R.color.red));
            } else if (result == 1) {
                changeTextOfView(mStatus, getNotRespondingFirstDevicesString("Потеряна связь с устройствами"));
                mMainLayout.setBackgroundColor(getResources().getColor(R.color.red));
            } else if (result == 2) {
                changeTextOfView(mStatus, getNotRespondingSecondDevicesString("Потеряна связь с устройствами"));
                mMainLayout.setBackgroundColor(getResources().getColor(R.color.red));
            } else {
                mStatus.setText("Испытание закончено");
            }
        }
    }

    private int startStage(int lastLevel, float u) {
        lastLevel = regulation(lastLevel, 3 * 10, 3, u, 0.05, 1, 400, 1050);

        if (isExperimentStart() && mStartState && isFirstDevicesResponding()) {
            pickUpState();
        }

        int experimentTime = mSpecifiedT2;
        while (isExperimentStart() && (experimentTime > 0) && mStartState && isFirstDevicesResponding()) {
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
        while (isExperimentStart() && ((mPM130V1 < end * coarseMinLimit) || (mPM130V1 > end * coarseMaxLimit)) && mStartState && isFirstDevicesResponding()) {
            Logger.withTag(Logger.DEBUG_TAG).log("end:" + end + " compared:" + mPM130V1);
            if (mPM130V1 < end * coarseMinLimit) {
                mDevicesController.setObjectUMax(start += coarseStep);
            } else if (mPM130V1 > end * coarseMaxLimit) {
                mDevicesController.setObjectUMax(start -= coarseStep);
            }
            sleep(coarseSleep);
            changeTextOfView(mStatus, "Выводим напряжение для получения заданного значения грубо");
        }
        while (isExperimentStart() && ((mPM130V1 < end - fineLimit) || (mPM130V1 > end + fineLimit)) && mStartState && isFirstDevicesResponding()) {
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

    private boolean isFirstDevicesResponding() {
        return isBeckhoffResponding() && isFRA800ObjectResponding() && isPM130Responding() && isVEHATResponding();
    }

    private boolean isSecondDevicesResponding() {
        return isBeckhoffResponding() && isIKASResponding();
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

    public boolean isVEHATResponding() {
        return mVEHATResponding;
    }

    public void setVEHATResponding(boolean VEHATResponding) {
        mVEHATResponding = VEHATResponding;
    }

    public void setV(float v) {
        if (mUTurn == 0 && v > 0) {
            mUTurn = mPM130V1;
//            changeTextOfView(mV13Cell, formatRealNumber(mUTurn));
        }
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

    public void setPCop13(float PCop13) {
        mPCop13 = PCop13;
        changeTextOfView(mPCop13Cell, formatRealNumber(PCop13));
    }

    public void setPmPst13(float pmPst13) {
        mPmPst13 = pmPst13;
        changeTextOfView(mPmPst13Cell, formatRealNumber(pmPst13));
    }

    public void setPst13(float pst13) {
        mPst13 = pst13;
        changeTextOfView(mPst13Cell, formatRealNumber(pst13));
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

    public void setPCop12(float PCop12) {
        mPCop12 = PCop12;
        changeTextOfView(mPCop12Cell, formatRealNumber(PCop12));
    }

    public void setPmPst12(float pmPst12) {
        mPmPst12 = pmPst12;
        changeTextOfView(mPmPst12Cell, formatRealNumber(pmPst12));
    }

    public void setPst12(float pst12) {
        mPst12 = pst12;
        changeTextOfView(mPst12Cell, formatRealNumber(pst12));
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

    public void setPCop11(float PCop11) {
        mPCop11 = PCop11;
        changeTextOfView(mPCop11Cell, formatRealNumber(PCop11));
    }

    public void setPmPst11(float pmPst11) {
        mPmPst11 = pmPst11;
        changeTextOfView(mPmPst11Cell, formatRealNumber(pmPst11));
    }

    public void setPst11(float pst11) {
        mPst11 = pst11;
        changeTextOfView(mPst11Cell, formatRealNumber(pst11));
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

    public void setPCop10(float PCop10) {
        mPCop10 = PCop10;
        changeTextOfView(mPCop10Cell, formatRealNumber(PCop10));
    }

    public void setPmPst10(float pmPst10) {
        mPmPst10 = pmPst10;
        changeTextOfView(mPmPst10Cell, formatRealNumber(pmPst10));
    }

    public void setPst10(double pst10) {
        mPst10 = pst10;
        changeTextOfView(mPst10Cell, formatRealNumber(pst10));
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

    public void setPCop09(float PCop09) {
        mPCop09 = PCop09;
        changeTextOfView(mPCop09Cell, formatRealNumber(PCop09));
    }

    public void setPmPst09(float pmPst09) {
        mPmPst09 = pmPst09;
        changeTextOfView(mPmPst09Cell, formatRealNumber(pmPst09));
    }

    public void setPst09(float pst09) {
        mPst09 = pst09;
        changeTextOfView(mPst09Cell, formatRealNumber(pst09));
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

    public void setPCop08(float PCop08) {
        mPCop08 = PCop08;
        changeTextOfView(mPCop08Cell, formatRealNumber(PCop08));
    }

    public void setPmPst08(float pmPst08) {
        mPmPst08 = pmPst08;
        changeTextOfView(mPmPst08Cell, formatRealNumber(pmPst08));
    }

    public void setPst08(float pst08) {
        mPst08 = pst08;
        changeTextOfView(mPst08Cell, formatRealNumber(pst08));
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

    public void setPCop07(float PCop07) {
        mPCop07 = PCop07;
        changeTextOfView(mPCop07Cell, formatRealNumber(PCop07));
    }

    public void setPmPst07(float pmPst07) {
        mPmPst07 = pmPst07;
        changeTextOfView(mPmPst07Cell, formatRealNumber(pmPst07));
    }

    public void setPst07(float pst07) {
        mPst07 = pst07;
        changeTextOfView(mPst07Cell, formatRealNumber(pst07));
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

    public void setPCop06(float PCop06) {
        mPCop06 = PCop06;
        changeTextOfView(mPCop06Cell, formatRealNumber(PCop06));
    }

    public void setPmPst06(float pmPst06) {
        mPmPst06 = pmPst06;
        changeTextOfView(mPmPst06Cell, formatRealNumber(pmPst06));
    }

    public void setPst06(float pst06) {
        mPst06 = pst06;
        changeTextOfView(mPst06Cell, formatRealNumber(pst06));
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

    public void setPCop05(float PCop05) {
        mPCop05 = PCop05;
        changeTextOfView(mPCop05Cell, formatRealNumber(PCop05));
    }

    public void setPmPst05(float pmPst05) {
        mPmPst05 = pmPst05;
        changeTextOfView(mPmPst05Cell, formatRealNumber(pmPst05));
    }

    public void setPst05(float pst05) {
        mPst05 = pst05;
        changeTextOfView(mPst05Cell, formatRealNumber(pst05));
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

    public void setR(float r) {
        mR = r;
        changeTextOfView(mRCell, formatRealNumber(r));
    }

    public void setPMech(double pMech) {
        mPMech = pMech;
        changeTextOfView(mPMechCell, formatRealNumber(pMech));
    }

    private void clearCells() {
        changeTextOfView(mU13ACell, "");
        changeTextOfView(mI13ACell, "");
        changeTextOfView(mU13BCell, "");
        changeTextOfView(mI13BCell, "");
        changeTextOfView(mP13Cell, "");
        changeTextOfView(mCos13Cell, "");
        changeTextOfView(mV13Cell, "");
        changeTextOfView(mPCop13Cell, "");
        changeTextOfView(mPmPst13Cell, "");
        changeTextOfView(mPst13Cell, "");
        changeTextOfView(mT13Cell, "");
        changeTextOfView(mU13CCell, "");
        changeTextOfView(mI13CCell, "");
        changeTextOfView(mU13AverageCell, "");
        changeTextOfView(mI13AverageCell, "");

        changeTextOfView(mU12ACell, "");
        changeTextOfView(mI12ACell, "");
        changeTextOfView(mU12BCell, "");
        changeTextOfView(mI12BCell, "");
        changeTextOfView(mP12Cell, "");
        changeTextOfView(mCos12Cell, "");
        changeTextOfView(mV12Cell, "");
        changeTextOfView(mPCop12Cell, "");
        changeTextOfView(mPmPst12Cell, "");
        changeTextOfView(mPst12Cell, "");
        changeTextOfView(mT12Cell, "");
        changeTextOfView(mU12CCell, "");
        changeTextOfView(mI12CCell, "");
        changeTextOfView(mU12AverageCell, "");
        changeTextOfView(mI12AverageCell, "");

        changeTextOfView(mU11ACell, "");
        changeTextOfView(mI11ACell, "");
        changeTextOfView(mU11BCell, "");
        changeTextOfView(mI11BCell, "");
        changeTextOfView(mP11Cell, "");
        changeTextOfView(mCos11Cell, "");
        changeTextOfView(mV11Cell, "");
        changeTextOfView(mPCop11Cell, "");
        changeTextOfView(mPmPst11Cell, "");
        changeTextOfView(mPst11Cell, "");
        changeTextOfView(mT11Cell, "");
        changeTextOfView(mU11CCell, "");
        changeTextOfView(mI11CCell, "");
        changeTextOfView(mU11AverageCell, "");
        changeTextOfView(mI11AverageCell, "");

        changeTextOfView(mU10ACell, "");
        changeTextOfView(mI10ACell, "");
        changeTextOfView(mU10BCell, "");
        changeTextOfView(mI10BCell, "");
        changeTextOfView(mP10Cell, "");
        changeTextOfView(mCos10Cell, "");
        changeTextOfView(mV10Cell, "");
        changeTextOfView(mPCop10Cell, "");
        changeTextOfView(mPmPst10Cell, "");
        changeTextOfView(mPst10Cell, "");
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
        changeTextOfView(mV09Cell, "");
        changeTextOfView(mPCop09Cell, "");
        changeTextOfView(mPmPst09Cell, "");
        changeTextOfView(mPst09Cell, "");
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
        changeTextOfView(mV08Cell, "");
        changeTextOfView(mPCop08Cell, "");
        changeTextOfView(mPmPst08Cell, "");
        changeTextOfView(mPst08Cell, "");
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
        changeTextOfView(mV07Cell, "");
        changeTextOfView(mPCop07Cell, "");
        changeTextOfView(mPmPst07Cell, "");
        changeTextOfView(mPst07Cell, "");
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
        changeTextOfView(mV06Cell, "");
        changeTextOfView(mPCop06Cell, "");
        changeTextOfView(mPmPst06Cell, "");
        changeTextOfView(mPst06Cell, "");
        changeTextOfView(mT06Cell, "");
        changeTextOfView(mU06CCell, "");
        changeTextOfView(mI06CCell, "");
        changeTextOfView(mU06AverageCell, "");
        changeTextOfView(mI06AverageCell, "");

        changeTextOfView(mU05ACell, "");
        changeTextOfView(mI05ACell, "");
        changeTextOfView(mU05BCell, "");
        changeTextOfView(mI05BCell, "");
        changeTextOfView(mP05Cell, "");
        changeTextOfView(mCos05Cell, "");
        changeTextOfView(mV05Cell, "");
        changeTextOfView(mPCop05Cell, "");
        changeTextOfView(mPmPst05Cell, "");
        changeTextOfView(mPst05Cell, "");
        changeTextOfView(mT05Cell, "");
        changeTextOfView(mU05CCell, "");
        changeTextOfView(mI05CCell, "");
        changeTextOfView(mU05AverageCell, "");
        changeTextOfView(mI05AverageCell, "");
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
        data.putExtra(MainActivity.INPUT_PARAMETER.P_ST_R, mPst10);
        data.putExtra(MainActivity.INPUT_PARAMETER.P_MECH_R, mPMech);
        data.putExtra(MainActivity.INPUT_PARAMETER.U_TURN_R, mUTurn);
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
        experiments.setE7PCop13(mPCop13Cell.getText().toString());
        experiments.setE7PmPst13(mPmPst13Cell.getText().toString());
        experiments.setE7Pst13(mPst13Cell.getText().toString());
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
        experiments.setE7PCop12(mPCop12Cell.getText().toString());
        experiments.setE7PmPst12(mPmPst12Cell.getText().toString());
        experiments.setE7Pst12(mPst12Cell.getText().toString());
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
        experiments.setE7PCop11(mPCop11Cell.getText().toString());
        experiments.setE7PmPst11(mPmPst11Cell.getText().toString());
        experiments.setE7Pst11(mPst11Cell.getText().toString());
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
        experiments.setE7PCop10(mPCop10Cell.getText().toString());
        experiments.setE7PmPst10(mPmPst10Cell.getText().toString());
        experiments.setE7Pst10(mPst10Cell.getText().toString());
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
        experiments.setE7PCop09(mPCop09Cell.getText().toString());
        experiments.setE7PmPst09(mPmPst09Cell.getText().toString());
        experiments.setE7Pst09(mPst09Cell.getText().toString());
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
        experiments.setE7PCop08(mPCop08Cell.getText().toString());
        experiments.setE7PmPst08(mPmPst08Cell.getText().toString());
        experiments.setE7Pst08(mPst08Cell.getText().toString());
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
        experiments.setE7PCop07(mPCop07Cell.getText().toString());
        experiments.setE7PmPst07(mPmPst07Cell.getText().toString());
        experiments.setE7Pst07(mPst07Cell.getText().toString());
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
        experiments.setE7PCop06(mPCop06Cell.getText().toString());
        experiments.setE7PmPst06(mPmPst06Cell.getText().toString());
        experiments.setE7Pst06(mPst06Cell.getText().toString());
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
        experiments.setE7PCop05(mPCop05Cell.getText().toString());
        experiments.setE7PmPst05(mPmPst05Cell.getText().toString());
        experiments.setE7Pst05(mPst05Cell.getText().toString());
        experiments.setE7T05(mT05Cell.getText().toString());
        experiments.setE7U05C(mU05CCell.getText().toString());
        experiments.setE7I05C(mI05CCell.getText().toString());
        experiments.setE7U05Average(mU05AverageCell.getText().toString());
        experiments.setE7I05Average(mI05AverageCell.getText().toString());
        experiments.setE7R(mRCell.getText().toString());
        experiments.setE7PMech(mPMechCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
