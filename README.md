# JVM Microeffect Suite

A set of JMH tests that indicate a very surprising and unusual behaviour. The
tests generate GUID using manual data for randomness and time-base bits. The
original goal was to test atomics vs synchronization, but they ended up
measuring something completely different.


# Measuring the Wrong Things

The test run outputs ("regular" tests) are provided in the
[random.txt](random.txt) file.  They cleary indicate that the synchronized block
could generate 1.5 times more numbers than atomics. Which is a bit strange at
this point, but could be attributed to some JVM optimizations. Although you may
notice that the JMH test is very fast and run with only a few iterations.

But does it matter? In fact, it does. I attempted a full run with default JMH
settings (5 forks, 5 warm-ups, 5 measurements). And it was initially exhibiting
the same behaviour. But eventually few benchmarks were 5 times slower than
ususal. And it was not specific for some algorithm. The output could be in a
very wide range of 400 000 - 2 000 000 for the same suite (and other suites). So
what happens here?


# Measuring the Right(?) Things

The reason for that performance drop is actually simple. The system uses secure
random, which (by default) needs some system-wide entropy. And the _actual_
thing being measured here is the amount of entropy that goes through the system
(i.e. throughput of the SecureRandom). During some periods there was no activity
at the computer (I was AFK, no network connections or anything else). This lead
to the very low amount of entropy and thus reduces the generation performance.

This gives us an interesting observation. The threading solutions outperforms
the atomic ones not because they are very good but because they are quite bad!
They create lot of thread contention at the _system_ level and this (self)-feeds
the entropy pool such needed by the computation. Atomic solutions don't involve
OS and thus does not contribute themselves to the pool thus decreasing possible
throughput.

Changing the entropy source to the non-blocking `/dev/urandom` fixes the
relationship between atomics and synchronized. The results are in
[urandom.txt](urandom.txt). These were run on the standard settings. Notice the
Error column - all the tests are now reliable.


## The Mystery

Now the atomics are 8 times faster than synchronized. This is expected - atomics
are lightweight, synchronized is very heavyweight. But! The synchronized
solution using `/dev/urandom` became 6 times _slower_ than the original one! Or,
actually, not all the synchronized solutions are that slow. Only the ones that
use SecureRandom in the synchronized block (unsynchronized ones are fast).

What happens there? I don't have an answer yet. Maybe it is caused by some
interactions between threads and system scheduler. At the end of the day, thread
swithcing feeds entropy which may unblock threads waiting for SecureRandom. And
this creates nice "one-after-another" order of threads entering and exiting
synchronized block. With regular random the behaviour is much more chaotic. Or
maybe urandom does not work well within synchronized blocks. Or maybe something
else...

Please, raise a ticket if you have any hypothesis causing this slowdown or if
you know how to test/investigate/debug this issue.
