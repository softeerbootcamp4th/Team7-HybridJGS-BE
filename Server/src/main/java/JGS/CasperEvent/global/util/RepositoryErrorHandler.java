package JGS.CasperEvent.global.util;

import JGS.CasperEvent.global.enums.CustomErrorCode;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public class RepositoryErrorHandler {
    public static <T, ID> T findByIdOrElseThrow(JpaRepository<T, ID> repository, ID id, CustomErrorCode customErrorCode) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(customErrorCode.name())
        );
    }

    public static <T, ID> void existsByIdOrElseThrow(JpaRepository<T, ID> repository, ID id, CustomErrorCode customErrorCode) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException(customErrorCode.name());
        }
    }
}
