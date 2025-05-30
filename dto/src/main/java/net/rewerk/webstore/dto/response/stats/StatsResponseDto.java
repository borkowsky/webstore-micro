package net.rewerk.webstore.dto.response.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsResponseDto {
    private Long users;
    private Long orders;
    private Long reviews;
    private Double paid;
    private Date createdAt;
    private Date updatedAt;
}
