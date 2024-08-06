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
public class JwtAdminFilter implements Filter {

    private final JwtProvider jwtProvider;

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        Object admin = request.getAttribute(VerifyAdminFilter.AUTHENTICATE_ADMIN);
        Map<String, Object> claims = new HashMap<>();
        String authenticateAdminJson = objectMapper.writeValueAsString(admin);
        claims.put(VerifyAdminFilter.AUTHENTICATE_ADMIN, authenticateAdminJson);
        Jwt jwt = jwtProvider.createJwt(claims);
        String json = objectMapper.writeValueAsString(jwt);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);

//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//        httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value());
    }
}