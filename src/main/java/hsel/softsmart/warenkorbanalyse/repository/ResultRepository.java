package hsel.softsmart.warenkorbanalyse.repository;

import hsel.softsmart.warenkorbanalyse.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Stellt CRUD-Funktionen für die Ergebnisse zur Verfügung.
 */
public interface ResultRepository extends JpaRepository<Result, Long> {
    /**
     * Gibt das letzte gespeicherte Ergebnis aus.
     *
     * @return Ein optionales Ergebnis, wenn eins gefunden wurde
     */
    Optional<Result> findTopByOrderByIdDesc();

    /**
     * Gibt die erste Zeile der Ergenistabelle aus.
     *
     * @return Ein optionales Ergebnis, wenn eins gefunden wurde
     */
    Optional<Result> findTopByOrderById();
}
