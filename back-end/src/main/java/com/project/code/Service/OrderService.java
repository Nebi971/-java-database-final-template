package com.project.code.Service;

import com.project.code.DTO.PlaceOrderRequestDTO;
import com.project.code.Entity.*;
import com.project.code.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        // 2. Retrieve or Create the Customer
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getCustomer().getEmail());
        if (customer == null) {
            customer = customerRepository.save(placeOrderRequest.getCustomer());
        }

        // 3. Retrieve the Store
        Store store = storeRepository.findById(placeOrderRequest.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found with ID: " + placeOrderRequest.getStoreId()));

        // 4. Create OrderDetails
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setOrderDate(LocalDateTime.now());
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetails = orderDetailsRepository.save(orderDetails);

        // 5. Create and Save OrderItems
        for (PlaceOrderRequestDTO.OrderItemDTO itemDTO : placeOrderRequest.getItems()) {
            // Find Inventory
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(itemDTO.getProductId(), store.getId());
            if (inventory == null || inventory.getQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("Not enough stock for product ID: " + itemDTO.getProductId());
            }

            // Update Inventory
            inventory.setQuantity(inventory.getQuantity() - itemDTO.getQuantity());
            inventoryRepository.save(inventory);

            // Save Order Item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(orderDetails);
            orderItem.setProduct(inventory.getProduct());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(itemDTO.getUnitPrice());

            orderItemRepository.save(orderItem);
        }
    }
}

