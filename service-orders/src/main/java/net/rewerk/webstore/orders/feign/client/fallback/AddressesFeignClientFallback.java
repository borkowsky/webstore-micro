package net.rewerk.webstore.orders.feign.client.fallback;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.orders.feign.client.AddressesFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class AddressesFeignClientFallback implements AddressesFeignClient {
    @Override
    public PaginatedPayloadResponseDto<AddressResponseDto> getAddresses(UUID userId) {
        log.error("AddressesFeignClient: called getAddresses fallback, userId: {}", userId);
        throw new EntityNotFoundException("Addresses not found");
    }

    @Override
    public SinglePayloadResponseDto<AddressResponseDto> getAddress(Integer id) {
        log.error("AddressesFeignClient: called getAddress fallback, id: {}", id);
        throw new EntityNotFoundException("Address not found");
    }

    @Override
    public PayloadResponseDto<AddressResponseDto> getAddressesById(List<Integer> ids) {
        log.error("AddressesFeignClient: called getAddressesById fallback, ids: {}", ids);
        throw new EntityNotFoundException("Addresses not found");
    }
}
