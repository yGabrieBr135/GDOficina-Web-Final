package br.com.gdoficina.config;

import org.springframework.context.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import java.util.Locale;
@Configuration
public class WebConfig {
    @Bean LocaleResolver localeResolver() { return new FixedLocaleResolver(Locale.forLanguageTag("pt-BR")); }
    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth->auth.anyRequest().permitAll()).build();
    }
}
