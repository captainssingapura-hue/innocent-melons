package melons.innocent.core.interaction;

/**
 * A state that a thing held and whoever holds it are in together, which lasts until it is left, with controls of
 * its own: where an action does not do its work at once, it leads into one of these ({@link Action#leads}). Each is
 * a state of the thing and of its holder both: the thing readied some way, the holder standing some way to it.
 */
public enum Mode {

    /**
     * Readied to be thrown, a projectile in the hand: the trigger held gathers power, and let go throws it, the way
     * the holder faces and looks.
     */
    THROWING("Throwing");

    private final String label;

    Mode(final String label) {
        this.label = label;
    }

    /** What it is called. */
    public String label() {
        return label;
    }
}
