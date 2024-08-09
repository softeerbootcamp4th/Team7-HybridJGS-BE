package JGS.CasperEvent.domain.url.service;

import JGS.CasperEvent.domain.url.dto.ShortenUrlResponseDto;
import JGS.CasperEvent.domain.url.entity.Url;
import JGS.CasperEvent.domain.url.repository.UrlRepository;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.util.AESUtils;
import JGS.CasperEvent.global.util.Base62Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class UrlService {

    @Value("${client.url}")
    private String clientUrl;

    @Value("${shortenUrlService.url}")
    private String shortenBaseUrl;

    private final UrlRepository urlRepository;
    private final SecretKey secretKey;

    @Autowired
    public UrlService(UrlRepository urlRepository, SecretKey secretKey) {
        this.urlRepository = urlRepository;
        this.secretKey = secretKey;
    }

    public ShortenUrlResponseDto generateShortUrl(BaseUser user) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encryptedUserId = AESUtils.encrypt(user.getId(), secretKey);
        String originalUrl = clientUrl + "?" + "referralId=" + encryptedUserId;

        Url url = urlRepository.findByOriginalUrl(originalUrl).orElseGet(
                () -> urlRepository.save(new Url(originalUrl))
        );

        Long urlId = url.getId();
        String shortenUrl = shortenBaseUrl + "/link/" + Base62Utils.encode(urlId);

        return new ShortenUrlResponseDto(shortenUrl);
    }

}
