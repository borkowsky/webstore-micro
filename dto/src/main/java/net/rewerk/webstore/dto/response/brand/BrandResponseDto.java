package net.rewerk.webstore.dto.response.brand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponseDto {
    private Integer id;
    private String name;
    private String image;
}
