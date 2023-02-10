package com.muling3.ecommerce.service;

import com.muling3.ecommerce.exceptions.CustomerNotFoundException;
import com.muling3.ecommerce.models.Cart;
import com.muling3.ecommerce.models.Customer;
import com.muling3.ecommerce.models.EmailConfirmation;
import com.muling3.ecommerce.repositories.CustomerRepository;
import com.muling3.ecommerce.repositories.EmailConfirmationRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService{

    private final CustomerRepository customerRepository;

    @Autowired
    private EmailConfirmationService emailService;

    public CustomerService(CustomerRepository repository){
        this.customerRepository = repository;
    }

    @Autowired
    private EmailConfirmationRepository confirmationEmailRepo;

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByEmail(String email) throws CustomerNotFoundException {
        return customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer with email " + email + " was not found"));
    }

    public Customer getCustomerById(Long id) throws CustomerNotFoundException {
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " was not found"));
    }

    public Customer getCustomerByName(String name) throws CustomerNotFoundException {
        return customerRepository.findByUserName(name).orElseThrow(() -> new CustomerNotFoundException("Customer by name " + name + " was not found"));
    }

    public List<Cart> getCustomerCart(String customerName) throws CustomerNotFoundException {
        //get the customer from the db
        Customer customer = getCustomerByName(customerName);

        return customer.getCarts();
    }

    public List<Cart> addProductToCustomerCart(String customerName, Cart cart) {
        //get the customer from the db
        Customer customer = customerRepository.findByUserName(customerName).orElse(null);

        assert customer != null;
        List<Cart> currentList = new ArrayList<>(customer.getCarts());
        currentList.add(cart);
        customer.setCarts(currentList);
        return customer.getCarts();
    }

    public List<Cart> removeProductFromCustomerCart(String customerName, String productName) {
        //get the customer from the db
        Customer customer = customerRepository.findByUserName(customerName).orElse(null);

        assert customer != null;
        customer.getCarts().stream().filter(c -> (c.getProductName().equals(productName))).findFirst().orElse(null);
        List<Cart> cart = customer.getCarts().stream().filter(c -> !(c.getProductName().equals(productName))).collect(Collectors.toList());

        customer.setCarts(cart);
//        cartRepository.deleteById(tobeRemoved.getProductId());

        return customer.getCarts();
    }

    public List<Cart> addProductQuantityToCustomerCart(String username, String productName, int quantity) {
        //get the customer from the db
        Customer customer = customerRepository.findByUserName(username).orElse(null);

        assert customer != null;
        List<Cart> currentList = customer.getCarts().stream().peek(cart -> {
            if(cart.getProductName().equals(productName)){
                cart.setProductQuantity(quantity);
            }
        }).collect(Collectors.toList());

        customer.setCarts(currentList);
        return customer.getCarts();
    }

    public List<Cart> setCartItemAsDelivered(String username, Long id) {
        //get the customer from the db
        Customer customer = customerRepository.findByUserName(username).orElse(null);

        assert customer != null;

        List<Cart> currentList = customer.getCarts().stream().peek(cart -> {
            if(cart.getProductId() == id){
                cart.setDelivered(true);
            }
        }).collect(Collectors.toList());

        customer.setCarts(currentList);
        return customer.getCarts();
    }

    public void saveCustomerConfirmationToken(Customer customer, String token) {
        EmailConfirmation emailConfirmation = new EmailConfirmation(token, customer);
        confirmationEmailRepo.save(emailConfirmation);
    }

    public Boolean validateCustomerToken(String token) {
        //check if the token exists
        Optional<EmailConfirmation> emailConfirmation = confirmationEmailRepo.findByToken(token);
        if (emailConfirmation.isEmpty()){
            return false;
        }

        //check if the token has expired
        if ((emailConfirmation.get().getExpirationTime().getTime() - new Date().getTime() )<= 0){
            return false;
        }

        //set the customer account as enabled
        Optional<Customer> customer = customerRepository.findById(emailConfirmation.get().getCustomer().getId());
        if (customer.isEmpty()){
            return false;
        }else{
            customer.get().setEnabled(true);
            customerRepository.save(customer.get());
        }
        return true;
    }

    public String resendConfirmationEmailLink(String email, String appUrl) throws CustomerNotFoundException {
        //check if the provided email exists
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isEmpty()){
            throw new CustomerNotFoundException("Customer with email " + email + " was not found");
        }

        //create a new token
        String token = UUID.randomUUID().toString();

        //create and send link to customer
        String link = "http://"+appUrl+"confirmEmail?token="+token;
        try {
            emailService.sendVerificationEmail(customer.get(), link);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return link;
    }

    public void resetCustomerPassword(String userEmail, String newPassword) throws CustomerNotFoundException {
        Optional<Customer> customer = customerRepository.findByEmail(userEmail);
        if (customer.isPresent()){
            customer.get().setUserPassword(newPassword);
            customerRepository.save(customer.get());
        }else {
            throw new CustomerNotFoundException("Customer Not Found");
        }
    }
}
