package net.rewerk.webstore.dto.response.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadSignUrlResponseDto {
    private String uploadURL;
    private String publicURL;
}
