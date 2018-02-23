package ru.avem.kspad.model;

import ru.avem.kspad.database.model.Experiments;

public class ExperimentsHolder {
    private static Experiments sExperiments;

    public static Experiments getExperiments() {
        return sExperiments;
    }

    public static void setExperiments(Experiments experiments) {
        sExperiments = experiments;
    }
}
