package ru.avem.kspad.presenter;

import android.util.SparseBooleanArray;

import ru.avem.kspad.database.model.Protocol;

public interface MainPresenterView {
    void finishApplication();

    void initializeViews();

    void showSaveLayout();

    void showReviewLayout();

    String getSerialNumber();

    void showSubjects(String serialNumber);

    void hideSubjects();

    void toast(String s);

    void uncheckSerialNumberEnter();

    void disableSubjectTab();

    void enableSubjectTab();

    void changeTabToExperiments();

    void initializeSubjectSelector();

    void hideExperimentsViews();

    void selectAllPIExperiments();

    void selectAllPSIExperiments();

    void unselectAllPIExperiments();

    void unselectAllPSIExperiments();

    boolean atLeastOnePIExperimentWasSelected();

    boolean atLeastOnePSIExperimentWasSelected();

    void setNextPIExperimentType(int experimentType);

    void setNextPSIExperimentType(int experimentType);

    void getPermissionDevices();

    void setBroadcastReceiver();

    SparseBooleanArray getPIExperiment();

    SparseBooleanArray getPSIExperiment();

    void PIExperimentsInvalidate();

    void PSIExperimentsInvalidate();

    void showAllExperimentsCompletedDialog();

    void hideAllExperiments();

    void show1Experiment();

    void show2Experiment();

    void show3Experiment();

    void show4Experiment();

    void show5Experiment();

    void show6Experiment();

    void show7Experiment();

    void show8Experiment();

    void show9Experiment();

    void show10Experiment();

    void show11Experiment();

    void show12Experiment();

    void show13Experiment();

    void show14Experiment();

    void show15Experiment();

    void show16Experiment();

    void show17Experiment();

    void showFoundProtocolDialog(Protocol serialNumber);

    void showNames(String serialNumber, String subjectName);

    void showNextCancelButtons();

    void clearResults();

    void isPlatformOneSelected();
}
