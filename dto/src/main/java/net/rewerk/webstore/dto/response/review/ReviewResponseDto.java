package net.rewerk.webstore.dto.response.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.dto.response.user.UserResponseDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Integer id;
    private Integer rating;
    private String text;
    @JsonIgnore
    private UUID userId;
    private UserResponseDto user;
    private ProductResponseDto product;
    private List<String> images;
    private Integer orderId;
    private Date createdAt;
    private Date updatedAt;
}
