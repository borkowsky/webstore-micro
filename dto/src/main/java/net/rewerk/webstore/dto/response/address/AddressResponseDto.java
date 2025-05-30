package net.rewerk.webstore.dto.response.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {
    private Integer id;
    private String address;
    private String city;
    private String region;
    private Integer postalCode;
    private String country;
    private UUID userId;
}
