package com.muling3.ecommerce.service;

import com.muling3.ecommerce.exceptions.ProductNotFoundException;
import com.muling3.ecommerce.models.Product;
import com.muling3.ecommerce.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ProductServiceTest {

    private ProductService service;
    @Mock
    ProductRepository repository;
    Product product;

    @BeforeEach
    void setUp() {
        service = new ProductService(repository);
        product = Product.builder()
                .id(12L)
                .name("Samsung A32")
                .quantity(23)
                .price(30_000.00)
                .imageUrl(null)
                .description("This is dummy description")
                .build();
    }

    @Test
    @DisplayName("Should save a given product")
    void shouldSaveProduct() {
        Mockito.when(repository.save(product)).thenReturn(product);

        Product p = service.saveProduct(product);
        assertEquals("Samsung A32", p.getName());
        assertEquals(23, p.getQuantity());
    }

    @Test
    @DisplayName("Should get all products")
    void shouldGetAllProducts() {
        Mockito.when(repository.findAll()).thenReturn(List.of(product));

        assertEquals(1, service.getProducts().size());
    }

    @Test
    @DisplayName("Should get a given product by id")
    void shouldGetProductById() throws ProductNotFoundException {
        Mockito.when(repository.findById(12L)).thenReturn(Optional.ofNullable(product));

        Product p = service.getProductById(12L);
        assertEquals(12L, p.getId());
        assertEquals("Samsung A32", p.getName());
        assertEquals(23, p.getQuantity());
    }

    @Test
    @DisplayName("Should throw an error given productId")
    void shouldThrowErrorUponGetProductById() {
        Mockito.when(repository.findById(12L)).thenReturn(Optional.ofNullable(product));

        ProductNotFoundException thrown= assertThrows(ProductNotFoundException.class, () -> service.getProductById(13L));
        assertEquals("Product with id 13 was not found", thrown.getMessage());
    }

    @Test
    @DisplayName("Should get product by given name")
    void shouldGetProductByName() throws ProductNotFoundException {
        Mockito.when(repository.findByName("Samsung A32")).thenReturn(Optional.ofNullable(product));

        Product p = service.getProductByName("Samsung A32");
        assertEquals(12L, p.getId());
        assertEquals("Samsung A32", p.getName());
        assertEquals(23, p.getQuantity());
    }

    @Test
    @DisplayName("Should throw an error given productName")
    void shouldThrowErrorUponGetProductByName() {
        Mockito.when(repository.findById(12L)).thenReturn(Optional.ofNullable(product));

        ProductNotFoundException thrown= assertThrows(ProductNotFoundException.class, () -> service.getProductByName("Samsung A52"));
        assertEquals("Product by name Samsung A52 was not found", thrown.getMessage());
    }
}
