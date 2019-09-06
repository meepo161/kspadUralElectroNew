package ru.avem.kspad.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import ru.avem.kspad.R;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.model.ExperimentsHolder;

import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class Experiment16Activity extends AppCompatActivity {
    private static final String EXPERIMENT_NAME = "Определение уровня шума и вибрации";
    @BindView(R.id.noise)
    EditText mNoise;
    @BindView(R.id.x1)
    EditText mX1;
    @BindView(R.id.y1)
    EditText mY1;
    @BindView(R.id.z1)
    EditText mZ1;
    @BindView(R.id.x2)
    EditText mX2;
    @BindView(R.id.y2)
    EditText mY2;
    @BindView(R.id.z2)
    EditText mZ2;

    private float mNoiseValue = -1f;
    private float mX1Value = -1f;
    private float mY1Value = -1f;
    private float mZ1Value = -1f;
    private float mX2Value = -1f;
    private float mY2Value = -1f;
    private float mZ2Value = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_experiment16);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        returnValues();
        fillExperimentTable();
        finish();
    }

    private void returnValues() {
        Intent data = new Intent();
        data.putExtra(MainActivity.INPUT_PARAMETER.NOISE_R, mNoiseValue);
        data.putExtra(MainActivity.INPUT_PARAMETER.X1_R, mX1Value);
        data.putExtra(MainActivity.INPUT_PARAMETER.Y1_R, mY1Value);
        data.putExtra(MainActivity.INPUT_PARAMETER.Z1_R, mZ1Value);
        data.putExtra(MainActivity.INPUT_PARAMETER.X2_R, mX2Value);
        data.putExtra(MainActivity.INPUT_PARAMETER.Y2_R, mY2Value);
        data.putExtra(MainActivity.INPUT_PARAMETER.Z2_R, mZ2Value);
        setResult(RESULT_OK, data);
    }

    @OnClick(R.id.experiment_switch)
    public void onViewClicked() {
        if (areAllFieldsFilled()) {
            saveValues();
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areAllFieldsFilled() {
        return (!mNoise.getText().toString().isEmpty() &&
                !mX1.getText().toString().isEmpty() &&
                !mY1.getText().toString().isEmpty() &&
                !mZ1.getText().toString().isEmpty() &&
                !mX2.getText().toString().isEmpty() &&
                !mY2.getText().toString().isEmpty() &&
                !mZ2.getText().toString().isEmpty());
    }

    private void saveValues() {
        mNoiseValue = Float.parseFloat(mNoise.getText().toString());
        mX1Value = Float.parseFloat(mX1.getText().toString());
        mY1Value = Float.parseFloat(mY1.getText().toString());
        mZ1Value = Float.parseFloat(mZ1.getText().toString());
        mX2Value = Float.parseFloat(mX2.getText().toString());
        mY2Value = Float.parseFloat(mY2.getText().toString());
        mZ2Value = Float.parseFloat(mZ2.getText().toString());
    }

    private void fillExperimentTable() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Experiments experiments = ExperimentsHolder.getExperiments();
        experiments.setE16Noise(mNoise.getText().toString());
        experiments.setE16X1(mX1.getText().toString());
        experiments.setE16Y1(mY1.getText().toString());
        experiments.setE16Z1(mZ1.getText().toString());
        experiments.setE16X2(mX2.getText().toString());
        experiments.setE16Y2(mY2.getText().toString());
        experiments.setE16Z2(mZ2.getText().toString());
        realm.commitTransaction();
        realm.close();
    }
}
