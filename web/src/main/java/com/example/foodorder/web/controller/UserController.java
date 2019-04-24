package com.example.foodorder.web.controller;


import com.example.foodorder.common.model.Address;
import com.example.foodorder.common.model.Product;
import com.example.foodorder.common.model.Products;
import com.example.foodorder.common.model.User;
import com.example.foodorder.common.repository.*;
import com.example.foodorder.web.security.SpringUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class UserController {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ProductsRepository productsRepository;

    private User user;

    @GetMapping("/user/home")
    public String userHome(ModelMap map,
                           @ModelAttribute User user,
                           @RequestParam("page") Optional<Integer> page,
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

        this.user = user;

        map.addAttribute("user", user);

        return "userhome";
    }

  @GetMapping("/user/productBySubcategoryId")
  public String userProductsBySubategoryId(@RequestParam("id") int id, ModelMap map){
      map.addAttribute("products",   productRepository.findAllBySubcategoryId(id));
      map.addAttribute("menus", menuRepository.findAll());
      map.addAttribute("categories", categoryRepository.findAll());
      map.addAttribute("subcategories", subcategoryRepository.findAll());
      map.addAttribute("user", user);
      return "userproducts";

  }
    @GetMapping("/user/productById")
    public String userProductById(@RequestParam("id") int id, ModelMap map) {
        Product one = productRepository.getOne(id);
        map.addAttribute("product", one);
        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("products", productRepository.findTop4BySubcategory(one.getSubcategory()));
        map.addAttribute("subcategories", subcategoryRepository.findAll());
        map.addAttribute("user", user);

        return "usersingle";
    }


    @GetMapping("/user/productCartByUser")
    public String productCart(@RequestParam("id") int id, ModelMap modelMap) {

        modelMap.addAttribute("products", productsRepository.findAllByUserId(id));
        modelMap.addAttribute("menus", menuRepository.findAll());
        modelMap.addAttribute("categories", categoryRepository.findAll());
        modelMap.addAttribute("subcategories", subcategoryRepository.findAll());

        return "checkout";
    }

    @GetMapping("/user/addProducts")
    public String addProducts(@RequestParam ("count") int count, @RequestParam("product") Product product,
                            @AuthenticationPrincipal SpringUser springUser,
                            RedirectAttributes redirectAttributes) {

        Products products = new Products();
        products.setCount(count);
        products.setProduct(product);
        products.setPrice(count*product.getPrice());
        products.setStatus(false);
        productsRepository.save(products);
        redirectAttributes.addAttribute("id",product.getId());
        return "redirect:/user/productById";
    }

    @GetMapping("/user/account")
    public String userAccount(ModelMap modelMap) {
        return "userAccount";

    }

    @PostMapping("/user/addAddress")
    public String addAddress(@ModelAttribute Address address, @AuthenticationPrincipal SpringUser springUser) {
        address.setUser(springUser.getUser());
        addressRepository.save(address);
        return "redirect:/";
    }


}
