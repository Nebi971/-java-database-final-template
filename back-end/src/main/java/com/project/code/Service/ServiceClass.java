package com.project.code.Service;


public class ServiceClass {
package com.project.code.Service;

import com.project.code.Entity.Inventory;
import com.project.code.Entity.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // 1. validateInventory Method
    public boolean validateInventory(Inventory inventory) {
        Inventory existingInventory = inventoryRepository.findByProductIdandStoreId(
            inventory.getProduct().getId(), inventory.getStore().getId());
        return existingInventory == null;
    }

    // 2. validateProduct Method
    public boolean validateProduct(Product product) {
        Product existingProduct = productRepository.findByName(product.getName());
        return existingProduct == null;
    }

    // 3. ValidateProductId Method
    public boolean validateProductId(long id) {
        return productRepository.findById(id).isPresent();
    }

    // 4. getInventoryId Method
    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdandStoreId(
            inventory.getProduct().getId(), inventory.getStore().getId());
    }
}


}
