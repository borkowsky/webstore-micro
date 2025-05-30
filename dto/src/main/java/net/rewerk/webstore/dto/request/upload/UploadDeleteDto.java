package net.rewerk.webstore.dto.request.upload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.entity.UploadDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadDeleteDto {
    @NotNull(message = "{validation.upload.filename.required}")
    private String filename;
    @NotNull(message = "{validation.upload.type.required}")
    private UploadDto.Type type;
}
