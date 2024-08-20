package JGS.CasperEvent.domain.url.controller;

import JGS.CasperEvent.domain.url.dto.ShortenUrlResponseDto;
import JGS.CasperEvent.domain.url.service.UrlService;
import JGS.CasperEvent.global.entity.BaseUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "공유 링크 생성", description = "사용자가 공유할 URL을 단축 링크로 생성합니다.")
    @ApiResponse(responseCode = "201", description = "Short URL creation successful")
    @PostMapping
    public ResponseEntity<ShortenUrlResponseDto> generateShortUrl(HttpServletRequest request)
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        BaseUser user = (BaseUser) request.getAttribute("user");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(urlService.generateShortUrl(user));
    }

    @Operation(summary = "공유 링크 접속", description = "단축 링크를 통해 원본 URL로 리다이렉트합니다.")
    @ApiResponse(responseCode = "302", description = "Redirect successful")
    @GetMapping("/{encodedId}")
    public ResponseEntity<Void> redirectOriginalUrl(@PathVariable String encodedId) {
        String originalUrl = urlService.getOriginalUrl(encodedId);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }
}
