package ru.avem.kspad.database.controller;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.Sort;
import ru.avem.kspad.database.model.Experiments;
import ru.avem.kspad.database.model.Protocol;
import ru.avem.kspad.database.model.Subject;
import ru.avem.kspad.view.OnRealmReceiverCallback;

public class DatabaseAdapter {
    private Realm mRealm;

    public DatabaseAdapter(OnRealmReceiverCallback onRealmReceiverCallback) {
        mRealm = Realm.getDefaultInstance();
        onRealmReceiverCallback.onRealmReceiver(mRealm);
    }

    public Protocol getProtocolByName(String serialNumber) {
        return mRealm.where(Protocol.class).equalTo("mSerialNumber", serialNumber).findFirst();
    }

    private <E extends RealmModel> int getNextKey(Class<E> clazz) {
        try {
            Number number = mRealm.where(clazz).max("mId");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 1;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 1;
        }
    }

    public void open() {
        mRealm.beginTransaction();
    }

    public List<Subject> getSubjects() {
        return mRealm.where(Subject.class).findAll();
    }

    public void close() {
        mRealm.commitTransaction();
    }

    public Subject getSubject(long subjectId) {
        return mRealm.where(Subject.class).equalTo("mId", subjectId).findFirst();
    }

    public void updateSubject(Subject subject) {
        mRealm.insertOrUpdate(subject);
    }

    public void insertSubject(Subject subject) {
        subject.setId(getNextKey(Subject.class));
        mRealm.insertOrUpdate(subject);
    }

    public void deleteSubject(long subjectId) {
        mRealm.where(Subject.class).equalTo("mId", subjectId).findAll().deleteFirstFromRealm();
    }

    public List<Protocol> getProtocols() {
        return mRealm.where(Protocol.class).findAll();
    }

    public List<Protocol> getProtocolsByDate(long startDate, long endDate) {
        return mRealm.where(Protocol.class).between("mDate", startDate, endDate).sort("mDate", Sort.DESCENDING).findAll();
    }

    public void updateProtocol(Protocol protocol) {
        mRealm.insertOrUpdate(protocol);
    }

    public long insertProtocol(Protocol protocol) {
        int nextKey = getNextKey(Protocol.class);
        protocol.setId(nextKey);
        mRealm.insertOrUpdate(protocol);
        return nextKey;
    }

    public void deleteProtocol(long id) {
        mRealm.where(Protocol.class).equalTo("mId", id).findAll().deleteFirstFromRealm();
    }

    public Experiments getExperimentsById(long id) {
        return mRealm.where(Experiments.class).equalTo("mId", id).findFirst();
    }

    public void deleteExperiments(long id) {
        mRealm.where(Experiments.class).equalTo("mId", id).findAll().deleteFirstFromRealm();
    }

    public void updateExperiments(Experiments experiments) {
        mRealm.insertOrUpdate(experiments);
    }

    public long insertExperiments(Experiments experiments) {
        int nextKey = getNextKey(Experiments.class);
        experiments.setId(nextKey);
        mRealm.insertOrUpdate(experiments);
        return nextKey;
    }
}
