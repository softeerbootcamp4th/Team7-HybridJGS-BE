package JGS.CasperEvent.global.jwt.filter;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
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
                AdminRequestDto adminRequestDto = objectMapper.readValue(request.getReader(), AdminRequestDto.class);
                Admin admin = adminService.verifyAdmin(adminRequestDto);

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
