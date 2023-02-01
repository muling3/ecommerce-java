package com.muling3.ecommerce.controllers;

import com.muling3.ecommerce.models.Customer;
import com.muling3.ecommerce.models.utils.LoginRequest;
import com.muling3.ecommerce.models.utils.RegisterResponse;
import com.muling3.ecommerce.repositories.CustomerRepository;
import com.muling3.ecommerce.security.utils.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerCustomer(@RequestBody Customer customer){
        customer.setUserPassword(passwordEncoder.encode(customer.getUserPassword()));
        Customer customer1 = customerRepository.save(customer);
        String token = jwtService.generateToken(customer1);
        return ResponseEntity.ok(new RegisterResponse(customer.getUsername(), token));
    }

    @PostMapping("/login")
    public ResponseEntity<RegisterResponse> loginCustomer(@RequestBody LoginRequest loginRequest){
        Customer customer = customerRepository.findByUserName(loginRequest.getUserName()).get();

        log.info("Username {} and password are {}", loginRequest.getUserName(), loginRequest.getUserPassword());
        //check if password matches the username
        if(passwordEncoder.matches(loginRequest.getUserPassword(), customer.getUserPassword())){
            String token = jwtService.generateToken(customer);
            log.info("Token generated is {}", token);
            return ResponseEntity.ok(new RegisterResponse(loginRequest.getUserName(), token));
        }
        return ResponseEntity.ok(new RegisterResponse("No customer", "For token"));
    }

}
