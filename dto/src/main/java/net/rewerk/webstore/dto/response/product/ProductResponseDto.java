package net.rewerk.webstore.dto.response.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.response.brand.BrandResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Integer id;
    private String name;
    private String description;
    private CategoryResponseDto category;
    private Double price;
    private Double discountPrice;
    private Double rating;
    private Integer balance;
    private BrandResponseDto brand;
    private List<String> images;
    private List<String> tags;
    private Boolean enabled;
    @JsonProperty("available")
    public boolean isAvailable() {
        return this.balance > 0 && this.enabled;
    }
}
