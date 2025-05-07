package com.project.code.Repo;


public interface InventoryRepository {
package com.project.code.Repo;

import com.project.code.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Custom query: Find inventory by product ID and store ID
    Inventory findByProductIdAndStoreId(Long productId, Long storeId);

    // Find all inventory records for a specific store
    List<Inventory> findByStore_Id(Long storeId);

    // Delete all inventory records for a specific product
    @Modifying
    @Transactional
    void deleteByProductId(Long productId);

    // Optional: Custom query using @Query for more control
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId")
    Inventory getInventoryByProductAndStore(Long productId, Long storeId);
}




}
