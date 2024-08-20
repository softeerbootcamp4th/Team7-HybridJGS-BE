package JGS.CasperEvent.global.jwt.filter;

import JGS.CasperEvent.global.entity.BaseUser;
import JGS.CasperEvent.global.jwt.dto.UserLoginDto;
import JGS.CasperEvent.global.jwt.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class VerifyUserFilter implements Filter {
    public static final String AUTHENTICATE_USER = "authenticateUser";
    private final ObjectMapper objectMapper;

    private final UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if ((httpServletRequest.getMethod().equals("POST"))) {
            try {
                UserLoginDto userLoginDto = objectMapper.readValue(request.getReader(), UserLoginDto.class);
                BaseUser user = userService.verifyUser(userLoginDto);
                request.setAttribute(AUTHENTICATE_USER, user);
                chain.doFilter(request, response);
            } catch (Exception e) {
                log.error("Fail User Verify: {}", e.getMessage());
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            }
        }
    }
}
