package hsel.softsmart.warenkorbanalyse.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class AprioriValue {

    @Id
    @GeneratedValue
    private Long id;
    private String product;
    private String boughtTogetherWith;

    @ManyToOne
    @JoinColumn
    private Result result;
}
