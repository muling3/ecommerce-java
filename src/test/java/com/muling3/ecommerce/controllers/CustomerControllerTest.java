package com.muling3.ecommerce.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muling3.ecommerce.models.Cart;
import com.muling3.ecommerce.models.Customer;
import com.muling3.ecommerce.models.utils.AddCartRequest;
import com.muling3.ecommerce.models.utils.AddQuantityRequest;
import com.muling3.ecommerce.models.utils.RemoveCartProductRequest;
import com.muling3.ecommerce.models.utils.SetProductDelivered;
import com.muling3.ecommerce.service.CustomerService;
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

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @MockBean
    private CustomerService service;
    @Autowired
    private MockMvc mockMvc;

    private Customer customer;
    List<Cart> cartList = List.of(new Cart(null, "cassava", 10, 20.00, null, false,"This is dummy description"), new Cart(null, "mango", 5, 10.00, null, false, "This is dummy description"), new Cart(null, "oranges", 2, 5.00, null, false, "This is dummy description"));
    Cart cartItem = new Cart(null, "sukali", 12, 40.00, null, false, "This is dummy description");
    List<Cart> afterAddCarts = List.of(new Cart(null, "cassava", 10, 20.00, null, false, "This is dummy description"), new Cart(null, "mango", 5, 10.00, null, false, "This is dummy description"), new Cart(null, "oranges", 2, 5.00, null, false, "This is dummy description"), cartItem);
    List<Cart> afterRemoveCarts = List.of(new Cart(null, "mango", 5, 10.00, null,false, "This is dummy description"), new Cart(null, "oranges", 2, 5.00, null, false, "This is dummy description"));
    List<Cart> afterChangeQuantity = List.of(new Cart(null, "mango", 20, 10.00, null,false, "This is dummy description"), new Cart(null, "oranges", 2, 5.00, null, false, "This is dummy description"));
    List<Cart> afterChangeDelivered = List.of(new Cart(1L, "mango", 20, 10.00, null, true, "This is dummy description"), new Cart(null, "oranges", 2, 5.00, null, false, "This is dummy description"));
    AddCartRequest addCartRequest = new AddCartRequest("Roy Fielding", cartItem);
    RemoveCartProductRequest removeCartRequest = new RemoveCartProductRequest("Roy Fielding", "cassava");

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(102L)
                .userName("Roy Fielding")
                .email("roy.fielding@hotmail.com")
                .password("12345")
                .carts(cartList)
                .build();
    }

    @Test
    @DisplayName("Should create and return the customer")
    void shouldCreateCustomer() throws Exception  {
        Mockito.when(service.saveCustomer(any(Customer.class))).thenReturn(customer);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/customers")
                        .content(asJsonString(customer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("location", "http://localhost/api/customers/102"));
    }

    private static String asJsonString(Customer c) {
        try {
            return new ObjectMapper().writeValueAsString(c);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should return a list of customers")
    void shouldGetCustomers() throws Exception {
        Mockito.when(service.getCustomers()).thenReturn(List.of(customer));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/customers"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(102)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userName", Matchers.is("Roy Fielding")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", Matchers.is("roy.fielding@hotmail.com")));
    }

    @Test
    @DisplayName("Should get customer by Email")
    void shouldGetCustomerByEmail() throws Exception {
        Mockito.when(service.getCustomerByEmail("roy.fielding@hotmail.com")).thenReturn(customer);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/email/{email}", "roy.fielding@hotmail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(102)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is("roy.fielding@hotmail.com")));
    }

    @Test
    @DisplayName("Should get customer by Id")
    void shouldGetCustomerById() throws Exception {
        Mockito.when(service.getCustomerById(102L)).thenReturn(customer);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/{id}", 102)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(102)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName", Matchers.is("Roy Fielding")));
    }

    @Test
    @DisplayName("Should throw error getCustomerById")
    void shouldThrowErrorGetCustomerById() throws Exception {
        Mockito.when(service.getCustomerById(102L)).thenReturn(customer);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("FAILED")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Should get customer by name")
    void shouldGetCustomerByName() throws Exception {
        Mockito.when(service.getCustomerByName("Roy Fielding")).thenReturn(customer);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/name/{name}", "Roy Fielding")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(102)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName", Matchers.is("Roy Fielding")));
    }

    @Test
    @DisplayName("Should throw error getCustomerByName")
    void shouldThrowErrorGetCustomerByName() throws Exception {
        Mockito.when(service.getCustomerByName("Roy Fielding")).thenReturn(customer);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/name/{name}", "Roy Fieldig")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is("FAILED")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Should return customer Carts")
    void shouldGetCustomerCartsByName() throws Exception {
        Mockito.when(service.getCustomerCart("Roy Fielding")).thenReturn(cartList);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/customers/cart/{user}", "Roy Fielding")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(3)));
    }

    @Test
    @DisplayName("Should add cart Item to customer carts")
    void shouldAddCartItemToCustomerCarts() throws Exception {
        Mockito.when(service.addProductToCustomerCart("Roy Fielding", cartItem)).thenReturn(afterAddCarts);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/customers/cart/add")
                        .content(addCartItemToJsonString(addCartRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(4)));
    }
    private static String addCartItemToJsonString(AddCartRequest request){
        try {
            return new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should remove cart Item from customer carts")
    void shouldRemoveCartItemFromCustomerCarts() throws Exception {
        Mockito.when(service.removeProductFromCustomerCart("Roy Fielding", "cassava")).thenReturn(afterRemoveCarts);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/customers/cart/remove")
                        .content(removeCartItemToJsonString(removeCartRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)));
    }
    private static String removeCartItemToJsonString(RemoveCartProductRequest request){
        try {
            return new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should change productQuantity for customer cart item")
    void shouldChangeProductQuantity() throws Exception {
        Mockito.when(service.addProductQuantityToCustomerCart("Roy Fielding", "mango", 20)).thenReturn(afterChangeQuantity);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/customers/cart/add-quantity")
                        .content(addCartItemQuantityToJsonString(new AddQuantityRequest(20, "mango","Roy Fielding")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productQuantity", Matchers.is(20)));
    }
    private static String addCartItemQuantityToJsonString(AddQuantityRequest request){
        try {
            return new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Should set product as delivered")
    void shouldSetProductDelivered() throws Exception {
        Mockito.when(service.setCartItemAsDelivered("Roy Fielding", 1L)).thenReturn(afterChangeDelivered);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/customers/cart/deliver")
                        .content(setProductDeliveredToJsonString(new SetProductDelivered("Roy Fielding", 1L)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].delivered", Matchers.is(true)));
    }
    private static String setProductDeliveredToJsonString(SetProductDelivered request){
        try {
            return new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}