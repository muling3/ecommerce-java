package com.muling3.ecommerce.service;

import com.muling3.ecommerce.models.Product;
import com.muling3.ecommerce.repositories.ProductRepository;
import com.muling3.ecommerce.exceptions.ProductNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Product saveProduct(Product product){
        return repository.save(product);
    }

    public List<Product> getProducts(){
        return repository.findAll();
    }

    public Product getProductById(Long id) throws ProductNotFoundException {
        System.out.println("Provided id: "+id);
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id "+id+" was not found"));
    }

    public Product getProductByName(String name) throws ProductNotFoundException {
        return  repository.findByName(name).orElseThrow(() -> new ProductNotFoundException("Product by name "+name+" was not found"));
    }
}
