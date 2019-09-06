package ru.avem.kspad.database.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Experiments extends RealmObject {
    @PrimaryKey
    private long mId;

    private String mE1IA = "";
    private String mE1UA = "";
    private String mE1IB = "";
    private String mE1UB = "";
    private String mE1S = "";
    private String mE1P1 = "";
    private String mE1Cos = "";
    private String mE1M = "";
    private String mE1V = "";
    private String mE1P2 = "";
    private String mE1Nu = "";
    private String mE1TempAmbient = "";
    private String mE1TempEngine = "";
    private String mE1Sk = "";
    private String mE1T = "";
    private String mE1IC = "";
    private String mE1UC = "";
    private String mE1IAverage = "";
    private String mE1UAverage = "";
    private String mE1SAverage = "";
    private String mE1P1Average = "";
    private String mE1CosAverage = "";
    private String mE1MAverage = "";
    private String mE1VAverage = "";
    private String mE1P2Average = "";
    private String mE1NuAverage = "";
    private String mE1TempEngineAverage = "";
    private String mE1SkAverage = "";
    private String mE1ISpecified = "";
    private String mE1USpecified = "";
    private String mE1SSpecified = "";
    private String mE1P1Specified = "";
    private String mE1CosSpecified = "";
    private String mE1MSpecified = "";
    private String mE1VSpecified = "";
    private String mE1P2Specified = "";
    private String mE1NuSpecified = "";
    private String mE1TempEngineSpecified = "";
    private String mE1SkSpecified = "";
    private String mE1TSpecified = "";

    private String mE2IA = "";
    private String mE2UA = "";
    private String mE2IB = "";
    private String mE2UB = "";
    private String mE2S = "";
    private String mE2P1 = "";
    private String mE2Cos = "";
    private String mE2M = "";
    private String mE2V = "";
    private String mE2P2 = "";
    private String mE2Nu = "";
    private String mE2TempAmbient = "";
    private String mE2TempEngine = "";
    private String mE2Sk = "";
    private String mE2T = "";
    private String mE2IC = "";
    private String mE2UC = "";
    private String mE2IAverage = "";
    private String mE2UAverage = "";
    private String mE2SAverage = "";
    private String mE2P1Average = "";
    private String mE2CosAverage = "";
    private String mE2MAverage = "";
    private String mE2VAverage = "";
    private String mE2P2Average = "";
    private String mE2NuAverage = "";
    private String mE2TempEngineAverage = "";
    private String mE2SkAverage = "";
    private String mE2ISpecified = "";
    private String mE2USpecified = "";
    private String mE2SSpecified = "";
    private String mE2P1Specified = "";
    private String mE2CosSpecified = "";
    private String mE2MSpecified = "";
    private String mE2VSpecified = "";
    private String mE2P2Specified = "";
    private String mE2NuSpecified = "";
    private String mE2TempEngineSpecified = "";
    private String mE2SkSpecified = "";
    private String mE2TSpecified = "";

    private String mE3IA = "";
    private String mE3UA = "";
    private String mE3IB = "";
    private String mE3UB = "";
    private String mE3S = "";
    private String mE3P1 = "";
    private String mE3Cos = "";
    private String mE3M = "";
    private String mE3V = "";
    private String mE3P2 = "";
    private String mE3Nu = "";
    private String mE3TempAmbient = "";
    private String mE3TempEngine = "";
    private String mE3Sk = "";
    private String mE3T = "";
    private String mE3IC = "";
    private String mE3UC = "";
    private String mE3IAverage = "";
    private String mE3UAverage = "";
    private String mE3SAverage = "";
    private String mE3P1Average = "";
    private String mE3CosAverage = "";
    private String mE3MAverage = "";
    private String mE3VAverage = "";
    private String mE3P2Average = "";
    private String mE3NuAverage = "";
    private String mE3TempEngineAverage = "";
    private String mE3SkAverage = "";
    private String mE3ISpecified = "";
    private String mE3USpecified = "";
    private String mE3SSpecified = "";
    private String mE3P1Specified = "";
    private String mE3CosSpecified = "";
    private String mE3MSpecified = "";
    private String mE3VSpecified = "";
    private String mE3P2Specified = "";
    private String mE3NuSpecified = "";
    private String mE3TempEngineSpecified = "";
    private String mE3SkSpecified = "";
    private String mE3TSpecified = "";

    private String mE4U1 = "";
    private String mE4U2 = "";
    private String mE4U3 = "";
    private String mE4I1 = "";
    private String mE4I2 = "";
    private String mE4I3 = "";
    private String mE4Result = "";
    private String mE4T = "";
    private String mE4TSpecified = "";

    private String mE5Ab = "";
    private String mE5Bc = "";
    private String mE5Ac = "";
    private String mE5AverageR = "";
    private String mE5Temp = "";
    private String mE5Result = "";
    private String mE5AverageRSpecified = "";

    private String mE6U = "";
    private String mE6I = "";
    private String mE6T = "";
    private String mE6Result = "";

    private String mE7U13A = "";
    private String mE7I13A = "";
    private String mE7U13B = "";
    private String mE7I13B = "";
    private String mE7P13 = "";
    private String mE7Cos13 = "";
    private String mE7PCop13 = "";
    private String mE7PmPst13 = "";
    private String mE7Pst13 = "";
    private String mE7T13 = "";
    private String mE7U13C = "";
    private String mE7I13C = "";
    private String mE7U13Average = "";
    private String mE7I13Average = "";
    private String mE7U12A = "";
    private String mE7I12A = "";
    private String mE7U12B = "";
    private String mE7I12B = "";
    private String mE7P12 = "";
    private String mE7Cos12 = "";
    private String mE7PCop12 = "";
    private String mE7PmPst12 = "";
    private String mE7Pst12 = "";
    private String mE7T12 = "";
    private String mE7U12C = "";
    private String mE7I12C = "";
    private String mE7U12Average = "";
    private String mE7I12Average = "";
    private String mE7U11A = "";
    private String mE7I11A = "";
    private String mE7U11B = "";
    private String mE7I11B = "";
    private String mE7P11 = "";
    private String mE7Cos11 = "";
    private String mE7PCop11 = "";
    private String mE7PmPst11 = "";
    private String mE7Pst11 = "";
    private String mE7T11 = "";
    private String mE7U11C = "";
    private String mE7I11C = "";
    private String mE7U11Average = "";
    private String mE7I11Average = "";
    private String mE7U10A = "";
    private String mE7I10A = "";
    private String mE7U10B = "";
    private String mE7I10B = "";
    private String mE7P10 = "";
    private String mE7Cos10 = "";
    private String mE7PCop10 = "";
    private String mE7PmPst10 = "";
    private String mE7Pst10 = "";
    private String mE7T10 = "";
    private String mE7U10C = "";
    private String mE7I10C = "";
    private String mE7U10Average = "";
    private String mE7I10Average = "";
    private String mE7U09A = "";
    private String mE7I09A = "";
    private String mE7U09B = "";
    private String mE7I09B = "";
    private String mE7P09 = "";
    private String mE7Cos09 = "";
    private String mE7PCop09 = "";
    private String mE7PmPst09 = "";
    private String mE7Pst09 = "";
    private String mE7T09 = "";
    private String mE7U09C = "";
    private String mE7I09C = "";
    private String mE7U09Average = "";
    private String mE7I09Average = "";
    private String mE7U08A = "";
    private String mE7I08A = "";
    private String mE7U08B = "";
    private String mE7I08B = "";
    private String mE7P08 = "";
    private String mE7Cos08 = "";
    private String mE7PCop08 = "";
    private String mE7PmPst08 = "";
    private String mE7Pst08 = "";
    private String mE7T08 = "";
    private String mE7U08C = "";
    private String mE7I08C = "";
    private String mE7U08Average = "";
    private String mE7I08Average = "";
    private String mE7U07A = "";
    private String mE7I07A = "";
    private String mE7U07B = "";
    private String mE7I07B = "";
    private String mE7P07 = "";
    private String mE7Cos07 = "";
    private String mE7PCop07 = "";
    private String mE7PmPst07 = "";
    private String mE7Pst07 = "";
    private String mE7T07 = "";
    private String mE7U07C = "";
    private String mE7I07C = "";
    private String mE7U07Average = "";
    private String mE7I07Average = "";
    private String mE7U06A = "";
    private String mE7I06A = "";
    private String mE7U06B = "";
    private String mE7I06B = "";
    private String mE7P06 = "";
    private String mE7Cos06 = "";
    private String mE7PCop06 = "";
    private String mE7PmPst06 = "";
    private String mE7Pst06 = "";
    private String mE7T06 = "";
    private String mE7U06C = "";
    private String mE7I06C = "";
    private String mE7U06Average = "";
    private String mE7I06Average = "";
    private String mE7U05A = "";
    private String mE7I05A = "";
    private String mE7U05B = "";
    private String mE7I05B = "";
    private String mE7P05 = "";
    private String mE7Cos05 = "";
    private String mE7PCop05 = "";
    private String mE7PmPst05 = "";
    private String mE7Pst05 = "";
    private String mE7T05 = "";
    private String mE7U05C = "";
    private String mE7I05C = "";
    private String mE7U05Average = "";
    private String mE7I05Average = "";
    private String mE7R = "";
    private String mE7PMech = "";

    private String mE8UA = "";
    private String mE8IA = "";
    private String mE8UB = "";
    private String mE8IB = "";
    private String mE8P = "";
    private String mE8Cos = "";
    private String mE8V = "";
    private String mE8Temp = "";
    private String mE8T = "";
    private String mE8UC = "";
    private String mE8IC = "";
    private String mE8UAverage = "";
    private String mE8IAverage = "";

    private String mE9U10A = "";
    private String mE9I10A = "";
    private String mE9U10B = "";
    private String mE9I10B = "";
    private String mE9P10 = "";
    private String mE9Cos10 = "";
    private String mE9TempAmbient10 = "";
    private String mE9TempEngine10 = "";
    private String mE9T10 = "";
    private String mE9U10C = "";
    private String mE9I10C = "";
    private String mE9U10Average = "";
    private String mE9I10Average = "";
    private String mE9U09A = "";
    private String mE9I09A = "";
    private String mE9U09B = "";
    private String mE9I09B = "";
    private String mE9P09 = "";
    private String mE9Cos09 = "";
    private String mE9TempAmbient09 = "";
    private String mE9TempEngine09 = "";
    private String mE9T09 = "";
    private String mE9U09C = "";
    private String mE9I09C = "";
    private String mE9U09Average = "";
    private String mE9I09Average = "";
    private String mE9U08A = "";
    private String mE9I08A = "";
    private String mE9U08B = "";
    private String mE9I08B = "";
    private String mE9P08 = "";
    private String mE9Cos08 = "";
    private String mE9TempAmbient08 = "";
    private String mE9TempEngine08 = "";
    private String mE9T08 = "";
    private String mE9U08C = "";
    private String mE9I08C = "";
    private String mE9U08Average = "";
    private String mE9I08Average = "";
    private String mE9U07A = "";
    private String mE9I07A = "";
    private String mE9U07B = "";
    private String mE9I07B = "";
    private String mE9P07 = "";
    private String mE9Cos07 = "";
    private String mE9TempAmbient07 = "";
    private String mE9TempEngine07 = "";
    private String mE9T07 = "";
    private String mE9U07C = "";
    private String mE9I07C = "";
    private String mE9U07Average = "";
    private String mE9I07Average = "";
    private String mE9U06A = "";
    private String mE9I06A = "";
    private String mE9U06B = "";
    private String mE9I06B = "";
    private String mE9P06 = "";
    private String mE9Cos06 = "";
    private String mE9TempAmbient06 = "";
    private String mE9TempEngine06 = "";
    private String mE9T06 = "";
    private String mE9U06C = "";
    private String mE9I06C = "";
    private String mE9U06Average = "";
    private String mE9I06Average = "";

    private String mE10IA = "";
    private String mE10UA = "";
    private String mE10IB = "";
    private String mE10UB = "";
    private String mE10S = "";
    private String mE10P1 = "";
    private String mE10Cos = "";
    private String mE10M = "";
    private String mE10V = "";
    private String mE10P2 = "";
    private String mE10Nu = "";
    private String mE10TempAmbient = "";
    private String mE10TempEngine = "";
    private String mE10Sk = "";
    private String mE10T = "";
    private String mE10IC = "";
    private String mE10UC = "";
    private String mE10IAverage = "";
    private String mE10UAverage = "";
    private String mE10SAverage = "";
    private String mE10P1Average = "";
    private String mE10CosAverage = "";
    private String mE10MAverage = "";
    private String mE10VAverage = "";
    private String mE10P2Average = "";
    private String mE10NuAverage = "";
    private String mE10TempEngineAverage = "";
    private String mE10SkAverage = "";
    private String mE10ISpecified = "";
    private String mE10USpecified = "";
    private String mE10SSpecified = "";
    private String mE10P1Specified = "";
    private String mE10CosSpecified = "";
    private String mE10MSpecified = "";
    private String mE10VSpecified = "";
    private String mE10P2Specified = "";
    private String mE10NuSpecified = "";
    private String mE10TempEngineSpecified = "";
    private String mE10SkSpecified = "";
    private String mE10TSpecified = "";

    private String mE11UR = "";
    private String mE11R15 = "";
    private String mE11R60 = "";
    private String mE11K = "";
    private String mE11Temp = "";
    private String mE11Result = "";

    private String mE12IA = "";
    private String mE12UA = "";
    private String mE12IB = "";
    private String mE12UB = "";
    private String mE12S = "";
    private String mE12P1 = "";
    private String mE12Cos = "";
    private String mE12M = "";
    private String mE12V = "";
    private String mE12P2 = "";
    private String mE12Nu = "";
    private String mE12TempAmbient = "";
    private String mE12TempEngine = "";
    private String mE12Sk = "";
    private String mE12T = "";
    private String mE12IC = "";
    private String mE12UC = "";
    private String mE12IAverage = "";
    private String mE12UAverage = "";
    private String mE12SAverage = "";
    private String mE12P1Average = "";
    private String mE12CosAverage = "";
    private String mE12MAverage = "";
    private String mE12VAverage = "";
    private String mE12P2Average = "";
    private String mE12NuAverage = "";
    private String mE12TempEngineAverage = "";
    private String mE12SkAverage = "";
    private String mE12ISpecified = "";
    private String mE12USpecified = "";
    private String mE12SSpecified = "";
    private String mE12P1Specified = "";
    private String mE12CosSpecified = "";
    private String mE12MSpecified = "";
    private String mE12VSpecified = "";
    private String mE12P2Specified = "";
    private String mE12NuSpecified = "";
    private String mE12TempEngineSpecified = "";
    private String mE12SkSpecified = "";
    private String mE12TSpecified = "";

    private String mE13U08A = "";
    private String mE13I08A = "";
    private String mE13U08B = "";
    private String mE13I08B = "";
    private String mE13S08 = "";
    private String mE13P08 = "";
    private String mE13V08 = "";
    private String mE13M08 = "";
    private String mE13F08 = "";
    private String mE13Temp08 = "";
    private String mE13T08 = "";
    private String mE13U08C = "";
    private String mE13I08C = "";
    private String mE13U08Average = "";
    private String mE13I08Average = "";
    private String mE13U11A = "";
    private String mE13I11A = "";
    private String mE13U11B = "";
    private String mE13I11B = "";
    private String mE13S11 = "";
    private String mE13P11 = "";
    private String mE13V11 = "";
    private String mE13M11 = "";
    private String mE13F11 = "";
    private String mE13Temp11 = "";
    private String mE13T11 = "";
    private String mE13U11C = "";
    private String mE13I11C = "";
    private String mE13U11Average = "";
    private String mE13I11Average = "";

    private String mE14M = "";
    private String mE14V = "";

    private String mE15I = "";
    private String mE15M = "";

    private String mE16Noise = "";
    private String mE16X1 = "";
    private String mE16Y1 = "";
    private String mE16Z1 = "";
    private String mE16X2 = "";
    private String mE16Y2 = "";
    private String mE16Z2 = "";

    private String mE17Ab = "";
    private String mE17Bc = "";
    private String mE17Ac = "";
    private String mE17AverageR = "";
    private String mE17Temp = "";
    private String mE17Result = "";
    private String mE17AverageRSpecified = "";

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getE1IA() {
        return mE1IA;
    }

    public void setE1IA(String e1IA) {
        mE1IA = e1IA;
    }

    public String getE1UA() {
        return mE1UA;
    }

    public void setE1UA(String e1UA) {
        mE1UA = e1UA;
    }

    public String getE1IB() {
        return mE1IB;
    }

    public void setE1IB(String e1IB) {
        mE1IB = e1IB;
    }

    public String getE1UB() {
        return mE1UB;
    }

    public void setE1UB(String e1UB) {
        mE1UB = e1UB;
    }

    public String getE1S() {
        return mE1S;
    }

    public void setE1S(String e1S) {
        mE1S = e1S;
    }

    public String getE1P1() {
        return mE1P1;
    }

    public void setE1P1(String e1P1) {
        mE1P1 = e1P1;
    }

    public String getE1Cos() {
        return mE1Cos;
    }

    public void setE1Cos(String e1Cos) {
        mE1Cos = e1Cos;
    }

    public String getE1M() {
        return mE1M;
    }

    public void setE1M(String e1M) {
        mE1M = e1M;
    }

    public String getE1V() {
        return mE1V;
    }

    public void setE1V(String e1V) {
        mE1V = e1V;
    }

    public String getE1P2() {
        return mE1P2;
    }

    public void setE1P2(String e1P2) {
        mE1P2 = e1P2;
    }

    public String getE1Nu() {
        return mE1Nu;
    }

    public void setE1Nu(String e1Nu) {
        mE1Nu = e1Nu;
    }

    public String getE1TempAmbient() {
        return mE1TempAmbient;
    }

    public void setE1TempAmbient(String e1TempAmbient) {
        mE1TempAmbient = e1TempAmbient;
    }

    public String getE1TempEngine() {
        return mE1TempEngine;
    }

    public void setE1TempEngine(String e1TempEngine) {
        mE1TempEngine = e1TempEngine;
    }

    public String getE1Sk() {
        return mE1Sk;
    }

    public void setE1Sk(String e1Sk) {
        mE1Sk = e1Sk;
    }

    public String getE1T() {
        return mE1T;
    }

    public void setE1T(String e1T) {
        mE1T = e1T;
    }

    public String getE1IC() {
        return mE1IC;
    }

    public void setE1IC(String e1IC) {
        mE1IC = e1IC;
    }

    public String getE1UC() {
        return mE1UC;
    }

    public void setE1UC(String e1UC) {
        mE1UC = e1UC;
    }

    public String getE1IAverage() {
        return mE1IAverage;
    }

    public void setE1IAverage(String e1IAverage) {
        mE1IAverage = e1IAverage;
    }

    public String getE1UAverage() {
        return mE1UAverage;
    }

    public void setE1UAverage(String e1UAverage) {
        mE1UAverage = e1UAverage;
    }

    public String getE1SAverage() {
        return mE1SAverage;
    }

    public void setE1SAverage(String e1SAverage) {
        mE1SAverage = e1SAverage;
    }

    public String getE1P1Average() {
        return mE1P1Average;
    }

    public void setE1P1Average(String e1P1Average) {
        mE1P1Average = e1P1Average;
    }

    public String getE1CosAverage() {
        return mE1CosAverage;
    }

    public void setE1CosAverage(String e1CosAverage) {
        mE1CosAverage = e1CosAverage;
    }

    public String getE1MAverage() {
        return mE1MAverage;
    }

    public void setE1MAverage(String e1MAverage) {
        mE1MAverage = e1MAverage;
    }

    public String getE1VAverage() {
        return mE1VAverage;
    }

    public void setE1VAverage(String e1VAverage) {
        mE1VAverage = e1VAverage;
    }

    public String getE1P2Average() {
        return mE1P2Average;
    }

    public void setE1P2Average(String e1P2Average) {
        mE1P2Average = e1P2Average;
    }

    public String getE1NuAverage() {
        return mE1NuAverage;
    }

    public void setE1NuAverage(String e1NuAverage) {
        mE1NuAverage = e1NuAverage;
    }

    public String getE1TempEngineAverage() {
        return mE1TempEngineAverage;
    }

    public void setE1TempEngineAverage(String e1TempEngineAverage) {
        mE1TempEngineAverage = e1TempEngineAverage;
    }

    public String getE1SkAverage() {
        return mE1SkAverage;
    }

    public void setE1SkAverage(String e1SkAverage) {
        mE1SkAverage = e1SkAverage;
    }

    public String getE1ISpecified() {
        return mE1ISpecified;
    }

    public void setE1ISpecified(String e1ISpecified) {
        mE1ISpecified = e1ISpecified;
    }

    public String getE1USpecified() {
        return mE1USpecified;
    }

    public void setE1USpecified(String e1USpecified) {
        mE1USpecified = e1USpecified;
    }

    public String getE1SSpecified() {
        return mE1SSpecified;
    }

    public void setE1SSpecified(String e1SSpecified) {
        mE1SSpecified = e1SSpecified;
    }

    public String getE1P1Specified() {
        return mE1P1Specified;
    }

    public void setE1P1Specified(String e1P1Specified) {
        mE1P1Specified = e1P1Specified;
    }

    public String getE1CosSpecified() {
        return mE1CosSpecified;
    }

    public void setE1CosSpecified(String e1CosSpecified) {
        mE1CosSpecified = e1CosSpecified;
    }

    public String getE1MSpecified() {
        return mE1MSpecified;
    }

    public void setE1MSpecified(String e1MSpecified) {
        mE1MSpecified = e1MSpecified;
    }

    public String getE1VSpecified() {
        return mE1VSpecified;
    }

    public void setE1VSpecified(String e1VSpecified) {
        mE1VSpecified = e1VSpecified;
    }

    public String getE1P2Specified() {
        return mE1P2Specified;
    }

    public void setE1P2Specified(String e1P2Specified) {
        mE1P2Specified = e1P2Specified;
    }

    public String getE1NuSpecified() {
        return mE1NuSpecified;
    }

    public void setE1NuSpecified(String e1NuSpecified) {
        mE1NuSpecified = e1NuSpecified;
    }

    public String getE1TempEngineSpecified() {
        return mE1TempEngineSpecified;
    }

    public void setE1TempEngineSpecified(String e1TempEngineSpecified) {
        mE1TempEngineSpecified = e1TempEngineSpecified;
    }

    public String getE1SkSpecified() {
        return mE1SkSpecified;
    }

    public void setE1SkSpecified(String e1SkSpecified) {
        mE1SkSpecified = e1SkSpecified;
    }

    public String getE1TSpecified() {
        return mE1TSpecified;
    }

    public void setE1TSpecified(String e1TSpecified) {
        mE1TSpecified = e1TSpecified;
    }


    public String getE2IA() {
        return mE2IA;
    }

    public void setE2IA(String e2IA) {
        mE2IA = e2IA;
    }

    public String getE2UA() {
        return mE2UA;
    }

    public void setE2UA(String e2UA) {
        mE2UA = e2UA;
    }

    public String getE2IB() {
        return mE2IB;
    }

    public void setE2IB(String e2IB) {
        mE2IB = e2IB;
    }

    public String getE2UB() {
        return mE2UB;
    }

    public void setE2UB(String e2UB) {
        mE2UB = e2UB;
    }

    public String getE2S() {
        return mE2S;
    }

    public void setE2S(String e2S) {
        mE2S = e2S;
    }

    public String getE2P1() {
        return mE2P1;
    }

    public void setE2P1(String e2P1) {
        mE2P1 = e2P1;
    }

    public String getE2Cos() {
        return mE2Cos;
    }

    public void setE2Cos(String e2Cos) {
        mE2Cos = e2Cos;
    }

    public String getE2M() {
        return mE2M;
    }

    public void setE2M(String e2M) {
        mE2M = e2M;
    }

    public String getE2V() {
        return mE2V;
    }

    public void setE2V(String e2V) {
        mE2V = e2V;
    }

    public String getE2P2() {
        return mE2P2;
    }

    public void setE2P2(String e2P2) {
        mE2P2 = e2P2;
    }

    public String getE2Nu() {
        return mE2Nu;
    }

    public void setE2Nu(String e2Nu) {
        mE2Nu = e2Nu;
    }

    public String getE2TempAmbient() {
        return mE2TempAmbient;
    }

    public void setE2TempAmbient(String e2TempAmbient) {
        mE2TempAmbient = e2TempAmbient;
    }

    public String getE2TempEngine() {
        return mE2TempEngine;
    }

    public void setE2TempEngine(String e2TempEngine) {
        mE2TempEngine = e2TempEngine;
    }

    public String getE2Sk() {
        return mE2Sk;
    }

    public void setE2Sk(String e2Sk) {
        mE2Sk = e2Sk;
    }

    public String getE2T() {
        return mE2T;
    }

    public void setE2T(String e2T) {
        mE2T = e2T;
    }

    public String getE2IC() {
        return mE2IC;
    }

    public void setE2IC(String e2IC) {
        mE2IC = e2IC;
    }

    public String getE2UC() {
        return mE2UC;
    }

    public void setE2UC(String e2UC) {
        mE2UC = e2UC;
    }

    public String getE2IAverage() {
        return mE2IAverage;
    }

    public void setE2IAverage(String e2IAverage) {
        mE2IAverage = e2IAverage;
    }

    public String getE2UAverage() {
        return mE2UAverage;
    }

    public void setE2UAverage(String e2UAverage) {
        mE2UAverage = e2UAverage;
    }

    public String getE2SAverage() {
        return mE2SAverage;
    }

    public void setE2SAverage(String e2SAverage) {
        mE2SAverage = e2SAverage;
    }

    public String getE2P1Average() {
        return mE2P1Average;
    }

    public void setE2P1Average(String e2P1Average) {
        mE2P1Average = e2P1Average;
    }

    public String getE2CosAverage() {
        return mE2CosAverage;
    }

    public void setE2CosAverage(String e2CosAverage) {
        mE2CosAverage = e2CosAverage;
    }

    public String getE2MAverage() {
        return mE2MAverage;
    }

    public void setE2MAverage(String e2MAverage) {
        mE2MAverage = e2MAverage;
    }

    public String getE2VAverage() {
        return mE2VAverage;
    }

    public void setE2VAverage(String e2VAverage) {
        mE2VAverage = e2VAverage;
    }

    public String getE2P2Average() {
        return mE2P2Average;
    }

    public void setE2P2Average(String e2P2Average) {
        mE2P2Average = e2P2Average;
    }

    public String getE2NuAverage() {
        return mE2NuAverage;
    }

    public void setE2NuAverage(String e2NuAverage) {
        mE2NuAverage = e2NuAverage;
    }

    public String getE2TempEngineAverage() {
        return mE2TempEngineAverage;
    }

    public void setE2TempEngineAverage(String e2TempEngineAverage) {
        mE2TempEngineAverage = e2TempEngineAverage;
    }

    public String getE2SkAverage() {
        return mE2SkAverage;
    }

    public void setE2SkAverage(String e2SkAverage) {
        mE2SkAverage = e2SkAverage;
    }

    public String getE2ISpecified() {
        return mE2ISpecified;
    }

    public void setE2ISpecified(String e2ISpecified) {
        mE2ISpecified = e2ISpecified;
    }

    public String getE2USpecified() {
        return mE2USpecified;
    }

    public void setE2USpecified(String e2USpecified) {
        mE2USpecified = e2USpecified;
    }

    public String getE2SSpecified() {
        return mE2SSpecified;
    }

    public void setE2SSpecified(String e2SSpecified) {
        mE2SSpecified = e2SSpecified;
    }

    public String getE2P1Specified() {
        return mE2P1Specified;
    }

    public void setE2P1Specified(String e2P1Specified) {
        mE2P1Specified = e2P1Specified;
    }

    public String getE2CosSpecified() {
        return mE2CosSpecified;
    }

    public void setE2CosSpecified(String e2CosSpecified) {
        mE2CosSpecified = e2CosSpecified;
    }

    public String getE2MSpecified() {
        return mE2MSpecified;
    }

    public void setE2MSpecified(String e2MSpecified) {
        mE2MSpecified = e2MSpecified;
    }

    public String getE2VSpecified() {
        return mE2VSpecified;
    }

    public void setE2VSpecified(String e2VSpecified) {
        mE2VSpecified = e2VSpecified;
    }

    public String getE2P2Specified() {
        return mE2P2Specified;
    }

    public void setE2P2Specified(String e2P2Specified) {
        mE2P2Specified = e2P2Specified;
    }

    public String getE2NuSpecified() {
        return mE2NuSpecified;
    }

    public void setE2NuSpecified(String e2NuSpecified) {
        mE2NuSpecified = e2NuSpecified;
    }

    public String getE2TempEngineSpecified() {
        return mE2TempEngineSpecified;
    }

    public void setE2TempEngineSpecified(String e2TempEngineSpecified) {
        mE2TempEngineSpecified = e2TempEngineSpecified;
    }

    public String getE2SkSpecified() {
        return mE2SkSpecified;
    }

    public void setE2SkSpecified(String e2SkSpecified) {
        mE2SkSpecified = e2SkSpecified;
    }

    public String getE2TSpecified() {
        return mE2TSpecified;
    }

    public void setE2TSpecified(String e2TSpecified) {
        mE2TSpecified = e2TSpecified;
    }


    public String getE3IA() {
        return mE3IA;
    }

    public void setE3IA(String e3IA) {
        mE3IA = e3IA;
    }

    public String getE3UA() {
        return mE3UA;
    }

    public void setE3UA(String e3UA) {
        mE3UA = e3UA;
    }

    public String getE3IB() {
        return mE3IB;
    }

    public void setE3IB(String e3IB) {
        mE3IB = e3IB;
    }

    public String getE3UB() {
        return mE3UB;
    }

    public void setE3UB(String e3UB) {
        mE3UB = e3UB;
    }

    public String getE3S() {
        return mE3S;
    }

    public void setE3S(String e3S) {
        mE3S = e3S;
    }

    public String getE3P1() {
        return mE3P1;
    }

    public void setE3P1(String e3P1) {
        mE3P1 = e3P1;
    }

    public String getE3Cos() {
        return mE3Cos;
    }

    public void setE3Cos(String e3Cos) {
        mE3Cos = e3Cos;
    }

    public String getE3M() {
        return mE3M;
    }

    public void setE3M(String e3M) {
        mE3M = e3M;
    }

    public String getE3V() {
        return mE3V;
    }

    public void setE3V(String e3V) {
        mE3V = e3V;
    }

    public String getE3P2() {
        return mE3P2;
    }

    public void setE3P2(String e3P2) {
        mE3P2 = e3P2;
    }

    public String getE3Nu() {
        return mE3Nu;
    }

    public void setE3Nu(String e3Nu) {
        mE3Nu = e3Nu;
    }

    public String getE3TempAmbient() {
        return mE3TempAmbient;
    }

    public void setE3TempAmbient(String e3TempAmbient) {
        mE3TempAmbient = e3TempAmbient;
    }

    public String getE3TempEngine() {
        return mE3TempEngine;
    }

    public void setE3TempEngine(String e3TempEngine) {
        mE3TempEngine = e3TempEngine;
    }

    public String getE3Sk() {
        return mE3Sk;
    }

    public void setE3Sk(String e3Sk) {
        mE3Sk = e3Sk;
    }

    public String getE3T() {
        return mE3T;
    }

    public void setE3T(String e3T) {
        mE3T = e3T;
    }

    public String getE3IC() {
        return mE3IC;
    }

    public void setE3IC(String e3IC) {
        mE3IC = e3IC;
    }

    public String getE3UC() {
        return mE3UC;
    }

    public void setE3UC(String e3UC) {
        mE3UC = e3UC;
    }

    public String getE3IAverage() {
        return mE3IAverage;
    }

    public void setE3IAverage(String e3IAverage) {
        mE3IAverage = e3IAverage;
    }

    public String getE3UAverage() {
        return mE3UAverage;
    }

    public void setE3UAverage(String e3UAverage) {
        mE3UAverage = e3UAverage;
    }

    public String getE3SAverage() {
        return mE3SAverage;
    }

    public void setE3SAverage(String e3SAverage) {
        mE3SAverage = e3SAverage;
    }

    public String getE3P1Average() {
        return mE3P1Average;
    }

    public void setE3P1Average(String e3P1Average) {
        mE3P1Average = e3P1Average;
    }

    public String getE3CosAverage() {
        return mE3CosAverage;
    }

    public void setE3CosAverage(String e3CosAverage) {
        mE3CosAverage = e3CosAverage;
    }

    public String getE3MAverage() {
        return mE3MAverage;
    }

    public void setE3MAverage(String e3MAverage) {
        mE3MAverage = e3MAverage;
    }

    public String getE3VAverage() {
        return mE3VAverage;
    }

    public void setE3VAverage(String e3VAverage) {
        mE3VAverage = e3VAverage;
    }

    public String getE3P2Average() {
        return mE3P2Average;
    }

    public void setE3P2Average(String e3P2Average) {
        mE3P2Average = e3P2Average;
    }

    public String getE3NuAverage() {
        return mE3NuAverage;
    }

    public void setE3NuAverage(String e3NuAverage) {
        mE3NuAverage = e3NuAverage;
    }

    public String getE3TempEngineAverage() {
        return mE3TempEngineAverage;
    }

    public void setE3TempEngineAverage(String e3TempEngineAverage) {
        mE3TempEngineAverage = e3TempEngineAverage;
    }

    public String getE3SkAverage() {
        return mE3SkAverage;
    }

    public void setE3SkAverage(String e3SkAverage) {
        mE3SkAverage = e3SkAverage;
    }

    public String getE3ISpecified() {
        return mE3ISpecified;
    }

    public void setE3ISpecified(String e3ISpecified) {
        mE3ISpecified = e3ISpecified;
    }

    public String getE3USpecified() {
        return mE3USpecified;
    }

    public void setE3USpecified(String e3USpecified) {
        mE3USpecified = e3USpecified;
    }

    public String getE3SSpecified() {
        return mE3SSpecified;
    }

    public void setE3SSpecified(String e3SSpecified) {
        mE3SSpecified = e3SSpecified;
    }

    public String getE3P1Specified() {
        return mE3P1Specified;
    }

    public void setE3P1Specified(String e3P1Specified) {
        mE3P1Specified = e3P1Specified;
    }

    public String getE3CosSpecified() {
        return mE3CosSpecified;
    }

    public void setE3CosSpecified(String e3CosSpecified) {
        mE3CosSpecified = e3CosSpecified;
    }

    public String getE3MSpecified() {
        return mE3MSpecified;
    }

    public void setE3MSpecified(String e3MSpecified) {
        mE3MSpecified = e3MSpecified;
    }

    public String getE3VSpecified() {
        return mE3VSpecified;
    }

    public void setE3VSpecified(String e3VSpecified) {
        mE3VSpecified = e3VSpecified;
    }

    public String getE3P2Specified() {
        return mE3P2Specified;
    }

    public void setE3P2Specified(String e3P2Specified) {
        mE3P2Specified = e3P2Specified;
    }

    public String getE3NuSpecified() {
        return mE3NuSpecified;
    }

    public void setE3NuSpecified(String e3NuSpecified) {
        mE3NuSpecified = e3NuSpecified;
    }

    public String getE3TempEngineSpecified() {
        return mE3TempEngineSpecified;
    }

    public void setE3TempEngineSpecified(String e3TempEngineSpecified) {
        mE3TempEngineSpecified = e3TempEngineSpecified;
    }

    public String getE3SkSpecified() {
        return mE3SkSpecified;
    }

    public void setE3SkSpecified(String e3SkSpecified) {
        mE3SkSpecified = e3SkSpecified;
    }

    public String getE3TSpecified() {
        return mE3TSpecified;
    }

    public void setE3TSpecified(String e3TSpecified) {
        mE3TSpecified = e3TSpecified;
    }


    public String getE4U1() {
        return mE4U1;
    }

    public void setE4U1(String e4U1) {
        mE4U1 = e4U1;
    }

    public String getE4U2() {
        return mE4U2;
    }

    public void setE4U2(String e4U2) {
        mE4U2 = e4U2;
    }

    public String getE4U3() {
        return mE4U3;
    }

    public void setE4U3(String e4U3) {
        mE4U3 = e4U3;
    }

    public String getE4I1() {
        return mE4I1;
    }

    public void setE4I1(String e4I1) {
        mE4I1 = e4I1;
    }

    public String getE4I2() {
        return mE4I2;
    }

    public void setE4I2(String e4I2) {
        mE4I2 = e4I2;
    }

    public String getE4I3() {
        return mE4I3;
    }

    public void setE4I3(String e4I3) {
        mE4I3 = e4I3;
    }

    public String getE4Result() {
        return mE4Result;
    }

    public void setE4Result(String e4Result) {
        mE4Result = e4Result;
    }

    public String getE4T() {
        return mE4T;
    }

    public void setE4T(String e4T) {
        mE4T = e4T;
    }

    public String getE4TSpecified() {
        return mE4TSpecified;
    }

    public void setE4TSpecified(String e4TSpecified) {
        mE4TSpecified = e4TSpecified;
    }


    public String getE5Ab() {
        return mE5Ab;
    }

    public void setE5Ab(String e5Ab) {
        mE5Ab = e5Ab;
    }

    public String getE5Bc() {
        return mE5Bc;
    }

    public void setE5Bc(String e5Bc) {
        mE5Bc = e5Bc;
    }

    public String getE5Ac() {
        return mE5Ac;
    }

    public void setE5Ac(String e5Ac) {
        mE5Ac = e5Ac;
    }

    public String getE5AverageR() {
        return mE5AverageR;
    }

    public void setE5AverageR(String e5AverageR) {
        mE5AverageR = e5AverageR;
    }

    public String getE5Temp() {
        return mE5Temp;
    }

    public void setE5Temp(String e5Temp) {
        mE5Temp = e5Temp;
    }

    public String getE5Result() {
        return mE5Result;
    }

    public void setE5Result(String e5Result) {
        mE5Result = e5Result;
    }

    public String getE5AverageRSpecified() {
        return mE5AverageRSpecified;
    }

    public void setE5AverageRSpecified(String e5AverageRSpecified) {
        mE5AverageRSpecified = e5AverageRSpecified;
    }


    public String getE6U() {
        return mE6U;
    }

    public void setE6U(String e6U) {
        mE6U = e6U;
    }

    public String getE6I() {
        return mE6I;
    }

    public void setE6I(String e6I) {
        mE6I = e6I;
    }

    public String getE6T() {
        return mE6T;
    }

    public void setE6T(String e6T) {
        mE6T = e6T;
    }

    public String getE6Result() {
        return mE6Result;
    }


    public void setE6Result(String e6Result) {
        mE6Result = e6Result;
    }

    public String getE7U13A() {
        return mE7U13A;
    }

    public void setE7U13A(String e7U13A) {
        mE7U13A = e7U13A;
    }

    public String getE7I13A() {
        return mE7I13A;
    }

    public void setE7I13A(String e7I13A) {
        mE7I13A = e7I13A;
    }

    public String getE7U13B() {
        return mE7U13B;
    }

    public void setE7U13B(String e7U13B) {
        mE7U13B = e7U13B;
    }

    public String getE7I13B() {
        return mE7I13B;
    }

    public void setE7I13B(String e7I13B) {
        mE7I13B = e7I13B;
    }

    public String getE7P13() {
        return mE7P13;
    }

    public void setE7P13(String e7P13) {
        mE7P13 = e7P13;
    }

    public String getE7Cos13() {
        return mE7Cos13;
    }

    public void setE7Cos13(String e7Cos13) {
        mE7Cos13 = e7Cos13;
    }

    public String getE7PCop13() {
        return mE7PCop13;
    }

    public void setE7PCop13(String e7PCop13) {
        mE7PCop13 = e7PCop13;
    }

    public String getE7PmPst13() {
        return mE7PmPst13;
    }

    public void setE7PmPst13(String e7PmPst13) {
        mE7PmPst13 = e7PmPst13;
    }

    public String getE7Pst13() {
        return mE7Pst13;
    }

    public void setE7Pst13(String e7Pst13) {
        mE7Pst13 = e7Pst13;
    }

    public String getE7T13() {
        return mE7T13;
    }

    public void setE7T13(String e7T13) {
        mE7T13 = e7T13;
    }

    public String getE7U13C() {
        return mE7U13C;
    }

    public void setE7U13C(String e7U13C) {
        mE7U13C = e7U13C;
    }

    public String getE7I13C() {
        return mE7I13C;
    }

    public void setE7I13C(String e7I13C) {
        mE7I13C = e7I13C;
    }

    public String getE7U13Average() {
        return mE7U13Average;
    }

    public void setE7U13Average(String e7U13Average) {
        mE7U13Average = e7U13Average;
    }

    public String getE7I13Average() {
        return mE7I13Average;
    }

    public void setE7I13Average(String e7I13Average) {
        mE7I13Average = e7I13Average;
    }

    public String getE7U12A() {
        return mE7U12A;
    }

    public void setE7U12A(String e7U12A) {
        mE7U12A = e7U12A;
    }

    public String getE7I12A() {
        return mE7I12A;
    }

    public void setE7I12A(String e7I12A) {
        mE7I12A = e7I12A;
    }

    public String getE7U12B() {
        return mE7U12B;
    }

    public void setE7U12B(String e7U12B) {
        mE7U12B = e7U12B;
    }

    public String getE7I12B() {
        return mE7I12B;
    }

    public void setE7I12B(String e7I12B) {
        mE7I12B = e7I12B;
    }

    public String getE7P12() {
        return mE7P12;
    }

    public void setE7P12(String e7P12) {
        mE7P12 = e7P12;
    }

    public String getE7Cos12() {
        return mE7Cos12;
    }

    public void setE7Cos12(String e7Cos12) {
        mE7Cos12 = e7Cos12;
    }

    public String getE7PCop12() {
        return mE7PCop12;
    }

    public void setE7PCop12(String e7PCop12) {
        mE7PCop12 = e7PCop12;
    }

    public String getE7PmPst12() {
        return mE7PmPst12;
    }

    public void setE7PmPst12(String e7PmPst12) {
        mE7PmPst12 = e7PmPst12;
    }

    public String getE7Pst12() {
        return mE7Pst12;
    }

    public void setE7Pst12(String e7Pst12) {
        mE7Pst12 = e7Pst12;
    }

    public String getE7T12() {
        return mE7T12;
    }

    public void setE7T12(String e7T12) {
        mE7T12 = e7T12;
    }

    public String getE7U12C() {
        return mE7U12C;
    }

    public void setE7U12C(String e7U12C) {
        mE7U12C = e7U12C;
    }

    public String getE7I12C() {
        return mE7I12C;
    }

    public void setE7I12C(String e7I12C) {
        mE7I12C = e7I12C;
    }

    public String getE7U12Average() {
        return mE7U12Average;
    }

    public void setE7U12Average(String e7U12Average) {
        mE7U12Average = e7U12Average;
    }

    public String getE7I12Average() {
        return mE7I12Average;
    }

    public void setE7I12Average(String e7I12Average) {
        mE7I12Average = e7I12Average;
    }

    public String getE7U11A() {
        return mE7U11A;
    }

    public void setE7U11A(String e7U11A) {
        mE7U11A = e7U11A;
    }

    public String getE7I11A() {
        return mE7I11A;
    }

    public void setE7I11A(String e7I11A) {
        mE7I11A = e7I11A;
    }

    public String getE7U11B() {
        return mE7U11B;
    }

    public void setE7U11B(String e7U11B) {
        mE7U11B = e7U11B;
    }

    public String getE7I11B() {
        return mE7I11B;
    }

    public void setE7I11B(String e7I11B) {
        mE7I11B = e7I11B;
    }

    public String getE7P11() {
        return mE7P11;
    }

    public void setE7P11(String e7P11) {
        mE7P11 = e7P11;
    }

    public String getE7Cos11() {
        return mE7Cos11;
    }

    public void setE7Cos11(String e7Cos11) {
        mE7Cos11 = e7Cos11;
    }

    public String getE7PCop11() {
        return mE7PCop11;
    }

    public void setE7PCop11(String e7PCop11) {
        mE7PCop11 = e7PCop11;
    }

    public String getE7PmPst11() {
        return mE7PmPst11;
    }

    public void setE7PmPst11(String e7PmPst11) {
        mE7PmPst11 = e7PmPst11;
    }

    public String getE7Pst11() {
        return mE7Pst11;
    }

    public void setE7Pst11(String e7Pst11) {
        mE7Pst11 = e7Pst11;
    }

    public String getE7T11() {
        return mE7T11;
    }

    public void setE7T11(String e7T11) {
        mE7T11 = e7T11;
    }

    public String getE7U11C() {
        return mE7U11C;
    }

    public void setE7U11C(String e7U11C) {
        mE7U11C = e7U11C;
    }

    public String getE7I11C() {
        return mE7I11C;
    }

    public void setE7I11C(String e7I11C) {
        mE7I11C = e7I11C;
    }

    public String getE7U11Average() {
        return mE7U11Average;
    }

    public void setE7U11Average(String e7U11Average) {
        mE7U11Average = e7U11Average;
    }

    public String getE7I11Average() {
        return mE7I11Average;
    }

    public void setE7I11Average(String e7I11Average) {
        mE7I11Average = e7I11Average;
    }

    public String getE7U10A() {
        return mE7U10A;
    }

    public void setE7U10A(String e7U10A) {
        mE7U10A = e7U10A;
    }

    public String getE7I10A() {
        return mE7I10A;
    }

    public void setE7I10A(String e7I10A) {
        mE7I10A = e7I10A;
    }

    public String getE7U10B() {
        return mE7U10B;
    }

    public void setE7U10B(String e7U10B) {
        mE7U10B = e7U10B;
    }

    public String getE7I10B() {
        return mE7I10B;
    }

    public void setE7I10B(String e7I10B) {
        mE7I10B = e7I10B;
    }

    public String getE7P10() {
        return mE7P10;
    }

    public void setE7P10(String e7P10) {
        mE7P10 = e7P10;
    }

    public String getE7Cos10() {
        return mE7Cos10;
    }

    public void setE7Cos10(String e7Cos10) {
        mE7Cos10 = e7Cos10;
    }

    public String getE7PCop10() {
        return mE7PCop10;
    }

    public void setE7PCop10(String e7PCop10) {
        mE7PCop10 = e7PCop10;
    }

    public String getE7PmPst10() {
        return mE7PmPst10;
    }

    public void setE7PmPst10(String e7PmPst10) {
        mE7PmPst10 = e7PmPst10;
    }

    public String getE7Pst10() {
        return mE7Pst10;
    }

    public void setE7Pst10(String e7Pst10) {
        mE7Pst10 = e7Pst10;
    }

    public String getE7T10() {
        return mE7T10;
    }

    public void setE7T10(String e7T10) {
        mE7T10 = e7T10;
    }

    public String getE7U10C() {
        return mE7U10C;
    }

    public void setE7U10C(String e7U10C) {
        mE7U10C = e7U10C;
    }

    public String getE7I10C() {
        return mE7I10C;
    }

    public void setE7I10C(String e7I10C) {
        mE7I10C = e7I10C;
    }

    public String getE7U10Average() {
        return mE7U10Average;
    }

    public void setE7U10Average(String e7U10Average) {
        mE7U10Average = e7U10Average;
    }

    public String getE7I10Average() {
        return mE7I10Average;
    }

    public void setE7I10Average(String e7I10Average) {
        mE7I10Average = e7I10Average;
    }

    public String getE7U09A() {
        return mE7U09A;
    }

    public void setE7U09A(String e7U09A) {
        mE7U09A = e7U09A;
    }

    public String getE7I09A() {
        return mE7I09A;
    }

    public void setE7I09A(String e7I09A) {
        mE7I09A = e7I09A;
    }

    public String getE7U09B() {
        return mE7U09B;
    }

    public void setE7U09B(String e7U09B) {
        mE7U09B = e7U09B;
    }

    public String getE7I09B() {
        return mE7I09B;
    }

    public void setE7I09B(String e7I09B) {
        mE7I09B = e7I09B;
    }

    public String getE7P09() {
        return mE7P09;
    }

    public void setE7P09(String e7P09) {
        mE7P09 = e7P09;
    }

    public String getE7Cos09() {
        return mE7Cos09;
    }

    public void setE7Cos09(String e7Cos09) {
        mE7Cos09 = e7Cos09;
    }

    public String getE7PCop09() {
        return mE7PCop09;
    }

    public void setE7PCop09(String e7PCop09) {
        mE7PCop09 = e7PCop09;
    }

    public String getE7PmPst09() {
        return mE7PmPst09;
    }

    public void setE7PmPst09(String e7PmPst09) {
        mE7PmPst09 = e7PmPst09;
    }

    public String getE7Pst09() {
        return mE7Pst09;
    }

    public void setE7Pst09(String e7Pst09) {
        mE7Pst09 = e7Pst09;
    }

    public String getE7T09() {
        return mE7T09;
    }

    public void setE7T09(String e7T09) {
        mE7T09 = e7T09;
    }

    public String getE7U09C() {
        return mE7U09C;
    }

    public void setE7U09C(String e7U09C) {
        mE7U09C = e7U09C;
    }

    public String getE7I09C() {
        return mE7I09C;
    }

    public void setE7I09C(String e7I09C) {
        mE7I09C = e7I09C;
    }

    public String getE7U09Average() {
        return mE7U09Average;
    }

    public void setE7U09Average(String e7U09Average) {
        mE7U09Average = e7U09Average;
    }

    public String getE7I09Average() {
        return mE7I09Average;
    }

    public void setE7I09Average(String e7I09Average) {
        mE7I09Average = e7I09Average;
    }

    public String getE7U08A() {
        return mE7U08A;
    }

    public void setE7U08A(String e7U08A) {
        mE7U08A = e7U08A;
    }

    public String getE7I08A() {
        return mE7I08A;
    }

    public void setE7I08A(String e7I08A) {
        mE7I08A = e7I08A;
    }

    public String getE7U08B() {
        return mE7U08B;
    }

    public void setE7U08B(String e7U08B) {
        mE7U08B = e7U08B;
    }

    public String getE7I08B() {
        return mE7I08B;
    }

    public void setE7I08B(String e7I08B) {
        mE7I08B = e7I08B;
    }

    public String getE7P08() {
        return mE7P08;
    }

    public void setE7P08(String e7P08) {
        mE7P08 = e7P08;
    }

    public String getE7Cos08() {
        return mE7Cos08;
    }

    public void setE7Cos08(String e7Cos08) {
        mE7Cos08 = e7Cos08;
    }

    public String getE7PCop08() {
        return mE7PCop08;
    }

    public void setE7PCop08(String e7PCop08) {
        mE7PCop08 = e7PCop08;
    }

    public String getE7PmPst08() {
        return mE7PmPst08;
    }

    public void setE7PmPst08(String e7PmPst08) {
        mE7PmPst08 = e7PmPst08;
    }

    public String getE7Pst08() {
        return mE7Pst08;
    }

    public void setE7Pst08(String e7Pst08) {
        mE7Pst08 = e7Pst08;
    }

    public String getE7T08() {
        return mE7T08;
    }

    public void setE7T08(String e7T08) {
        mE7T08 = e7T08;
    }

    public String getE7U08C() {
        return mE7U08C;
    }

    public void setE7U08C(String e7U08C) {
        mE7U08C = e7U08C;
    }

    public String getE7I08C() {
        return mE7I08C;
    }

    public void setE7I08C(String e7I08C) {
        mE7I08C = e7I08C;
    }

    public String getE7U08Average() {
        return mE7U08Average;
    }

    public void setE7U08Average(String e7U08Average) {
        mE7U08Average = e7U08Average;
    }

    public String getE7I08Average() {
        return mE7I08Average;
    }

    public void setE7I08Average(String e7I08Average) {
        mE7I08Average = e7I08Average;
    }

    public String getE7U07A() {
        return mE7U07A;
    }

    public void setE7U07A(String e7U07A) {
        mE7U07A = e7U07A;
    }

    public String getE7I07A() {
        return mE7I07A;
    }

    public void setE7I07A(String e7I07A) {
        mE7I07A = e7I07A;
    }

    public String getE7U07B() {
        return mE7U07B;
    }

    public void setE7U07B(String e7U07B) {
        mE7U07B = e7U07B;
    }

    public String getE7I07B() {
        return mE7I07B;
    }

    public void setE7I07B(String e7I07B) {
        mE7I07B = e7I07B;
    }

    public String getE7P07() {
        return mE7P07;
    }

    public void setE7P07(String e7P07) {
        mE7P07 = e7P07;
    }

    public String getE7Cos07() {
        return mE7Cos07;
    }

    public void setE7Cos07(String e7Cos07) {
        mE7Cos07 = e7Cos07;
    }

    public String getE7PCop07() {
        return mE7PCop07;
    }

    public void setE7PCop07(String e7PCop07) {
        mE7PCop07 = e7PCop07;
    }

    public String getE7PmPst07() {
        return mE7PmPst07;
    }

    public void setE7PmPst07(String e7PmPst07) {
        mE7PmPst07 = e7PmPst07;
    }

    public String getE7Pst07() {
        return mE7Pst07;
    }

    public void setE7Pst07(String e7Pst07) {
        mE7Pst07 = e7Pst07;
    }

    public String getE7T07() {
        return mE7T07;
    }

    public void setE7T07(String e7T07) {
        mE7T07 = e7T07;
    }

    public String getE7U07C() {
        return mE7U07C;
    }

    public void setE7U07C(String e7U07C) {
        mE7U07C = e7U07C;
    }

    public String getE7I07C() {
        return mE7I07C;
    }

    public void setE7I07C(String e7I07C) {
        mE7I07C = e7I07C;
    }

    public String getE7U07Average() {
        return mE7U07Average;
    }

    public void setE7U07Average(String e7U07Average) {
        mE7U07Average = e7U07Average;
    }

    public String getE7I07Average() {
        return mE7I07Average;
    }

    public void setE7I07Average(String e7I07Average) {
        mE7I07Average = e7I07Average;
    }

    public String getE7U06A() {
        return mE7U06A;
    }

    public void setE7U06A(String e7U06A) {
        mE7U06A = e7U06A;
    }

    public String getE7I06A() {
        return mE7I06A;
    }

    public void setE7I06A(String e7I06A) {
        mE7I06A = e7I06A;
    }

    public String getE7U06B() {
        return mE7U06B;
    }

    public void setE7U06B(String e7U06B) {
        mE7U06B = e7U06B;
    }

    public String getE7I06B() {
        return mE7I06B;
    }

    public void setE7I06B(String e7I06B) {
        mE7I06B = e7I06B;
    }

    public String getE7P06() {
        return mE7P06;
    }

    public void setE7P06(String e7P06) {
        mE7P06 = e7P06;
    }

    public String getE7Cos06() {
        return mE7Cos06;
    }

    public void setE7Cos06(String e7Cos06) {
        mE7Cos06 = e7Cos06;
    }

    public String getE7PCop06() {
        return mE7PCop06;
    }

    public void setE7PCop06(String e7PCop06) {
        mE7PCop06 = e7PCop06;
    }

    public String getE7PmPst06() {
        return mE7PmPst06;
    }

    public void setE7PmPst06(String e7PmPst06) {
        mE7PmPst06 = e7PmPst06;
    }

    public String getE7Pst06() {
        return mE7Pst06;
    }

    public void setE7Pst06(String e7Pst06) {
        mE7Pst06 = e7Pst06;
    }

    public String getE7T06() {
        return mE7T06;
    }

    public void setE7T06(String e7T06) {
        mE7T06 = e7T06;
    }

    public String getE7U06C() {
        return mE7U06C;
    }

    public void setE7U06C(String e7U06C) {
        mE7U06C = e7U06C;
    }

    public String getE7I06C() {
        return mE7I06C;
    }

    public void setE7I06C(String e7I06C) {
        mE7I06C = e7I06C;
    }

    public String getE7U06Average() {
        return mE7U06Average;
    }

    public void setE7U06Average(String e7U06Average) {
        mE7U06Average = e7U06Average;
    }

    public String getE7I06Average() {
        return mE7I06Average;
    }

    public void setE7I06Average(String e7I06Average) {
        mE7I06Average = e7I06Average;
    }

    public String getE7U05A() {
        return mE7U05A;
    }

    public void setE7U05A(String e7U05A) {
        mE7U05A = e7U05A;
    }

    public String getE7I05A() {
        return mE7I05A;
    }

    public void setE7I05A(String e7I05A) {
        mE7I05A = e7I05A;
    }

    public String getE7U05B() {
        return mE7U05B;
    }

    public void setE7U05B(String e7U05B) {
        mE7U05B = e7U05B;
    }

    public String getE7I05B() {
        return mE7I05B;
    }

    public void setE7I05B(String e7I05B) {
        mE7I05B = e7I05B;
    }

    public String getE7P05() {
        return mE7P05;
    }

    public void setE7P05(String e7P05) {
        mE7P05 = e7P05;
    }

    public String getE7Cos05() {
        return mE7Cos05;
    }

    public void setE7Cos05(String e7Cos05) {
        mE7Cos05 = e7Cos05;
    }

    public String getE7PCop05() {
        return mE7PCop05;
    }

    public void setE7PCop05(String e7PCop05) {
        mE7PCop05 = e7PCop05;
    }

    public String getE7PmPst05() {
        return mE7PmPst05;
    }

    public void setE7PmPst05(String e7PmPst05) {
        mE7PmPst05 = e7PmPst05;
    }

    public String getE7Pst05() {
        return mE7Pst05;
    }

    public void setE7Pst05(String e7Pst05) {
        mE7Pst05 = e7Pst05;
    }

    public String getE7T05() {
        return mE7T05;
    }

    public void setE7T05(String e7T05) {
        mE7T05 = e7T05;
    }

    public String getE7U05C() {
        return mE7U05C;
    }

    public void setE7U05C(String e7U05C) {
        mE7U05C = e7U05C;
    }

    public String getE7I05C() {
        return mE7I05C;
    }

    public void setE7I05C(String e7I05C) {
        mE7I05C = e7I05C;
    }

    public String getE7U05Average() {
        return mE7U05Average;
    }

    public void setE7U05Average(String e7U05Average) {
        mE7U05Average = e7U05Average;
    }

    public String getE7I05Average() {
        return mE7I05Average;
    }

    public void setE7I05Average(String e7I05Average) {
        mE7I05Average = e7I05Average;
    }

    public String getE7R() {
        return mE7R;
    }

    public void setE7R(String e7R) {
        mE7R = e7R;
    }

    public String getE7PMech() {
        return mE7PMech;
    }

    public void setE7PMech(String e7PMech) {
        mE7PMech = e7PMech;
    }

    public String getE8UA() {
        return mE8UA;
    }

    public void setE8UA(String e8UA) {
        mE8UA = e8UA;
    }

    public String getE8IA() {
        return mE8IA;
    }

    public void setE8IA(String e8IA) {
        mE8IA = e8IA;
    }

    public String getE8UB() {
        return mE8UB;
    }

    public void setE8UB(String e8UB) {
        mE8UB = e8UB;
    }

    public String getE8IB() {
        return mE8IB;
    }

    public void setE8IB(String e8IB) {
        mE8IB = e8IB;
    }

    public String getE8P() {
        return mE8P;
    }

    public void setE8P(String e8P) {
        mE8P = e8P;
    }

    public String getE8Cos() {
        return mE8Cos;
    }

    public void setE8Cos(String e8Cos) {
        mE8Cos = e8Cos;
    }

    public String getE8V() {
        return mE8V;
    }

    public void setE8V(String e8V) {
        mE8V = e8V;
    }

    public String getE8Temp() {
        return mE8Temp;
    }

    public void setE8Temp(String e8Temp) {
        mE8Temp = e8Temp;
    }

    public String getE8T() {
        return mE8T;
    }

    public void setE8T(String e8T) {
        mE8T = e8T;
    }

    public String getE8UC() {
        return mE8UC;
    }

    public void setE8UC(String e8UC) {
        mE8UC = e8UC;
    }

    public String getE8IC() {
        return mE8IC;
    }

    public void setE8IC(String e8IC) {
        mE8IC = e8IC;
    }

    public String getE8UAverage() {
        return mE8UAverage;
    }

    public void setE8UAverage(String e8UAverage) {
        mE8UAverage = e8UAverage;
    }

    public String getE8IAverage() {
        return mE8IAverage;
    }

    public void setE8IAverage(String e8IAverage) {
        mE8IAverage = e8IAverage;
    }


    public String getE9U10A() {
        return mE9U10A;
    }

    public void setE9U10A(String e9U10A) {
        mE9U10A = e9U10A;
    }

    public String getE9I10A() {
        return mE9I10A;
    }

    public void setE9I10A(String e9I10A) {
        mE9I10A = e9I10A;
    }

    public String getE9U10B() {
        return mE9U10B;
    }

    public void setE9U10B(String e9U10B) {
        mE9U10B = e9U10B;
    }

    public String getE9I10B() {
        return mE9I10B;
    }

    public void setE9I10B(String e9I10B) {
        mE9I10B = e9I10B;
    }

    public String getE9P10() {
        return mE9P10;
    }

    public void setE9P10(String e9P10) {
        mE9P10 = e9P10;
    }

    public String getE9Cos10() {
        return mE9Cos10;
    }

    public void setE9Cos10(String e9Cos10) {
        mE9Cos10 = e9Cos10;
    }

    public String getE9TempAmbient10() {
        return mE9TempAmbient10;
    }

    public void setE9TempAmbient10(String e9TempAmbient10) {
        mE9TempAmbient10 = e9TempAmbient10;
    }

    public String getE9TempEngine10() {
        return mE9TempEngine10;
    }

    public void setE9TempEngine10(String e9TempEngine10) {
        mE9TempEngine10 = e9TempEngine10;
    }

    public String getE9T10() {
        return mE9T10;
    }

    public void setE9T10(String e9T10) {
        mE9T10 = e9T10;
    }

    public String getE9U10C() {
        return mE9U10C;
    }

    public void setE9U10C(String e9U10C) {
        mE9U10C = e9U10C;
    }

    public String getE9I10C() {
        return mE9I10C;
    }

    public void setE9I10C(String e9I10C) {
        mE9I10C = e9I10C;
    }

    public String getE9U10Average() {
        return mE9U10Average;
    }

    public void setE9U10Average(String e9U10Average) {
        mE9U10Average = e9U10Average;
    }

    public String getE9I10Average() {
        return mE9I10Average;
    }

    public void setE9I10Average(String e9I10Average) {
        mE9I10Average = e9I10Average;
    }

    public String getE9U09A() {
        return mE9U09A;
    }

    public void setE9U09A(String e9U09A) {
        mE9U09A = e9U09A;
    }

    public String getE9I09A() {
        return mE9I09A;
    }

    public void setE9I09A(String e9I09A) {
        mE9I09A = e9I09A;
    }

    public String getE9U09B() {
        return mE9U09B;
    }

    public void setE9U09B(String e9U09B) {
        mE9U09B = e9U09B;
    }

    public String getE9I09B() {
        return mE9I09B;
    }

    public void setE9I09B(String e9I09B) {
        mE9I09B = e9I09B;
    }

    public String getE9P09() {
        return mE9P09;
    }

    public void setE9P09(String e9P09) {
        mE9P09 = e9P09;
    }

    public String getE9Cos09() {
        return mE9Cos09;
    }

    public void setE9Cos09(String e9Cos09) {
        mE9Cos09 = e9Cos09;
    }

    public String getE9TempAmbient09() {
        return mE9TempAmbient09;
    }

    public void setE9TempAmbient09(String e9TempAmbient09) {
        mE9TempAmbient09 = e9TempAmbient09;
    }

    public String getE9TempEngine09() {
        return mE9TempEngine09;
    }

    public void setE9TempEngine09(String e9TempEngine09) {
        mE9TempEngine09 = e9TempEngine09;
    }

    public String getE9T09() {
        return mE9T09;
    }

    public void setE9T09(String e9T09) {
        mE9T09 = e9T09;
    }

    public String getE9U09C() {
        return mE9U09C;
    }

    public void setE9U09C(String e9U09C) {
        mE9U09C = e9U09C;
    }

    public String getE9I09C() {
        return mE9I09C;
    }

    public void setE9I09C(String e9I09C) {
        mE9I09C = e9I09C;
    }

    public String getE9U09Average() {
        return mE9U09Average;
    }

    public void setE9U09Average(String e9U09Average) {
        mE9U09Average = e9U09Average;
    }

    public String getE9I09Average() {
        return mE9I09Average;
    }

    public void setE9I09Average(String e9I09Average) {
        mE9I09Average = e9I09Average;
    }

    public String getE9U08A() {
        return mE9U08A;
    }

    public void setE9U08A(String e9U08A) {
        mE9U08A = e9U08A;
    }

    public String getE9I08A() {
        return mE9I08A;
    }

    public void setE9I08A(String e9I08A) {
        mE9I08A = e9I08A;
    }

    public String getE9U08B() {
        return mE9U08B;
    }

    public void setE9U08B(String e9U08B) {
        mE9U08B = e9U08B;
    }

    public String getE9I08B() {
        return mE9I08B;
    }

    public void setE9I08B(String e9I08B) {
        mE9I08B = e9I08B;
    }

    public String getE9P08() {
        return mE9P08;
    }

    public void setE9P08(String e9P08) {
        mE9P08 = e9P08;
    }

    public String getE9Cos08() {
        return mE9Cos08;
    }

    public void setE9Cos08(String e9Cos08) {
        mE9Cos08 = e9Cos08;
    }

    public String getE9TempAmbient08() {
        return mE9TempAmbient08;
    }

    public void setE9TempAmbient08(String e9TempAmbient08) {
        mE9TempAmbient08 = e9TempAmbient08;
    }

    public String getE9TempEngine08() {
        return mE9TempEngine08;
    }

    public void setE9TempEngine08(String e9TempEngine08) {
        mE9TempEngine08 = e9TempEngine08;
    }

    public String getE9T08() {
        return mE9T08;
    }

    public void setE9T08(String e9T08) {
        mE9T08 = e9T08;
    }

    public String getE9U08C() {
        return mE9U08C;
    }

    public void setE9U08C(String e9U08C) {
        mE9U08C = e9U08C;
    }

    public String getE9I08C() {
        return mE9I08C;
    }

    public void setE9I08C(String e9I08C) {
        mE9I08C = e9I08C;
    }

    public String getE9U08Average() {
        return mE9U08Average;
    }

    public void setE9U08Average(String e9U08Average) {
        mE9U08Average = e9U08Average;
    }

    public String getE9I08Average() {
        return mE9I08Average;
    }

    public void setE9I08Average(String e9I08Average) {
        mE9I08Average = e9I08Average;
    }

    public String getE9U07A() {
        return mE9U07A;
    }

    public void setE9U07A(String e9U07A) {
        mE9U07A = e9U07A;
    }

    public String getE9I07A() {
        return mE9I07A;
    }

    public void setE9I07A(String e9I07A) {
        mE9I07A = e9I07A;
    }

    public String getE9U07B() {
        return mE9U07B;
    }

    public void setE9U07B(String e9U07B) {
        mE9U07B = e9U07B;
    }

    public String getE9I07B() {
        return mE9I07B;
    }

    public void setE9I07B(String e9I07B) {
        mE9I07B = e9I07B;
    }

    public String getE9P07() {
        return mE9P07;
    }

    public void setE9P07(String e9P07) {
        mE9P07 = e9P07;
    }

    public String getE9Cos07() {
        return mE9Cos07;
    }

    public void setE9Cos07(String e9Cos07) {
        mE9Cos07 = e9Cos07;
    }

    public String getE9TempAmbient07() {
        return mE9TempAmbient07;
    }

    public void setE9TempAmbient07(String e9TempAmbient07) {
        mE9TempAmbient07 = e9TempAmbient07;
    }

    public String getE9TempEngine07() {
        return mE9TempEngine07;
    }

    public void setE9TempEngine07(String e9TempEngine07) {
        mE9TempEngine07 = e9TempEngine07;
    }

    public String getE9T07() {
        return mE9T07;
    }

    public void setE9T07(String e9T07) {
        mE9T07 = e9T07;
    }

    public String getE9U07C() {
        return mE9U07C;
    }

    public void setE9U07C(String e9U07C) {
        mE9U07C = e9U07C;
    }

    public String getE9I07C() {
        return mE9I07C;
    }

    public void setE9I07C(String e9I07C) {
        mE9I07C = e9I07C;
    }

    public String getE9U07Average() {
        return mE9U07Average;
    }

    public void setE9U07Average(String e9U07Average) {
        mE9U07Average = e9U07Average;
    }

    public String getE9I07Average() {
        return mE9I07Average;
    }

    public void setE9I07Average(String e9I07Average) {
        mE9I07Average = e9I07Average;
    }

    public String getE9U06A() {
        return mE9U06A;
    }

    public void setE9U06A(String e9U06A) {
        mE9U06A = e9U06A;
    }

    public String getE9I06A() {
        return mE9I06A;
    }

    public void setE9I06A(String e9I06A) {
        mE9I06A = e9I06A;
    }

    public String getE9U06B() {
        return mE9U06B;
    }

    public void setE9U06B(String e9U06B) {
        mE9U06B = e9U06B;
    }

    public String getE9I06B() {
        return mE9I06B;
    }

    public void setE9I06B(String e9I06B) {
        mE9I06B = e9I06B;
    }

    public String getE9P06() {
        return mE9P06;
    }

    public void setE9P06(String e9P06) {
        mE9P06 = e9P06;
    }

    public String getE9Cos06() {
        return mE9Cos06;
    }

    public void setE9Cos06(String e9Cos06) {
        mE9Cos06 = e9Cos06;
    }

    public String getE9TempAmbient06() {
        return mE9TempAmbient06;
    }

    public void setE9TempAmbient06(String e9TempAmbient06) {
        mE9TempAmbient06 = e9TempAmbient06;
    }

    public String getE9TempEngine06() {
        return mE9TempEngine06;
    }

    public void setE9TempEngine06(String e9TempEngine06) {
        mE9TempEngine06 = e9TempEngine06;
    }

    public String getE9T06() {
        return mE9T06;
    }

    public void setE9T06(String e9T06) {
        mE9T06 = e9T06;
    }

    public String getE9U06C() {
        return mE9U06C;
    }

    public void setE9U06C(String e9U06C) {
        mE9U06C = e9U06C;
    }

    public String getE9I06C() {
        return mE9I06C;
    }

    public void setE9I06C(String e9I06C) {
        mE9I06C = e9I06C;
    }

    public String getE9U06Average() {
        return mE9U06Average;
    }

    public void setE9U06Average(String e9U06Average) {
        mE9U06Average = e9U06Average;
    }

    public String getE9I06Average() {
        return mE9I06Average;
    }

    public void setE9I06Average(String e9I06Average) {
        mE9I06Average = e9I06Average;
    }


    public String getE10IA() {
        return mE10IA;
    }

    public void setE10IA(String e10IA) {
        mE10IA = e10IA;
    }

    public String getE10UA() {
        return mE10UA;
    }

    public void setE10UA(String e10UA) {
        mE10UA = e10UA;
    }

    public String getE10IB() {
        return mE10IB;
    }

    public void setE10IB(String e10IB) {
        mE10IB = e10IB;
    }

    public String getE10UB() {
        return mE10UB;
    }

    public void setE10UB(String e10UB) {
        mE10UB = e10UB;
    }

    public String getE10S() {
        return mE10S;
    }

    public void setE10S(String e10S) {
        mE10S = e10S;
    }

    public String getE10P1() {
        return mE10P1;
    }

    public void setE10P1(String e10P1) {
        mE10P1 = e10P1;
    }

    public String getE10Cos() {
        return mE10Cos;
    }

    public void setE10Cos(String e10Cos) {
        mE10Cos = e10Cos;
    }

    public String getE10M() {
        return mE10M;
    }

    public void setE10M(String e10M) {
        mE10M = e10M;
    }

    public String getE10V() {
        return mE10V;
    }

    public void setE10V(String e10V) {
        mE10V = e10V;
    }

    public String getE10P2() {
        return mE10P2;
    }

    public void setE10P2(String e10P2) {
        mE10P2 = e10P2;
    }

    public String getE10Nu() {
        return mE10Nu;
    }

    public void setE10Nu(String e10Nu) {
        mE10Nu = e10Nu;
    }

    public String getE10TempAmbient() {
        return mE10TempAmbient;
    }

    public void setE10TempAmbient(String e10TempAmbient) {
        mE10TempAmbient = e10TempAmbient;
    }

    public String getE10TempEngine() {
        return mE10TempEngine;
    }

    public void setE10TempEngine(String e10TempEngine) {
        mE10TempEngine = e10TempEngine;
    }

    public String getE10Sk() {
        return mE10Sk;
    }

    public void setE10Sk(String e10Sk) {
        mE10Sk = e10Sk;
    }

    public String getE10T() {
        return mE10T;
    }

    public void setE10T(String e10T) {
        mE10T = e10T;
    }

    public String getE10IC() {
        return mE10IC;
    }

    public void setE10IC(String e10IC) {
        mE10IC = e10IC;
    }

    public String getE10UC() {
        return mE10UC;
    }

    public void setE10UC(String e10UC) {
        mE10UC = e10UC;
    }

    public String getE10IAverage() {
        return mE10IAverage;
    }

    public void setE10IAverage(String e10IAverage) {
        mE10IAverage = e10IAverage;
    }

    public String getE10UAverage() {
        return mE10UAverage;
    }

    public void setE10UAverage(String e10UAverage) {
        mE10UAverage = e10UAverage;
    }

    public String getE10SAverage() {
        return mE10SAverage;
    }

    public void setE10SAverage(String e10SAverage) {
        mE10SAverage = e10SAverage;
    }

    public String getE10P1Average() {
        return mE10P1Average;
    }

    public void setE10P1Average(String e10P1Average) {
        mE10P1Average = e10P1Average;
    }

    public String getE10CosAverage() {
        return mE10CosAverage;
    }

    public void setE10CosAverage(String e10CosAverage) {
        mE10CosAverage = e10CosAverage;
    }

    public String getE10MAverage() {
        return mE10MAverage;
    }

    public void setE10MAverage(String e10MAverage) {
        mE10MAverage = e10MAverage;
    }

    public String getE10VAverage() {
        return mE10VAverage;
    }

    public void setE10VAverage(String e10VAverage) {
        mE10VAverage = e10VAverage;
    }

    public String getE10P2Average() {
        return mE10P2Average;
    }

    public void setE10P2Average(String e10P2Average) {
        mE10P2Average = e10P2Average;
    }

    public String getE10NuAverage() {
        return mE10NuAverage;
    }

    public void setE10NuAverage(String e10NuAverage) {
        mE10NuAverage = e10NuAverage;
    }

    public String getE10TempEngineAverage() {
        return mE10TempEngineAverage;
    }

    public void setE10TempEngineAverage(String e10TempEngineAverage) {
        mE10TempEngineAverage = e10TempEngineAverage;
    }

    public String getE10SkAverage() {
        return mE10SkAverage;
    }

    public void setE10SkAverage(String e10SkAverage) {
        mE10SkAverage = e10SkAverage;
    }

    public String getE10ISpecified() {
        return mE10ISpecified;
    }

    public void setE10ISpecified(String e10ISpecified) {
        mE10ISpecified = e10ISpecified;
    }

    public String getE10USpecified() {
        return mE10USpecified;
    }

    public void setE10USpecified(String e10USpecified) {
        mE10USpecified = e10USpecified;
    }

    public String getE10SSpecified() {
        return mE10SSpecified;
    }

    public void setE10SSpecified(String e10SSpecified) {
        mE10SSpecified = e10SSpecified;
    }

    public String getE10P1Specified() {
        return mE10P1Specified;
    }

    public void setE10P1Specified(String e10P1Specified) {
        mE10P1Specified = e10P1Specified;
    }

    public String getE10CosSpecified() {
        return mE10CosSpecified;
    }

    public void setE10CosSpecified(String e10CosSpecified) {
        mE10CosSpecified = e10CosSpecified;
    }

    public String getE10MSpecified() {
        return mE10MSpecified;
    }

    public void setE10MSpecified(String e10MSpecified) {
        mE10MSpecified = e10MSpecified;
    }

    public String getE10VSpecified() {
        return mE10VSpecified;
    }

    public void setE10VSpecified(String e10VSpecified) {
        mE10VSpecified = e10VSpecified;
    }

    public String getE10P2Specified() {
        return mE10P2Specified;
    }

    public void setE10P2Specified(String e10P2Specified) {
        mE10P2Specified = e10P2Specified;
    }

    public String getE10NuSpecified() {
        return mE10NuSpecified;
    }

    public void setE10NuSpecified(String e10NuSpecified) {
        mE10NuSpecified = e10NuSpecified;
    }

    public String getE10TempEngineSpecified() {
        return mE10TempEngineSpecified;
    }

    public void setE10TempEngineSpecified(String e10TempEngineSpecified) {
        mE10TempEngineSpecified = e10TempEngineSpecified;
    }

    public String getE10SkSpecified() {
        return mE10SkSpecified;
    }

    public void setE10SkSpecified(String e10SkSpecified) {
        mE10SkSpecified = e10SkSpecified;
    }

    public String getE10TSpecified() {
        return mE10TSpecified;
    }

    public void setE10TSpecified(String e10TSpecified) {
        mE10TSpecified = e10TSpecified;
    }


    public String getE11UR() {
        return mE11UR;
    }

    public void setE11UR(String e11UR) {
        mE11UR = e11UR;
    }

    public String getE11R15() {
        return mE11R15;
    }

    public void setE11R15(String e11R15) {
        mE11R15 = e11R15;
    }

    public String getE11R60() {
        return mE11R60;
    }

    public void setE11R60(String e11R60) {
        mE11R60 = e11R60;
    }

    public String getE11K() {
        return mE11K;
    }

    public void setE11K(String e11K) {
        mE11K = e11K;
    }

    public String getE11Temp() {
        return mE11Temp;
    }

    public void setE11Temp(String e11Temp) {
        mE11Temp = e11Temp;
    }

    public String getE11Result() {
        return mE11Result;
    }

    public void setE11Result(String e11Result) {
        mE11Result = e11Result;
    }


    public String getE12IA() {
        return mE12IA;
    }

    public void setE12IA(String e12IA) {
        mE12IA = e12IA;
    }

    public String getE12UA() {
        return mE12UA;
    }

    public void setE12UA(String e12UA) {
        mE12UA = e12UA;
    }

    public String getE12IB() {
        return mE12IB;
    }

    public void setE12IB(String e12IB) {
        mE12IB = e12IB;
    }

    public String getE12UB() {
        return mE12UB;
    }

    public void setE12UB(String e12UB) {
        mE12UB = e12UB;
    }

    public String getE12S() {
        return mE12S;
    }

    public void setE12S(String e12S) {
        mE12S = e12S;
    }

    public String getE12P1() {
        return mE12P1;
    }

    public void setE12P1(String e12P1) {
        mE12P1 = e12P1;
    }

    public String getE12Cos() {
        return mE12Cos;
    }

    public void setE12Cos(String e12Cos) {
        mE12Cos = e12Cos;
    }

    public String getE12M() {
        return mE12M;
    }

    public void setE12M(String e12M) {
        mE12M = e12M;
    }

    public String getE12V() {
        return mE12V;
    }

    public void setE12V(String e12V) {
        mE12V = e12V;
    }

    public String getE12P2() {
        return mE12P2;
    }

    public void setE12P2(String e12P2) {
        mE12P2 = e12P2;
    }

    public String getE12Nu() {
        return mE12Nu;
    }

    public void setE12Nu(String e12Nu) {
        mE12Nu = e12Nu;
    }

    public String getE12TempAmbient() {
        return mE12TempAmbient;
    }

    public void setE12TempAmbient(String e12TempAmbient) {
        mE12TempAmbient = e12TempAmbient;
    }

    public String getE12TempEngine() {
        return mE12TempEngine;
    }

    public void setE12TempEngine(String e12TempEngine) {
        mE12TempEngine = e12TempEngine;
    }

    public String getE12Sk() {
        return mE12Sk;
    }

    public void setE12Sk(String e12Sk) {
        mE12Sk = e12Sk;
    }

    public String getE12T() {
        return mE12T;
    }

    public void setE12T(String e12T) {
        mE12T = e12T;
    }

    public String getE12IC() {
        return mE12IC;
    }

    public void setE12IC(String e12IC) {
        mE12IC = e12IC;
    }

    public String getE12UC() {
        return mE12UC;
    }

    public void setE12UC(String e12UC) {
        mE12UC = e12UC;
    }

    public String getE12IAverage() {
        return mE12IAverage;
    }

    public void setE12IAverage(String e12IAverage) {
        mE12IAverage = e12IAverage;
    }

    public String getE12UAverage() {
        return mE12UAverage;
    }

    public void setE12UAverage(String e12UAverage) {
        mE12UAverage = e12UAverage;
    }

    public String getE12SAverage() {
        return mE12SAverage;
    }

    public void setE12SAverage(String e12SAverage) {
        mE12SAverage = e12SAverage;
    }

    public String getE12P1Average() {
        return mE12P1Average;
    }

    public void setE12P1Average(String e12P1Average) {
        mE12P1Average = e12P1Average;
    }

    public String getE12CosAverage() {
        return mE12CosAverage;
    }

    public void setE12CosAverage(String e12CosAverage) {
        mE12CosAverage = e12CosAverage;
    }

    public String getE12MAverage() {
        return mE12MAverage;
    }

    public void setE12MAverage(String e12MAverage) {
        mE12MAverage = e12MAverage;
    }

    public String getE12VAverage() {
        return mE12VAverage;
    }

    public void setE12VAverage(String e12VAverage) {
        mE12VAverage = e12VAverage;
    }

    public String getE12P2Average() {
        return mE12P2Average;
    }

    public void setE12P2Average(String e12P2Average) {
        mE12P2Average = e12P2Average;
    }

    public String getE12NuAverage() {
        return mE12NuAverage;
    }

    public void setE12NuAverage(String e12NuAverage) {
        mE12NuAverage = e12NuAverage;
    }

    public String getE12TempEngineAverage() {
        return mE12TempEngineAverage;
    }

    public void setE12TempEngineAverage(String e12TempEngineAverage) {
        mE12TempEngineAverage = e12TempEngineAverage;
    }

    public String getE12SkAverage() {
        return mE12SkAverage;
    }

    public void setE12SkAverage(String e12SkAverage) {
        mE12SkAverage = e12SkAverage;
    }

    public String getE12ISpecified() {
        return mE12ISpecified;
    }

    public void setE12ISpecified(String e12ISpecified) {
        mE12ISpecified = e12ISpecified;
    }

    public String getE12USpecified() {
        return mE12USpecified;
    }

    public void setE12USpecified(String e12USpecified) {
        mE12USpecified = e12USpecified;
    }

    public String getE12SSpecified() {
        return mE12SSpecified;
    }

    public void setE12SSpecified(String e12SSpecified) {
        mE12SSpecified = e12SSpecified;
    }

    public String getE12P1Specified() {
        return mE12P1Specified;
    }

    public void setE12P1Specified(String e12P1Specified) {
        mE12P1Specified = e12P1Specified;
    }

    public String getE12CosSpecified() {
        return mE12CosSpecified;
    }

    public void setE12CosSpecified(String e12CosSpecified) {
        mE12CosSpecified = e12CosSpecified;
    }

    public String getE12MSpecified() {
        return mE12MSpecified;
    }

    public void setE12MSpecified(String e12MSpecified) {
        mE12MSpecified = e12MSpecified;
    }

    public String getE12VSpecified() {
        return mE12VSpecified;
    }

    public void setE12VSpecified(String e12VSpecified) {
        mE12VSpecified = e12VSpecified;
    }

    public String getE12P2Specified() {
        return mE12P2Specified;
    }

    public void setE12P2Specified(String e12P2Specified) {
        mE12P2Specified = e12P2Specified;
    }

    public String getE12NuSpecified() {
        return mE12NuSpecified;
    }

    public void setE12NuSpecified(String e12NuSpecified) {
        mE12NuSpecified = e12NuSpecified;
    }

    public String getE12TempEngineSpecified() {
        return mE12TempEngineSpecified;
    }

    public void setE12TempEngineSpecified(String e12TempEngineSpecified) {
        mE12TempEngineSpecified = e12TempEngineSpecified;
    }

    public String getE12SkSpecified() {
        return mE12SkSpecified;
    }

    public void setE12SkSpecified(String e12SkSpecified) {
        mE12SkSpecified = e12SkSpecified;
    }

    public String getE12TSpecified() {
        return mE12TSpecified;
    }

    public void setE12TSpecified(String e12TSpecified) {
        mE12TSpecified = e12TSpecified;
    }


    public String getE13U08A() {
        return mE13U08A;
    }

    public void setE13U08A(String e13U08A) {
        mE13U08A = e13U08A;
    }

    public String getE13I08A() {
        return mE13I08A;
    }

    public void setE13I08A(String e13I08A) {
        mE13I08A = e13I08A;
    }

    public String getE13U08B() {
        return mE13U08B;
    }

    public void setE13U08B(String e13U08B) {
        mE13U08B = e13U08B;
    }

    public String getE13I08B() {
        return mE13I08B;
    }

    public void setE13I08B(String e13I08B) {
        mE13I08B = e13I08B;
    }

    public String getE13S08() {
        return mE13S08;
    }

    public void setE13S08(String e13S08) {
        mE13S08 = e13S08;
    }

    public String getE13P08() {
        return mE13P08;
    }

    public void setE13P08(String e13P08) {
        mE13P08 = e13P08;
    }

    public String getE13V08() {
        return mE13V08;
    }

    public void setE13V08(String e13V08) {
        mE13V08 = e13V08;
    }

    public String getE13M08() {
        return mE13M08;
    }

    public void setE13M08(String e13M08) {
        mE13M08 = e13M08;
    }

    public String getE13F08() {
        return mE13F08;
    }

    public void setE13F08(String e13F08) {
        mE13F08 = e13F08;
    }

    public String getE13Temp08() {
        return mE13Temp08;
    }

    public void setE13Temp08(String e13Temp08) {
        mE13Temp08 = e13Temp08;
    }

    public String getE13T08() {
        return mE13T08;
    }

    public void setE13T08(String e13T08) {
        mE13T08 = e13T08;
    }

    public String getE13U08C() {
        return mE13U08C;
    }

    public void setE13U08C(String e13U08C) {
        mE13U08C = e13U08C;
    }

    public String getE13I08C() {
        return mE13I08C;
    }

    public void setE13I08C(String e13I08C) {
        mE13I08C = e13I08C;
    }

    public String getE13U08Average() {
        return mE13U08Average;
    }

    public void setE13U08Average(String e13U08Average) {
        mE13U08Average = e13U08Average;
    }

    public String getE13I08Average() {
        return mE13I08Average;
    }

    public void setE13I08Average(String e13I08Average) {
        mE13I08Average = e13I08Average;
    }

    public String getE13U11A() {
        return mE13U11A;
    }

    public void setE13U11A(String e13U11A) {
        mE13U11A = e13U11A;
    }

    public String getE13I11A() {
        return mE13I11A;
    }

    public void setE13I11A(String e13I11A) {
        mE13I11A = e13I11A;
    }

    public String getE13U11B() {
        return mE13U11B;
    }

    public void setE13U11B(String e13U11B) {
        mE13U11B = e13U11B;
    }

    public String getE13I11B() {
        return mE13I11B;
    }

    public void setE13I11B(String e13I11B) {
        mE13I11B = e13I11B;
    }

    public String getE13S11() {
        return mE13S11;
    }

    public void setE13S11(String e13S11) {
        mE13S11 = e13S11;
    }

    public String getE13P11() {
        return mE13P11;
    }

    public void setE13P11(String e13P11) {
        mE13P11 = e13P11;
    }

    public String getE13V11() {
        return mE13V11;
    }

    public void setE13V11(String e13V11) {
        mE13V11 = e13V11;
    }

    public String getE13M11() {
        return mE13M11;
    }

    public void setE13M11(String e13M11) {
        mE13M11 = e13M11;
    }

    public String getE13F11() {
        return mE13F11;
    }

    public void setE13F11(String e13F11) {
        mE13F11 = e13F11;
    }

    public String getE13Temp11() {
        return mE13Temp11;
    }

    public void setE13Temp11(String e13Temp11) {
        mE13Temp11 = e13Temp11;
    }

    public String getE13T11() {
        return mE13T11;
    }

    public void setE13T11(String e13T11) {
        mE13T11 = e13T11;
    }

    public String getE13U11C() {
        return mE13U11C;
    }

    public void setE13U11C(String e13U11C) {
        mE13U11C = e13U11C;
    }

    public String getE13I11C() {
        return mE13I11C;
    }

    public void setE13I11C(String e13I11C) {
        mE13I11C = e13I11C;
    }

    public String getE13U11Average() {
        return mE13U11Average;
    }

    public void setE13U11Average(String e13U11Average) {
        mE13U11Average = e13U11Average;
    }

    public String getE13I11Average() {
        return mE13I11Average;
    }

    public void setE13I11Average(String e13I11Average) {
        mE13I11Average = e13I11Average;
    }


    public String getE14M() {
        return mE14M;
    }

    public void setE14M(String e14M) {
        mE14M = e14M;
    }

    public String getE14V() {
        return mE14V;
    }

    public void setE14V(String e14V) {
        mE14V = e14V;
    }


    public String getE15I() {
        return mE15I;
    }

    public void setE15I(String e15I) {
        mE15I = e15I;
    }

    public String getE15M() {
        return mE15M;
    }

    public void setE15M(String e15M) {
        mE15M = e15M;
    }




    public String getE16Noise() {
        return mE16Noise;
    }

    public void setE16Noise(String e16Noise) {
        mE16Noise = e16Noise;
    }

    public String getE16X1() {
        return mE16X1;
    }

    public void setE16X1(String e16X1) {
        mE16X1 = e16X1;
    }

    public String getE16Y1() {
        return mE16Y1;
    }

    public void setE16Y1(String e16Y1) {
        mE16Y1 = e16Y1;
    }

    public String getE16Z1() {
        return mE16Z1;
    }

    public void setE16Z1(String e16Z1) {
        mE16Z1 = e16Z1;
    }

    public String getE16X2() {
        return mE16X2;
    }

    public void setE16X2(String e16X2) {
        mE16X2 = e16X2;
    }

    public String getE16Y2() {
        return mE16Y2;
    }

    public void setE16Y2(String e16Y2) {
        mE16Y2 = e16Y2;
    }

    public String getE16Z2() {
        return mE16Z2;
    }

    public void setE16Z2(String e16Z2) {
        mE16Z2 = e16Z2;
    }

    public String getE17Ab() {
        return mE17Ab;
    }

    public void setE17Ab(String e17Ab) {
        mE17Ab = e17Ab;
    }

    public String getE17Bc() {
        return mE17Bc;
    }

    public void setE17Bc(String e17Bc) {
        mE17Bc = e17Bc;
    }

    public String getE17Ac() {
        return mE17Ac;
    }

    public void setE17Ac(String e17Ac) {
        mE17Ac = e17Ac;
    }

    public String getE17AverageR() {
        return mE17AverageR;
    }

    public void setE17AverageR(String e17AverageR) {
        mE17AverageR = e17AverageR;
    }

    public String getE17Temp() {
        return mE17Temp;
    }

    public void setE17Temp(String e17Temp) {
        mE17Temp = e17Temp;
    }

    public String getE17Result() {
        return mE17Result;
    }

    public void setE17Result(String e17Result) {
        mE17Result = e17Result;
    }

    public String getE17AverageRSpecified() {
        return mE17AverageRSpecified;
    }

    public void setE17AverageRSpecified(String e17AverageRSpecified) {
        mE17AverageRSpecified = e17AverageRSpecified;
    }
}
