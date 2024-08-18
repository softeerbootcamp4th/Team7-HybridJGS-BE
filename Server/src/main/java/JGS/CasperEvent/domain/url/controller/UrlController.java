package JGS.CasperEvent.domain.url.controller;

import JGS.CasperEvent.domain.url.dto.ShortenUrlResponseDto;
import JGS.CasperEvent.domain.url.service.UrlService;
import JGS.CasperEvent.global.entity.BaseUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("link")
public class UrlController {

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // 공유링크 생성
    @PostMapping
    public ResponseEntity<ShortenUrlResponseDto> generateShortUrl(HttpServletRequest request) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        BaseUser user = (BaseUser) request.getAttribute("user");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(urlService.generateShortUrl(user));
    }

    // 공유링크 접속
    @GetMapping("/{encodedId}")
    public ResponseEntity<Void> redirectOriginalUrl(@PathVariable String encodedId){
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", urlService.getOriginalUrl(encodedId))
                .build();
    }
}
