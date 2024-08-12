package JGS.CasperEvent.global.jwt.filter;

import JGS.CasperEvent.global.jwt.dto.Jwt;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtUserFilter implements Filter {

    private final JwtProvider jwtProvider;

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        Object user = request.getAttribute(VerifyUserFilter.AUTHENTICATE_USER);
        Map<String, Object> claims = new HashMap<>();
        String authenticateUserJson = objectMapper.writeValueAsString(user);
        claims.put(VerifyUserFilter.AUTHENTICATE_USER, authenticateUserJson);
        Jwt jwt = jwtProvider.createJwt(claims);
        String json = objectMapper.writeValueAsString(jwt);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}