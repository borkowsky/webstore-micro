package net.rewerk.users.service.entity;

import net.rewerk.webstore.dto.request.favorite.FavoriteCreateDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.dto.response.favorite.FavoriteResponseDto;
import net.rewerk.webstore.entity.Favorite;
import net.rewerk.webstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface FavoriteService {
     FavoriteResponseDto create(FavoriteCreateDto favoriteCreateDto, UUID userId);

     void delete(Favorite favorite, UUID userId);

     Favorite findById(Integer id, User user);

     Page<FavoriteResponseDto> findAll(Specification<Favorite> specification, Pageable pageable);

     List<CategoryResponseDto> findAllCategories(Specification<Favorite> specification);
}
