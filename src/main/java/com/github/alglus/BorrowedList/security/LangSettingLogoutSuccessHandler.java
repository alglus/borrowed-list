package com.github.alglus.BorrowedList.security;

import com.github.alglus.BorrowedList.services.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LangSettingLogoutSuccessHandler implements LogoutSuccessHandler {

    private final SettingsService settingsService;

    @Autowired
    public LangSettingLogoutSuccessHandler(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * This implementation of the LogoutSuccessHandler allows to show the login page
     * in the user preferred language after signing out.
     * This is done by adding the GET parameter: lang=preferred-lang
     * <p>
     * Otherwise, the login page is shown in the default language, regardless of the language, which was set by the user.
     * This is because the session is deleted after signing out and the user settings in the database
     * are also unavailable, because no user is signed in at that point.
     *
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        String userPreferredLanguageCode = settingsService.getPreferredLanguageCode(userId);

        response.sendRedirect("/login?logged_out&lang=" + userPreferredLanguageCode);
    }
}
