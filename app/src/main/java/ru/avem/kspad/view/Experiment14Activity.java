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
import ru.avem.kspad.communication.devices.m40.M40Model;
import ru.avem.kspad.communication.devices.veha_t.VEHATModel;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_GENERATOR_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.DeviceController.M40_ID;
import static ru.avem.kspad.communication.devices.DeviceController.VEHA_T_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment14Activity extends AppCompatActivity implements Observer {
    //region Константы
    private static final String EXPERIMENT_NAME = "Определение минимального момента";
    //endregion

    //region Виджеты
    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;


    @BindView(R.id.m)
    TextView mMCell;
    @BindView(R.id.v)
    TextView mVCell;

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

    private double mV1;
    private float mSpecifiedTorque;
    private float mSpecifiedFrequency;
    private int mIntSpecifiedFrequencyK100;
    private int mSpecifiedU;
    private int mSpecifiedUK10;
    private boolean mPlatformOneSelected;

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private boolean mM40Responding;
    private float mM;

    private boolean mIsNeedToFixMMax;
    private float mMMax;

    private boolean mVEHATResponding;
    private float mV;

    private boolean mFRA800ObjectResponding;
    private boolean mFRA800ObjectReady;

    private boolean mFRA800GeneratorResponding;
    private boolean mFRA800GeneratorReady;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment14);
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
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.V1) != 0) {
                mV1 = extras.getInt(MainActivity.OUTPUT_PARAMETER.V1);
            } else {
                throw new NullPointerException("Не передано V1");
            }
            if (extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_TORQUE) != 0) {
                mSpecifiedTorque = extras.getFloat(MainActivity.OUTPUT_PARAMETER.SPECIFIED_TORQUE);
            } else {
                throw new NullPointerException("Не передано specifiedTorque");
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
            mThreadOn = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isExperimentStart()) {
                changeTextOfView(mStatus, "Испытание началось");
                mDevicesController.initDevicesFrom14Group();
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
                mDevicesController.initDevicesFrom14Group();
            }
            while (isExperimentStart() && !isDevicesResponding()) {
                changeTextOfView(mStatus, "Нет связи с устройствами");
                sleep(100);
            }

            changeTextOfView(mStatus, "Инициализация...");
            mDevicesController.setObjectParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
            mDevicesController.setGeneratorParams(1 * 10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);

            int result = 1;
            int u = 90;
            do {
                mMMax = 0;
                if (result == 1) {
                    u += 10;
                    if (u > 400) {
                        break;
                    }
                } else if (result == 2) {
                    u -= 10;
                    if (u <= 0) {
                        break;
                    }
                }
                result = startNextIteration(u);
            } while (isExperimentStart() && (result != 0) && mStartState);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDevicesController.diversifyDevices();
            mExperimentSwitch.setChecked(false);
            mStatus.setText("Испытание закончено");
            changeTextOfView(mMCell, formatRealNumber(mMMax));
            mM = mMMax;
            mThreadOn = false;
        }
    }

    private int startNextIteration(int u) {
        int result = -1;
        changeTextOfView(mStatus, "При U=" + u);
        sleep(500);

        if (isExperimentStart() && mStartState) {
            mDevicesController.onKMsFrom14Group();
            sleep(500);
            mDevicesController.startObject();
        }

        while (isExperimentStart() && !mFRA800ObjectReady && mStartState) {
            sleep(100);
            changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь ОИ выйдет к заданным характеристикам");
        }

        int t = 5;
        while (isExperimentStart() && (--t > 0) && mStartState) {
            changeTextOfView(mStatus, "Ждём 5 секунд. Осталось: " + t);
            sleep(1000);
        }

        if (isExperimentStart() && mStartState) {
            mDevicesController.setGeneratorUMax(u * 10);
            mDevicesController.startReversGenerator();
        }

        while (isExperimentStart() && !mFRA800GeneratorReady && mStartState) {
            sleep(100);
            changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь генератора выйдет к заданным характеристикам");
        }

        t = 5;
        while (isExperimentStart() && (--t > 0) && mStartState) {
            changeTextOfView(mStatus, "Ждём 5 секунд. Осталось: " + t);
            sleep(1000);
        }

        changeTextOfView(mStatus, "Запустили ЧП ОИ");

        if (isExperimentStart() && mStartState) {
            mDevicesController.onObject();
        }

        t = 10;
        while (isExperimentStart() && (--t > 0) && mStartState) {
            changeTextOfView(mStatus, "Ждём 10 секунд. Осталось: " + t);
            sleep(1000);
            if (t == 3) {
                mIsNeedToFixMMax = true;
            }
        }
        mIsNeedToFixMMax = false;

        changeTextOfView(mStatus, "Сравниваем V");
        if ((mV > (mV1 * 0.3)) && (mV < (mV1 * 0.9))) {
            result = 0;
        } else if (mV > (mV1 * 0.9)) {
            result = 1;
        } else if (mV < (mV1 * 0.3)) {
            result = 2;
        }

        changeTextOfView(mStatus, "Ожидаем");
        mDevicesController.offObject();
        sleep(500);
        mDevicesController.stopObject();
        mDevicesController.stopGenerator();
        mDevicesController.offKMsFrom14Group();
        t = 6;
        while (isExperimentStart() && (--t > 0) && mStartState) {
            changeTextOfView(mStatus, "Ждём 6 секунд. Осталось: " + t);
            sleep(1000);
        }

        return result;
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

    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isM40Responding() && isFRA800ObjectResponding() &&
                isFRA800GeneratorResponding() && isVEHATResponding();
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
        if ((M > mMMax) && mIsNeedToFixMMax) {
            mMMax = M;
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

    public boolean isFRA800GeneratorResponding() {
        return mFRA800GeneratorResponding;
    }

    public void setFRA800GeneratorResponding(boolean FRA800GeneratorResponding) {
        mFRA800GeneratorResponding = FRA800GeneratorResponding;
    }

    public void setFRA800GeneratorReady(boolean FRA800GeneratorReady) {
        mFRA800GeneratorReady = FRA800GeneratorReady;
    }

    private void clearCells() {
        changeTextOfView(mMCell, "");
        changeTextOfView(mVCell, "");
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
        data.putExtra(MainActivity.INPUT_PARAMETER.M_MIN_R, mM);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE14M(mMCell.getText().toString());
        experiments.setE14V(mVCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
