package net.rewerk.webstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import net.rewerk.webstore.entity.meta.DeletableEntityMeta;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders_products")
public class OrdersProducts extends DeletableEntityMeta {
    @Column(
            nullable = false,
            updatable = false,
            name = "product_id"
    )
    private Integer productId;
    @JsonIgnore
    @Column(
            nullable = false,
            updatable = false,
            name = "order_id"
    )
    private Integer orderId;
    private Integer amount;
}
