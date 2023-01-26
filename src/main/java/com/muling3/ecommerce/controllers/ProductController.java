package com.muling3.ecommerce.controllers;

import com.muling3.ecommerce.exceptions.ProductNotFoundException;
import com.muling3.ecommerce.models.Product;
import com.muling3.ecommerce.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService service) {
        this.productService = service;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product){
        Product p = productService.saveProduct(product);
        return new ResponseEntity<>(p, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getProducts(){
        return ResponseEntity.ok().body(productService.getProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) throws ProductNotFoundException {
        Product p = productService.getProductById(id);
        if(p == null){
            throw new ProductNotFoundException("Product with id "+id+" was not found");
        }
        return ResponseEntity.ok().body(p);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Product> getProductByName(@PathVariable String name) throws ProductNotFoundException {
        Product p = productService.getProductByName(name);
        if(p == null){
            throw new ProductNotFoundException("Product by name "+name+" was not found");
        }
        return ResponseEntity.ok().body(p);
    }
}