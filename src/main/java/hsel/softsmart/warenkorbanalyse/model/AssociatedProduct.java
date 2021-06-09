package hsel.softsmart.warenkorbanalyse.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
public class AssociatedProduct {

    @Id
    @GeneratedValue
    private Long id;
    private String associatedProduct;

    @ManyToOne
    @JoinColumn
    @ToString.Exclude
    private AprioriValue aprioriValue;
}
