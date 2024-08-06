package JGS.CasperEvent.global.jwt.filter;

import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.service.AdminService.AdminService;
import JGS.CasperEvent.global.jwt.dto.AdminLoginDto;
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
public class VerifyAdminFilter implements Filter {
    public static final String AUTHENTICATE_ADMIN = "authenticateAdmin";
    private final ObjectMapper objectMapper;

    private final AdminService adminService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if ((httpServletRequest.getMethod().equals("POST"))) {
            try {
                AdminLoginDto adminLoginDto = objectMapper.readValue(request.getReader(), AdminLoginDto.class);
                Admin admin = adminService.verifyAdmin(adminLoginDto);

                request.setAttribute(AUTHENTICATE_ADMIN, admin);

                chain.doFilter(request, response);
            } catch (Exception e) {
                log.error("Fail User Verify");
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            }
        }
    }
}
