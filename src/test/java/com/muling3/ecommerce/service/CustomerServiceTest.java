package com.muling3.ecommerce.service;

import com.muling3.ecommerce.exceptions.CustomerNotFoundException;
import com.muling3.ecommerce.models.Cart;
import com.muling3.ecommerce.models.Customer;
import com.muling3.ecommerce.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
class CustomerServiceTest {

    private CustomerService service;
    @Mock
    CustomerRepository repository;

    Customer customer;

    @BeforeEach
    void setUp() {
        service = new CustomerService(repository);
        customer = Customer.builder()
                .id(102L)
                .userName("George Boole")
                .email("george.boole@gmail.com")
                .userPassword("12345")
                .carts(List.of(new Cart(1L, "cassava", 10,20.00, null, false, "This is dummy description"), new Cart(null, "mango", 5, 10.00,null, false, "This is dummy description"), new Cart(null, "oranges", 2, 5.00, null,false, "This is dummy description")))
                .build();
    }

    @Test
    @DisplayName("Should save customer")
    void shouldSaveCustomer() {
        when(repository.save(customer)).thenReturn(customer);
        Customer customer1 = service.saveCustomer(customer);

        assertEquals("George Boole", customer1.getUsername());
        assertEquals(102L, customer1.getId());
    }

    @Test
    @DisplayName("Should get all customers")
    void shouldGetAllCustomers() {
        when(repository.findAll()).thenReturn(List.of(customer));

        assertEquals(1, service.getCustomers().size());
    }

    @Test
    @DisplayName("Should get customer by id")
    void shouldGetCustomerById() throws CustomerNotFoundException {
        when(repository.findById(102L)).thenReturn(Optional.ofNullable(customer));
        Customer customer1 = service.getCustomerById(102L);

        assertEquals(102L, customer1.getId());
        assertEquals("George Boole", customer1.getUsername());
    }

    @Test
    @DisplayName("Should throw error getCustomerId")
    void shouldThrowErrorUponGetCustomerById() {
        when(repository.findById(102L)).thenReturn(Optional.ofNullable(customer));

        CustomerNotFoundException thrown = assertThrows(CustomerNotFoundException.class, () -> {
            service.getCustomerById(101L);
        });

        assertEquals("Customer with id 101 was not found", thrown.getMessage());
    }

    @Test
    @DisplayName("Should get customer by email")
    void shouldGetCustomerByEmail() throws CustomerNotFoundException {
        when(repository.findByEmail("george.boole@gmail.com")).thenReturn(Optional.ofNullable(customer));
        Customer customer1 = service.getCustomerByEmail("george.boole@gmail.com");

        assertEquals(102L, customer1.getId());
        assertEquals("George Boole", customer1.getUsername());
        assertEquals("george.boole@gmail.com", customer1.getEmail());
    }
    @Test
    @DisplayName("Should throw error customerByEmail")
    void shouldThrowErrorUponGetCustomerByEmail() {
        when(repository.findByEmail("george.boole@gmail.com")).thenReturn(Optional.ofNullable(customer));

        CustomerNotFoundException thrown = assertThrows(CustomerNotFoundException.class, () -> {
            service.getCustomerByEmail("George.Boole@gmail.com");
        });

        assertEquals("Customer with email George.Boole@gmail.com was not found", thrown.getMessage());
    }

    @Test
    @DisplayName("Should get customer by username")
    void shouldGetCustomerByName() throws CustomerNotFoundException {
        when(repository.findByUserName("George Boole")).thenReturn(Optional.ofNullable(customer));
        Customer customer1 = service.getCustomerByName("George Boole");

        assertEquals(102L, customer1.getId());
        assertEquals("George Boole", customer1.getUsername());
    }

    @Test
    @DisplayName("Should throw error customerByName")
    void shouldThrowErrorUponGetCustomerByName() {
        when(repository.findByUserName("George Boole")).thenReturn(Optional.ofNullable(customer));

        CustomerNotFoundException thrown = assertThrows(CustomerNotFoundException.class, () -> {
            service.getCustomerByName("Jane Marherita");
        });

        assertEquals("Customer by name Jane Marherita was not found", thrown.getMessage());
    }

    @Test
    @DisplayName("Should get customer cart")
    void shouldGetCustomerCart() throws CustomerNotFoundException {
        when(repository.findByUserName("George Boole")).thenReturn(Optional.ofNullable(customer));
        List<Cart> carts = service.getCustomerCart("George Boole");

        assertEquals(3, carts.size());
    }

    @Test
    @DisplayName("Should add cart product to customer")
    void shouldAddCartProductToCustomer() throws CustomerNotFoundException {
        when(repository.findByUserName("George Boole")).thenReturn(Optional.ofNullable(customer));
        service.addProductToCustomerCart("George Boole", new Cart(null, "tea", 12, 7.00, null,false, "This is dummy description"));

        List<Cart> carts = service.getCustomerCart("George Boole");

        assertEquals(4, carts.size());
    }

    @Test
    @DisplayName("Should remove cart product from customer")
    void shouldRemoveCartProductToCustomer() throws CustomerNotFoundException {
        when(repository.findByUserName("George Boole")).thenReturn(Optional.ofNullable(customer));
        service.removeProductFromCustomerCart("George Boole", "mango");

        List<Cart> carts = service.getCustomerCart("George Boole");

        assertEquals(2, carts.size());
    }

    @Test
    @DisplayName("Should change product quantity")
    void shouldChangeProductQuantityForUser() throws CustomerNotFoundException {
        when(repository.findByUserName("George Boole")).thenReturn(Optional.ofNullable(customer));
        service.addProductQuantityToCustomerCart("George Boole", "mango", 10);

        List<Cart> carts = service.getCustomerCart("George Boole");

        assertEquals(3, carts.size());
        assertEquals(10, carts.get(1).getProductQuantity());
    }

    @Test
    @DisplayName("Should change product quantity")
    void shouldSetCartItemAsDelivered() throws CustomerNotFoundException {
        when(repository.findByUserName("George Boole")).thenReturn(Optional.ofNullable(customer));
        service.setCartItemAsDelivered("George Boole", 1L);

        List<Cart> carts = service.getCustomerCart("George Boole");

        assertEquals(3, carts.size());
        assertEquals(true, carts.get(0).isDelivered());
    }
}