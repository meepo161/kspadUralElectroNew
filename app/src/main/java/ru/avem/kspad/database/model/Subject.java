package ru.avem.kspad.database.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Subject extends RealmObject {
    @PrimaryKey
    private long mId;
    private String mName;

    private float mPN;
    private int mUN;
    private float mIN;
    private float mMN;
    private int mVN;
    private String mWinding;
    private float mSN;
    private float mEfficiencyN;
    private float mFN;

    private int mUMgr;
    private float mRMgr;

    private float mRIkas;

    private int mUViu;
    private int mTViu;
    private float mIViu;

    private int mTBreakInIdle;
    private int mNumOfStagesIdle;
    private int mTOnStageIdle;

    private int mNumOfStagesSc;
    private int mTOnStageSc;

    private int mTHeating;
    private int mTempHeating;

    private int mNumOfStagesPerformance;
    private int mZ1Performance;
    private int mZ2Performance;
    private int mTBreakInPerformance;
    private int mTPerformance;

    private float mKOverloadI;

    private float mNoise;
    private float mVibration;

    public Subject() {
    }

    public Subject(long id, String name, float PN, int UN, float IN, float MN, int VN,
                   String Winding, float SN, float EfficiencyN, float FN, int UMgr, float RMgr, float RIkas, int UViu, int TViu, float IViu,
                   int TBreakInIdle, int NumOfStagesIdle, int TOnStageIdle, int NumOfStagesSc,
                   int TOnStageSc, int THeating, int TempHeating, int NumOfStagesPerformance, int Z1Performance, int Z2Performance,
                   int TBreakInPerformance, int TPerformance, float KOverloadI, float Noise,
                   float Vibration) {
        mId = id;
        mName = name;
        mPN = PN;
        mUN = UN;
        mIN = IN;
        mMN = MN;
        mVN = VN;
        mWinding = Winding;
        mSN = SN;
        mEfficiencyN = EfficiencyN;
        mFN = FN;
        mUMgr = UMgr;
        mRMgr = RMgr;
        mRIkas = RIkas;
        mUViu = UViu;
        mTViu = TViu;
        mIViu = IViu;
        mTBreakInIdle = TBreakInIdle;
        mNumOfStagesIdle = NumOfStagesIdle;
        mTOnStageIdle = TOnStageIdle;
        mNumOfStagesSc = NumOfStagesSc;
        mTOnStageSc = TOnStageSc;
        mTHeating = THeating;
        mTempHeating = TempHeating;
        mNumOfStagesPerformance = NumOfStagesPerformance;
        mZ1Performance = Z1Performance;
        mZ2Performance = Z2Performance;
        mTBreakInPerformance = TBreakInPerformance;
        mTPerformance = TPerformance;
        mKOverloadI = KOverloadI;
        mNoise = Noise;
        mVibration = Vibration;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public float getPN() {
        return mPN;
    }

    public void setPN(float PN) {
        mPN = PN;
    }

    public int getUN() {
        return mUN;
    }

    public void setUN(int UN) {
        mUN = UN;
    }

    public float getIN() {
        return mIN;
    }

    public void setIN(float IN) {
        mIN = IN;
    }

    public float getMN() {
        return mMN;
    }

    public void setMN(float MN) {
        mMN = MN;
    }

    public int getVN() {
        return mVN;
    }

    public void setVN(int VN) {
        mVN = VN;
    }

    public String getWinding() {
        return mWinding;
    }

    public void setWinding(String winding) {
        mWinding = winding;
    }

    public float getSN() {
        return mSN;
    }

    public void setSN(float SN) {
        mSN = SN;
    }

    public float getEfficiencyN() {
        return mEfficiencyN;
    }

    public void setEfficiencyN(float efficiencyN) {
        mEfficiencyN = efficiencyN;
    }

    public float getFN() {
        return mFN;
    }

    public void setFN(float FN) {
        mFN = FN;
    }

    public int getUMgr() {
        return mUMgr;
    }

    public void setUMgr(int UMgr) {
        mUMgr = UMgr;
    }

    public float getRMgr() {
        return mRMgr;
    }

    public void setRMgr(float RMgr) {
        mRMgr = RMgr;
    }

    public float getRIkas() {
        return mRIkas;
    }

    public void setRIkas(float RIkas) {
        mRIkas = RIkas;
    }

    public int getUViu() {
        return mUViu;
    }

    public void setUViu(int UViu) {
        mUViu = UViu;
    }

    public int getTViu() {
        return mTViu;
    }

    public void setTViu(int TViu) {
        mTViu = TViu;
    }

    public float getIViu() {
        return mIViu;
    }

    public void setIViu(float IViu) {
        mIViu = IViu;
    }

    public int getTBreakInIdle() {
        return mTBreakInIdle;
    }

    public void setTBreakInIdle(int TBreakInIdle) {
        mTBreakInIdle = TBreakInIdle;
    }

    public int getNumOfStagesIdle() {
        return mNumOfStagesIdle;
    }

    public void setNumOfStagesIdle(int numOfStagesIdle) {
        mNumOfStagesIdle = numOfStagesIdle;
    }

    public int getTOnStageIdle() {
        return mTOnStageIdle;
    }

    public void setTOnStageIdle(int TOnStageIdle) {
        mTOnStageIdle = TOnStageIdle;
    }

    public int getNumOfStagesSc() {
        return mNumOfStagesSc;
    }

    public void setNumOfStagesSc(int numOfStagesSc) {
        mNumOfStagesSc = numOfStagesSc;
    }

    public int getTOnStageSc() {
        return mTOnStageSc;
    }

    public void setTOnStageSc(int TOnStageSc) {
        mTOnStageSc = TOnStageSc;
    }

    public int getTHeating() {
        return mTHeating;
    }

    public void setTHeating(int THeating) {
        mTHeating = THeating;
    }

    public int getTempHeating() {
        return mTempHeating;
    }

    public void setTempHeating(int tempHeating) {
        mTempHeating = tempHeating;
    }

    public int getNumOfStagesPerformance() {
        return mNumOfStagesPerformance;
    }

    public void setNumOfStagesPerformance(int numOfStagesPerformance) {
        mNumOfStagesPerformance = numOfStagesPerformance;
    }

    public int getZ1Performance() {
        return mZ1Performance;
    }

    public void setZ1Performance(int z1Performance) {
        mZ1Performance = z1Performance;
    }

    public int getZ2Performance() {
        return mZ2Performance;
    }

    public void setZ2Performance(int z2Performance) {
        mZ2Performance = z2Performance;
    }

    public int getTBreakInPerformance() {
        return mTBreakInPerformance;
    }

    public void setTBreakInPerformance(int TBreakInPerformance) {
        mTBreakInPerformance = TBreakInPerformance;
    }

    public int getTPerformance() {
        return mTPerformance;
    }

    public void setTPerformance(int TPerformance) {
        mTPerformance = TPerformance;
    }

    public float getKOverloadI() {
        return mKOverloadI;
    }

    public void setKOverloadI(float KOverloadI) {
        mKOverloadI = KOverloadI;
    }

    public float getNoise() {
        return mNoise;
    }

    public void setNoise(float noise) {
        mNoise = noise;
    }

    public float getVibration() {
        return mVibration;
    }

    public void setVibration(float vibration) {
        mVibration = vibration;
    }

    @Override
    public String toString() {
        return mName;
    }
}