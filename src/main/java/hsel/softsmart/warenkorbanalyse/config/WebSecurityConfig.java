package hsel.softsmart.warenkorbanalyse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Konfiguriert die Sicherheitsschicht der Anwendung.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Überschreibt die Standardsicherheitskonfiguration.
     *
     * CSRF wird deaktiviert. Für jede Seite wird eine Authentifizierung gefordert.
     *
     * @param http Anfrage, die konfiguriert wird
     * @throws Exception Alle Fehler werden weitergeleitet
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/dist/**", "/css/**", "/img/**", "/plugins/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/analysis", true)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login");
    }
}
