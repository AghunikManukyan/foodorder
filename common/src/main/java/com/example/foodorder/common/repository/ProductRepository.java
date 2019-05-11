package com.example.foodorder.common.repository;

import com.example.foodorder.common.model.Product;
import com.example.foodorder.common.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    List<Product> findAllBySubcategoryId(int subcategoryId);

//    @Modifying
//    @Transactional
//    @Query(value = "INSERT INTO product_picture(product_id,picture_id) VALUES(:productId,:pictureId)", nativeQuery = true)
//    void addPicture(@Param("productId") int productId, @Param("pictureId") int pictureId);


    List<Product> findTop4BySubcategory(Subcategory subcategory);

    Optional<Product> findProductByName(String name);

    List<Product> findAllByMenuId(int menuId);
}
