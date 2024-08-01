package JGS.CasperEvent.global.util;

import java.util.concurrent.atomic.AtomicLong;

public class UserUtil {
    private static final AtomicLong counter = new AtomicLong(0);

    //TODO: 현재는 그냥 userData 즉시 반환, 키 이용한 복호화로 수정하기
    public static String getDecodedPhoneNumber(String userData) {
        return userData;
    }

    public static long generateId(){
        return counter.incrementAndGet();
    }

}
