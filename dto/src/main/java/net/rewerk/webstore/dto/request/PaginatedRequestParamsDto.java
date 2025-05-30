package net.rewerk.webstore.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedRequestParamsDto {
    @NotNull(message = "Page parameter required")
    @Min(value = 0, message = "Page parameter can not be less than 0")
    @Max(value = Integer.MAX_VALUE, message = "Page parameter can not be greater than {max}")
    protected Integer page = 0;
    @NotNull(message = "Limit parameter required")
    @Min(value = 0, message = "Limit parameter can not be less than 0")
    @Max(value = 500, message = "Limit parameter can not be greater than 500")
    protected Integer limit = 20;
}
