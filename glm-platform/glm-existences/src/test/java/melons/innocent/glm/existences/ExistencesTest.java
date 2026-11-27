package melons.innocent.glm.existences;

import com.example.fxsuite.taxonomy.Branch;
import com.example.fxsuite.taxonomy.Node;
import com.example.fxsuite.taxonomy.Taxonomy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What exists in the GLM platform: nothing wrong with its trees, every node with its meaning. The engine parts in
 * seven groups, each split into the GLM's own and the V2's, and every part under one of the two; the twin's primary
 * drive, clutch and gearbox each a group of its own among its drives. The motorcycle parts in eleven groups, each of
 * any motorcycle's kinds, the V2's bike's own beside them where it is made otherwise: its frames and its bodywork.
 */
class ExistencesTest {

    private final Taxonomy parts = new EngineParts().tree();
    private final Taxonomy bike = new MotorcycleParts().tree();

    @Test
    void nothingWrongWithItsTreesEveryNodeWithItsMeaning() {
        assertEquals(List.of(), new Existences().problems());
        assertEquals(List.of("engine parts", "motorcycle parts"),
                new Existences().trees().stream().map(Taxonomy::name).toList());
        assertEquals(58, parts.leaves().size());
        assertEquals(83, parts.nodes().size());
        assertEquals(44, bike.leaves().size());
        assertEquals(69, bike.nodes().size());
        assertEquals("What turns one head's cams from the crank, up its own cylinder's side.",
                parts.meaning(new EngineParts.CamChain()));
        assertEquals("The fuel tank under the seat, low, so the bike's weight is lower.",
                bike.meaning(new MotorcycleParts.UnderseatTank()));
    }

    @Test
    void sevenGroupsEachSplitIntoTheGlmsOwnAndTheV2s() {
        final List<Branch> groups = groups(parts);
        assertEquals(List.of(new EngineParts.Blocks(), new EngineParts.Cranks(), new EngineParts.Heads(),
                new EngineParts.Covers(), new EngineParts.Electrics(), new EngineParts.Drives(),
                new EngineParts.Fluids()), groups);
        for (final Branch group : groups) {
            assertEquals(List.of("Glm" + group.title(), "V2" + group.title()), split(parts, group), group.title());
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
            final String side = side(part);
            assertTrue(side.startsWith("Glm") || side.startsWith("V2"), part.title());
        }
    }

    @Test
    void theTwinsPrimaryDriveClutchAndGearboxEachAGroupAmongItsDrives() {
        assertEquals(List.of(new Transmission.Mainshaft(), new Transmission.Gearbox(), new EngineParts.V2Drives(),
                new EngineParts.Drives(), new EngineParts.EnginePart()), new Transmission.Mainshaft().lineage());
        assertEquals(2, parts.under(new Transmission.PrimaryDrive()).size());
        assertEquals(7, parts.under(new Transmission.WetClutch()).size());
        assertEquals(9, parts.under(new Transmission.Gearbox()).size());
        assertEquals(18 + 1, parts.under(new EngineParts.V2Drives()).size(), "and its cam chain");
    }

    @Test
    void elevenGroupsOfAnyMotorcyclesKindsTheV2sBikesOwnBesideThemWhereItDiffers() {
        final List<Branch> groups = groups(bike);
        assertEquals(List.of("Frames", "Suspension", "Controls", "Wheels", "Brakes", "FinalDrives", "Bodywork",
                "Intakes", "Exhausts", "Cooling", "Lamps"), groups.stream().map(Branch::title).toList());
        for (final Branch group : groups) {
            final boolean own = group.equals(new MotorcycleParts.Frames())
                    || group.equals(new MotorcycleParts.Bodywork());
            assertEquals(own ? List.of("Moto" + group.title(), "V2" + group.title()) : List.of("Moto"
                    + group.title()), split(bike, group), group.title());
        }
        assertEquals(List.of(new MotorcycleParts.TrellisFrame(), new MotorcycleParts.MidStructure()),
                bike.under(new MotorcycleParts.V2Frames()));
        assertEquals(List.of(new MotorcycleParts.AirboxCover(), new MotorcycleParts.UnderseatTank()),
                bike.under(new MotorcycleParts.V2Bodywork()));
    }

    /** The groups of {@code tree}: its branches under its root. */
    private List<Branch> groups(final Taxonomy tree) {
        return tree.branches().stream().filter(branch -> branch.lineage().size() == 2).toList();
    }

    /** What {@code group} is split into, in {@code tree}. */
    private List<String> split(final Taxonomy tree, final Branch group) {
        return tree.branches().stream()
                .filter(branch -> branch.lineage().size() == 3 && branch.lineage().get(1).equals(group))
                .map(Branch::title).toList();
    }

    /** Which side of its group {@code node} is on: the branch of its lineage under its group. */
    private String side(final Node node) {
        final List<Node> lineage = node.lineage();
        return lineage.get(lineage.size() - 3).title();
    }
}
