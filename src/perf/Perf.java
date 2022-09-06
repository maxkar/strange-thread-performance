package perf;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Perf {
  @Benchmark
  public final void uid1() {
    Uid1.next();
  }

  @Benchmark
  public final void uid1Fixed() {
    Uid1Fixed.next();
  }

  @Benchmark
  public final void uid1FixedSleep() throws InterruptedException {
    Uid1FixedSleep.next();
  }

  @Benchmark
  public final void uid2() {
    Uid2.next();
  }

  @Benchmark
  public final void uid2Nano() {
    Uid2Nano.next();
  }

  @Benchmark
  public final void uid2MinSynchro() {
    Uid2MinSynchro.next();
  }

  @Benchmark
  public final void uid2RndFirst() {
    Uid2RndFirst.next();
  }

  @Benchmark
  public final void uid2RndFirstNano() {
    Uid2RndFirstNano.next();
  }

  @Benchmark
  public final void uid2LsdSync() {
    Uid2LsdSync.next();
  }

  @Benchmark
  public final void uid2Ssr() {
    Uid2Ssr.next();
  }

  @Benchmark
  public final void uid3() {
    Uid3.next();
  }

  @Benchmark
  public final void uid3Sleep() throws InterruptedException {
    Uid3Sleep.next();
  }

  @Benchmark
  public final void uid4() {
    Uid4.next();
  }

  public static void main(String[] args) throws RunnerException {
    Options opts = new OptionsBuilder()
      .include(Perf.class.getSimpleName())
      .forks(1)
      .build();

    new Runner(opts).run();
  }
}
