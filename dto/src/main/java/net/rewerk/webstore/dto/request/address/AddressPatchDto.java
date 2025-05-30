package net.rewerk.webstore.dto.request.address;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressPatchDto {
    @Size(
            min = 6,
            max = 128,
            message = "{validation.address.address.length}"
    )
    private String address;
    @Size(
            min = 3,
            max = 128,
            message = "{validation.address.city.length}"
    )
    private String city;
    @Size(
            min = 3,
            max = 128,
            message = "{validation.address.region.length}"
    )
    private String region;
    @Size(
            min = 1,
            max = 4,
            message = "{validation.address.country.length}"
    )
    private String country;
    private Integer postalCode;
}
