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
import ru.avem.kspad.communication.devices.DevicesController;
import ru.avem.kspad.communication.devices.beckhoff.BeckhoffModel;

import static ru.avem.kspad.communication.devices.DeviceController.BECKHOFF_CONTROL_ID;

public class ProtectionsActivity extends AppCompatActivity implements Observer {
    public static final String OK = "Норма";
    public static final String BAD = "Ошибка";
    public static final String EMPTY = "";

    @BindView(R.id.responding)
    TextView mResponding;
    @BindView(R.id.cabinet_door)
    TextView mCabinetDoor;
    @BindView(R.id.current_protection_subject)
    TextView mCurrentProtectionSubject;
    @BindView(R.id.current_protection_viu)
    TextView mCurrentProtectionViu;
    @BindView(R.id.current_protection_entry)
    TextView mCurrentProtectionEntry;
    @BindView(R.id.zone_door)
    TextView mZoneDoor;


    private DevicesController mDevicesController;
    private final Handler mHandler = new Handler();
    private OnBroadcastCallback mOnBroadcastCallback = new OnBroadcastCallback() {
        @Override
        public void onBroadcastUsbReceiver(BroadcastReceiver broadcastReceiver) {
            mBroadcastReceiver = broadcastReceiver;
        }
    };
    private BroadcastReceiver mBroadcastReceiver;
    private boolean mBeckhoffResponding;

    private boolean mCabinetDoorInstantState;
    private boolean mCurrentProtectionSubjectInstantState;
    private boolean mCurrentProtectionViuInstantState;
    private boolean mCurrentProtectionEntryInstantState;
    private boolean mZoneDoorInstantState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protections);
        ButterKnife.bind(this);

        mDevicesController = new DevicesController(this, this, mOnBroadcastCallback, true);
        mDevicesController.initBeckhoff();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        mDevicesController.setNeededToRunThreads(false);
    }

    @Override
    public void update(Observable observable, Object values) {
        int modelId = (int) (((Object[]) values)[0]);
        int param = (int) (((Object[]) values)[1]);
        Object value = (((Object[]) values)[2]);

        switch (modelId) {
            case BECKHOFF_CONTROL_ID:
                switch (param) {
                    case BeckhoffModel.RESPONDING_PARAM:
                        setBeckhoffResponding((boolean) value);
                        break;
                    case BeckhoffModel.DOOR_S_PARAM:
                        setCabinetDoorInstantState((boolean) value);
                        break;
                    case BeckhoffModel.I_PROTECTION_OBJECT_PARAM:
                        setCurrentProtectionSubjectInstantState((boolean) value);
                        break;
                    case BeckhoffModel.I_PROTECTION_VIU_PARAM:
                        setCurrentProtectionViuInstantState((boolean) value);
                        break;
                    case BeckhoffModel.I_PROTECTION_IN_PARAM:
                        setCurrentProtectionEntryInstantState((boolean) value);
                        break;
                    case BeckhoffModel.DOOR_Z_PARAM:
                        setZoneDoorInstantState((boolean) value);
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
                switch (text) {
                    case OK:
                        view.setBackgroundResource(R.drawable.border_padding_light_green);
                        break;
                    case BAD:
                        view.setBackgroundResource(R.drawable.border_padding_light_red);
                        break;
                    case EMPTY:
                        view.setBackgroundResource(R.drawable.border_padding);
                        break;
                }
            }
        });
    }

    private boolean isBeckhoffResponding() {
        return mBeckhoffResponding;
    }

    private void setBeckhoffResponding(boolean beckhoffResponding) {
        mBeckhoffResponding = beckhoffResponding;
        changeTextOfView(mResponding, (mBeckhoffResponding ? OK : BAD));
    }

    private void setCabinetDoorInstantState(boolean IProtectionLoadState) {
        mCabinetDoorInstantState = IProtectionLoadState;
        changeTextOfView(mCabinetDoor, isBeckhoffResponding() ? (mCabinetDoorInstantState ? OK : BAD) : EMPTY);
    }

    private void setCurrentProtectionSubjectInstantState(boolean IProtectionGeneratorState) {
        mCurrentProtectionSubjectInstantState = IProtectionGeneratorState;
        changeTextOfView(mCurrentProtectionSubject, isBeckhoffResponding() ? (mCurrentProtectionSubjectInstantState ? OK : BAD) : EMPTY);
    }

    private void setCurrentProtectionViuInstantState(boolean IProtectionSState) {
        mCurrentProtectionViuInstantState = IProtectionSState;
        changeTextOfView(mCurrentProtectionViu, isBeckhoffResponding() ? (mCurrentProtectionViuInstantState ? OK : BAD) : EMPTY);
    }

    private void setCurrentProtectionEntryInstantState(boolean IProtectionFqLoadState) {
        mCurrentProtectionEntryInstantState = IProtectionFqLoadState;
        changeTextOfView(mCurrentProtectionEntry, isBeckhoffResponding() ? (mCurrentProtectionEntryInstantState ? OK : BAD) : EMPTY);
    }

    private void setZoneDoorInstantState(boolean IProtectionChainOfConstantIState) {
        mZoneDoorInstantState = IProtectionChainOfConstantIState;
        changeTextOfView(mZoneDoor, isBeckhoffResponding() ? (mZoneDoorInstantState ? OK : BAD) : EMPTY);
    }
}
