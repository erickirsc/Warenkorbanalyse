package hsel.softsmart.warenkorbanalyse.service;

import hsel.softsmart.warenkorbanalyse.model.CustomerCsvEntry;
import hsel.softsmart.warenkorbanalyse.model.Result;
import weka.core.Instances;

import java.io.File;
import java.util.List;

public interface AnalysisService {
    Instances loadCSVData(File file);

    Instances loadArffData(Instances csvData);

    Result processData(List<CustomerCsvEntry> csvEntries, Instances csvData, Instances arffData);

    void saveResult(Result result);

    Result findLastResult();

    List<Result> resultHistory();

    Result findResultById(Long id);
}
