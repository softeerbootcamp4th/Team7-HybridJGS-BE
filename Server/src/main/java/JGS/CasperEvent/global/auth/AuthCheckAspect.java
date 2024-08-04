package JGS.CasperEvent.global.auth;

import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class AuthCheckAspect {
    private static final String Authorization = "Authorization";


    private final HttpServletRequest httpServletRequest;
    private final LotteryParticipantsRepository lotteryParticipantsRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public AuthCheckAspect(HttpServletRequest httpServletRequest, LotteryParticipantsRepository lotteryParticipantsRepository, AdminRepository adminRepository) {
        this.httpServletRequest = httpServletRequest;
        this.lotteryParticipantsRepository = lotteryParticipantsRepository;
        this.adminRepository = adminRepository;
    }

    @Around("@annotation(JGS.CasperEvent.global.auth.AuthCheck)")
    public Object authCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String token = httpServletRequest.getHeader(Authorization);

        String phoneNumber = UserUtil.getDecodedPhoneNumber(token);
        LotteryParticipants lotteryParticipant = lotteryParticipantsRepository.findByPhoneNumber(token)
                .orElse(null);

        if(lotteryParticipant == null)
            throw new CustomException("유저 인증 정보가 존재하지 않습니다.", CustomErrorCode.USER_NOT_FOUND);

        return proceedingJoinPoint.proceed();

    }

    @Around("@annotation(JGS.CasperEvent.global.auth.AdminCheck)")
    public Object adminCheck(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String token = httpServletRequest.getHeader(Authorization);
        System.out.println("token = " + token);
        boolean valid = UserUtil.getIsValidAdminToken(token);

        if(!valid)
            throw new CustomException("권한이 없습니다.", CustomErrorCode.UNAUTHORIZED);

        return proceedingJoinPoint.proceed();
    }
}
