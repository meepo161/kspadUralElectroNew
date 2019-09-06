package ru.avem.kspad.model;

import java.util.List;

import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.database.model.Protocol;
import ru.avem.kspad.database.model.Subject;
import ru.avem.kspad.database.controller.DatabaseAdapter;
import ru.avem.kspad.view.OnRealmReceiverCallback;

public class MainModel {
    private DatabaseAdapter mDatabaseAdapter;

    private Protocol mProtocol;
    private long mSubjectId;
    private boolean PISelected;

    public MainModel(OnRealmReceiverCallback onRealmReceiverCallback) {
        mDatabaseAdapter = new DatabaseAdapter(onRealmReceiverCallback);
    }

    public Protocol getProtocolByName(String serialNumber) {
        mDatabaseAdapter.open();
        Protocol protocol = mDatabaseAdapter.getProtocolByName(serialNumber);
        mDatabaseAdapter.close();
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        mProtocol = new Protocol(protocol);
        mDatabaseAdapter.open();
        ExperimentsHolder.setExperiments(mDatabaseAdapter.getExperimentsById(mProtocol.getId()));
        mDatabaseAdapter.close();
    }

    public void createNewProtocol(String serialNumber) {
        mProtocol = new Protocol(serialNumber);
        ExperimentsHolder.setExperiments(new Experiments());
    }

    public void deleteProtocolFromDatabase(Protocol protocol) {
        mDatabaseAdapter.open();
        long id = protocol.getId();
        mDatabaseAdapter.deleteProtocol(id);
        mDatabaseAdapter.deleteExperiments(id);
        mDatabaseAdapter.close();
    }

    public void destructProtocol() {
        mProtocol = null;
        ExperimentsHolder.setExperiments(null);
    }

    public List<Protocol> getProtocolsByDateFromDB(long startDate, long endDate) {
        List<Protocol> protocols;
        mDatabaseAdapter.open();
        protocols = mDatabaseAdapter.getProtocolsByDate(startDate, endDate);
        mDatabaseAdapter.close();
        return protocols;
    }

    public void saveProtocolInDB(String position1, String position1Number, String position1FullName,
                                 String position2, String position2Number, String position2FullName) {
        mProtocol.setPosition1(position1);
        mProtocol.setPosition1Number(position1Number);
        mProtocol.setPosition1FullName(position1FullName);
        mProtocol.setPosition2(position2);
        mProtocol.setPosition2Number(position2Number);
        mProtocol.setPosition2FullName(position2FullName);
        mProtocol.setDate(System.currentTimeMillis());
        saveProtocolToDB();
        saveExperimentsToDB();
    }

    private void saveProtocolToDB() {
        mDatabaseAdapter.open();
        if (mProtocol.getId() > 0) {
            mDatabaseAdapter.updateProtocol(mProtocol);
        } else {
            mDatabaseAdapter.insertProtocol(mProtocol);
        }
        mDatabaseAdapter.close();
    }

    private void saveExperimentsToDB() {
        mDatabaseAdapter.open();
        if (ExperimentsHolder.getExperiments().getId() > 0) {
            mDatabaseAdapter.updateExperiments(ExperimentsHolder.getExperiments());
        } else {
            mDatabaseAdapter.insertExperiments(ExperimentsHolder.getExperiments());
        }
        mDatabaseAdapter.close();
    }

    public List<Subject> getAllSubjectsFromDB() {
        List<Subject> subjects;
        mDatabaseAdapter.open();
        subjects = mDatabaseAdapter.getSubjects();
        mDatabaseAdapter.close();
        return subjects;
    }

    public void setCurrentSubject(Subject currentSubject) {
        mSubjectId = currentSubject.getId();
        mProtocol.setSubject(currentSubject);
    }

    public boolean isPlatformOneSelected() {
        return mProtocol.isPlatformOneSelected();
    }

    public int getNumOfStagesPerformance() {
        return mProtocol.getNumOfStagesPerformance();
    }

    public int getZ1Performance() {
        return mProtocol.getZ1Performance();
    }

    public int getZ2Performance() {
        return mProtocol.getZ2Performance();
    }

    public int getVN() {
        return mProtocol.getVN();
    }

    public float getMN() {
        return mProtocol.getMN();
    }

    public int getTBreakInPerformance() {
        return mProtocol.getTBreakInPerformance();
    }

    public int getTPerformance() {
        return mProtocol.getTPerformance();
    }

    public float getIN() {
        return mProtocol.getIN();
    }

    public int getTViu() {
        return mProtocol.getTViu();
    }

    public int getUViu() {
        return mProtocol.getUViu();
    }

    public float getIViu() {
        return mProtocol.getIViu();
    }

    public int getUN() {
        return mProtocol.getUN();
    }

    public int getTBreakInIdle() {
        return mProtocol.getTBreakInIdle();
    }

    public int getTOnStageIdle() {
        return mProtocol.getTOnStageIdle();
    }

    public int getNumOfStagesIdle() {
        return mProtocol.getNumOfStagesIdle();
    }

    public int getTOnStageSc() {
        return mProtocol.getTOnStageSc();
    }

    public int getNumOfStagesSc() {
        return mProtocol.getNumOfStagesSc();
    }

    public int getUMgr() {
        return mProtocol.getUMgr();
    }

    public int getTHeating() {
        return mProtocol.getTHeating();
    }

    public int getTempHeating() {
        return mProtocol.getTempHeating();
    }

    public float getFN() {
        return mProtocol.getFN();
    }

    public float getPN() {
        return mProtocol.getPN();
    }

    public float getEfficiencyN() {
        return mProtocol.getEfficiencyN();
    }

    public float getSN() {
        return mProtocol.getSN();
    }

    public float getKOverloadI() {
        return mProtocol.getKOverloadI();
    }

    public float getRIkas() {
        return mProtocol.getRIkas();
    }

    public int getIkasRType() {
        return mProtocol.getIkasRTypeR();
    }


    public void setPlatformOneSelected(boolean platformOneSelected) {
        mProtocol.setPlatformOneSelected(platformOneSelected);
    }

    public void setP2R(float P2R) {
        mProtocol.setP2R(P2R);
    }

    public void setUR(float UR) {
        mProtocol.setUR(UR);
    }

    public void setIR(float IR) {
        mProtocol.setIR(IR);
    }

    public void setVR(float VR) {
        mProtocol.setVR(VR);
    }

    public void setSR(float SR) {
        mProtocol.setSR(SR);
    }

    public void setNuR(float NuR) {
        mProtocol.setNuR(NuR);
    }

    public void setCosR(float CosR) {
        mProtocol.setCosR(CosR);
    }

    public void setP1R(float P1R) {
        mProtocol.setP1R(P1R);
    }

    public void setMR(float MR) {
        mProtocol.setMR(MR);
    }

    public void setMMaxR(float MMaxR) {
        mProtocol.setMMaxR(MMaxR);
    }

    public void setMMinR(float MMinR) {
        mProtocol.setMMinR(MMinR);
    }

    public void setMStartR(float MStartR) {
        mProtocol.setMStartR(MStartR);
    }

    public void setIStartR(float IStartR) {
        mProtocol.setIStartR(IStartR);
    }

    public void setI13IdleR(float i13IdleR) {
        mProtocol.setI13IdleR(i13IdleR);
    }

    public void setP13IdleR(float p13IdleR) {
        mProtocol.setP13IdleR(p13IdleR);
    }

    public void setI12IdleR(float i12IdleR) {
        mProtocol.setI12IdleR(i12IdleR);
    }

    public void setP12IdleR(float p12IdleR) {
        mProtocol.setP12IdleR(p12IdleR);
    }

    public void setI11IdleR(float i11IdleR) {
        mProtocol.setI11IdleR(i11IdleR);
    }

    public void setP11IdleR(float p11IdleR) {
        mProtocol.setP11IdleR(p11IdleR);
    }

    public void setI10IdleR(float i10IdleR) {
        mProtocol.setI10IdleR(i10IdleR);
    }

    public void setP10IdleR(float p10IdleR) {
        mProtocol.setP10IdleR(p10IdleR);
    }

    public void setI09IdleR(float i09IdleR) {
        mProtocol.setI09IdleR(i09IdleR);
    }

    public void setP09IdleR(float p09IdleR) {
        mProtocol.setP09IdleR(p09IdleR);
    }

    public void setI08IdleR(float i08IdleR) {
        mProtocol.setI08IdleR(i08IdleR);
    }

    public void setP08IdleR(float p08IdleR) {
        mProtocol.setP08IdleR(p08IdleR);
    }

    public void setI07IdleR(float i07IdleR) {
        mProtocol.setI07IdleR(i07IdleR);
    }

    public void setP07IdleR(float p07IdleR) {
        mProtocol.setP07IdleR(p07IdleR);
    }

    public void setU07IdleR(float u07IdleR) {
        mProtocol.setU07IdleR(u07IdleR);
    }

    public void setI06IdleR(float i06IdleR) {
        mProtocol.setI06IdleR(i06IdleR);
    }

    public void setP06IdleR(float p06IdleR) {
        mProtocol.setP06IdleR(p06IdleR);
    }

    public void setU06IdleR(float u06IdleR) {
        mProtocol.setU06IdleR(u06IdleR);
    }

    public void setI05IdleR(float i05IdleR) {
        mProtocol.setI05IdleR(i05IdleR);
    }

    public void setP05IdleR(float p05IdleR) {
        mProtocol.setP05IdleR(p05IdleR);
    }

    public void setU05IdleR(float u05IdleR) {
        mProtocol.setU05IdleR(u05IdleR);
    }

    public void setPStR(double PStR) {
        mProtocol.setPStR(PStR);
    }

    public void setPMechR(double PMechR) {
        mProtocol.setPMechR(PMechR);
    }

    public void setUTurnR(float UTurnR) {
        mProtocol.setUTurnR(UTurnR);
    }

    public void setI10SCR(float i10SCR) {
        mProtocol.setI10SCR(i10SCR);
    }

    public void setP10SCR(float p10SCR) {
        mProtocol.setP10SCR(p10SCR);
    }

    public void setI09SCR(float i09SCR) {
        mProtocol.setI09SCR(i09SCR);
    }

    public void setP09SCR(float p09SCR) {
        mProtocol.setP09SCR(p09SCR);
    }

    public void setI08SCR(float i08SCR) {
        mProtocol.setI08SCR(i08SCR);
    }

    public void setP08SCR(float p08SCR) {
        mProtocol.setP08SCR(p08SCR);
    }

    public void setI07SCR(float i07SCR) {
        mProtocol.setI07SCR(i07SCR);
    }

    public void setP07SCR(float p07SCR) {
        mProtocol.setP07SCR(p07SCR);
    }

    public void setI06SCR(float i06SCR) {
        mProtocol.setI06SCR(i06SCR);
    }

    public void setP06SCR(float p06SCR) {
        mProtocol.setP06SCR(p06SCR);
    }

    public void setSpecifiedIOverloadR(float SpecifiedIOverloadR) {
        mProtocol.setSpecifiedIOverloadR(SpecifiedIOverloadR);
    }

    public void setIOverloadR(float IOverloadR) {
        mProtocol.setIOverloadR(IOverloadR);
    }

    public void setUMVZ1R(float UMVZ1R) {
        mProtocol.setI1MVZR(UMVZ1R);
    }

    public void setUMVZ2R(float UMVZ2R) {
        mProtocol.setI2MVZR(UMVZ2R);
    }

    public void setUMVZ3R(float UMVZ3R) {
        mProtocol.setI3MVZR(UMVZ3R);
    }

    public void setIkasRColdR(float IkasRColdR) {
        mProtocol.setIkasRColdR(IkasRColdR);
    }

    public void setIkasR20R(float IkasR20R) {
        mProtocol.setIkasR20R(IkasR20R);
    }

    public void setIkasRTypeR(int IkasRTypeR) {
        mProtocol.setIkasRTypeR(IkasRTypeR);
    }

    public void setIkasRHotR(float IkasRHotR) {
        mProtocol.setIkasRHotR(IkasRHotR);
    }

    public void setUViuR(float UViuR) {
        mProtocol.setUViuR(UViuR);
    }

    public void setTViuR(float TViuR) {
        mProtocol.setTViuR(TViuR);
    }

    public void setVOverloadR(float VOverloadR) {
        mProtocol.setVOverloadR(VOverloadR);
    }

    public void setTOverloadR(float TOverloadR) {
        mProtocol.setTOverloadR(TOverloadR);
    }

    public void setMgrR(float MgrR) {
        mProtocol.setMgrR(MgrR);
    }

    public void setTempEngineR(float TempEngineR) {
        mProtocol.setTempEngineR(TempEngineR);
    }

    public void setTempAmbientR(float TempAmbientR) {
        mProtocol.setTempAmbientR(TempAmbientR);
    }

    public void setNoiseR(float NoiseR) {
        mProtocol.setNoiseR(NoiseR);
    }

    public void setX1R(float X1R) {
        mProtocol.setX1R(X1R);
    }

    public void setY1R(float Y1R) {
        mProtocol.setY1R(Y1R);
    }

    public void setZ1R(float Z1R) {
        mProtocol.setZ1R(Z1R);
    }

    public void setX2R(float X2R) {
        mProtocol.setX2R(X2R);
    }

    public void setY2R(float Y2R) {
        mProtocol.setY2R(Y2R);
    }

    public void setZ2R(float Z2R) {
        mProtocol.setZ2R(Z2R);
    }

    public long getSubjectId() {
        return mSubjectId;
    }

    public void setPISelected(boolean piSelected) {
        this.PISelected = piSelected;
    }

    public boolean getPISelected() {
        return PISelected;
    }

    public void setNewSubjectDataToProtocol(Protocol protocol) {
        mDatabaseAdapter.open();
        Subject subjectByName = getSubjectByName(protocol.getSubjectName());
        if (subjectByName != null) {
            protocol.setSubject(subjectByName);
        }
        mDatabaseAdapter.close();
    }

    private Subject getSubjectByName(String name) {
        return mDatabaseAdapter.getSubjectByName(name);
    }
}
