package net.rewerk.webstore.uploads.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rewerk.exception.OperationInterruptedException;
import net.rewerk.webstore.dto.request.upload.UploadDeleteDto;
import net.rewerk.webstore.dto.request.upload.UploadMultipleDeleteDto;
import net.rewerk.webstore.dto.request.upload.UploadSignUrlDto;
import net.rewerk.webstore.dto.response.SinglePayloadResponseDto;
import net.rewerk.webstore.dto.response.upload.UploadSignUrlResponseDto;
import net.rewerk.webstore.uploads.service.UploadService;
import net.rewerk.webstore.utility.ResponseUtils;
import net.rewerk.webstore.utility.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Uploads service
 *
 * @author rewerk
 */

@RequestMapping("/api/v1/uploads")
@RestController
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    /**
     * POST endpoint for create Google Cloud Storage signed URL for provide uploads
     *
     * @param signUrlDto     DTO with sign url parameters
     * @param jwt            OAuth2 jwt token
     * @param authentication Spring Security authentication object
     * @return Response with status 200 OK and single payload with response DTO
     */

    @PostMapping("/sign-url")
    public ResponseEntity<SinglePayloadResponseDto<UploadSignUrlResponseDto>> signUploadUrl(
            @Valid @RequestBody UploadSignUrlDto signUrlDto,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        return ResponseUtils.createSingleResponse(uploadService.signUploadURL(
                signUrlDto, SecurityUtils.getUserDtoFromJwtToken(jwt, authentication)
        ));
    }

    /**
     * DELETE endpoint for delete uploaded object from Google Cloud Storage
     *
     * @param request DTO with parameters
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping
    public ResponseEntity<Void> deleteUpload(
            @Valid @RequestBody UploadDeleteDto request
    ) {
        uploadService.deleteObject(request.getFilename(), request.getType());
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE endpoint for provide multiple objects deletion from Google Cloud Storage
     *
     * @param request DTO with parameters
     * @return Response with status 204 No Content without payload
     */

    @DeleteMapping("/multiple")
    public ResponseEntity<Void> deleteMultipleUploads(
            @Valid @RequestBody UploadMultipleDeleteDto request
    ) {
        try {
            uploadService.deleteObjects(request.getFilenames(), request.getType()).join();
        } catch (InterruptedException e) {
            throw new OperationInterruptedException("Failed to delete multiple objects");
        }
        return ResponseEntity.noContent().build();
    }
}
