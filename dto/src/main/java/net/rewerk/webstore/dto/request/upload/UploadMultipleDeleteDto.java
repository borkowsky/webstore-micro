package net.rewerk.webstore.dto.request.upload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.entity.UploadDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadMultipleDeleteDto {
    @NotNull(message = "{validation.upload.filenames.required}")
    @Size(
            min = 1,
            max = 16,
            message = "{validation.upload.filenames.range}"
    )
    private List<String> filenames;
    @NotNull(message = "{validation.upload.type.required}")
    private UploadDto.Type type;
}
