package JGS.CasperEvent.domain.url.service;

import JGS.CasperEvent.domain.url.dto.ShortenUrlResponseDto;
import JGS.CasperEvent.domain.url.entity.Url;
import JGS.CasperEvent.domain.url.repository.UrlRepository;
import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import JGS.CasperEvent.global.util.AESUtils;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;

    private BaseUser user;
    private SecretKey secretKey;

    @InjectMocks
    UrlService urlService;

    @BeforeEach
    void setUp(){
        user = new BaseUser("010-0000-0000", Role.USER);

        byte[] decodedKey = "I0EM1X1NeXKJv4Q+ifZllg==".getBytes();
        secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        ReflectionTestUtils.setField(urlService, "secretKey", secretKey);
        ReflectionTestUtils.setField(urlService, "shortenBaseUrl", "baseUrl");

    }

    @TestConfiguration
    static class TestConfig{
        @Bean
        public JwtProvider jwtProvider(){
            String secretKey = "mockKEymockKEymockKEymockKEymockKEymockKEymockKEy";
            byte[] secret = secretKey.getBytes();
            return new JwtProvider(Keys.hmacShaKeyFor(secret));
        }
    }

    @Test
    @DisplayName("단축 url 생성 테스트 - 성공")
    void generateShortUrlTest_Success() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //given
        Url originalUrl = spy(new Url(AESUtils.encrypt(user.getPhoneNumber(), secretKey)));

        given(urlRepository.save(any())).willReturn(originalUrl);
        given(originalUrl.getId()).willReturn(1L);

        //when
        ShortenUrlResponseDto shortenUrlResponseDto = urlService.generateShortUrl(user);

        //then
        assertThat(shortenUrlResponseDto.shortenUrl()).isEqualTo("baseUrl/link/B");
        assertThat(shortenUrlResponseDto.shortenLocalUrl()).isEqualTo("baseUrl/link/B");
    }

    @Test
    @DisplayName("원본 url 조회 테스트 - 성공")
    void getOriginalUrlTest_Success() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        //given
        Url url = new Url(AESUtils.encrypt(user.getPhoneNumber(), secretKey));
        given(urlRepository.findById(any())).willReturn(Optional.of(url));

        //when
        String originalUrl = urlService.getOriginalUrl("B");

        //then
        assertThat(originalUrl).isEqualTo("GH379iuBZdNRV9uGEk1KWg==");

    }

}