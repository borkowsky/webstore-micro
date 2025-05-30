package net.rewerk.users.service.aggregator;

import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.entity.Basket;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BasketResponseAggregatorService {
    BasketResponseDto aggregate(Basket basket);

    List<BasketResponseDto> aggregate(List<Basket> baskets);

    Page<BasketResponseDto > aggregate(Page<Basket> page);
}
