package melons.innocent.terrain.bench;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * What the bench's controls ask of it, said on JavaFX's thread and read on the card's: the first view back, and an
 * end. Each is read once: asked, it holds until the card's thread takes it.
 */
public final class Wanted {

    private final AtomicBoolean first = new AtomicBoolean();
    private final AtomicBoolean stopped = new AtomicBoolean();

    /** The view looked from first, wanted back. */
    public void first() {
        first.set(true);
    }

    /** Whether the first view has been wanted back since this was last asked. */
    public boolean firstWanted() {
        return first.getAndSet(false);
    }

    /** An end, wanted. */
    public void stop() {
        stopped.set(true);
    }

    /** Whether an end has been wanted. */
    public boolean stopped() {
        return stopped.get();
    }
}
