package com.muling3.ecommerce.service;

import com.muling3.ecommerce.models.Customer;
import com.muling3.ecommerce.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerDetailsService implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customerDetails = customerRepository.findByUserName(username);
        return customerDetails.map(CustomerDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));
    }
}
