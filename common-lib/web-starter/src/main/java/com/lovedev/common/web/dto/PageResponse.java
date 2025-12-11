package com.lovedev.common.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Standard pagination response wrapper
 * Provides consistent pagination structure across all microservices
 *
 * @param <T> Type of content in the page
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pagination response wrapper")
public class PageResponse<T> {

    @Schema(description = "List of items in current page")
    private List<T> content;

    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int pageNumber;

    @Schema(description = "Number of items per page", example = "10")
    private int pageSize;

    @Schema(description = "Total number of items", example = "100")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;

    @Schema(description = "Is this the last page?", example = "false")
    private boolean last;

    @Schema(description = "Is this the first page?", example = "true")
    private boolean first;

    @Schema(description = "Is the result empty?", example = "false")
    private boolean empty;

    @Schema(description = "Does next page exist?", example = "true")
    private boolean hasNext;

    @Schema(description = "Does previous page exist?", example = "false")
    private boolean hasPrevious;

    /**
     * Create PageResponse from Spring Data Page
     *
     * @param page Spring Data Page object
     * @return PageResponse with same content
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Create PageResponse from Spring Data Page with content mapping
     * Useful for converting entities to DTOs
     *
     * @param page Spring Data Page object
     * @param mapper Function to map content
     * @return PageResponse with mapped content
     */
    public static <T, U> PageResponse<U> of(Page<T> page, Function<T, U> mapper) {
        List<U> mappedContent = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PageResponse.<U>builder()
                .content(mappedContent)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Create empty PageResponse
     *
     * @return Empty PageResponse
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .content(List.of())
                .pageNumber(0)
                .pageSize(0)
                .totalElements(0)
                .totalPages(0)
                .last(true)
                .first(true)
                .empty(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}