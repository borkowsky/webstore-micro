package net.rewerk.webstore.products.repository;

import lombok.NonNull;
import net.rewerk.webstore.entity.Brand;
import net.rewerk.webstore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Brand entity JPA repository
 *
 * @author rewerk
 */

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer>, JpaSpecificationExecutor<Brand> {
    @NonNull
    Optional<Brand> findById(@NonNull Integer id);

    boolean existsByName(String name);

    @Query("select distinct p.brand from Product p where p.category.id = :categoryId")
    List<Brand> findDistinctByProductCategoryId(Integer categoryId);

    @Query("select distinct p.category from Product p where p.brand.id = :productBrandId")
    List<Category> findDistinctCategoriesByProductBrandId(Integer productBrandId);

    Page<Brand> findAllByNameLikeIgnoreCase(String name, Pageable pageable);
}
