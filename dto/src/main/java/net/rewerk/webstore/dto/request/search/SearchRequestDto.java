package net.rewerk.webstore.dto.request.search;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SearchRequestDto {
    @NotNull(message = "{validation.search.query.required}")
    @Size(
            min = 2,
            max = 32,
            message = "{validation.search.query.length}"
    )
    private String query;
}
