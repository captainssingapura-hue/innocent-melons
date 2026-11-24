package melons.innocent.building.walk;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.texture.materials.Grounds;
import com.example.fxsuite.vulkan.bench.Afoot;
import com.example.fxsuite.vulkan.bench.Walk;
import com.example.fxsuite.vulkan.term.Surface;
import melons.innocent.forms.Facets;
import melons.innocent.testkit.BaseMap;
import melons.innocent.testkit.Outline;
import melons.innocent.testkit.TestKit;

import java.util.Locale;
import java.util.function.Function;

/**
 * A building walked round, and into, on the test kit: stood where it is built at the middle of a round base map of
 * turf, wide enough to stand well back from it, with the game engine's avatar starting some metres before its front
 * and facing it. The walk is the renderer's bench's own, with its keys, its jets, its light and its shadows.
 *
 * @param title     what the building is called
 * @param structure the building
 * @param looks     the surface each of its coats is drawn in
 */
public record WalkRound(String title, Structure structure, Function<Facets.Coat, Surface> looks) {

    private static final double METRE = 100;
    /** How far before the building's front the avatar starts, in centimetres. */
    private static final double BEFORE = 800;
    /** How far the map reaches past the building, in metres, and the least and the most it reaches at all. */
    private static final double PAST = 40;
    private static final double LEAST = 60;
    private static final double MOST = 5_000;
    private static final double BEHIND = 180;

    /** The base map it is stood on: a circle of turf reaching well past the furthest of it from its middle. */
    public BaseMap map() {
        double furthest = 0;
        for (final Solid solid : structure.solids()) {
            for (final Point corner : solid.corners()) {
                furthest = Math.max(furthest, Math.hypot(corner.across(), corner.along()));
            }
        }
        return new BaseMap(Outline.CIRCLE, Math.min(MOST, Math.max(LEAST, Math.ceil(furthest / METRE + PAST))),
                Grounds.TURF);
    }

    /**
     * Where the avatar starts, as a walk is told it (metres across and along, and degrees round): before its front,
     * facing it, across from its first way in, so the way in is before it; from the middle of its front, if it has
     * none; at the map's middle, for a building of nothing.
     */
    public String start() {
        final double[] across = {Double.MAX_VALUE, -Double.MAX_VALUE};
        double front = -Double.MAX_VALUE;
        for (final Solid solid : structure.solids()) {
            for (final Point corner : solid.corners()) {
                across[0] = Math.min(across[0], corner.across());
                across[1] = Math.max(across[1], corner.across());
                front = Math.max(front, corner.along());
            }
        }
        final double from = structure.waysIn().isEmpty() ? (across[0] + across[1]) / 2
                : structure.waysIn().getFirst().across();
        return across[0] > across[1] ? "" : String.format(Locale.ROOT, "%.2f,%.2f,%.0f", from / METRE,
                (front + BEFORE) / METRE, BEHIND);
    }

    /**
     * The walk round it, not yet begun, to be run beside a bench that is already running, on the thread its card's
     * windows want; it may be asked to stop from any other.
     */
    public Walk walk() {
        return new TestKit(title + ", walked round", map())
                .with((card, laid) -> new BuildingPlace(card, laid, structure, looks))
                .walk(new Afoot(start())).beside();
    }
}
