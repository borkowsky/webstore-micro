package net.rewerk.webstore.reviews.dto.mapper;

import net.rewerk.webstore.dto.response.review.ReviewResponseDto;
import net.rewerk.webstore.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReviewDtoMapper {
    ReviewResponseDto toDto(Review review);

    List<ReviewResponseDto> toDto(List<Review> reviews);
}
