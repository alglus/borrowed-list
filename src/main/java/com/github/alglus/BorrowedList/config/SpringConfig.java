package com.github.alglus.BorrowedList.config;

import com.github.alglus.BorrowedList.security.LangSettingLogoutSuccessHandler;
import com.github.alglus.BorrowedList.util.DatabaseLocaleResolver;
import com.transferwise.icu.ICUReloadableResourceBundleMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.sql.DataSource;
import java.util.*;

@EnableWebSecurity
@PropertySource("classpath:config/db.properties")
public class SpringConfig implements WebMvcConfigurer {

    private final DataSource dataSource;
    private final LangSettingLogoutSuccessHandler langSettingLogoutSuccessHandler;


    @Autowired
    public SpringConfig(DataSource dataSource, LangSettingLogoutSuccessHandler langSettingLogoutSuccessHandler) {
        this.dataSource = dataSource;
        this.langSettingLogoutSuccessHandler = langSettingLogoutSuccessHandler;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/actuator/**").hasRole("ADMIN")
                .antMatchers("/login", "/register", "/error").permitAll()
                .antMatchers("/css/auth.css", "/img/**").permitAll()
                .anyRequest().hasAnyRole("USER", "ADMIN")

                .and()

                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/login_successful", false)
                .failureUrl("/login?error")

                .and()

                .rememberMe()
                .tokenRepository(persistentTokenRepository())

                .and()

                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(langSettingLogoutSuccessHandler);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MessageSource messageSource() {
        ICUReloadableResourceBundleMessageSource messageSource = new ICUReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:language/messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl ptr = new JdbcTokenRepositoryImpl();
        ptr.setDataSource(dataSource);
        return ptr;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new DatabaseLocaleResolver();
        slr.setDefaultLocale(Locale.forLanguageTag("en"));
        slr.setLocaleAttributeName("session.current.locale");
        slr.setTimeZoneAttributeName("session.current.timezone");
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean
    @ConfigurationProperties(prefix = "alglus.items-display.allowed-options")
    public Map<String, List<String>> allowedItemsDisplayOptions() {
        return new HashMap<>();
    }

    @Bean
    @ConfigurationProperties(prefix = "alglus.items-display.default-option")
    public Map<String, String> defaultItemsDisplayOption() {
        return new HashMap<>();
    }

    @Bean
    @ConfigurationProperties(prefix = "alglus.items-display.sort-url")
    public Map<String, String> itemsDisplaySortUrl() {
        // A LinkedHashMap is used, so that the Sort By options are displayed by their alphabetical order.
        return new LinkedHashMap<>();
    }
}
