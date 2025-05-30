package net.rewerk.webstore.products.repository;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import net.rewerk.webstore.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Category entity JPA repository
 *
 * @author rewerk
 */

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
    @NonNull
    Page<Category> findAll(@NonNull Pageable pageable);

    @NonNull
    Page<Category> findAll(@Nullable Specification<Category> specification, @NonNull Pageable pageable);

    List<Category> findDistinctByIdIn(Collection<Integer> ids);

    Page<Category> findAllByNameLikeIgnoreCaseAndEnabled(String name, Boolean enabled, Pageable pageable);
}
