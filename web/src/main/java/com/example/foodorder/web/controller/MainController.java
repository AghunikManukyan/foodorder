package com.example.foodorder.web.controller;


import com.example.foodorder.common.model.*;
import com.example.foodorder.common.repository.*;
import com.example.foodorder.common.specification.ProductSpecificationsBuilder;
import com.example.foodorder.web.security.SpringUser;
import com.example.foodorder.web.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
public class MainController {
    @Value("${user.image.upload.dir}")
    private String imageUploadDir;


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

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EmailService emailService;

    private User userik;

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
    public String loginSuccess(@AuthenticationPrincipal SpringUser springUser, ModelMap modelMap) {

        if (springUser.getUser().getUserType() == UserType.ADMIN) {
            return "redirect:/admin/orderPending";
        }
        return "redirect:/user/home";


    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, @RequestParam("file") MultipartFile file) throws IOException, MessagingException {

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "redirect:/register";
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPicUrl(fileName);
        userRepository.save(user);
        userik = user;
        emailService.sendSimpleMessage(user.getEmail(),
                "Բարի Գալուստ " + user.getName(),
                "Դուք Հաջողությամբ գրանցվել եք!");
        return "redirect:/user/address";


    }

    @GetMapping("/user/address")
    public String addAddress() {
        return "address";
    }

    @PostMapping("/user/addAddress")
    public String addAddress(@ModelAttribute Address address) {
        address.setUser(userik);
        addressRepository.save(address);
        return "redirect:/login";

    }

    @GetMapping("/contact")
    public String contact(ModelMap map) {
        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("subcategories", subcategoryRepository.findAll());
        return "contact";

    }


    @PostMapping("/contact")
    public String addContact(@ModelAttribute Contact contact) {
        contactRepository.save(contact);

        return "redirect:/contact";

    }

    @GetMapping("/search")
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
        return "products";
    }
}

