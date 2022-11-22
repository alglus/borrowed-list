package com.github.alglus.BorrowedList;

import com.github.alglus.BorrowedList.config.WithMockCustomUser;
import com.github.alglus.BorrowedList.controllers.AuthController;
import com.github.alglus.BorrowedList.dto.RegistrationDTO;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import com.github.alglus.BorrowedList.security.LangSettingLogoutSuccessHandler;
import com.github.alglus.BorrowedList.services.RegisterService;
import com.github.alglus.BorrowedList.services.SettingsService;
import com.github.alglus.BorrowedList.util.validators.RegisterFormValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
public class AuthControllerTests {

    private final MockMvc mockMvc;
    private final MessageSource messageSource;

    @MockBean
    private DataSource dataSource;

    @MockBean
    private RegisterService registerService;

    @MockBean
    private RegisterFormValidator registerFormValidator;

    @MockBean
    private SettingsService settingsService;

    @MockBean
    private CustomUserDetails customUserDetails;

    @MockBean
    private LangSettingLogoutSuccessHandler langSettingLogoutSuccessHandler;

    @Autowired
    public AuthControllerTests(MockMvc mockMvc, MessageSource messageSource) {
        this.mockMvc = mockMvc;
        this.messageSource = messageSource;
    }


    @Test
    @WithAnonymousUser
    void root_URL_redirects_anonymous_user_to_login() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockCustomUser
    void root_URL_redirects_authenticated_user_to_login_successful() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login_successful"));
    }

    @Test
    @WithAnonymousUser
    void login_URL_allows_anonymous_users_and_shows_login_view() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));
    }

    @Test
    @WithMockCustomUser
    void login_URL_redirects_authenticated_users_to_loginSuccessful() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login_successful"));
    }

    @Test
    @WithMockCustomUser(id = 42)
    void loginSuccessful_URL_redirects_to_the_user_homepage() throws Exception {
        String homepageURL = "/items?type=borrowed";
        when(settingsService.getPreferredHomepageURL(42)).thenReturn(homepageURL);

        mockMvc.perform(get("/login_successful"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(homepageURL));
    }

    @Test
    @WithAnonymousUser
    void register_URL_allows_anonymous_users_and_shows_register_view() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("registrationDTO"));
    }

    @Test
    @WithAnonymousUser
    void registration_with_invalid_email_shows_validation_error() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setEmail("name@");
        registrationDTO.setPassword("password");
        registrationDTO.setRetypedPassword("password");

        String responseBody = mockMvc.perform(post("/register")
                        .with(csrf())
                        .flashAttr("registrationDTO", registrationDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andReturn().getResponse().getContentAsString();

        assertThat(responseBody).containsIgnoringCase(messageSource.getMessage(
                "register.validate.email.format", null, LocaleContextHolder.getLocale()));
    }

    @Test
    @WithAnonymousUser
    void registration_with_correct_data_redirects_to_login_page() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setEmail("name@example.com");
        registrationDTO.setPassword("password");
        registrationDTO.setRetypedPassword("password");

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .flashAttr("registrationDTO", registrationDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
