package com.github.alglus.BorrowedList.controllers;

import com.github.alglus.BorrowedList.dto.SettingsPageDTO;
import com.github.alglus.BorrowedList.dto.ChangePasswordDTO;
import com.github.alglus.BorrowedList.models.Settings;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import com.github.alglus.BorrowedList.services.RegisterService;
import com.github.alglus.BorrowedList.services.SettingsService;
import com.github.alglus.BorrowedList.services.UserService;
import com.github.alglus.BorrowedList.util.validators.ChangePasswordFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
public class SettingsController {

    private final SettingsService settingsService;
    private final RegisterService registerService;
    private final UserService userService;
    private final ChangePasswordFormValidator changePasswordFormValidator;

    @Value("${alglus.languages.supported}")
    private List<String> supportedLanguages;

    @Autowired
    public SettingsController(SettingsService settingsService, RegisterService registerService, UserService userService, ChangePasswordFormValidator changePasswordFormValidator) {
        this.settingsService = settingsService;
        this.registerService = registerService;
        this.userService = userService;
        this.changePasswordFormValidator = changePasswordFormValidator;
    }


    @GetMapping("/settings")
    public String showSettingsPage(Model model,
                                   @ModelAttribute("settingsPageDTO") SettingsPageDTO settingsPageDTO,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {

        Settings settings = settingsService.findByUserIdOrCreateNewEntry(userDetails.getId());

        model.addAttribute("currentHomepageItemType", settingsService.getPreferredHomepageItemTypeOrDefault(settings));
        model.addAttribute("currentLanguageCode", settings.getLanguageCode());
        model.addAttribute("supportedLanguages", supportedLanguages);

        return "config/settings";
    }

    @PatchMapping("/settings")
    public String saveSettings(@ModelAttribute("settingsPageDTO") SettingsPageDTO settingsPageDTO,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               RedirectAttributes redirectAttributes) {

        settingsService.savePreferredHomepageItemType(userDetails.getId(), settingsPageDTO.getHomepageItemType());

        // The language is set by sending a GET request with the parameter: lang=<new language>
        // This parameter is detected by the LocaleChangeInterceptor, which passes it to the DatabaseLocaleResolver.
        redirectAttributes.addAttribute("lang", settingsPageDTO.getLanguageCode());

        redirectAttributes.addFlashAttribute("save", "success");

        return "redirect:/settings";
    }

    @GetMapping("/account")
    public String showAccountPage(Model model,
                                  @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {

        model.addAttribute("email", userDetails.getEmail());

        return "config/account";
    }

    @PatchMapping("/account/change-password")
    public String changePassword(Model model,
                                 @Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {

        changePasswordFormValidator.validate(changePasswordDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("email", userDetails.getEmail());
            return "config/account";
        }

        registerService.changePassword(changePasswordDTO, userDetails.getId());

        redirectAttributes.addFlashAttribute("save", "success");

        return "redirect:/account";
    }

    @PostMapping("/account/delete")
    public String deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails,
                                HttpServletRequest request) throws ServletException {

        request.logout();

        userService.delete(userDetails.getId());

        return "redirect:/login";
    }
}
