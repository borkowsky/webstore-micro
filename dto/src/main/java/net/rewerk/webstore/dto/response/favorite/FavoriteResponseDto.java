package net.rewerk.webstore.dto.response.favorite;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class FavoriteResponseDto {
    private Integer id;
    private UUID userId;
    private ProductResponseDto product;
    @JsonIgnore
    private Integer productId;
}
