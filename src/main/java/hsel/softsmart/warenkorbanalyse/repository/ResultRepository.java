package hsel.softsmart.warenkorbanalyse.repository;

import hsel.softsmart.warenkorbanalyse.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findTopByOrderByIdDesc();
    Optional<Result> findTopByOrderById();
}
