package melons.innocent.glm.engine;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * What closes an engine in, as it is with the bonnet up: a cover bolted on each head over its camshafts, the coils
 * through it onto the plugs, the timing cover across the front over the chain, and over it all the trim cover, but on
 * a boxer, whose heads lie out either side and have nothing over them to hide. A twin wears its cam covers and coils
 * only: its cam chains, each up its own cylinder's side, are not yet made, nor what closes them in. Made once, its
 * parts kept; joined to the engine only when it is asked to be covered.
 */
final class Covers {

    private final List<Part> heads = new ArrayList<>();
    private final List<Part> coils = new ArrayList<>();
    private final Optional<Part> timing;
    private final Optional<Part> trim;

    /** Its parts, made to {@code figures}. */
    Covers(final EngineFigures figures) {
        for (int b = 0; b < figures.banks(); b++) {
            heads.add(new HeadCover(figures, b).part());
            coils.add(new CoilPacks(figures, b).part());
        }
        this.timing = figures.twin() ? Optional.empty() : Optional.of(new TimingCover(figures).part());
        this.trim = figures.boxer() || figures.twin() ? Optional.empty() : Optional.of(new TrimCover(figures).part());
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
        timing.ifPresent(cover -> joined.add(new Connection(cover.name() + " on the block", block, "crank, front",
                cover, "crank", Joining.BOLTED)));
        trim.ifPresent(cover -> joined.add(new Connection(cover.name() + " clipped on", heads.getFirst(), "top",
                cover, "clip", Joining.SEATED)));
        return joined;
    }

    /** Whether {@code part} is one of them. */
    boolean has(final Part part) {
        return heads.contains(part) || coils.contains(part) || timing.filter(t -> t == part).isPresent()
                || trim.filter(t -> t == part).isPresent();
    }

    /** How many there are. */
    int count() {
        return heads.size() + coils.size() + (timing.isPresent() ? 1 : 0) + (trim.isPresent() ? 1 : 0);
    }
}
