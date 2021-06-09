package hsel.softsmart.warenkorbanalyse.service;

import hsel.softsmart.warenkorbanalyse.model.CustomerCsvEntry;
import hsel.softsmart.warenkorbanalyse.model.Result;
import hsel.softsmart.warenkorbanalyse.repository.ResultRepository;
import hsel.softsmart.warenkorbanalyse.weka.WekaBspStud;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericCleaner;

import java.io.File;
import java.util.*;

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
    public Result processData(List<CustomerCsvEntry> csvEntries, Instances csvData, Instances arffData) {
        String topDay = weka.findMaximum(arffData, 5);
        String topTime = weka.findMaximum(arffData, 6);
        String[] aprioris = weka.makeApriori(productData(csvData));
        String topProduct = topProductBySales(csvEntries);
        String flopProduct = flopProductBySales(csvEntries);

        Result result = new Result();
        result.setTopDay(topDay);
        result.setTopTime(topTime);
        result.setTopProduct(topProduct);
        result.setFlopProduct(flopProduct);
        result.setDate(new Date());

        Map<String, Set<String>> aprioriPositions = new HashMap<>();
        for (String apriori : aprioris) {
            String[] split = apriori.split("==>");
            String product = split[1].trim();
            String[] boughtTogetherWith = split[0].replace(" ", "").split(",");

            if (aprioriPositions.containsKey(product)) {
                aprioriPositions.get(product).addAll(Arrays.asList(boughtTogetherWith));
            } else {
                aprioriPositions.put(product, new HashSet<>(Arrays.asList(boughtTogetherWith)));
            }
        }

        for (Map.Entry<String, Set<String>> aprioriPosition : aprioriPositions.entrySet()) {
            result.addAprioriValue(aprioriPosition.getKey(), aprioriPosition.getValue());
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

    private String topProductBySales(List<CustomerCsvEntry> csvEntries) {
        Map<String, Integer> totalOfProductSales = totalOfProductSales(csvEntries);
        return Collections.max(totalOfProductSales.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    private String flopProductBySales(List<CustomerCsvEntry> csvEntries) {
        Map<String, Integer> totalOfProductSales = totalOfProductSales(csvEntries);
        return Collections.min(totalOfProductSales.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    private Map<String, Integer> totalOfProductSales(List<CustomerCsvEntry> csvEntries) {
        Map<String, Integer> results = new HashMap<>();

        results.put(
                "Fertiggerichte",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getReadyMeal)
                        .sum()
        );

        results.put(
                "Tiefkuehlware",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getFrozenGoods)
                        .sum()
        );

        results.put(
                "Milchprodukte",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getMilkProducts)
                        .sum()
        );

        results.put(
                "Backwaren",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getBakeryProducts)
                        .sum()
        );

        results.put(
                "ObstGemuese",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getFruitAndVegetables)
                        .sum()
        );

        results.put(
                "Spirituosen",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getSpirits)
                        .sum()
        );

        results.put(
                "Tiernahrung",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getPetFood)
                        .sum()
        );

        results.put(
                "Bier",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getBeer)
                        .sum()
        );

        results.put(
                "Frischfleisch",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getFreshMeat)
                        .sum()
        );

        results.put(
                "Drogerieartikel",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getDrugstoreItems)
                        .sum()
        );

        results.put(
                "Konserven",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getCannedGoods)
                        .sum()
        );

        results.put(
                "KaffeeTee",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getCoffeeAndTea)
                        .sum()
        );

        results.put(
                "Suessigkeiten",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getSweets)
                        .sum()
        );

        results.put(
                "Wurstwaren",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getSausages)
                        .sum()
        );

        results.put(
                "Schreibwaren",
                csvEntries
                        .stream()
                        .mapToInt(CustomerCsvEntry::getStationery)
                        .sum()
        );

        return results;
    }
}
