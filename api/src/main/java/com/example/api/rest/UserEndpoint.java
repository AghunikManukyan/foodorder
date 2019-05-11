package com.example.api.rest;

import com.example.api.dto.JwtAuthRequestDto;
import com.example.api.dto.JwtAuthResponseDto;
import com.example.api.security.CurrentUser;
import com.example.api.util.JwtTokenUtil;
import com.example.foodorder.common.model.*;
import com.example.foodorder.common.repository.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserEndpoint {
    @Value("${image.upload.dir}")
    private String imageUploadDir;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductsRepository productsRepository;

    private User user;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    //////User

    @PostMapping("/addUser")
    @ApiOperation(value = "Create User", response = User.class)
    @ApiResponses({
            @ApiResponse(code = 409, message = "User with email already exists"),
            @ApiResponse(code = 200, message = "User created")
    })
    public ResponseEntity add(@RequestBody User user, @RequestParam("picture") MultipartFile file) throws IOException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);
        user.setPicUrl(fileName);

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }


    @PostMapping("/auth")
    public ResponseEntity auth(@RequestBody JwtAuthRequestDto authRequestDto) {
        String email = authRequestDto.getEmail();
        User byEmail = userRepository.findByEmail(email);
        if (byEmail != null) {
            User user = byEmail;
            if (passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
                String token = jwtTokenUtil.generateToken(user.getEmail());
                JwtAuthResponseDto response = JwtAuthResponseDto.builder()
                        .token(token)
                        .user(user)
                        .build();
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }

    @PostMapping("/user/addAddress/{id}")

    public ResponseEntity addAddress(@RequestBody Address address, @PathVariable("id") int id) {

        User one = userRepository.getOne(id);
        if (one == null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        if (addressRepository.findAddressByUser(one) == null) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
        address.setUser(one);
        addressRepository.save(address);
        return ResponseEntity.ok(user);
    }


    @PutMapping("/user/update")
    public ResponseEntity update(@RequestBody User user) {
        if (userRepository.findById(user.getId()).isPresent()) {
            userRepository.save(user);
            return ResponseEntity
                    .ok(user);
        }
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/user/updateAddress")
    public ResponseEntity update(@RequestBody Address address) {
        if (addressRepository.findById(address.getId()).isPresent()) {
            addressRepository.save(address);
            return ResponseEntity
                    .ok().build();
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/user/all")
    public ResponseEntity getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getById(@PathVariable("id") int id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            return ResponseEntity.ok(byId.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteById(@PathVariable("id") int id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity
                    .ok()
                    .build();
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/user/home")
    public ResponseEntity addHome() {
        this.user = user;
        return ResponseEntity
                .ok(user);
    }


    @GetMapping("/productCartByUser/{id}")
    public ResponseEntity<List<Products>> addProductCart(@PathVariable("id") int id) {
        List<Products> allByUserId = productsRepository.findAllByUserId(id);
        if (allByUserId != null) {
            userRepository.deleteById(id);
            return ResponseEntity
                    .ok(allByUserId);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/user/checkout")
    public ResponseEntity getCheckout( @AuthenticationPrincipal CurrentUser springUser) {



        List<Products> allByUserId = productsRepository.findAllByUserId(springUser.getUser().getId());
        if (allByUserId == null) {
            return ResponseEntity.notFound().build();

        }

        List<Products> productsByStatus = new LinkedList<>();

        for (Products products : allByUserId) {
            if (!products.isStatus()) {
                productsByStatus.add(products);
            }

        }

        return ResponseEntity
                .ok(allByUserId);



    }
    @GetMapping("/user/account")
    public ResponseEntity userAccount(@AuthenticationPrincipal CurrentUser springUser) {
        Address addressByUser = addressRepository.findAddressByUser(springUser.getUser());


        return ResponseEntity
                .ok(springUser.getUser());

    }
    @GetMapping("/user/accountAddress")
    public ResponseEntity userAccountAddress(@AuthenticationPrincipal CurrentUser springUser) {
        Address addressByUser = addressRepository.findAddressByUser(springUser.getUser());
        return ResponseEntity
                .ok(addressByUser);

    }



    /////Admin


    @PostMapping("/admin/addProduct")
    @ApiOperation(value = "Create Product", response = Product.class)
    @ApiResponses({
            @ApiResponse(code = 409, message = "Product with name already exists"),
            @ApiResponse(code = 200, message = "Product created")
    })
    public ResponseEntity addProduct(@RequestBody Product product, @RequestParam("picture") MultipartFile file) throws IOException {
        Optional<Product> productByName = productRepository.findProductByName(product.getName());
        if (productByName.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File picture = new File(imageUploadDir + File.separator + fileName);
        file.transferTo(picture);
        product.setPicUrl(fileName);

        product.setDate(new Date());
        productRepository.save(product);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/admin/contact/all")
    public ResponseEntity getAllContact() {
        return ResponseEntity.ok(contactRepository.findAll());
    }


    @PostMapping("/addContact")
    public ResponseEntity addProduct(@RequestBody Contact contact) throws IOException {

        contactRepository.save(contact);
        return ResponseEntity.ok().build();

    }
}