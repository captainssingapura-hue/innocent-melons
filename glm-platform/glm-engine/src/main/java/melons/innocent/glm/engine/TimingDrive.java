package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What drives an engine's camshafts from its crank, at the front: for each bank a timing chain from its sprocket on
 * the crank's nose up round its two camshafts' sprockets ({@link TimingChain}), a fixed guide beside its tight side
 * and a tensioner on its slack side ({@link ChainGuides}). Made once, its parts kept, so it can say where each chain's
 * links have run to when the crank has turned so far.
 */
final class TimingDrive {

    private final Map<Part, TimingChain> chains = new LinkedHashMap<>();
    private final List<Part> guides = new ArrayList<>();

    /** Its parts, made to {@code figures}. */
    TimingDrive(final EngineFigures figures) {
        for (int b = 0; b < figures.banks(); b++) {
            final TimingChain chain = new TimingChain(figures, b);
            chains.put(chain.part(), chain);
            final ChainGuides kept = new ChainGuides(figures, b);
            guides.add(kept.guide());
            guides.add(kept.tensioner());
        }
    }

    /** How its parts are joined to {@code block} and {@code crank}, in the order they are walked. */
    List<Connection> joined(final Part block, final Part crank) {
        final List<Connection> joined = new ArrayList<>();
        chains.forEach((part, chain) -> joined.add(new Connection(part.name() + " on its sprocket", crank,
                "sprocket, bank " + (chain.bank() + 1), part, "crank", Joining.SEATED)));
        guides.forEach(part -> joined.add(new Connection(part.name() + " on the block", block, "crank, front", part,
                "block", Joining.BOLTED)));
        return joined;
    }

    /** Whether {@code part} is one of its chains. */
    boolean chain(final Part part) {
        return chains.containsKey(part);
    }

    /** Where chain {@code part}'s links are, the crank turned {@code turned} degrees on from rest; none if not one. */
    List<Mat4> links(final Part part, final double turned) {
        final TimingChain chain = chains.get(part);
        return chain == null ? List.of() : chain.places(turned);
    }

    /** One link of chain {@code part}, in its own frame. */
    Mesh link(final Part part) {
        return chains.get(part).link();
    }

    /** How many parts it has. */
    int count() {
        return chains.size() + guides.size();
    }
}
