package perf;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.System.currentTimeMillis;

/** Uid1 fixed from race. */
public class Uid1Fixed {
    private static final class State {
      long timeMillis;
      int counter;

      public State(long timeMillis, int counter) {
        this.timeMillis = timeMillis;
        this.counter = counter;
      }
    }

    private static final ThreadLocal<SecureRandom> secureRandom = ThreadLocal.withInitial(SecureRandom::new);
    private static final AtomicReference<State> state = new AtomicReference();

    public static UUID next() {
        State prev = null;
        State next = null;

        while (true) {
            prev = state.get();
            long timeMillis = currentTimeMillis();
            if (prev != null) {
              if (timeMillis < prev.timeMillis)
                continue;
              if (timeMillis == prev.timeMillis && prev.counter >= 0x1_000)
                continue;
            }
            int nc = prev == null || prev.timeMillis < timeMillis ? 0 : prev.counter + 1;

            next = new State(timeMillis, nc);

            if (!state.compareAndSet(prev, next)) {
                continue;
            }
            break;
        }

        long lsb1 = secureRandom.get().nextLong(0x40_00_00_00_00_00_00_00L);

        long msb = next.timeMillis << 16 | 0x40_00 | next.counter;
        long lsb = 0x80_00_00_00_00_00_00_00L | lsb1;
        return new UUID(msb, lsb);
    }
}

