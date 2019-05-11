package com.example.api.rest;

import com.example.api.security.CurrentUser;
import com.example.foodorder.common.model.Product;
import com.example.foodorder.common.model.Products;
import com.example.foodorder.common.repository.ProductRepository;
import com.example.foodorder.common.repository.ProductsRepository;
import com.example.foodorder.common.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ProductEndpoint {

    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping("/productBySubcategoryId/{id}")
    public ResponseEntity addProductsBySubategoryId(@PathVariable("id") int id) {
        List<Product> allBySubcategoryId = productRepository.findAllBySubcategoryId(id);
        if (allBySubcategoryId != null) {
            userRepository.deleteById(id);
            return ResponseEntity
                    .ok()
                    .build();
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/productByMenuId/{id}")
    public ResponseEntity addProductsByMenuId(@PathVariable("id") int id) {
        List<Product> allBySubcategoryId = productRepository.findAllByMenuId(id);
        if (allBySubcategoryId != null) {
            userRepository.deleteById(id);
            return ResponseEntity
                    .ok()
                    .build();
        }
        return ResponseEntity.notFound().build();

    }


    @GetMapping("/productById/{id}")
    public ResponseEntity addProductById(@PathVariable("id") int id) {
        Product one = productRepository.getOne(id);
        if (one != null) {
            return ResponseEntity
                    .ok(one);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/user/addProducts")
    public ResponseEntity addProducts(@RequestParam("count") int count,@RequestParam("productId") int id,
                              @AuthenticationPrincipal CurrentUser springUser) {
        Product byId = productRepository.getOne(id);
        Products products = new Products();
        products.setUser(springUser.getUser());
        products.setCount(count);

        products.setProduct(byId);
        products.setPrice(count * byId.getPrice());
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

        return ResponseEntity
                .ok(products);
    }




}
