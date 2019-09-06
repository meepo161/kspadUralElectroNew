package ru.avem.kspad.database.model;

import java.text.SimpleDateFormat;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import static ru.avem.kspad.utils.Utils.RU_LOCALE;

public class Protocol extends RealmObject {
    @PrimaryKey
    private long mId;
    private String mSerialNumber;
    private String mSubjectName;
    private boolean mPlatformOneSelected;


    private float mPN = -1f;
    private int mUN = -1;
    private float mIN = -1f;
    private float mMN = -1f;
    private int mVN = -1;
    private String mWinding;
    private float mSN = -1f;
    private float mEfficiencyN = -1f;
    private float mFN = -1f;

    private int mUMgr = -1;
    private float mRMgr = -1f;

    private float mRIkas = -1f;

    private int mUViu = -1;
    private int mTViu = -1;
    private float mIViu = -1f;

    private int mTBreakInIdle = -1;
    private int mNumOfStagesIdle = -1;
    private int mTOnStageIdle = -1;

    private int mNumOfStagesSc = -1;
    private int mTOnStageSc = -1;

    private int mTHeating = -1;
    private int mTempHeating = -1;

    private int mNumOfStagesPerformance = -1;
    private int mZ1Performance = -1;
    private int mZ2Performance = -1;
    private int mTBreakInPerformance = -1;
    private int mTPerformance = -1;

    private float mKOverloadI = -1f;

    private float mNoise = -1f;
    private float mVibration = -1f;


    private float mP2R = -1f;
    private float mUR = -1f;
    private float mIR = -1f;
    private float mVR = -1f;
    private float mSR = -1f;
    private float mNuR = -1f;
    private float mCosR = -1f;
    private float mP1R = -1f;
    private float mMR = -1f;

    private float mMMaxR = -1f;
    private float mMMinR = -1f;
    private float mMStartR = -1f;
    private float mIStartR = -1f;

    private float mI13IdleR = -1f;
    private float mP13IdleR = -1f;
    private float mI12IdleR = -1f;
    private float mP12IdleR = -1f;
    private float mI11IdleR = -1f;
    private float mP11IdleR = -1f;
    private float mI10IdleR = -1f;
    private float mP10IdleR = -1f;
    private float mI09IdleR = -1f;
    private float mP09IdleR = -1f;
    private float mI08IdleR = -1f;
    private float mP08IdleR = -1f;
    private float mI07IdleR = -1f;
    private float mP07IdleR = -1f;
    private float mU07IdleR = -1f;
    private float mI06IdleR = -1f;
    private float mP06IdleR = -1f;
    private float mU06IdleR = -1f;
    private float mI05IdleR = -1f;
    private float mP05IdleR = -1f;
    private float mU05IdleR = -1f;
    private double mPStR = -1.;
    private double mPMechR = -1.;
    private float mUTurnR = -1f;

    private float mI10SCR = -1f;
    private float mP10SCR = -1f;
    private float mI09SCR = -1f;
    private float mP09SCR = -1f;
    private float mI08SCR = -1f;
    private float mP08SCR = -1f;
    private float mI07SCR = -1f;
    private float mP07SCR = -1f;
    private float mI06SCR = -1f;
    private float mP06SCR = -1f;

    private float mTempEngineR = -1f;
    private float mTempAmbientR = -1f;

    private float mIkasRColdR = -1f;
    private float mIkasR20R = -1f;
    private int mIkasRTypeR = 1;
    private float mIkasRHotR = -1f;

    private float mMgrR = -1f;

    private float mI1MVZR = -1f;
    private float mI2MVZR = -1f;
    private float mI3MVZR = -1f;

    private float mUViuR = -1f;
    private float mTViuR = -1f;

    private float mVOverloadR = -1f;
    private float mTOverloadR = -1f;

    private float mSpecifiedIOverloadR = -1f;
    private float mIOverloadR = -1f;

    private float mNoiseR = -1f;
    private float mX1R = -1f;
    private float mY1R = -1f;
    private float mZ1R = -1f;
    private float mX2R = -1f;
    private float mY2R = -1f;
    private float mZ2R = -1f;


    private String mPosition1;
    private String mPosition1Number;
    private String mPosition1FullName;
    private String mPosition2;
    private String mPosition2Number;
    private String mPosition2FullName;
    private long mDate = System.currentTimeMillis();


    public Protocol() {
    }

    public Protocol(String serialNumber) {
        this(0, serialNumber, null, true,

                -1f, -1, -1f, -1f, -1,
                null, -1f, -1f, -1f, -1, -1f, -1f,
                -1, -1, -1f, -1, -1,
                -1, -1, -1, -1,
                -1, -1, -1, -1,
                -1, -1, -1f, -1f,
                -1f,

                -1f, -1f, -1f, -1f, -1f, -1f, -1f, -1f,
                -1f, -1f, -1f, -1f, -1f, -1f, -1f,
                -1f, -1f, -1f, -1f, -1f, -1f,
                -1f, -1f, -1f, -1f, -1f, -1f,
                -1f, -1f, -1f, -1f, -1f, -1f,
                -1f, -1., -1., -1f, -1f, -1f, -1f, -1f, -1f,
                -1f, -1f, -1f, -1f, -1f, -1f,
                -1f, -1f, -1f, 1, -1f, -1f, -1f,
                -1f, -1f, -1f, -1f, -1f, -1f, -1f, -1f,
                -1f, -1f, -1f, -1f, -1f, -1f, -1f,

                null, null, null,
                null, null, null,
                System.currentTimeMillis());
    }

    private Protocol(long id, String serialNumber, String subjectName, boolean PlatformOneSelected,

                     float PN, int UN, float IN, float MN, int VN,
                     String Winding, float SN, float EfficiencyN, float FN, int UMgr, float RMgr,
                     float RIkas, int UViu, int TViu, float IViu, int TBreakInIdle, int NumOfStagesIdle,
                     int TOnStageIdle, int NumOfStagesSc, int TOnStageSc, int THeating,
                     int TempHeating,

                     int NumOfStagesPerformance, int Z1Performance, int Z2Performance,
                     int TBreakInPerformance, int TPerformance, float KOverloadI, float Noise,
                     float Vibration,

                     float P2R, float UR, float IR, float VR, float SR, float NuR, float CosR,
                     float P1R, float MR, float MMaxR, float MMinR, float MStartR, float IStartR,
                     float I13IdleR, float P13IdleR, float I12IdleR, float P12IdleR, float I11IdleR,
                     float P11IdleR, float I10IdleR, float P10IdleR, float I09IdleR, float P09IdleR,
                     float I08IdleR, float P08IdleR, float I07IdleR, float P07IdleR, float U07IdleR, float I06IdleR,
                     float P06IdleR, float U06IdleR, float I05IdleR, float P05IdleR, float U05IdleR, double PStR, double PMechR, float UTurnR,
                     float I10SCR, float P10SCR, float I09SCR, float P09SCR, float I08SCR,
                     float P08SCR, float I07SCR, float P07SCR, float I06SCR, float P06SCR,
                     float TempEngineR, float TempAmbientR,
                     float ikasRColdR, float ikasR20R, int ikasRTypeR, float ikasRHotR,
                     float MgrR, float I1MVZR, float I2MVZR,
                     float I3MVZR, float UViuR, float TViuR, float VOverloadR, float TOverloadR,
                     float SpecifiedIOverloadR, float IOverloadR,

                     float NoiseR, float X1R, float Y1R, float Z1R, float X2R, float Y2R, float Z2R,

                     String position1, String position1Number, String position1FullName,
                     String position2, String position2Number, String position2FullName,
                     long date) {
        mId = id;
        mSerialNumber = serialNumber;
        mSubjectName = subjectName;
        mPlatformOneSelected = PlatformOneSelected;


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


        mP2R = P2R;
        mUR = UR;
        mIR = IR;
        mVR = VR;
        mSR = SR;
        mNuR = NuR;
        mCosR = CosR;
        mP1R = P1R;
        mMR = MR;
        mMMaxR = MMaxR;
        mMMinR = MMinR;
        mMStartR = MStartR;
        mIStartR = IStartR;
        mI13IdleR = I13IdleR;
        mP13IdleR = P13IdleR;
        mI12IdleR = I12IdleR;
        mP12IdleR = P12IdleR;
        mI11IdleR = I11IdleR;
        mP11IdleR = P11IdleR;
        mI10IdleR = I10IdleR;
        mP10IdleR = P10IdleR;
        mI09IdleR = I09IdleR;
        mP09IdleR = P09IdleR;
        mI08IdleR = I08IdleR;
        mP08IdleR = P08IdleR;
        mI07IdleR = I07IdleR;
        mP07IdleR = P07IdleR;
        mU07IdleR = U07IdleR;
        mI06IdleR = I06IdleR;
        mP06IdleR = P06IdleR;
        mU06IdleR = U06IdleR;
        mI05IdleR = I05IdleR;
        mP05IdleR = P05IdleR;
        mU05IdleR = U05IdleR;
        mPStR = PStR;
        mPMechR = PMechR;
        mUTurnR = UTurnR;

        mI10SCR = I10SCR;
        mP10SCR = P10SCR;
        mI09SCR = I09SCR;
        mP09SCR = P09SCR;
        mI08SCR = I08SCR;
        mP08SCR = P08SCR;
        mI07SCR = I07SCR;
        mP07SCR = P07SCR;
        mI06SCR = I06SCR;
        mP06SCR = P06SCR;
        mTempEngineR = TempEngineR;
        mTempAmbientR = TempAmbientR;

        mIkasRColdR = ikasRColdR;
        mIkasR20R = ikasR20R;
        mIkasRTypeR = ikasRTypeR;
        mIkasRHotR = ikasRHotR;

        mMgrR = MgrR;
        mI1MVZR = I1MVZR;
        mI2MVZR = I2MVZR;
        mI3MVZR = I3MVZR;
        mUViuR = UViuR;
        mTViuR = TViuR;
        mVOverloadR = VOverloadR;
        mTOverloadR = TOverloadR;

        mSpecifiedIOverloadR = SpecifiedIOverloadR;
        mIOverloadR = IOverloadR;

        mNoiseR = NoiseR;
        mX1R = X1R;
        mY1R = Y1R;
        mZ1R = Z1R;
        mIOverloadR = X2R;
        mIOverloadR = Y2R;
        mIOverloadR = Z2R;


        mPosition1 = position1;
        mPosition1Number = position1Number;
        mPosition1FullName = position1FullName;
        mPosition2 = position2;
        mPosition2Number = position2Number;
        mPosition2FullName = position2FullName;
        mDate = date;
    }

    public Protocol(Protocol protocol) {
        mId = protocol.getId();
        mSerialNumber = protocol.getSerialNumber();
        mSubjectName = protocol.getSubjectName();
        mPlatformOneSelected = protocol.isPlatformOneSelected();
        mPN = protocol.getPN();
        mUN = protocol.getUN();
        mIN = protocol.getIN();
        mMN = protocol.getMN();
        mVN = protocol.getVN();
        mWinding = protocol.getWinding();
        mSN = protocol.getSN();
        mEfficiencyN = protocol.getEfficiencyN();
        mFN = protocol.getFN();
        mUMgr = protocol.getUMgr();
        mRMgr = protocol.getRMgr();
        mRIkas = protocol.getRIkas();
        mUViu = protocol.getUViu();
        mTViu = protocol.getTViu();
        mIViu = protocol.getIViu();
        mTBreakInIdle = protocol.getTBreakInIdle();
        mNumOfStagesIdle = protocol.getNumOfStagesIdle();
        mTOnStageIdle = protocol.getTOnStageIdle();
        mNumOfStagesSc = protocol.getNumOfStagesSc();
        mTOnStageSc = protocol.getTOnStageSc();
        mTHeating = protocol.getTHeating();
        mTempHeating = protocol.getTempHeating();
        mNumOfStagesPerformance = protocol.getNumOfStagesPerformance();
        mZ1Performance = protocol.getZ1Performance();
        mZ2Performance = protocol.getZ2Performance();
        mTBreakInPerformance = protocol.getTBreakInPerformance();
        mTPerformance = protocol.getTPerformance();
        mKOverloadI = protocol.getKOverloadI();
        mNoise = protocol.getNoise();
        mVibration = protocol.getVibration();
        mP2R = protocol.getP2R();
        mUR = protocol.getUR();
        mIR = protocol.getIR();
        mVR = protocol.getVR();
        mSR = protocol.getSR();
        mNuR = protocol.getNuR();
        mCosR = protocol.getCosR();
        mP1R = protocol.getP1R();
        mMR = protocol.getMR();
        mMMaxR = protocol.getMMaxR();
        mMMinR = protocol.getMMinR();
        mMStartR = protocol.getMStartR();
        mIStartR = protocol.getIStartR();
        mI13IdleR = protocol.getI13IdleR();
        mP13IdleR = protocol.getP13IdleR();
        mI12IdleR = protocol.getI12IdleR();
        mP12IdleR = protocol.getP12IdleR();
        mI11IdleR = protocol.getI11IdleR();
        mP11IdleR = protocol.getP11IdleR();
        mI10IdleR = protocol.getI10IdleR();
        mP10IdleR = protocol.getP10IdleR();
        mI09IdleR = protocol.getI09IdleR();
        mP09IdleR = protocol.getP09IdleR();
        mI08IdleR = protocol.getI08IdleR();
        mP08IdleR = protocol.getP08IdleR();
        mI07IdleR = protocol.getI07IdleR();
        mP07IdleR = protocol.getP07IdleR();
        mU07IdleR = protocol.getU07IdleR();
        mI06IdleR = protocol.getI06IdleR();
        mP06IdleR = protocol.getP06IdleR();
        mU06IdleR = protocol.getU06IdleR();
        mI05IdleR = protocol.getI05IdleR();
        mP05IdleR = protocol.getP05IdleR();
        mU05IdleR = protocol.getU05IdleR();
        mPStR = protocol.getPStR();
        mPMechR = protocol.getPMechR();
        mUTurnR = protocol.getUTurnR();
        mI10SCR = protocol.getI10SCR();
        mP10SCR = protocol.getP10SCR();
        mI09SCR = protocol.getI09SCR();
        mP09SCR = protocol.getP09SCR();
        mI08SCR = protocol.getI08SCR();
        mP08SCR = protocol.getP08SCR();
        mI07SCR = protocol.getI07SCR();
        mP07SCR = protocol.getP07SCR();
        mI06SCR = protocol.getI06SCR();
        mP06SCR = protocol.getP06SCR();
        mTempEngineR = protocol.getTempEngineR();
        mTempAmbientR = protocol.getTempAmbientR();
        mIkasRColdR = protocol.getIkasRColdR();
        mIkasR20R = protocol.getIkasR20R();
        mIkasRTypeR = protocol.getIkasRTypeR();
        mIkasRHotR = protocol.getIkasRHotR();
        mMgrR = protocol.getMgrR();
        mI1MVZR = protocol.getI1MVZR();
        mI2MVZR = protocol.getI2MVZR();
        mI3MVZR = protocol.getI3MVZR();
        mUViuR = protocol.getUViuR();
        mTViuR = protocol.getTViuR();
        mVOverloadR = protocol.getVOverloadR();
        mTOverloadR = protocol.getTOverloadR();
        mSpecifiedIOverloadR = protocol.getSpecifiedIOverloadR();
        mIOverloadR = protocol.getIOverloadR();
        mPosition1 = protocol.getPosition1();
        mPosition1Number = protocol.getPosition1Number();
        mPosition1FullName = protocol.getPosition1FullName();
        mPosition2 = protocol.getPosition2();
        mPosition2Number = protocol.getPosition2Number();
        mPosition2FullName = protocol.getPosition2FullName();
        mDate = protocol.getDate();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getSerialNumber() {
        return mSerialNumber;
    }

    public String getSubjectName() {
        return mSubjectName;
    }

    public boolean isPlatformOneSelected() {
        return mPlatformOneSelected;
    }

    public void setPlatformOneSelected(boolean platformOneSelected) {
        mPlatformOneSelected = platformOneSelected;
    }

    public void setSubject(Subject subject) {
        mSubjectName = subject.getName();

        mPN = subject.getPN();
        mUN = subject.getUN();
        mIN = subject.getIN();
        mMN = subject.getMN();
        mVN = subject.getVN();
        mWinding = subject.getWinding();
        mSN = subject.getSN();
        mEfficiencyN = subject.getEfficiencyN();
        mFN = subject.getFN();

        mUMgr = subject.getUMgr();
        mRMgr = subject.getRMgr();

        mRIkas = subject.getRIkas();

        mUViu = subject.getUViu();
        mTViu = subject.getTViu();
        mIViu = subject.getIViu();

        mTBreakInIdle = subject.getTBreakInIdle();
        mNumOfStagesIdle = subject.getNumOfStagesIdle();
        mTOnStageIdle = subject.getTOnStageIdle();

        mNumOfStagesSc = subject.getNumOfStagesSc();
        mTOnStageSc = subject.getTOnStageSc();

        mTHeating = subject.getTHeating();
        mTempHeating = subject.getTempHeating();

        mNumOfStagesPerformance = subject.getNumOfStagesPerformance();
        mZ1Performance = subject.getZ1Performance();
        mZ2Performance = subject.getZ2Performance();
        mTBreakInPerformance = subject.getTBreakInPerformance();
        mTPerformance = subject.getTPerformance();
        mKOverloadI = subject.getKOverloadI();
        mNoise = subject.getNoise();
        mVibration = subject.getVibration();

    }

    public float getPN() {
        return mPN;
    }

    public int getUN() {
        return mUN;
    }

    public float getIN() {
        return mIN;
    }

    public float getMN() {
        return mMN;
    }

    public int getVN() {
        return mVN;
    }

    public String getWinding() {
        return mWinding;
    }

    public float getSN() {
        return mSN;
    }

    public float getEfficiencyN() {
        return mEfficiencyN;
    }

    public float getFN() {
        return mFN;
    }

    public int getUMgr() {
        return mUMgr;
    }

    public float getRMgr() {
        return mRMgr;
    }

    public float getRIkas() {
        return mRIkas;
    }

    public int getUViu() {
        return mUViu;
    }

    public int getTViu() {
        return mTViu;
    }

    public float getIViu() {
        return mIViu;
    }

    public int getTBreakInIdle() {
        return mTBreakInIdle;
    }

    public int getNumOfStagesIdle() {
        return mNumOfStagesIdle;
    }

    public int getTOnStageIdle() {
        return mTOnStageIdle;
    }

    public int getNumOfStagesSc() {
        return mNumOfStagesSc;
    }

    public int getTOnStageSc() {
        return mTOnStageSc;
    }

    public int getTHeating() {
        return mTHeating;
    }

    public int getTempHeating() {
        return mTempHeating;
    }

    public int getNumOfStagesPerformance() {
        return mNumOfStagesPerformance;
    }

    public int getZ1Performance() {
        return mZ1Performance;
    }

    public int getZ2Performance() {
        return mZ2Performance;
    }

    public int getTBreakInPerformance() {
        return mTBreakInPerformance;
    }

    public int getTPerformance() {
        return mTPerformance;
    }

    public float getKOverloadI() {
        return mKOverloadI;
    }

    public float getNoise() {
        return mNoise;
    }

    public float getVibration() {
        return mVibration;
    }

    public float getP2R() {
        return mP2R;
    }

    public void setP2R(float p2R) {
        mP2R = p2R;
    }

    public float getUR() {
        return mUR;
    }

    public void setUR(float UR) {
        mUR = UR;
    }

    public float getIR() {
        return mIR;
    }

    public void setIR(float IR) {
        mIR = IR;
    }

    public float getVR() {
        return mVR;
    }

    public void setVR(float VR) {
        mVR = VR;
    }

    public float getSR() {
        return mSR;
    }

    public void setSR(float SR) {
        mSR = SR;
    }

    public float getNuR() {
        return mNuR;
    }

    public void setNuR(float nuR) {
        mNuR = nuR;
    }

    public float getCosR() {
        return mCosR;
    }

    public void setCosR(float cosR) {
        mCosR = cosR;
    }

    public float getP1R() {
        return mP1R;
    }

    public void setP1R(float p1R) {
        mP1R = p1R;
    }

    public float getMR() {
        return mMR;
    }

    public void setMR(float MR) {
        mMR = MR;
    }

    public float getMMaxR() {
        return mMMaxR;
    }

    public void setMMaxR(float MMaxR) {
        mMMaxR = MMaxR;
    }

    public float getMMinR() {
        return mMMinR;
    }

    public void setMMinR(float MMinR) {
        mMMinR = MMinR;
    }

    public float getMStartR() {
        return mMStartR;
    }

    public void setMStartR(float MStartR) {
        mMStartR = MStartR;
    }

    public float getIStartR() {
        return mIStartR;
    }

    public void setIStartR(float IStartR) {
        mIStartR = IStartR;
    }

    public float getI13IdleR() {
        return mI13IdleR;
    }

    public void setI13IdleR(float i13IdleR) {
        mI13IdleR = i13IdleR;
    }

    public float getP13IdleR() {
        return mP13IdleR;
    }

    public void setP13IdleR(float p13IdleR) {
        mP13IdleR = p13IdleR;
    }

    public float getI12IdleR() {
        return mI12IdleR;
    }

    public void setI12IdleR(float i12IdleR) {
        mI12IdleR = i12IdleR;
    }

    public float getP12IdleR() {
        return mP12IdleR;
    }

    public void setP12IdleR(float p12IdleR) {
        mP12IdleR = p12IdleR;
    }

    public float getI11IdleR() {
        return mI11IdleR;
    }

    public void setI11IdleR(float i11IdleR) {
        mI11IdleR = i11IdleR;
    }

    public float getP11IdleR() {
        return mP11IdleR;
    }

    public void setP11IdleR(float p11IdleR) {
        mP11IdleR = p11IdleR;
    }

    public float getI10IdleR() {
        return mI10IdleR;
    }

    public void setI10IdleR(float i10IdleR) {
        mI10IdleR = i10IdleR;
    }

    public float getP10IdleR() {
        return mP10IdleR;
    }

    public void setP10IdleR(float p10IdleR) {
        mP10IdleR = p10IdleR;
    }

    public float getI09IdleR() {
        return mI09IdleR;
    }

    public void setI09IdleR(float i09IdleR) {
        mI09IdleR = i09IdleR;
    }

    public float getP09IdleR() {
        return mP09IdleR;
    }

    public void setP09IdleR(float p09IdleR) {
        mP09IdleR = p09IdleR;
    }

    public float getI08IdleR() {
        return mI08IdleR;
    }

    public void setI08IdleR(float i08IdleR) {
        mI08IdleR = i08IdleR;
    }

    public float getP08IdleR() {
        return mP08IdleR;
    }

    public void setP08IdleR(float p08IdleR) {
        mP08IdleR = p08IdleR;
    }

    public float getI07IdleR() {
        return mI07IdleR;
    }

    public void setI07IdleR(float i07IdleR) {
        mI07IdleR = i07IdleR;
    }

    public float getP07IdleR() {
        return mP07IdleR;
    }

    public void setP07IdleR(float p07IdleR) {
        mP07IdleR = p07IdleR;
    }

    public float getU07IdleR() {
        return mU07IdleR;
    }

    public void setU07IdleR(float u07IdleR) {
        mU07IdleR = u07IdleR;
    }

    public float getI06IdleR() {
        return mI06IdleR;
    }

    public void setI06IdleR(float i06IdleR) {
        mI06IdleR = i06IdleR;
    }

    public float getP06IdleR() {
        return mP06IdleR;
    }

    public void setP06IdleR(float p06IdleR) {
        mP06IdleR = p06IdleR;
    }

    public float getU06IdleR() {
        return mU06IdleR;
    }

    public void setU06IdleR(float u06IdleR) {
        mU06IdleR = u06IdleR;
    }

    public float getI05IdleR() {
        return mI05IdleR;
    }

    public void setI05IdleR(float i05IdleR) {
        mI05IdleR = i05IdleR;
    }

    public float getP05IdleR() {
        return mP05IdleR;
    }

    public void setP05IdleR(float p05IdleR) {
        mP05IdleR = p05IdleR;
    }

    public float getU05IdleR() {
        return mU05IdleR;
    }

    public void setU05IdleR(float u05IdleR) {
        mU05IdleR = u05IdleR;
    }

    public double getPStR() {
        return mPStR;
    }

    public void setPStR(double PStR) {
        mPStR = PStR;
    }

    public double getPMechR() {
        return mPMechR;
    }

    public void setPMechR(double PMechR) {
        mPMechR = PMechR;
    }

    public float getUTurnR() {
        return mUTurnR;
    }

    public void setUTurnR(float UTurnR) {
        mUTurnR = UTurnR;
    }

    public float getI10SCR() {
        return mI10SCR;
    }

    public void setI10SCR(float i10SCR) {
        mI10SCR = i10SCR;
    }

    public float getP10SCR() {
        return mP10SCR;
    }

    public void setP10SCR(float p10SCR) {
        mP10SCR = p10SCR;
    }

    public float getI09SCR() {
        return mI09SCR;
    }

    public void setI09SCR(float i09SCR) {
        mI09SCR = i09SCR;
    }

    public float getP09SCR() {
        return mP09SCR;
    }

    public void setP09SCR(float p09SCR) {
        mP09SCR = p09SCR;
    }

    public float getI08SCR() {
        return mI08SCR;
    }

    public void setI08SCR(float i08SCR) {
        mI08SCR = i08SCR;
    }

    public float getP08SCR() {
        return mP08SCR;
    }

    public void setP08SCR(float p08SCR) {
        mP08SCR = p08SCR;
    }

    public float getI07SCR() {
        return mI07SCR;
    }

    public void setI07SCR(float i07SCR) {
        mI07SCR = i07SCR;
    }

    public float getP07SCR() {
        return mP07SCR;
    }

    public void setP07SCR(float p07SCR) {
        mP07SCR = p07SCR;
    }

    public float getI06SCR() {
        return mI06SCR;
    }

    public void setI06SCR(float i06SCR) {
        mI06SCR = i06SCR;
    }

    public float getP06SCR() {
        return mP06SCR;
    }

    public void setP06SCR(float p06SCR) {
        mP06SCR = p06SCR;
    }

    public float getTempEngineR() {
        return mTempEngineR;
    }

    public void setTempEngineR(float tempEngineR) {
        mTempEngineR = tempEngineR;
    }

    public float getTempAmbientR() {
        return mTempAmbientR;
    }

    public void setTempAmbientR(float tempAmbientR) {
        mTempAmbientR = tempAmbientR;
    }

    public float getIkasRColdR() {
        return mIkasRColdR;
    }

    public void setIkasRColdR(float ikasRColdR) {
        mIkasRColdR = ikasRColdR;
    }

    public float getIkasR20R() {
        return mIkasR20R;
    }

    public void setIkasR20R(float ikasR20R) {
        mIkasR20R = ikasR20R;
    }

    public int getIkasRTypeR() {
        return mIkasRTypeR;
    }

    public void setIkasRTypeR(int ikasRTypeR) {
        mIkasRTypeR = ikasRTypeR;
    }

    public float getIkasRHotR() {
        return mIkasRHotR;
    }

    public void setIkasRHotR(float ikasRHotR) {
        mIkasRHotR = ikasRHotR;
    }

    public float getMgrR() {
        return mMgrR;
    }

    public void setMgrR(float mgrR) {
        mMgrR = mgrR;
    }

    public float getI1MVZR() {
        return mI1MVZR;
    }

    public void setI1MVZR(float i1MVZR) {
        mI1MVZR = i1MVZR;
    }

    public float getI2MVZR() {
        return mI2MVZR;
    }

    public void setI2MVZR(float i2MVZR) {
        mI2MVZR = i2MVZR;
    }

    public float getI3MVZR() {
        return mI3MVZR;
    }

    public void setI3MVZR(float i3MVZR) {
        mI3MVZR = i3MVZR;
    }

    public float getUViuR() {
        return mUViuR;
    }

    public void setUViuR(float UViuR) {
        mUViuR = UViuR;
    }

    public float getTViuR() {
        return mTViuR;
    }

    public void setTViuR(float TViuR) {
        mTViuR = TViuR;
    }

    public float getVOverloadR() {
        return mVOverloadR;
    }

    public void setVOverloadR(float VOverloadR) {
        mVOverloadR = VOverloadR;
    }

    public float getTOverloadR() {
        return mTOverloadR;
    }

    public void setTOverloadR(float TOverloadR) {
        mTOverloadR = TOverloadR;
    }

    public float getSpecifiedIOverloadR() {
        return mSpecifiedIOverloadR;
    }

    public void setSpecifiedIOverloadR(float specifiedIOverloadR) {
        mSpecifiedIOverloadR = specifiedIOverloadR;
    }

    public float getIOverloadR() {
        return mIOverloadR;
    }

    public void setIOverloadR(float IOverloadR) {
        mIOverloadR = IOverloadR;
    }

    public float getNoiseR() {
        return mNoiseR;
    }

    public void setNoiseR(float noiseR) {
        mNoiseR = noiseR;
    }

    public float getX1R() {
        return mX1R;
    }

    public void setX1R(float x1R) {
        mX1R = x1R;
    }

    public float getY1R() {
        return mY1R;
    }

    public void setY1R(float y1R) {
        mY1R = y1R;
    }

    public float getZ1R() {
        return mZ1R;
    }

    public void setZ1R(float z1R) {
        mZ1R = z1R;
    }

    public float getX2R() {
        return mX2R;
    }

    public void setX2R(float x2R) {
        mX2R = x2R;
    }

    public float getY2R() {
        return mY2R;
    }

    public void setY2R(float y2R) {
        mY2R = y2R;
    }

    public float getZ2R() {
        return mZ2R;
    }

    public void setZ2R(float z2R) {
        mZ2R = z2R;
    }

    public String getPosition1() {
        return mPosition1;
    }

    public void setPosition1(String position1) {
        mPosition1 = position1;
    }

    public String getPosition1Number() {
        return mPosition1Number;
    }

    public void setPosition1Number(String position1Number) {
        mPosition1Number = position1Number;
    }

    public String getPosition1FullName() {
        return mPosition1FullName;
    }

    public void setPosition1FullName(String position1FullName) {
        mPosition1FullName = position1FullName;
    }

    public String getPosition2() {
        return mPosition2;
    }

    public void setPosition2(String position2) {
        mPosition2 = position2;
    }

    public String getPosition2Number() {
        return mPosition2Number;
    }

    public void setPosition2Number(String position2Number) {
        mPosition2Number = position2Number;
    }

    public String getPosition2FullName() {
        return mPosition2FullName;
    }

    public void setPosition2FullName(String position2FullName) {
        mPosition2FullName = position2FullName;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public boolean isPI() {
        return mP2R != -1 || mSpecifiedIOverloadR != -1 || mVOverloadR != -1 || mMMaxR != -1 || mTempEngineR != -1 || mMMinR != -1 || mMStartR != -1 || mNoiseR != -1 || mIkasRHotR != -1;
    }

    public boolean isPSI() {
        return mP2R == -1 && mSpecifiedIOverloadR == -1 && mVOverloadR == -1 && mMMaxR == -1 && mTempEngineR == -1 && mMMinR == -1 && mMStartR == -1 && mNoiseR == -1 && mIkasRHotR == -1;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("Время проведения испытания: HH:mm:ss", RU_LOCALE);
        return String.format("%s. № %s (%s) %s", mId, mSerialNumber, mSubjectName, sdf.format(mDate));
    }
}
