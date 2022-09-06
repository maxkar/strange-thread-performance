package perf;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;

/** Uid 2, simple secure random. */
public class Uid2Ssr {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static long timeMillis;
    private static int counter;

    public static synchronized UUID next() {
        long currentTimeMillis;
        int currentCounter;

        long lsb1 = secureRandom.nextLong(0x40_00_00_00_00_00_00_00L);
        long lsb = 0x80_00_00_00_00_00_00_00L | lsb1;

        do {
            currentTimeMillis = currentTimeMillis();
            assert currentTimeMillis >= timeMillis;
            currentCounter = timeMillis == currentTimeMillis ? counter + 1 : 0;
        } while (currentCounter >= 0x10_00);
        

        timeMillis = currentTimeMillis;
        counter = currentCounter;

        long msb = currentTimeMillis << 16 | 0x40_00 | currentCounter;
        return new UUID(msb, lsb);
    }
}



