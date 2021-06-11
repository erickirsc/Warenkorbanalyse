package hsel.softsmart.warenkorbanalyse.config;

import hsel.softsmart.warenkorbanalyse.weka.WekaBspStud;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialisiert die nötigen Klassen für die WEKA Analysen.
 */
@Configuration
public class WekaConfig {

    /**
     * Legt die WekaBspStud Klasse als Singleton im Application Context ab.
     *
     * @return WEKA Object Bean
     */
    @Bean
    public WekaBspStud wekaBspStud() {
        return new WekaBspStud();
    }
}
