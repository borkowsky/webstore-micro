package net.rewerk.users.service.entity;

import net.rewerk.webstore.dto.request.basket.BasketCreateDto;
import net.rewerk.webstore.dto.request.basket.BasketMultipleDeleteDto;
import net.rewerk.webstore.dto.request.basket.BasketPatchDto;
import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.entity.Basket;
import net.rewerk.webstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface BasketService {
    BasketResponseDto create(BasketCreateDto dto, UUID userId);

    void update(Basket basket, BasketPatchDto dto, UUID userId);

    void delete(Basket basket, UUID userId);

    void deleteAllById(BasketMultipleDeleteDto dto, User user);

    Basket findBasketById(Integer id, User user);

    List<CategoryResponseDto> findAllCategories(Specification<Basket> specification);

    List<BasketResponseDto> findAllById(List<Integer> ids);

    Page<BasketResponseDto> findAll(Specification<Basket> specification, Pageable pageable);

    void sync(List<Integer> productIds, User user);
}