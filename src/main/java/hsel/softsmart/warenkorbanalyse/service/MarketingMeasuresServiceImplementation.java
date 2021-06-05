package hsel.softsmart.warenkorbanalyse.service;

import hsel.softsmart.warenkorbanalyse.model.MarketingMeasure;
import hsel.softsmart.warenkorbanalyse.repository.MarketingMeasureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketingMeasuresServiceImplementation implements MarketingMeasuresService {

    private final MarketingMeasureRepository marketingMeasureRepository;

    @Autowired
    public MarketingMeasuresServiceImplementation(MarketingMeasureRepository marketingMeasureRepository) {
        this.marketingMeasureRepository = marketingMeasureRepository;
    }

    @Override
    public List<MarketingMeasure> getAllMarketingMeasures() {
        return marketingMeasureRepository.findAll();
    }

    @Override
    public void deleteMarketingMeasure(long id) {
        marketingMeasureRepository.deleteById(id);
    }
}
