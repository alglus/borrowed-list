package com.github.alglus.BorrowedList.util;

import com.github.alglus.BorrowedList.security.CustomUserDetails;
import com.github.alglus.BorrowedList.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

@Configuration
public class DatabaseLocaleResolver extends SessionLocaleResolver {

    private SettingsService settingsService;

    @Value("${alglus.languages.supported}")
    private List<String> supportedLanguages;

    @Value("${alglus.languages.default}")
    private String defaultLanguage;

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }


    @Override
    public Locale resolveLocale(HttpServletRequest request) {

        if (Util.userIsSignedIn()) {
            return resolveLocaleForSignedInUser(request);
        } else {
            return resolveLocaleForAnonymousUser(request);
        }
    }

    private Locale resolveLocaleForAnonymousUser(HttpServletRequest request) {
        return resolveSessionLocale(request);
    }

    private Locale resolveSessionLocale(HttpServletRequest request) {
        return super.resolveLocale(request);
    }

    private Locale resolveLocaleForSignedInUser(HttpServletRequest request) {

        String preferredLanguage = getUserPreferredLanguage();

        if (hasNotBeenSetYet(preferredLanguage)) {
            return resolveLocaleForAnonymousUser(request);

        } else {
            Locale preferredLocale = Locale.forLanguageTag(preferredLanguage);

            setSessionLocaleToTheUserPreferredLocale(request, preferredLocale);

            return preferredLocale;
        }
    }

    private String getUserPreferredLanguage() {

        long userId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        return settingsService.getPreferredLanguageCode(userId);
    }

    private boolean hasNotBeenSetYet(String preferredLanguage) {
        return preferredLanguage == null;
    }

    private void setSessionLocaleToTheUserPreferredLocale(HttpServletRequest request, Locale preferredLocale) {
        Locale sessionLocale = resolveSessionLocale(request);

        if (!sessionLocale.equals(preferredLocale)) {
            setSessionLocale(request, null, preferredLocale);
        }
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        setSessionLocale(request, response, locale);

        if (Util.userIsSignedIn()) {
            saveUserPreferredLanguage(locale);
        }
    }

    private void setSessionLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        super.setLocale(request, response, locale);
    }

    private void saveUserPreferredLanguage(Locale locale) {

        long userId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        String language = getAndValidateLanguageFromLocale(locale);

        settingsService.savePreferredLanguageCode(userId, language);
    }

    private String getAndValidateLanguageFromLocale(Locale locale) {
        String newLanguage = locale.toLanguageTag();

        return validateChosenLanguage(newLanguage);
    }

    private String validateChosenLanguage(String chosenLanguage) {

        if (supportedLanguages.contains(chosenLanguage)) {
            return chosenLanguage;
        } else {
            return defaultLanguage;
        }
    }
}
