package net.rewerk.users.service.aggregator;

import net.rewerk.webstore.dto.response.favorite.FavoriteResponseDto;
import net.rewerk.webstore.entity.Favorite;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FavoriteResponseAggregatorService {
    FavoriteResponseDto aggregate(Favorite favorite);

    List<FavoriteResponseDto> aggregate(List<Favorite> baskets);

    Page<FavoriteResponseDto > aggregate(Page<Favorite> page);
}
