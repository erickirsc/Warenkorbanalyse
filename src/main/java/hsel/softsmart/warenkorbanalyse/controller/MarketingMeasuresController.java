package hsel.softsmart.warenkorbanalyse.controller;

import hsel.softsmart.warenkorbanalyse.model.MarketingMeasure;
import hsel.softsmart.warenkorbanalyse.service.MarketingMeasuresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("marketing-measures")
public class MarketingMeasuresController {

    private final MarketingMeasuresService marketingMeasuresService;

    @Autowired
    public MarketingMeasuresController(MarketingMeasuresService marketingMeasuresService) {
        this.marketingMeasuresService = marketingMeasuresService;
    }

    @GetMapping
    public String index(Model model) {
        List<MarketingMeasure> marketingMeasures = marketingMeasuresService.getAllMarketingMeasures();
        model.addAttribute("marketingMeasures", marketingMeasures);
        return "marketingMeasures";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable long id) {
        marketingMeasuresService.deleteMarketingMeasure(id);
        return "redirect:/marketing-measures";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable long id, @ModelAttribute MarketingMeasure marketingMeasure) {
        marketingMeasuresService.updateMarketingMeasure(id, marketingMeasure.getMeasure());
        return "redirect:/marketing-measures";
    }
}
