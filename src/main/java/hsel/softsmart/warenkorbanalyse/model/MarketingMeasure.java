package hsel.softsmart.warenkorbanalyse.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class MarketingMeasure {

    @Id
    @GeneratedValue
    private Long id;
    private String measure;
}
