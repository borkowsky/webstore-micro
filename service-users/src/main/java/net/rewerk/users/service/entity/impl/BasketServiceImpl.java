package net.rewerk.users.service.entity.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.exception.EntityExistsException;
import net.rewerk.exception.UnprocessableOperation;
import net.rewerk.users.dto.mapper.BasketDtoMapper;
import net.rewerk.webstore.dto.request.basket.BasketCreateDto;
import net.rewerk.webstore.dto.request.basket.BasketMultipleDeleteDto;
import net.rewerk.webstore.dto.request.basket.BasketPatchDto;
import net.rewerk.users.feign.client.CategoriesFeignClient;
import net.rewerk.users.feign.client.ProductsFeignClient;
import net.rewerk.users.repository.BasketRepository;
import net.rewerk.users.service.aggregator.BasketResponseAggregatorService;
import net.rewerk.users.service.entity.BasketService;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.basket.BasketResponseDto;
import net.rewerk.webstore.dto.response.category.CategoryResponseDto;
import net.rewerk.webstore.dto.response.product.ProductResponseDto;
import net.rewerk.webstore.entity.Basket;
import net.rewerk.webstore.entity.User;
import net.rewerk.webstore.utility.CommonUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Basket entity service implementation
 *
 * @author rewerk
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BasketServiceImpl implements BasketService {
    private final BasketDtoMapper basketDtoMapper;
    private final BasketRepository basketRepository;
    private final ProductsFeignClient productsFeignClient;
    private final CategoriesFeignClient categoriesFeignClient;
    private final BasketResponseAggregatorService basketResponseAggregatorService;

    /**
     * Create Basket entity
     *
     * @param dto    DTO with create data
     * @param userId Authenticated user identifier
     * @return Basket entity response DTO
     * @throws EntityExistsException Basket already exists
     */

    @Override
    public BasketResponseDto create(@NonNull BasketCreateDto dto, @NonNull UUID userId) throws EntityExistsException {
        log.info("BasketServiceImpl.create: dto = {}, userID = {}", dto, userId);
        if (basketRepository.existsByProductIdAndUserId(dto.getProduct_id(), userId)) {
            log.error("BasketServiceImpl.create: Basket already exists");
            throw new EntityExistsException("Basket already exists");
        }
        SinglePayloadResponseDto<ProductResponseDto> productPayload = productsFeignClient.getProduct(dto.getProduct_id());
        Basket basket = basketDtoMapper.createDtoToBasket(dto);
        basket.setProductId(productPayload.getPayload().getId());
        basket.setUserId(userId);
        BasketResponseDto result = basketDtoMapper.toResponseDto(basketRepository.save(basket));
        result.setProduct(productPayload.getPayload());
        return result;
    }

    /**
     * Update Basket entity
     *
     * @param basket Basket entity to update
     * @param dto    DTO with update data
     * @param userId Authenticated user identifier
     */

    @Override
    public void update(@NonNull Basket basket,
                       @NonNull BasketPatchDto dto,
                       @NonNull UUID userId) {
        log.info("BasketServiceImpl.update: basket.id = {}, dto = {}, userID = {}", basket.getId(), dto, userId);
        if (!Objects.equals(basket.getUserId(), userId)) {
            log.error("BasketServiceImpl.update: Basket does not belong to user");
            throw new EntityNotFoundException("Basket not found");
        }
        if (dto.getAmount() != null) {
            SinglePayloadResponseDto<ProductResponseDto> productPayload = productsFeignClient
                    .getProduct(basket.getProductId());
            if (productPayload.getPayload().getBalance() < dto.getAmount()) {
                log.error("BasketServiceImpl.update: Products quantity limit exceeded");
                throw new UnprocessableOperation("Products quantity limit exceeded");
            }
            basket.setAmount(dto.getAmount());
        }
        basketRepository.save(basket);
    }

    /**
     * Delete Basket entity
     *
     * @param basket Basket entity to delete
     * @param userId Authenticated user identifier
     * @throws EntityNotFoundException Basket not found, basket item not found
     */

    @Override
    public void delete(@NonNull Basket basket, @NonNull UUID userId) throws EntityNotFoundException {
        log.info("BasketServiceImpl.delete: basket.id = {}, userID = {}", basket.getId(), userId);
        if (!Objects.equals(userId, basket.getUserId())) {
            log.error("BasketServiceImpl.delete: Basket does not belong to user");
            throw new EntityNotFoundException("Basket not found");
        }
        basketRepository.findById(basket.getId())
                .ifPresentOrElse(basketRepository::delete, () -> {
                    log.error("BasketServiceImpl.delete: Basket does not exist");
                    throw new EntityNotFoundException("Basket item not found");
                });
    }

    /**
     * Delete all baskets by list of identifiers
     *
     * @param dto  DTO with delete data
     * @param user Authenticated user
     */

    @Override
    public void deleteAllById(@NonNull BasketMultipleDeleteDto dto, @NonNull User user) {
        log.info("BasketServiceImpl.deleteAllById: dto = {}, user = {}", dto, user);
        UUID userId = user.getId();
        if (dto.getUser_id() != null && List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
            userId = dto.getUser_id();
        }
        List<Basket> baskets = basketRepository.findAllByIdInAndUserId(
                dto.getBasket_ids(), userId
        );
        basketRepository.deleteAllById(baskets.stream()
                .filter(Objects::nonNull)
                .map(Basket::getId)
                .collect(Collectors.toList())
        );
    }

    /**
     * Find Basket entity by identifier with access check
     *
     * @param id   Basket identifier
     * @param user Authenticated user
     * @return Basket entity
     * @throws EntityNotFoundException Basket item not found
     */

    @Override
    public Basket findBasketById(@NonNull Integer id, User user) throws EntityNotFoundException {
        log.info("BasketServiceImpl.findBasketById: id = {}, user = {}", id, user);
        if (List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
            return basketRepository.findById(id).orElseThrow(
                    () -> {
                        log.error("BasketServiceImpl.findBasketById: Basket does not exist. role admin or service");
                        return new EntityNotFoundException("Basket item not found");
                    }
            );
        } else {
            return basketRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                    () -> {
                        log.error("BasketServiceImpl.findBasketById: Basket does not exist.");
                        return new EntityNotFoundException("Basket item not found");
                    }
            );
        }

    }

    /**
     * Find list of categories by basket products
     *
     * @param specification Basket JPA specification
     * @return List of Category entity response DTO
     */

    @Override
    public List<CategoryResponseDto> findAllCategories(@NonNull Specification<Basket> specification) {
        log.info("BasketServiceImpl.findAllCategories");
        List<Basket> baskets = basketRepository.findAll(specification);
        PayloadResponseDto<CategoryResponseDto> result = categoriesFeignClient.getCategoriesByProductIds(baskets
                .stream()
                .map(Basket::getProductId)
                .toList());
        return result.getPayload();
    }

    /**
     * Find baskets by list of identifiers
     *
     * @param ids List of Basket identifiers
     * @return List of Basket entity response DTO
     */

    @Override
    public List<BasketResponseDto> findAllById(@NonNull List<Integer> ids) {
        log.info("BasketServiceImpl.findAllById: ids = {}", ids);
        return basketResponseAggregatorService.aggregate(basketRepository.findAllById(ids));
    }

    /**
     * Find page of baskets by specification and pageable request
     *
     * @param specification Basket JPA specification
     * @param pageable      Spring Pageable object
     * @return Page of Basket entity response DTO
     */

    @Override
    public Page<BasketResponseDto> findAll(@NonNull Specification<Basket> specification, @NonNull Pageable pageable) {
        log.info("BasketServiceImpl.findAll: specification and pageable = {}", pageable);
        return basketResponseAggregatorService.aggregate(basketRepository.findAll(specification, pageable));
    }

    /**
     * Sync local saved basket
     *
     * @param productIds List of Product identifiers
     * @param user       Authenticated user
     */

    @Override
    public void sync(@NonNull List<Integer> productIds,
                     @NonNull User user
    ) {
        log.info("BasketServiceImpl.sync: sync local saved basket productIds = {}, user = {}", productIds, user);
        List<Basket> basketIds = basketRepository.findAllByProductIdInAndUserId(productIds, user.getId());
        List<Integer> missingProducts = CommonUtils.lostItemsInList(productIds, basketIds.stream()
                .map(Basket::getProductId)
                .toList());
        if (!missingProducts.isEmpty()) {
            missingProducts.forEach(productId -> {
                this.create(BasketCreateDto.builder()
                        .amount(1)
                        .product_id(productId)
                        .build(), user.getId());
            });
        }
    }
}
