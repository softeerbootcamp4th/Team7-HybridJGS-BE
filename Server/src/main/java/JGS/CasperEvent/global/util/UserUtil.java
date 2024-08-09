package JGS.CasperEvent.global.util;

import java.util.concurrent.atomic.AtomicLong;

public class UserUtil {
    //TODO: 스프링 서버 뻗으면 캐스퍼 아이디 0부터 다시 시작함
    private static final AtomicLong counter = new AtomicLong(0);
    public static long generateId(){
        return counter.incrementAndGet();
    }

}
