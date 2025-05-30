package net.rewerk.webstore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import net.rewerk.webstore.entity.meta.EntityMeta;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product extends EntityMeta {
    private String name;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;
    private Double price;
    private Double discountPrice;
    private Double rating;
    private Integer balance;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    private List<String> images;
    private List<String> tags;
    private Boolean enabled;

    @JsonProperty("available")
    public boolean isAvailable() {
        return this.balance > 0 && this.enabled;
    }
}
