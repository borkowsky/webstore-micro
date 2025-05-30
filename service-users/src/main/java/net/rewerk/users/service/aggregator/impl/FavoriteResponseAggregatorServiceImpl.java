package net.rewerk.users.service.aggregator.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.users.dto.mapper.FavoriteDtoMapper;
import net.rewerk.users.feign.client.ProductsFeignClient;
import net.rewerk.users.service.aggregator.FavoriteResponseAggregatorService;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.favorite.FavoriteResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Data aggregate service for Favorite responses
 * Aggregated data: product
 *
 * @author rewerk
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteResponseAggregatorServiceImpl implements FavoriteResponseAggregatorService {
    private final FavoriteDtoMapper favoriteDtoMapper;
    private final ProductsFeignClient productsFeignClient;

    /**
     * Single Favorite entity response data aggregator
     *
     * @param favorite Favorite entity to aggregate
     * @return Favorite entity response DTO with aggregated data
     */

    @Override
    public FavoriteResponseDto aggregate(Favorite favorite) {
        log.info("FavoriteResponseAggregatorServiceImpl: Aggregating favorite response: {}", favorite);
        FavoriteResponseDto mapped = favoriteDtoMapper.toResponseDto(favorite);
        SinglePayloadResponseDto<ProductResponseDto> productDto = productsFeignClient.getProduct(mapped.getProductId());
        if (productDto != null && productDto.getPayload() != null) {
            mapped.setProduct(productDto.getPayload());
        }
        return mapped;
    }

    /**
     * List of Favorite entities response data aggregator
     *
     * @param favorites List of Favorite entities
     * @return List of Favorite entities response DTO with aggregated data
     */

    @Override
    public List<FavoriteResponseDto> aggregate(List<Favorite> favorites) {
        log.info("FavoriteResponseAggregatorServiceImpl: Aggregating list of favorites: SIZE = {}", favorites.size());
        List<FavoriteResponseDto> mappedList = favorites.stream()
                .map(favoriteDtoMapper::toResponseDto)
                .toList();
        PayloadResponseDto<ProductResponseDto> productsResponse = productsFeignClient
                .getProductsByIds(favorites.stream()
                        .map(Favorite::getProductId)
                        .distinct()
                        .toList()
                );
        if (productsResponse != null && productsResponse.getPayload() != null) {
            List<ProductResponseDto> products = productsResponse.getPayload();
            mappedList = mappedList.stream()
                    .peek(favoriteDto -> products.stream()
                            .filter(p -> favoriteDto.getProductId().equals(p.getId()))
                            .findFirst().ifPresent(favoriteDto::setProduct))
                    .toList();
        }
        return mappedList;
    }

    /**
     * Page of Favorite entities response data aggregator
     *
     * @param page Page of Favorite entities
     * @return Page of Favorite entities response DTO with aggregated data
     */

    @Override
    public Page<FavoriteResponseDto> aggregate(Page<Favorite> page) {
        log.info("FavoriteResponseAggregatorServiceImpl: Aggregating page of favorites: SIZE = {}", page.getSize());
        return new PageImpl<>(this.aggregate(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
