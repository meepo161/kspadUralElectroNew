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
import ru.avem.kspad.communication.devices.trm201.TRM201Model;
import ru.avem.kspad.communication.devices.veha_t.VEHATModel;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;

import static ru.avem.kspad.communication.devices.Device.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.Device.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.Device.PM130_ID;
import static ru.avem.kspad.communication.devices.Device.TRM201_ID;
import static ru.avem.kspad.communication.devices.Device.VEHA_T_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment8Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Испытание при повышенной частоте вращения";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int STATE_40_TO_5_MULTIPLIER = 40 / 5;
    private static final int STATE_5_TO_5_MULTIPLIER = 5 / 5;

    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.u_a)
    TextView mUACell;
    @BindView(R.id.i_a)
    TextView mIACell;
    @BindView(R.id.u_b)
    TextView mUBCell;
    @BindView(R.id.i_b)
    TextView mIBCell;
    @BindView(R.id.p)
    TextView mPCell;
    @BindView(R.id.cos)
    TextView mCosCell;
    @BindView(R.id.v)
    TextView mVCell;
    @BindView(R.id.temp)
    TextView mTempCell;
    @BindView(R.id.t)
    TextView mTCell;
    @BindView(R.id.u_c)
    TextView mUCCell;
    @BindView(R.id.i_c)
    TextView mICCell;
    @BindView(R.id.i_average)
    TextView mIAverageCell;
    @BindView(R.id.u_average)
    TextView mUAverageCell;

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
    private boolean mNeedToSave;
    private boolean mPlatformOneSelected;

    private float mSpecifiedFrequency;
    private int mIntSpecifiedFrequencyK100;
    private int mSpecifiedU;
    private int mSpecifiedUK10;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mVEHATResponding;
    private float mV = -1f;

    private boolean mFRA800ObjectResponding;
    private boolean mFRA800ObjectReady;

    private boolean mPM130Responding;
    private float mUA;
    private float mUB;
    private float mUC;
    private float mUAverage;
    private boolean m200to5State;
    private boolean m40to5State;
    private boolean m5to5State;
    private float mIA;
    private float mIB;
    private float mIC;
    private float mIAverage;
    private float mP;
    private float mCos;

    private boolean mTRM201Responding;
    private float mTemp = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment8);
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
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY) != 0) {
                mSpecifiedFrequency = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_FREQUENCY);
                mIntSpecifiedFrequencyK100 = (int) (mSpecifiedFrequency * 100);
            } else {
                throw new NullPointerException("Не передано specifiedFrequency");
            }
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U) != 0) {
                mSpecifiedU = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U);
                mSpecifiedUK10 = mSpecifiedU * 10;
            } else {
                throw new NullPointerException("Не передано specifiedU");
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
            mNeedToSave = true;
            setFRA800ObjectReady(false);
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setVEHATResponding(true);
            setFRA800ObjectResponding(true);
            setPM130Responding(true);
            setTRM201Responding(true);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            changeTextOfView(mStatus, "Испытание началось");
            mDevicesController.initDevices8Group();
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(100);
            }
            while (isExperimentStart() && !mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }
            mDevicesController.initDevices8Group();
            while (isExperimentStart() && !isDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, getNotRespondingDevicesString("Нет связи с устройствами"));
                sleep(100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom8To9Group();
                m200to5State = true;
                sleep(500);
                mDevicesController.setObjectParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.startObject();
                sleep(2000);
            }

            while (isExperimentStart() && !mFRA800ObjectReady && mStartState && isDevicesResponding()) {
                sleep(100);
                changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь выйдет к заданным характеристикам");
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                changeTextOfView(mStatus, "Ждём заданное время обкатки на ХХ");
                int t = 10;
                while (isExperimentStart() && (--t > 0) && mStartState && isDevicesResponding()) {
                    sleep(1000);
                }
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                mDevicesController.setObjectParams(mSpecifiedUK10 + (int) (mSpecifiedUK10 - mUA * 10) + 15, mIntSpecifiedFrequencyK100 + 10 * 100, mIntSpecifiedFrequencyK100 + 10 * 100);
            }

            if (isExperimentStart() && mStartState && isDevicesResponding()) {
                pickUpState();
            }

            int experimentTime = 120;
            while (isExperimentStart() && (experimentTime > 0) && mStartState && isDevicesResponding()) {
                experimentTime--;
                sleep(1000);
                changeTextOfView(mStatus, "Ждём заданное время. Осталось: " + experimentTime);
                changeTextOfView(mTCell, "" + experimentTime);
                if (experimentTime < 3) {
                    mNeedToSave = false;
                }
            }

            mDevicesController.stopObject();
            experimentTime = 20;
            while (isExperimentStart() && (experimentTime > 0) && mStartState && isDevicesResponding()) {
                experimentTime--;
                sleep(1000);
            }
            mDevicesController.offKMsFrom8To9Group();
            m200to5State = false;
            m40to5State = false;
            m5to5State = false;

            return null;
        }

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
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
        return isBeckhoffResponding() && isVEHATResponding() && isFRA800ObjectResponding() && isPM130Responding() && isTRM201Responding();
    }

    @Override
    public void update(Observable o, Object values) {
        if (!mNeedToSave) {
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
                        setP(P1);
                        break;
                    case PM130Model.COS_PARAM:
                        setCos((float) value);
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
                    case TRM201Model.T_ENGINE_PARAM:
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

    public boolean isVEHATResponding() {
        return mVEHATResponding;
    }

    public void setVEHATResponding(boolean VEHATResponding) {
        mVEHATResponding = VEHATResponding;
    }

    public void setV(float V) {
        mV = V;
        changeTextOfView(mVCell, formatRealNumber(V));
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
        changeTextOfView(mUACell, formatRealNumber(UA));
    }

    public void setUB(float UB) {
        mUB = UB;
        changeTextOfView(mUBCell, formatRealNumber(UB));
    }

    public void setUC(float UC) {
        mUC = UC;
        changeTextOfView(mUCCell, formatRealNumber(UC));
        setUAverage((mUA + mUB + UC) / 3f);
    }

    public void setUAverage(float UAverage) {
        mUAverage = UAverage;
        changeTextOfView(mUAverageCell, formatRealNumber(UAverage));
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
    }

    public void setIB(float IB) {
        mIB = IB;
        changeTextOfView(mIBCell, formatRealNumber(IB));
    }

    public void setIC(float IC) {
        mIC = IC;
        changeTextOfView(mICCell, formatRealNumber(IC));
        setIAverage((mIA + mIB + IC) / 3f);
    }

    public void setIAverage(float IAverage) {
        mIAverage = IAverage;
        changeTextOfView(mIAverageCell, formatRealNumber(IAverage));
    }

    public void setP(float p) {
        mP = p;
        changeTextOfView(mPCell, formatRealNumber(p));
    }

    public void setCos(float cos) {
        mCos = cos;
        changeTextOfView(mCosCell, formatRealNumber(cos));
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

    private void clearCells() {
        changeTextOfView(mUACell, "");
        changeTextOfView(mIACell, "");
        changeTextOfView(mUBCell, "");
        changeTextOfView(mIBCell, "");
        changeTextOfView(mPCell, "");
        changeTextOfView(mCosCell, "");
        changeTextOfView(mVCell, "");
        changeTextOfView(mTempCell, "");
        changeTextOfView(mTCell, "");
        changeTextOfView(mUCCell, "");
        changeTextOfView(mICCell, "");
        changeTextOfView(mIAverageCell, "");
        changeTextOfView(mUAverageCell, "");
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
        data.putExtra(MainActivity.INPUT_PARAMETER.V_OVERLOAD_R, mV);
        data.putExtra(MainActivity.INPUT_PARAMETER.T_OVERLOAD_R, 120f);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE8UA(mUACell.getText().toString());
        experiments.setE8IA(mIACell.getText().toString());
        experiments.setE8UB(mUBCell.getText().toString());
        experiments.setE8IB(mIBCell.getText().toString());
        experiments.setE8P(mPCell.getText().toString());
        experiments.setE8Cos(mCosCell.getText().toString());
        experiments.setE8V(mVCell.getText().toString());
        experiments.setE8Temp(mTempCell.getText().toString());
        experiments.setE8T(mTCell.getText().toString());
        experiments.setE8UC(mUCCell.getText().toString());
        experiments.setE8IC(mICCell.getText().toString());
        experiments.setE8UAverage(mUAverageCell.getText().toString());
        experiments.setE8IAverage(mIAverageCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
