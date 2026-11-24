package melons.innocent.plants.walk;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Something being made on another thread, waited for: what it comes to, or, if its making failed or the wait was
 * broken off, a refusal saying so.
 *
 * @param coming what is being made
 * @param <T>    what it is
 */
record Awaited<T>(Future<T> coming) {

    /** What it comes to, waited for as long as its making takes. */
    T come() {
        try {
            return coming.get();
        } catch (final InterruptedException broken) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("the wait for what was being made was broken off", broken);
        } catch (final ExecutionException failed) {
            throw new IllegalStateException("what was being made could not be made", failed.getCause());
        }
    }
}
