package perf;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;


/* Like UID2 but with unroll. */
public class Uid4 {
    private static final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);
    private static long timeMillis;
    private static int counter;

    public static synchronized UUID next() {
        long currentTimeMillis;
        int currentCounter;

        long lsb1 = secureRandom.get().nextLong(0x40_00_00_00_00_00_00_00L);
        long lsb = 0x80_00_00_00_00_00_00_00L | lsb1;

        currentTimeMillis = currentTimeMillis();
        assert currentTimeMillis >= timeMillis;

        if (currentTimeMillis == timeMillis) {
          currentCounter = counter + 1;
          if (currentCounter >= 0x10_00) {
            do {
              currentTimeMillis = currentTimeMillis();
              assert currentTimeMillis >= timeMillis;
            } while (currentTimeMillis == timeMillis);
            currentCounter = 0;
          }
        } else {
          currentCounter = 0;
        }

        timeMillis = currentTimeMillis;
        counter = currentCounter;

        long msb = currentTimeMillis << 16 | 0x40_00 | currentCounter;
        return new UUID(msb, lsb);
    }
}

