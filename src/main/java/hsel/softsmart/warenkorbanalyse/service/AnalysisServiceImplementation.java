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
import java.util.stream.Collectors;

/**
 * Stellt die Businesslogik für Analysen zur Verfügung.
 */
@Service
public class AnalysisServiceImplementation implements AnalysisService {

    private final WekaBspStud weka;
    private final ResultRepository resultRepository;

    @Autowired
    public AnalysisServiceImplementation(WekaBspStud weka, ResultRepository resultRepository) {
        this.weka = weka;
        this.resultRepository = resultRepository;
    }

    /**
     * Konvertiert eine {@link File} in eine {@link Instances} im CSV Format.
     *
     * @param file Zu konvertierende Datei
     * @return Instance im CSV Format
     */
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

    /**
     * Konvertiert eine {@link File} in eine {@link Instances} im ARFF Format.
     *
     * @param csvData Zu konvertierende Datei im CSV-Format
     * @return Instance, im ARFF Format
     */
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

    /**
     * Verarbeitet die Daten und gibt das Ergebnis aus.
     *
     * Dabei werden im Ergebnis das umsatzstärkste und umsatzschwächste Produkt,
     * die umsatzstärkste Einkaufsuhrzeit, der umsatzstärkste Einkaufstag und
     * die Apriori-Ergebnisse gepeichert.
     *
     * @param csvEntries Liste mit allen CSV-Zeilen
     * @param csvData Instance im CSV Format
     * @param arffData Instance im ARFF Format
     * @return Result
     */
    @SneakyThrows
    @Override
    public Result processData(List<CustomerCsvEntry> csvEntries, Instances csvData, Instances arffData) {
        String topDayBySales = topDayBySales(csvEntries);
        String topTimeBySales = topTimeBySales(csvEntries);
        String[] aprioris = weka.makeApriori(productData(csvData));
        String topProductBySales = topProductBySales(csvEntries);
        String flopProductBySales = flopProductBySales(csvEntries);

        Result result = new Result();
        result.setTopDay(topDayBySales);
        result.setTopTime(topTimeBySales);
        result.setTopProduct(topProductBySales);
        result.setFlopProduct(flopProductBySales);
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

    /**
     * Speichert das Ergebnis in der Datenbank.
     *
     * Dabei werden ab fünf Ergebnissen immer das älteste Ergebnis gelöscht.
     *
     * @param result Das gespeichert werden soll
     */
    @Override
    @Transactional
    public void saveResult(Result result) {
        if (resultRepository.count() >= 5) {
            Optional<Result> oldestResult = resultRepository.findTopByOrderById();
            oldestResult.ifPresent(resultRepository::delete);
        }
        resultRepository.save(result);
    }

    /**
     * Gibt das letzte gespeicherte Ergebnis aus.
     *
     * @return Result oder leeres Result, wenn noch kein Result in der Datenbank liegt
     */
    @Override
    public Result findLastResult() {
        return resultRepository.findTopByOrderByIdDesc().orElse(new Result());
    }

    /**
     * Gibt das Ergebnis aus, welches mit der ID gesucht wird
     *
     * @param id Zu findende Result-ID
     * @return Result oder leeres Result, wenn Result nicht gefunden wurde
     */
    @Override
    public Result findResultById(Long id) {
        return resultRepository.findById(id).orElse(new Result());
    }

    /**
     * Gibt alle Ergebnisse aus der Datenbank aus.
     *
     * @return Liste von allen Ergebnissen in der Datenbank
     */
    @Override
    public List<Result> resultHistory() {
        return resultRepository.findAll();
    }

    /**
     * Alle 0 in der Datei, werden durch ein Fragezeichen ersetzt.
     *
     * @param data Die gefiltert werden müssen
     * @return Gefilterte Instance
     */
    @SneakyThrows
    private Instances filter(Instances data) {
        NumericCleaner nc = new NumericCleaner();
        nc.setMinThreshold(1.0);
        nc.setMinDefault(Double.NaN);
        nc.setInputFormat(data);
        data = Filter.useFilter(data, nc);
        return data;
    }

    /**
     * Filtert die Produkte aus den Daten.
     *
     * @param data Die gefiltert werden müssen
     * @return Gefilterte Instance mit den Produkten
     */
    private Instances productData(Instances data) {
        Instances productData = new Instances(data);

        for (int i = 0; i < 11; i++) {
            productData.deleteAttributeAt(0);
        }

        return productData;
    }

    /**
     * Sucht nach dem umsatzstärksten Produkt.
     *
     * @param csvEntries Liste mit allen CSV-Einträgen
     * @return Umsatzstärkste Produkt
     */
    private String topProductBySales(List<CustomerCsvEntry> csvEntries) {
        Map<String, Integer> totalOfProductSales = totalOfProductSales(csvEntries);
        return Collections.max(totalOfProductSales.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    /**
     * Sucht nach dem umsatzschwächsten Produkt.
     *
     * @param csvEntries Liste mit allen CSV-Einträgen
     * @return Umsatzschwächste Produkt
     */
    private String flopProductBySales(List<CustomerCsvEntry> csvEntries) {
        Map<String, Integer> totalOfProductSales = totalOfProductSales(csvEntries);
        return Collections.min(totalOfProductSales.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    /**
     * Berechnet von jedem Produkt die Umsatzsumme.
     *
     * @param csvEntries Liste mit allen CSV-Einträgen
     * @return Die Umsatzsumme von jedem Produkt
     */
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

    /**
     * Sucht nach dem umsatzstärksten Wochentag.
     *
     * @param csvEntries Liste mit allen CSV-Einträgen
     * @return Umsatzstärkster Wochentag
     */
    private String topDayBySales(List<CustomerCsvEntry> csvEntries) {
        Map<String, Integer> totalOfPurchaseAmountByShoppingDay = totalOfPurchaseAmountByShoppingDay(csvEntries);
        return Collections.max(totalOfPurchaseAmountByShoppingDay.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    /**
     * Berechnet die Umsatzsummen der jeweiligen Wochentagen.
     *
     * @param csvEntries Liste mit allen CSV-Einträgen
     * @return Umsatzsummen der jeweiligen Wochentagen.
     */
    private Map<String, Integer> totalOfPurchaseAmountByShoppingDay(List<CustomerCsvEntry> csvEntries) {
        return csvEntries.stream().collect(Collectors.groupingBy(CustomerCsvEntry::getShoppingDay, Collectors.summingInt(CustomerCsvEntry::getPurchaseAmount)));
    }

    /**
     * Sucht nach der umsatzstärksten Uhrzeit.
     *
     * @param csvEntries Liste mit allen CSV-Einträgen
     * @return Umsatzstärkste Uhrzeit
     */
    private String topTimeBySales(List<CustomerCsvEntry> csvEntries) {
        Map<String, Integer> totalOfPurchaseAmountByShoppingTime = totalOfPurchaseAmountByShoppingTime(csvEntries);
        return Collections.max(totalOfPurchaseAmountByShoppingTime.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
    }

    /**
     * Berechnet die Umsatzsummen der jeweiligen Uhrzeiten.
     *
     * @param csvEntries Liste mit allen CSV-Einträgen
     * @return Umsatzsummen der jeweiligen Uhrzeiten.
     */
    private Map<String, Integer> totalOfPurchaseAmountByShoppingTime(List<CustomerCsvEntry> csvEntries) {
        return csvEntries.stream().collect(Collectors.groupingBy(CustomerCsvEntry::getShoppingTime, Collectors.summingInt(CustomerCsvEntry::getPurchaseAmount)));
    }
}
