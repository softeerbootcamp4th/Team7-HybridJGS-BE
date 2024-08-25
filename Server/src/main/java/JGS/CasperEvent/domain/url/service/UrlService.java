package JGS.CasperEvent.domain.url.service;

import JGS.CasperEvent.domain.url.dto.ShortenUrlResponseDto;
import JGS.CasperEvent.domain.url.entity.Url;
import JGS.CasperEvent.domain.url.repository.UrlRepository;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.util.AESUtils;
import JGS.CasperEvent.global.util.Base62Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class UrlService {

    @Value("${client.url}")
    private String clientUrl;
    @Value("${client.localUrl}")
    private String localClientUrl;

    @Value("${shortenUrlService.url}")
    private String shortenBaseUrl;


    private final UrlRepository urlRepository;
    private final SecretKey secretKey;

    //todo: 테스트 끝나면 수정필요
    // 단축 url 생성
    public ShortenUrlResponseDto generateShortUrl(BaseUser user) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encryptedUserId = AESUtils.encrypt(user.getPhoneNumber(), secretKey);

        String originalUrl = clientUrl + "?" + "referralId=" + encryptedUserId;
        String originalLocalUrl = localClientUrl + "?" + "referralId=" + encryptedUserId;

        Url url = urlRepository.findByOriginalUrl(originalUrl).orElseGet(
                () -> urlRepository.save(new Url(originalUrl))
        );
        Url localUrl = urlRepository.findByOriginalUrl(originalLocalUrl).orElseGet(
                () -> urlRepository.save(new Url(originalLocalUrl))
        );

        Long urlId = url.getId();
        Long localUrlId = localUrl.getId();

        String shortenUrl = shortenBaseUrl + "/link/" + Base62Utils.encode(urlId);
        String shortenLocalUrl = shortenBaseUrl + "/link/" + Base62Utils.encode(localUrlId);

        return new ShortenUrlResponseDto(shortenUrl, shortenLocalUrl);
    }

    // 원본 url 조회 테스트
    public String getOriginalUrl(String encodedId) {
        Long urlId = Base62Utils.decode(encodedId);
        Url url = urlRepository.findById(urlId).orElseGet(() -> new Url(clientUrl));
        return url.getOriginalUrl();
    }

}
