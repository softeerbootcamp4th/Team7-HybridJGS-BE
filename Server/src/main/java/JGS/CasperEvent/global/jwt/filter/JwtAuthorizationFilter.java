package JGS.CasperEvent.global.jwt.filter;

import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.error.exception.AuthorizationException;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter implements Filter {

    private final String[] whiteListUris = new String[]{"/event/auth", "/event/rush", "/event/lottery/caspers", "/admin/join", "/admin/auth", "/h2", "/h2/*", "/swagger-ui/*", "/v3/api-docs", "/v3/api-docs/*"};
    private final String[] blackListUris = new String[]{"/event/rush/*"};

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String requestUri = httpServletRequest.getRequestURI();

        if (whiteListCheck(requestUri) && !blackListCheck(requestUri)) {
            chain.doFilter(request, response);
            return;
        }

        if (!isContainToken(httpServletRequest)) {
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "인증 오류");
            return;
        }

        try {
            String token = getToken(httpServletRequest);
            BaseUser user = getAuthenticateUser(token);
            verifyAuthorization(requestUri, user);
            log.info("값 : {}", user.getId());
            httpServletRequest.setAttribute("userId", user.getId());
            chain.doFilter(request, response);
        } catch (JsonParseException e) {
            log.error("JsonParseException");
            httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value());
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            log.error("JwtException");
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "인증 오류");
        } catch (ExpiredJwtException e) {
            log.error("JwtTokenExpired");
            httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "토큰이 만료됐습니다");
        } catch (AuthorizationException e) {
            log.error("AuthorizationException");
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다");
        }
    }

    private boolean whiteListCheck(String uri) {
        return PatternMatchUtils.simpleMatch(whiteListUris, uri);
    }

    private boolean blackListCheck(String uri) {
        return PatternMatchUtils.simpleMatch(blackListUris, uri);
    }

    private boolean isContainToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization != null && authorization.startsWith("Bearer ");
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        return authorization.substring(7);
    }

    private BaseUser getAuthenticateUser(String token) throws JsonProcessingException {
        Claims claims = jwtProvider.getClaims(token);
        String authenticateUserJson = claims.get(VerifyUserFilter.AUTHENTICATE_USER, String.class);
        return objectMapper.readValue(authenticateUserJson, BaseUser.class);
    }

    private void verifyAuthorization(String uri, BaseUser user) {
        if (PatternMatchUtils.simpleMatch("*/admin*", uri) && user.getRole() != Role.ADMIN) {
            throw new AuthorizationException();
        }
    }
}
