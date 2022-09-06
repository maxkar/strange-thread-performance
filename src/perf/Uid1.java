package perf;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;

public class Uid1 {
    private static final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);
    private static final AtomicLong timeMillis = new AtomicLong();
    private static final AtomicInteger counter = new AtomicInteger();

    public static UUID next() {
        long timeMillis2;
        int counter2;

        while (true) {
            long timeMillis1 = timeMillis.get();
            timeMillis2 = currentTimeMillis();
            if (timeMillis2 < timeMillis1) {
                continue;
            }

            int counter1 = counter.get();
            counter2 = timeMillis1 == timeMillis2 ? counter1 + 1 : 0;
            if (counter2 >= 0x10_00) {
                continue;
            }

            if (!timeMillis.compareAndSet(timeMillis1, timeMillis2)) {
                continue;
            }
            if (!counter.compareAndSet(counter1, counter2)) {
                continue;
            }

            break;
        }

        long lsb1 = secureRandom.get().nextLong(0x40_00_00_00_00_00_00_00L);

        long msb = timeMillis2 << 16 | 0x40_00 | counter2;
        long lsb = 0x80_00_00_00_00_00_00_00L | lsb1;
        return new UUID(msb, lsb);
    }
}
