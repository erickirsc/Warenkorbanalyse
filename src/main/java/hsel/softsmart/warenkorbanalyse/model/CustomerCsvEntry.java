package hsel.softsmart.warenkorbanalyse.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class CustomerCsvEntry {

    @CsvBindByName(column = "Geschlecht")
    private String gender;

    @CsvBindByName(column = "Alter")
    private String age;

    @CsvBindByName(column = "Kinder")
    private String children;

    @CsvBindByName(column = "Familienstand")
    private String maritalStatus;

    @CsvBindByName(column = "Berufstaetig")
    private String employed;

    @CsvBindByName(column = "Einkaufstag")
    private String shoppingDay;

    @CsvBindByName(column = "Einkaufsuhrzeit")
    private String shoppingTime;

    @CsvBindByName(column = "Wohnort")
    private String place;

    @CsvBindByName(column = "Haushaltsnettoeinkommen")
    private String salary;

    @CsvBindByName(column = "Stammkunde")
    private String regular;

    @CsvBindByName(column = "Einkaufssumme")
    private Integer purchaseAmount;

    @CsvBindByName(column = "Fertiggerichte")
    private Integer readyMeal;

    @CsvBindByName(column = "Tiefkuehlware")
    private Integer frozenGoods;

    @CsvBindByName(column = "Milchprodukte")
    private Integer milkProducts;

    @CsvBindByName(column = "Backwaren")
    private Integer bakeryProducts;

    @CsvBindByName(column = "Obst/Gemuese")
    private Integer fruitAndVegetables;

    @CsvBindByName(column = "Spirituosen")
    private Integer spirits;

    @CsvBindByName(column = "Tiernahrung")
    private Integer petFood;

    @CsvBindByName(column = "Bier")
    private Integer beer;

    @CsvBindByName(column = "Frischfleisch")
    private Integer freshMeat;

    @CsvBindByName(column = "Drogerieartikel")
    private Integer drugstoreItems;

    @CsvBindByName(column = "Konserven")
    private Integer cannedGoods;

    @CsvBindByName(column = "Kaffee/Tee")
    private Integer coffeeAndTea;

    @CsvBindByName(column = "Suessigkeiten")
    private Integer sweets;

    @CsvBindByName(column = "Wurstwaren")
    private Integer sausages;

    @CsvBindByName(column = "Schreibwaren")
    private Integer stationery;
}
