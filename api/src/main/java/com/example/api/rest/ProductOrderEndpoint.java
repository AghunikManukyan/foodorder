package com.example.api.rest;

import com.example.api.security.CurrentUser;
import com.example.foodorder.common.model.ProductOrder;
import com.example.foodorder.common.model.Products;
import com.example.foodorder.common.model.Status;
import com.example.foodorder.common.repository.ProductOrderRepository;
import com.example.foodorder.common.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ProductOrderEndpoint {


    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductsRepository productsRepository;



    @GetMapping("/user/addOrder")
    private ResponseEntity addOrder(@AuthenticationPrincipal CurrentUser springUser) {

        List<Products> allByUserId = productsRepository.findAllByUserId(springUser.getUser().getId());

        if (allByUserId == null) {
            return ResponseEntity.notFound().build();
        }
        ProductOrder productOrder = new ProductOrder();
        for (Products products : allByUserId) {
            products.setStatus(true);
        }
        productOrder.setDate(new Date());
        productOrder.setProducts(allByUserId);
        productOrder.setUser(springUser.getUser());
        productOrderRepository.save(productOrder);
        return ResponseEntity
                .ok().build();
    }


    @GetMapping("/user/productCartByUser/{id}")
    public ResponseEntity productCart(@RequestParam("id") int id) {
        List<Products> allByUserId = productsRepository.findAllByUserId(id);
            if (allByUserId  != null) {
                return ResponseEntity
                        .ok(allByUserId );
            }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/productOrderByPending")
    public ResponseEntity getProductOrderPending() {
        List<ProductOrder> allByStatus = productOrderRepository.findAllByStatus(Status.PENDING);

        if (allByStatus != null) {
            return ResponseEntity
                    .ok(allByStatus);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/productOrderByCompletes")
    public ResponseEntity getProductOrderCompletes() {
        List<ProductOrder> allByStatus = productOrderRepository.findAllByStatus(Status.COMPLETES);

        if (allByStatus != null) {
            return ResponseEntity
                    .ok(allByStatus);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/admin/productOrderByProcessing")
    public ResponseEntity getProductOrderProcessing() {
        List<ProductOrder> allByStatus = productOrderRepository.findAllByStatus(Status.PROCESSING);

        if (allByStatus != null) {
            return ResponseEntity
                    .ok(allByStatus);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/confirmedOrder/{id}")
    public ResponseEntity confirmedOrder(@PathVariable("id") int id) {
        Optional<ProductOrder> byId = productOrderRepository.findById(id);

        if (byId.isPresent()) {
            byId.get().setStatus(Status.PROCESSING);
            return ResponseEntity
                    .ok(byId);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/admin/finishOrder/{id}")
    public ResponseEntity finishOrder(@PathVariable("id") int id) {
        Optional<ProductOrder> byId = productOrderRepository.findById(id);

        if (byId.isPresent()) {
            byId.get().setStatus(Status.COMPLETES);
            return ResponseEntity
                    .ok(byId);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/user/deleteOrder/{id}")
    public ResponseEntity deleteOrder(@RequestParam("id") int id, @AuthenticationPrincipal CurrentUser springUser) {
        ProductOrder one = productOrderRepository.getOne(id);
        productsRepository.deleteAll(one.getProducts());
        productOrderRepository.deleteById(id);

        return ResponseEntity
                .ok()
                .build();
    }
    @GetMapping("/user/myOrder")
    public ResponseEntity myOrder(@AuthenticationPrincipal CurrentUser springUser) {
        List<ProductOrder> allByUserId = productOrderRepository.findAllByUserId(springUser.getUser().getId());

        if (allByUserId != null) {
            return ResponseEntity
                    .ok(allByUserId);
        }
        return ResponseEntity.notFound().build();

    }


}
