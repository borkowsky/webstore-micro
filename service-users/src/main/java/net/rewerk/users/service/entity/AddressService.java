package net.rewerk.users.service.entity;

import net.rewerk.webstore.dto.request.address.AddressCreateDto;
import net.rewerk.webstore.dto.request.address.AddressPatchDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.entity.Address;
import net.rewerk.webstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    Address findById(Integer id, User user);

    List<AddressResponseDto> findAllByIds(List<Integer> ids);

    Page<AddressResponseDto> findAll(Specification<Address> specification, Pageable pageable);

    AddressResponseDto create(AddressCreateDto dto, UUID userId);

    void update(Address address, AddressPatchDto dto, UUID userId);

    void delete(Address address, UUID userId);
}
