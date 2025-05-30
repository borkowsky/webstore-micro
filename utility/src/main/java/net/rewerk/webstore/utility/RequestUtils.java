package net.rewerk.webstore.utility;

import net.rewerk.webstore.dto.request.PaginatedRequestParamsDto;
import net.rewerk.webstore.dto.request.SortedRequestParamsDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Common servlet request utility methods
 *
 * @author rewerk
 */

public abstract class RequestUtils {

    /**
     * Get Spring PageRequest from paginated request params DTO
     *
     * @param requestDto Paginated request parameters DTO
     * @return Spring PageRequest object
     */

    public static PageRequest getPageRequest(PaginatedRequestParamsDto requestDto) {
        Integer page = requestDto.getPage();
        Integer limit = requestDto.getLimit();
        return PageRequest.of(page, limit);
    }

    /**
     * Get Spring PageRequest with sort parameters from sorted request params DTO
     *
     * @param requestDto Sorted request parameters DTO
     * @return Spring PageRequest object
     */

    public static PageRequest getSortAndPageRequest(SortedRequestParamsDto requestDto) {
        Integer page = requestDto.getPage();
        Integer limit = requestDto.getLimit();
        String sort = requestDto.getSort();
        String order = requestDto.getOrder();
        return PageRequest.of(page, limit,
                sort == null ? Sort.unsorted()
                        : Sort.by(order == null || "asc".equalsIgnoreCase(order) ?
                        Sort.Direction.ASC : Sort.Direction.DESC, sort
                )
        );
    }
}
