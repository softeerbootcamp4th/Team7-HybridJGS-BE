package JGS.CasperEvent.domain.url.controller;

import JGS.CasperEvent.domain.url.dto.ShortenUrlResponseDto;
import JGS.CasperEvent.domain.url.service.UrlService;
import JGS.CasperEvent.global.entity.BaseUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link")
public class UrlController {

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<ShortenUrlResponseDto> generateShortUrl(HttpServletRequest request) {
        BaseUser user = (BaseUser) request.getAttribute("user");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(urlService.generateShortUrl(user));
    }
}
