package com.example.foodorder.web.controller;


import com.example.foodorder.common.model.Address;
import com.example.foodorder.common.model.Product;
import com.example.foodorder.common.model.User;
import com.example.foodorder.common.model.UserType;
import com.example.foodorder.common.repository.*;
import com.example.foodorder.web.security.SpringUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
public class MainController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private SubcategoryRepository subcategoryRepository;

    private User user;

    @GetMapping("/")
    public String main(ModelMap map, @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(12);
        Page<Product> all = productRepository.findAll(PageRequest.of(currentPage - 1, pageSize, Sort.by("date")));
        map.addAttribute("productsPage", all);
        int totalPages = all.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            map.addAttribute("pageNumbers", pageNumbers);
        }


        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("subcategories", subcategoryRepository.findAll());

        return "index";
    }



    @GetMapping("/login")
    public String loginPage(ModelMap map) {
        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("subcategories", subcategoryRepository.findAll());
        return "login";
    }



    @GetMapping("/register")
    public String registerPage(ModelMap map) {

        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("subcategories", subcategoryRepository.findAll());
        return "register";
    }


    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal SpringUser springUser) {

        if(springUser.getUser().getUserType() == UserType.ADMIN){
            return "AdminHome";
        }
        return "redirect:/user/home";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "redirect:/register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }



}

