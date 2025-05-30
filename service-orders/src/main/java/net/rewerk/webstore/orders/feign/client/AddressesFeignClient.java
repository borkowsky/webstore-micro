package net.rewerk.webstore.orders.feign.client;

import net.rewerk.webstore.dto.response.PaginatedPayloadResponseDto;
import net.rewerk.webstore.dto.response.PayloadResponseDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.address.AddressResponseDto;
import net.rewerk.webstore.orders.feign.client.fallback.AddressesFeignClientFallback;
import net.rewerk.webstore.orders.feign.configuration.DefaultConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * Feign client for retrieve Address data
 *
 * @author rewerk
 */

@FeignClient(
        name = "addresses-client",
        url = "${services.addresses.base_uri}",
        configuration = {
                DefaultConfiguration.class
        },
        fallback = AddressesFeignClientFallback.class
)
public interface AddressesFeignClient {

    /**
     * Cacheable method for get addresses by userId
     *
     * @param userId User identifier
     * @return Paginated payload of AddressResponseDto
     */
    
    @Cacheable(
            value = "caffeine",
            key = "'addresses-client-getAddresses' + #userId"
    )
    @GetMapping
    PaginatedPayloadResponseDto<AddressResponseDto> getAddresses(
            @RequestParam(name = "user_id") UUID userId
    );


    /**
     * Cacheable method for get address by id
     *
     * @param id Address identifier
     * @return Single payload of AddressResponseDto
     */

    @Cacheable(
            value = "caffeine",
            key = "'addresses-client-getAddress' + #id"
    )
    @GetMapping("{id:\\d+}")
    SinglePayloadResponseDto<AddressResponseDto> getAddress(
            @PathVariable Integer id
    );

    /**
     * Cacheable method for get list of addresses by id
     *
     * @param ids List of addresses identifiers
     * @return Payload response of AddressResponseDto
     */

    @Cacheable(
            value = "caffeine",
            key = "'addresses-client-getAddressesById' + #ids"
    )
    @GetMapping("by_ids")
    PayloadResponseDto<AddressResponseDto> getAddressesById(
            @RequestParam List<Integer> ids
    );
}
