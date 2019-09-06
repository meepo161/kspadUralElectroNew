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

import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_GENERATOR_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;

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

    private Connection mRS485Connection;
    private Connection mMeggerConnection;

    private List<DeviceController> mDevicesControllers = new ArrayList<>();
    private List<DeviceController> mDevicesControllersMegger = new ArrayList<>();

    private DeviceController mBeckhoffController;
    private DeviceController mM40Controller;
    private DeviceController mFRA800ObjectController;
    private DeviceController mFRA800GeneratorController;
    private DeviceController mPM130Controller;
    private DeviceController mVoltmeterController;
    private DeviceController mTRM201Controller;
    private DeviceController mIKASController;
    private DeviceController mVEHATController;

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

        ModbusController modbusController = new RTUController(mRS485Connection);

        mBeckhoffController = new BeckhoffController(observer, modbusController);
        mDevicesControllers.add(mBeckhoffController);

        mM40Controller = new M40Controller(observer, modbusController);
        mDevicesControllers.add(mM40Controller);

        mFRA800ObjectController = new FRA800Controller(0x0B, observer, modbusController, FR_A800_OBJECT_ID);
        mDevicesControllers.add(mFRA800ObjectController);

        mFRA800GeneratorController = new FRA800Controller(0X0C, observer, modbusController, FR_A800_GENERATOR_ID);
        mDevicesControllers.add(mFRA800GeneratorController);

        mPM130Controller = new PM130Controller(observer, modbusController);
        mDevicesControllers.add(mPM130Controller);

        mVoltmeterController = new VoltmeterController(observer, modbusController);
        mDevicesControllers.add(mVoltmeterController);

        mTRM201Controller = new TRM201Controller(observer, modbusController);
        mDevicesControllers.add(mTRM201Controller);

        mIKASController = new IKASController(observer, modbusController);
        mDevicesControllers.add(mIKASController);

        mVEHATController = new VEHATController(observer, modbusController);
        mDevicesControllers.add(mVEHATController);

        mCS02021Controller = new CS02021Controller(context, (byte) 0x04, observer);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isNeededToRunThreads()) {
                    for (DeviceController deviceController : mDevicesControllers) {
                        if (deviceController.needToRead()) {
                            if (deviceController instanceof PM130Controller) {
                                deviceController.read(1);
                            } else {
                                deviceController.read();
                            }
                        }
                        deviceController.resetAttempts();
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
                    for (DeviceController deviceController : mDevicesControllersMegger) {
                        if (deviceController.needToRead()) {
                            deviceController.read();
                        }
                        deviceController.resetAttempts();
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
                            if (Objects.equals(device.getProductName(), RS485_DEVICE_NAME) ||
                                    Objects.equals(device.getProductName(), MEGGER_DEVICE_NAME)) {
                                connectMainBus();
                                connectTermodatBus();
                            }
                        }
                    }
                } else if (ACTION_USB_DETACHED.equals(action) || ACTION_USB_ATTACHED.equals(action)) {
                    synchronized (this) {
                        disconnectMainBus();
                        disconnectTermodatBus();
                        connectMainBus();
                        connectTermodatBus();
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

    private void disconnectMainBus() {
        mRS485Connection.closeConnection();
    }

    private void connectTermodatBus() {
        if (!mMeggerConnection.isInitiatedConnection()) {
            mMeggerConnection.initConnection();
        }
    }

    private void disconnectTermodatBus() {
        mMeggerConnection.closeConnection();
    }

    public void initAllDevices() {
        connectMainBus();
        connectTermodatBus();
        mBeckhoffController.setNeedToRead(true);
        mM40Controller.setNeedToRead(true);
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800GeneratorController.setNeedToRead(true);
        mPM130Controller.setNeedToRead(true);
        mVoltmeterController.setNeedToRead(true);
        mTRM201Controller.setNeedToRead(true);
        mIKASController.setNeedToRead(true);
        mVEHATController.setNeedToRead(true);
    }
}