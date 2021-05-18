package hsel.softsmart.warenkorbanalyse.controller;

import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public String index() {
        return "dashboard";
    }

    @SneakyThrows
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        //TODO: Use reader and parse the file into WEKA API
        return "redirect:/dashboard";
    }
}
