package melons.innocent.glm.engine;

import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.taxonomy.Taxonomy;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.existences.EngineParts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Each engine's parts of their kinds: every part of every engine in the range, covered, of a kind in the engine
 * parts' tree and among the GLM's own, but the twin's crankcase, barrels, crank, heads, cam covers and cam chains,
 * which are the V2's own; its block in line, in a V or flat as its banks are; a crank, and a rod, a piston and a
 * gudgeon pin to each cylinder; a valve and its spring to each valve, two cams to each head and a cover over each, a
 * chain, its guide and its tensioner to each bank, its coils, its timing cover and, but on a boxer or the twin, its
 * trim cover.
 */
class PartKindsTest {

    private final Taxonomy tree = new EngineParts().tree();

    @Test
    void everyPartOfAKindAmongTheGlmsOwnButTheTwinsOwn() {
        for (final EngineFigures e : new EngineRange().all()) {
            for (final Part part : parts(e)) {
                assertTrue(part.kind().isPresent(), part.name() + " of no kind");
                assertTrue(tree.has(part.kind().get()), part.name());
                final String side = part.kind().get().lineage().get(1).title();
                final boolean own = e.twin() && (part.is(new EngineParts.Blocks())
                        || part.is(new EngineParts.TwinCamHead()) || part.is(new EngineParts.CamCover())
                        || part.is(new EngineParts.SplitPinCrankshaft()) || part.is(new EngineParts.CamChain()));
                assertTrue(side.startsWith(own ? "V2" : "Glm"), part.name() + " among the right ones, not " + side);
            }
        }
    }

    @Test
    void itsBlockAsItsBanksAreAndItsPartsCountedByKind() {
        final EngineRange range = new EngineRange();
        assertEquals(new EngineParts.InlineBlock(), new CastBlock(range.l5()).part().kind().get());
        assertEquals(new EngineParts.VeeBlock(), new CastBlock(range.v12()).part().kind().get());
        assertEquals(new EngineParts.BoxerBlock(), new CastBlock(range.b6()).part().kind().get());
        assertEquals(new EngineParts.TwinCrankcase(), new CastBlock(range.v2()).part().kind().get());
        final List<Part> twin = parts(range.v2());
        assertEquals(2, count(twin, new EngineParts.CylinderBarrel()));
        assertEquals(1, count(twin, new EngineParts.SplitPinCrankshaft()));
        assertEquals(2, count(twin, new EngineParts.TwinCamHead()));
        assertEquals(2, count(twin, new EngineParts.CamCover()));
        for (final EngineFigures e : range.all()) {
            final List<Part> parts = parts(e);
            final int cylinders = e.cylinders();
            final int valves = (int) count(parts, new EngineParts.Valve());
            final int glm = e.twin() ? 0 : 1;
            assertEquals(glm, count(parts, new EngineParts.GlmBlocks()), e.name());
            assertEquals(glm, count(parts, new EngineParts.Crankshaft()), e.name());
            assertEquals(cylinders, count(parts, new EngineParts.ConnectingRod()), e.name());
            assertEquals(cylinders, count(parts, new EngineParts.Piston()), e.name());
            assertEquals(cylinders, count(parts, new EngineParts.GudgeonPin()), e.name());
            assertTrue(valves >= 2 * cylinders, e.name() + " two valves at least to each cylinder");
            assertEquals(valves, count(parts, new EngineParts.ValveSpring()), e.name());
            assertEquals(glm * e.banks(), count(parts, new EngineParts.CylinderHead()), e.name());
            assertEquals(2 * e.banks(), count(parts, new EngineParts.Camshaft()), e.name());
            assertEquals(glm * e.banks(), count(parts, new EngineParts.HeadCover()), e.name());
            assertEquals(e.banks(), count(parts, new EngineParts.CoilPack()), e.name());
            assertEquals(1, count(parts, new EngineParts.TimingCover()), e.name());
            assertEquals(glm * e.banks(), count(parts, new EngineParts.TimingChain()), e.name());
            assertEquals((1 - glm) * e.banks(), count(parts, new EngineParts.CamChain()), e.name());
            assertEquals(e.banks(), count(parts, new EngineParts.ChainGuide()), e.name());
            assertEquals(e.banks(), count(parts, new EngineParts.ChainTensioner()), e.name());
            assertEquals(e.boxer() ? 0 : glm, count(parts, new EngineParts.TrimCover()), e.name());
            assertEquals(parts.size(), count(parts, new EngineParts.EnginePart()), e.name() + " every one counted");
        }
    }

    /** Every part of {@code e}, covered, once. */
    private List<Part> parts(final EngineFigures e) {
        return new Assembly(new EngineInternals(e).platform(true)).assembled().placed().keySet().stream()
                .map(PartAt::part).distinct().toList();
    }

    /** How many of {@code parts} are of the kind {@code kind}, or of a kind under it. */
    private long count(final List<Part> parts, final Node kind) {
        return parts.stream().filter(p -> p.is(kind)).count();
    }
}
