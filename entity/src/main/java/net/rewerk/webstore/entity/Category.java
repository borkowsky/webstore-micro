package net.rewerk.webstore.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rewerk.webstore.entity.meta.EntityMeta;
import org.hibernate.annotations.Formula;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category extends EntityMeta {
    @Column(nullable = false)
    private String name;
    private String description;
    private String icon;
    private Integer categoryId;
    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.REFRESH,
                    CascadeType.MERGE,
                    CascadeType.REMOVE
            },
            mappedBy = "categoryId"
    )
    private List<Category> categories;
    @Formula("(select count(p.*) from products p where" +
            " (p.category_id in(select c.id from categories c where c.category_id = id)" +
            " or p.category_id = id) and p.deleted = false and p.enabled = true and p.balance > 0)")
    private Integer productsCount;
    private Boolean enabled;
}
