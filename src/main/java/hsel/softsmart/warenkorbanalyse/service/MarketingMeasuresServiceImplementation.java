package hsel.softsmart.warenkorbanalyse.service;

import hsel.softsmart.warenkorbanalyse.model.MarketingMeasure;
import hsel.softsmart.warenkorbanalyse.repository.MarketingMeasureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Stellt die Businesslogik für Marketingmaßnahmen zur Verfügung.
 */
@Service
public class MarketingMeasuresServiceImplementation implements MarketingMeasuresService {

    private final MarketingMeasureRepository marketingMeasureRepository;

    @Autowired
    public MarketingMeasuresServiceImplementation(MarketingMeasureRepository marketingMeasureRepository) {
        this.marketingMeasureRepository = marketingMeasureRepository;
    }

    /**
     * Liest alle Marketingmaßnahmen aus der Datenbank aus.
     *
     * @return Liste mit allen Marketingmaßnahmen
     */
    @Override
    public List<MarketingMeasure> getAllMarketingMeasures() {
        return marketingMeasureRepository.findAll();
    }

    /**
     * Löscht die Marketingmaßnahme aus der Datenbank.
     *
     * @param id Zu löschende Marketingmaßnahme
     */
    @Override
    public void deleteMarketingMeasure(long id) {
        marketingMeasureRepository.deleteById(id);
    }

    /**
     * Überschreibt die Marketingmaßnahme in der Datenbank.
     *
     * @param id Zu überschreibende Marketingmaßnahme
     * @param measure Wert, der übernommen werden soll
     */
    @Override
    @Transactional
    public void updateMarketingMeasure(long id, String measure) {
        marketingMeasureRepository.updateMarketingMeasureById(id, measure);
    }

    /**
     * Speichert die Marketingmaßnahme in der Datenbank ab.
     *
     * @param marketingMeasure Zu speichernde Marketingmaßnahme.
     */
    @Override
    public void saveMarketingMeasure(MarketingMeasure marketingMeasure) {
        marketingMeasureRepository.save(marketingMeasure);
    }
}
