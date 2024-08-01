package JGS.CasperEvent.global.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(title = "리스트 응답 래핑 DTO")
public class ListResponse<T> {
    private final List<T> result;
    private final Metadata metadata;

    public ListResponse(List<T> result, Metadata metadata) {
        this.result = result;
        this.metadata = metadata;
    }

    public List<T> getResult() {
        return result;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public static <T> ListResponse<T> create(Page<T> page) {
        return new ListResponse<>(page.toList(), Metadata.create(page));
    }

    public static <T> ListResponse<T> create(List<T> list) {
        return new ListResponse<>(list, null);
    }

    public static <T, S> ListResponse<T> create(List<T> list, Page<S> page) {
        return new ListResponse<>(list, Metadata.create(page));
    }

    public static <T> ListResponse<T> create(List<T> list, Long totalCount, Integer size, String lastId) {
        return new ListResponse<>(list, Metadata.create(totalCount, size, lastId));
    }

    @Schema(title = "검색 메타데이터")
    public static class Metadata {
        private final Long totalCount;
        private final Integer totalPageCount;
        private final Integer size;
        public Integer getCurrentPageNumber() {
            return currentPageNumber;
        }
        private final Integer currentPageNumber;
        private final String lastId;

        public Metadata(Long totalCount, Integer totalPageCount, Integer size, Integer currentPageNumber, String lastId) {
            this.totalCount = totalCount;
            this.totalPageCount = totalPageCount;
            this.size = size;
            this.currentPageNumber = currentPageNumber;
            this.lastId = lastId;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public Integer getTotalPageCount() {
            return totalPageCount;
        }

        public Integer getSize() {
            return size;
        }

        public String getLastId() {
            return lastId;
        }

        public static <T> Metadata create(Page<T> page) {
            return new Metadata(page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber(),"");
        }

        public static Metadata create(Long totalCount, Integer size, String lastId) {
            return new Metadata(totalCount, 0, size, 0, lastId);
        }
    }
}