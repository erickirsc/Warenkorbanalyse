package hsel.softsmart.warenkorbanalyse.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    /*private Map<String, List<String>> apriori = new HashMap<>();

    public void addApriori(String group, String ... products) {
        //TODO: Implement addApriori
    }*/
}
