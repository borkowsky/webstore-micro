package net.rewerk.webstore.products.feign.client.fallback;

import lombok.extern.slf4j.Slf4j;
import net.rewerk.exception.CloudFileNotFound;
import net.rewerk.webstore.dto.request.upload.UploadMultipleDeleteDto;
import net.rewerk.webstore.products.feign.client.UploadsFeignClient;
import org.springframework.stereotype.Component;

/**
 * Fallback class for UploadsFeignClient
 *
 * @author rewerk
 */

@Component
@Slf4j
public class UploadsFeignClientFallback implements UploadsFeignClient {
    @Override
    public void deleteUploads(UploadMultipleDeleteDto dto) {
        log.error("UploadsFeignClient: deleteUploads fallback called. DTO: {}", dto);
        throw new CloudFileNotFound("Failed to delete uploads");
    }
}
