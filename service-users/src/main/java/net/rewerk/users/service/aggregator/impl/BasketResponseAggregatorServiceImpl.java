package net.rewerk.users.service.aggregator.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.users.dto.mapper.BasketDtoMapper;
import net.rewerk.users.feign.client.ProductsFeignClient;
import net.rewerk.users.service.aggregator.BasketResponseAggregatorService;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Basket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Data aggregate service for Basket responses
 * Aggregated data: product
 *
 * @author rewerk
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketResponseAggregatorServiceImpl implements BasketResponseAggregatorService {
    private final BasketDtoMapper basketDtoMapper;
    private final ProductsFeignClient productsFeignClient;

    /**
     * Single Basket entity response data aggregator
     *
     * @param basket Basket entity to aggregate
     * @return Basket entity response DTO with aggregated data
     */

    @Override
    public BasketResponseDto aggregate(Basket basket) {
        log.info("BasketResponseAggregatorServiceImpl.aggregate: Aggregate basket: {}", basket);
        BasketResponseDto mapped = basketDtoMapper.toResponseDto(basket);
        SinglePayloadResponseDto<ProductResponseDto> productDto = productsFeignClient.getProduct(mapped.getProductId());
        if (productDto != null && productDto.getPayload() != null) {
            mapped.setProduct(productDto.getPayload());
        }
        return mapped;
    }

    /**
     * List of Basket entities response data aggregator
     *
     * @param baskets List of Basket entities
     * @return List of Basket entities response DTO with aggregated data
     */

    @Override
    public List<BasketResponseDto> aggregate(List<Basket> baskets) {
        log.info("BasketResponseAggregatorServiceImpl.aggregate: Aggregate list of baskets: SIZE = {}", baskets.size());
        List<BasketResponseDto> mappedList = baskets.stream()
                .map(basketDtoMapper::toResponseDto)
                .toList();
        PayloadResponseDto<ProductResponseDto> productsResponse = productsFeignClient.getProductsByIds(baskets.stream()
                .map(Basket::getProductId)
                .distinct()
                .toList()
        );
        if (productsResponse != null && productsResponse.getPayload() != null) {
            List<ProductResponseDto> products = productsResponse.getPayload();
            mappedList = mappedList.stream()
                    .peek(basketDto -> products.stream()
                            .filter(p -> basketDto.getProductId().equals(p.getId()))
                            .findFirst().ifPresent(basketDto::setProduct))
                    .toList();
        }
        return mappedList;
    }

    /**
     * Page of Basket entities response data aggregator
     *
     * @param page Page of Basket entities
     * @return Page of Basket entities response DTO with aggregated data
     */

    @Override
    public Page<BasketResponseDto> aggregate(Page<Basket> page) {
        log.info("BasketResponseAggregatorServiceImpl.aggregate: Aggregate page of baskets: SIZE = {}", page.getSize());
        return new PageImpl<>(this.aggregate(page.getContent()), page.getPageable(), page.getTotalElements());
    }
}
