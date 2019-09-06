package ru.avem.kspad.presenter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;
import java.util.Observable;

import ru.avem.kspad.R;
import ru.avem.kspad.database.model.Protocol;
import ru.avem.kspad.database.model.Subject;
import ru.avem.kspad.model.MainModel;

import static ru.avem.kspad.view.MainActivity.EXPERIMENT_1;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_10;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_11;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_12;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_13;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_14;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_15;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_16;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_17;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_2;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_3;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_4;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_5;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_6;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_7;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_8;
import static ru.avem.kspad.view.MainActivity.EXPERIMENT_9;

public class MainPresenter extends Observable {
    public static final int NUM_OF_PI_EXPERIMENTS = 17;
    public static final int NUM_OF_PSI_EXPERIMENTS = 6;

    private final MainPresenterView mView;
    private final MainModel mModel;

    private SparseBooleanArray mPIExperiments;
    private SparseBooleanArray mPSIExperiments;

    private boolean mNeedToSave;
    private Protocol mProtocolForInteraction;


    public MainPresenter(MainPresenterView view, MainModel model) {
        mView = view;
        mModel = model;
    }

    public void activityReady() {
        mView.getPermissionDevices();
        mView.setBroadcastReceiver();
        mView.initializeViews();
        mPIExperiments = mView.getPIExperiment();
        mPSIExperiments = mView.getPSIExperiment();
    }

    public void exitPressed() {
        mView.finishApplication();
    }

    public void serialNumberEnterClicked(boolean checked) {
        if (checked) {
            String serialNumber = mView.getSerialNumber();
            if (!serialNumber.isEmpty()) {
                Protocol protocol = mModel.getProtocolByName(serialNumber);
                if (protocol != null) {
                    mView.showFoundProtocolDialog(protocol);
                } else {
                    mModel.createNewProtocol(serialNumber);
                    mView.showSubjects(serialNumber);
                }
            } else {
                mView.uncheckSerialNumberEnter();
                mView.toast("Введите заводской №");
            }
        } else {
            mModel.destructProtocol();
            mView.hideExperimentsViews();
            mView.initializeSubjectSelector();
            mView.uncheckSerialNumberEnter();
            mView.clearResults();
        }
    }

    public void protocolTabSelected(Context context, Spinner protocols, long startDate, long endDate) {
        if (isNeedToSave()) {
            mView.showSaveLayout();
        } else {
            fillSpinnerFromDB(context, protocols, startDate, endDate);
            mView.showReviewLayout();
        }
    }

    private boolean isNeedToSave() {
        return mNeedToSave;
    }

    public void setNeedToSave(boolean needToSave) {
        mNeedToSave = needToSave;
    }

    private void fillSpinnerFromDB(Context context, Spinner spinner, long startDate, long endDate) {
        ArrayAdapter<Protocol> protocolArrayAdapter =
                new ArrayAdapter<>(context,
                        R.layout.spinner_layout,
                        mModel.getProtocolsByDateFromDB(startDate, endDate));
        spinner.setAdapter(protocolArrayAdapter);
    }

    public void setProtocolForInteraction(Protocol selectedItem) {
        mProtocolForInteraction = selectedItem;
    }

    public void subjectEnter() {
        mView.disableSubjectTab();
        mView.isPlatformOneSelected();
    }

    public void subjectCancel() {
        mView.enableSubjectTab();
        mView.hideExperimentsViews();
    }

    public void subjectNext() {
        mView.changeTabToExperiments();
    }

    public Protocol getProtocolForInteraction() {
        return mProtocolForInteraction;
    }

    public void saveProtocolInDB(String position1, String position1Number, String position1FullName,
                                 String position2, String position2Number, String position2FullName) {
        mModel.saveProtocolInDB(position1, position1Number, position1FullName, position2,
                position2Number, position2FullName);
    }

    public List<Subject> getAllSubjects() {
        return mModel.getAllSubjectsFromDB();
    }

    public void startFirstPIExperiment() {
        if (mView.atLeastOnePIExperimentWasSelected()) {
            mModel.setPISelected(true);
            startNextPIExperiment();
        } else {
            mView.toast("Выберите хотя бы одно испытание");
        }
    }

    public void startFirstPSIExperiment() {
        if (mView.atLeastOnePSIExperimentWasSelected()) {
            mModel.setPISelected(false);
            startNextPSIExperiment();
        } else {
            mView.toast("Выберите хотя бы одно испытание");
        }
    }

    public void startNextPIExperiment() {
        for (int i = 0; i < NUM_OF_PI_EXPERIMENTS; i++) {
            if (mPIExperiments.get(i)) {
                mPIExperiments.put(i, false);
                mView.PIExperimentsInvalidate();
                mView.setNextPIExperimentType(i);
                return;
            }
        }
        mView.showAllExperimentsCompletedDialog();
    }

    public void startNextPSIExperiment() {
        for (int i = 0; i < NUM_OF_PSI_EXPERIMENTS; i++) {
            if (mPSIExperiments.get(i)) {
                mPSIExperiments.put(i, false);
                mView.PSIExperimentsInvalidate();
                mView.setNextPSIExperimentType(i);
                return;
            }
        }
        mView.showAllExperimentsCompletedDialog();
    }

    public void selectAllPIClicked(boolean state) {
        if (state) {
            mView.selectAllPIExperiments();
        } else {
            mView.unselectAllPIExperiments();
        }
    }

    public void selectAllPSIClicked(boolean state) {
        if (state) {
            mView.selectAllPSIExperiments();
        } else {
            mView.unselectAllPSIExperiments();
        }
    }

    public void subjectSelected(Subject selectedSubject) {
        mModel.setCurrentSubject(selectedSubject);
    }

    public void saveResults() {
        setNeedToSave(true);
        mView.showSaveLayout();
    }

    public void setExperimentForDisplay(String selectedItem) {
        mView.hideAllExperiments();
        switch (selectedItem) {
            case EXPERIMENT_1:
                mView.show1Experiment();
                break;
            case EXPERIMENT_2:
                mView.show2Experiment();
                break;
            case EXPERIMENT_3:
                mView.show3Experiment();
                break;
            case EXPERIMENT_4:
                mView.show4Experiment();
                break;
            case EXPERIMENT_5:
                mView.show5Experiment();
                break;
            case EXPERIMENT_6:
                mView.show6Experiment();
                break;
            case EXPERIMENT_7:
                mView.show7Experiment();
                break;
            case EXPERIMENT_8:
                mView.show8Experiment();
                break;
            case EXPERIMENT_9:
                mView.show9Experiment();
                break;
            case EXPERIMENT_10:
                mView.show10Experiment();
                break;
            case EXPERIMENT_11:
                mView.show11Experiment();
                break;
            case EXPERIMENT_12:
                mView.show12Experiment();
                break;
            case EXPERIMENT_13:
                mView.show13Experiment();
                break;
            case EXPERIMENT_14:
                mView.show14Experiment();
                break;
            case EXPERIMENT_15:
                mView.show15Experiment();
                break;
            case EXPERIMENT_16:
                mView.show16Experiment();
                break;
            case EXPERIMENT_17:
                mView.show17Experiment();
                break;
        }
    }

    public void continueProtocolSelected(Protocol protocol) {
        mModel.setNewSubjectDataToProtocol(protocol);

        mModel.setProtocol(protocol);

        mView.showNames(protocol.getSerialNumber(), protocol.getSubjectName());
        mView.showNextCancelButtons();
    }

    public void clearProtocolSelected(Protocol protocol) {
        String serialNumber = protocol.getSerialNumber();
        mModel.deleteProtocolFromDatabase(protocol);
        mModel.createNewProtocol(serialNumber);
        mView.showSubjects(serialNumber);
    }
}
