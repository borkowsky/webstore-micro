package net.rewerk.users.service.entity.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.exception.EntityExistsException;
import net.rewerk.users.dto.mapper.FavoriteDtoMapper;
import net.rewerk.webstore.dto.request.favorite.FavoriteCreateDto;
import net.rewerk.users.feign.client.CategoriesFeignClient;
import net.rewerk.users.feign.client.ProductsFeignClient;
import net.rewerk.users.repository.FavoriteRepository;
import net.rewerk.users.service.aggregator.FavoriteResponseAggregatorService;
import net.rewerk.users.service.entity.FavoriteService;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.dto.response.favorite.FavoriteResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Favorite;
import net.rewerk.webstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Favorite entity service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteDtoMapper favoriteDtoMapper;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteResponseAggregatorService favoriteResponseAggregatorService;
    private final ProductsFeignClient productsFeignClient;
    private final CategoriesFeignClient categoriesFeignClient;

    /**
     * Find Favorite entity by identifier
     *
     * @param id   Favorite identifier
     * @param user Authenticated user
     * @return Favorite entity
     */

    @Override
    public Favorite findById(Integer id, User user) {
        log.info("FavoriteServiceImpl.findById: Find favorite by id {}, user = {}", id, user);
        if (List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
            return favoriteRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("FavoriteServiceImpl.findById:" +
                                        " Cannot find favorite by id {}, user = {}, role admin or service",
                                id, user);
                        return new EntityNotFoundException("Favorite not found");
                    });
        } else {
            return favoriteRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> {
                        log.error("FavoriteServiceImpl.findById: Favorite with id {} was not found", id);
                        return new EntityNotFoundException("Favorite not found");
                    });
        }
    }

    /**
     * Find page of favorites by specification and pageable request
     *
     * @param specification Favorite JPA specification
     * @param pageable      Spring Pageable object
     * @return Page of Favorite response entity DTO
     */

    @Override
    public Page<FavoriteResponseDto> findAll(Specification<Favorite> specification, Pageable pageable) {
        log.info("FavoriteServiceImpl.findAll: page of favorites. specification and pageable = {}",
                pageable);
        return favoriteResponseAggregatorService.aggregate(favoriteRepository.findAll(specification, pageable));
    }

    /**
     * Find categories by favorites products
     *
     * @param specification Favorite JPA specification
     * @return List of Category entity response DTO
     */

    @Override
    public List<CategoryResponseDto> findAllCategories(Specification<Favorite> specification) {
        log.info("FavoriteServiceImpl.findAllCategories: find list of categories by specification");
        List<Favorite> baskets = favoriteRepository.findAll(specification);
        List<Integer> list = baskets
                .stream()
                .map(Favorite::getProductId)
                .toList();
        PayloadResponseDto<CategoryResponseDto> categoriesByProductIds = categoriesFeignClient
                .getCategoriesByProductIds(list);
        return categoriesByProductIds.getPayload();
    }

    /**
     * Create Favorite entity
     *
     * @param favoriteCreateDto DTO with create data
     * @param userId            Authenticated user identifier
     * @return Favorite entity response DTO
     * @throws EntityExistsException Favorite item already exists
     */

    @Override
    public FavoriteResponseDto create(FavoriteCreateDto favoriteCreateDto, UUID userId) throws EntityExistsException {
        log.info("FavoriteServiceImpl.create: Create favorite with id {}, userId = {}", favoriteCreateDto, userId);
        if (favoriteRepository.existsByUserIdAndProductId(
                userId, favoriteCreateDto.getProduct_id()
        )) {
            log.error("FavoriteServiceImpl.create: Favorite with id {} already exists", favoriteCreateDto);
            throw new EntityExistsException("Favorite already exists");
        }
        Favorite entity = favoriteDtoMapper.createDtoToFavorite(favoriteCreateDto);
        ProductResponseDto product = null;
        if (favoriteCreateDto.getProduct_id() != null) {
            SinglePayloadResponseDto<ProductResponseDto> productPayload = productsFeignClient
                    .getProduct(favoriteCreateDto.getProduct_id());
            product = productPayload.getPayload();
            entity.setProductId(product.getId());
        }
        entity.setUserId(userId);
        entity = favoriteRepository.save(entity);
        FavoriteResponseDto result = favoriteDtoMapper.toResponseDto(entity);
        if (product != null) {
            result.setProduct(product);
        }
        return result;
    }

    /**
     * Delete Favorite entity
     *
     * @param favorite Favorite entity to delete
     * @param userId   Authenticated user identifier
     */

    @Override
    public void delete(Favorite favorite, UUID userId) {
        log.info("FavoriteServiceImpl.delete: Delete favorite with id {}, userId = {}", favorite.getId(), userId);
        if (!Objects.equals(userId, favorite.getUserId())) {
            throw new EntityNotFoundException("Favorite not found");
        }
        favoriteRepository.delete(favorite);
    }
}
