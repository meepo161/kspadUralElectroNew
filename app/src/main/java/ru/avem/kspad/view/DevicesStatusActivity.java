package ru.avem.kspad.view;

import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.avem.kspad.R;
import ru.avem.kspad.communication.devices.DevicesControllerDiagnostic;
import ru.avem.kspad.communication.devices.FR_A800.FRA800Model;
import ru.avem.kspad.communication.devices.beckhoff.BeckhoffModel;
import ru.avem.kspad.communication.devices.ikas.IKASModel;
import ru.avem.kspad.communication.devices.m40.M40Model;
import ru.avem.kspad.communication.devices.pm130.PM130Model;
import ru.avem.kspad.communication.devices.trm201.TRM201Model;
import ru.avem.kspad.communication.devices.veha_t.VEHATModel;
import ru.avem.kspad.communication.devices.voltmeter.VoltmeterModel;

import static ru.avem.kspad.communication.devices.Device.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.Device.FR_A800_GENERATOR_ID;
import static ru.avem.kspad.communication.devices.Device.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.Device.IKAS_ID;
import static ru.avem.kspad.communication.devices.Device.M40_ID;
import static ru.avem.kspad.communication.devices.Device.PM130_ID;
import static ru.avem.kspad.communication.devices.Device.TRM201_ID;
import static ru.avem.kspad.communication.devices.Device.VEHA_T_ID;
import static ru.avem.kspad.communication.devices.Device.VOLTMETER_ID;

public class DevicesStatusActivity extends AppCompatActivity implements Observer {
    @BindView(R.id.control_unit)
    TextView mControlUnit;
    @BindView(R.id.megger)
    TextView mMegger;
    @BindView(R.id.fq_subject)
    TextView mFqSubject;
    @BindView(R.id.fq_generator)
    TextView mFqGenerator;
    @BindView(R.id.ikas)
    TextView mIkas;
    @BindView(R.id.torque_sensor)
    TextView mTorqueSensor;
    @BindView(R.id.pm130)
    TextView mPm130;
    @BindView(R.id.trm)
    TextView mTrm;
    @BindView(R.id.fq_counter)
    TextView mFqCounter;
    @BindView(R.id.avem)
    TextView mAvem;

    private DevicesControllerDiagnostic mDevicesController;
    private final Handler mHandler = new Handler();
    private OnBroadcastCallback mOnBroadcastCallback = new OnBroadcastCallback() {
        @Override
        public void onBroadcastUsbReceiver(BroadcastReceiver broadcastReceiver) {
            mBroadcastReceiver = broadcastReceiver;
        }
    };
    private BroadcastReceiver mBroadcastReceiver;

    private boolean mControlUnitResponding;
    private boolean mMeggerResponding;
    private boolean mSubjectResponding;
    private boolean mGeneratorResponding;
    private boolean mIkasResponding;
    private boolean mTorqueSensorResponding;
    private boolean mPM130Responding;
    private boolean mTrmResponding;
    private boolean mFqCounterResponding;
    private boolean mAvemResponding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_status);
        ButterKnife.bind(this);

        mDevicesController = new DevicesControllerDiagnostic(this, this, mOnBroadcastCallback);
        mDevicesController.initAllDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDevicesController.setNeededToRunThreads(false);
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
                        setControlUnitResponding((boolean) value);
                        break;
                }
                break;
            case FR_A800_OBJECT_ID:
                switch (param) {
                    case FRA800Model.RESPONDING_PARAM:
                        setSubjectResponding((boolean) value);
                        break;
                }
                break;
            case FR_A800_GENERATOR_ID:
                switch (param) {
                    case FRA800Model.RESPONDING_PARAM:
                        setGeneratorResponding((boolean) value);
                        break;
                }
                break;
            case IKAS_ID:
                switch (param) {
                    case IKASModel.RESPONDING_PARAM:
                        setIkasResponding((boolean) value);
                        break;
                }
                break;
            case M40_ID:
                switch (param) {
                    case M40Model.RESPONDING_PARAM:
                        setTorqueSensorResponding((boolean) value);
                        break;
                }
                break;
            case PM130_ID:
                switch (param) {
                    case PM130Model.RESPONDING_PARAM:
                        setPM130Responding((boolean) value);
                        break;
                }
                break;
            case TRM201_ID:
                switch (param) {
                    case TRM201Model.RESPONDING_PARAM:
                        setTrmResponding((boolean) value);
                        break;
                }
                break;
            case VEHA_T_ID:
                switch (param) {
                    case VEHATModel.RESPONDING_PARAM:
                        setFqCounterResponding((boolean) value);
                        break;
                }
            case VOLTMETER_ID:
                switch (param) {
                    case VoltmeterModel.RESPONDING_PARAM:
                        setAvemResponding((boolean) value);
                        break;
                }
        }
    }

    public void setControlUnitResponding(boolean controlUnitResponding) {
        mControlUnitResponding = controlUnitResponding;
        changeTextOfView(mControlUnit, mControlUnitResponding ? "Соединение установлено" : "Соединения отсутствует");
    }

    private void changeTextOfView(final TextView view, final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                view.setText(text);
                if (text.equals("Соединение установлено")) {
                    view.setBackgroundResource(R.drawable.border_padding_light_green);
                } else if (text.equals("Соединения отсутствует")) {
                    view.setBackgroundResource(R.drawable.border_padding_light_red);
                }
            }
        });
    }

    public void setMeggerResponding(boolean meggerResponding) {
        mMeggerResponding = meggerResponding;
        changeTextOfView(mMegger, mMeggerResponding ? "Соединение установлено" : "Соединения отсутствует");

    }

    public void setSubjectResponding(boolean subjectResponding) {
        mSubjectResponding = subjectResponding;
        changeTextOfView(mFqSubject, mSubjectResponding ? "Соединение установлено" : "Соединения отсутствует");
    }

    public void setGeneratorResponding(boolean generatorResponding) {
        mGeneratorResponding = generatorResponding;
        changeTextOfView(mFqGenerator, mGeneratorResponding ? "Соединение установлено" : "Соединения отсутствует");
    }

    public void setIkasResponding(boolean ikasResponding) {
        mIkasResponding = ikasResponding;
        changeTextOfView(mIkas, mIkasResponding ? "Соединение установлено" : "Соединения отсутствует");
    }

    public void setTorqueSensorResponding(boolean torqueSensorResponding) {
        mTorqueSensorResponding = torqueSensorResponding;
        changeTextOfView(mTorqueSensor, mTorqueSensorResponding ? "Соединение установлено" : "Соединения отсутствует");
    }

    public void setPM130Responding(boolean PM130Responding) {
        mPM130Responding = PM130Responding;
        changeTextOfView(mPm130, mPM130Responding ? "Соединение установлено" : "Соединения отсутствует");
    }

    public void setTrmResponding(boolean trmResponding) {
        mTrmResponding = trmResponding;
        changeTextOfView(mTrm, mTrmResponding ? "Соединение установлено" : "Соединения отсутствует");
    }

    public void setFqCounterResponding(boolean fqCounterResponding) {
        mFqCounterResponding = fqCounterResponding;
        changeTextOfView(mFqCounter, mFqCounterResponding ? "Соединение установлено" : "Соединения отсутствует");
    }

    public void setAvemResponding(boolean avemResponding) {
        mAvemResponding = avemResponding;
        changeTextOfView(mAvem, mAvemResponding ? "Соединение установлено" : "Соединения отсутствует");
    }
}
