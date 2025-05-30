package net.rewerk.webstore.dto.mq.uploads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rewerk.webstore.dto.entity.UploadDto;

import java.util.List;

/**
 * DTO to send messages to delete upload objects SCS Queue
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadsDeleteObjectsDto {
    /**
     * List of object names to delete
     */
    private List<String> object_names;
    /**
     * Upload type
     */
    private UploadDto.Type type;
}
