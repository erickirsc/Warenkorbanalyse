package hsel.softsmart.warenkorbanalyse.repository;

import hsel.softsmart.warenkorbanalyse.model.MarketingMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MarketingMeasureRepository extends JpaRepository<MarketingMeasure, Long> {
    @Modifying
    @Query("UPDATE MarketingMeasure m SET m.measure = :measure WHERE m.id = :id")
    void updateMarketingMeasureById(long id, String measure);
}
