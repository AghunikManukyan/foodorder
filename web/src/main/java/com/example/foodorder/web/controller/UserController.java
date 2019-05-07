package com.example.foodorder.web.controller;


import com.example.foodorder.common.model.*;
import com.example.foodorder.common.repository.*;
import com.example.foodorder.common.specification.ProductSpecificationsBuilder;
import com.example.foodorder.web.security.SpringUser;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class UserController {
    @Value("${user.image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ContactRepository contactRepository;

    private Product product;

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
    public String userProductsBySubategoryId(@RequestParam("id") int id, ModelMap map) {
        map.addAttribute("products", productRepository.findAllBySubcategoryId(id));
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
        product = one;

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

    @PostMapping("/user/addProducts")
    public String addProducts(@RequestParam("count") int count,
                              @AuthenticationPrincipal SpringUser springUser,
                              RedirectAttributes redirectAttributes) {

        Products products = new Products();
        products.setUser(springUser.getUser());
        products.setCount(count);
        products.setProduct(product);
        products.setPrice(count * product.getPrice());
        products.setStatus(false);
        products.setDate(new Date());
        List<Products> allByUserId = productsRepository.findAllByUserId(springUser.getUser().getId());
        productsRepository.save(products);
        for (Products products1 : allByUserId) {
            if (products1.getProduct().getId() == products.getProduct().getId()) {
                products.setCount(products.getCount() + products1.getCount());
                productsRepository.deleteById(products1.getId());
            }

        }


        redirectAttributes.addAttribute("id", product.getId());
        return "redirect:/user/productById";
    }


    @GetMapping("/user/account")
    public String userAccount(ModelMap modelMap, @AuthenticationPrincipal SpringUser springUser) {

        modelMap.addAttribute("address", addressRepository.findAddressByUser(springUser.getUser()));
        modelMap.addAttribute("menus", menuRepository.findAll());
        modelMap.addAttribute("categories", categoryRepository.findAll());
        modelMap.addAttribute("subcategories", subcategoryRepository.findAll());
        modelMap.addAttribute("user", springUser.getUser());
        return "myAccount";

    }

    @GetMapping("/user/checkout")
    public String userCheckout(ModelMap modelMap, @AuthenticationPrincipal SpringUser springUser) {

        modelMap.addAttribute("menus", menuRepository.findAll());
        modelMap.addAttribute("categories", categoryRepository.findAll());
        modelMap.addAttribute("subcategories", subcategoryRepository.findAll());


        List<Products> allByUserId = productsRepository.findAllByUserId(springUser.getUser().getId());
        List<Products> productsByStatus = new LinkedList<>();

        for (Products products : allByUserId) {
            if (!products.isStatus()) {
                productsByStatus.add(products);
            }

        }

        modelMap.addAttribute("products", productsByStatus);

        return "checkout";

    }


    @GetMapping("/user/deleteProductsById")
    public String deleteProductsById(@RequestParam("id") int id) {
        productsRepository.deleteById(id);
        return "redirect:/user/checkout";
    }

    @GetMapping("/user/myOrder")
    public String myOrder(ModelMap modelMap, @AuthenticationPrincipal SpringUser springUser) {
        modelMap.addAttribute("orders", productOrderRepository.findAllByUserId(springUser.getUser().getId()));
        modelMap.addAttribute("menus", menuRepository.findAll());
        modelMap.addAttribute("categories", categoryRepository.findAll());
        modelMap.addAttribute("subcategories", subcategoryRepository.findAll());


        return "myOrder";
    }




    @GetMapping("/user/getImage")
    public void getImageAsByteArray(HttpServletResponse response, @RequestParam("picUrl") String picUrl) throws IOException {
        InputStream in = new FileInputStream(imageUploadDir + File.separator + picUrl);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }



    @GetMapping("/user/updateAddress")
    public String updateAddress() {
        return "updateaddress";
    }

    @PostMapping("/user/updateAddress")
    public String updateAddress(@ModelAttribute Address address,@AuthenticationPrincipal SpringUser springUser) {
        Address addressByUser = addressRepository.findAddressByUser(springUser.getUser());
        address.setId(addressByUser.getId());
        address.setUser(springUser.getUser());
        addressRepository.deleteById(addressByUser.getId());
        addressRepository.save(address);
        return "redirect:/user/account";

    }

    @GetMapping("/user/updateUser")
    public String updateUser(ModelMap modelMap) {
        modelMap.addAttribute("menus", menuRepository.findAll());
        modelMap.addAttribute("categories", categoryRepository.findAll());
        modelMap.addAttribute("subcategories", subcategoryRepository.findAll());
        return "updateUser";
    }

    @PostMapping("/user/updateUser")
    public String updateUser(@ModelAttribute User user, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal SpringUser springUser) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);

        springUser.getUser().setName(user.getName());
        springUser.getUser().setSurname(user.getSurname());
        springUser.getUser().setEmail(user.getEmail());
        springUser.getUser().setPicUrl(fileName);

        return "redirect:/user/account";

    }

    @GetMapping("/user/contact")
    public String contact(ModelMap map){
        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("subcategories", subcategoryRepository.findAll());
        return "userContact";

    }

    @PostMapping("/user/contact")
    public String addContact(@ModelAttribute Contact contact) {
        contactRepository.save(contact);

        return "redirect:/user/contact";

    }

    @GetMapping("/user/search")
    public String search(@RequestParam("search") String search, ModelMap map) {
       ProductSpecificationsBuilder builder = new ProductSpecificationsBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        List<Product> all = productRepository.findAll(builder.build());
        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("subcategories", subcategoryRepository.findAll());
        map.addAttribute("products", all);
        return "userproducts";
    }
}
