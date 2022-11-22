package com.github.alglus.BorrowedList;

import com.github.alglus.BorrowedList.config.WithMockCustomUser;
import com.github.alglus.BorrowedList.controllers.SettingsController;
import com.github.alglus.BorrowedList.dto.ChangePasswordDTO;
import com.github.alglus.BorrowedList.dto.SettingsPageDTO;
import com.github.alglus.BorrowedList.models.Settings;
import com.github.alglus.BorrowedList.security.LangSettingLogoutSuccessHandler;
import com.github.alglus.BorrowedList.services.RegisterService;
import com.github.alglus.BorrowedList.services.SettingsService;
import com.github.alglus.BorrowedList.services.UserService;
import com.github.alglus.BorrowedList.util.validators.ChangePasswordFormValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SettingsController.class)
public class SettingsControllerTests {

    @TestConfiguration
    public static class SettingsControllerTestsConfig {

        private final MessageSource messageSource;
        private final PasswordEncoder passwordEncoder;

        public SettingsControllerTestsConfig(MessageSource messageSource, PasswordEncoder passwordEncoder) {
            this.messageSource = messageSource;
            this.passwordEncoder = passwordEncoder;
        }

        @Bean
        public ChangePasswordFormValidator changePasswordFormValidator() {
            return new ChangePasswordFormValidator(messageSource, passwordEncoder);
        }
    }

    private final MockMvc mockMvc;
    private final MessageSource messageSource;

    @MockBean
    private DataSource dataSource;

    @MockBean
    private SettingsService settingsService;

    @MockBean
    private RegisterService registerService;

    @MockBean
    private UserService userService;

    @MockBean
    private LangSettingLogoutSuccessHandler langSettingLogoutSuccessHandler;

    @Autowired
    public SettingsControllerTests(MockMvc mockMvc, MessageSource messageSource) {
        this.mockMvc = mockMvc;
        this.messageSource = messageSource;
    }


    @Test
    @WithMockCustomUser(id = 42)
    void settings_URL_shows_settings_view() throws Exception {
        Settings settings = new Settings();
        settings.setLanguageCode("en");

        when(settingsService.findByUserIdOrCreateNewEntry(42)).thenReturn(settings);
        when(settingsService.getPreferredHomepageItemTypeOrDefault(settings)).thenReturn("borrowed");

        mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andExpect(view().name("config/settings"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("settingsPageDTO", "currentHomepageItemType",
                        "currentLanguageCode", "supportedLanguages"));
    }

    @Test
    @WithMockCustomUser(id = 42)
    void settings_change_redirects_to_settings_page_with_success_parameter() throws Exception {
        SettingsPageDTO settingsPageDTO = new SettingsPageDTO();
        settingsPageDTO.setLanguageCode("en");
        settingsPageDTO.setHomepageItemType("borrowed");

        doNothing().when(settingsService).savePreferredHomepageItemType(42, settingsPageDTO.getHomepageItemType());

        mockMvc.perform(patch("/settings")
                        .with(csrf())
                        .flashAttr("settingsPageDTO", settingsPageDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/settings?lang={newLang}", settingsPageDTO.getLanguageCode()))
                .andExpect(flash().attribute("save", "success"));
    }

    @Test
    @WithMockCustomUser
    void account_URL_shows_account_view() throws Exception {
        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("config/account"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("changePasswordDTO", "email"));
    }

    @Test
    @WithAnonymousUser
    void account_URL_not_accessible_to_anonymous_user_and_redirects_to_login_page() throws Exception {
        mockMvc.perform(get("/account"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockCustomUser
    void password_change_with_wrong_current_password_shows_validation_error() throws Exception {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("pwd");
        changePasswordDTO.setNewPassword("newpassword");
        changePasswordDTO.setRetypedPassword("newpassword");

        String responseBody = mockMvc.perform(patch("/account/change-password")
                        .with(csrf())
                        .flashAttr("changePasswordDTO", changePasswordDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("config/account"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("changePasswordDTO", "email"))
                .andReturn().getResponse().getContentAsString();

        assertThat(responseBody).containsIgnoringCase(messageSource.getMessage(
                "account.password.current.wrong", null, LocaleContextHolder.getLocale()));
    }

    @Test
    @WithMockCustomUser
    void password_change_with_correct_input_redirects_to_account_page_with_success_parameter() throws Exception {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setCurrentPassword("password");
        changePasswordDTO.setNewPassword("newpassword");
        changePasswordDTO.setRetypedPassword("newpassword");

        mockMvc.perform(patch("/account/change-password")
                        .with(csrf())
                        .flashAttr("changePasswordDTO", changePasswordDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("save", "success"))
                .andExpect(redirectedUrl("/account"));
    }
}