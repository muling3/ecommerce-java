package com.muling3.ecommerce.controllers;

import com.muling3.ecommerce.exceptions.CustomerNotFoundException;
import com.muling3.ecommerce.models.Customer;
import com.muling3.ecommerce.models.utils.AuthResponse;
import com.muling3.ecommerce.models.utils.LoginRequest;
import com.muling3.ecommerce.models.utils.ResetRequest;
import com.muling3.ecommerce.service.utils.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTService jwtService;
    @Autowired
    private CustomerController customerController;

    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestBody Customer customer, HttpServletRequest request){
        customer.setUserPassword(passwordEncoder.encode(customer.getUserPassword()));
        return customerController.createCustomer(customer, request);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getUserPassword()));

        if(authentication.isAuthenticated()){
            return ResponseEntity.ok(new AuthResponse(loginRequest.getUserName(), jwtService.generateToken(loginRequest.getUserName())));
        }else{
            throw new UsernameNotFoundException("Customer not found");
        }
    }

    @GetMapping("/confirmEmail")
    public ModelAndView confirmUserEmail(@RequestParam String token){
        Boolean isValid = customerController.confirmCustomerRegistrationEmail(token);

        ModelAndView modelAndView = new ModelAndView();

        if (!isValid) {
            modelAndView.setViewName("confirm_email_invalid");
            return modelAndView;
        }

        modelAndView.setViewName("confirm_email");
        return modelAndView;
    }

    @GetMapping("/resendConfirmation")
    public ResponseEntity<String> resendEmailConfirmation(@RequestParam String email, HttpServletRequest request) throws CustomerNotFoundException {
        return customerController.resendEmailConfirmation(email, request);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetCustomerPassword(@RequestBody ResetRequest request) throws CustomerNotFoundException {
        return customerController.resetPassword(request);
    }
}
