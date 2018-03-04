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
import ru.avem.kspad.communication.devices.beckhoff.BeckhoffModel;
import ru.avem.kspad.communication.devices.trm201.TRM201Model;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.DeviceController.TRM201_ID;
import static ru.avem.kspad.utils.Utils.formatRealNumber;
import static ru.avem.kspad.utils.Utils.sleep;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment11Activity extends AppCompatActivity implements Observer {
    private static final String EXPERIMENT_NAME = "Определение сопротивления изоляции обмоток относительно корпуса машины и между обмотками";

    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.experiment_switch)
    ToggleButton mExperimentSwitch;

    @BindView(R.id.u_r)
    TextView mUrCell;
    @BindView(R.id.r15)
    TextView mR15Cell;
    @BindView(R.id.r60)
    TextView mR60Cell;
    @BindView(R.id.k)
    TextView mKCell;
    @BindView(R.id.temp)
    TextView mTempCell;
    @BindView(R.id.result)
    TextView mResultCell;

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

    private boolean mBeckhoffResponding;
    private boolean mStartState;

    private int mSpecifiedU;
    private boolean mPlatformOneSelected;

    private float mUr;
    private float mR15;
    private float mR60 = -1f;
    private float mK;

    private boolean mTRM201Responding;
    private float mTemp;

    private boolean mExperimentResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment11);
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
            if (extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U) != 0) {
                mSpecifiedU = extras.getInt(MainActivity.OUTPUT_PARAMETER.SPECIFIED_U);
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
        if (!experimentStart) {
            mStatus.setText("В ожидании начала испытания");
        }
    }

    private class ExperimentTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearCells();
            setExperimentStart(true);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            changeTextOfView(mStatus, "Испытание началось");
            mDevicesController.initDevicesFrom11Group();
            while (isExperimentStart() && !isBeckhoffResponding()) {
                changeTextOfView(mStatus, "Нет связи с ПЛК");
                sleep(100);
            }
            while (isExperimentStart() && !mStartState) {
                sleep(100);
                changeTextOfView(mStatus, "Включите кнопочный пост");
            }
            changeTextOfView(mStatus, "Инициализация");
            mDevicesController.initDevicesFrom11Group();
            while (isExperimentStart() && !isDevicesResponding() && mStartState) {
                changeTextOfView(mStatus, "Нет связи с устройствами");
                sleep(100);
            }

            if (isExperimentStart() && mStartState) {
                changeTextOfView(mStatus, "Инициализация...");
                mDevicesController.onKMsFrom11Group();
            }

            if (isExperimentStart() && mStartState) {
                changeTextOfView(mStatus, "Измерение началось");
                mDevicesController.setUMgr(mSpecifiedU);
            }

            int experimentTime = 80;
            while (isExperimentStart() && (experimentTime-- > 0) && mStartState) {
                sleep(1000);
                changeTextOfView(mStatus, "Ждём, пока измерение закончится. Осталось: " + experimentTime);
            }

            if (isExperimentStart() && mStartState) {
                float[] data = mDevicesController.readDataMgr();
                setUr(data[1]);
                setR15(data[3]);
                setR60(data[0]);
                setK(data[2]);
            }

            mDevicesController.offKMsFrom11Group();

            experimentTime = 15;
            while (isExperimentStart() && (experimentTime-- > 0) && mStartState) {
                sleep(1000);
                changeTextOfView(mStatus, "Ждём, пока разрядится. Осталось: " + experimentTime);
            }

            if (isExperimentStart() && mStartState) {
                setExperimentResult(mK > 1.3f);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDevicesController.diversifyDevices();
            mExperimentSwitch.setChecked(false);
            mStatus.setText("Испытание закончено");
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

    private boolean isDevicesResponding() {
        return isBeckhoffResponding() && isTRM201Responding();
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

    public void setUr(float ur) {
        mUr = ur;
        changeTextOfView(mUrCell, formatRealNumber(ur));
    }

    public void setR15(float r15) {
        mR15 = r15 / 1000000f;
        if (mR15 < 200000f) {
            changeTextOfView(mR15Cell, formatRealNumber(mR15));
        } else {
            changeTextOfView(mR15Cell, "> 200000");
        }
    }

    public void setR60(float r60) {
        mR60 = r60 / 1000000f;
        if (mR60 < 200000f) {
            changeTextOfView(mR60Cell, formatRealNumber(mR60));
        } else {
            changeTextOfView(mR60Cell, "> 200000");
        }
    }

    public void setK(float k) {
        mK = k;
        changeTextOfView(mKCell, formatRealNumber(k));
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

    public void setExperimentResult(boolean experimentResult) {
        mExperimentResult = experimentResult;
        changeTextOfView(mResultCell, experimentResult ? "Успешно" : "Неуспешно");
    }

    private void clearCells() {
        changeTextOfView(mUrCell, "");
        changeTextOfView(mR15Cell, "");
        changeTextOfView(mR60Cell, "");
        changeTextOfView(mKCell, "");
        changeTextOfView(mTempCell, "");
        changeTextOfView(mResultCell, "");
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
        data.putExtra(MainActivity.INPUT_PARAMETER.MGR_R, mR60);
        setResult(RESULT_OK, data);
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE11UR(mUrCell.getText().toString());
        experiments.setE11R15(mR15Cell.getText().toString());
        experiments.setE11R60(mR60Cell.getText().toString());
        experiments.setE11K(mKCell.getText().toString());
        experiments.setE11Temp(mTempCell.getText().toString());
        experiments.setE11Result(mResultCell.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
