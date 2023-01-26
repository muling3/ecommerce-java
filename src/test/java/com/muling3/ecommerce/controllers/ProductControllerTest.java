package com.muling3.ecommerce.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muling3.ecommerce.models.Product;
import com.muling3.ecommerce.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {
    @MockBean
    private ProductService service;

    @Autowired
    MockMvc mockMvc;

    private Product product;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Should create a product")
    void shouldCreateAndReturnTheProduct() throws Exception {
        Mockito.when(service.saveProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .content(asJsonString(product))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect((MockMvcResultMatchers.jsonPath("$.name").value("Samsung A32")))
                .andExpect((MockMvcResultMatchers.jsonPath("$.quantity").value(23)));
    }
    public static String asJsonString(Product prod){
        try{
            return new ObjectMapper().writeValueAsString(prod);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should get a list of products")
    void shouldReturnListOfProducts() throws Exception {
        Mockito.when(service.getProducts()).thenReturn(List.of(product));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Samsung A32")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].quantity", Matchers.is(23)));
    }

    @Test
    @DisplayName("Should get a product by given name")
    void shouldReturnAProductById() throws Exception {
        Mockito.when(service.getProductById(12L)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{id}", 12)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(12))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Samsung A32"));
    }

    @Test
    @DisplayName("Should throw an error upon getById")
    void shouldThrowErrorUponProductById() throws Exception {
        Mockito.when(service.getProductById(12L)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{id}", 11)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Should get a product by given id")
    void shouldReturnAProductByName() throws Exception {
        Mockito.when(service.getProductByName("Samsung A32")).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/name/{name}", "Samsung A32")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(12))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Samsung A32"));
    }

    @Test
    @DisplayName("Should throw an error upon getByName")
    void shouldThrowErrorUponProductByName() throws Exception {
        Mockito.when(service.getProductByName("Samsung A32")).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/name/{name}", "Invalid name")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }
}