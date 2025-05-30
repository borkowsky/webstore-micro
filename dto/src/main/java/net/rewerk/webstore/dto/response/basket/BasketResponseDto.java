package net.rewerk.webstore.dto.response.basket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketResponseDto {
    private Integer id;
    private UUID userId;
    private ProductResponseDto product;
    @JsonIgnore
    private Integer productId;
    private Integer amount;
    @JsonProperty("can_inc_amount")
    private Boolean canIncAmount() {
        return (this.amount + 1) <= this.product.getBalance();
    }
    @JsonProperty("can_dec_amount")
    private Boolean canDecAmount() {
        return this.amount > 1;
    }
}
