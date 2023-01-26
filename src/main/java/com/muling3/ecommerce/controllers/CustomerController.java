package com.muling3.ecommerce.controllers;

import com.muling3.ecommerce.event.CustomerRegistrationEvent;
import com.muling3.ecommerce.exceptions.CustomerNotFoundException;
import com.muling3.ecommerce.models.Cart;
import com.muling3.ecommerce.models.Customer;
import com.muling3.ecommerce.models.utils.*;
import com.muling3.ecommerce.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    private ApplicationEventPublisher publisher;

    public CustomerController(CustomerService service) {
        this.customerService = service;
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer, HttpServletRequest request) {
        Customer c = customerService.saveCustomer(customer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(c.getId()).toUri();

        //create an event
        publisher.publishEvent(new CustomerRegistrationEvent(c, createAppUrl(request)));
        return ResponseEntity.created(location).build();
    }

    private String createAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getServletPath();
    }

    @GetMapping("/confirmEmail")
    public ResponseEntity<String> confirmCustomerRegistrationEmail(@RequestParam String token) {
        Boolean isValid = customerService.validateCustomerToken(token);
        if (!isValid) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        return ResponseEntity.ok("Email successfully confirmed");
    }

    @GetMapping("/resendConfirmation")
    public ResponseEntity resendEmailConfirmation(@RequestParam String email, HttpServletRequest request) throws CustomerNotFoundException {
        String link = customerService.resendConfirmationEmailLink(email, createAppUrl(request));

        if(link == null){
            throw new CustomerNotFoundException("Customer with email " + email + " was not found");
        }

        return ResponseEntity.ok(link);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetRequest resetRequest) throws CustomerNotFoundException {
        customerService.resetCustomerPassword(resetRequest.getUserEmail(), resetRequest.getNewPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers() {
        return ResponseEntity.ok().body(customerService.getCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) throws CustomerNotFoundException {
        Customer c = customerService.getCustomerById(id);

        if(c == null){
            throw new CustomerNotFoundException("Customer with id " + id + " was not found");
        }
        return ResponseEntity.ok().body(c);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Customer> getCustomerByName(@PathVariable String name) throws CustomerNotFoundException {
        Customer c = customerService.getCustomerByName(name);

        if(c == null){
            throw new CustomerNotFoundException("Customer by name " + name + " was not found");
        }
        return ResponseEntity.ok().body(c);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Customer> getCustomerByEmail(@PathVariable String email) throws CustomerNotFoundException {
        return ResponseEntity.ok().body(customerService.getCustomerByEmail(email));
    }

    @GetMapping("/cart/{user}")
    public ResponseEntity<List<Cart>> getCustomersCart(@PathVariable String user) throws CustomerNotFoundException {
        return ResponseEntity.ok().body(customerService.getCustomerCart(user));
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addCartProductToCustomer(@RequestBody AddCartRequest cartRequest) {
        List<Cart> carts = customerService.addProductToCustomerCart(cartRequest.getUsername(), cartRequest.getCart());
        return ResponseEntity.created(null).body(carts);
    }

    @PostMapping("/cart/remove")
    public ResponseEntity<?> removeCartProductFromCustomer(@RequestBody RemoveCartProductRequest cartRequest) {
        List<Cart> carts = customerService.removeProductFromCustomerCart(cartRequest.getUsername(), cartRequest.getProductName());
        return ResponseEntity.created(null).body(carts);
    }

    @PostMapping("/cart/add-quantity")
    public ResponseEntity<?> addCartQuantity(@RequestBody AddQuantityRequest request) {
        List<Cart> carts = customerService.addProductQuantityToCustomerCart(request.getUserName(), request.getProductName(), request.getQuantity());
        return ResponseEntity.created(null).body(carts);
    }

    @PostMapping("/cart/deliver")
    public ResponseEntity<?> setCartItemAsDelivered(@RequestBody SetProductDelivered request) {
        List<Cart> carts = customerService.setCartItemAsDelivered(request.getUsername(), request.getProductId());
        return ResponseEntity.created(null).body(carts);
    }
}
