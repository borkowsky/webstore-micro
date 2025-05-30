package net.rewerk.webstore.dto.request.address;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressCreateDto {
    @NotNull(message = "{validation.address.address.required}")
    @Size(
            min = 6,
            max = 128,
            message = "{validation.address.address.length}"
    )
    private String address;
    @NotNull(message = "{validation.address.city.required}")
    @Size(
            min = 3,
            max = 128,
            message = "{validation.address.city.length}"
    )
    private String city;
    @NotNull(message = "{validation.address.region.required}")
    @Size(
            min = 3,
            max = 128,
            message = "{validation.address.region.length}"
    )
    private String region;
    @NotNull(message = "{validation.address.country.required}")
    @Size(
            min = 1,
            max = 4,
            message = "{validation.address.country.length}"
    )
    private String country;
    @NotNull(message = "{validation.address.postal_code.required}")
    private Integer postalCode;
}
