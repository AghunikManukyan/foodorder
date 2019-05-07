package com.example.foodorder.web.controller;


import com.example.foodorder.common.model.*;
import com.example.foodorder.common.repository.AddressRepository;
import com.example.foodorder.common.repository.ProductOrderRepository;
import com.example.foodorder.common.repository.ProductsRepository;
import com.example.foodorder.common.repository.UserRepository;
import com.example.foodorder.web.security.SpringUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Controller
public class ProductOrderController {


    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/admin/orderPending")
    public String getOrderUserPeding(ModelMap modelMap) {

        List<ProductOrder> allByStatus = productOrderRepository.findAllByStatus(Status.PENDING);
        List<User> allUserByOrder = new LinkedList<>();
        for (ProductOrder byStatus : allByStatus) {
            allUserByOrder.add(byStatus.getUser());

        }

        modelMap.addAttribute("users", allUserByOrder);
        return "AdminHome";

    }

    @GetMapping("/admin/orderPendingLook")
    public String getOrderPedingLook(@RequestParam("id") int id, ModelMap modelMap) {

        modelMap.addAttribute("orders", productOrderRepository.findAllByUserId(id));
        modelMap.addAttribute("address", addressRepository.findAddressByUser(userRepository.getOne(id)));
        return "orderpending";

    }

    @GetMapping("/admin/orderProcessingLook")
    public String getOrderProcessingLook(@RequestParam("id") int id, ModelMap modelMap) {

        modelMap.addAttribute("orders", productOrderRepository.findAllByUserId(id));
        modelMap.addAttribute("address", addressRepository.findAddressByUser(userRepository.getOne(id)));
        return "orderprocessing";

    }

    @GetMapping("/admin/orderCompletesLook")
    public String getOrderCompletesLook(@RequestParam("id") int id, ModelMap modelMap) {

        modelMap.addAttribute("orders", productOrderRepository.findAllByUserId(id));
        modelMap.addAttribute("address", addressRepository.findAddressByUser(userRepository.getOne(id)));
        return "ordercompletes";

    }

    @GetMapping("/admin/confirmedOrder")
    public String confirmedOrder(@RequestParam("id") int id, ModelMap modelMap) {
        ProductOrder one = productOrderRepository.getOne(id);
        one.setStatus(Status.PROCESSING);
        productOrderRepository.save(one);

        return "redirect:/admin/orderPending";

    }

    @GetMapping("/admin/finishOrder")
    public String finishOrder(@RequestParam("id") int id, ModelMap modelMap) {
        ProductOrder one = productOrderRepository.getOne(id);
        one.setStatus(Status.COMPLETES);

        productOrderRepository.save(one);
        return "redirect:/admin/orderProcessing";

    }


    @GetMapping("/admin/orderProcessing")
    public String getOrderProcessing(ModelMap modelMap) {

        List<ProductOrder> allByStatus = productOrderRepository.findAllByStatus(Status.PROCESSING);
        List<User> allUserByOrder = new LinkedList<>();
        for (ProductOrder byStatus : allByStatus) {
            allUserByOrder.add(byStatus.getUser());

        }

        modelMap.addAttribute("users", allUserByOrder);
        return "AdminProcessing";

    }


    @GetMapping("/admin/orderCompletes")
    public String getOrderCompletes(ModelMap modelMap) {
        List<ProductOrder> allByStatus = productOrderRepository.findAllByStatus(Status.COMPLETES);
        List<User> allUserByOrder = new LinkedList<>();
        for (ProductOrder byStatus : allByStatus) {
            allUserByOrder.add(byStatus.getUser());

        }

        modelMap.addAttribute("users", allUserByOrder);
        return "AdminCompletes";

    }

    @GetMapping("/user/addOrder")
    public String addOrder(@AuthenticationPrincipal SpringUser springUser) {
        List<Products> allByUserId = productsRepository.findAllByUserId(springUser.getUser().getId());
        if (allByUserId == null) {
            return "redirect:/user/checkout";
        }
        ProductOrder productOrder = new ProductOrder();
        for (Products products : allByUserId) {
            products.setStatus(true);
        }
        productOrder.setDate(new Date());
        productOrder.setProducts(allByUserId);
        productOrder.setUser(springUser.getUser());
        productOrderRepository.save(productOrder);
        return "redirect:/user/myOrder";

    }

    @GetMapping("/user/deleteOrder")
    public String deleteOrder(@RequestParam("id") int id, @AuthenticationPrincipal SpringUser springUser) {
        ProductOrder one = productOrderRepository.getOne(id);
        productsRepository.deleteAll(one.getProducts());
        productOrderRepository.deleteById(id);
        if (springUser.getUser().getUserType() == UserType.ADMIN) {
            return "redirect:/admin/orderPending";
        }
        return "redirect:/user/myOrder";
    }


}
