package net.rewerk.users.service.entity.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.users.dto.mapper.AddressDtoMapper;
import net.rewerk.webstore.dto.request.address.AddressCreateDto;
import net.rewerk.webstore.dto.request.address.AddressPatchDto;
import net.rewerk.users.repository.AddressRepository;
import net.rewerk.users.service.entity.AddressService;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.entity.Address;
import net.rewerk.webstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Address entity service implementation
 *
 * @author rewerk
 */

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AddressServiceImpl implements AddressService {
    private final AddressDtoMapper addressDtoMapper;
    private final AddressRepository addressRepository;

    /**
     * Find Address entity by identifier
     *
     * @param id   Address identifier
     * @param user Authenticated user
     * @return Address entity
     */

    @Override
    public Address findById(@NonNull Integer id, @NonNull User user) {
        log.info("AddressServiceImpl.findById: Find address by id {}, user: {}", id, user);
        if (List.of(User.Role.ROLE_ADMIN, User.Role.ROLE_SERVICE).contains(user.getRole())) {
            return addressRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("AddressServiceImpl.findById: Could not find address by id {}," +
                                        " admin or service role user",
                                id);
                        return new EntityNotFoundException("Address not found");
                    });
        } else {
            return addressRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> {
                        log.error("AddressServiceImpl.findById: Could not find address by id {}", id);
                        return new EntityNotFoundException("Address not found");
                    });
        }

    }

    /**
     * Find list of addresses by list of identifiers
     *
     * @param ids List of Address identifiers
     * @return List of Address entity response DTO
     */

    @Override
    public List<AddressResponseDto> findAllByIds(List<Integer> ids) {
        log.info("AddressServiceImpl.findAllByIds: Find list of addresses by ids {}", ids);
        return addressDtoMapper.toDto(addressRepository.findAllById(ids));
    }

    /**
     * Find page of addresses by JPA specification and Spring Pageable object
     *
     * @param specification Address JPA specification
     * @param pageable      Spring Pageable object
     * @return Page of Address entity response DTO
     */

    @Override
    public Page<AddressResponseDto> findAll(Specification<Address> specification, Pageable pageable) {
        log.info("AddressServiceImpl.findAll: Find page of addresses by specification");
        Page<Address> page = addressRepository.findAll(specification, pageable);
        return new PageImpl<>(addressDtoMapper.toDto(page.getContent()), pageable, page.getTotalElements());
    }

    /**
     * Create Address entity
     *
     * @param dto    DTO with create data
     * @param userId Authenticated user identifier
     * @return Address entity response DTO
     */

    @Override
    public AddressResponseDto create(@NonNull AddressCreateDto dto, @NonNull UUID userId) {
        log.info("AddressServiceImpl.create: Create address {}, userId = {}", dto, userId);
        Address mapped = addressDtoMapper.createDtoToAddress(dto);
        mapped.setUserId(userId);
        return addressDtoMapper.toDto(addressRepository.save(mapped));
    }

    /**
     * Update Address entity
     *
     * @param address         Address entity to update
     * @param addressPatchDto DTO with patch data
     * @param userId          Authenticated user identifier
     */

    @Override
    public void update(@NonNull Address address,
                       @NonNull AddressPatchDto addressPatchDto,
                       @NonNull UUID userId) {
        log.info("AddressServiceImpl.update: Update address {}, userId = {}", address, userId);
        if (!Objects.equals(address.getUserId(), userId)) {
            log.error("AddressServiceImpl.update: User id {} does not match address user id {}",
                    address.getUserId(), userId);
            throw new EntityNotFoundException("Address not found");
        }
        addressRepository.save(addressDtoMapper.patchDtoToAddress(address, addressPatchDto));
    }

    /**
     * Delete Address entity
     *
     * @param address Address entity to delete
     * @param userId  Authenticated user identifier
     */

    @Override
    public void delete(@NonNull Address address, @NonNull UUID userId) {
        log.info("AddressServiceImpl.delete: Delete address {}, userId = {}", address, userId);
        if (!Objects.equals(address.getUserId(), userId)) {
            log.error("AddressServiceImpl.delete: address not found");
            throw new EntityNotFoundException("Address not found");
        }
        addressRepository.delete(address);
    }
}
