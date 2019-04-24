package com.example.foodorder.web.controller;

import com.example.foodorder.common.model.Category;
import com.example.foodorder.common.model.Menu;
import com.example.foodorder.common.model.Subcategory;
import com.example.foodorder.common.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MenuController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @GetMapping("/admin/addMenuCategoryProduct")
    public String addProduct(ModelMap map) {
        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("subcategories", subcategoryRepository.findAll());
        return "AddMenuCategory";
    }

    @PostMapping("/admin/addMenu")
    public String addProduct(@ModelAttribute Menu menu) {
        menuRepository.save(menu);
        return "redirect:/admin/addMenuCategoryProduct";
    }

    @PostMapping("/admin/addCategory")
    public String addProduct(@ModelAttribute Category category) {
        categoryRepository.save(category);
        return "redirect:/admin/addMenuCategoryProduct";
    }

    @PostMapping("/admin/addSubcategory")
    public String addSubcategory(@ModelAttribute Subcategory subcategory) {
        subcategoryRepository.save(subcategory);
        return "redirect:/admin/addMenuCategoryProduct";
    }


}
