package net.rewerk.webstore.dto.request.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchPatchRequestDto {
    Map<Integer, ProductPatchDto> data;
}
