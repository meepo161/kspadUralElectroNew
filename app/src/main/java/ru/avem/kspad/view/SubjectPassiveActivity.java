package ru.avem.kspad.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import ru.avem.kspad.R;
import ru.avem.kspad.database.controller.DatabaseAdapter;
import ru.avem.kspad.database.model.Subject;

import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class SubjectPassiveActivity extends AppCompatActivity {

    @BindView(R.id.name)
    TextView mName;

    @BindView(R.id.p_n)
    TextView mPN;
    @BindView(R.id.u_n)
    TextView mUN;
    @BindView(R.id.i_n)
    TextView mIN;
    @BindView(R.id.m_n)
    TextView mMN;
    @BindView(R.id.v_n)
    TextView mVN;
    @BindView(R.id.winding)
    TextView mWinding;
    @BindView(R.id.s_n)
    TextView mSN;
    @BindView(R.id.efficiency_n)
    TextView mEfficiencyN;
    @BindView(R.id.f_n)
    TextView mFN;

    @BindView(R.id.u_mgr)
    TextView mUMgr;
    @BindView(R.id.r_mgr)
    TextView mRMgr;

    @BindView(R.id.r_ikas)
    TextView mRIkas;

    @BindView(R.id.u_viu)
    TextView mUViu;
    @BindView(R.id.t_viu)
    TextView mTViu;
    @BindView(R.id.i_viu)
    TextView mIViu;

    @BindView(R.id.t_break_in_idle)
    TextView mTBreakInIdle;
    @BindView(R.id.num_of_stages_idle)
    TextView mNumOfStagesIdle;
    @BindView(R.id.t_on_stage_idle)
    TextView mTOnStageIdle;

    @BindView(R.id.num_of_stages_sc)
    TextView mNumOfStagesSc;
    @BindView(R.id.t_on_stage_sc)
    TextView mTOnStageSc;

    @BindView(R.id.t_heating)
    TextView mTHeating;
    @BindView(R.id.temp_heating)
    TextView mTempHeating;

    @BindView(R.id.z1_performance)
    TextView mZ1Performance;
    @BindView(R.id.z2_performance)
    TextView mZ2Performance;
    @BindView(R.id.t_break_in_performance)
    TextView mTBreakInPerformance;
    @BindView(R.id.t_performance)
    TextView mTPerformance;

    @BindView(R.id.k_overload_i)
    TextView mKOverloadI;

    @BindView(R.id.noise)
    TextView mNoise;
    @BindView(R.id.vibration)
    TextView mVibration;

    private OnRealmReceiverCallback mOnRealmReceiverCallback = new OnRealmReceiverCallback() {
        @Override
        public void onRealmReceiver(Realm realm) {
            mRealm = realm;
        }
    };
    private Realm mRealm;

    private long mSubjectId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_subject_passive);
        ButterKnife.bind(this);

        DatabaseAdapter adapter = new DatabaseAdapter(mOnRealmReceiverCallback);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSubjectId = extras.getLong("id");
        }
        if (mSubjectId > 0) {
            adapter.open();
            Subject subject = adapter.getSubject(mSubjectId);
            mName.setText(String.format("%s", subject.getName()));
            mPN.setText(String.format("%s", subject.getPN()));
            mUN.setText(String.format("%s", subject.getUN()));
            mIN.setText(String.format("%s", subject.getIN()));
            mMN.setText(String.format("%s", subject.getMN()));
            mVN.setText(String.format("%s", subject.getVN()));
            mWinding.setText(String.format("%s", subject.getWinding()));
            mSN.setText(String.format("%s", subject.getSN()));
            mEfficiencyN.setText(String.format("%s", subject.getEfficiencyN()));
            mFN.setText(String.format("%s", subject.getFN()));

            mUMgr.setText(String.format("%s", subject.getUMgr()));

            int ERMgr = subject.getRMgr();
            if (ERMgr == -1) {
                mRMgr.setText(String.format("%s", ""));
            } else {
                mRMgr.setText(String.format("%s", ERMgr));
            }

            int ERIkas = subject.getRIkas();
            if (ERIkas == -1) {
                mRIkas.setText(String.format("%s", ""));
            } else {
                mRIkas.setText(String.format("%s", ERIkas));
            }

            mUViu.setText(String.format("%s", subject.getUViu()));
            mTViu.setText(String.format("%s", subject.getTViu()));
            mIViu.setText(String.format("%s", subject.getIViu()));
            mTBreakInIdle.setText(String.format("%s", subject.getTBreakInIdle()));
            mNumOfStagesIdle.setText(String.format("%s", subject.getNumOfStagesIdle()));
            mTOnStageIdle.setText(String.format("%s", subject.getTOnStageIdle()));
            mNumOfStagesSc.setText(String.format("%s", subject.getNumOfStagesSc()));
            mTOnStageSc.setText(String.format("%s", subject.getTOnStageSc()));
            mTHeating.setText(String.format("%s", subject.getTHeating()));

            int ETempHeating = subject.getTempHeating();
            if (ETempHeating == -1) {
                mTempHeating.setText(String.format("%s", ""));
            } else {
                mTempHeating.setText(String.format("%s", ETempHeating));
            }

            mZ1Performance.setText(String.format("%s", subject.getZ1Performance()));
            mZ2Performance.setText(String.format("%s", subject.getZ2Performance()));
            mTBreakInPerformance.setText(String.format("%s", subject.getTBreakInPerformance()));
            mTPerformance.setText(String.format("%s", subject.getTPerformance()));
            mKOverloadI.setText(String.format("%s", subject.getKOverloadI()));

            float ENoise = subject.getNoise();
            if (ENoise == -1) {
                mNoise.setText(String.format("%s", ""));
            } else {
                mNoise.setText(String.format("%s", ENoise));
            }

            float EVibration = subject.getVibration();
            if (EVibration == -1) {
                mVibration.setText(String.format("%s", ""));
            } else {
                mVibration.setText(String.format("%s", EVibration));
            }

            adapter.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}