package JGS.CasperEvent.global.util;

import java.util.concurrent.atomic.AtomicLong;

public class UserUtil {
    //TODO: 스프링 서버 뻗으면 캐스퍼 아이디 0부터 다시 시작함
    private static final AtomicLong counter = new AtomicLong(0);

    //TODO: 현재는 그냥 userData 즉시 반환, 키 이용한 복호화로 수정하기
    public static String getDecodedPhoneNumber(String userData) {
        return userData;
    }

    //TODO: 현재는 true 리턴, jwt로 변경 필요
    public static Boolean isValidAdminToken(String token){
        return true;
    }
    public static long generateId(){
        return counter.incrementAndGet();
    }

}
