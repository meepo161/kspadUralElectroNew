package ru.avem.kspad.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
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
import ru.avem.kspad.communication.devices.veha_t.VEHATModel;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;

import static ru.avem.kspad.communication.devices.Device.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.Device.FR_A800_GENERATOR_ID;
import static ru.avem.kspad.communication.devices.Device.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.Device.M40_ID;
import static ru.avem.kspad.communication.devices.Device.PM130_ID;
import static ru.avem.kspad.communication.devices.Device.VEHA_T_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment14Activity extends AppCompatActivity implements Observer {
    //region Константы
    private static final String EXPERIMENT_NAME = "Определение минимального момента";
    private static final int STATE_200_TO_5_MULTIPLIER = 200 / 5;
    private static final int NEOPREDELENNO = -1;
    private static final int FOUND = 0;
    private static final int MALO = 1;
    private static final int MNOGO = 2;
    //endregion

    //region Виджеты
    @BindView(R.id.main_layout)
    ConstraintLayout mMainLayout;
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
    private String mCause = "";

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

    private boolean mIsNeedToFixMMin;
    private float mMMin;
    private float mMDiff;

    private boolean mVEHATResponding;
    private float mV;

    private boolean mFRA800ObjectResponding;
    private boolean mFRA800ObjectReady;

    private boolean mFRA800GeneratorResponding;
    private boolean mFRA800GeneratorReady;

    private boolean mPM130Responding;
    private int result = NEOPREDELENNO;
    private float rotationFreq = 0f;
    private boolean misNeedToFixRotationFreq = false;
    private boolean isButtonClicked = false;
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
        mDevicesController.setNeededToRunThreads(false);
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
            setFRA800ObjectReady(false);
            setFRA800GeneratorReady(false);
            mMainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mCause = "";
            setBeckhoffResponding(true);
            setM40Responding(true);
            setVEHATResponding(true);
            setFRA800ObjectResponding(true);
            setFRA800GeneratorResponding(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mExperimentStart = true; //не баг, а фича
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
            while (isExperimentStart() && !isDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, getNotRespondingDevicesString("Нет связи с устройствами"));
                sleep(100);
            }
            changeTextOfView(mStatus, "Инициализация...");
            mDevicesController.setObjectParams(mSpecifiedUK10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);
            mDevicesController.setGeneratorParams(1 * 10, mIntSpecifiedFrequencyK100, mIntSpecifiedFrequencyK100);

            result = NEOPREDELENNO;
            int minU = 0;
            int maxU = 380;
            int attempts = 9;
            int u = minU + (maxU - minU) / 2;
            do {
                mMMin = 0;
                if (result == MNOGO) {
                    maxU = u;
                } else if (result == MALO) {
                    minU = u;
                }
                u = (minU + maxU) / 2;
                result = startNextIteration(u);
                while (!isButtonClicked) {
                    sleep(100);
                    if (result == FOUND) {
                        break;
                    }
                }
                isButtonClicked = false;
            } while (isExperimentStart() && (result != FOUND) && mStartState && isDevicesResponding() && (attempts-- > 0));


            return null;
        }

        private String getNotRespondingDevicesString(String mainText) {
            return String.format("%s %s%s%s%s%s%s",
                    mainText,
                    isBeckhoffResponding() ? "" : "БСУ, ",
                    isM40Responding() ? "" : "Датчик момента, ",
                    mPM130Responding ? "" : "PM130, ",
                    isFRA800ObjectResponding() ? "" : "ЧП ОИ, ",
                    isFRA800GeneratorResponding() ? "" : "ЧП генератора, ",
                    isVEHATResponding() ? "" : "ВЕХА-Т");
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
            mM = mMMin;
            changeTextOfView(mMCell, formatRealNumber(mMMin));
            mThreadOn = false;
        }
    }

    private int startNextIteration(int u) {
        int result = NEOPREDELENNO;
        changeTextOfView(mStatus, "При U=" + u);
        sleep(500);

        if (isExperimentStart() && mStartState && isDevicesResponding()) {
            mDevicesController.onKMsFrom14GroupLoad();
            sleep(500);
            mDevicesController.startObject();
        }

        while (isExperimentStart() && !mFRA800ObjectReady && mStartState && isDevicesResponding()) {
            sleep(100);
            changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь ОИ выйдет к заданным характеристикам");
        }

        int t = 5;
        while (isExperimentStart() && (--t > 0) && mStartState && isDevicesResponding()) {
            changeTextOfView(mStatus, "Ждём 5 секунд. Осталось: " + t);
            sleep(1000);
        }

        if (isExperimentStart() && mStartState && isDevicesResponding()) {
            mDevicesController.setGeneratorUMax(u * 10);
            mDevicesController.startReversGenerator();
        }

        while (isExperimentStart() && !mFRA800GeneratorReady && mStartState && isDevicesResponding()) {
            sleep(100);
            changeTextOfView(mStatus, "Ожидаем, пока частотный преобразователь генератора выйдет к заданным характеристикам");
        }

        t = 10;
        while (isExperimentStart() && (--t > 0) && mStartState && isDevicesResponding()) {
            changeTextOfView(mStatus, "Ждём 10 секунд. Осталось: " + t);
            sleep(1000);
        }

        changeTextOfView(mStatus, "Запустили ЧП ОИ");

        if (isExperimentStart() && mStartState && isDevicesResponding()) {
            mDevicesController.onObject();
        }

        t = 5;
        while (isExperimentStart() && (--t > 0) && mStartState && isDevicesResponding()) {
            changeTextOfView(mStatus, "Ждём 5 секунд. Осталось: " + t);
            sleep(1000);
            if (t == 3) {
                mIsNeedToFixMMin = true;
                misNeedToFixRotationFreq = true;
            }
        }
        mIsNeedToFixMMin = false;
        misNeedToFixRotationFreq = false;

        changeTextOfView(mStatus, "Сравниваем V");
        if ((rotationFreq > (mV1 * 0.2)) && (rotationFreq < (mV1 * 0.9))) {
            result = FOUND;
        }

        changeTextOfView(mStatus, "Ожидаем");
        mDevicesController.offObject();
        sleep(500);
        mDevicesController.stopObject();
        mDevicesController.stopGenerator();
        mDevicesController.offKMsFrom14Group();
        t = 6;
        while (isExperimentStart() && (--t > 0) && mStartState && isDevicesResponding()) {
            changeTextOfView(mStatus, "Ждём 6 секунд. Осталось: " + t);
            sleep(1000);
        }

        if (isExperimentStart() && mStartState && isDevicesResponding() && result != FOUND) {
            checkRotation();
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

    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isM40Responding() && isFRA800ObjectResponding() &&
                isFRA800GeneratorResponding() && isVEHATResponding() && mPM130Responding;
    }

    @SuppressLint("DefaultLocale")
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
                        if (misNeedToFixRotationFreq) {
                            rotationFreq = (float) value;
                        }
                        break;
                }
                break;
            case PM130_ID:
                switch (param) {
                    case PM130Model.RESPONDING_PARAM:
                        mPM130Responding = ((boolean) value);
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
        changeTextOfView(mMCell, formatRealNumber(mM));
        if (mIsNeedToFixMMin) {
            mMMin = mM;
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
        data.putExtra(MainActivity.INPUT_PARAMETER.M_MIN_R, mMMin);
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

    public void checkRotation() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(Experiment14Activity.this)
                        .setTitle("")
                        .setMessage("Вращение происходит в сторону двигателя?")
                        .setIcon(R.drawable.ic_warning_black_48dp)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result = MALO;
                                isButtonClicked = true;
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result = MNOGO;
                                isButtonClicked = true;
                            }
                        })
                        .create()
                        .show();

            }
        });
    }
}
