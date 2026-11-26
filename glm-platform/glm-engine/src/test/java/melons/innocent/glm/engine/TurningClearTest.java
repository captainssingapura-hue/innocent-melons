package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Part;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * What turns in a boxer and in a twin clear all the way round, their short decks bringing their bores down into the
 * crank's swing: the crank, each rod and each piston, the crank turned a twelfth of a turn at a time, none touching
 * the block it turns in, or a twin's crankcase and barrels, but where the crank's flange is bolted flush on its rear
 * face; and no piston touching the crank, its slipper skirt clear of the webs that pass under it at the bottom of its
 * stroke, nor a twin's two pistons each other, across its narrow V.
 */
class TurningClearTest {

    private final EngineRange range = new EngineRange();
    private final TurnedTouching touching = new TurnedTouching();

    @Test
    void theCrankTheRodsAndThePistonsClearOfTheBlockAndEachOtherAllTheWayRound() {
        for (final EngineFigures e : List.of(range.b4(), range.b6(), range.v2())) {
            final EngineInternals inside = new EngineInternals(e);
            final Assembled built = new Assembly(inside.platform()).assembled();
            final List<Part> parts = built.placed().keySet().stream().map(p -> p.part()).toList();
            final List<Part> cast = new ArrayList<>(named(parts, " block"));
            cast.addAll(named(parts, " crankcase"));
            cast.addAll(named(parts, " cylinder barrel"));
            final Part crank = named(parts, "crankshaft").getFirst();
            final List<Part> turning = new ArrayList<>(named(parts, " rod "));
            turning.add(crank);
            final List<Part> pistons = named(parts, " piston ");
            turning.addAll(pistons);
            final List<String> touched = new ArrayList<>();
            for (int step = 0; step < 12; step++) {
                final double turned = step * 30.0;
                for (final Part part : turning) {
                    for (final Part block : cast) {
                        touching.wheres(part.form(), inside.moved(part, turned), block.form(), new Mat4()).stream()
                                .filter(at -> part != crank || at.z() < e.length() - 0.05).findFirst()
                                .ifPresent(at -> touched.add(part.name() + " on the " + block.name() + " at "
                                        + turned + " " + written(at)));
                    }
                }
                for (final Part piston : pistons) {
                    touching.wheres(piston.form(), inside.moved(piston, turned), crank.form(),
                            inside.moved(crank, turned)).stream().findFirst().ifPresent(at -> touched.add(
                            piston.name() + " on the crank at " + turned + " " + written(at)));
                }
                if (e.twin()) {
                    touching.wheres(pistons.get(0).form(), inside.moved(pistons.get(0), turned),
                            pistons.get(1).form(), inside.moved(pistons.get(1), turned)).stream().findFirst()
                            .ifPresent(at -> touched.add("the pistons on each other at " + turned + " "
                                    + written(at)));
                }
            }
            assertEquals(List.of(), touched.stream().limit(6).toList(), e.name() + " clear all the way round");
        }
    }

    private List<Part> named(final List<Part> parts, final String called) {
        return parts.stream().filter(p -> p.name().contains(called)).toList();
    }

    /** Where {@code at} is, written shortly. */
    private String written(final Vec3 at) {
        return String.format("(%.1f, %.1f, %.1f)", at.x(), at.y(), at.z());
    }
}
