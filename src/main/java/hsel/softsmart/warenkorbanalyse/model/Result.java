package hsel.softsmart.warenkorbanalyse.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
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
    private Date date;

    @OneToMany(
            mappedBy = "result",
            cascade = CascadeType.ALL,
            orphanRemoval = true
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
