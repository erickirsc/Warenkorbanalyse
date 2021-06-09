package hsel.softsmart.warenkorbanalyse.service;

import hsel.softsmart.warenkorbanalyse.model.Result;
import hsel.softsmart.warenkorbanalyse.repository.ResultRepository;
import hsel.softsmart.warenkorbanalyse.weka.WekaBspStud;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AnalysisServiceImplementation implements AnalysisService {

    private final WekaBspStud weka;
    private final ResultRepository resultRepository;

    @Autowired
    public AnalysisServiceImplementation(WekaBspStud weka, ResultRepository resultRepository) {
        this.weka = weka;
        this.resultRepository = resultRepository;
    }

    @SneakyThrows
    @Override
    public Instances loadCSVData(File file) {
        Instances data;

        CSVLoader loader = new CSVLoader();
        loader.setSource(file);
        data = loader.getDataSet();
        data = filter(data);

        return data;
    }

    @SneakyThrows
    @Override
    public Instances loadArffData(Instances csvData) {
        Instances arffData;

        File file = File.createTempFile("warenkorbanalyse", ".tmp");

        ArffSaver saver = new ArffSaver();
        saver.setInstances(csvData);
        saver.setFile(file);
        saver.writeBatch();

        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setSource(file);
        arffData = arffLoader.getDataSet();

        file.delete();

        return arffData;
    }

    @SneakyThrows
    @Override
    public Result processData(Instances csvData, Instances arffData) {
        String topDay = weka.findMaximum(arffData, 5);
        String topTime = weka.findMaximum(arffData, 6);
        String[] aprioris = weka.makeApriori(productData(csvData));
        String topProduct = "Bier";
        String flopProduct = "Konserven";

        Result result = new Result();
        result.setTopDay(topDay);
        result.setTopTime(topTime);
        result.setTopProduct(topProduct);
        result.setFlopProduct(flopProduct);
        result.setDate(new Date());

        for (String apriori : aprioris) {
            String[] split = apriori.split("==>");
            result.addAprioriValue(split[1].trim(), split[0].trim());
        }

        return result;
    }

    @Override
    @Transactional
    public void saveResult(Result result) {
        if (resultRepository.count() >= 5) {
            Optional<Result> oldestResult = resultRepository.findTopByOrderById();
            oldestResult.ifPresent(resultRepository::delete);
        }
        resultRepository.save(result);
    }

    @Override
    public Result findLastResult() {
        return resultRepository.findTopByOrderByIdDesc().orElse(new Result());
    }

    @Override
    public Result findResultById(Long id) {
        return resultRepository.findById(id).orElse(new Result());
    }

    @Override
    public List<Result> resultHistory() {
        return resultRepository.findAll();
    }

    @SneakyThrows
    private Instances filter(Instances data) {
        NumericCleaner nc = new NumericCleaner();
        nc.setMinThreshold(1.0);
        nc.setMinDefault(Double.NaN);
        nc.setInputFormat(data);
        data = Filter.useFilter(data, nc);
        return data;
    }

    private Instances productData(Instances data) {
        Instances productData = new Instances(data);

        for (int i = 0; i < 11; i++) {
            productData.deleteAttributeAt(0);
        }

        return productData;
    }
}
