package hsel.softsmart.warenkorbanalyse.repository;

import hsel.softsmart.warenkorbanalyse.model.MarketingMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Stellt CRUD-Funktionen für die Marketingmaßnahmen zur Verfügung.
 */
public interface MarketingMeasureRepository extends JpaRepository<MarketingMeasure, Long> {
    /**
     * Überschreibt die Marketingmaßnahme in der Datenbank.
     *
     * @param id Die Marketingmaßnahmen-ID, die überarbeitet werden soll
     * @param measure Maßnahme, die den alten Wert überschreibt
     */
    @Modifying
    @Query("UPDATE MarketingMeasure m SET m.measure = :measure WHERE m.id = :id")
    void updateMarketingMeasureById(long id, String measure);
}
