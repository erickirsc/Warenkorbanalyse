package hsel.softsmart.warenkorbanalyse.controller;

import hsel.softsmart.warenkorbanalyse.model.MarketingMeasure;
import hsel.softsmart.warenkorbanalyse.service.MarketingMeasuresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Webschnittstelle für {@code /marketing-measures}.
 */
@Controller
@RequestMapping("marketing-measures")
public class MarketingMeasuresController {

    private final MarketingMeasuresService marketingMeasuresService;

    @Autowired
    public MarketingMeasuresController(MarketingMeasuresService marketingMeasuresService) {
        this.marketingMeasuresService = marketingMeasuresService;
    }

    /**
     * Wird bei GET-Anfragen ausgeführt.
     *
     * Liest die Marketingmaßnahmen aus der Datenbank aus und koppelt diese an die View.
     *
     * @param model Die Referenz zur View
     * @return Sendet dem Client die marketingMeasures View
     */
    @GetMapping
    public String index(Model model) {
        List<MarketingMeasure> marketingMeasures = marketingMeasuresService.getAllMarketingMeasures();
        model.addAttribute("marketingMeasures", marketingMeasures);
        return "marketingMeasures";
    }

    /**
     * Wird bei GET-Anfragen ausgeführt.
     *
     * Löscht die Marketingmaßnahme in der Datenbank mit der passenden angegebenen ID.
     *
     * @param id Die zu löschende Marketingmaßnahme
     * @return Leitet den Client an /marketing-measures weiter
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable long id) {
        marketingMeasuresService.deleteMarketingMeasure(id);
        return "redirect:/marketing-measures";
    }

    /**
     * Wird bei POST-Anfragen ausgeführt.
     *
     * Sucht die Marketingmaßnahme in der Datenbank mit der passenden übergebenen ID
     * und überschreibt die Attribute mit dem übergebenen {@code MarketingMeasure} Object.
     *
     * @param id Die zu editiernde Marketingmaßnahme
     * @param marketingMeasure Beinhaltet neue Attribute, die übernommen werden
     * @return Leitet den Client an /marketing-measures weiter
     */
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable long id, @ModelAttribute MarketingMeasure marketingMeasure) {
        marketingMeasuresService.updateMarketingMeasure(id, marketingMeasure.getMeasure());
        return "redirect:/marketing-measures";
    }

    /**
     * Wird bei PSOT-Anfragen ausgeführt.
     *
     * Speichert die übergebene Marketingmaßnahme in der Datenbank.
     *
     * @param marketingMeasure Beinhaltet die Marketingmaßnahme, die gespeichert werden soll
     * @return Leitet den Client an /marketing-measures weiter
     */
    @PostMapping("/save")
    public String save(@ModelAttribute MarketingMeasure marketingMeasure) {
        marketingMeasuresService.saveMarketingMeasure(marketingMeasure);
        return "redirect:/marketing-measures";
    }
}
