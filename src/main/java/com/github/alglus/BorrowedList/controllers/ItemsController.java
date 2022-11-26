package com.github.alglus.BorrowedList.controllers;

import com.github.alglus.BorrowedList.dto.ItemsDisplaySettingsDTO;
import com.github.alglus.BorrowedList.models.*;
import com.github.alglus.BorrowedList.security.CustomUserDetails;
import com.github.alglus.BorrowedList.services.ItemService;
import com.github.alglus.BorrowedList.services.ItemsDisplaySettingsService;
import com.github.alglus.BorrowedList.util.validators.ItemValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/items")
public class ItemsController {

    private final ItemService itemService;
    private final ItemsDisplaySettingsService itemsDisplaySettingsService;
    private final ItemValidator itemValidator;
    private final List<String> sortUrlGetParamKeys;
    private final List<String> sortUrlGetParams;


    @ModelAttribute("itemService")
    public ItemService getItemService() {
        return itemService;
    }

    @Autowired
    public ItemsController(ItemService itemService, ItemsDisplaySettingsService itemsDisplaySettingsService,
                           ItemValidator itemValidator, Map<String, String> itemsDisplaySortUrl) {
        this.itemService = itemService;
        this.itemsDisplaySettingsService = itemsDisplaySettingsService;
        this.itemValidator = itemValidator;
        this.sortUrlGetParamKeys = itemsDisplaySortUrl.keySet().stream().toList();
        this.sortUrlGetParams = itemsDisplaySortUrl.values().stream().toList();
    }


    @GetMapping()
    public String showItems(Model model,
                            @RequestParam("type") ItemType itemType,
                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                            @RequestParam(value = "items", required = false) String itemsPerPage,
                            @RequestParam(value = "sort", required = false) String sortBy,
                            @RequestParam(value = "dir", required = false) String sortDirection,
                            @AuthenticationPrincipal CustomUserDetails userDetails) {

        long userId = userDetails.getId();

        ItemsDisplaySettingsDTO newItemsDisplaySettings = new ItemsDisplaySettingsDTO(
                userId, itemsPerPage, sortBy, sortDirection);

        itemsDisplaySettingsService.saveItemsDisplaySettingsOrGetDefaults(newItemsDisplaySettings);

        Page<Item> items = itemService.findAllByUserIdAndTypeAndReturnedOrderedWithPagination(
                userId, itemType, false,
                newItemsDisplaySettings.getSortBy(),
                Sort.Direction.valueOf(newItemsDisplaySettings.getSortDirection().toUpperCase()),
                page,
                Integer.parseInt(newItemsDisplaySettings.getItemsPerPage()));

        model.addAttribute("settings", newItemsDisplaySettings);
        model.addAttribute("items", items.toList());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(1, items.getTotalPages()));
        model.addAttribute("totalItems", items.getTotalElements());
        model.addAttribute("sortUrlGetParamKeys", sortUrlGetParamKeys);
        model.addAttribute("sortUrlGetParams", sortUrlGetParams);

        return "items/list";
    }


    @GetMapping("/new")
    public String showNewItemPage(Model model,
                                  @RequestParam("type") ItemType itemType, // Serves to validate the correctness of the 'type' parameter.
                                  @ModelAttribute("item") Item item) {

        model.addAttribute("allWhomNames", itemService.findDistinctSortedWhomNames());

        return "items/new";
    }


    @PostMapping("/new")
    public String addNewItem(Model model,
                             @RequestParam("type") ItemType itemType,
                             @Valid @ModelAttribute("item") Item item,
                             BindingResult bindingResult) {

        itemValidator.validate(item, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("allWhomNames", itemService.findDistinctSortedWhomNames());
            return "items/new";
        }

        itemService.save(item, itemType);

        return "redirect:/items?type=" + itemType;
    }


    @GetMapping("/{id}")
    public String showItem(Model model,
                           @PathVariable("id") int id) {

        Item item = itemService.findByIdOrThrowResponseStatusNotFoundException(id);

        model.addAttribute("item", item);

        return "items/show";
    }


    @GetMapping("/{id}/return")
    public String markItemAsReturned(@PathVariable("id") int id) {

        Item item = itemService.findByIdOrThrowResponseStatusNotFoundException(id);

        itemService.markReturned(item, true);

        return "redirect:/items?type=" + item.getType();
    }


    @GetMapping("/{id}/edit")
    public String showEditPage(Model model,
                               @PathVariable("id") int id) {

        Item item = itemService.findByIdOrThrowResponseStatusNotFoundException(id);

        model.addAttribute("itemType", item.getType());
        model.addAttribute("item", item);
        model.addAttribute("allWhomNames", itemService.findDistinctSortedWhomNames());

        return "items/edit";
    }


    @PatchMapping("/{id}/edit")
    public String editItem(Model model,
                           @PathVariable("id") int id,
                           @Valid @ModelAttribute("item") Item updatedItem,
                           BindingResult bindingResult,
                           HttpServletRequest request) {

        itemValidator.validate(updatedItem, bindingResult);

        if (bindingResult.hasErrors()) {
            Item item = itemService.findByIdOrThrowResponseStatusNotFoundException(id);

            model.addAttribute("itemType", item.getType());
            model.addAttribute("allWhomNames", itemService.findDistinctSortedWhomNames());

            return "items/edit";
        }

        itemService.update(updatedItem, id);

        return "redirect:/items/" + id + "?" + request.getQueryString();
    }


    @DeleteMapping("/{id}/delete")
    public String deleteItem(@PathVariable("id") int id,
                             @RequestParam("type") ItemType itemType) {

        itemService.delete(id);

        return "redirect:/items?type=" + itemType;
    }
}
