package com.example.fxsuite.taxonomy;

import java.util.List;

/** A small tree to test with: the failures of a service, two branches that say what their leaves share. */
final class Failures {

    private Failures() {
    }

    /** What a branch says of retrying, for its leaves to inherit. */
    interface Retry {
        int times();
    }

    record Failure() implements L0 {
    }

    record Transient() implements L1<Failure>, Retry {
        public Failure parent() {
            return new Failure();
        }

        public int times() {
            return 3;
        }
    }

    record Permanent() implements L1<Failure>, Retry {
        public Failure parent() {
            return new Failure();
        }

        public int times() {
            return 0;
        }
    }

    record Timeout() implements Leaf<Transient> {
        public Transient parent() {
            return new Transient();
        }
    }

    /** Says its own, nearer than its branch's. */
    record RateLimited() implements Leaf<Transient>, Retry {
        public Transient parent() {
            return new Transient();
        }

        public int times() {
            return 1;
        }
    }

    record NotFound() implements Leaf<Permanent> {
        public Permanent parent() {
            return new Permanent();
        }
    }

    record Forbidden() implements Leaf<Permanent> {
        public Permanent parent() {
            return new Permanent();
        }
    }

    /** Another tree's, to be refused in this one. */
    record Elsewhere() implements L0 {
    }

    record Stray() implements Leaf<Elsewhere> {
        public Elsewhere parent() {
            return new Elsewhere();
        }
    }

    static final List<Leaf<?>> LEAVES = List.of(new Timeout(), new RateLimited(), new NotFound(), new Forbidden());
}
