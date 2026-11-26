package melons.innocent.glm.existences;

import com.example.fxsuite.taxonomy.Branch;
import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What exists in the GLM platform: nothing wrong with its tree, every node with its meaning. The engine parts in seven
 * groups, each split into the GLM's own and the V2's, and every part under one of the two.
 */
class ExistencesTest {

    private final Taxonomy parts = new EngineParts().tree();

    @Test
    void nothingWrongWithItsTreeEveryNodeWithItsMeaning() {
        assertEquals(List.of(), new Existences().problems());
        assertEquals(List.of("engine parts"), new Existences().trees().stream().map(Taxonomy::name).toList());
        assertEquals(43, parts.leaves().size());
        assertEquals(65, parts.nodes().size());
        assertEquals("What turns one head's cams from the crank, up its own cylinder's side.",
                parts.meaning(new EngineParts.CamChain()));
    }

    @Test
    void sevenGroupsEachSplitIntoTheGlmsOwnAndTheV2s() {
        final List<Branch> groups = parts.branches().stream()
                .filter(branch -> branch.lineage().size() == 2).toList();
        assertEquals(List.of(new EngineParts.Blocks(), new EngineParts.Cranks(), new EngineParts.Heads(),
                new EngineParts.Covers(), new EngineParts.Electrics(), new EngineParts.Drives(),
                new EngineParts.Fluids()), groups);
        for (final Branch group : groups) {
            final List<String> split = parts.branches().stream()
                    .filter(branch -> branch.lineage().size() == 3 && branch.lineage().get(1).equals(group))
                    .map(Branch::title).toList();
            assertEquals(List.of("Glm" + group.title(), "V2" + group.title()), split, group.title());
        }
    }

    @Test
    void everyPartUnderTheGlmsOwnOrTheV2s() {
        assertEquals(List.of(new EngineParts.InlineBlock(), new EngineParts.VeeBlock(), new EngineParts.BoxerBlock(),
                new EngineParts.CylinderLiner()), parts.under(new EngineParts.GlmBlocks()));
        assertEquals(List.of(new EngineParts.TwinCrankcase(), new EngineParts.CylinderBarrel()),
                parts.under(new EngineParts.V2Blocks()));
        assertEquals(List.of(new EngineParts.SplitPinCrankshaft(), new EngineParts.PrimaryBalancer(),
                new EngineParts.SecondaryBalancer()), parts.under(new EngineParts.V2Cranks()));
        assertEquals(List.of(new EngineParts.CamCover(), new EngineParts.ClutchCover(),
                new EngineParts.AlternatorCover()), parts.under(new EngineParts.V2Covers()));
        assertEquals(List.of(new EngineParts.Piston(), new EngineParts.GlmCranks(), new EngineParts.Cranks(),
                new EngineParts.EnginePart()), new EngineParts.Piston().lineage());
        assertEquals("v2-drives", new EngineParts.V2Drives().name());
        for (final var part : parts.leaves()) {
            final String side = part.lineage().get(1).title();
            assertTrue(side.startsWith("Glm") || side.startsWith("V2"), part.title());
        }
    }
}
