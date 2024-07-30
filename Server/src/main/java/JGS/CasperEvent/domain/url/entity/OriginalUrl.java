package JGS.CasperEvent.domain.url.entity;

import JGS.CasperEvent.global.entity.BaseEntity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class OriginalUrl extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    private String originalUrl;
}
