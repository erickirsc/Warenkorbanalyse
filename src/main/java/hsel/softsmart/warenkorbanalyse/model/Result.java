package hsel.softsmart.warenkorbanalyse.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Result {

    @Id
    @GeneratedValue
    private Long id;
    private String topProduct;
    private String flopProduct;
    private String topDay;
    private String topTime;

    @OneToMany(
            mappedBy = "result",
            cascade = CascadeType.ALL
    )
    private List<AprioriValue> aprioriValues = new ArrayList<>();

    public void addAprioriValue(String product, String boughtTogetherWith) {
        AprioriValue aprioriValue = new AprioriValue();
        aprioriValue.setProduct(product);
        aprioriValue.setBoughtTogetherWith(boughtTogetherWith);
        aprioriValues.add(aprioriValue);
        aprioriValue.setResult(this);
    }
}
