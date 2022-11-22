package com.github.alglus.BorrowedList.controllers;

import com.github.alglus.BorrowedList.dto.RegistrationDTO;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import com.github.alglus.BorrowedList.services.RegisterService;
import com.github.alglus.BorrowedList.services.SettingsService;
import com.github.alglus.BorrowedList.util.Util;
import com.github.alglus.BorrowedList.util.validators.RegisterFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class AuthController {

    private final RegisterService registerService;
    private final RegisterFormValidator registerFormValidator;
    private final SettingsService settingsService;

    @Value("${alglus.languages.supported}")
    private List<String> supportedLanguages;

    @ModelAttribute("supportedLanguages")
    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    @Autowired
    public AuthController(RegisterService registerService, RegisterFormValidator registerFormValidator, SettingsService settingsService) {
        this.registerService = registerService;
        this.registerFormValidator = registerFormValidator;
        this.settingsService = settingsService;
    }


    @GetMapping("/")
    public String redirectToHomepage() {

        if (Util.userIsNotSignedIn())
            return "redirect:/login";

        return "redirect:/login_successful";
    }


    @GetMapping("/login")
    public String showLoginPage() {

        if (Util.userIsSignedIn())
            return "redirect:/login_successful";

        return "auth/login";
    }


    @GetMapping("/login_successful")
    public String redirectAfterLogin(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return "redirect:" + settingsService.getPreferredHomepageURL(userDetails.getId());
    }


    @GetMapping("/register")
    public String showRegisterPage(@ModelAttribute("registrationDTO") RegistrationDTO registrationDTO) {

        return "auth/register";
    }


    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationDTO") RegistrationDTO registrationDTO,
                           BindingResult bindingResult) {

        registerFormValidator.validate(registrationDTO, bindingResult);

        if (bindingResult.hasErrors())
            return "auth/register";

        registerService.registerNewUser(registrationDTO);

        return "redirect:/login";
    }
}
