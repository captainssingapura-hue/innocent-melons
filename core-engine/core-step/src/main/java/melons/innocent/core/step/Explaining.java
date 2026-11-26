package melons.innocent.core.step;

/**
 * Whether engines explain what they work out: said once, when a run begins, and not changed after. Off, an engine
 * does none of the work of explaining and gives its one empty explanation every time; the state it gives is the
 * very same either way.
 *
 * @param on whether engines are to explain
 */
public record Explaining(boolean on) {

    /** Not explaining. */
    public Explaining() {
        this(false);
    }
}
