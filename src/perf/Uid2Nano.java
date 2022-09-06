package perf;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Uid2Nano {
    private static final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);
    private static long timeMillis;
    private static int counter;

    private static long base = System.currentTimeMillis();
    private static long nanos = System.nanoTime();

    public static synchronized UUID next() {
        long currentTimeMillis;
        int currentCounter;

        do {
            currentTimeMillis = nowTime();
            assert currentTimeMillis >= timeMillis;
            currentCounter = timeMillis == currentTimeMillis ? counter + 1 : 0;
        } while (currentCounter >= 0x10_00);

        timeMillis = currentTimeMillis;
        counter = currentCounter;

        long lsb1 = secureRandom.get().nextLong(0x40_00_00_00_00_00_00_00L);

        long msb = currentTimeMillis << 16 | 0x40_00 | currentCounter;
        long lsb = 0x80_00_00_00_00_00_00_00L | lsb1;
        return new UUID(msb, lsb);
    }

    private static long nowTime() {
      return (System.nanoTime() - nanos) / 1_000_000 + base;
    }
}

