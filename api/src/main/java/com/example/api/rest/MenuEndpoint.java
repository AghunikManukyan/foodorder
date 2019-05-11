package com.example.api.rest;

import com.example.foodorder.common.model.Category;
import com.example.foodorder.common.model.Menu;
import com.example.foodorder.common.model.Subcategory;
import com.example.foodorder.common.repository.CategoryRepository;
import com.example.foodorder.common.repository.MenuRepository;
import com.example.foodorder.common.repository.SubcategoryRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class MenuEndpoint {


    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubcategoryRepository subcategoryRepository;




    @PostMapping("/addMneu")
    @ApiOperation(value = "Create Menu", response = Menu.class)
    @ApiResponses({
            @ApiResponse(code = 409, message = "Menu with name already exists"),
            @ApiResponse(code = 200, message = "Menu created")
    })
    public ResponseEntity addMenu(@RequestBody Menu menu) {
        if (menuRepository.findAllByName(menu.getName()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
        menuRepository.save(menu);
        return ResponseEntity.ok(menu);
    }



    @PostMapping("/addCategory")
    @ApiOperation(value = "Create Category", response = Category.class)
    @ApiResponses({
            @ApiResponse(code = 409, message = "Category with name already exists"),
            @ApiResponse(code = 200, message = "Category created")
    })
    public ResponseEntity addCategory(@RequestBody Category category) {
        if (categoryRepository.findAllByName(category.getName()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
        categoryRepository.save(category);
        return ResponseEntity.ok(category);
    }

    @PostMapping("/addSubategory")
    @ApiOperation(value = "Create Subcategory", response = Subcategory.class)
    @ApiResponses({
            @ApiResponse(code = 409, message = "Subcategory with name already exists"),
            @ApiResponse(code = 200, message = "Subcategory created")
    })
    public ResponseEntity addSubcategorry(@RequestBody Subcategory subcategory) {
        if (subcategoryRepository.findAllByName(subcategory.getName()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
        subcategoryRepository.save(subcategory);
        return ResponseEntity.ok(subcategory);
    }

    @GetMapping("/menu/all")
    public ResponseEntity getAllMenu() {
        return ResponseEntity.ok(menuRepository.findAll());
    }

    @GetMapping("/menu/{id}")
    public ResponseEntity getMenuById(@PathVariable("id") int id) {
        Optional<Menu> byId = menuRepository.findById(id);
        if (byId.isPresent()) {
            return ResponseEntity.ok(byId.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/category/all")
    public ResponseEntity getAllCategory() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/category/{id}")
    public ResponseEntity getCategoryById(@PathVariable("id") int id) {
        Optional<Category> byId = categoryRepository.findById(id);
        if (byId.isPresent()) {
            return ResponseEntity.ok(byId.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/subcategory/all")
    public ResponseEntity getAllSubcategory() {
        return ResponseEntity.ok(subcategoryRepository.findAll());
    }

    @GetMapping("/subcategory/{id}")
    public ResponseEntity getSubcategoryById(@PathVariable("id") int id) {
        Optional<Subcategory> byId = subcategoryRepository.findById(id);
        if (byId.isPresent()) {
            return ResponseEntity.ok(byId.get());
        }
        return ResponseEntity.notFound().build();
    }

}
