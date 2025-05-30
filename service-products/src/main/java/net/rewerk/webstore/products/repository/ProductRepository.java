package net.rewerk.webstore.products.repository;

import net.rewerk.webstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product entity JPA repository
 *
 * @author rewerk
 */

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    @Query("from Product p where p.rating > 0.0 " +
            "and p.enabled = true and p.balance > 0" +
            " and p.category.enabled = true order by p.rating desc limit 20")
    List<Product> findTopPopularProducts();

    Page<Product> findAllByNameLikeIgnoreCaseAndEnabledAndBalanceGreaterThanAndCategoryEnabled(
            String name,
            Boolean enabled,
            Integer balanceIsGreaterThan,
            Boolean categoryEnabled,
            Pageable pageable
    );

    List<Product> findAllByIdInAndCategoryEnabledAndBalanceGreaterThanAndEnabled(List<Integer> ids,
                                                                                 Boolean categoryEnabled,
                                                                                 Integer minBalance,
                                                                                 Boolean productEnabled
                                                                                 );
}
