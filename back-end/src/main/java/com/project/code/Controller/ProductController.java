package com.project.code.Controller;

import com.project.code.Model.Product;
import com.project.code.Repository.InventoryRepository;
import com.project.code.Repository.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepository;

    // 1. Add Product
    @PostMapping
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (serviceClass.validateProduct(product)) {
                Product savedProduct = productRepository.save(product);
                response.put("product", savedProduct);
                response.put("message", "Product added successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Invalid product data");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (DataIntegrityViolationException e) {
            response.put("error", "Product already exists or invalid data");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 2. Get Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductbyId(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(Map.of("products", product.get()));
        }
        return ResponseEntity.notFound().build();
    }

    // 3. Update Product
    @PutMapping
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody Product product) {
        if (productRepository.existsById(product.getId())) {
            productRepository.save(product);
            return ResponseEntity.ok(Map.of("message", "Product updated successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // 4. Filter by Category/Name
    @GetMapping("/category/{name}/{category}")
    public ResponseEntity<Map<String, Object>> filterbyCategoryProduct(
            @PathVariable String name,
            @PathVariable String category) {
        
        List<Product> products;
        if ("null".equals(name) && "null".equals(category)) {
            products = productRepository.findAll();
        } else if ("null".equals(name)) {
            products = productRepository.findByCategory(category);
        } else if ("null".equals(category)) {
            products = productRepository.findByNameContaining(name);
        } else {
            products = productRepository.findByNameContainingAndCategory(name, category);
        }
        
        return ResponseEntity.ok(Map.of("products", products));
    }

    // 5. List All Products
    @GetMapping
    public ResponseEntity<Map<String, Object>> listProduct() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(Map.of("products", products));
    }

    // 6. Get Products by Category and Store
    @GetMapping("/filter/{category}/{storeid}")
    public ResponseEntity<Map<String, Object>> getProductbyCategoryAndStoreId(
            @PathVariable String category,
            @PathVariable int storeid) {
        
        List<Product> products = productRepository.findByCategoryAndStoreId(category, storeid);
        return ResponseEntity.ok(Map.of("product", products));
    }

    // 7. Delete Product
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        if (serviceClass.validateProductId(id)) {
            inventoryRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }

    // 8. Search Product by Name
    @GetMapping("/searchProduct/{name}")
    public ResponseEntity<Map<String, Object>> searchProduct(@PathVariable String name) {
        List<Product> products = productRepository.findByNameContaining(name);
        return ResponseEntity.ok(Map.of("products", products));
    }
}
