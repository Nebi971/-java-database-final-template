package com.project.code.Controller;

import com.project.code.Model.*;
import com.project.code.Repository.*;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ServiceClass serviceClass;

    // 1. Update Inventory
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateInventory(@RequestBody CombinedRequest request) {
        Map<String, String> response = new HashMap<>();
        
        if (!serviceClass.isValidProductId(request.getProduct().getId())) {
            response.put("message", "Invalid Product ID");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Inventory> existingInventory = inventoryRepository.findByProductIdAndStoreId(
            request.getProduct().getId(), 
            request.getInventory().getStoreId()
        );

        if (existingInventory.isPresent()) {
            Inventory inventory = existingInventory.get();
            inventory.setStockLevel(request.getInventory().getStockLevel());
            inventoryRepository.save(inventory);
            response.put("message", "Inventory updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "No inventory found for this product/store");
            return ResponseEntity.notFound().build();
        }
    }

    // 2. Save New Inventory
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();
        
        Optional<Inventory> existing = inventoryRepository.findByProductIdAndStoreId(
            inventory.getProduct().getId(), 
            inventory.getStoreId()
        );

        if (existing.isPresent()) {
            response.put("message", "Inventory already exists for this product/store");
            return ResponseEntity.badRequest().body(response);
        } else {
            inventoryRepository.save(inventory);
            response.put("message", "Inventory saved successfully");
            return ResponseEntity.ok(response);
        }
    }

    // 3. Get All Products for a Store
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Map<String, List<Product>>> getAllProducts(@PathVariable int storeId) {
        List<Product> products = productRepository.findByStoreId(storeId);
        return ResponseEntity.ok(Map.of("products", products));
    }

    // 4. Filter Products by Category/Name
    @GetMapping("/filter")
    public ResponseEntity<Map<String, List<Product>>> getProductName(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String name) {
        
        List<Product> products;
        if ("null".equals(category) && "null".equals(name)) {
            products = productRepository.findAll();
        } else if ("null".equals(category)) {
            products = productRepository.findByNameContaining(name);
        } else if ("null".equals(name)) {
            products = productRepository.findByCategory(category);
        } else {
            products = productRepository.findByCategoryAndNameContaining(category, name);
        }
        
        return ResponseEntity.ok(Map.of("product", products));
    }

    // 5. Search Product in Store
    @GetMapping("/search")
    public ResponseEntity<Map<String, List<Product>>> searchProduct(
            @RequestParam String name,
            @RequestParam int storeId) {
        
        List<Product> products = productRepository.findByNameContainingAndStoreId(name, storeId);
        return ResponseEntity.ok(Map.of("product", products));
    }

    // 6. Remove Product
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Map<String, String>> removeProduct(@PathVariable Long productId) {
        Map<String, String> response = new HashMap<>();
        
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            inventoryRepository.deleteByProductId(productId);
            productRepository.deleteById(productId);
            response.put("message", "Product and associated inventory deleted");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Product not found");
            return ResponseEntity.notFound().build();
        }
    }

    // 7. Validate Quantity
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateQuantity(
            @RequestParam Long productId,
            @RequestParam int storeId,
            @RequestParam int requestedQuantity) {
        
        Optional<Inventory> inventory = inventoryRepository.findByProductIdAndStoreId(productId, storeId);
        return ResponseEntity.ok(
            inventory.isPresent() && 
            inventory.get().getStockLevel() >= requestedQuantity
        );
    }
}

// Supporting Classes
class CombinedRequest {
    private Product product;
    private Inventory inventory;
    // Getters and setters
}
