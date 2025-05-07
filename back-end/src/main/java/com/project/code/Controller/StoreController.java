package com.project.code.Controller;

import com.project.code.Model.Store;
import com.project.code.Model.DTO.PlaceOrderRequestDTO;
import com.project.code.Repository.StoreRepository;
import com.project.code.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private OrderService orderService;

    // 1. Add Store
    @PostMapping
    public ResponseEntity<Map<String, String>> addStore(@RequestBody Store store) {
        Map<String, String> response = new HashMap<>();
        try {
            Store savedStore = storeRepository.save(store);
            response.put("message", "Store created successfully with ID: " + savedStore.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to create store: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 2. Validate Store
    @GetMapping("/validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {
        return storeRepository.existsById(storeId);
    }

    // 3. Place Order
    @PostMapping("/placeOrder")
    public ResponseEntity<Map<String, String>> placeOrder(@RequestBody PlaceOrderRequestDTO orderRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            // Validate store exists
            if (!storeRepository.existsById(orderRequest.getStoreId())) {
                response.put("Error", "Store not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Process order through service
            String result = orderService.processOrder(orderRequest);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("Error", "Failed to place order: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
