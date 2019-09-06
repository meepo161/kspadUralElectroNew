package ru.avem.kspad.view;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
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
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_GENERATOR_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.DeviceController.M40_ID;
import static ru.avem.kspad.communication.devices.DeviceController.PM130_ID;
import static ru.avem.kspad.communication.devices.DeviceController.TRM201_ID;
import static ru.avem.kspad.communication.devices.DeviceController.VEHA_T_ID;
import static ru.avem.kspad.utils.Utils.RU_LOCALE;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.getSyncV;
import static ru.avem.kspad.utils.Utils.setNextValueAndReturnAverage;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;
import static ru.avem.kspad.utils.Visibility.setViewAndChildrenVisibility;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.COS;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.I;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.M;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.NU;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.P1;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.P2;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.S;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.SK;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.TEMP_AMBIENT;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.TEMP_ENGINE;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.U;
import static ru.avem.kspad.view.Experiment1Activity.Characteristic.V;

public class Experiment1Activity extends AppCompatActivity implements Observer {
    //region Константы
    private static final String EXPERIMENT_NAME = "Определение рабочих характеристик";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int STATE_40_TO_5_MULTIPLIER = 40 / 5;
    private static final int STATE_5_TO_5_MULTIPLIER = 5 / 5;
    //endregion

    //region Виджеты
    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;
    @BindView(R.id.graph_panel)
    ConstraintLayout mGraphPanel;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;
    @BindView(R.id.check)
    Button mCheck;

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
    @BindView(R.id.graph)
    GraphView mGraph;
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
    private boolean mNeededToSave;

    private int mNumOfStages;
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
    private float mCurrentSpecifiedP2;
    private float mSpecifiedEff;
    private float mSpecifiedSk;
    private boolean mPlatformOneSelected;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mM40Responding;

    private float mM;
    private List<Float> mSeveralM = new ArrayList<>();
    private float mMAverage = -1;

    private boolean mVEHATResponding;

    private float mV;
    private List<Float> mSeveralV = new ArrayList<>();
    private float mVAverage = -1;

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

    private float mUAverage = -1;

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

    private float mIAverage = -1;

    private float mP1;
    private List<Float> mSeveralP1 = new ArrayList<>();
    private float mP1Average = -1;

    private float mS;
    private List<Float> mSeveralS = new ArrayList<>();
    private float mSAverage;

    private float mCos;
    private List<Float> mSeveralCos = new ArrayList<>();
    private float mCosAverage = -1;

    private double mP2;
    private double mP2Average = -1;
    private double mNu;
    private double mNuAverage = -1;
    private double mSk;
    private double mSkAverage = -1;

    private boolean mTRM201Responding;
    private float mTempAmbient;

    private float mTempEngine;
    private List<Float> mSeveralTempEngine = new ArrayList<>();
    private float mTempEngineAverage;

    double i = 7.5;
    private int mFCurGeneratorK100;
    private float mMDiff;

    @OnClick(R.id.check)
    public void onViewClicked() {
        i -= 0.33;
        mIAverageCharacteristics.add(i);
        mUAverageCharacteristics.add(i);
        mSCharacteristics.add(i);
        mP1Characteristics.add(i);
        mCosCharacteristics.add(i);
        mMCharacteristics.add(i);
        mVCharacteristics.add(i);
        mP2Characteristics.add(i);
        mNuCharacteristics.add(i);
        mTempAmbientCharacteristics.add(i);
        mTempEngineCharacteristics.add(i);
        mSkCharacteristics.add(i);

        changeSeriesAndLabel();
    }

    enum Characteristic {
        I,
        U,
        S,
        P1,
        COS,
        M,
        V,
        P2,
        NU,
        TEMP_AMBIENT,
        TEMP_ENGINE,
        SK
    }

    private List<DataPoint> mDataPoints;

    private Characteristic mX;
    private String mXLabel = "";
    private Characteristic mY;
    private String mYLabel = "";

    private LineGraphSeries<DataPoint> mCurrentSeries = new LineGraphSeries<>();

    private List<Double> mIAverageCharacteristics = new ArrayList<>();
    private List<Double> mUAverageCharacteristics = new ArrayList<>();
    private List<Double> mSCharacteristics = new ArrayList<>();
    private List<Double> mP1Characteristics = new ArrayList<>();
    private List<Double> mCosCharacteristics = new ArrayList<>();
    private List<Double> mMCharacteristics = new ArrayList<>();
    private List<Double> mVCharacteristics = new ArrayList<>();
    private List<Double> mP2Characteristics = new ArrayList<>();
    private List<Double> mNuCharacteristics = new ArrayList<>();
    private List<Double> mTempAmbientCharacteristics = new ArrayList<>();
    private List<Double> mTempEngineCharacteristics = new ArrayList<>();
    private List<Double> mSkCharacteristics = new ArrayList<>();
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment1);
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
                throw new NullPointerException(String.format(RU_LOCALE, "Не передано %s", MainActivity.OUTPUT_PARAMETER.EXPERIMENT_NAME));// TODO: 28.02.2018 везде
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_PERFORMANCE) != 0) {
                mNumOfStages = extras.getInt(MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_PERFORMANCE);
                if (mNumOfStages == 1) {
                    setViewAndChildrenVisibility(mGraphPanel, View.GONE);
                } else if (mNumOfStages == 7) {
                    setViewAndChildrenVisibility(mGraphPanel, View.VISIBLE);
                }
            } else {
                throw new NullPointerException("Не передано " + MainActivity.OUTPUT_PARAMETER.NUM_OF_STAGES_PERFORMANCE);
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

        mGraph.addSeries(mCurrentSeries);
        mGraph.getViewport().setXAxisBoundsManual(true);
//        mGraph.getViewport().setMinX(0);
//        mGraph.getViewport().setMaxX(100);
        mGraph.getViewport().setScalable(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        mDevicesController.setNeededToRunThreads(false);
    }

    @OnItemSelected(R.id.x_selector)
    public void onXSelected(Spinner view) {
        setXCharacteristicForDisplay((String) view.getSelectedItem());
    }

    private void setXCharacteristicForDisplay(String selectedItem) {
        switch (selectedItem) {
            case "I":
                mX = I;
                mXLabel = "А";
                break;
            case "U":
                mX = U;
                mXLabel = "В";
                break;
            case "S":
                mX = S;
                mXLabel = "кВА";
                break;
            case "P1":
                mX = P1;
                mXLabel = "кВт";
                break;
            case "cos φ":
                mX = COS;
                mXLabel = "";
                break;
            case "M":
                mX = M;
                mXLabel = "Н/м";
                break;
            case "V":
                mX = V;
                mXLabel = "об/мин";
                break;
            case "P2":
                mX = P2;
                mXLabel = "кВт";
                break;
            case "η":
                mX = NU;
                mXLabel = "";
                break;
            case "tокр.с.":
                mX = TEMP_AMBIENT;
                mXLabel = "°C";
                break;
            case "tдвиг.с.":
                mX = TEMP_ENGINE;
                mXLabel = "°C";
                break;
            case "s":
                mX = SK;
                mXLabel = "%";
                break;
        }

        changeSeriesAndLabel();
    }

    private void changeSeriesAndLabel() {
        mGraph.removeAllSeries();
        mCurrentSeries = new LineGraphSeries<>();
        mGraph.addSeries(mCurrentSeries);
        mDataPoints = new ArrayList<>();
        for (int i = 0; i < mSkCharacteristics.size(); i++) {
            double xValue = 0;
            switch (mX) {
                case I:
                    xValue = mIAverageCharacteristics.get(i);
                    break;
                case U:
                    xValue = mUAverageCharacteristics.get(i);
                    break;
                case S:
                    xValue = mSCharacteristics.get(i);
                    break;
                case P1:
                    xValue = mP1Characteristics.get(i);
                    break;
                case COS:
                    xValue = mCosCharacteristics.get(i);
                    break;
                case M:
                    xValue = mMCharacteristics.get(i);
                    break;
                case V:
                    xValue = mVCharacteristics.get(i);
                    break;
                case P2:
                    xValue = mP2Characteristics.get(i);
                    break;
                case NU:
                    xValue = mNuCharacteristics.get(i);
                    break;
                case TEMP_AMBIENT:
                    xValue = mTempAmbientCharacteristics.get(i);
                    break;
                case TEMP_ENGINE:
                    xValue = mTempEngineCharacteristics.get(i);
                    break;
                case SK:
                    xValue = mSkCharacteristics.get(i);
                    break;
            }

            double yValue = 0;
            switch (mY) {
                case I:
                    yValue = mIAverageCharacteristics.get(i);
                    break;
                case U:
                    yValue = mUAverageCharacteristics.get(i);
                    break;
                case S:
                    yValue = mSCharacteristics.get(i);
                    break;
                case P1:
                    yValue = mP1Characteristics.get(i);
                    break;
                case COS:
                    yValue = mCosCharacteristics.get(i);
                    break;
                case M:
                    yValue = mMCharacteristics.get(i);
                    break;
                case V:
                    yValue = mVCharacteristics.get(i);
                    break;
                case P2:
                    yValue = mP2Characteristics.get(i);
                    break;
                case NU:
                    yValue = mNuCharacteristics.get(i);
                    break;
                case TEMP_AMBIENT:
                    yValue = mTempAmbientCharacteristics.get(i);
                    break;
                case TEMP_ENGINE:
                    yValue = mTempEngineCharacteristics.get(i);
                    break;
                case SK:
                    yValue = mSkCharacteristics.get(i);
                    break;
            }
            mDataPoints.add(new DataPoint(xValue, yValue));
        }

        Collections.sort(mDataPoints, new Comparator<DataPoint>() {
            @Override
            public int compare(DataPoint a, DataPoint b) {
                if (a.getX() > b.getX()) {
                    return 1;
                } else if (a.getX() < b.getX()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        Logger.withTag("Point").log("New series");
        for (DataPoint dataPoint : mDataPoints) {
            Logger.withTag("Point").log(dataPoint.getX());
        }
        Logger.withTag("Point").log("End series");

        for (DataPoint dataPoint : mDataPoints) {
            mCurrentSeries.appendData(dataPoint, true, 7);
        }

        mGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return super.formatLabel(value, isValueX) + " " + mXLabel;
                } else {
                    return super.formatLabel(value, isValueX) + " " + mYLabel;
                }
            }
        });
    }

    @OnItemSelected(R.id.y_selector)
    public void onYSelected(Spinner view) {
        setYCharacteristicForDisplay((String) view.getSelectedItem());
    }

    private void setYCharacteristicForDisplay(String selectedItem) {
        switch (selectedItem) {
            case "I":
                mY = I;
                mYLabel = "А";
                break;
            case "U":
                mY = U;
                mYLabel = "В";
                break;
            case "S":
                mY = S;
                mYLabel = "кВА";
                break;
            case "P1":
                mY = P1;
                mYLabel = "кВт";
                break;
            case "cos φ":
                mY = COS;
                mYLabel = "";
                break;
            case "M":
                mY = M;
                mYLabel = "Н/м";
                break;
            case "V":
                mY = V;
                mYLabel = "об/мин";
                break;
            case "P2":
                mY = P2;
                mYLabel = "кВт";
                break;
            case "η":
                mY = NU;
                mYLabel = "";
                break;
            case "tокр.с.":
                mY = TEMP_AMBIENT;
                mYLabel = "°C";
                break;
            case "tдвиг.с.":
                mY = TEMP_ENGINE;
                mYLabel = "°C";
                break;
            case "s":
                mY = SK;
                mYLabel = "%";
                break;
        }

        changeSeriesAndLabel();
    }

    @OnCheckedChanged(R.id.experiment_switch)
    public void onExperimentCheckedChanged(CompoundButton compoundButton) {
        if (compoundButton.isChecked() && !mThreadOn) {
            initExperiment();
        } else {
            setExperimentStart(false);
        }
    }

    private void initExperiment() {
        if (mNumOfStages == 1) {
            new ExperimentTask1Stage().execute();
        } else if (mNumOfStages == 7) {
            new ExperimentTask7Stage().execute();
        }
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

    private class ExperimentTask1Stage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            setExperimentStart(true);
            setNeededToSave(true);
            mThreadOn = true;
            setFRA800ObjectReady(false);
            setFRA800GeneratorReady(false);
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setM40Responding(true);
            setVEHATResponding(true);
            setFRA800ObjectResponding(true);
            setFRA800GeneratorResponding(true);
            setPM130Responding(true);
            setTRM201Responding(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isExperimentStart()) {
                changeTextOfView(mStatus, "Испытание началось");
                mDevicesController.initBeckhoff();
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
                changeTextOfView(mStatus, getNotRespondingDevicesString("Нет связи с устройствами"));
                sleep(100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom1To3And10And12Group();
                m200to5State = true;
                sleep(500);
                mDevicesController.setObjectParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
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
            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.setObjectUMax(mSpecifiedUK10 + (int) (mSpecifiedUK10 - mUA * 10) + 15);
            }

            double f2 = mV * mZ1 * mSpecifiedFrequency / mV2 / mZ2;
            mFCurGeneratorK100 = (int) (f2 * 100);

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.setGeneratorParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, mFCurGeneratorK100);
                mDevicesController.startGenerator();
            }
            while (isExperimentStart() && !mFRA800GeneratorReady && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь генератора выйдет к заданным характеристикам");
            }

            int experimentTime = mExperimentTimeIdle;
            while (isExperimentStart() && (experimentTime-- > 0) && mStartState && isDevicesResponding()) {
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время обкатки на ХХ. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
            }
            changeTextOfView(mTCell, "");

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.onLoad();
            }
            sleep(500);
            int waits = 100;
            while (isExperimentStart() && (mM < 0) && (waits-- > 0) && mStartState && isDevicesResponding()) {
                sleep(50);
            }

            double limit = 0.05;
            while (isExperimentStart() &&
                    ((mP2 < mSpecifiedP2 * 0.8) || (mP2 > mSpecifiedP2 * 1.2)) && mStartState && isDevicesResponding()) {
                if (mP2 < mSpecifiedP2 * 0.8) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100 -= 5);
                } else if (mP2 > mSpecifiedP2 * 1.2) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100 += 5);
                }
                sleep(50);
                changeTextOfView(mStatus, "Выводим частоту генератора для получения номинального P2 * 0.8");
            }
            while (isExperimentStart() &&
                    ((mP2 < mSpecifiedP2 * (1 - limit)) || (mP2 > mSpecifiedP2 * (1 + limit))) && mStartState && isDevicesResponding()) {
                if (mP2 < mSpecifiedP2 * (1 - limit)) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100 -= 3);
                } else if (mP2 > mSpecifiedP2 * (1 + limit)) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100 += 3);
                }
                sleep(100);
                changeTextOfView(mStatus, "Выводим частоту генератора для получения номинального P2 грубо");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                pickUpState();
            }

            limit = 0.02;
            while (isExperimentStart() &&
                    ((mP2Average < mSpecifiedP2 * (1 - limit)) || (mP2Average > mSpecifiedP2 * (1 + limit))) && mStartState && isDevicesResponding()) {
                if (mP2Average < mSpecifiedP2 * (1 - limit)) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100--);
                } else if (mP2Average > mSpecifiedP2 * (1 + limit)) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100++);
                }
                sleep(500);
                changeTextOfView(mStatus, "Выводим частоту генератора для получения номинального P2 точно");
            }

            experimentTime = mExperimentTime;
            while (isExperimentStart() && (experimentTime-- > 0) && mStartState && isDevicesResponding()) {
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время под номинальной нагрузкой. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
                if (experimentTime > 20) {
                    if (mP2Average < mSpecifiedP2 * (1 - limit)) {
                        mDevicesController.setGeneratorFCur(mFCurGeneratorK100--);
                    } else if (mP2Average > mSpecifiedP2 * (1 + limit)) {
                        mDevicesController.setGeneratorFCur(mFCurGeneratorK100++);
                    }
                } else if (experimentTime < 5) {
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

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s%s%s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isM40Responding() ? "" : "Датчик момента, ",
                    isFRA800ObjectResponding() ? "" : "ЧП ОИ, ",
                    isFRA800GeneratorResponding() ? "" : "ЧП генератора, ",
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
            mThreadOn = false;
        }
    }

    private class ExperimentTask7Stage extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIAverageCharacteristics = new ArrayList<>();
            mUAverageCharacteristics = new ArrayList<>();
            mSCharacteristics = new ArrayList<>();
            mP1Characteristics = new ArrayList<>();
            mCosCharacteristics = new ArrayList<>();
            mMCharacteristics = new ArrayList<>();
            mVCharacteristics = new ArrayList<>();
            mP2Characteristics = new ArrayList<>();
            mNuCharacteristics = new ArrayList<>();
            mTempAmbientCharacteristics = new ArrayList<>();
            mTempEngineCharacteristics = new ArrayList<>();
            mSkCharacteristics = new ArrayList<>();
            clearCells();
            setExperimentStart(true);
            setNeededToSave(true);
            mThreadOn = true;
            setFRA800ObjectReady(false);
            setFRA800GeneratorReady(false);
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setM40Responding(true);
            setVEHATResponding(true);
            setFRA800ObjectResponding(true);
            setFRA800GeneratorResponding(true);
            setPM130Responding(true);
            setTRM201Responding(true);
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
            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.initDevicesFrom1To3And10And12Group();
            }
            while (isExperimentStart() && !isDevicesResponding() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Нет связи с устройствами");
                sleep(100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom1To3And10And12Group();
                m200to5State = true;
                sleep(500);
                mDevicesController.setObjectParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
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
            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.setObjectUMax(mSpecifiedUK10 + (int) (mSpecifiedUK10 - mUA * 10) + 15);
            }

            double f2 = mV * mZ1 * mSpecifiedFrequency / mV2 / mZ2;
            mFCurGeneratorK100 = (int) (f2 * 100);

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.setGeneratorParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, mFCurGeneratorK100);
                mDevicesController.startGenerator();
            }
            while (isExperimentStart() && !mFRA800GeneratorReady && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь генератора выйдет к заданным характеристикам");
            }

            int experimentTime = 10;
            while (isExperimentStart() && (experimentTime-- > 0) && mStartState && isDevicesResponding()) {
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время обкатки на ХХ. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
            }
            changeTextOfView(mTCell, "");

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.onLoad();
            }
            sleep(500);
            int waits = 100;
            while (isExperimentStart() && (mM < 0) && (waits-- > 0) && mStartState && isDevicesResponding()) {
                sleep(50);
            }

            mCurrentSpecifiedP2 = mSpecifiedP2 * 1.1f;

            double limit = 0.05;
            while (isExperimentStart() &&
                    ((mP2 < mCurrentSpecifiedP2 * 0.8) || (mP2 > mCurrentSpecifiedP2 * 1.2)) && mStartState && isDevicesResponding()) {
                if (mP2 < mCurrentSpecifiedP2 * 0.8) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100 -= 5);
                } else if (mP2 > mCurrentSpecifiedP2 * 1.2) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100 += 5);
                }
                sleep(50);
                changeTextOfView(mStatus, "Выводим частоту генератора для получения номинального P2 * 0.8");
            }
            while (isExperimentStart() &&
                    ((mP2 < mCurrentSpecifiedP2 * (1 - limit)) || (mP2 > mCurrentSpecifiedP2 * (1 + limit))) && mStartState && isDevicesResponding()) {
                if (mP2 < mCurrentSpecifiedP2 * (1 - limit)) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100 -= 3);
                } else if (mP2 > mCurrentSpecifiedP2 * (1 + limit)) {
                    mDevicesController.setGeneratorFCur(mFCurGeneratorK100 += 3);
                }
                sleep(100);
                changeTextOfView(mStatus, "Выводим частоту генератора для получения номинального P2 грубо");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                pickUpState();
            }

            mFCurGeneratorK100 = getNextCharacteristics(mFCurGeneratorK100);

            mCurrentSpecifiedP2 = mSpecifiedP2 * 1f;

            mFCurGeneratorK100 = getNextCharacteristics(mFCurGeneratorK100);

            mCurrentSpecifiedP2 = mSpecifiedP2 * 0.9f;

            mFCurGeneratorK100 = getNextCharacteristics(mFCurGeneratorK100);

            mCurrentSpecifiedP2 = mSpecifiedP2 * 0.8f;

            mFCurGeneratorK100 = getNextCharacteristics(mFCurGeneratorK100);

            mCurrentSpecifiedP2 = mSpecifiedP2 * 0.7f;

            mFCurGeneratorK100 = getNextCharacteristics(mFCurGeneratorK100);

            mCurrentSpecifiedP2 = mSpecifiedP2 * 0.6f;

            mFCurGeneratorK100 = getNextCharacteristics(mFCurGeneratorK100);

            mCurrentSpecifiedP2 = mSpecifiedP2 * 0.5f;

            getNextCharacteristics(mFCurGeneratorK100);

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

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s%s%s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isM40Responding() ? "" : "Датчик момента, ",
                    isFRA800ObjectResponding() ? "" : "ЧП ОИ, ",
                    isFRA800GeneratorResponding() ? "" : "ЧП генератора, ",
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
            mThreadOn = false;
        }
    }

    private int getNextCharacteristics(int fCurGenerator) {
        double limit;
        int experimentTime;
        limit = 0.01;
        while (isExperimentStart() &&
                ((mP2 < mCurrentSpecifiedP2 * (1 - limit)) || (mP2 > mCurrentSpecifiedP2 * (1 + limit))) && mStartState && isDevicesResponding()) {
            if (mP2 < mCurrentSpecifiedP2 * (1 - limit)) {
                fCurGenerator -= 4;
                mDevicesController.setGeneratorFCur(fCurGenerator);
            } else if (mP2 > mCurrentSpecifiedP2 * (1 + limit)) {
                fCurGenerator += 4;
                mDevicesController.setGeneratorFCur(fCurGenerator);
            }
            sleep(500);
            changeTextOfView(mStatus, "Выводим частоту генератора для получения номинального P2 точно");
        }

        experimentTime = 5;
        while (isExperimentStart() && (experimentTime-- > 0) && mStartState && isDevicesResponding()) {
            sleep(1000);
            changeTextOfView(mStatus, "Ждём заданное время под номинальной нагрузкой. Осталось: " + experimentTime);
            changeTextOfView(mTCell, "" + experimentTime);
        }

        if (isExperimentStart() && mStartState && isDevicesResponding()) {
            saveCharacteristics();
        }
        return fCurGenerator;
    }

    private void saveCharacteristics() {
        double IAverage = mIAverage;
        double UAverage = mUAverage;
        double S = mS;
        double P1 = mP1;
        double Cos = mCos;
        double M = mM;
        double V = mV;
        double P2 = mP2;
        double Nu = mNu;
        double TempAmbient = mTempAmbient;
        double TempEngine = mTempEngine;
        double Sk = mSk;

        mIAverageCharacteristics.add(IAverage);
        mUAverageCharacteristics.add(UAverage);
        mSCharacteristics.add(S);
        mP1Characteristics.add(P1);
        mCosCharacteristics.add(Cos);
        mMCharacteristics.add(M);
        mVCharacteristics.add(V);
        mP2Characteristics.add(P2);
        mNuCharacteristics.add(Nu);
        mTempAmbientCharacteristics.add(TempAmbient);
        mTempEngineCharacteristics.add(TempEngine);
        mSkCharacteristics.add(Sk);

        changeSeriesAndLabel();
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
        if (mMDiff == 0) {
            mMDiff = M;
        }
        mM = M - mMDiff;
        changeTextOfView(mMCell, formatRealNumber(mM));
        setMAverage(mM);
        recountP2();
    }

    public void setMAverage(float MAverage) {
        float averageM = setNextValueAndReturnAverage(mSeveralM, MAverage);
        if (averageM != -1) {
            mMAverage = averageM;
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
        float averageV = setNextValueAndReturnAverage(mSeveralV, VAverage);
        if (averageV != -1) {
            mVAverage = averageV;
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
        float averageUA = setNextValueAndReturnAverage(mSeveralUA, UAAverage);
        if (averageUA != -1) {
            mUAAverage = averageUA;
        }
    }

    public void setUB(float UB) {
        mUB = UB;
        changeTextOfView(mUBCell, formatRealNumber(UB));
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
        changeTextOfView(mUCCell, formatRealNumber(UC));
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
        float averageIA = setNextValueAndReturnAverage(mSeveralIA, IAAverage);
        if (averageIA != -1) {
            mIAAverage = averageIA;
        }
    }

    public void setIB(float IB) {
        mIB = IB;
        changeTextOfView(mIBCell, formatRealNumber(IB));
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
        changeTextOfView(mICCell, formatRealNumber(IC));
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
        changeTextOfView(mIAverageCell, formatRealNumber(mIAverage));
    }

    public void setP1(float P1) {
        mP1 = P1;
        changeTextOfView(mP1Cell, formatRealNumber(P1));
        setP1Average(P1);
    }

    public void setP1Average(float P1Average) {
        float averageP1 = setNextValueAndReturnAverage(mSeveralP1, P1Average);
        if (averageP1 != -1) {
            mP1Average = averageP1;
            changeTextOfView(mP1AverageCell, formatRealNumber(mP1Average));
        }
    }

    public void setS(float S) {
        mS = S;
        changeTextOfView(mSCell, formatRealNumber(S));
        setSAverage(S);
    }

    public void setSAverage(float SAverage) {
        float averageS = setNextValueAndReturnAverage(mSeveralS, SAverage);
        if (averageS != -1) {
            mSAverage = averageS;
            changeTextOfView(mSAverageCell, formatRealNumber(mSAverage));
        }
    }

    public void setCos(float cos) {
        mCos = cos;
        changeTextOfView(mCosCell, formatRealNumber(cos));
        setCosAverage(cos);
    }

    public void setCosAverage(float CosAverage) {
        float averageCos = setNextValueAndReturnAverage(mSeveralCos, CosAverage);
        if (averageCos != -1) {
            mCosAverage = averageCos;
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
        float averageTempEngine = setNextValueAndReturnAverage(mSeveralTempEngine, tempEngineAverage);
        if (averageTempEngine != -1) {
            mTempEngineAverage = averageTempEngine;
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
        returnValues();
        fillExperimentTable();
        finish();
    }

    private void returnValues() {
        Intent data = new Intent();
        data.putExtra(MainActivity.INPUT_PARAMETER.P2_R, (float) mP2Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.U_R, mUAverage);
        data.putExtra(MainActivity.INPUT_PARAMETER.I_R, mIAverage);
        data.putExtra(MainActivity.INPUT_PARAMETER.V_R, mVAverage);
        data.putExtra(MainActivity.INPUT_PARAMETER.S_R, (float) mSkAverage);
        data.putExtra(MainActivity.INPUT_PARAMETER.NU_R, (float) mNuAverage);
        data.putExtra(MainActivity.INPUT_PARAMETER.COS_R, mCosAverage);
        data.putExtra(MainActivity.INPUT_PARAMETER.P1_R, mP1Average);
        data.putExtra(MainActivity.INPUT_PARAMETER.M_R, mMAverage);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE1IA(mIACell.getText().toString());
        experiments.setE1UA(mUACell.getText().toString());
        experiments.setE1IB(mIBCell.getText().toString());
        experiments.setE1UB(mUBCell.getText().toString());
        experiments.setE1S(mSCell.getText().toString());
        experiments.setE1P1(mP1Cell.getText().toString());
        experiments.setE1Cos(mCosCell.getText().toString());
        experiments.setE1M(mMCell.getText().toString());
        experiments.setE1V(mVCell.getText().toString());
        experiments.setE1P2(mP2Cell.getText().toString());
        experiments.setE1Nu(mNuCell.getText().toString());
        experiments.setE1TempAmbient(mTempAmbientCell.getText().toString());
        experiments.setE1TempEngine(mTempEngineCell.getText().toString());
        experiments.setE1Sk(mSkCell.getText().toString());
        experiments.setE1T(mTCell.getText().toString());
        experiments.setE1IC(mICCell.getText().toString());
        experiments.setE1UC(mUCCell.getText().toString());
        experiments.setE1IAverage(mIAverageCell.getText().toString());
        experiments.setE1UAverage(mUAverageCell.getText().toString());
        experiments.setE1SAverage(mSAverageCell.getText().toString());
        experiments.setE1P1Average(mP1AverageCell.getText().toString());
        experiments.setE1CosAverage(mCosAverageCell.getText().toString());
        experiments.setE1MAverage(mMAverageCell.getText().toString());
        experiments.setE1VAverage(mVAverageCell.getText().toString());
        experiments.setE1P2Average(mP2AverageCell.getText().toString());
        experiments.setE1NuAverage(mNuAverageCell.getText().toString());
        experiments.setE1TempEngineAverage(mTempEngineAverageCell.getText().toString());
        experiments.setE1SkAverage(mSkAverageCell.getText().toString());
        experiments.setE1ISpecified(mISpecifiedCell.getText().toString());
        experiments.setE1USpecified(mUSpecifiedCell.getText().toString());
        experiments.setE1SSpecified(mSSpecifiedCell.getText().toString());
        experiments.setE1P1Specified(mP1SpecifiedCell.getText().toString());
        experiments.setE1CosSpecified(mCosSpecifiedCell.getText().toString());
        experiments.setE1MSpecified(mMSpecifiedCell.getText().toString());
        experiments.setE1VSpecified(mVSpecifiedCell.getText().toString());
        experiments.setE1P2Specified(mP2SpecifiedCell.getText().toString());
        experiments.setE1NuSpecified(mNuSpecifiedCell.getText().toString());
        experiments.setE1TempEngineSpecified(mTempEngineSpecifiedCell.getText().toString());
        experiments.setE1SkSpecified(mSkSpecifiedCell.getText().toString());
        experiments.setE1TSpecified(mTSpecifiedCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
