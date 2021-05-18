package hsel.softsmart.warenkorbanalyse.config;

import hsel.softsmart.warenkorbanalyse.weka.WekaBspStud;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WekaConfig {

    @Bean
    public WekaBspStud wekaBspStud() {
        return new WekaBspStud();
    }
}
