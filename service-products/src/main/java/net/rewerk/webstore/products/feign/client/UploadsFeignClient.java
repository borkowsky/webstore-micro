package net.rewerk.webstore.products.feign.client;

import net.rewerk.webstore.dto.request.upload.UploadMultipleDeleteDto;
import net.rewerk.webstore.products.feign.client.fallback.UploadsFeignClientFallback;
import net.rewerk.webstore.products.feign.configuration.DefaultConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client to provide operations with uploads objects
 *
 * @author rewerk
 */

@FeignClient(
        name = "uploads-client",
        url = "${services.uploads.base_uri}",
        configuration = {
                DefaultConfiguration.class
        },
        fallback = UploadsFeignClientFallback.class
)
public interface UploadsFeignClient {

    /**
     * Method for deleting upload objects
     *
     * @param dto DTO with information about objects to delete
     */

    @DeleteMapping
    void deleteUploads(@RequestBody UploadMultipleDeleteDto dto);
}
