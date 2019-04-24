package com.example.foodorder.web.controller;

import com.example.foodorder.common.model.Product;
import com.example.foodorder.common.model.Products;
import com.example.foodorder.common.repository.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Controller
public class ProductController {
    @Value("${image.upload.dir}")
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


    @PostMapping("/admin/addProduct")
    public String addProduct(@ModelAttribute Product product, @RequestParam("picture") MultipartFile file) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);
        product.setPicUrl(fileName);
        product.setDate(new Date());
        productRepository.save(product);


        return "redirect:/admin/addMenuCategoryProduct";

    }

    @GetMapping("/productById")
    public String productById(@RequestParam("id") int id, ModelMap map) {

        Product one = productRepository.getOne(id);
        map.addAttribute("product", one);
        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("products", productRepository.findTop4BySubcategory(one.getSubcategory()));
        map.addAttribute("subcategories", subcategoryRepository.findAll());

        return "single";
    }



    @GetMapping("/productBySubcategoryId")
    public String productBySubcategoryId(@RequestParam("id") int id, ModelMap map){

        map.addAttribute("products",   productRepository.findAllBySubcategoryId(id));
        map.addAttribute("menus", menuRepository.findAll());
        map.addAttribute("categories", categoryRepository.findAll());
        map.addAttribute("subcategories", subcategoryRepository.findAll());

        return "products";
    }


    @GetMapping("/product/getImage")
    public void getImageAsByteArray(HttpServletResponse response, @RequestParam("picUrl") String picUrl) throws IOException {
        InputStream in = new FileInputStream(imageUploadDir + File.separator + picUrl);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }








}
