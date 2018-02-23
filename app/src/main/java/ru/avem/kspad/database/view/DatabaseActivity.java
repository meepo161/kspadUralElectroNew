package ru.avem.kspad.database.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import ru.avem.kspad.R;
import ru.avem.kspad.database.controller.DatabaseAdapter;
import ru.avem.kspad.database.model.Subject;
import ru.avem.kspad.view.OnRealmReceiverCallback;

import static ru.avem.kspad.R.id.subjects_selector;
import static ru.avem.kspad.utils.Utils.setSpinnerAdapter;
import static ru.avem.kspad.utils.Visibility.onFullscreenMode;

public class DatabaseActivity extends AppCompatActivity {
    @BindView(subjects_selector)
    Spinner mSubjects;

    private OnRealmReceiverCallback mOnRealmReceiverCallback = new OnRealmReceiverCallback() {
        @Override
        public void onRealmReceiver(Realm realm) {
            mRealm = realm;
        }
    };
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFullscreenMode(this);
        setContentView(R.layout.activity_database);
        ButterKnife.bind(this);
        showPasswordDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseAdapter adapter = new DatabaseAdapter(mOnRealmReceiverCallback);
        adapter.open();
        setSpinnerAdapter(this, mSubjects, adapter.getSubjects());
        adapter.close();
    }

    @OnClick({R.id.add_subject, R.id.edit_subject})
    public void onViewClicked(View view) {
        Intent intent = new Intent(getApplicationContext(), SubjectActivity.class);
        switch (view.getId()) {
            case R.id.add_subject:
                startActivity(intent);
                break;
            case R.id.edit_subject:
                if (mSubjects.getSelectedItem() != null) {
                    intent.putExtra("id", ((Subject) mSubjects.getSelectedItem()).getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Выберите ОИ", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Внимание");
        builder.setMessage("Введите пароль для получения доступа и нажмите ОК");
        final EditText input = new EditText(this);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!input.getText().toString().equals("1234")) {
                    Toast.makeText(DatabaseActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                    showPasswordDialog();
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                System.exit(0);
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
