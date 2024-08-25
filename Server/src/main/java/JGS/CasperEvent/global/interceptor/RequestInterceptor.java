package JGS.CasperEvent.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "requestId";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // UUID를 사용해 고유한 requestId 생성
        String requestId = UUID.randomUUID().toString();

        // MDC에 requestId 추가하여 로깅 시 포함되도록 설정
        MDC.put(REQUEST_ID, requestId);

        String requestURI = request.getMethod() + " " + request.getRequestURL();

        String queryString = request.getQueryString();

        log.info("Request [{}] QueryString [{}]", requestURI, queryString);

        // 요청의 헤더에 requestId 추가 (선택 사항)
        response.addHeader(REQUEST_ID, requestId);

        return true; // 다음 인터셉터나 컨트롤러로 요청 전달
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        log.info("Response {} [{}]", response.getStatus(), handler);

        // 요청이 완료된 후 MDC에서 requestId 제거
        MDC.remove(REQUEST_ID);
    }
}