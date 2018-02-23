package ru.avem.kspad.database.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import ru.avem.kspad.R;
import ru.avem.kspad.database.controller.DatabaseAdapter;
import ru.avem.kspad.database.model.Subject;
import ru.avem.kspad.view.OnRealmReceiverCallback;

import static ru.avem.kspad.utils.Visibility.onFullscreenMode;


public class SubjectActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText mName;

    @BindView(R.id.p_n)
    EditText mPN;
    @BindView(R.id.u_n)
    EditText mUN;
    @BindView(R.id.i_n)
    EditText mIN;
    @BindView(R.id.m_n)
    EditText mMN;
    @BindView(R.id.v_n)
    EditText mVN;
    @BindView(R.id.winding)
    TextView mWinding;
    @BindView(R.id.s_n)
    EditText mSN;
    @BindView(R.id.efficiency_n)
    EditText mEfficiencyN;
    @BindView(R.id.f_n)
    EditText mFN;

    @BindView(R.id.u_mgr)
    EditText mUMgr;
    @BindView(R.id.r_mgr)
    EditText mRMgr;

    @BindView(R.id.r_ikas)
    EditText mRIkas;

    @BindView(R.id.u_viu)
    EditText mUViu;
    @BindView(R.id.t_viu)
    EditText mTViu;
    @BindView(R.id.i_viu)
    EditText mIViu;

    @BindView(R.id.t_break_in_idle)
    EditText mTBreakInIdle;
    @BindView(R.id.num_of_stages_idle)
    EditText mNumOfStagesIdle;
    @BindView(R.id.t_on_stage_idle)
    EditText mTOnStageIdle;

    @BindView(R.id.num_of_stages_sc)
    EditText mNumOfStagesSc;
    @BindView(R.id.t_on_stage_sc)
    EditText mTOnStageSc;

    @BindView(R.id.t_heating)
    EditText mTHeating;
    @BindView(R.id.temp_heating)
    EditText mTempHeating;

    @BindView(R.id.z1_performance)
    EditText mZ1Performance;
    @BindView(R.id.z2_performance)
    EditText mZ2Performance;
    @BindView(R.id.t_break_in_performance)
    EditText mTBreakInPerformance;
    @BindView(R.id.t_performance)
    EditText mTPerformance;

    @BindView(R.id.k_overload_i)
    EditText mKOverloadI;

    @BindView(R.id.noise)
    EditText mNoise;
    @BindView(R.id.vibration)
    EditText mVibration;

    @BindView(R.id.save)
    Button mSaveButton;
    @BindView(R.id.delete)
    Button mDeleteButton;

    private OnRealmReceiverCallback mOnRealmReceiverCallback = new OnRealmReceiverCallback() {
        @Override
        public void onRealmReceiver(Realm realm) {
            mRealm = realm;
        }
    };
    private Realm mRealm;

    private DatabaseAdapter mAdapter;
    private long mSubjectId = 0;
    private Subject mSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_subject);
        ButterKnife.bind(this);

        mAdapter = new DatabaseAdapter(mOnRealmReceiverCallback);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mSubjectId = extras.getLong("id");
        }
        if (mSubjectId > 0) {
            mAdapter.open();
            mSubject = mAdapter.getSubject(mSubjectId);
            mName.setText(String.format("%s", mSubject.getName()));
            mPN.setText(String.format("%s", mSubject.getPN()));
            mUN.setText(String.format("%s", mSubject.getUN()));
            mIN.setText(String.format("%s", mSubject.getIN()));
            mMN.setText(String.format("%s", mSubject.getMN()));
            mVN.setText(String.format("%s", mSubject.getVN()));
            mWinding.setText(String.format("%s", mSubject.getWinding()));
            mSN.setText(String.format("%s", mSubject.getSN()));
            mEfficiencyN.setText(String.format("%s", mSubject.getEfficiencyN()));
            mFN.setText(String.format("%s", mSubject.getFN()));

            mUMgr.setText(String.format("%s", mSubject.getUMgr()));

            int ERMgr = mSubject.getRMgr();
            if (ERMgr == -1) {
                mRMgr.setText(String.format("%s", ""));
            } else {
                mRMgr.setText(String.format("%s", ERMgr));
            }

            int ERIkas = mSubject.getRIkas();
            if (ERIkas == -1) {
                mRIkas.setText(String.format("%s", ""));
            } else {
                mRIkas.setText(String.format("%s", ERIkas));
            }

            mUViu.setText(String.format("%s", mSubject.getUViu()));
            mTViu.setText(String.format("%s", mSubject.getTViu()));
            mIViu.setText(String.format("%s", mSubject.getIViu()));
            mTBreakInIdle.setText(String.format("%s", mSubject.getTBreakInIdle()));
            mNumOfStagesIdle.setText(String.format("%s", mSubject.getNumOfStagesIdle()));
            mTOnStageIdle.setText(String.format("%s", mSubject.getTOnStageIdle()));
            mNumOfStagesSc.setText(String.format("%s", mSubject.getNumOfStagesSc()));
            mTOnStageSc.setText(String.format("%s", mSubject.getTOnStageSc()));
            mTHeating.setText(String.format("%s", mSubject.getTHeating()));

            int ETempHeating = mSubject.getTempHeating();
            if (ETempHeating == -1) {
                mTempHeating.setText(String.format("%s", ""));
            } else {
                mTempHeating.setText(String.format("%s", ETempHeating));
            }

            mZ1Performance.setText(String.format("%s", mSubject.getZ1Performance()));
            mZ2Performance.setText(String.format("%s", mSubject.getZ2Performance()));
            mTBreakInPerformance.setText(String.format("%s", mSubject.getTBreakInPerformance()));
            mTPerformance.setText(String.format("%s", mSubject.getTPerformance()));
            mKOverloadI.setText(String.format("%s", mSubject.getKOverloadI()));

            float ENoise = mSubject.getNoise();
            if (ENoise == -1) {
                mNoise.setText(String.format("%s", ""));
            } else {
                mNoise.setText(String.format("%s", ENoise));
            }

            float EVibration = mSubject.getVibration();
            if (EVibration == -1) {
                mVibration.setText(String.format("%s", ""));
            } else {
                mVibration.setText(String.format("%s", EVibration));
            }

            mAdapter.close();
        } else {
            mDeleteButton.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.winding, R.id.save, R.id.delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.winding:
                if (mWinding.getText().toString().equals("звезда")) {
                    mWinding.setText("треугольник");
                } else {
                    mWinding.setText("звезда");
                }
                break;
            case R.id.save:
                save();
                break;
            case R.id.delete:
                delete();
                break;
        }
    }

    private void save() {
        if (allFieldsAreFilled()) {
            if (allFieldsAreFilledCorrectly()) {
                String name = mName.getText().toString();
                float PN = Float.parseFloat(mPN.getText().toString());
                int UN = Integer.parseInt(mUN.getText().toString());
                float IN = Float.parseFloat(mIN.getText().toString());
                float MN = Float.parseFloat(mMN.getText().toString());
                int VN = Integer.parseInt(mVN.getText().toString());
                String Winding = mWinding.getText().toString();
                float SN = Float.parseFloat(mSN.getText().toString());
                float EfficiencyN = Float.parseFloat(mEfficiencyN.getText().toString());
                float FN = Float.parseFloat(mFN.getText().toString());

                int UMgr = Integer.parseInt(mUMgr.getText().toString());

                int ERMgr;
                try {
                    ERMgr = Integer.parseInt(mRMgr.getText().toString());
                } catch (Exception e) {
                    ERMgr = -1;
                }

                int ERIkas;
                try {
                    ERIkas = Integer.parseInt(mRIkas.getText().toString());
                } catch (Exception e) {
                    ERIkas = -1;
                }

                int UViu = Integer.parseInt(mUViu.getText().toString());
                int TViu = Integer.parseInt(mTViu.getText().toString());
                float IViu = Float.parseFloat(mIViu.getText().toString());
                int TBreakInIdle = Integer.parseInt(mTBreakInIdle.getText().toString());
                int NumOfStagesIdle = Integer.parseInt(mNumOfStagesIdle.getText().toString());
                int TOnStageIdle = Integer.parseInt(mTOnStageIdle.getText().toString());
                int NumOfStagesSc = Integer.parseInt(mNumOfStagesSc.getText().toString());
                int TOnStageSc = Integer.parseInt(mTOnStageSc.getText().toString());
                int THeating = Integer.parseInt(mTHeating.getText().toString());

                int ETempHeating;
                try {
                    ETempHeating = Integer.parseInt(mTempHeating.getText().toString());
                } catch (Exception e) {
                    ETempHeating = -1;
                }

                int Z1Performance = Integer.parseInt(mZ1Performance.getText().toString());
                int Z2Performance = Integer.parseInt(mZ2Performance.getText().toString());
                int TBreakInPerformance = Integer.parseInt(mTBreakInPerformance.getText().toString());
                int TPerformance = Integer.parseInt(mTPerformance.getText().toString());
                float KOverloadI = Float.parseFloat(mKOverloadI.getText().toString());

                float ENoise;
                try {
                    ENoise = Float.parseFloat(mNoise.getText().toString());
                } catch (Exception e) {
                    ENoise = -1;
                }

                float EVibration;
                try {
                    EVibration = Float.parseFloat(mVibration.getText().toString());
                } catch (Exception e) {
                    EVibration = -1;
                }

                Subject subject = new Subject(mSubjectId, name, PN, UN, IN, MN, VN, Winding, SN,
                        EfficiencyN, FN, UMgr, ERMgr, ERIkas, UViu, TViu, IViu, TBreakInIdle,
                        NumOfStagesIdle, TOnStageIdle, NumOfStagesSc, TOnStageSc, THeating,
                        ETempHeating, Z1Performance, Z2Performance, TBreakInPerformance,
                        TPerformance, KOverloadI, ENoise, EVibration);

                mAdapter.open();
                if (mSubjectId > 0) {
                    mAdapter.updateSubject(subject);
                } else {
                    mAdapter.insertSubject(subject);
                }
                mAdapter.close();
                Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
                goHome();
            }
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean allFieldsAreFilled() {
        return mName.getText().length() > 0 &&
                mPN.getText().length() > 0 &&
                mUN.getText().length() > 0 &&
                mIN.getText().length() > 0 &&
                mMN.getText().length() > 0 &&
                mVN.getText().length() > 0 &&
                mWinding.getText().length() > 0 &&
                mSN.getText().length() > 0 &&
                mEfficiencyN.getText().length() > 0 &&
                mFN.getText().length() > 0 &&
                mUMgr.getText().length() > 0 &&
                mRMgr.getText().length() > 0 &&
                mRIkas.getText().length() > 0 &&
                mUViu.getText().length() > 0 &&
                mTViu.getText().length() > 0 &&
                mIViu.getText().length() > 0 &&
                mTBreakInIdle.getText().length() > 0 &&
                mNumOfStagesIdle.getText().length() > 0 &&
                mTOnStageIdle.getText().length() > 0 &&
                mNumOfStagesSc.getText().length() > 0 &&
                mTOnStageSc.getText().length() > 0 &&
                mTHeating.getText().length() > 0 &&
                mTempHeating.getText().length() > 0 &&
                mZ1Performance.getText().length() > 0 &&
                mZ2Performance.getText().length() > 0 &&
                mTBreakInPerformance.getText().length() > 0 &&
                mTPerformance.getText().length() > 0 &&
                mKOverloadI.getText().length() > 0 &&
                mNoise.getText().length() > 0 &&
                mVibration.getText().length() > 0;
    }

    private boolean allFieldsAreFilledCorrectly() {
//        String Winding = mWinding.getText().toString();
//        if (!Winding.equals("звезда") && !Winding.equals("треугольник")) {
//            Toast.makeText(this, "Введите корректное значение в поле \"Схема соединения обмоток\" : звезда или треугольник", Toast.LENGTH_LONG).show();
//            return false;
//        }

        int UMgr = Integer.parseInt(mUMgr.getText().toString());
        if (UMgr < 500 || UMgr > 2500 || (UMgr % 50 != 0)) {
            Toast.makeText(this, "Введите корректное значение в поле \"Напряжение\" опыта \"Измерение сопротивления изоляции обмоток\": больше 500, меньше 2500, кратное 50", Toast.LENGTH_LONG).show();
            return false;
        }

        int UViu = Integer.parseInt(mUViu.getText().toString());
        if (UViu < 500 || UViu > 3000) {
            Toast.makeText(this, "Введите корректное значение в поле \"Напряжение\" опыта \"Испытание изоляции обмоток переменным напряжением\": больше 500, меньше 3000", Toast.LENGTH_LONG).show();
            return false;
        }

        float IViu = Float.parseFloat(mIViu.getText().toString());
        if (IViu < 0.1f || IViu > 1) {
            Toast.makeText(this, "Введите корректное значение в поле \"Ток утечки\" опыта \"Испытание изоляции обмоток переменным напряжением\": больше 0.1, меньше 1", Toast.LENGTH_LONG).show();
            return false;
        }

        float NumOfStagesIdle = Integer.parseInt(mNumOfStagesIdle.getText().toString());
        if (NumOfStagesIdle != 1 && NumOfStagesIdle != 9) {
            Toast.makeText(this, "Введите корректное значение в поле \"Количество ступеней\" опыта \"Определение токов и потерь ХХ\": 1 или 9", Toast.LENGTH_LONG).show();
            return false;
        }

        float NumOfStagesSc = Integer.parseInt(mNumOfStagesSc.getText().toString());
        if (NumOfStagesSc != 1 && NumOfStagesSc != 5) {
            Toast.makeText(this, "Введите корректное значение в поле \"ТКоличество ступеней\" опыта \"Определение токов и потерь КЗ\": 1 или 5", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void delete() {
        mAdapter.open();
        mAdapter.deleteSubject(mSubjectId);
        mAdapter.close();
        goHome();
    }

    private void goHome() {
        Intent intent = new Intent(this, DatabaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}