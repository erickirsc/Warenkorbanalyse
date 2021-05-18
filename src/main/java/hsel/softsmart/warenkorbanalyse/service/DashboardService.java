package hsel.softsmart.warenkorbanalyse.service;

import hsel.softsmart.warenkorbanalyse.model.Result;
import weka.core.Instances;

import java.io.File;

public interface DashboardService {
    Instances loadCSVData(File file);
    Instances loadArffData(Instances csvData);
    Result processData(Instances csvData, Instances arffData);
    void saveResult(Result result);
    Result findLastResult();
}
