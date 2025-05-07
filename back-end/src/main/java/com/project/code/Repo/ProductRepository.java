package com.project.code.Repo;


public interface ProductRepository {
package com.project.code.Repo;

import com.project.code.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 1. Retrieve all products
    List<Product> findAll();

    // 2. Retrieve products by category
    List<Product> findByCategory(String category);

    // 3. Retrieve products within a price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // 4. Retrieve product by SKU
    Product findBySku(String sku);

    // 5. Retrieve product by name
    Product findByName(String name);

    // 6. Retrieve products by name pattern for a specific store using custom query
    @Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND p.name LIKE %:pname%")
    List<Product> findByNameLike(Long storeId, String pname);
}



}
