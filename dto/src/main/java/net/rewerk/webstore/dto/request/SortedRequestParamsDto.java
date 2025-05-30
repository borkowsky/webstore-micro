package net.rewerk.webstore.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortedRequestParamsDto extends PaginatedRequestParamsDto {
    @NotNull(message = "Sort parameter required")
    private String sort = "id";
    @NotNull(message = "Order parameter required")
    private String order = "asc";
}
