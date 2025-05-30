package net.rewerk.webstore.orders.feign.client.fallback;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.request.basket.BasketMultipleDeleteDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.orders.feign.client.BasketFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class BasketFeignClientFallback implements BasketFeignClient {
    @Override
    public PayloadResponseDto<BasketResponseDto> getBasketsByIdsAndUserId(List<Integer> ids) {
        log.error("BasketFeignClient: fallback getBasketsByIdsAndUserId called, ids: {}", ids);
        throw new EntityNotFoundException("Baskets not found");
    }

    @Override
    public void deleteBasketsByIds(BasketMultipleDeleteDto basketMultipleDeleteDto) {
        log.error("BasketFeignClient: fallback deleteBasketsByIds called, dto: {}", basketMultipleDeleteDto);
        throw new RuntimeException("Failed to delete baskets");
    }
}
