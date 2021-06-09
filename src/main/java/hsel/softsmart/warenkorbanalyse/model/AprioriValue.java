package hsel.softsmart.warenkorbanalyse.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class AprioriValue {

    @Id
    @GeneratedValue
    private Long id;
    private String product;
    @OneToMany(
            mappedBy = "aprioriValue",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AssociatedProduct> associatedProducts = new ArrayList<>();

    @ManyToOne
    @JoinColumn
    @ToString.Exclude
    private Result result;

    public void addAssociatedProducts(Set<String> boughtTogetherWith) {
        for (String associatedProduct : boughtTogetherWith) {
            AssociatedProduct ap = new AssociatedProduct();
            ap.setAssociatedProduct(associatedProduct);
            ap.setAprioriValue(this);

            associatedProducts.add(ap);
        }
    }
}
