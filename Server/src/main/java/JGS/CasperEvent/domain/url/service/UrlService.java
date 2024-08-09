package JGS.CasperEvent.domain.url.service;

import JGS.CasperEvent.domain.url.dto.ShortenUrlResponseDto;
import JGS.CasperEvent.domain.url.repository.UrlRepository;
import JGS.CasperEvent.global.entity.BaseUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    @Autowired
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public ShortenUrlResponseDto generateShortUrl(BaseUser user){
        String id = user.getId();

        System.out.println("id = " + id);

        return new ShortenUrlResponseDto("shortenLink");
    }
}
