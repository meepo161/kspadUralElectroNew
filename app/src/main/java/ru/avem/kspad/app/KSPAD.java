package ru.avem.kspad.app;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.avem.kspad.database.model.Subject;

public class KSPAD extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .schemaVersion(2)
                .initialData(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        int i = 1;
                        Subject w800 = realm.createObject(Subject.class, i++);
                        w800.setName("АДМС71В4УХЛ1");
                        w800.setPN(750f);
                        w800.setUN(380);
                        w800.setIN(2.2f);
                        w800.setMN(10f);
                        w800.setVN(1350);
                        w800.setWinding("звезда");
                        w800.setSN(5f);
                        w800.setEfficiencyN(0.7f);
                        w800.setFN(50f);
                        w800.setUMgr(1000);
                        w800.setRMgr(10f);
                        w800.setRIkas(17f);
                        w800.setUViu(1200);
                        w800.setTViu(30);
                        w800.setIViu(0.5f);
                        w800.setTBreakInIdle(10);
                        w800.setNumOfStagesIdle(9);
                        w800.setTOnStageIdle(5);
                        w800.setNumOfStagesSc(5);
                        w800.setTOnStageSc(10);
                        w800.setTHeating(60);
                        w800.setTempHeating(45);
                        w800.setNumOfStagesPerformance(7);
                        w800.setZ1Performance(72);
                        w800.setZ2Performance(72);
                        w800.setTBreakInPerformance(60);
                        w800.setTPerformance(300);
                        w800.setKOverloadI(1.5f);
                        w800.setNoise(50f);
                        w800.setVibration(1.8f);

                        Subject w2200 = realm.createObject(Subject.class, i++);
                        w2200.setName("АД112МА-ОМ2");
                        w2200.setPN(2200f);
                        w2200.setUN(380);
                        w2200.setIN(6.3f);
                        w2200.setMN(30.4f);
                        w2200.setVN(705);
                        w2200.setWinding("звезда");
                        w2200.setSN(6f);
                        w2200.setEfficiencyN(0.75f);
                        w2200.setFN(50f);
                        w2200.setUMgr(1000);
                        w2200.setRMgr(10f);
                        w2200.setRIkas(2f);
                        w2200.setUViu(1750);
                        w2200.setTViu(60);
                        w2200.setIViu(0.5f);
                        w2200.setTBreakInIdle(1800);
                        w2200.setNumOfStagesIdle(9);
                        w2200.setTOnStageIdle(60);
                        w2200.setNumOfStagesSc(5);
                        w2200.setTOnStageSc(10);
                        w2200.setTHeating(300);
                        w2200.setTempHeating(50);
                        w2200.setNumOfStagesPerformance(1);
                        w2200.setZ1Performance(72);
                        w2200.setZ2Performance(48);
                        w2200.setTBreakInPerformance(120);
                        w2200.setTPerformance(600);
                        w2200.setKOverloadI(1.5f);
                        w2200.setNoise(70);
                        w2200.setVibration(1.8f);

                        Subject w7500 = realm.createObject(Subject.class, i++);
                        w7500.setName("7,5кВт");
                        w7500.setPN(7500f);
                        w7500.setUN(380);
                        w7500.setIN(18f);
                        w7500.setMN(30f);
                        w7500.setVN(730);
                        w7500.setWinding("треугольник");
                        w7500.setSN(6f);
                        w7500.setEfficiencyN(0.84f);
                        w7500.setFN(50f);
                        w7500.setUMgr(1000);
                        w7500.setRMgr(100f);
                        w7500.setRIkas(3f);
                        w7500.setUViu(1200);
                        w7500.setTViu(30);
                        w7500.setIViu(1f);
                        w7500.setTBreakInIdle(30);
                        w7500.setNumOfStagesIdle(1);
                        w7500.setTOnStageIdle(30);
                        w7500.setNumOfStagesSc(1);
                        w7500.setTOnStageSc(10);
                        w7500.setTHeating(50);
                        w7500.setTempHeating(4);
                        w7500.setNumOfStagesPerformance(1);
                        w7500.setZ1Performance(510);
                        w7500.setZ2Performance(260);
                        w7500.setTBreakInPerformance(30);
                        w7500.setTPerformance(30);
                        w7500.setKOverloadI(1.5f);
                        w7500.setNoise(80f);
                        w7500.setVibration(5f);

                        Subject w15000 = realm.createObject(Subject.class, i);
                        w15000.setName("15кВт");
                        w15000.setPN(15000f);
                        w15000.setUN(380);
                        w15000.setIN(30f);
                        w15000.setMN(30f);
                        w15000.setVN(2930);
                        w15000.setWinding("треугольник");
                        w15000.setSN(6f);
                        w15000.setEfficiencyN(0.89f);
                        w15000.setFN(50f);
                        w15000.setUMgr(1000);
                        w15000.setRMgr(100f);
                        w15000.setRIkas(3f);
                        w15000.setUViu(1200);
                        w15000.setTViu(30);
                        w15000.setIViu(1f);
                        w15000.setTBreakInIdle(30);
                        w15000.setNumOfStagesIdle(9);
                        w15000.setTOnStageIdle(30);
                        w15000.setNumOfStagesSc(5);
                        w15000.setTOnStageSc(10);
                        w15000.setTHeating(50);
                        w15000.setTempHeating(4);
                        w15000.setNumOfStagesPerformance(1);
                        w15000.setZ1Performance(260);
                        w15000.setZ2Performance(500);
                        w15000.setTBreakInPerformance(30);
                        w15000.setTPerformance(30);
                        w15000.setKOverloadI(1.5f);
                        w15000.setNoise(90f);
                        w15000.setVibration(6f);
                    }
                })
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}
