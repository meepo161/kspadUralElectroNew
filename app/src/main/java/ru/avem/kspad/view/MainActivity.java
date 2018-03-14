package ru.avem.kspad.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.joaquimley.faboptions.FabOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import io.realm.Realm;
import ru.avem.kspad.R;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.database.model.Protocol;
import ru.avem.kspad.database.model.Subject;
import ru.avem.kspad.logging.Logging;
import ru.avem.kspad.model.EventsHolder;
import ru.avem.kspad.model.ExperimentsHolder;
import ru.avem.kspad.model.MainModel;
import ru.avem.kspad.presenter.ControlPanelView;
import ru.avem.kspad.presenter.MainPresenter;
import ru.avem.kspad.presenter.MainPresenterView;

import static android.text.TextUtils.isEmpty;
import static ru.avem.kspad.utils.Utils.setListViewAdapterFromResources;
import static ru.avem.kspad.utils.Utils.setSpinnerAdapter;
import static ru.avem.kspad.utils.Utils.setSpinnerAdapterFromResources;
import static ru.avem.kspad.utils.Visibility.addTabToTabHost;
import static ru.avem.kspad.utils.Visibility.disableView;
import static ru.avem.kspad.utils.Visibility.enableView;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;
import static ru.avem.kspad.utils.Visibility.setViewAndChildrenVisibility;
import static ru.avem.kspad.utils.Visibility.switchTabState;

public class MainActivity extends AppCompatActivity implements MainPresenterView, ControlPanelView {
    //region Статические
    //region Константы вкладок
    private static final String SUBJECT_TAB_TAG = "Subject";
    private static final int SUBJECT_VIEW_ID = R.id.tab_subject;
    private static final String SUBJECT_TAB_LABEL = "Объект";
    private static final int SUBJECT_TAB_INDEX = 0;

    private static final String EXPERIMENTS_TAB_TAG = "Experiments";
    private static final int EXPERIMENTS_VIEW_ID = R.id.tab_experiments;
    private static final String EXPERIMENTS_TAB_LABEL = "Испытания";
    private static final int EXPERIMENTS_TAB_INDEX = 1;

    private static final String RESULTS_TAB_TAG = "Results";
    private static final int RESULTS_VIEW_ID = R.id.tab_results;
    private static final String RESULTS_TAB_LABEL = "Результаты";
    private static final int RESULTS_TAB_INDEX = 2;

    private static final String PROTOCOL_TAB_TAG = "Protocol";
    private static final int PROTOCOL_VIEW_ID = R.id.tab_protocol;
    private static final String PROTOCOL_TAB_LABEL = "Протокол";
    private static final int PROTOCOL_TAB_INDEX = 3;
    //endregion

    //region Константы доступа устройств
    public static final String ACTION_USB_PERMISSION =
            "ru.avem.kspad.USB_PERMISSION";
    public static final String ACTION_INIT_USB_PERMISSION =
            "ru.avem.kspad.INIT_USB_PERMISSION";
    private static final String ACTION_USB_ATTACHED =
            "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String RS485_DEVICE_NAME = "CP2103 USB to RS-485";
    private static final String MEGGER_DEVICE_NAME = "CP2103 USB to Megger";
    //endregion

    //region Константы испытаний
    public static final String EXPERIMENT_1 = "Определение рабочих характеристик";
    public static final String EXPERIMENT_2 = "Опыт перегрузки по моменту";
    public static final String EXPERIMENT_3 = "Опыт перегрузки по току";
    public static final String EXPERIMENT_4 = "Опыт проверки межвитковой изоляции";
    public static final String EXPERIMENT_5 = "Определение сопротивления обмоток при постоянном токе в практически холодном состоянии";
    public static final String EXPERIMENT_6 = "Опыт ВИУ";
    public static final String EXPERIMENT_7 = "Определение токов и потерь холостого хода";
    public static final String EXPERIMENT_8 = "Испытание при повышенной частоте вращения";
    public static final String EXPERIMENT_9 = "Определение токов и потерь короткого замыкания";
    public static final String EXPERIMENT_10 = "Опыт снятия максимального момента";
    public static final String EXPERIMENT_11 = "Определение сопротивления изоляции обмоток относительно корпуса машины и между обмотками";
    public static final String EXPERIMENT_12 = "Испытание на нагревание";
    public static final String EXPERIMENT_13 = "Проверка работоспособности при изменении напряжения и частоты питающей сети";
    public static final String EXPERIMENT_14 = "Определение минимального момента";
    public static final String EXPERIMENT_15 = "Определение пускового момента и тока";
    public static final String EXPERIMENT_16 = "Определение уровня шума и вибрации";
    public static final String EXPERIMENT_17 = "Определение сопротивления обмоток при постоянном токе в горячем состоянии";

    public static final int EXPERIMENT_1_ID = 1;
    public static final int EXPERIMENT_2_ID = 2;
    public static final int EXPERIMENT_3_ID = 3;
    public static final int EXPERIMENT_4_ID = 4;
    public static final int EXPERIMENT_5_ID = 5;
    public static final int EXPERIMENT_6_ID = 6;
    public static final int EXPERIMENT_7_ID = 7;
    public static final int EXPERIMENT_8_ID = 8;
    public static final int EXPERIMENT_9_ID = 9;
    public static final int EXPERIMENT_10_ID = 10;
    public static final int EXPERIMENT_11_ID = 11;
    public static final int EXPERIMENT_12_ID = 12;
    public static final int EXPERIMENT_13_ID = 13;
    public static final int EXPERIMENT_14_ID = 14;
    public static final int EXPERIMENT_15_ID = 15;
    public static final int EXPERIMENT_16_ID = 16;
    public static final int EXPERIMENT_17_ID = 17;
    //endregion

    private static final int DEFAULT_VALUE_INTEGER = -1;
    private static final float DEFAULT_VALUE_FLOAT = -1f;
    private static final double DEFAULT_VALUE_DOUBLE = -1.;

    //region Константы параметров
    public static class OUTPUT_PARAMETER {
        public static final String EXPERIMENT_NAME = "experimentName";
        public static final String NUM_OF_STAGES_PERFORMANCE = "numOfStagesPerformance";
        public static final String NUM_OF_STAGES_IDLE = "numOfStagesIdle";
        public static final String NUM_OF_STAGES_SC = "numOfStagesSc";
        public static final String Z1 = "Z1";
        public static final String Z2 = "Z2";
        public static final String V1 = "V1";
        public static final String SPECIFIED_TORQUE = "specifiedTorque";
        public static final String EXPERIMENT_TIME_IDLE = "experimentTimeIdle";
        public static final String EXPERIMENT_TIME = "experimentTime";
        public static final String K_OVERLOAD_I = "kOverloadI";
        public static final String SPECIFIED_AMPERAGE = "specifiedAmperage";
        public static final String SPECIFIED_FREQUENCY = "specifiedFrequency";
        public static final String SPECIFIED_U = "specifiedU";
        public static final String SPECIFIED_I = "specifiedI";
        public static final String SPECIFIED_P2 = "specifiedP2";
        public static final String SPECIFIED_EFF = "specifiedEff";
        public static final String SPECIFIED_SK = "specifiedSk";
        public static final String PLATFORM_ONE_SELECTED = "platformOneSelected";
        public static final String SPECIFIED_R = "specifiedR";
        public static final String SPECIFIED_R_TYPE = "specifiedRType";
        public static final String SPECIFIED_T1 = "specifiedT1";
        public static final String SPECIFIED_T2 = "specifiedT2";
        public static final String SPECIFIED_T = "specifiedT";
        public static final String SPECIFIED_TEMP_HEATING = "specifiedTempHeating";
    }

    static class INPUT_PARAMETER {
        static final String P2_R = "P2R";
        static final String U_R = "UR";
        static final String I_R = "IR";
        static final String V_R = "VR";
        static final String S_R = "SR";
        static final String NU_R = "NuR";
        static final String COS_R = "CosR";
        static final String P1_R = "P1R";
        static final String M_R = "MR";
        static final String M_MAX_R = "MMaxR";
        static final String M_MIN_R = "MMinR";
        static final String M_START_R = "MStartR";
        static final String I_START_R = "IStartR";

        static final String I13_IDLE_R = "I13IdleR";
        static final String P13_IDLE_R = "P13IdleR";
        static final String I12_IDLE_R = "I12IdleR";
        static final String P12_IDLE_R = "P12IdleR";
        static final String I11_IDLE_R = "I11IdleR";
        static final String P11_IDLE_R = "P11IdleR";
        static final String I10_IDLE_R = "I10IdleR";
        static final String P10_IDLE_R = "P10IdleR";
        static final String I09_IDLE_R = "I09IdleR";
        static final String P09_IDLE_R = "P09IdleR";
        static final String I08_IDLE_R = "I08IdleR";
        static final String P08_IDLE_R = "P08IdleR";
        static final String I07_IDLE_R = "I07IdleR";
        static final String P07_IDLE_R = "P07IdleR";
        static final String U07_IDLE_R = "U07IdleR";
        static final String I06_IDLE_R = "I06IdleR";
        static final String P06_IDLE_R = "P06IdleR";
        static final String U06_IDLE_R = "U06IdleR";
        static final String I05_IDLE_R = "I05IdleR";
        static final String P05_IDLE_R = "P05IdleR";
        static final String U05_IDLE_R = "U05IdleR";
        static final String P_ST_R = "PStR";
        static final String P_MECH_R = "PMechR";

        static final String I10_SC_R = "I10SCR";
        static final String P10_SC_R = "P10SCR";
        static final String I09_SC_R = "I09SCR";
        static final String P09_SC_R = "P09SCR";
        static final String I08_SC_R = "I08SCR";
        static final String P08_SC_R = "P08SCR";
        static final String I07_SC_R = "I07SCR";
        static final String P07_SC_R = "P07SCR";
        static final String I06_SC_R = "I06SCR";
        static final String P06_SC_R = "P06SCR";

        static final String TEMP_ENGINE_R = "TempEngineR";
        static final String TEMP_AMBIENT_R = "TempAmbientR";

        static final String IKAS_R_COLD_R = "IkasRColdR";
        static final String IKAS_R_20_R = "IkasR20R";
        static final String IKAS_R_TYPE_R = "IkasRTypeR";
        static final String IKAS_R_HOT_R = "IkasRHotR";

        static final String MGR_R = "MgrR";

        static final String I_MVZ1_R = "I1MVZR";
        static final String I_MVZ2_R = "I2MVZR";
        static final String I_MVZ3_R = "I3MVZR";

        static final String U_VIU_R = "UViuR";
        static final String T_VIU_R = "TViuR";

        static final String V_OVERLOAD_R = "VOverloadR";
        static final String T_OVERLOAD_R = "TOverloadR";
        static final String SPECIFIED_I_OVERLOAD_R = "SpecifiedIOverloadR";
        static final String I_OVERLOAD_R = "IOverloadR";
    }
    //endregion
    //endregion

    //region Виджеты

    //region Заголовочные
    @BindView(R.id.exit)
    TextView mExit;
    @BindView(R.id.main_title)
    TextView mMainTitle;
    @BindView(R.id.subject_title)
    TextView mSubjectTitle;
    @BindView(R.id.serial_number_title)
    TextView mSerialNumberTitle;
    //endregion

    //region Управление вкладками
    @BindView(R.id.tab_host)
    TabHost mTabHost;
    @BindView(android.R.id.tabs)
    TabWidget mTabs;
    //endregion

    //region Вкладка 1 Выбор объекта
    @BindView(R.id.serial_number)
    EditText mSerialNumber;
    @BindView(R.id.serial_number_enter)
    ToggleButton mSerialNumberEnter;
    @BindView(R.id.subjects_selector_title)
    TextView mSubjectsSelectorTitle;
    @BindView(R.id.subjects_selector)
    Spinner mSubjectsSelector;
    @BindView(R.id.platforms_selector_title)
    TextView mPlatformsSelectorTitle;
    @BindView(R.id.platforms_selector)
    Spinner mPlatformsSelector;


    @BindView(R.id.subject_enter)
    Button mSubjectEnter;
    @BindView(R.id.subject_cancel)
    Button mSubjectCancel;
    @BindView(R.id.subject_next)
    Button mSubjectNext;
    //endregion

    //region Вкладка 2 Испытания
    @BindView(R.id.tab_experiments)
    LinearLayout mTabExperiments;
    @BindView(R.id.experiments_list)
    ListView mExperimentsList;
    //endregion

    //region Вкладка 3 Результаты
    @BindView(R.id.experiments_selector)
    Spinner mExperimentsSelector;
    @BindView(R.id.experiment1)
    FrameLayout mExperiment1;
    @BindView(R.id.experiment2)
    FrameLayout mExperiment2;
    @BindView(R.id.experiment3)
    FrameLayout mExperiment3;
    @BindView(R.id.experiment4)
    FrameLayout mExperiment4;
    @BindView(R.id.experiment5)
    FrameLayout mExperiment5;
    @BindView(R.id.experiment6)
    FrameLayout mExperiment6;
    @BindView(R.id.experiment7)
    FrameLayout mExperiment7;
    @BindView(R.id.experiment8)
    FrameLayout mExperiment8;
    @BindView(R.id.experiment9)
    FrameLayout mExperiment9;
    @BindView(R.id.experiment10)
    FrameLayout mExperiment10;
    @BindView(R.id.experiment11)
    FrameLayout mExperiment11;
    @BindView(R.id.experiment12)
    FrameLayout mExperiment12;
    @BindView(R.id.experiment13)
    FrameLayout mExperiment13;
    @BindView(R.id.experiment14)
    FrameLayout mExperiment14;
    @BindView(R.id.experiment15)
    FrameLayout mExperiment15;
    @BindView(R.id.experiment16)
    FrameLayout mExperiment16;
    @BindView(R.id.experiment17)
    FrameLayout mExperiment17;

    @BindView(R.id.e1_i_a)
    TextView mE1IA;
    @BindView(R.id.e1_u_a)
    TextView mE1UA;
    @BindView(R.id.e1_i_b)
    TextView mE1IB;
    @BindView(R.id.e1_u_b)
    TextView mE1UB;
    @BindView(R.id.e1_s)
    TextView mE1S;
    @BindView(R.id.e1_p1)
    TextView mE1P1;
    @BindView(R.id.e1_cos)
    TextView mE1Cos;
    @BindView(R.id.e1_m)
    TextView mE1M;
    @BindView(R.id.e1_v)
    TextView mE1V;
    @BindView(R.id.e1_p2)
    TextView mE1P2;
    @BindView(R.id.e1_nu)
    TextView mE1Nu;
    @BindView(R.id.e1_temp_ambient)
    TextView mE1TempAmbient;
    @BindView(R.id.e1_temp_engine)
    TextView mE1TempEngine;
    @BindView(R.id.e1_sk)
    TextView mE1Sk;
    @BindView(R.id.e1_t)
    TextView mE1T;
    @BindView(R.id.e1_i_c)
    TextView mE1IC;
    @BindView(R.id.e1_u_c)
    TextView mE1UC;
    @BindView(R.id.e1_i_average)
    TextView mE1IAverage;
    @BindView(R.id.e1_u_average)
    TextView mE1UAverage;
    @BindView(R.id.e1_s_average)
    TextView mE1SAverage;
    @BindView(R.id.e1_p1_average)
    TextView mE1P1Average;
    @BindView(R.id.e1_cos_average)
    TextView mE1CosAverage;
    @BindView(R.id.e1_m_average)
    TextView mE1MAverage;
    @BindView(R.id.e1_v_average)
    TextView mE1VAverage;
    @BindView(R.id.e1_p2_average)
    TextView mE1P2Average;
    @BindView(R.id.e1_nu_average)
    TextView mE1NuAverage;
    @BindView(R.id.e1_temp_engine_average)
    TextView mE1TempEngineAverage;
    @BindView(R.id.e1_sk_average)
    TextView mE1SkAverage;
    @BindView(R.id.e1_i_specified)
    TextView mE1ISpecified;
    @BindView(R.id.e1_u_specified)
    TextView mE1USpecified;
    @BindView(R.id.e1_s_specified)
    TextView mE1SSpecified;
    @BindView(R.id.e1_p1_specified)
    TextView mE1P1Specified;
    @BindView(R.id.e1_cos_specified)
    TextView mE1CosSpecified;
    @BindView(R.id.e1_m_specified)
    TextView mE1MSpecified;
    @BindView(R.id.e1_v_specified)
    TextView mE1VSpecified;
    @BindView(R.id.e1_p2_specified)
    TextView mE1P2Specified;
    @BindView(R.id.e1_nu_specified)
    TextView mE1NuSpecified;
    @BindView(R.id.e1_temp_engine_specified)
    TextView mE1TempEngineSpecified;
    @BindView(R.id.e1_sk_specified)
    TextView mE1SkSpecified;
    @BindView(R.id.e1_t_specified)
    TextView mE1TSpecified;

    @BindView(R.id.e2_i_a)
    TextView mE2IA;
    @BindView(R.id.e2_u_a)
    TextView mE2UA;
    @BindView(R.id.e2_i_b)
    TextView mE2IB;
    @BindView(R.id.e2_u_b)
    TextView mE2UB;
    @BindView(R.id.e2_s)
    TextView mE2S;
    @BindView(R.id.e2_p1)
    TextView mE2P1;
    @BindView(R.id.e2_cos)
    TextView mE2Cos;
    @BindView(R.id.e2_m)
    TextView mE2M;
    @BindView(R.id.e2_v)
    TextView mE2V;
    @BindView(R.id.e2_p2)
    TextView mE2P2;
    @BindView(R.id.e2_nu)
    TextView mE2Nu;
    @BindView(R.id.e2_temp_ambient)
    TextView mE2TempAmbient;
    @BindView(R.id.e2_temp_engine)
    TextView mE2TempEngine;
    @BindView(R.id.e2_sk)
    TextView mE2Sk;
    @BindView(R.id.e2_t)
    TextView mE2T;
    @BindView(R.id.e2_i_c)
    TextView mE2IC;
    @BindView(R.id.e2_u_c)
    TextView mE2UC;
    @BindView(R.id.e2_i_average)
    TextView mE2IAverage;
    @BindView(R.id.e2_u_average)
    TextView mE2UAverage;
    @BindView(R.id.e2_s_average)
    TextView mE2SAverage;
    @BindView(R.id.e2_p1_average)
    TextView mE2P1Average;
    @BindView(R.id.e2_cos_average)
    TextView mE2CosAverage;
    @BindView(R.id.e2_m_average)
    TextView mE2MAverage;
    @BindView(R.id.e2_v_average)
    TextView mE2VAverage;
    @BindView(R.id.e2_p2_average)
    TextView mE2P2Average;
    @BindView(R.id.e2_nu_average)
    TextView mE2NuAverage;
    @BindView(R.id.e2_temp_engine_average)
    TextView mE2TempEngineAverage;
    @BindView(R.id.e2_sk_average)
    TextView mE2SkAverage;
    @BindView(R.id.e2_i_specified)
    TextView mE2ISpecified;
    @BindView(R.id.e2_u_specified)
    TextView mE2USpecified;
    @BindView(R.id.e2_s_specified)
    TextView mE2SSpecified;
    @BindView(R.id.e2_p1_specified)
    TextView mE2P1Specified;
    @BindView(R.id.e2_cos_specified)
    TextView mE2CosSpecified;
    @BindView(R.id.e2_m_specified)
    TextView mE2MSpecified;
    @BindView(R.id.e2_v_specified)
    TextView mE2VSpecified;
    @BindView(R.id.e2_p2_specified)
    TextView mE2P2Specified;
    @BindView(R.id.e2_nu_specified)
    TextView mE2NuSpecified;
    @BindView(R.id.e2_temp_engine_specified)
    TextView mE2TempEngineSpecified;
    @BindView(R.id.e2_sk_specified)
    TextView mE2SkSpecified;
    @BindView(R.id.e2_t_specified)
    TextView mE2TSpecified;

    @BindView(R.id.e3_i_a)
    TextView mE3IA;
    @BindView(R.id.e3_u_a)
    TextView mE3UA;
    @BindView(R.id.e3_i_b)
    TextView mE3IB;
    @BindView(R.id.e3_u_b)
    TextView mE3UB;
    @BindView(R.id.e3_s)
    TextView mE3S;
    @BindView(R.id.e3_p1)
    TextView mE3P1;
    @BindView(R.id.e3_cos)
    TextView mE3Cos;
    @BindView(R.id.e3_m)
    TextView mE3M;
    @BindView(R.id.e3_v)
    TextView mE3V;
    @BindView(R.id.e3_p2)
    TextView mE3P2;
    @BindView(R.id.e3_nu)
    TextView mE3Nu;
    @BindView(R.id.e3_temp_ambient)
    TextView mE3TempAmbient;
    @BindView(R.id.e3_temp_engine)
    TextView mE3TempEngine;
    @BindView(R.id.e3_sk)
    TextView mE3Sk;
    @BindView(R.id.e3_t)
    TextView mE3T;
    @BindView(R.id.e3_i_c)
    TextView mE3IC;
    @BindView(R.id.e3_u_c)
    TextView mE3UC;
    @BindView(R.id.e3_i_average)
    TextView mE3IAverage;
    @BindView(R.id.e3_u_average)
    TextView mE3UAverage;
    @BindView(R.id.e3_s_average)
    TextView mE3SAverage;
    @BindView(R.id.e3_p1_average)
    TextView mE3P1Average;
    @BindView(R.id.e3_cos_average)
    TextView mE3CosAverage;
    @BindView(R.id.e3_m_average)
    TextView mE3MAverage;
    @BindView(R.id.e3_v_average)
    TextView mE3VAverage;
    @BindView(R.id.e3_p2_average)
    TextView mE3P2Average;
    @BindView(R.id.e3_nu_average)
    TextView mE3NuAverage;
    @BindView(R.id.e3_temp_engine_average)
    TextView mE3TempEngineAverage;
    @BindView(R.id.e3_sk_average)
    TextView mE3SkAverage;
    @BindView(R.id.e3_i_specified)
    TextView mE3ISpecified;
    @BindView(R.id.e3_u_specified)
    TextView mE3USpecified;
    @BindView(R.id.e3_s_specified)
    TextView mE3SSpecified;
    @BindView(R.id.e3_p1_specified)
    TextView mE3P1Specified;
    @BindView(R.id.e3_cos_specified)
    TextView mE3CosSpecified;
    @BindView(R.id.e3_m_specified)
    TextView mE3MSpecified;
    @BindView(R.id.e3_v_specified)
    TextView mE3VSpecified;
    @BindView(R.id.e3_p2_specified)
    TextView mE3P2Specified;
    @BindView(R.id.e3_nu_specified)
    TextView mE3NuSpecified;
    @BindView(R.id.e3_temp_engine_specified)
    TextView mE3TempEngineSpecified;
    @BindView(R.id.e3_sk_specified)
    TextView mE3SkSpecified;
    @BindView(R.id.e3_t_specified)
    TextView mE3TSpecified;

    @BindView(R.id.e4_u1)
    TextView mE4U1;
    @BindView(R.id.e4_u2)
    TextView mE4U2;
    @BindView(R.id.e4_u3)
    TextView mE4U3;
    @BindView(R.id.e4_i1)
    TextView mE4I1;
    @BindView(R.id.e4_i2)
    TextView mE4I2;
    @BindView(R.id.e4_i3)
    TextView mE4I3;
    @BindView(R.id.e4_result)
    TextView mE4Result;
    @BindView(R.id.e4_t)
    TextView mE4T;
    @BindView(R.id.e4_t_specified)
    TextView mE4TSpecified;

    @BindView(R.id.e5_ab)
    TextView mE5Ab;
    @BindView(R.id.e5_bc)
    TextView mE5Bc;
    @BindView(R.id.e5_ac)
    TextView mE5Ac;
    @BindView(R.id.e5_average_r)
    TextView mE5AverageR;
    @BindView(R.id.e5_temp)
    TextView mE5Temp;
    @BindView(R.id.e5_result)
    TextView mE5Result;
    @BindView(R.id.e5_average_r_specified)
    TextView mE5AverageRSpecified;

    @BindView(R.id.e6_u)
    TextView mE6U;
    @BindView(R.id.e6_i)
    TextView mE6I;
    @BindView(R.id.e6_t)
    TextView mE6T;
    @BindView(R.id.e6_result)
    TextView mE6Result;

    @BindView(R.id.e7_u_1_3_a)
    TextView mE7U13A;
    @BindView(R.id.e7_i_1_3_a)
    TextView mE7I13A;
    @BindView(R.id.e7_u_1_3_b)
    TextView mE7U13B;
    @BindView(R.id.e7_i_1_3_b)
    TextView mE7I13B;
    @BindView(R.id.e7_p_1_3)
    TextView mE7P13;
    @BindView(R.id.e7_cos_1_3)
    TextView mE7Cos13;
    @BindView(R.id.e7_p_cop_1_3)
    TextView mE7PCop13;
    @BindView(R.id.e7_p_m_p_st_1_3)
    TextView mE7PmPst13;
    @BindView(R.id.e7_p_st_1_3)
    TextView mE7Pst13;
    @BindView(R.id.e7_t_1_3)
    TextView mE7T13;
    @BindView(R.id.e7_u_1_3_c)
    TextView mE7U13C;
    @BindView(R.id.e7_i_1_3_c)
    TextView mE7I13C;
    @BindView(R.id.e7_u_1_3_average)
    TextView mE7U13Average;
    @BindView(R.id.e7_i_1_3_average)
    TextView mE7I13Average;
    @BindView(R.id.e7_u_1_2_a)
    TextView mE7U12A;
    @BindView(R.id.e7_i_1_2_a)
    TextView mE7I12A;
    @BindView(R.id.e7_u_1_2_b)
    TextView mE7U12B;
    @BindView(R.id.e7_i_1_2_b)
    TextView mE7I12B;
    @BindView(R.id.e7_p_1_2)
    TextView mE7P12;
    @BindView(R.id.e7_cos_1_2)
    TextView mE7Cos12;
    @BindView(R.id.e7_p_cop_1_2)
    TextView mE7PCop12;
    @BindView(R.id.e7_p_m_p_st_1_2)
    TextView mE7PmPst12;
    @BindView(R.id.e7_p_st_1_2)
    TextView mE7Pst12;
    @BindView(R.id.e7_t_1_2)
    TextView mE7T12;
    @BindView(R.id.e7_u_1_2_c)
    TextView mE7U12C;
    @BindView(R.id.e7_i_1_2_c)
    TextView mE7I12C;
    @BindView(R.id.e7_u_1_2_average)
    TextView mE7U12Average;
    @BindView(R.id.e7_i_1_2_average)
    TextView mE7I12Average;
    @BindView(R.id.e7_u_1_1_a)
    TextView mE7U11A;
    @BindView(R.id.e7_i_1_1_a)
    TextView mE7I11A;
    @BindView(R.id.e7_u_1_1_b)
    TextView mE7U11B;
    @BindView(R.id.e7_i_1_1_b)
    TextView mE7I11B;
    @BindView(R.id.e7_p_1_1)
    TextView mE7P11;
    @BindView(R.id.e7_cos_1_1)
    TextView mE7Cos11;
    @BindView(R.id.e7_p_cop_1_1)
    TextView mE7PCop11;
    @BindView(R.id.e7_p_m_p_st_1_1)
    TextView mE7PmPst11;
    @BindView(R.id.e7_p_st_1_1)
    TextView mE7Pst11;
    @BindView(R.id.e7_t_1_1)
    TextView mE7T11;
    @BindView(R.id.e7_u_1_1_c)
    TextView mE7U11C;
    @BindView(R.id.e7_i_1_1_c)
    TextView mE7I11C;
    @BindView(R.id.e7_u_1_1_average)
    TextView mE7U11Average;
    @BindView(R.id.e7_i_1_1_average)
    TextView mE7I11Average;
    @BindView(R.id.e7_u_1_0_a)
    TextView mE7U10A;
    @BindView(R.id.e7_i_1_0_a)
    TextView mE7I10A;
    @BindView(R.id.e7_u_1_0_b)
    TextView mE7U10B;
    @BindView(R.id.e7_i_1_0_b)
    TextView mE7I10B;
    @BindView(R.id.e7_p_1_0)
    TextView mE7P10;
    @BindView(R.id.e7_cos_1_0)
    TextView mE7Cos10;
    @BindView(R.id.e7_p_cop_1_0)
    TextView mE7PCop10;
    @BindView(R.id.e7_p_m_p_st_1_0)
    TextView mE7PmPst10;
    @BindView(R.id.e7_p_st_1_0)
    TextView mE7Pst10;
    @BindView(R.id.e7_t_1_0)
    TextView mE7T10;
    @BindView(R.id.e7_u_1_0_c)
    TextView mE7U10C;
    @BindView(R.id.e7_i_1_0_c)
    TextView mE7I10C;
    @BindView(R.id.e7_u_1_0_average)
    TextView mE7U10Average;
    @BindView(R.id.e7_i_1_0_average)
    TextView mE7I10Average;
    @BindView(R.id.e7_u_0_9_a)
    TextView mE7U09A;
    @BindView(R.id.e7_i_0_9_a)
    TextView mE7I09A;
    @BindView(R.id.e7_u_0_9_b)
    TextView mE7U09B;
    @BindView(R.id.e7_i_0_9_b)
    TextView mE7I09B;
    @BindView(R.id.e7_p_0_9)
    TextView mE7P09;
    @BindView(R.id.e7_cos_0_9)
    TextView mE7Cos09;
    @BindView(R.id.e7_p_cop_0_9)
    TextView mE7PCop09;
    @BindView(R.id.e7_p_m_p_st_0_9)
    TextView mE7PmPst09;
    @BindView(R.id.e7_p_st_0_9)
    TextView mE7Pst09;
    @BindView(R.id.e7_t_0_9)
    TextView mE7T09;
    @BindView(R.id.e7_u_0_9_c)
    TextView mE7U09C;
    @BindView(R.id.e7_i_0_9_c)
    TextView mE7I09C;
    @BindView(R.id.e7_u_0_9_average)
    TextView mE7U09Average;
    @BindView(R.id.e7_i_0_9_average)
    TextView mE7I09Average;
    @BindView(R.id.e7_u_0_8_a)
    TextView mE7U08A;
    @BindView(R.id.e7_i_0_8_a)
    TextView mE7I08A;
    @BindView(R.id.e7_u_0_8_b)
    TextView mE7U08B;
    @BindView(R.id.e7_i_0_8_b)
    TextView mE7I08B;
    @BindView(R.id.e7_p_0_8)
    TextView mE7P08;
    @BindView(R.id.e7_cos_0_8)
    TextView mE7Cos08;
    @BindView(R.id.e7_p_cop_0_8)
    TextView mE7PCop08;
    @BindView(R.id.e7_p_m_p_st_0_8)
    TextView mE7PmPst08;
    @BindView(R.id.e7_p_st_0_8)
    TextView mE7Pst08;
    @BindView(R.id.e7_t_0_8)
    TextView mE7T08;
    @BindView(R.id.e7_u_0_8_c)
    TextView mE7U08C;
    @BindView(R.id.e7_i_0_8_c)
    TextView mE7I08C;
    @BindView(R.id.e7_u_0_8_average)
    TextView mE7U08Average;
    @BindView(R.id.e7_i_0_8_average)
    TextView mE7I08Average;
    @BindView(R.id.e7_u_0_7_a)
    TextView mE7U07A;
    @BindView(R.id.e7_i_0_7_a)
    TextView mE7I07A;
    @BindView(R.id.e7_u_0_7_b)
    TextView mE7U07B;
    @BindView(R.id.e7_i_0_7_b)
    TextView mE7I07B;
    @BindView(R.id.e7_p_0_7)
    TextView mE7P07;
    @BindView(R.id.e7_cos_0_7)
    TextView mE7Cos07;
    @BindView(R.id.e7_p_cop_0_7)
    TextView mE7PCop07;
    @BindView(R.id.e7_p_m_p_st_0_7)
    TextView mE7PmPst07;
    @BindView(R.id.e7_p_st_0_7)
    TextView mE7Pst07;
    @BindView(R.id.e7_t_0_7)
    TextView mE7T07;
    @BindView(R.id.e7_u_0_7_c)
    TextView mE7U07C;
    @BindView(R.id.e7_i_0_7_c)
    TextView mE7I07C;
    @BindView(R.id.e7_u_0_7_average)
    TextView mE7U07Average;
    @BindView(R.id.e7_i_0_7_average)
    TextView mE7I07Average;
    @BindView(R.id.e7_u_0_6_a)
    TextView mE7U06A;
    @BindView(R.id.e7_i_0_6_a)
    TextView mE7I06A;
    @BindView(R.id.e7_u_0_6_b)
    TextView mE7U06B;
    @BindView(R.id.e7_i_0_6_b)
    TextView mE7I06B;
    @BindView(R.id.e7_p_0_6)
    TextView mE7P06;
    @BindView(R.id.e7_cos_0_6)
    TextView mE7Cos06;
    @BindView(R.id.e7_p_cop_0_6)
    TextView mE7PCop06;
    @BindView(R.id.e7_p_m_p_st_0_6)
    TextView mE7PmPst06;
    @BindView(R.id.e7_p_st_0_6)
    TextView mE7Pst06;
    @BindView(R.id.e7_t_0_6)
    TextView mE7T06;
    @BindView(R.id.e7_u_0_6_c)
    TextView mE7U06C;
    @BindView(R.id.e7_i_0_6_c)
    TextView mE7I06C;
    @BindView(R.id.e7_u_0_6_average)
    TextView mE7U06Average;
    @BindView(R.id.e7_i_0_6_average)
    TextView mE7I06Average;
    @BindView(R.id.e7_u_0_5_a)
    TextView mE7U05A;
    @BindView(R.id.e7_i_0_5_a)
    TextView mE7I05A;
    @BindView(R.id.e7_u_0_5_b)
    TextView mE7U05B;
    @BindView(R.id.e7_i_0_5_b)
    TextView mE7I05B;
    @BindView(R.id.e7_p_0_5)
    TextView mE7P05;
    @BindView(R.id.e7_cos_0_5)
    TextView mE7Cos05;
    @BindView(R.id.e7_p_cop_0_5)
    TextView mE7PCop05;
    @BindView(R.id.e7_p_m_p_st_0_5)
    TextView mE7PmPst05;
    @BindView(R.id.e7_p_st_0_5)
    TextView mE7Pst05;
    @BindView(R.id.e7_t_0_5)
    TextView mE7T05;
    @BindView(R.id.e7_u_0_5_c)
    TextView mE7U05C;
    @BindView(R.id.e7_i_0_5_c)
    TextView mE7I05C;
    @BindView(R.id.e7_u_0_5_average)
    TextView mE7U05Average;
    @BindView(R.id.e7_i_0_5_average)
    TextView mE7I05Average;
    @BindView(R.id.e7_r)
    TextView mE7R;
    @BindView(R.id.e7_p_mech)
    TextView mE7PMech;

    @BindView(R.id.e8_u_a)
    TextView mE8UA;
    @BindView(R.id.e8_i_a)
    TextView mE8IA;
    @BindView(R.id.e8_u_b)
    TextView mE8UB;
    @BindView(R.id.e8_i_b)
    TextView mE8IB;
    @BindView(R.id.e8_p)
    TextView mE8P;
    @BindView(R.id.e8_cos)
    TextView mE8Cos;
    @BindView(R.id.e8_v)
    TextView mE8V;
    @BindView(R.id.e8_temp)
    TextView mE8Temp;
    @BindView(R.id.e8_t)
    TextView mE8T;
    @BindView(R.id.e8_u_c)
    TextView mE8UC;
    @BindView(R.id.e8_i_c)
    TextView mE8IC;
    @BindView(R.id.e8_u_average)
    TextView mE8UAverage;
    @BindView(R.id.e8_i_average)
    TextView mE8IAverage;

    @BindView(R.id.e9_u_1_0_a)
    TextView mE9U10A;
    @BindView(R.id.e9_i_1_0_a)
    TextView mE9I10A;
    @BindView(R.id.e9_u_1_0_b)
    TextView mE9U10B;
    @BindView(R.id.e9_i_1_0_b)
    TextView mE9I10B;
    @BindView(R.id.e9_p_1_0)
    TextView mE9P10;
    @BindView(R.id.e9_cos_1_0)
    TextView mE9Cos10;
    @BindView(R.id.e9_temp_ambient_1_0)
    TextView mE9TempAmbient10;
    @BindView(R.id.e9_temp_engine_1_0)
    TextView mE9TempEngine10;
    @BindView(R.id.e9_t_1_0)
    TextView mE9T10;
    @BindView(R.id.e9_u_1_0_c)
    TextView mE9U10C;
    @BindView(R.id.e9_i_1_0_c)
    TextView mE9I10C;
    @BindView(R.id.e9_u_1_0_average)
    TextView mE9U10Average;
    @BindView(R.id.e9_i_1_0_average)
    TextView mE9I10Average;
    @BindView(R.id.e9_u_0_9_a)
    TextView mE9U09A;
    @BindView(R.id.e9_i_0_9_a)
    TextView mE9I09A;
    @BindView(R.id.e9_u_0_9_b)
    TextView mE9U09B;
    @BindView(R.id.e9_i_0_9_b)
    TextView mE9I09B;
    @BindView(R.id.e9_p_0_9)
    TextView mE9P09;
    @BindView(R.id.e9_cos_0_9)
    TextView mE9Cos09;
    @BindView(R.id.e9_temp_ambient_0_9)
    TextView mE9TempAmbient09;
    @BindView(R.id.e9_temp_engine_0_9)
    TextView mE9TempEngine09;
    @BindView(R.id.e9_t_0_9)
    TextView mE9T09;
    @BindView(R.id.e9_u_0_9_c)
    TextView mE9U09C;
    @BindView(R.id.e9_i_0_9_c)
    TextView mE9I09C;
    @BindView(R.id.e9_u_0_9_average)
    TextView mE9U09Average;
    @BindView(R.id.e9_i_0_9_average)
    TextView mE9I09Average;
    @BindView(R.id.e9_u_0_8_a)
    TextView mE9U08A;
    @BindView(R.id.e9_i_0_8_a)
    TextView mE9I08A;
    @BindView(R.id.e9_u_0_8_b)
    TextView mE9U08B;
    @BindView(R.id.e9_i_0_8_b)
    TextView mE9I08B;
    @BindView(R.id.e9_p_0_8)
    TextView mE9P08;
    @BindView(R.id.e9_cos_0_8)
    TextView mE9Cos08;
    @BindView(R.id.e9_temp_ambient_0_8)
    TextView mE9TempAmbient08;
    @BindView(R.id.e9_temp_engine_0_8)
    TextView mE9TempEngine08;
    @BindView(R.id.e9_t_0_8)
    TextView mE9T08;
    @BindView(R.id.e9_u_0_8_c)
    TextView mE9U08C;
    @BindView(R.id.e9_i_0_8_c)
    TextView mE9I08C;
    @BindView(R.id.e9_u_0_8_average)
    TextView mE9U08Average;
    @BindView(R.id.e9_i_0_8_average)
    TextView mE9I08Average;
    @BindView(R.id.e9_u_0_7_a)
    TextView mE9U07A;
    @BindView(R.id.e9_i_0_7_a)
    TextView mE9I07A;
    @BindView(R.id.e9_u_0_7_b)
    TextView mE9U07B;
    @BindView(R.id.e9_i_0_7_b)
    TextView mE9I07B;
    @BindView(R.id.e9_p_0_7)
    TextView mE9P07;
    @BindView(R.id.e9_cos_0_7)
    TextView mE9Cos07;
    @BindView(R.id.e9_temp_ambient_0_7)
    TextView mE9TempAmbient07;
    @BindView(R.id.e9_temp_engine_0_7)
    TextView mE9TempEngine07;
    @BindView(R.id.e9_t_0_7)
    TextView mE9T07;
    @BindView(R.id.e9_u_0_7_c)
    TextView mE9U07C;
    @BindView(R.id.e9_i_0_7_c)
    TextView mE9I07C;
    @BindView(R.id.e9_u_0_7_average)
    TextView mE9U07Average;
    @BindView(R.id.e9_i_0_7_average)
    TextView mE9I07Average;
    @BindView(R.id.e9_u_0_6_a)
    TextView mE9U06A;
    @BindView(R.id.e9_i_0_6_a)
    TextView mE9I06A;
    @BindView(R.id.e9_u_0_6_b)
    TextView mE9U06B;
    @BindView(R.id.e9_i_0_6_b)
    TextView mE9I06B;
    @BindView(R.id.e9_p_0_6)
    TextView mE9P06;
    @BindView(R.id.e9_cos_0_6)
    TextView mE9Cos06;
    @BindView(R.id.e9_temp_ambient_0_6)
    TextView mE9TempAmbient06;
    @BindView(R.id.e9_temp_engine_0_6)
    TextView mE9TempEngine06;
    @BindView(R.id.e9_t_0_6)
    TextView mE9T06;
    @BindView(R.id.e9_u_0_6_c)
    TextView mE9U06C;
    @BindView(R.id.e9_i_0_6_c)
    TextView mE9I06C;
    @BindView(R.id.e9_u_0_6_average)
    TextView mE9U06Average;
    @BindView(R.id.e9_i_0_6_average)
    TextView mE9I06Average;

    @BindView(R.id.e10_i_a)
    TextView mE10IA;
    @BindView(R.id.e10_u_a)
    TextView mE10UA;
    @BindView(R.id.e10_i_b)
    TextView mE10IB;
    @BindView(R.id.e10_u_b)
    TextView mE10UB;
    @BindView(R.id.e10_s)
    TextView mE10S;
    @BindView(R.id.e10_p1)
    TextView mE10P1;
    @BindView(R.id.e10_cos)
    TextView mE10Cos;
    @BindView(R.id.e10_m)
    TextView mE10M;
    @BindView(R.id.e10_v)
    TextView mE10V;
    @BindView(R.id.e10_p2)
    TextView mE10P2;
    @BindView(R.id.e10_nu)
    TextView mE10Nu;
    @BindView(R.id.e10_temp_ambient)
    TextView mE10TempAmbient;
    @BindView(R.id.e10_temp_engine)
    TextView mE10TempEngine;
    @BindView(R.id.e10_sk)
    TextView mE10Sk;
    @BindView(R.id.e10_t)
    TextView mE10T;
    @BindView(R.id.e10_i_c)
    TextView mE10IC;
    @BindView(R.id.e10_u_c)
    TextView mE10UC;
    @BindView(R.id.e10_i_average)
    TextView mE10IAverage;
    @BindView(R.id.e10_u_average)
    TextView mE10UAverage;
    @BindView(R.id.e10_s_average)
    TextView mE10SAverage;
    @BindView(R.id.e10_p1_average)
    TextView mE10P1Average;
    @BindView(R.id.e10_cos_average)
    TextView mE10CosAverage;
    @BindView(R.id.e10_m_average)
    TextView mE10MAverage;
    @BindView(R.id.e10_v_average)
    TextView mE10VAverage;
    @BindView(R.id.e10_p2_average)
    TextView mE10P2Average;
    @BindView(R.id.e10_nu_average)
    TextView mE10NuAverage;
    @BindView(R.id.e10_temp_engine_average)
    TextView mE10TempEngineAverage;
    @BindView(R.id.e10_sk_average)
    TextView mE10SkAverage;
    @BindView(R.id.e10_i_specified)
    TextView mE10ISpecified;
    @BindView(R.id.e10_u_specified)
    TextView mE10USpecified;
    @BindView(R.id.e10_s_specified)
    TextView mE10SSpecified;
    @BindView(R.id.e10_p1_specified)
    TextView mE10P1Specified;
    @BindView(R.id.e10_cos_specified)
    TextView mE10CosSpecified;
    @BindView(R.id.e10_m_specified)
    TextView mE10MSpecified;
    @BindView(R.id.e10_v_specified)
    TextView mE10VSpecified;
    @BindView(R.id.e10_p2_specified)
    TextView mE10P2Specified;
    @BindView(R.id.e10_nu_specified)
    TextView mE10NuSpecified;
    @BindView(R.id.e10_temp_engine_specified)
    TextView mE10TempEngineSpecified;
    @BindView(R.id.e10_sk_specified)
    TextView mE10SkSpecified;
    @BindView(R.id.e10_t_specified)
    TextView mE10TSpecified;

    @BindView(R.id.e11_u_r)
    TextView mE11UR;
    @BindView(R.id.e11_r15)
    TextView mE11R15;
    @BindView(R.id.e11_r60)
    TextView mE11R60;
    @BindView(R.id.e11_k)
    TextView mE11K;
    @BindView(R.id.e11_temp)
    TextView mE11Temp;
    @BindView(R.id.e11_result)
    TextView mE11Result;

    @BindView(R.id.e12_i_a)
    TextView mE12IA;
    @BindView(R.id.e12_u_a)
    TextView mE12UA;
    @BindView(R.id.e12_i_b)
    TextView mE12IB;
    @BindView(R.id.e12_u_b)
    TextView mE12UB;
    @BindView(R.id.e12_s)
    TextView mE12S;
    @BindView(R.id.e12_p1)
    TextView mE12P1;
    @BindView(R.id.e12_cos)
    TextView mE12Cos;
    @BindView(R.id.e12_m)
    TextView mE12M;
    @BindView(R.id.e12_v)
    TextView mE12V;
    @BindView(R.id.e12_p2)
    TextView mE12P2;
    @BindView(R.id.e12_nu)
    TextView mE12Nu;
    @BindView(R.id.e12_temp_ambient)
    TextView mE12TempAmbient;
    @BindView(R.id.e12_temp_engine)
    TextView mE12TempEngine;
    @BindView(R.id.e12_sk)
    TextView mE12Sk;
    @BindView(R.id.e12_t)
    TextView mE12T;
    @BindView(R.id.e12_i_c)
    TextView mE12IC;
    @BindView(R.id.e12_u_c)
    TextView mE12UC;
    @BindView(R.id.e12_i_average)
    TextView mE12IAverage;
    @BindView(R.id.e12_u_average)
    TextView mE12UAverage;
    @BindView(R.id.e12_s_average)
    TextView mE12SAverage;
    @BindView(R.id.e12_p1_average)
    TextView mE12P1Average;
    @BindView(R.id.e12_cos_average)
    TextView mE12CosAverage;
    @BindView(R.id.e12_m_average)
    TextView mE12MAverage;
    @BindView(R.id.e12_v_average)
    TextView mE12VAverage;
    @BindView(R.id.e12_p2_average)
    TextView mE12P2Average;
    @BindView(R.id.e12_nu_average)
    TextView mE12NuAverage;
    @BindView(R.id.e12_temp_engine_average)
    TextView mE12TempEngineAverage;
    @BindView(R.id.e12_sk_average)
    TextView mE12SkAverage;
    @BindView(R.id.e12_i_specified)
    TextView mE12ISpecified;
    @BindView(R.id.e12_u_specified)
    TextView mE12USpecified;
    @BindView(R.id.e12_s_specified)
    TextView mE12SSpecified;
    @BindView(R.id.e12_p1_specified)
    TextView mE12P1Specified;
    @BindView(R.id.e12_cos_specified)
    TextView mE12CosSpecified;
    @BindView(R.id.e12_m_specified)
    TextView mE12MSpecified;
    @BindView(R.id.e12_v_specified)
    TextView mE12VSpecified;
    @BindView(R.id.e12_p2_specified)
    TextView mE12P2Specified;
    @BindView(R.id.e12_nu_specified)
    TextView mE12NuSpecified;
    @BindView(R.id.e12_temp_engine_specified)
    TextView mE12TempEngineSpecified;
    @BindView(R.id.e12_sk_specified)
    TextView mE12SkSpecified;
    @BindView(R.id.e12_t_specified)
    TextView mE12TSpecified;

    @BindView(R.id.e13_u_0_8_a)
    TextView mE13U08A;
    @BindView(R.id.e13_i_0_8_a)
    TextView mE13I08A;
    @BindView(R.id.e13_u_0_8_b)
    TextView mE13U08B;
    @BindView(R.id.e13_i_0_8_b)
    TextView mE13I08B;
    @BindView(R.id.e13_s_0_8)
    TextView mE13S08;
    @BindView(R.id.e13_p_0_8)
    TextView mE13P08;
    @BindView(R.id.e13_v_0_8)
    TextView mE13V08;
    @BindView(R.id.e13_m_0_8)
    TextView mE13M08;
    @BindView(R.id.e13_f_0_8)
    TextView mE13F08;
    @BindView(R.id.e13_temp_0_8)
    TextView mE13Temp08;
    @BindView(R.id.e13_t_0_8)
    TextView mE13T08;
    @BindView(R.id.e13_u_0_8_c)
    TextView mE13U08C;
    @BindView(R.id.e13_i_0_8_c)
    TextView mE13I08C;
    @BindView(R.id.e13_u_0_8_average)
    TextView mE13U08Average;
    @BindView(R.id.e13_i_0_8_average)
    TextView mE13I08Average;
    @BindView(R.id.e13_u_1_1_a)
    TextView mE13U11A;
    @BindView(R.id.e13_i_1_1_a)
    TextView mE13I11A;
    @BindView(R.id.e13_u_1_1_b)
    TextView mE13U11B;
    @BindView(R.id.e13_i_1_1_b)
    TextView mE13I11B;
    @BindView(R.id.e13_s_1_1)
    TextView mE13S11;
    @BindView(R.id.e13_p_1_1)
    TextView mE13P11;
    @BindView(R.id.e13_v_1_1)
    TextView mE13V11;
    @BindView(R.id.e13_m_1_1)
    TextView mE13M11;
    @BindView(R.id.e13_f_1_1)
    TextView mE13F11;
    @BindView(R.id.e13_temp_1_1)
    TextView mE13Temp11;
    @BindView(R.id.e13_t_1_1)
    TextView mE13T11;
    @BindView(R.id.e13_u_1_1_c)
    TextView mE13U11C;
    @BindView(R.id.e13_i_1_1_c)
    TextView mE13I11C;
    @BindView(R.id.e13_u_1_1_average)
    TextView mE13U11Average;
    @BindView(R.id.e13_i_1_1_average)
    TextView mE13I11Average;

    @BindView(R.id.e14_m)
    TextView mE14M;
    @BindView(R.id.e14_v)
    TextView mE14V;

    @BindView(R.id.e15_i)
    TextView mE15I;
    @BindView(R.id.e15_m)
    TextView mE15M;

    @BindView(R.id.e16_i_b)
    TextView mE16IB;
    @BindView(R.id.e16_u_b)
    TextView mE16UB;
    @BindView(R.id.e16_s)
    TextView mE16S;
    @BindView(R.id.e16_p1)
    TextView mE16P1;
    @BindView(R.id.e16_cos)
    TextView mE16Cos;
    @BindView(R.id.e16_m)
    TextView mE16M;
    @BindView(R.id.e16_v)
    TextView mE16V;
    @BindView(R.id.e16_i_specified)
    TextView mE16ISpecified;
    @BindView(R.id.e16_u_specified)
    TextView mE16USpecified;
    @BindView(R.id.e16_s_specified)
    TextView mE16SSpecified;
    @BindView(R.id.e16_p1_specified)
    TextView mE16P1Specified;
    @BindView(R.id.e16_cos_specified)
    TextView mE16CosSpecified;
    @BindView(R.id.e16_m_specified)
    TextView mE16MSpecified;
    @BindView(R.id.e16_v_specified)
    TextView mE16VSpecified;

    @BindView(R.id.e17_ab)
    TextView mE17Ab;
    @BindView(R.id.e17_bc)
    TextView mE17Bc;
    @BindView(R.id.e17_ac)
    TextView mE17Ac;
    @BindView(R.id.e17_average_r)
    TextView mE17AverageR;
    @BindView(R.id.e17_temp)
    TextView mE17Temp;
    @BindView(R.id.e17_result)
    TextView mE17Result;
    @BindView(R.id.e17_average_r_specified)
    TextView mE17AverageRSpecified;
    //endregion

    //region Вкладка 4 Протокол
    @BindView(R.id.review_layout)
    GridLayout mReviewLayout;

    @BindView(R.id.select_date)
    Button mSelectDate;
    @BindView(R.id.protocols)
    Spinner mProtocols;

    @BindView(R.id.save_layout)
    GridLayout mSaveLayout;


    @BindView(R.id.position1)
    Spinner mPosition1;
    @BindView(R.id.position1_number)
    EditText mPosition1Number;
    @BindView(R.id.position1_full_name)
    EditText mPosition1FullName;
    @BindView(R.id.position2)
    Spinner mPosition2;
    @BindView(R.id.position2_number)
    EditText mPosition2Number;
    @BindView(R.id.position2_full_name)
    EditText mPosition2FullName;
    //endregion

    //region Панель управления
    @BindView(R.id.fab_options)
    FabOptions mFabOptions;
    //endregion

    //endregion

    private OnRealmReceiverCallback mOnRealmReceiverCallback = new OnRealmReceiverCallback() {
        @Override
        public void onRealmReceiver(Realm realm) {
            mRealm = realm;
        }
    };
    private Realm mRealm;

    private String mExperimentType;
    private MainModel mModel;
    private MainPresenter mMainPresenter;
    private BroadcastReceiver mBroadcastReceiver;
    private Locale mRuLocale =  new Locale("ru");
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd.MM.yyyy", mRuLocale);
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", mRuLocale);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_main);

        replacingImplementations();
        ButterKnife.bind(this);

        mModel = new MainModel(mOnRealmReceiverCallback);
        mMainPresenter = new MainPresenter(this, mModel);
        mMainPresenter.activityReady();
    }

    /**
     * Замена реализаций библиотеки POI(сохранение xml-документов: xlsx, docx) на более лёгкие
     */
    private void replacingImplementations() {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
    }

    @Override
    public SparseBooleanArray getExperiment() {
        return mExperimentsList.getCheckedItemPositions();
    }

    private long getStartDate(String s) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(mDateFormat.parse(s));
        } catch (ParseException ignored) {
        }
        return cal.getTimeInMillis();
    }

    private long getEndDate(String s) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(mDateFormat.parse(s));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        } catch (ParseException ignored) {
        }
        return cal.getTimeInMillis();
    }

    @Override
    public void setBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_INIT_USB_PERMISSION);
        filter.addAction(ACTION_USB_ATTACHED);
        mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_INIT_USB_PERMISSION.equals(action) || ACTION_USB_ATTACHED.equals(action)) {
                    synchronized (this) {
                        getPermissionDevices();
                    }
                }
            }
        };
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    public void getPermissionDevices() {
        UsbManager usbManager = (UsbManager) getSystemService(USB_SERVICE);
        if (usbManager != null) {
            for (UsbDevice usbDevice : usbManager.getDeviceList().values()) {
                if (!usbManager.hasPermission(usbDevice)) {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_INIT_USB_PERMISSION), 0);
                    usbManager.requestPermission(usbDevice, pi);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        mRealm.close();
    }

    @Override
    public void initializeViews() {
        initializeFabOptions();
        initializeTabHost();
        initializeSubjectSelector();
        initializePlatformSelector();
        initializeExperimentsSelector();
        initializeDateButton();
        initializeExperimentsList();
        mPosition2.setSelection(1);
    }

    private void initializeFabOptions() {
        mFabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (v.getId()) {
                    case R.id.faboptions_devices:
                        intent.setClass(MainActivity.this, DevicesStatusActivity.class);
                        break;
                    case R.id.faboptions_subject:
                        intent.putExtra("id", mModel.getSubjectId());
                        intent.setClass(MainActivity.this, SubjectPassiveActivity.class);
                        break;
                    case R.id.faboptions_protections:
                        intent.setClass(MainActivity.this, ProtectionsActivity.class);
                        break;
                    case R.id.faboptions_events:
                        intent.setClass(MainActivity.this, EventsActivity.class);
                        break;
                }
                startActivity(intent);
            }
        });
    }

    private void initializeTabHost() {
        mTabHost.setup();
        addTabToTabHost(mTabHost, SUBJECT_TAB_TAG, SUBJECT_VIEW_ID, SUBJECT_TAB_LABEL);
        addTabToTabHost(mTabHost, EXPERIMENTS_TAB_TAG, EXPERIMENTS_VIEW_ID, EXPERIMENTS_TAB_LABEL);
        addTabToTabHost(mTabHost, RESULTS_TAB_TAG, RESULTS_VIEW_ID, RESULTS_TAB_LABEL);
        addTabToTabHost(mTabHost, PROTOCOL_TAB_TAG, PROTOCOL_VIEW_ID, PROTOCOL_TAB_LABEL);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tag) {
                switch (tag) {
                    case PROTOCOL_TAB_TAG:
                        mMainPresenter.protocolTabSelected(MainActivity.this, mProtocols,
                                getStartDate(mSelectDate.getText().toString()), getEndDate(mSelectDate.getText().toString()));
                        break;
                    case RESULTS_TAB_TAG:
                        refillResults();
                        break;
                }
            }
        });
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            TextView tabTextView = mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tabTextView.setTextColor(Color.parseColor("#000000"));
        }
    }

    private void refillResults() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        if (experiments != null) {
            mE1IA.setText(experiments.getE1IA());
            mE1UA.setText(experiments.getE1UA());
            mE1IB.setText(experiments.getE1IB());
            mE1UB.setText(experiments.getE1UB());
            mE1S.setText(experiments.getE1S());
            mE1P1.setText(experiments.getE1P1());
            mE1Cos.setText(experiments.getE1Cos());
            mE1M.setText(experiments.getE1M());
            mE1V.setText(experiments.getE1V());
            mE1P2.setText(experiments.getE1P2());
            mE1Nu.setText(experiments.getE1Nu());
            mE1TempAmbient.setText(experiments.getE1TempAmbient());
            mE1TempEngine.setText(experiments.getE1TempEngine());
            mE1Sk.setText(experiments.getE1Sk());
            mE1T.setText(experiments.getE1T());
            mE1IC.setText(experiments.getE1IC());
            mE1UC.setText(experiments.getE1UC());
            mE1IAverage.setText(experiments.getE1IAverage());
            mE1UAverage.setText(experiments.getE1UAverage());
            mE1SAverage.setText(experiments.getE1SAverage());
            mE1P1Average.setText(experiments.getE1P1Average());
            mE1CosAverage.setText(experiments.getE1CosAverage());
            mE1MAverage.setText(experiments.getE1MAverage());
            mE1VAverage.setText(experiments.getE1VAverage());
            mE1P2Average.setText(experiments.getE1P2Average());
            mE1NuAverage.setText(experiments.getE1NuAverage());
            mE1TempEngineAverage.setText(experiments.getE1TempEngineAverage());
            mE1SkAverage.setText(experiments.getE1SkAverage());
            mE1ISpecified.setText(experiments.getE1ISpecified());
            mE1USpecified.setText(experiments.getE1USpecified());
            mE1SSpecified.setText(experiments.getE1SSpecified());
            mE1P1Specified.setText(experiments.getE1P1Specified());
            mE1CosSpecified.setText(experiments.getE1CosSpecified());
            mE1MSpecified.setText(experiments.getE1MSpecified());
            mE1VSpecified.setText(experiments.getE1VSpecified());
            mE1P2Specified.setText(experiments.getE1P2Specified());
            mE1NuSpecified.setText(experiments.getE1NuSpecified());
            mE1TempEngineSpecified.setText(experiments.getE1TempEngineSpecified());
            mE1SkSpecified.setText(experiments.getE1SkSpecified());
            mE1TSpecified.setText(experiments.getE1TSpecified());

            mE2IA.setText(experiments.getE2IA());
            mE2UA.setText(experiments.getE2UA());
            mE2IB.setText(experiments.getE2IB());
            mE2UB.setText(experiments.getE2UB());
            mE2S.setText(experiments.getE2S());
            mE2P1.setText(experiments.getE2P1());
            mE2Cos.setText(experiments.getE2Cos());
            mE2M.setText(experiments.getE2M());
            mE2V.setText(experiments.getE2V());
            mE2P2.setText(experiments.getE2P2());
            mE2Nu.setText(experiments.getE2Nu());
            mE2TempAmbient.setText(experiments.getE2TempAmbient());
            mE2TempEngine.setText(experiments.getE2TempEngine());
            mE2Sk.setText(experiments.getE2Sk());
            mE2T.setText(experiments.getE2T());
            mE2IC.setText(experiments.getE2IC());
            mE2UC.setText(experiments.getE2UC());
            mE2IAverage.setText(experiments.getE2IAverage());
            mE2UAverage.setText(experiments.getE2UAverage());
            mE2SAverage.setText(experiments.getE2SAverage());
            mE2P1Average.setText(experiments.getE2P1Average());
            mE2CosAverage.setText(experiments.getE2CosAverage());
            mE2MAverage.setText(experiments.getE2MAverage());
            mE2VAverage.setText(experiments.getE2VAverage());
            mE2P2Average.setText(experiments.getE2P2Average());
            mE2NuAverage.setText(experiments.getE2NuAverage());
            mE2TempEngineAverage.setText(experiments.getE2TempEngineAverage());
            mE2SkAverage.setText(experiments.getE2SkAverage());
            mE2ISpecified.setText(experiments.getE2ISpecified());
            mE2USpecified.setText(experiments.getE2USpecified());
            mE2SSpecified.setText(experiments.getE2SSpecified());
            mE2P1Specified.setText(experiments.getE2P1Specified());
            mE2CosSpecified.setText(experiments.getE2CosSpecified());
            mE2MSpecified.setText(experiments.getE2MSpecified());
            mE2VSpecified.setText(experiments.getE2VSpecified());
            mE2P2Specified.setText(experiments.getE2P2Specified());
            mE2NuSpecified.setText(experiments.getE2NuSpecified());
            mE2TempEngineSpecified.setText(experiments.getE2TempEngineSpecified());
            mE2SkSpecified.setText(experiments.getE2SkSpecified());
            mE2TSpecified.setText(experiments.getE2TSpecified());

            mE3IA.setText(experiments.getE3IA());
            mE3UA.setText(experiments.getE3UA());
            mE3IB.setText(experiments.getE3IB());
            mE3UB.setText(experiments.getE3UB());
            mE3S.setText(experiments.getE3S());
            mE3P1.setText(experiments.getE3P1());
            mE3Cos.setText(experiments.getE3Cos());
            mE3M.setText(experiments.getE3M());
            mE3V.setText(experiments.getE3V());
            mE3P2.setText(experiments.getE3P2());
            mE3Nu.setText(experiments.getE3Nu());
            mE3TempAmbient.setText(experiments.getE3TempAmbient());
            mE3TempEngine.setText(experiments.getE3TempEngine());
            mE3Sk.setText(experiments.getE3Sk());
            mE3T.setText(experiments.getE3T());
            mE3IC.setText(experiments.getE3IC());
            mE3UC.setText(experiments.getE3UC());
            mE3IAverage.setText(experiments.getE3IAverage());
            mE3UAverage.setText(experiments.getE3UAverage());
            mE3SAverage.setText(experiments.getE3SAverage());
            mE3P1Average.setText(experiments.getE3P1Average());
            mE3CosAverage.setText(experiments.getE3CosAverage());
            mE3MAverage.setText(experiments.getE3MAverage());
            mE3VAverage.setText(experiments.getE3VAverage());
            mE3P2Average.setText(experiments.getE3P2Average());
            mE3NuAverage.setText(experiments.getE3NuAverage());
            mE3TempEngineAverage.setText(experiments.getE3TempEngineAverage());
            mE3SkAverage.setText(experiments.getE3SkAverage());
            mE3ISpecified.setText(experiments.getE3ISpecified());
            mE3USpecified.setText(experiments.getE3USpecified());
            mE3SSpecified.setText(experiments.getE3SSpecified());
            mE3P1Specified.setText(experiments.getE3P1Specified());
            mE3CosSpecified.setText(experiments.getE3CosSpecified());
            mE3MSpecified.setText(experiments.getE3MSpecified());
            mE3VSpecified.setText(experiments.getE3VSpecified());
            mE3P2Specified.setText(experiments.getE3P2Specified());
            mE3NuSpecified.setText(experiments.getE3NuSpecified());
            mE3TempEngineSpecified.setText(experiments.getE3TempEngineSpecified());
            mE3SkSpecified.setText(experiments.getE3SkSpecified());
            mE3TSpecified.setText(experiments.getE3TSpecified());

            mE4U1.setText(experiments.getE4U1());
            mE4U2.setText(experiments.getE4U2());
            mE4U3.setText(experiments.getE4U3());
            mE4I1.setText(experiments.getE4I1());
            mE4I2.setText(experiments.getE4I2());
            mE4I3.setText(experiments.getE4I3());
            mE4Result.setText(experiments.getE4Result());
            mE4T.setText(experiments.getE4T());
            mE4TSpecified.setText(experiments.getE4TSpecified());

            mE5Ab.setText(experiments.getE5Ab());
            mE5Bc.setText(experiments.getE5Bc());
            mE5Ac.setText(experiments.getE5Ac());
            mE5AverageR.setText(experiments.getE5AverageR());
            mE5Temp.setText(experiments.getE5Temp());
            mE5Result.setText(experiments.getE5Result());
            mE5AverageRSpecified.setText(experiments.getE5AverageRSpecified());

            mE6U.setText(experiments.getE6U());
            mE6I.setText(experiments.getE6I());
            mE6T.setText(experiments.getE6T());
            mE6Result.setText(experiments.getE6Result());

            mE7U13A.setText(experiments.getE7U13A());
            mE7I13A.setText(experiments.getE7I13A());
            mE7U13B.setText(experiments.getE7U13B());
            mE7I13B.setText(experiments.getE7I13B());
            mE7P13.setText(experiments.getE7P13());
            mE7Cos13.setText(experiments.getE7Cos13());
            mE7PCop13.setText(experiments.getE7PCop13());
            mE7PmPst13.setText(experiments.getE7PmPst13());
            mE7Pst13.setText(experiments.getE7Pst13());
            mE7T13.setText(experiments.getE7T13());
            mE7U13C.setText(experiments.getE7U13C());
            mE7I13C.setText(experiments.getE7I13C());
            mE7U13Average.setText(experiments.getE7U13Average());
            mE7I13Average.setText(experiments.getE7I13Average());
            mE7U12A.setText(experiments.getE7U12A());
            mE7I12A.setText(experiments.getE7I12A());
            mE7U12B.setText(experiments.getE7U12B());
            mE7I12B.setText(experiments.getE7I12B());
            mE7P12.setText(experiments.getE7P12());
            mE7Cos12.setText(experiments.getE7Cos12());
            mE7PCop12.setText(experiments.getE7PCop12());
            mE7PmPst12.setText(experiments.getE7PmPst12());
            mE7Pst12.setText(experiments.getE7Pst12());
            mE7T12.setText(experiments.getE7T12());
            mE7U12C.setText(experiments.getE7U12C());
            mE7I12C.setText(experiments.getE7I12C());
            mE7U12Average.setText(experiments.getE7U12Average());
            mE7I12Average.setText(experiments.getE7I12Average());
            mE7U11A.setText(experiments.getE7U11A());
            mE7I11A.setText(experiments.getE7I11A());
            mE7U11B.setText(experiments.getE7U11B());
            mE7I11B.setText(experiments.getE7I11B());
            mE7P11.setText(experiments.getE7P11());
            mE7Cos11.setText(experiments.getE7Cos11());
            mE7PCop11.setText(experiments.getE7PCop11());
            mE7PmPst11.setText(experiments.getE7PmPst11());
            mE7Pst11.setText(experiments.getE7Pst11());
            mE7T11.setText(experiments.getE7T11());
            mE7U11C.setText(experiments.getE7U11C());
            mE7I11C.setText(experiments.getE7I11C());
            mE7U11Average.setText(experiments.getE7U11Average());
            mE7I11Average.setText(experiments.getE7I11Average());
            mE7U10A.setText(experiments.getE7U10A());
            mE7I10A.setText(experiments.getE7I10A());
            mE7U10B.setText(experiments.getE7U10B());
            mE7I10B.setText(experiments.getE7I10B());
            mE7P10.setText(experiments.getE7P10());
            mE7Cos10.setText(experiments.getE7Cos10());
            mE7PCop10.setText(experiments.getE7PCop10());
            mE7PmPst10.setText(experiments.getE7PmPst10());
            mE7Pst10.setText(experiments.getE7Pst10());
            mE7T10.setText(experiments.getE7T10());
            mE7U10C.setText(experiments.getE7U10C());
            mE7I10C.setText(experiments.getE7I10C());
            mE7U10Average.setText(experiments.getE7U10Average());
            mE7I10Average.setText(experiments.getE7I10Average());
            mE7U09A.setText(experiments.getE7U09A());
            mE7I09A.setText(experiments.getE7I09A());
            mE7U09B.setText(experiments.getE7U09B());
            mE7I09B.setText(experiments.getE7I09B());
            mE7P09.setText(experiments.getE7P09());
            mE7Cos09.setText(experiments.getE7Cos09());
            mE7PCop09.setText(experiments.getE7PCop09());
            mE7PmPst09.setText(experiments.getE7PmPst09());
            mE7Pst09.setText(experiments.getE7Pst09());
            mE7T09.setText(experiments.getE7T09());
            mE7U09C.setText(experiments.getE7U09C());
            mE7I09C.setText(experiments.getE7I09C());
            mE7U09Average.setText(experiments.getE7U09Average());
            mE7I09Average.setText(experiments.getE7I09Average());
            mE7U08A.setText(experiments.getE7U08A());
            mE7I08A.setText(experiments.getE7I08A());
            mE7U08B.setText(experiments.getE7U08B());
            mE7I08B.setText(experiments.getE7I08B());
            mE7P08.setText(experiments.getE7P08());
            mE7Cos08.setText(experiments.getE7Cos08());
            mE7PCop08.setText(experiments.getE7PCop08());
            mE7PmPst08.setText(experiments.getE7PmPst08());
            mE7Pst08.setText(experiments.getE7Pst08());
            mE7T08.setText(experiments.getE7T08());
            mE7U08C.setText(experiments.getE7U08C());
            mE7I08C.setText(experiments.getE7I08C());
            mE7U08Average.setText(experiments.getE7U08Average());
            mE7I08Average.setText(experiments.getE7I08Average());
            mE7U07A.setText(experiments.getE7U07A());
            mE7I07A.setText(experiments.getE7I07A());
            mE7U07B.setText(experiments.getE7U07B());
            mE7I07B.setText(experiments.getE7I07B());
            mE7P07.setText(experiments.getE7P07());
            mE7Cos07.setText(experiments.getE7Cos07());
            mE7PCop07.setText(experiments.getE7PCop07());
            mE7PmPst07.setText(experiments.getE7PmPst07());
            mE7Pst07.setText(experiments.getE7Pst07());
            mE7T07.setText(experiments.getE7T07());
            mE7U07C.setText(experiments.getE7U07C());
            mE7I07C.setText(experiments.getE7I07C());
            mE7U07Average.setText(experiments.getE7U07Average());
            mE7I07Average.setText(experiments.getE7I07Average());
            mE7U06A.setText(experiments.getE7U06A());
            mE7I06A.setText(experiments.getE7I06A());
            mE7U06B.setText(experiments.getE7U06B());
            mE7I06B.setText(experiments.getE7I06B());
            mE7P06.setText(experiments.getE7P06());
            mE7Cos06.setText(experiments.getE7Cos06());
            mE7PCop06.setText(experiments.getE7PCop06());
            mE7PmPst06.setText(experiments.getE7PmPst06());
            mE7Pst06.setText(experiments.getE7Pst06());
            mE7T06.setText(experiments.getE7T06());
            mE7U06C.setText(experiments.getE7U06C());
            mE7I06C.setText(experiments.getE7I06C());
            mE7U06Average.setText(experiments.getE7U06Average());
            mE7I06Average.setText(experiments.getE7I06Average());
            mE7U05A.setText(experiments.getE7U05A());
            mE7I05A.setText(experiments.getE7I05A());
            mE7U05B.setText(experiments.getE7U05B());
            mE7I05B.setText(experiments.getE7I05B());
            mE7P05.setText(experiments.getE7P05());
            mE7Cos05.setText(experiments.getE7Cos05());
            mE7PCop05.setText(experiments.getE7PCop05());
            mE7PmPst05.setText(experiments.getE7PmPst05());
            mE7Pst05.setText(experiments.getE7Pst05());
            mE7T05.setText(experiments.getE7T05());
            mE7U05C.setText(experiments.getE7U05C());
            mE7I05C.setText(experiments.getE7I05C());
            mE7U05Average.setText(experiments.getE7U05Average());
            mE7I05Average.setText(experiments.getE7I05Average());
            mE7R.setText(experiments.getE7R());
            mE7PMech.setText(experiments.getE7PMech());

            mE8UA.setText(experiments.getE8UA());
            mE8IA.setText(experiments.getE8IA());
            mE8UB.setText(experiments.getE8UB());
            mE8IB.setText(experiments.getE8IB());
            mE8P.setText(experiments.getE8P());
            mE8Cos.setText(experiments.getE8Cos());
            mE8V.setText(experiments.getE8V());
            mE8Temp.setText(experiments.getE8Temp());
            mE8T.setText(experiments.getE8T());
            mE8UC.setText(experiments.getE8UC());
            mE8IC.setText(experiments.getE8IC());
            mE8UAverage.setText(experiments.getE8UAverage());
            mE8IAverage.setText(experiments.getE8IAverage());

            mE9U10A.setText(experiments.getE9U10A());
            mE9I10A.setText(experiments.getE9I10A());
            mE9U10B.setText(experiments.getE9U10B());
            mE9I10B.setText(experiments.getE9I10B());
            mE9P10.setText(experiments.getE9P10());
            mE9Cos10.setText(experiments.getE9Cos10());
            mE9TempAmbient10.setText(experiments.getE9TempAmbient10());
            mE9TempEngine10.setText(experiments.getE9TempEngine10());
            mE9T10.setText(experiments.getE9T10());
            mE9U10C.setText(experiments.getE9U10C());
            mE9I10C.setText(experiments.getE9I10C());
            mE9U10Average.setText(experiments.getE9U10Average());
            mE9I10Average.setText(experiments.getE9I10Average());
            mE9U09A.setText(experiments.getE9U09A());
            mE9I09A.setText(experiments.getE9I09A());
            mE9U09B.setText(experiments.getE9U09B());
            mE9I09B.setText(experiments.getE9I09B());
            mE9P09.setText(experiments.getE9P09());
            mE9Cos09.setText(experiments.getE9Cos09());
            mE9TempAmbient09.setText(experiments.getE9TempAmbient09());
            mE9TempEngine09.setText(experiments.getE9TempEngine09());
            mE9T09.setText(experiments.getE9T09());
            mE9U09C.setText(experiments.getE9U09C());
            mE9I09C.setText(experiments.getE9I09C());
            mE9U09Average.setText(experiments.getE9U09Average());
            mE9I09Average.setText(experiments.getE9I09Average());
            mE9U08A.setText(experiments.getE9U08A());
            mE9I08A.setText(experiments.getE9I08A());
            mE9U08B.setText(experiments.getE9U08B());
            mE9I08B.setText(experiments.getE9I08B());
            mE9P08.setText(experiments.getE9P08());
            mE9Cos08.setText(experiments.getE9Cos08());
            mE9TempAmbient08.setText(experiments.getE9TempAmbient08());
            mE9TempEngine08.setText(experiments.getE9TempEngine08());
            mE9T08.setText(experiments.getE9T08());
            mE9U08C.setText(experiments.getE9U08C());
            mE9I08C.setText(experiments.getE9I08C());
            mE9U08Average.setText(experiments.getE9U08Average());
            mE9I08Average.setText(experiments.getE9I08Average());
            mE9U07A.setText(experiments.getE9U07A());
            mE9I07A.setText(experiments.getE9I07A());
            mE9U07B.setText(experiments.getE9U07B());
            mE9I07B.setText(experiments.getE9I07B());
            mE9P07.setText(experiments.getE9P07());
            mE9Cos07.setText(experiments.getE9Cos07());
            mE9TempAmbient07.setText(experiments.getE9TempAmbient07());
            mE9TempEngine07.setText(experiments.getE9TempEngine07());
            mE9T07.setText(experiments.getE9T07());
            mE9U07C.setText(experiments.getE9U07C());
            mE9I07C.setText(experiments.getE9I07C());
            mE9U07Average.setText(experiments.getE9U07Average());
            mE9I07Average.setText(experiments.getE9I07Average());
            mE9U06A.setText(experiments.getE9U06A());
            mE9I06A.setText(experiments.getE9I06A());
            mE9U06B.setText(experiments.getE9U06B());
            mE9I06B.setText(experiments.getE9I06B());
            mE9P06.setText(experiments.getE9P06());
            mE9Cos06.setText(experiments.getE9Cos06());
            mE9TempAmbient06.setText(experiments.getE9TempAmbient06());
            mE9TempEngine06.setText(experiments.getE9TempEngine06());
            mE9T06.setText(experiments.getE9T06());
            mE9U06C.setText(experiments.getE9U06C());
            mE9I06C.setText(experiments.getE9I06C());
            mE9U06Average.setText(experiments.getE9U06Average());
            mE9I06Average.setText(experiments.getE9I06Average());

            mE10IA.setText(experiments.getE10IA());
            mE10UA.setText(experiments.getE10UA());
            mE10IB.setText(experiments.getE10IB());
            mE10UB.setText(experiments.getE10UB());
            mE10S.setText(experiments.getE10S());
            mE10P1.setText(experiments.getE10P1());
            mE10Cos.setText(experiments.getE10Cos());
            mE10M.setText(experiments.getE10M());
            mE10V.setText(experiments.getE10V());
            mE10P2.setText(experiments.getE10P2());
            mE10Nu.setText(experiments.getE10Nu());
            mE10TempAmbient.setText(experiments.getE10TempAmbient());
            mE10TempEngine.setText(experiments.getE10TempEngine());
            mE10Sk.setText(experiments.getE10Sk());
            mE10T.setText(experiments.getE10T());
            mE10IC.setText(experiments.getE10IC());
            mE10UC.setText(experiments.getE10UC());
            mE10IAverage.setText(experiments.getE10IAverage());
            mE10UAverage.setText(experiments.getE10UAverage());
            mE10SAverage.setText(experiments.getE10SAverage());
            mE10P1Average.setText(experiments.getE10P1Average());
            mE10CosAverage.setText(experiments.getE10CosAverage());
            mE10MAverage.setText(experiments.getE10MAverage());
            mE10VAverage.setText(experiments.getE10VAverage());
            mE10P2Average.setText(experiments.getE10P2Average());
            mE10NuAverage.setText(experiments.getE10NuAverage());
            mE10TempEngineAverage.setText(experiments.getE10TempEngineAverage());
            mE10SkAverage.setText(experiments.getE10SkAverage());
            mE10ISpecified.setText(experiments.getE10ISpecified());
            mE10USpecified.setText(experiments.getE10USpecified());
            mE10SSpecified.setText(experiments.getE10SSpecified());
            mE10P1Specified.setText(experiments.getE10P1Specified());
            mE10CosSpecified.setText(experiments.getE10CosSpecified());
            mE10MSpecified.setText(experiments.getE10MSpecified());
            mE10VSpecified.setText(experiments.getE10VSpecified());
            mE10P2Specified.setText(experiments.getE10P2Specified());
            mE10NuSpecified.setText(experiments.getE10NuSpecified());
            mE10TempEngineSpecified.setText(experiments.getE10TempEngineSpecified());
            mE10SkSpecified.setText(experiments.getE10SkSpecified());
            mE10TSpecified.setText(experiments.getE10TSpecified());

            mE11UR.setText(experiments.getE11UR());
            mE11R15.setText(experiments.getE11R15());
            mE11R60.setText(experiments.getE11R60());
            mE11K.setText(experiments.getE11K());
            mE11Temp.setText(experiments.getE11Temp());
            mE11Result.setText(experiments.getE11Result());

            mE12IA.setText(experiments.getE12IA());
            mE12UA.setText(experiments.getE12UA());
            mE12IB.setText(experiments.getE12IB());
            mE12UB.setText(experiments.getE12UB());
            mE12S.setText(experiments.getE12S());
            mE12P1.setText(experiments.getE12P1());
            mE12Cos.setText(experiments.getE12Cos());
            mE12M.setText(experiments.getE12M());
            mE12V.setText(experiments.getE12V());
            mE12P2.setText(experiments.getE12P2());
            mE12Nu.setText(experiments.getE12Nu());
            mE12TempAmbient.setText(experiments.getE12TempAmbient());
            mE12TempEngine.setText(experiments.getE12TempEngine());
            mE12Sk.setText(experiments.getE12Sk());
            mE12T.setText(experiments.getE12T());
            mE12IC.setText(experiments.getE12IC());
            mE12UC.setText(experiments.getE12UC());
            mE12IAverage.setText(experiments.getE12IAverage());
            mE12UAverage.setText(experiments.getE12UAverage());
            mE12SAverage.setText(experiments.getE12SAverage());
            mE12P1Average.setText(experiments.getE12P1Average());
            mE12CosAverage.setText(experiments.getE12CosAverage());
            mE12MAverage.setText(experiments.getE12MAverage());
            mE12VAverage.setText(experiments.getE12VAverage());
            mE12P2Average.setText(experiments.getE12P2Average());
            mE12NuAverage.setText(experiments.getE12NuAverage());
            mE12TempEngineAverage.setText(experiments.getE12TempEngineAverage());
            mE12SkAverage.setText(experiments.getE12SkAverage());
            mE12ISpecified.setText(experiments.getE12ISpecified());
            mE12USpecified.setText(experiments.getE12USpecified());
            mE12SSpecified.setText(experiments.getE12SSpecified());
            mE12P1Specified.setText(experiments.getE12P1Specified());
            mE12CosSpecified.setText(experiments.getE12CosSpecified());
            mE12MSpecified.setText(experiments.getE12MSpecified());
            mE12VSpecified.setText(experiments.getE12VSpecified());
            mE12P2Specified.setText(experiments.getE12P2Specified());
            mE12NuSpecified.setText(experiments.getE12NuSpecified());
            mE12TempEngineSpecified.setText(experiments.getE12TempEngineSpecified());
            mE12SkSpecified.setText(experiments.getE12SkSpecified());
            mE12TSpecified.setText(experiments.getE12TSpecified());

            mE13U08A.setText(experiments.getE13U08A());
            mE13I08A.setText(experiments.getE13I08A());
            mE13U08B.setText(experiments.getE13U08B());
            mE13I08B.setText(experiments.getE13I08B());
            mE13S08.setText(experiments.getE13S08());
            mE13P08.setText(experiments.getE13P08());
            mE13V08.setText(experiments.getE13V08());
            mE13M08.setText(experiments.getE13M08());
            mE13F08.setText(experiments.getE13F08());
            mE13Temp08.setText(experiments.getE13Temp08());
            mE13T08.setText(experiments.getE13T08());
            mE13U08C.setText(experiments.getE13U08C());
            mE13I08C.setText(experiments.getE13I08C());
            mE13U08Average.setText(experiments.getE13U08Average());
            mE13I08Average.setText(experiments.getE13I08Average());
            mE13U11A.setText(experiments.getE13U11A());
            mE13I11A.setText(experiments.getE13I11A());
            mE13U11B.setText(experiments.getE13U11B());
            mE13I11B.setText(experiments.getE13I11B());
            mE13S11.setText(experiments.getE13S11());
            mE13P11.setText(experiments.getE13P11());
            mE13V11.setText(experiments.getE13V11());
            mE13M11.setText(experiments.getE13M11());
            mE13F11.setText(experiments.getE13F11());
            mE13Temp11.setText(experiments.getE13Temp11());
            mE13T11.setText(experiments.getE13T11());
            mE13U11C.setText(experiments.getE13U11C());
            mE13I11C.setText(experiments.getE13I11C());
            mE13U11Average.setText(experiments.getE13U11Average());
            mE13I11Average.setText(experiments.getE13I11Average());

            mE14M.setText(experiments.getE14M());
            mE14V.setText(experiments.getE14V());

            mE15I.setText(experiments.getE15I());
            mE15M.setText(experiments.getE15M());

            mE16IB.setText(experiments.getE16IB());
            mE16UB.setText(experiments.getE16UB());
            mE16S.setText(experiments.getE16S());
            mE16P1.setText(experiments.getE16P1());
            mE16Cos.setText(experiments.getE16Cos());
            mE16M.setText(experiments.getE16M());
            mE16V.setText(experiments.getE16V());
            mE16ISpecified.setText(experiments.getE16ISpecified());
            mE16USpecified.setText(experiments.getE16USpecified());
            mE16SSpecified.setText(experiments.getE16SSpecified());
            mE16P1Specified.setText(experiments.getE16P1Specified());
            mE16CosSpecified.setText(experiments.getE16CosSpecified());
            mE16MSpecified.setText(experiments.getE16MSpecified());
            mE16VSpecified.setText(experiments.getE16VSpecified());

            mE17Ab.setText(experiments.getE17Ab());
            mE17Bc.setText(experiments.getE17Bc());
            mE17Ac.setText(experiments.getE17Ac());
            mE17AverageR.setText(experiments.getE17AverageR());
            mE17Temp.setText(experiments.getE17Temp());
            mE17Result.setText(experiments.getE17Result());
            mE17AverageRSpecified.setText(experiments.getE17AverageRSpecified());
        }
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void clearResults() {
        ExperimentsHolder.setExperiments(new Experiments());
        refillResults();
        ExperimentsHolder.setExperiments(null);
    }

    @Override
    public void showSaveLayout() {
        mReviewLayout.setVisibility(View.GONE);
        mSaveLayout.setVisibility(View.VISIBLE);
        changeTabToProtocol();
        switchTabState(mTabs, SUBJECT_TAB_INDEX, false, mTabHost);
        switchTabState(mTabs, EXPERIMENTS_TAB_INDEX, false, mTabHost);
    }

    @Override
    public void showReviewLayout() {
        mSaveLayout.setVisibility(View.GONE);
        mReviewLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void initializeSubjectSelector() {
        setSpinnerAdapter(this, mSubjectsSelector, mMainPresenter.getAllSubjects());
        hideSubjects();
    }

    private void initializePlatformSelector() {
        setSpinnerAdapterFromResources(this, mPlatformsSelector, R.array.platforms);
    }

    private void initializeExperimentsSelector() {
        setSpinnerAdapterFromResources(this, mExperimentsSelector, R.array.experiments);
    }

    @OnItemSelected(R.id.protocols)
    public void onProtocolSelected(Spinner view) {
        mMainPresenter.setProtocolForInteraction((Protocol) view.getSelectedItem());
    }

    @OnItemSelected(R.id.experiments_selector)
    public void onExperimentSelected(Spinner view) {
        mMainPresenter.setExperimentForDisplay((String) view.getSelectedItem());
    }

    @Override
    public void finishApplication() {
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        finishApplication();
    }

    @OnCheckedChanged(R.id.serial_number_enter)
    public void onCheckedSerialNumberEnterChanged(boolean state) {
        mMainPresenter.serialNumberEnterClicked(state);
    }

    @OnCheckedChanged(R.id.select_all)
    public void onCheckedSelectAllChanged(boolean state) {
        mMainPresenter.selectAllClicked(state);
    }

    @OnClick({R.id.exit,
            R.id.serial_number_enter,
            R.id.subject_enter,
            R.id.subject_cancel,
            R.id.subject_next,
            R.id.start_experiments,
            R.id.save,
            R.id.preview,
            R.id.save_on_flash})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exit:
                mMainPresenter.exitPressed();
                break;
            case R.id.subject_enter:
                mMainPresenter.subjectEnter();
                break;
            case R.id.subject_cancel:
                mMainPresenter.subjectCancel();
                uncheckSerialNumberEnter();
                break;
            case R.id.subject_next:
                mMainPresenter.subjectNext();
                break;
            case R.id.start_experiments:
                mMainPresenter.startFirstExperiment();
                break;
            case R.id.save:
                if (fieldsAreFilled()) {
                    mMainPresenter.saveProtocolInDB(
                            (String) mPosition1.getSelectedItem(),
                            mPosition1Number.getText().toString(),
                            mPosition1FullName.getText().toString(),
                            (String) mPosition2.getSelectedItem(),
                            mPosition2Number.getText().toString(),
                            mPosition2FullName.getText().toString());
                    mMainPresenter.setNeedToSave(false);
                    Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show();
                    mMainPresenter.protocolTabSelected(MainActivity.this, mProtocols,
                            getStartDate(mSelectDate.getText().toString()),
                            getEndDate(mSelectDate.getText().toString()));
                    mMainPresenter.subjectCancel();
                    uncheckSerialNumberEnter();
                    switchTabState(mTabs, SUBJECT_TAB_INDEX, true, mTabHost);
                    switchTabState(mTabs, EXPERIMENTS_TAB_INDEX, true, mTabHost);
                    EventsHolder.addLine(String.format("%s Результаты сохранены", mTimeFormat.format(System.currentTimeMillis())));
                } else {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.preview:
                Logging.preview(this, mMainPresenter.getProtocolForInteraction());
                break;
            case R.id.save_on_flash:
                Logging.saveFileOnFlashMassStorage(this, mMainPresenter.getProtocolForInteraction());
                break;
        }
    }

    private boolean fieldsAreFilled() {
        return !isEmpty(mPosition1Number.getText().toString()) &&
                !isEmpty(mPosition1FullName.getText().toString()) &&
                !isEmpty(mPosition2Number.getText().toString()) &&
                !isEmpty(mPosition2FullName.getText().toString());
    }

    @OnItemSelected(R.id.subjects_selector)
    public void onSubjectSelected(AdapterView<?> view) {
        Subject selectedSubject = (Subject) view.getSelectedItem();
        mSubjectTitle.setText(selectedSubject.toString());
        mMainPresenter.subjectSelected(selectedSubject);
    }

    @Override
    public String getSerialNumber() {
        return mSerialNumber.getText().toString();
    }

    @Override
    public void showSubjects(String serialNumber) {
        mSerialNumberTitle.setText(serialNumber);
        disableView(mSerialNumber);
        mSubjectsSelectorTitle.setVisibility(View.VISIBLE);
        mSubjectsSelector.setVisibility(View.VISIBLE);
        mPlatformsSelectorTitle.setVisibility(View.VISIBLE);
        mPlatformsSelector.setVisibility(View.VISIBLE);
        mSubjectEnter.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSubjects() {
        mSerialNumberTitle.setText("");
        mSubjectTitle.setText("");
        enableView(mSerialNumber);
        mSubjectsSelectorTitle.setVisibility(View.GONE);
        mSubjectsSelector.setVisibility(View.GONE);
        mPlatformsSelectorTitle.setVisibility(View.GONE);
        mPlatformsSelector.setVisibility(View.GONE);
        mSubjectEnter.setVisibility(View.GONE);
    }

    @Override
    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void uncheckSerialNumberEnter() {
        mSerialNumberEnter.setChecked(false);
    }

    @Override
    public void disableSubjectTab() {
        disableView(mSerialNumberEnter);
        disableView(mSubjectsSelector);
        disableView(mPlatformsSelector);
        disableView(mSubjectEnter);
        mSubjectCancel.setVisibility(View.VISIBLE);
        mSubjectNext.setVisibility(View.VISIBLE);
    }

    @Override
    public void isPlatformOneSelected() {
        String selectedPlatform = (String) mPlatformsSelector.getSelectedItem();
        boolean selectedPlatformOne = selectedPlatform.equals("Платформа 1");
        mModel.setPlatformOneSelected(selectedPlatformOne);
        mSubjectTitle.append(selectedPlatformOne ? "(Платформа 1)" : "(Платформа 2)");
    }

    @Override
    public void enableSubjectTab() {
        enableView(mSerialNumberEnter);
        enableView(mSubjectsSelector);
        enableView(mPlatformsSelector);
        enableView(mSubjectEnter);
        mSubjectCancel.setVisibility(View.GONE);
        mSubjectNext.setVisibility(View.GONE);
    }

    @Override
    public void changeTabToExperiments() {
        EventsHolder.addLine(String.format("%s Испытания начались", mTimeFormat.format(System.currentTimeMillis())));
        setViewAndChildrenVisibility(mTabExperiments, View.VISIBLE);
        mTabHost.setCurrentTab(EXPERIMENTS_TAB_INDEX);
    }

    private void initializeDateButton() {
        mSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = DatePickerFragment.newInstance(mSelectDate.getText().toString());
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });
        mSelectDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSpinnerAdapter(MainActivity.this, mProtocols,
                        mModel.getProtocolsByDateFromDB(getStartDate(mSelectDate.getText().toString()),
                                getEndDate(mSelectDate.getText().toString())));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mSelectDate.setText(mDateFormat.format(System.currentTimeMillis()));
    }

    private void initializeExperimentsList() {
        setListViewAdapterFromResources(this, mExperimentsList, R.array.experiments);
    }

    private void changeTabToProtocol() {
        mTabHost.setCurrentTab(PROTOCOL_TAB_INDEX);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public static DatePickerFragment newInstance(String date) {
            DatePickerFragment f = new DatePickerFragment();
            Bundle args = new Bundle();
            args.putString("date", date);
            f.setArguments(args);
            return f;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(new SimpleDateFormat("dd.MM.yyyy", new Locale("ru")).parse(getArguments().getString("date")));
            } catch (ParseException ignored) {
            }
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date = (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth) + "." + (++month < 10 ? "0" + month : month) + "." + year;
            ((MainActivity) getActivity()).mSelectDate.setText(date);
        }
    }

    @Override
    public void hideExperimentsViews() {
        setViewAndChildrenVisibility(mTabExperiments, View.GONE);
    }

    @Override
    public void setNextExperimentType(int experimentType) {
        setExperimentType((String) mExperimentsList.getAdapter().getItem(experimentType));
    }

    public void setExperimentType(String experimentType) {
        mExperimentType = experimentType;
        showAlertDialogOfPeople(experimentType);
    }

    public void showAlertDialogOfPeople(final String experimentType) {
        new AlertDialog.Builder(this)
                .setTitle(experimentType)
                .setMessage("В испытательной зоне есть люди?")
                .setIcon(R.drawable.ic_warning_black_48dp)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAlertDialogOfPeople(experimentType);
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAlertDialogOfStart(experimentType);
                    }
                })
                .create()
                .show();
    }

    public void showAlertDialogOfStart(String experimentType) {
        new AlertDialog.Builder(this)
                .setTitle(experimentType)
                .setMessage("Приступить к испытанию?")
                .setIcon(R.drawable.ic_help_outline_black_48dp)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startNextExperiment();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainPresenter.startNextExperiment();
                    }
                })
                .create()
                .show();
    }

    public void startNextExperiment() {
        EventsHolder.addLine(String.format("%s Испытание \"%s\" началось", mTimeFormat.format(System.currentTimeMillis()), mExperimentType));
        Intent intent = new Intent();
        intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_NAME, mExperimentType);
        intent.putExtra(OUTPUT_PARAMETER.PLATFORM_ONE_SELECTED, mModel.isPlatformOneSelected());
        switch (mExperimentType) {
            case EXPERIMENT_1:
                intent.setClass(this, Experiment1Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.NUM_OF_STAGES_PERFORMANCE, mModel.getNumOfStagesPerformance()); // Количество ступеней
                intent.putExtra(OUTPUT_PARAMETER.Z1, mModel.getZ1Performance()); // Параметр шкива ОИ
                intent.putExtra(OUTPUT_PARAMETER.Z2, mModel.getZ2Performance()); // Параметр шкива НМ
                intent.putExtra(OUTPUT_PARAMETER.V1, mModel.getVN()); // Номинальная частота вращения
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_TORQUE, mModel.getMN()); // Номинальный момент
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME_IDLE, mModel.getTBreakInPerformance()); // Время обкатки на ХХ
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME, mModel.getTPerformance()); // Время выдержки под номиональной нагрузкой
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_AMPERAGE, mModel.getIN()); // Номинальный ток
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN()); // Номинальное напряжение
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_P2, mModel.getPN()); // Номинальный P2
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_EFF, mModel.getEfficiencyN()); // Номинальный КПД
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_SK, mModel.getSN()); // Номинальное скольжение
                startActivityForResult(intent, EXPERIMENT_1_ID);
                break;
            case EXPERIMENT_2:
                intent.setClass(this, Experiment2Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.Z1, mModel.getZ1Performance()); // Параметр шкива ОИ
                intent.putExtra(OUTPUT_PARAMETER.Z2, mModel.getZ2Performance()); // Параметр шкива НМ
                intent.putExtra(OUTPUT_PARAMETER.V1, mModel.getVN()); // Номинальная частота вращения
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_TORQUE, mModel.getMN() * 1.6f); // Номинальный момент * 1.6f
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME_IDLE, 10); // Время обкатки на ХХ
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME, 15); // Время выдержки под номиональной нагрузкой
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_AMPERAGE, mModel.getIN()); // Номинальный ток
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN()); // Номинальное напряжение
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_P2, mModel.getPN()); // Номинальный P2
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_EFF, mModel.getEfficiencyN()); // Номинальный КПД
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_SK, mModel.getSN()); // Номинальное скольжение
                startActivityForResult(intent, EXPERIMENT_2_ID);
                break;
            case EXPERIMENT_3:
                intent.setClass(this, Experiment3Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.Z1, mModel.getZ1Performance()); // Параметр шкива ОИ
                intent.putExtra(OUTPUT_PARAMETER.Z2, mModel.getZ2Performance()); // Параметр шкива НМ
                intent.putExtra(OUTPUT_PARAMETER.V1, mModel.getVN()); // Номинальная частота вращения
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_TORQUE, mModel.getMN()); // Номинальный момент
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME_IDLE, 10); // Время обкатки на ХХ
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME, 60); // Время выдержки под номиональной нагрузкой
                intent.putExtra(OUTPUT_PARAMETER.K_OVERLOAD_I, mModel.getKOverloadI()); // Номинальный ток * коэф перегрузки
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN()); // Номинальное напряжение
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_P2, mModel.getPN()); // Номинальный P2
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_EFF, mModel.getEfficiencyN()); // Номинальный КПД
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_SK, mModel.getSN()); // Номинальное скольжение
                startActivityForResult(intent, EXPERIMENT_3_ID);
                break;
            case EXPERIMENT_4:
                intent.setClass(this, Experiment4Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME, 60); // Время выдержки под номиональной нагрузкой
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                startActivityForResult(intent, EXPERIMENT_4_ID);
                break;
            case EXPERIMENT_5:
                intent.setClass(this, Experiment5Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_R, mModel.getRIkas()); // Среднее сопротивление ИКАС
                startActivityForResult(intent, EXPERIMENT_5_ID);
                break;
            case EXPERIMENT_6:
                intent.setClass(this, Experiment6Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUViu()); // Напряжение ВИУ
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME, mModel.getTViu()); // Время выдержки под номиональной нагрузкой
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_I, mModel.getIViu()); // Ток ВИУ
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                startActivityForResult(intent, EXPERIMENT_6_ID);
                break;
            case EXPERIMENT_7:
                intent.setClass(this, Experiment7Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, (float) mModel.getUN()); // Номинальное напряжение ХХ
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_T1, mModel.getTBreakInIdle()); // Время обкатки ХХ
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_T2, mModel.getTOnStageIdle()); // Время нахождения на каждой ступени ХХ
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.NUM_OF_STAGES_IDLE, mModel.getNumOfStagesIdle()); // Количество ступеней
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_R_TYPE, mModel.getIkasRType()); // Номер обмотки (1-3)
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_R, mModel.getRIkas()); // Среднее сопротивление ИКАС
                startActivityForResult(intent, EXPERIMENT_7_ID);
                break;
            case EXPERIMENT_8:
                intent.setClass(this, Experiment8Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN()); // Номинальное напряжение
                startActivityForResult(intent, EXPERIMENT_8_ID);
                break;
            case EXPERIMENT_9:
                intent.setClass(this, Experiment9Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN() / 3.8f); // Номинальное напряжение / 3.8f
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_T, mModel.getTOnStageSc()); // Время нахождения на каждой ступени КЗ
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.NUM_OF_STAGES_SC, mModel.getNumOfStagesSc()); // Количество ступеней
                startActivityForResult(intent, EXPERIMENT_9_ID);
                break;
            case EXPERIMENT_10:
                intent.setClass(this, Experiment10Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.Z1, mModel.getZ1Performance()); // Параметр шкива ОИ
                intent.putExtra(OUTPUT_PARAMETER.Z2, mModel.getZ2Performance()); // Параметр шкива НМ
                intent.putExtra(OUTPUT_PARAMETER.V1, mModel.getVN()); // Номинальная частота вращения
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_TORQUE, mModel.getMN()); // Номинальный момент * 1.6f
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME_IDLE, 10); // Время обкатки на ХХ
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_AMPERAGE, mModel.getIN()); // Номинальный ток
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN()); // Номинальное напряжение
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_P2, mModel.getPN()); // Номинальный P2
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_EFF, mModel.getEfficiencyN()); // Номинальный КПД
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_SK, mModel.getSN()); // Номинальное скольжение
                startActivityForResult(intent, EXPERIMENT_10_ID);
                break;
            case EXPERIMENT_11:
                intent.setClass(this, Experiment11Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUMgr());// Напряжение МГР
                startActivityForResult(intent, EXPERIMENT_11_ID);
                break;
            case EXPERIMENT_12:
                intent.setClass(this, Experiment12Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.Z1, mModel.getZ1Performance()); // Параметр шкива ОИ
                intent.putExtra(OUTPUT_PARAMETER.Z2, mModel.getZ2Performance()); // Параметр шкива НМ
                intent.putExtra(OUTPUT_PARAMETER.V1, mModel.getVN()); // Номинальная частота вращения
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_TORQUE, mModel.getMN()); // Номинальный момент
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME_IDLE, 10); // Время обкатки на ХХ
                intent.putExtra(OUTPUT_PARAMETER.EXPERIMENT_TIME, mModel.getTHeating()); // Время выдержки под номиональной нагрузкой
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_AMPERAGE, mModel.getIN()); // Номинальный ток
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN()); // Номинальное напряжение
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_P2, mModel.getPN()); // Номинальный P2
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_EFF, mModel.getEfficiencyN()); // Номинальный КПД
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_SK, mModel.getSN()); // Номинальное скольжение
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_TEMP_HEATING, mModel.getTempHeating()); // Номинальная температура
                startActivityForResult(intent, EXPERIMENT_12_ID);
                break;
            case EXPERIMENT_13:
                intent.setClass(this, Experiment13Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, (float) mModel.getUN()); // Номинальное напряжение
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                startActivityForResult(intent, EXPERIMENT_13_ID);
                break;
            case EXPERIMENT_14:
                intent.setClass(this, Experiment14Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.V1, mModel.getVN()); // Номинальная частота вращения
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_TORQUE, mModel.getMN()); // Номинальный момент
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN()); // Номинальное напряжение
                startActivityForResult(intent, EXPERIMENT_14_ID);
                break;
            case EXPERIMENT_15:
                intent.setClass(this, Experiment15Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_U, mModel.getUN()); // Номинальное напряжение
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_FREQUENCY, mModel.getFN()); // Номинальная частота сети
                startActivityForResult(intent, EXPERIMENT_15_ID);
                break;
            case EXPERIMENT_16:
                intent.setClass(this, Experiment16Activity.class);
                startActivityForResult(intent, EXPERIMENT_16_ID);
                break;
            case EXPERIMENT_17:
                intent.setClass(this, Experiment17Activity.class);
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_R, mModel.getRIkas()); // Среднее сопротивление ИКАС
                intent.putExtra(OUTPUT_PARAMETER.SPECIFIED_R_TYPE, mModel.getIkasRType()); // Номер обмотки (1-3)
                startActivityForResult(intent, EXPERIMENT_17_ID);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        EventsHolder.addLine(String.format("%s Испытание \"%s\" завершилось", mTimeFormat.format(System.currentTimeMillis()), mExperimentType));
        switch (requestCode) {
            case EXPERIMENT_1_ID:
                mModel.setP2R(data.getFloatExtra(INPUT_PARAMETER.P2_R, DEFAULT_VALUE_FLOAT));
                mModel.setUR(data.getFloatExtra(INPUT_PARAMETER.U_R, DEFAULT_VALUE_FLOAT));
                mModel.setIR(data.getFloatExtra(INPUT_PARAMETER.I_R, DEFAULT_VALUE_FLOAT));
                mModel.setVR(data.getFloatExtra(INPUT_PARAMETER.V_R, DEFAULT_VALUE_FLOAT));
                mModel.setSR(data.getFloatExtra(INPUT_PARAMETER.S_R, DEFAULT_VALUE_FLOAT));
                mModel.setNuR(data.getFloatExtra(INPUT_PARAMETER.NU_R, DEFAULT_VALUE_FLOAT));
                mModel.setCosR(data.getFloatExtra(INPUT_PARAMETER.COS_R, DEFAULT_VALUE_FLOAT));
                mModel.setP1R(data.getFloatExtra(INPUT_PARAMETER.P1_R, DEFAULT_VALUE_FLOAT));
                mModel.setMR(data.getFloatExtra(INPUT_PARAMETER.M_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_2_ID:
                break;
            case EXPERIMENT_3_ID:
                mModel.setSpecifiedIOverloadR(data.getFloatExtra(INPUT_PARAMETER.SPECIFIED_I_OVERLOAD_R, DEFAULT_VALUE_FLOAT));
                mModel.setIOverloadR(data.getFloatExtra(INPUT_PARAMETER.I_OVERLOAD_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_4_ID:
                mModel.setUMVZ1R(data.getFloatExtra(INPUT_PARAMETER.I_MVZ1_R, DEFAULT_VALUE_FLOAT));
                mModel.setUMVZ2R(data.getFloatExtra(INPUT_PARAMETER.I_MVZ2_R, DEFAULT_VALUE_FLOAT));
                mModel.setUMVZ3R(data.getFloatExtra(INPUT_PARAMETER.I_MVZ3_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_5_ID:
                mModel.setIkasRColdR(data.getFloatExtra(INPUT_PARAMETER.IKAS_R_COLD_R, DEFAULT_VALUE_FLOAT));
                mModel.setIkasR20R(data.getFloatExtra(INPUT_PARAMETER.IKAS_R_20_R, DEFAULT_VALUE_FLOAT));
                mModel.setIkasRTypeR(data.getIntExtra(INPUT_PARAMETER.IKAS_R_TYPE_R, DEFAULT_VALUE_INTEGER));
                break;
            case EXPERIMENT_6_ID:
                mModel.setUViuR(data.getFloatExtra(INPUT_PARAMETER.U_VIU_R, DEFAULT_VALUE_FLOAT));
                mModel.setTViuR(data.getFloatExtra(INPUT_PARAMETER.T_VIU_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_7_ID:
                mModel.setI13IdleR(data.getFloatExtra(INPUT_PARAMETER.I13_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP13IdleR(data.getFloatExtra(INPUT_PARAMETER.P13_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setI12IdleR(data.getFloatExtra(INPUT_PARAMETER.I12_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP12IdleR(data.getFloatExtra(INPUT_PARAMETER.P12_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setI11IdleR(data.getFloatExtra(INPUT_PARAMETER.I11_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP11IdleR(data.getFloatExtra(INPUT_PARAMETER.P11_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setI10IdleR(data.getFloatExtra(INPUT_PARAMETER.I10_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP10IdleR(data.getFloatExtra(INPUT_PARAMETER.P10_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setI09IdleR(data.getFloatExtra(INPUT_PARAMETER.I09_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP09IdleR(data.getFloatExtra(INPUT_PARAMETER.P09_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setI08IdleR(data.getFloatExtra(INPUT_PARAMETER.I08_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP08IdleR(data.getFloatExtra(INPUT_PARAMETER.P08_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setI07IdleR(data.getFloatExtra(INPUT_PARAMETER.I07_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP07IdleR(data.getFloatExtra(INPUT_PARAMETER.P07_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setU07IdleR(data.getFloatExtra(INPUT_PARAMETER.U07_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setI06IdleR(data.getFloatExtra(INPUT_PARAMETER.I06_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP06IdleR(data.getFloatExtra(INPUT_PARAMETER.P06_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setU06IdleR(data.getFloatExtra(INPUT_PARAMETER.U06_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setI05IdleR(data.getFloatExtra(INPUT_PARAMETER.I05_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setP05IdleR(data.getFloatExtra(INPUT_PARAMETER.P05_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setU05IdleR(data.getFloatExtra(INPUT_PARAMETER.U05_IDLE_R, DEFAULT_VALUE_FLOAT));
                mModel.setPStR(data.getDoubleExtra(INPUT_PARAMETER.P_ST_R, DEFAULT_VALUE_DOUBLE));
                mModel.setPMechR(data.getDoubleExtra(INPUT_PARAMETER.P_MECH_R, DEFAULT_VALUE_DOUBLE));
                break;
            case EXPERIMENT_8_ID:
                mModel.setVOverloadR(data.getFloatExtra(INPUT_PARAMETER.V_OVERLOAD_R, DEFAULT_VALUE_FLOAT));
                mModel.setTOverloadR(data.getFloatExtra(INPUT_PARAMETER.T_OVERLOAD_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_9_ID:
                mModel.setI10SCR(data.getFloatExtra(INPUT_PARAMETER.I10_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setP10SCR(data.getFloatExtra(INPUT_PARAMETER.P10_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setI09SCR(data.getFloatExtra(INPUT_PARAMETER.I09_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setP09SCR(data.getFloatExtra(INPUT_PARAMETER.P09_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setI08SCR(data.getFloatExtra(INPUT_PARAMETER.I08_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setP08SCR(data.getFloatExtra(INPUT_PARAMETER.P08_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setI07SCR(data.getFloatExtra(INPUT_PARAMETER.I07_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setP07SCR(data.getFloatExtra(INPUT_PARAMETER.P07_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setI06SCR(data.getFloatExtra(INPUT_PARAMETER.I06_SC_R, DEFAULT_VALUE_FLOAT));
                mModel.setP06SCR(data.getFloatExtra(INPUT_PARAMETER.P06_SC_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_10_ID:
                mModel.setMMaxR(data.getFloatExtra(INPUT_PARAMETER.M_MAX_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_11_ID:
                mModel.setMgrR(data.getFloatExtra(INPUT_PARAMETER.MGR_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_12_ID:
                mModel.setTempEngineR(data.getFloatExtra(INPUT_PARAMETER.TEMP_ENGINE_R, DEFAULT_VALUE_FLOAT));
                mModel.setTempAmbientR(data.getFloatExtra(INPUT_PARAMETER.TEMP_AMBIENT_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_13_ID:
                break;
            case EXPERIMENT_14_ID:
                mModel.setMMinR(data.getFloatExtra(INPUT_PARAMETER.M_MIN_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_15_ID:
                mModel.setMStartR(data.getFloatExtra(INPUT_PARAMETER.M_START_R, DEFAULT_VALUE_FLOAT));
                mModel.setIStartR(data.getFloatExtra(INPUT_PARAMETER.I_START_R, DEFAULT_VALUE_FLOAT));
                break;
            case EXPERIMENT_16_ID:
                break;
            case EXPERIMENT_17_ID:
                mModel.setIkasRHotR(data.getFloatExtra(INPUT_PARAMETER.IKAS_R_HOT_R, DEFAULT_VALUE_FLOAT));
                break;
        }

        if (requestCode != EXPERIMENT_6_ID) {
            mMainPresenter.startNextExperiment();
        } else {
            showAlertDialogOfVIU();
        }
    }

    private void showAlertDialogOfVIU() {
        new AlertDialog.Builder(this)
                .setTitle("Внимание")
                .setMessage("Отключите высоковольтный крокодил от объекта испытания")
                .setIcon(R.drawable.ic_warning_black_48dp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainPresenter.startNextExperiment();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean atLeastOneExperimentWasSelected() {
        for (int i = 0; i < mExperimentsList.getAdapter().getCount(); i++) {
            if (mExperimentsList.isItemChecked(i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void selectAllExperiments() {
        for (int i = 0; i < mExperimentsList.getAdapter().getCount(); i++) {
            mExperimentsList.setItemChecked(i, true);
        }
    }

    @Override
    public void unselectAllExperiments() {
        for (int i = 0; i < mExperimentsList.getAdapter().getCount(); i++) {
            mExperimentsList.setItemChecked(i, false);
        }
    }

    @Override
    public void invalidate() {
        mExperimentsList.invalidateViews();
    }

    @Override
    public void showAllExperimentsCompletedDialog() {
        EventsHolder.addLine(String.format("%s Все выбранные испытания завершились", mTimeFormat.format(System.currentTimeMillis())));
        new AlertDialog.Builder(this)
                .setTitle("Все испытания закончены")
                .setMessage("Желаете сохранить результаты?")
                .setIcon(R.drawable.ic_save_black_48dp)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainPresenter.saveResults();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    @Override
    public void hideAllExperiments() {
        mExperiment1.setVisibility(View.GONE);
        mExperiment2.setVisibility(View.GONE);
        mExperiment3.setVisibility(View.GONE);
        mExperiment4.setVisibility(View.GONE);
        mExperiment5.setVisibility(View.GONE);
        mExperiment6.setVisibility(View.GONE);
        mExperiment7.setVisibility(View.GONE);
        mExperiment8.setVisibility(View.GONE);
        mExperiment9.setVisibility(View.GONE);
        mExperiment10.setVisibility(View.GONE);
        mExperiment11.setVisibility(View.GONE);
        mExperiment12.setVisibility(View.GONE);
        mExperiment13.setVisibility(View.GONE);
        mExperiment14.setVisibility(View.GONE);
        mExperiment15.setVisibility(View.GONE);
        mExperiment16.setVisibility(View.GONE);
        mExperiment17.setVisibility(View.GONE);
    }

    @Override
    public void show1Experiment() {
        mExperiment1.setVisibility(View.VISIBLE);
    }

    @Override
    public void show2Experiment() {
        mExperiment2.setVisibility(View.VISIBLE);
    }

    @Override
    public void show3Experiment() {
        mExperiment3.setVisibility(View.VISIBLE);
    }

    @Override
    public void show4Experiment() {
        mExperiment4.setVisibility(View.VISIBLE);
    }

    @Override
    public void show5Experiment() {
        mExperiment5.setVisibility(View.VISIBLE);
    }

    @Override
    public void show6Experiment() {
        mExperiment6.setVisibility(View.VISIBLE);
    }

    @Override
    public void show7Experiment() {
        mExperiment7.setVisibility(View.VISIBLE);
    }

    @Override
    public void show8Experiment() {
        mExperiment8.setVisibility(View.VISIBLE);
    }

    @Override
    public void show9Experiment() {
        mExperiment9.setVisibility(View.VISIBLE);
    }

    @Override
    public void show10Experiment() {
        mExperiment10.setVisibility(View.VISIBLE);
    }

    @Override
    public void show11Experiment() {
        mExperiment11.setVisibility(View.VISIBLE);
    }

    @Override
    public void show12Experiment() {
        mExperiment12.setVisibility(View.VISIBLE);
    }

    @Override
    public void show13Experiment() {
        mExperiment13.setVisibility(View.VISIBLE);
    }

    @Override
    public void show14Experiment() {
        mExperiment14.setVisibility(View.VISIBLE);
    }

    @Override
    public void show15Experiment() {
        mExperiment15.setVisibility(View.VISIBLE);
    }

    @Override
    public void show16Experiment() {
        mExperiment16.setVisibility(View.VISIBLE);
    }

    @Override
    public void show17Experiment() {
        mExperiment17.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFoundProtocolDialog(final Protocol protocol) {
        new AlertDialog.Builder(this)
                .setTitle("Испытания ОИ с данным заводским номером уже проводились")
                .setMessage("Выберите дальнейшее действие")
                .setIcon(R.drawable.ic_help_outline_black_48dp)
                .setPositiveButton("Продолжить испытания", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainPresenter.continueProtocolSelected(protocol);
                    }
                })
                .setNegativeButton("Начать испытания заново", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMainPresenter.clearProtocolSelected(protocol);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void showNames(String serialNumber, String subjectName) {
        mSerialNumberTitle.setText(serialNumber);
        mSubjectTitle.setText(subjectName);
    }

    @Override
    public void showNextCancelButtons() {
        disableView(mSerialNumber);
        disableView(mSerialNumberEnter);
        mSubjectCancel.setVisibility(View.VISIBLE);
        mSubjectNext.setVisibility(View.VISIBLE);
    }
}
