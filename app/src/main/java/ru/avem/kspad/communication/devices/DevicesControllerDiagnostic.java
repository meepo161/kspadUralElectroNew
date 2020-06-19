package ru.avem.kspad.communication.devices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import ru.avem.kspad.communication.connection_protocols.Connection;
import ru.avem.kspad.communication.connection_protocols.SerialConnection;
import ru.avem.kspad.communication.devices.FR_A800.FRA800Controller;
import ru.avem.kspad.communication.devices.beckhoff.BeckhoffController;
import ru.avem.kspad.communication.devices.cs02021.CS02021Controller;
import ru.avem.kspad.communication.devices.ikas.IKASController;
import ru.avem.kspad.communication.devices.m40.M40Controller;
import ru.avem.kspad.communication.devices.pm130.PM130Controller;
import ru.avem.kspad.communication.devices.trm201.TRM201Controller;
import ru.avem.kspad.communication.devices.veha_t.VEHATController;
import ru.avem.kspad.communication.devices.voltmeter.VoltmeterController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;
import ru.avem.kspad.communication.protocol.modbus.RTUController;
import ru.avem.kspad.view.OnBroadcastCallback;

import static ru.avem.kspad.communication.devices.Device.BECKHOFF_CONTROL_ID;
import static ru.avem.kspad.communication.devices.Device.FR_A800_GENERATOR_ID;
import static ru.avem.kspad.communication.devices.Device.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.Device.IKAS_ID;
import static ru.avem.kspad.communication.devices.Device.M40_ID;
import static ru.avem.kspad.communication.devices.Device.PM130_ID;
import static ru.avem.kspad.communication.devices.Device.TRM201_ID;
import static ru.avem.kspad.communication.devices.Device.VEHA_T_ID;
import static ru.avem.kspad.communication.devices.Device.VOLTMETER_ID;

public class DevicesControllerDiagnostic extends Observable {
    public static final String ACTION_USB_PERMISSION =
            "ru.avem.kspad.USB_PERMISSION";
    private static final String ACTION_USB_ATTACHED =
            "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DETACHED =
            "android.hardware.usb.action.USB_DEVICE_DETACHED";

    private static final int WRITE_TIMEOUT = 100;
    private static final int READ_TIMEOUT = 100;

    private static final String RS485_DEVICE_NAME = "CP2103 USB to RS-485";
    private static final int BAUD_RATE = 38400;

    private static final String MEGGER_DEVICE_NAME = "CP2103 USB to Megger";
    private static final int BAUD_RATE_MEGGER = 9600;

    private static final String FI1_DEVICE_NAME = "CP2103 USB to FI1";
    private static final String FI2_DEVICE_NAME = "CP2103 USB to FI2";

    private Connection mRS485Connection;
    private Connection mMeggerConnection;
    private Connection mFI1Connection;
    private Connection mFI2Connection;

    private List<Device> mDevicesControllers = new ArrayList<>();
    private List<Device> mDevicesControllersMegger = new ArrayList<>();

    private Device mBeckhoffController;
    private Device mM40Controller;
    private Device mFRA800ObjectController;
    private Device mFRA800GeneratorController;
    private Device mPM130Controller;
    private Device mVoltmeterController;
    private Device mTRM201Controller;
    private Device mIKASController;
    private Device mVEHATController;

    private CS02021Controller mCS02021Controller;

    private boolean mNeededToRunThreads = true;

    public DevicesControllerDiagnostic(final Context context, Observer observer, OnBroadcastCallback onBroadcastCallback) {
        addObserver(observer);
        mRS485Connection = new SerialConnection(context, RS485_DEVICE_NAME, BAUD_RATE,
                UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE,
                WRITE_TIMEOUT, READ_TIMEOUT);
        mMeggerConnection = new SerialConnection(context, MEGGER_DEVICE_NAME, BAUD_RATE_MEGGER,
                UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE,
                WRITE_TIMEOUT, READ_TIMEOUT);
        mFI1Connection = new SerialConnection(context, FI1_DEVICE_NAME, BAUD_RATE,
                UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE,
                WRITE_TIMEOUT, READ_TIMEOUT);

        mFI2Connection = new SerialConnection(context, FI2_DEVICE_NAME, BAUD_RATE,
                UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE,
                WRITE_TIMEOUT, READ_TIMEOUT);

        ModbusController modbusController = new RTUController(mRS485Connection);
        ModbusController fi1Controller = new RTUController(mFI1Connection);
        ModbusController fi2Controller = new RTUController(mFI2Connection);

        mBeckhoffController = new BeckhoffController(BECKHOFF_CONTROL_ID, observer, modbusController);
        mDevicesControllers.add(mBeckhoffController);

        mM40Controller = new M40Controller(M40_ID, observer, modbusController);
        mDevicesControllers.add(mM40Controller);

        mFRA800ObjectController = new FRA800Controller(FR_A800_OBJECT_ID, observer, fi1Controller);
        mDevicesControllers.add(mFRA800ObjectController);

        mFRA800GeneratorController = new FRA800Controller(FR_A800_GENERATOR_ID, observer, fi2Controller);
        mDevicesControllers.add(mFRA800GeneratorController);

        mPM130Controller = new PM130Controller(PM130_ID, observer, modbusController);
        mDevicesControllers.add(mPM130Controller);

        mVoltmeterController = new VoltmeterController(VOLTMETER_ID, observer, modbusController);
        mDevicesControllers.add(mVoltmeterController);

        mTRM201Controller = new TRM201Controller(TRM201_ID, observer, modbusController);
        mDevicesControllers.add(mTRM201Controller);

        mIKASController = new IKASController(IKAS_ID, observer, modbusController);
        mDevicesControllers.add(mIKASController);

        mVEHATController = new VEHATController(VEHA_T_ID, observer, modbusController);
        mDevicesControllers.add(mVEHATController);

        mCS02021Controller = new CS02021Controller(context, (byte) 0x04, observer);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isNeededToRunThreads()) {
                    for (Device deviceController : mDevicesControllers) {
                        if (deviceController.isNeedToRead()) {
                            if (deviceController instanceof PM130Controller) {
                                deviceController.read(1);
                            } else {
                                deviceController.read();
                            }
                        }
                        deviceController.resetAttemptsToOneAndStart();
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isNeededToRunThreads()) {
                    for (Device deviceController : mDevicesControllersMegger) {
                        if (deviceController.isNeedToRead()) {
                            deviceController.read();
                        }
                        deviceController.resetAttemptsToOneAndStart();
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_ATTACHED);
        filter.addAction(ACTION_USB_DETACHED);
        final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        if (device != null) {
                            if (Objects.equals(device.getProductName(), RS485_DEVICE_NAME)
                                    || Objects.equals(device.getProductName(), MEGGER_DEVICE_NAME)
                                            || (Objects.equals(device.getProductName(), FI1_DEVICE_NAME)
                                            || (Objects.equals(device.getProductName(), FI2_DEVICE_NAME)))) {
                                connectMainBus();
                                connectMeggerBus();
                                connectFI1Bus();
                                connectFI2Bus();
                            }
                        }
                    }
                } else if (ACTION_USB_DETACHED.equals(action) || ACTION_USB_ATTACHED.equals(action)) {
                    synchronized (this) {
                        disconnectAllBus();
                        connectMainBus();
                        connectFI1Bus();
                        connectFI2Bus();
                        connectMeggerBus();
                    }
                }
            }
        };
        context.registerReceiver(usbReceiver, filter);
        onBroadcastCallback.onBroadcastUsbReceiver(usbReceiver);
    }

    private boolean isNeededToRunThreads() {
        return mNeededToRunThreads;
    }

    public void setNeededToRunThreads(boolean neededToRunThreads) {
        mNeededToRunThreads = neededToRunThreads;
    }

    private void connectMainBus() {
        if (!mRS485Connection.isInitiatedConnection()) {
            mRS485Connection.initConnection();
        }
    }

    private void connectFI1Bus() {
        if (!mFI1Connection.isInitiatedConnection()) {
            mFI1Connection.initConnection();
        }
    }

    private void connectFI2Bus() {
        if (!mFI2Connection.isInitiatedConnection()) {
            mFI2Connection.initConnection();
        }
    }

    private void disconnectAllBus() {
        mRS485Connection.closeConnection();
        mMeggerConnection.closeConnection();
        mFI1Connection.closeConnection();
        mFI2Connection.closeConnection();
    }

    private void connectMeggerBus() {
        if (!mMeggerConnection.isInitiatedConnection()) {
            mMeggerConnection.initConnection();
        }
    }


    public void initAllDevices() {
        connectMainBus();
        connectMeggerBus();
        mBeckhoffController.resetAndStart();
        mM40Controller.resetAndStart();
        mFRA800ObjectController.resetAndStart();
        mFRA800GeneratorController.resetAndStart();
        mPM130Controller.resetAndStart();
        mVoltmeterController.resetAndStart();
        mTRM201Controller.resetAndStart();
        mIKASController.resetAndStart();
        mVEHATController.resetAndStart();
    }
}