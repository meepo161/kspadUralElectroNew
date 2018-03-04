package ru.avem.kspad.communication.devices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import ru.avem.kspad.communication.devices.FR_A800.FRA800Controller;
import ru.avem.kspad.communication.devices.beckhoff.BeckhoffController;
import ru.avem.kspad.communication.devices.cs02021.CS02021Controller;
import ru.avem.kspad.communication.devices.ikas.IKASController;
import ru.avem.kspad.communication.devices.m40.M40Controller;
import ru.avem.kspad.communication.devices.pm130.PM130Controller;
import ru.avem.kspad.communication.devices.pm130_ia.PM130ControllerIA;
import ru.avem.kspad.communication.devices.trm201.TRM201Controller;
import ru.avem.kspad.communication.devices.veha_t.VEHATController;
import ru.avem.kspad.communication.devices.voltmeter.VoltmeterController;
import ru.avem.kspad.communication.protocol.modbus.ModbusController;
import ru.avem.kspad.communication.protocol.modbus.RTUController;
import ru.avem.kspad.communication.serial.SerialConnection;
import ru.avem.kspad.utils.Logger;
import ru.avem.kspad.view.OnBroadcastCallback;

import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_GENERATOR_ID;
import static ru.avem.kspad.communication.devices.DeviceController.FR_A800_OBJECT_ID;
import static ru.avem.kspad.communication.devices.FR_A800.FRA800Controller.CONTROL_REGISTER;
import static ru.avem.kspad.communication.devices.FR_A800.FRA800Controller.CURRENT_FREQUENCY_REGISTER;
import static ru.avem.kspad.communication.devices.FR_A800.FRA800Controller.MAX_FREQUENCY_REGISTER;
import static ru.avem.kspad.communication.devices.FR_A800.FRA800Controller.MAX_VOLTAGE_REGISTER;
import static ru.avem.kspad.communication.devices.beckhoff.BeckhoffController.LIGHT_REGISTER;
import static ru.avem.kspad.communication.devices.beckhoff.BeckhoffController.MOD_1_REGISTER;
import static ru.avem.kspad.communication.devices.beckhoff.BeckhoffController.MOD_2_REGISTER;
import static ru.avem.kspad.communication.devices.beckhoff.BeckhoffController.RESET_CONNECTION_REGISTER;
import static ru.avem.kspad.communication.devices.beckhoff.BeckhoffController.SOUND_REGISTER;
import static ru.avem.kspad.communication.devices.beckhoff.BeckhoffController.WATCH_DOG_REGISTER;
import static ru.avem.kspad.communication.devices.ikas.IKASController.MEASURABLE_TYPE_AB;
import static ru.avem.kspad.communication.devices.ikas.IKASController.MEASURABLE_TYPE_AC;
import static ru.avem.kspad.communication.devices.ikas.IKASController.MEASURABLE_TYPE_BC;
import static ru.avem.kspad.communication.devices.ikas.IKASController.MEASURABLE_TYPE_REGISTER;
import static ru.avem.kspad.communication.devices.ikas.IKASController.RANGE_R_REGISTER;
import static ru.avem.kspad.communication.devices.ikas.IKASController.RANGE_TYPE_LESS_8;
import static ru.avem.kspad.communication.devices.ikas.IKASController.RANGE_TYPE_MORE_200;
import static ru.avem.kspad.communication.devices.ikas.IKASController.RANGE_TYPE_MORE_8_LESS_200;
import static ru.avem.kspad.communication.devices.ikas.IKASController.TYPE_OF_RANGE_R_REGISTER;
import static ru.avem.kspad.communication.devices.ikas.IKASController.START_MEASURABLE_REGISTER;
import static ru.avem.kspad.communication.devices.m40.M40Controller.AVERAGING_REGISTER;

public class DevicesController extends Observable implements Runnable {
    public static final String ACTION_USB_PERMISSION =
            "ru.avem.kspad.USB_PERMISSION";
    private static final String ACTION_USB_ATTACHED =
            "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DETACHED =
            "android.hardware.usb.action.USB_DEVICE_DETACHED";

    private static final int WRITE_TIMEOUT = 100;//25
    private static final int READ_TIMEOUT = 100;

    private static final String RS485_DEVICE_NAME = "CP2103 USB to RS-485";
    private static final int BAUD_RATE = 38400;

    private SerialConnection mRS485Connection;

    private List<DeviceController> mDevicesControllers = new ArrayList<>();

    private DeviceController mBeckhoffController;
    private DeviceController mM40Controller;
    private DeviceController mFRA800ObjectController;
    private DeviceController mFRA800GeneratorController;
    private DeviceController mPM130Controller;
    private DeviceController mVoltmeterController;
    private DeviceController mTRM201Controller;
    private DeviceController mIKASController;
    private DeviceController mVEHATController;
    private DeviceController mPM130ControllerIA;

    private CS02021Controller mCS02021Controller;

    private int mModule1;
    private int mModule2;
//    private int mModule3;

    private boolean mLastOne;
    private boolean mPlatformOneSelected;

    private boolean mNeededToRunThreads = true;

    public DevicesController(final Context context, Observer observer, OnBroadcastCallback onBroadcastCallback, boolean platformOneSelected) {
        addObserver(observer);
        mRS485Connection = new SerialConnection(context, RS485_DEVICE_NAME, BAUD_RATE,
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

        mPM130ControllerIA = new PM130ControllerIA(observer, modbusController);
        mDevicesControllers.add(mPM130ControllerIA);

        mCS02021Controller = new CS02021Controller(context);

        Thread continuousReadingThread = new Thread(this);
        continuousReadingThread.start();

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_ATTACHED);
        filter.addAction(ACTION_USB_DETACHED);
        final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                                if (Objects.equals(device.getProductName(), RS485_DEVICE_NAME)) {
                                    resetDevicesAttempts();
                                }
//                                } else if (isDeviceFlashMassStorage(device)) {
//                                    Logging.saveFileOnFlashMassStorage(context, mModel.getProtocolForInteraction());
//                                }
                            }
                        } else {
                            if (device != null) {
                                if (Objects.equals(device.getProductName(), RS485_DEVICE_NAME)) {
                                    connectMainBus();
                                }
                            }
                        }
                    }
                } else if (ACTION_USB_DETACHED.equals(action) || ACTION_USB_ATTACHED.equals(action)) {
                    synchronized (this) {
                        disconnectMainBus();
                        connectMainBus();
//                        if (ACTION_USB_ATTACHED.equals(action) && isDeviceFlashMassStorage(device)) {
//                            synchronized (this) {
//                                UsbManager usbManager = (UsbManager) context.getSystemService(USB_SERVICE);
//                                PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
//                                if (usbManager != null) {
//                                    usbManager.requestPermission(device, pi);
//                                }
//                            }
//                        }
                    }
                }
            }
        };
        context.registerReceiver(usbReceiver, filter);
        onBroadcastCallback.onBroadcastUsbReceiver(usbReceiver);
        mPlatformOneSelected = platformOneSelected;
    }

    private boolean isNeededToRunThreads() {
        return mNeededToRunThreads;
    }

    public void setNeededToRunThreads(boolean neededToRunThreads) {
        mNeededToRunThreads = neededToRunThreads;
    }

    @Override
    public void run() {
        while (isNeededToRunThreads()) {
            for (DeviceController deviceController : mDevicesControllers) {
                if (deviceController.needToRead() && deviceController.thereAreAttempts()) {
                    if (deviceController instanceof PM130Controller) {
                        for (int i = 1; i < 5; i++) {
                            deviceController.read(i);
                        }
                    } else {
                        deviceController.read();
                    }
//                    if (deviceController instanceof BeckhoffController) {
                        resetDog();
//                    }
                }
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void resetDog() {
        if (mLastOne) {
            mBeckhoffController.write(WATCH_DOG_REGISTER, 1, 0);
            mLastOne = false;
        } else {
            mBeckhoffController.write(WATCH_DOG_REGISTER, 1, 1);
            mLastOne = true;
        }
    }

    private void resetTimer() {
        mModule1 = 0;
        mBeckhoffController.write(MOD_1_REGISTER, 1, mModule1);
        mModule2 = 0;
        mBeckhoffController.write(MOD_2_REGISTER, 1, mModule2);
        mLastOne = true;
        mBeckhoffController.write(WATCH_DOG_REGISTER, 1, 0);
        mBeckhoffController.write(WATCH_DOG_REGISTER, 1, 1);
        mBeckhoffController.write(RESET_CONNECTION_REGISTER, 1, 1);
        mBeckhoffController.write(RESET_CONNECTION_REGISTER, 1, 0);
    }

    public void diversifyDevices() {
        for (DeviceController devicesController : mDevicesControllers) {
            devicesController.setNeedToRead(false);
        }
        offLight();
    }

    private void offLight() {
        mBeckhoffController.write(LIGHT_REGISTER, 1, 0);
    }

    private void connectMainBus() {
        Logger.withTag("DEBUG_TAG").log("connectMainBus");
        if (!mRS485Connection.isInitiated()) {
            Logger.withTag("DEBUG_TAG").log("!isInitiated");
            try {
                mRS485Connection.initSerialPort();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        resetTimer();
        makeSound();
        onLight();
    }

    private void makeSound() {
        mBeckhoffController.write(SOUND_REGISTER, 1, 1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        mBeckhoffController.write(SOUND_REGISTER, 1, 0);
    }

    private void onLight() {
        mBeckhoffController.write(LIGHT_REGISTER, 1, 1);
    }

    private void resetDevicesAttempts() {
        mBeckhoffController.resetAttempts();
    }

    private void disconnectMainBus() {
        mRS485Connection.closeSerialPort();
    }

    private void onRegisterInTheModule(int numberOfRegister, int module) {
        int mask = (int) Math.pow(2, numberOfRegister);
        try {
            int moduleField = DevicesController.class.getDeclaredField("mModule" + module).getInt(this);
            moduleField |= mask;
            DevicesController.class.getDeclaredMethod(String.format("%s%d%s", "writeToMod", module, "Register"), int.class).invoke(this, moduleField);
            DevicesController.class.getDeclaredField("mModule" + module).set(this, moduleField);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
        }
        Logger.withTag("DEBUG_TAG").log("numberOfRegister=" + numberOfRegister + " module=" + module);
        Logger.withTag("DEBUG_TAG").log("1=" + mModule1 + " 2=" + mModule2);
    }

    private void offRegisterInTheModule(int numberOfRegister, int module) {
        int mask = ~(int) Math.pow(2, numberOfRegister);
        try {
            int moduleField = DevicesController.class.getDeclaredField("mModule" + module).getInt(this);
            moduleField &= mask;
            DevicesController.class.getDeclaredMethod(String.format("%s%d%s", "writeToMod", module, "Register"), int.class).invoke(this, moduleField);
            DevicesController.class.getDeclaredField("mModule" + module).set(this, moduleField);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
        }
        Logger.withTag("DEBUG_TAG").log("numberOfRegister=" + numberOfRegister + " module=" + module);
        Logger.withTag("DEBUG_TAG").log("1=" + mModule1 + " 2=" + mModule2);
    }

    private void writeToMod1Register(int value) {
        mBeckhoffController.write(MOD_1_REGISTER, 1, value);
    }

    private void writeToMod2Register(int value) {
        mBeckhoffController.write(MOD_2_REGISTER, 1, value);
    }

//    private void writeToMod3Register(int value) {
//        mBeckhoffController.write(MOD_3_REGISTER, 1, value);
//    }

    public void initDevicesFrom1To3And10And12Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        set5000PointsM40();
        mM40Controller.setNeedToRead(true);
        mM40Controller.resetAttempts();
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800ObjectController.resetAttempts();
        mFRA800GeneratorController.setNeedToRead(true);
        mFRA800GeneratorController.resetAttempts();
        mPM130Controller.setNeedToRead(true);
        mPM130Controller.resetAttempts();
        mVEHATController.setNeedToRead(true);
        mVEHATController.resetAttempts();
        mTRM201Controller.setNeedToRead(true);
        mTRM201Controller.resetAttempts();
    }

    public void startObject() {
        mFRA800ObjectController.write(CONTROL_REGISTER, 1, 0b10);
    }

    public void startReversObject() {
        mFRA800ObjectController.write(CONTROL_REGISTER, 1, 0b100);
    }

    public void startGenerator() {
        mFRA800GeneratorController.write(CONTROL_REGISTER, 1, 0b10);
    }

    public void startReversGenerator() {
        mFRA800GeneratorController.write(CONTROL_REGISTER, 1, 0b100);
    }

    public void stopObject() {
        mFRA800ObjectController.write(CONTROL_REGISTER, 1, 0);
    }

    public void stopGenerator() {
        mFRA800GeneratorController.write(CONTROL_REGISTER, 1, 0);
    }

    public void onKMsFrom1To3And10And12Group() {
        onRegisterInTheModule(2, 1);
        if (mPlatformOneSelected) {
            onRegisterInTheModule(7, 1);
        } else {
            onRegisterInTheModule(6, 1);
        }
        onRegisterInTheModule(3, 1);
        onRegisterInTheModule(4, 2);
    }

    public void setObjectParams(int voltageMax, int fMax, int fCur) {
        mFRA800ObjectController.write(MAX_VOLTAGE_REGISTER, 1, voltageMax);
        mFRA800ObjectController.write(MAX_FREQUENCY_REGISTER, 1, fMax);
        mFRA800ObjectController.write(CURRENT_FREQUENCY_REGISTER, 1, fCur);
    }

    public void setGeneratorParams(int voltageMax, int fMax, int fCur) {
        mFRA800GeneratorController.write(MAX_VOLTAGE_REGISTER, 1, voltageMax);
        mFRA800GeneratorController.write(MAX_FREQUENCY_REGISTER, 1, fMax);
        mFRA800GeneratorController.write(CURRENT_FREQUENCY_REGISTER, 1, fCur);
    }

    public void setGeneratorUMax(int voltageMax) {
        mFRA800GeneratorController.write(MAX_VOLTAGE_REGISTER, 1, voltageMax);
    }

    public void onLoad() {
        if (mPlatformOneSelected) {
            onRegisterInTheModule(11, 1);
        } else {
            onRegisterInTheModule(10, 1);
        }
    }

    public void setGeneratorFCur(Integer fCur) {
        mFRA800GeneratorController.write(CURRENT_FREQUENCY_REGISTER, 1, fCur);
    }

    public void offKMsFrom1To3And10And12Group() {
        offRegisterInTheModule(2, 1);
        if (mPlatformOneSelected) {
            offRegisterInTheModule(7, 1);
        } else {
            offRegisterInTheModule(6, 1);
        }
        offRegisterInTheModule(3, 1);
        offRegisterInTheModule(4, 2);
        offRegisterInTheModule(4, 1);
        offRegisterInTheModule(5, 1);
    }

    public void on40To5() {
        onRegisterInTheModule(4, 1);
        offRegisterInTheModule(3, 1);
        offRegisterInTheModule(5, 1);
    }

    public void on5To5() {
        onRegisterInTheModule(5, 1);
        offRegisterInTheModule(4, 1);
    }

    public void offLoad() {
        if (mPlatformOneSelected) {
            offRegisterInTheModule(11, 1);
        } else {
            offRegisterInTheModule(10, 1);
        }
    }

    public void initDevicesFrom4Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800ObjectController.resetAttempts();
        mPM130Controller.setNeedToRead(true);
        mPM130Controller.resetAttempts();
    }

    public void onKMsFrom4And7And13Group() {
        onRegisterInTheModule(1, 1);
        onRegisterInTheModule(3, 2);
        onRegisterInTheModule(3, 1);
        if (mPlatformOneSelected) {
            onRegisterInTheModule(7, 1);
        } else {
            onRegisterInTheModule(6, 1);
        }
    }

    public void setObjectUMax(int voltageMax) {
        mFRA800ObjectController.write(MAX_VOLTAGE_REGISTER, 1, voltageMax);
    }

    public void on200To5() {
        onRegisterInTheModule(3, 1);
        offRegisterInTheModule(4, 1);
    }

    public void offKMsFrom4And7And13Group() {
        offRegisterInTheModule(1, 1);
        offRegisterInTheModule(3, 2);
        offRegisterInTheModule(3, 1);
        if (mPlatformOneSelected) {
            offRegisterInTheModule(7, 1);
        } else {
            offRegisterInTheModule(6, 1);
        }
        offRegisterInTheModule(4, 1);
        offRegisterInTheModule(5, 1);
    }

    public void initDevicesFrom5And17Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        mIKASController.setNeedToRead(true);
        mIKASController.resetAttempts();
        mTRM201Controller.setNeedToRead(true);
        mTRM201Controller.resetAttempts();
    }

    public void onKMsFrom5And17Group() {
        if (mPlatformOneSelected) {
            onRegisterInTheModule(9, 1);
        } else {
            onRegisterInTheModule(8, 1);
        }
    }

    public void startMeasuringAB(float supposedValue) {
        mIKASController.write(MEASURABLE_TYPE_REGISTER, MEASURABLE_TYPE_AB);
        mIKASController.write(TYPE_OF_RANGE_R_REGISTER, getRangeType(supposedValue));
        mIKASController.write(RANGE_R_REGISTER, supposedValue);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        mIKASController.write(START_MEASURABLE_REGISTER, 0x01);
    }

    private int getRangeType(float supposedValue) {
        int rangeType = 1;
        if (supposedValue < 8) {
            rangeType = RANGE_TYPE_LESS_8;
        } else if (supposedValue > 8 && supposedValue < 200) {
            rangeType = RANGE_TYPE_MORE_8_LESS_200;
        } else if (supposedValue > 200) {
            rangeType = RANGE_TYPE_MORE_200;
        }
        return rangeType;
    }

    public void startMeasuringBC(float supposedValue) {
        mIKASController.write(MEASURABLE_TYPE_REGISTER, MEASURABLE_TYPE_BC);
        mIKASController.write(TYPE_OF_RANGE_R_REGISTER, getRangeType(supposedValue));
        mIKASController.write(RANGE_R_REGISTER, supposedValue);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        mIKASController.write(START_MEASURABLE_REGISTER, 0x01);
    }

    public void startMeasuringAC(float supposedValue) {
        mIKASController.write(MEASURABLE_TYPE_REGISTER, MEASURABLE_TYPE_AC);
        mIKASController.write(TYPE_OF_RANGE_R_REGISTER, getRangeType(supposedValue));
        mIKASController.write(RANGE_R_REGISTER, supposedValue);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        mIKASController.write(START_MEASURABLE_REGISTER, 0x01);
    }

    public void offKMsFrom5And17Group() {
        if (mPlatformOneSelected) {
            offRegisterInTheModule(9, 1);
        } else {
            offRegisterInTheModule(8, 1);
        }
    }

    public void initDevicesFrom6Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800ObjectController.resetAttempts();
        mPM130Controller.setNeedToRead(true);
        mPM130Controller.resetAttempts();
        mVoltmeterController.setNeedToRead(true);
        mVoltmeterController.resetAttempts();
    }

    public void onKMsFrom6Group() {
        onRegisterInTheModule(0, 1);
        onRegisterInTheModule(5, 2);
        onRegisterInTheModule(0, 2);
        onRegisterInTheModule(2, 2);
    }

    public void offGround() {
        offRegisterInTheModule(2, 2);
    }

    public void offKMsFrom6Group() {
        offRegisterInTheModule(0, 1);
        offRegisterInTheModule(5, 2);
        offRegisterInTheModule(0, 2);
    }

    public void initDevicesFrom7To8Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800ObjectController.resetAttempts();
        mPM130Controller.setNeedToRead(true);
        mPM130Controller.resetAttempts();
        mVEHATController.setNeedToRead(true);
        mVEHATController.resetAttempts();
        mTRM201Controller.setNeedToRead(true);
        mTRM201Controller.resetAttempts();
    }

    public void onKMsFrom8To9Group() {
        onRegisterInTheModule(2, 1);
        if (mPlatformOneSelected) {
            onRegisterInTheModule(7, 1);
        } else {
            onRegisterInTheModule(6, 1);
        }
        onRegisterInTheModule(3, 1);
    }

    public void offKMsFrom8To9Group() {
        offRegisterInTheModule(2, 1);
        if (mPlatformOneSelected) {
            offRegisterInTheModule(7, 1);
        } else {
            offRegisterInTheModule(6, 1);
        }
        offRegisterInTheModule(3, 1);
        offRegisterInTheModule(4, 1);
        offRegisterInTheModule(5, 1);
    }

    public void initDevicesFrom9Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800ObjectController.resetAttempts();
        mPM130Controller.setNeedToRead(true);
        mPM130Controller.resetAttempts();
        mTRM201Controller.setNeedToRead(true);
        mTRM201Controller.resetAttempts();
    }

    public void initDevicesFrom11Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        mTRM201Controller.setNeedToRead(true);
        mTRM201Controller.resetAttempts();
    }

    public void onKMsFrom11Group() {
        onRegisterInTheModule(1, 2);
        onRegisterInTheModule(2, 2);
    }

    public void offKMsFrom11Group() {
        offRegisterInTheModule(1, 2);
        offRegisterInTheModule(2, 2);
    }

    public void setUMgr(int u) {
        mCS02021Controller.setVoltage(u);
    }

    public float[] readDataMgr() {
        return mCS02021Controller.readData();
    }

    public void setObjectFCur(int fCur) {
        mFRA800ObjectController.write(CURRENT_FREQUENCY_REGISTER, 1, fCur);
    }

    public void initDevicesFrom13Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800ObjectController.resetAttempts();
        mPM130Controller.setNeedToRead(true);
        mPM130Controller.resetAttempts();
        set5000PointsM40();
        mM40Controller.setNeedToRead(true);
        mM40Controller.resetAttempts();
        mTRM201Controller.setNeedToRead(true);
        mTRM201Controller.resetAttempts();
        mVEHATController.setNeedToRead(true);
        mVEHATController.resetAttempts();
    }

    public void initDevicesFrom14Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        set5000PointsM40();
        mM40Controller.setNeedToRead(true);
        mM40Controller.resetAttempts();
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800ObjectController.resetAttempts();
        mFRA800GeneratorController.setNeedToRead(true);
        mFRA800GeneratorController.resetAttempts();
        mVEHATController.setNeedToRead(true);
        mVEHATController.resetAttempts();
    }

    public void onKMsFrom14Group() {
        onRegisterInTheModule(2, 1);
        onRegisterInTheModule(3, 1);
        if (mPlatformOneSelected) {
            onRegisterInTheModule(11, 1);
        } else {
            onRegisterInTheModule(10, 1);
        }
    }

    public void onObject() {
        if (mPlatformOneSelected) {
            onRegisterInTheModule(7, 1);
        } else {
            onRegisterInTheModule(6, 1);
        }
    }

    public void offObject() {
        if (mPlatformOneSelected) {
            offRegisterInTheModule(7, 1);
        } else {
            offRegisterInTheModule(6, 1);
        }
    }

    public void offKMsFrom14Group() {
        offRegisterInTheModule(2, 1);
        offRegisterInTheModule(3, 1);
        if (mPlatformOneSelected) {
            offRegisterInTheModule(11, 1);
        } else {
            offRegisterInTheModule(10, 1);
        }
    }

    public void initDevicesFrom15Group() {
        connectMainBus();
        mBeckhoffController.setNeedToRead(true);
        mBeckhoffController.resetAttempts();
        mFRA800ObjectController.setNeedToRead(true);
        mFRA800ObjectController.resetAttempts();
        mPM130ControllerIA.setNeedToRead(true);
        mPM130ControllerIA.resetAttempts();
        set5000PointsM40();
        mM40Controller.setNeedToRead(true);
        mM40Controller.resetAttempts();
    }

    public void onKMsFrom15Group() {
        onRegisterInTheModule(2, 1);
        onRegisterInTheModule(3, 1);
    }

    public void offKMsFrom15Group() {
        offRegisterInTheModule(2, 1);
        if (mPlatformOneSelected) {
            offRegisterInTheModule(7, 1);
        } else {
            offRegisterInTheModule(6, 1);
        }
        offRegisterInTheModule(3, 1);
        offRegisterInTheModule(4, 1);
        offRegisterInTheModule(5, 1);
    }

    public void set5000PointsM40() {
        mM40Controller.write(AVERAGING_REGISTER, (short) 5000);
    }

    public void set100PointsM40() {
        mM40Controller.write(AVERAGING_REGISTER, (short) 100);
    }
}