package com.example.foodorder.web.controller;


import com.example.foodorder.common.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ContactController {


    @Autowired
    private ContactRepository contactRepository;


    @GetMapping("/admin/contact")
    public String getContactMessage(ModelMap modelMap){

        modelMap.addAttribute("contacts", contactRepository.findAll());
        return "AdminContact";

    }






}
