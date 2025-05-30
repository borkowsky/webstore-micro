package net.rewerk.webstore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import net.rewerk.webstore.entity.meta.DeletableEntityMeta;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "basket")
public class Basket extends DeletableEntityMeta {
    @Column(nullable = false, updatable = false)
    private UUID userId;
    private Integer productId;
    @Column(nullable = false)
    private Integer amount;

    @JsonProperty("can_inc_amount")
    private Boolean canIncAmount() {
        return true; //(this.amount + 1) <= this.product.getBalance();
    }

    @JsonProperty("can_dec_amount")
    private Boolean canDecAmount() {
        return this.amount > 1;
    }
}
