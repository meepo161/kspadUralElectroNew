package ru.avem.kspad.logging;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import ru.avem.kspad.R;
import ru.avem.kspad.database.model.Protocol;
import ru.avem.kspad.utils.Logger;

import static android.content.Context.USB_SERVICE;
import static ru.avem.kspad.utils.Utils.RU_LOCALE;
import static ru.avem.kspad.utils.Utils.formatRealNumber;

public class Logging {
    private static final String ACTION_USB_PERMISSION =
            "ru.avem.coilstestingfacility.USB_PERMISSION";

    public static void preview(Activity activity, Protocol protocol) {
        if (requestPermission(activity)) {
            if (protocol != null) {
                new SaveTask(2, activity).execute(protocol.getId());
            } else {
                Toast.makeText(activity, "Выберите протокол из выпадающего списка", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Ошибка доступа. Дайте разрешение на запись.", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean requestPermission(Activity activity) {
        boolean hasPermission = (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    112);
            return false;
        } else {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/protocol";
            File storageDir = new File(path);
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    private static class SaveTask extends AsyncTask<Long, Void, String> {
        private ProgressDialog dialog;
        private int mType;
        private final Context mContext;

        SaveTask(int type, Context context) {
            mType = type;
            mContext = context;
            dialog = new ProgressDialog(context);
            if (mType == 1) {
                dialog.setMessage("Идёт сохранение...");
            } else if (mType == 2) {
                dialog.setMessage("Подождите...");
            }
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(Long... longs) {
            String fileName = null;
            Protocol protocol;
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.beginTransaction();
                protocol = realm.where(Protocol.class).equalTo("mId", longs[0]).findFirst();
                realm.commitTransaction();

                if (protocol != null) {
                    if (mType == 1) {
                        fileName = writeWorkbookToMassStorage(protocol, mContext);
                    } else if (mType == 2) {
                        fileName = writeWorkbookToInternalStorage(protocol, mContext);
                    }
                }
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            dialog.dismiss();
            if (fileName != null) {
                if (mType == 1) {
                    Toast.makeText(mContext, "Сохранено в " + fileName, Toast.LENGTH_SHORT).show();
                } else if (mType == 2) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file = new File(fileName);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = FileProvider.getUriForFile(mContext,
                            mContext.getApplicationContext().getPackageName() + ".provider",
                            file);
                    String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            "xlsx");
                    intent.setDataAndType(uri, mimeType);
                    mContext.startActivity(intent);
                }
            } else {
                Toast.makeText(mContext, "Выберите протокол из выпадающего списка", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private static String writeWorkbookToMassStorage(Protocol protocol, Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM(HH-mm-ss)", RU_LOCALE);
        String fileName = "protocol-" + sdf.format(System.currentTimeMillis()) + ".xlsx";
        UsbMassStorageDevice[] massStorageDevices = UsbMassStorageDevice.getMassStorageDevices(context);
        UsbMassStorageDevice currentDevice = massStorageDevices[0];
        try {
            currentDevice.init();
            FileSystem currentFS = currentDevice.getPartitions().get(0).getFileSystem();

            UsbFile root = currentFS.getRootDirectory();
            UsbFile file = root.createFile(fileName);
            ByteArrayOutputStream out = convertProtocolToWorkbook(protocol, context);
            file.write(0, ByteBuffer.wrap(out.toByteArray()));
            file.close();
            fileName = currentFS.getVolumeLabel() + "/" + fileName;
            currentDevice.close();
        } catch (IOException e) {
            Log.e("TAG", "setup device error", e);
        }
        return fileName;
    }

    private static ByteArrayOutputStream convertProtocolToWorkbook(Protocol protocol, Context context) throws IOException {
        Resources res = context.getResources();
        InputStream inputStream = protocol.isPSI() ? res.openRawResource(R.raw.template_kspad_psi) : res.openRawResource(R.raw.template_kspad_pi);
        Workbook wb = new XSSFWorkbook(inputStream);
        try {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 0; i < 100; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < 10; j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null && (cell.getCellTypeEnum() == CellType.STRING)) {
                            switch (cell.getStringCellValue()) {
                                case "$PROTOCOL_NUMBER$":
                                    long id = protocol.getId();
                                    if (id != 0) {
                                        cell.setCellValue(id + "");
                                    } else {
                                        cell.setCellValue("");
                                    }
                                    break;
                                case "$OBJECT$":
                                    String subjectName = protocol.getSubjectName();
                                    if (subjectName != null) {
                                        cell.setCellValue(subjectName);
                                    } else {
                                        cell.setCellValue("");
                                    }
                                    break;
                                case "$SERIAL_NUMBER$":
                                    String serialNumber = protocol.getSerialNumber();
                                    if ((serialNumber != null) && !serialNumber.isEmpty()) {
                                        cell.setCellValue(serialNumber);
                                    } else {
                                        cell.setCellValue("");
                                    }
                                    break;
                                case "$N1":
                                    String Winding = protocol.getWinding();
                                    if (Winding != null) {
                                        cell.setCellValue(Winding);
                                    } else {
                                        cell.setCellValue("");
                                    }
                                    break;
                                case "$N3":
                                    setNumberCellValue(cell, protocol.getPN() / 1000f);
                                    break;
                                case "$N4":
                                    setNumberCellValue(cell, protocol.getUN());
                                    break;
                                case "$N5":
                                    setNumberCellValue(cell, protocol.getIN());
                                    break;
                                case "$N6":
                                    if (protocol.getIN() != -1) {
                                        setNumberCellValue(cell, protocol.getFN());
                                    }
                                    break;
                                case "$N7":
                                    setNumberCellValue(cell, protocol.getVN());
                                    break;
                                case "$N8":
                                    setNumberCellValue(cell, protocol.getSN());
                                    break;
                                case "$N9":
                                    setNumberCellValue(cell, protocol.getEfficiencyN());
                                    break;
                                case "$N12":
                                    setNumberCellValue(cell, protocol.getMN());
                                    break;
                                case "$N47":
                                    setNumberCellValue(cell, protocol.getVibration());
                                    break;
                                case "$N48":
                                    setNumberCellValue(cell, protocol.getNoise());
                                    break;
                                case "$N49":
                                    setNumberCellValue(cell, protocol.getTempHeating());
                                    break;
                                case "$N50":
                                    setNumberCellValue(cell, protocol.getRIkas());
                                    break;
                                case "$N51":
                                    setNumberCellValue(cell, protocol.getRMgr());
                                    break;
                                case "$N64":
                                    setNumberCellValue(cell, protocol.getUViu());
                                    break;
                                case "$N65":
                                    setNumberCellValue(cell, protocol.getTViu());
                                    break;
                                case "$N69":
                                    setNumberCellValue(cell, protocol.getSpecifiedIOverloadR());
                                    break;
                                case "$R3":
                                    setNumberCellValue(cell, protocol.getP2R());
                                    break;
                                case "$R4":
                                    setNumberCellValue(cell, protocol.getUR());
                                    break;
                                case "$R5":
                                    setNumberCellValue(cell, protocol.getIR());
                                    break;
                                case "$R6":
                                    setNumberCellValue(cell, protocol.getFN());
                                    break;
                                case "$R7":
                                    setNumberCellValue(cell, protocol.getVR());
                                    break;
                                case "$R8":
                                    setNumberCellValue(cell, protocol.getSR());
                                    break;
                                case "$R9":
                                    setNumberCellValue(cell, protocol.getNuR());
                                    break;
                                case "$R10":
                                    setNumberCellValue(cell, protocol.getCosR());
                                    break;
                                case "$R11":
                                    setNumberCellValue(cell, protocol.getP1R());
                                    break;
                                case "$R12":
                                    setNumberCellValue(cell, protocol.getMR());
                                    break;
                                case "$R13":
                                    float MStartR = protocol.getMStartR();
                                    float MR13 = protocol.getMR();
                                    if ((MStartR != -1) && (MR13 != -1)) {
                                        float MStartToMR = MStartR / MR13;
                                        setNumberCellValue(cell, MStartToMR);
                                    } else {
                                        cell.setCellValue("");
                                    }
                                    break;
                                case "$R14":
                                    float MMaxR = protocol.getMMaxR();
                                    float MR14 = protocol.getMR();
//                                    if ((MMaxR != -1) && (MR14 != -1)) {
//                                        float MMaxToMR = MMaxR / MR14;
//                                    } else {
//                                        cell.setCellValue("");
//                                    }
                                    setNumberCellValue(cell, MMaxR / MR14);
                                    break;
                                case "$R15":
                                    float MMinR = protocol.getMMinR();
                                    float MR15 = protocol.getMR();
//                                    if ((MMinR != -1) && (MR15 != -1)) {
//                                        float MMinRToMR = MMinR / MR15;
//                                        setNumberCellValue(cell, MMinRToMR);
//                                    } else {
//                                        cell.setCellValue("");f4r
//                                    }
                                    setNumberCellValue(cell, MMinR / MR15);
                                    break;
                                case "$R16":
                                    float IStartR = protocol.getIStartR();
                                    float IR = protocol.getIR();
                                    if ((IStartR != -1) && (IR != -1)) {
                                        float IStartRToIR = IStartR / IR;
                                        setNumberCellValue(cell, IStartRToIR);
                                    } else {
                                        cell.setCellValue("");
                                    }
                                    break;
                                case "$R18":
                                    setNumberCellValue(cell, protocol.getI13IdleR());
                                    break;
                                case "$R19":
                                    setNumberCellValue(cell, protocol.getP13IdleR() * 1000);
                                    break;
                                case "$R20":
                                    setNumberCellValue(cell, protocol.getI12IdleR());
                                    break;
                                case "$R21":
                                    setNumberCellValue(cell, protocol.getP12IdleR() * 1000);
                                    break;
                                case "$R22":
                                    setNumberCellValue(cell, protocol.getI11IdleR());
                                    break;
                                case "$R23":
                                    setNumberCellValue(cell, protocol.getP11IdleR() * 1000);
                                    break;
                                case "$R24":
                                    setNumberCellValue(cell, protocol.getI10IdleR());
                                    break;
                                case "$R25":
                                    setNumberCellValue(cell, protocol.getP10IdleR() * 1000);
                                    break;
                                case "$R26":
                                    setNumberCellValue(cell, protocol.getI09IdleR());
                                    break;
                                case "$R27":
                                    setNumberCellValue(cell, protocol.getP09IdleR() * 1000);
                                    break;
                                case "$R28":
                                    setNumberCellValue(cell, protocol.getI08IdleR());
                                    break;
                                case "$R29":
                                    setNumberCellValue(cell, protocol.getP08IdleR() * 1000);
                                    break;
                                case "$R30":
                                    setNumberCellValue(cell, protocol.getI07IdleR());
                                    break;
                                case "$R31":
                                    setNumberCellValue(cell, protocol.getP07IdleR() * 1000);
                                    break;
                                case "$R32":
                                    setNumberCellValue(cell, protocol.getI06IdleR());
                                    break;
                                case "$R33":
                                    setNumberCellValue(cell, protocol.getP06IdleR() * 1000);
                                    break;
                                case "$R34":
                                    setNumberCellValue(cell, protocol.getI05IdleR());
                                    break;
                                case "$R35":
                                    setNumberCellValue(cell, protocol.getP05IdleR() * 1000);
                                    break;
                                case "$R37":
                                    setNumberCellValue(cell, protocol.getI10SCR());
                                    break;
                                case "$R38":
                                    setNumberCellValue(cell, protocol.getP10SCR() * 1000);
                                    break;
                                case "$R39":
                                    setNumberCellValue(cell, protocol.getI09SCR());
                                    break;
                                case "$R40":
                                    setNumberCellValue(cell, protocol.getP09SCR() * 1000);
                                    break;
                                case "$R41":
                                    setNumberCellValue(cell, protocol.getI08SCR());
                                    break;
                                case "$R42":
                                    setNumberCellValue(cell, protocol.getP08SCR() * 1000);
                                    break;
                                case "$R43":
                                    setNumberCellValue(cell, protocol.getI07SCR());
                                    break;
                                case "$R44":
                                    setNumberCellValue(cell, protocol.getP07SCR() * 1000);
                                    break;
                                case "$R45":
                                    setNumberCellValue(cell, protocol.getI06SCR());
                                    break;
                                case "$R46":
                                    setNumberCellValue(cell, protocol.getP06SCR() * 1000);
                                    break;
                                case "$R47":
                                    List<Float> vibrations = new ArrayList<>();
                                    vibrations.add(protocol.getX1R());
                                    vibrations.add(protocol.getY1R());
                                    vibrations.add(protocol.getZ1R());
                                    vibrations.add(protocol.getX2R());
                                    vibrations.add(protocol.getY2R());
                                    vibrations.add(protocol.getZ2R());
                                    Collections.sort(vibrations);
                                    Collections.reverse(vibrations);
                                    setNumberCellValue(cell, vibrations.get(0));
                                    break;
                                case "$R48":
                                    setNumberCellValue(cell, protocol.getNoiseR());
                                    break;
                                case "$R49":
                                    Logger.withTag("R49").log(String.format("R горячее: %f, R20: %f", protocol.getIkasRHotR(), protocol.getIkasR20R()));
                                    if ((protocol.getIkasRHotR() > 0) && (protocol.getIkasR20R() > 0)) {
                                        float ikasRHotHalf = protocol.getIkasRHotR() / 2f;
                                        double value = ((ikasRHotHalf / protocol.getIkasR20R() - 1) / 0.00393) + 20;
                                        setNumberCellValue(cell, value);
                                    } else {
                                        cell.setCellValue("");
                                    }
                                    break;
                                case "$R50":
                                    setNumberCellValue(cell, protocol.getIkasR20R());
                                    break;
                                case "$R51":
                                    setNumberCellValue(cell, protocol.getMgrR());
                                    break;
                                case "$R52":
                                    setNumberCellValue(cell, protocol.getPStR() * 1000);
                                    break;
                                case "$R53":
                                    calculatePstator(protocol, cell);
                                    break;
                                case "$R54":
                                    calculateProtor(protocol, cell);
                                    break;
                                case "$R55":
                                    setNumberCellValue(cell, protocol.getPMechR() * 1000);
                                    break;
                                case "$R56":
                                    calculatePadd(protocol, cell);
                                    break;
                                case "$R57":
                                    calculatePsum(protocol, cell);
                                    break;
                                case "$R58":
                                    setNumberCellValue(cell, protocol.getUTurnR());
                                    break;
                                case "$R60":
                                    setNumberCellValue(cell, protocol.getI1MVZR());
                                    break;
                                case "$R61":
                                    setNumberCellValue(cell, protocol.getI2MVZR());
                                    break;
                                case "$R62":
                                    setNumberCellValue(cell, protocol.getI3MVZR());
                                    break;
                                case "$R64":
                                    setNumberCellValue(cell, protocol.getUViuR());
                                    break;
                                case "$R65":
                                    setNumberCellValue(cell, protocol.getTViuR());
                                    break;
                                case "$R67":
                                    setNumberCellValue(cell, protocol.getVOverloadR());
                                    break;
                                case "$R68":
                                    setNumberCellValue(cell, protocol.getTOverloadR());
                                    break;
                                case "$R69":
                                    setNumberCellValue(cell, protocol.getIOverloadR());
                                    break;
                                case "$R70":
//                                    if ((protocol.getTempEngineR() > 0) && (protocol.getTempAmbientR() > 0)) {
//                                        setNumberCellValue(cell, protocol.getTempEngineR() - protocol.getTempAmbientR());
//                                    }
                                    setNumberCellValue(cell, protocol.getTempEngineR());
                                    break;
                                case "$R71":
                                    setNumberCellValue(cell, protocol.getIkasRColdR() / 2f);
                                    break;
                                case "$R72":
                                    setNumberCellValue(cell, protocol.getIkasRHotR() / 2f);
                                    break;
                                case "$POS1$":
                                    cell.setCellValue(protocol.getPosition1());
                                    break;
                                case "$POS2$":
                                    cell.setCellValue(protocol.getPosition2());
                                    break;
                                case "$POS1NAME$":
                                    cell.setCellValue(String.format(RU_LOCALE, "/%s/", protocol.getPosition1FullName()));
                                    break;
                                case "$POS2NAME$":
                                    cell.setCellValue(String.format(RU_LOCALE, "/%s/", protocol.getPosition2FullName()));
                                    break;
                                case "$DATE$":
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy", RU_LOCALE);
                                    cell.setCellValue(sdf.format(protocol.getDate()));
                                    break;
                                default:
                                    if (cell.getStringCellValue().contains("$N") || cell.getStringCellValue().contains("$R")) {
                                        cell.setCellValue("");
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                wb.write(out);
            } finally {
                out.close();
            }
            return out;
        } finally {
            wb.close();
        }
    }

    private static void calculatePsum(Protocol protocol, Cell cell) {
        float P1R = protocol.getP1R() * 1000;
        float P2R = protocol.getP2R() * 1000;
        if ((P1R > 0) && (P2R > 0)) {
            float Psum = P1R - P2R;
            setNumberCellValue(cell, Psum);
        } else {
            cell.setCellValue("");
        }
    }

    private static void calculatePstator(Protocol protocol, Cell cell) {
        float IR = protocol.getIR();
        float RIkas = protocol.getIkasRHotR();
        if ((IR > 0) && (RIkas > 0)) {
            double Pstat = 3 * IR * IR * (RIkas / 2.0);
            setNumberCellValue(cell, Pstat);
        } else {
            cell.setCellValue("");
        }
    }

    private static void calculateProtor(Protocol protocol, Cell cell) {
        double Pstat = -1.;

        float IR = protocol.getIR();
        float RIkas = protocol.getIkasRHotR();
        if ((IR > 0) && (RIkas > 0)) {
            Pstat = 3 * IR * IR * (RIkas / 2.0);
        }

        double Psteel = protocol.getPStR() * 1000;
        float P1R = protocol.getP1R() * 1000;
        float SR = protocol.getSR() / 100f;

        if ((P1R > 0) && (Psteel > 0) && (Pstat > 0) && (SR > 0)) {
            double Protor = (P1R - Psteel - Pstat) * SR;
            setNumberCellValue(cell, Protor);
        } else {
            cell.setCellValue("");
        }
    }

    private static void calculatePadd(Protocol protocol, Cell cell) {
        double Pstat = -1.;
        double Psum = -1.;

        float IR = protocol.getIR();
        float RIkas = protocol.getIkasRHotR();
        if ((IR > 0) && (RIkas > 0)) {
            Pstat = 3 * IR * IR * (RIkas / 2.0);
        }

        double Psteel = protocol.getPStR() * 1000;
        double Pmech = protocol.getPMechR() * 1000;
        float P1R = protocol.getP1R() * 1000;
        float P2R = protocol.getP2R() * 1000;
        if ((P1R > 0) && (P2R > 0)) {
            Psum = P1R - P2R;
        }

        float SR = protocol.getSR() / 100f;

        if ((Psum > 0) && (Psteel > 0) && (Pstat > 0) && (SR > 0) && (Pmech > 0)) {
            double Protor = (P1R - Psteel - Pstat) * SR;
            double Padd = Psum - (Pstat + Protor + Psteel + Pmech);
            setNumberCellValue(cell, Padd);
        } else {
            cell.setCellValue("");
        }
    }

    private static <T> void setNumberCellValue(Cell cell, T value) {
        if (value instanceof Double) {
            if ((Double) value >= 0.0) {
                cell.setCellValue(formatRealNumber((Double) value));
            } else {
                cell.setCellValue("");
            }
        } else if (value instanceof Float) {
            if ((Float) value >= 0f) {
                cell.setCellValue(formatRealNumber((Float) value));
            } else {
                cell.setCellValue("");
            }
        } else if (value instanceof Integer) {
            if ((Integer) value >= 0) {
                cell.setCellValue(value + "");
            } else {
                cell.setCellValue("");
            }
        }
    }

    private static String writeWorkbookToInternalStorage(Protocol protocol, Context context) {
        clearDirectory(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/protocol"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM(HH-mm-ss)", RU_LOCALE);
        String fileName = "protocol-" + sdf.format(System.currentTimeMillis()) + ".xlsx";
        try {
            ByteArrayOutputStream out = convertProtocolToWorkbook(protocol, context);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/protocol", fileName);
            FileOutputStream fileOut = new FileOutputStream(file);
            out.writeTo(fileOut);
            fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/protocol/" + fileName;
            out.close();
            fileOut.close();
        } catch (IOException e) {
            Log.e("TAG", " error", e);
        }
        return fileName;
    }

    private static void clearDirectory(File directory) {
        for (File child : directory.listFiles()) {
            child.delete();
        }
    }

    public static void saveFileOnFlashMassStorage(Context context, Protocol protocol) {
        if (checkMassStorageConnection(context)) {
            new SaveTask(1, context).execute(protocol.getId());
        } else {
            new AlertDialog.Builder(
                    context)
                    .setTitle("Нет подключения")
                    .setCancelable(false)
                    .setMessage("Подключите USB FLASH накопитель с файловой системой FAT32 и предоставьте доступ к нему")
                    .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private static boolean checkMassStorageConnection(Context context) {
        UsbMassStorageDevice[] massStorageDevices = UsbMassStorageDevice.getMassStorageDevices(context);
        if (massStorageDevices.length != 1) {
            return false;
        } else {
            UsbManager usbManager = (UsbManager) context.getSystemService(USB_SERVICE);
            if (usbManager.hasPermission(massStorageDevices[0].getUsbDevice())) {
                return true;
            } else {
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(massStorageDevices[0].getUsbDevice(), pi);
                return false;
            }
        }
    }

    public static boolean isDeviceFlashMassStorage(UsbDevice device) {
        int interfaceCount = device.getInterfaceCount();
        for (int i = 0; i < interfaceCount; i++) {
            UsbInterface usbInterface = device.getInterface(i);
            int INTERFACE_SUBCLASS = 6;
            int INTERFACE_PROTOCOL = 80;
            if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_MASS_STORAGE
                    && usbInterface.getInterfaceSubclass() == INTERFACE_SUBCLASS
                    && usbInterface.getInterfaceProtocol() == INTERFACE_PROTOCOL) {
                return true;
            }
        }
        return false;
    }
}