package perf;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.currentTimeMillis;

/** Uid3 but with extra sleep. */
public class Uid3Sleep {
    private static final class State {
      long timeMillis;
      AtomicInteger counter;

      public State(long timeMillis) {
        this.timeMillis = timeMillis;
        this.counter = new AtomicInteger();
      }
    }

    private static final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);
    private static final AtomicReference<State> state = new AtomicReference<>();

    public static UUID next() throws InterruptedException {
        int counter;
        long timeMillis;

        while (true) {
            timeMillis = currentTimeMillis();

            State prev = state.get();
            State cur;
            
            if (prev != null && prev.timeMillis > timeMillis) {
              Thread.sleep(prev.timeMillis - timeMillis + 1);
              continue;
            }

            if (prev == null || prev.timeMillis < timeMillis) {
              cur = new State(timeMillis);
              if (!state.compareAndSet(prev, cur)) {
                Thread.sleep(1);
                continue;
              }
            } else 
              cur = prev;
              

            counter = cur.counter.incrementAndGet();
            if (counter >= 0x100_0)
              continue;

            break;
        }

        long lsb1 = secureRandom.get().nextLong(0x40_00_00_00_00_00_00_00L);

        long msb = timeMillis << 16 | 0x40_00 | counter;
        long lsb = 0x80_00_00_00_00_00_00_00L | lsb1;
        return new UUID(msb, lsb);
    }
}



