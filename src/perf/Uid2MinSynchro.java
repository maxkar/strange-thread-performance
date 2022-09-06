package perf;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;

public class Uid2MinSynchro {
    private static final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);
    private static long timeMillis;
    private static int counter;

    public static  UUID next() {
        long currentTimeMillis;
        int currentCounter;

        synchronized(Uid2MinSynchro.class) {
          do {
              currentTimeMillis = currentTimeMillis();
              assert currentTimeMillis >= timeMillis;
              currentCounter = timeMillis == currentTimeMillis ? counter + 1 : 0;
          } while (currentCounter >= 0x10_00);
          

          timeMillis = currentTimeMillis;
          counter = currentCounter;
        }

        long lsb1 = secureRandom.get().nextLong(0x40_00_00_00_00_00_00_00L);

        long msb = currentTimeMillis << 16 | 0x40_00 | currentCounter;
        long lsb = 0x80_00_00_00_00_00_00_00L | lsb1;
        return new UUID(msb, lsb);
    }
}

