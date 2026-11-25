package melons.innocent.glm.engine;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * What closes an engine in, as it is with the bonnet up: a cover bolted on each head over its camshafts, the coils
 * through it onto the plugs, the timing cover across the front over the chain, and over it all the trim cover. Made
 * once, its parts kept; joined to the engine only when it is asked to be covered.
 */
final class Covers {

    private final List<Part> heads = new ArrayList<>();
    private final List<Part> coils = new ArrayList<>();
    private final Part timing;
    private final Part trim;

    /** Its parts, made to {@code figures}. */
    Covers(final EngineFigures figures) {
        for (int b = 0; b < figures.banks(); b++) {
            heads.add(new HeadCover(figures, b).part());
            coils.add(new CoilPacks(figures, b).part());
        }
        this.timing = new TimingCover(figures).part();
        this.trim = new TrimCover(figures).part();
    }

    /** How they are joined to {@code block} and its heads {@code on}, one to a bank, in the order they are walked. */
    List<Connection> joined(final Part block, final List<Part> on) {
        final List<Connection> joined = new ArrayList<>();
        for (int b = 0; b < heads.size(); b++) {
            joined.add(new Connection(heads.get(b).name() + " on its head", on.get(b), "top", heads.get(b), "head",
                    Joining.BOLTED));
            joined.add(new Connection(coils.get(b).name() + " in their cover", heads.get(b), "top", coils.get(b),
                    "cover", Joining.SEATED));
        }
        joined.add(new Connection(timing.name() + " on the block", block, "crank, front", timing, "crank",
                Joining.BOLTED));
        joined.add(new Connection(trim.name() + " clipped on", heads.getFirst(), "top", trim, "clip",
                Joining.SEATED));
        return joined;
    }

    /** Whether {@code part} is one of them. */
    boolean has(final Part part) {
        return heads.contains(part) || coils.contains(part) || part == timing || part == trim;
    }

    /** How many there are. */
    int count() {
        return heads.size() + coils.size() + 2;
    }
}
