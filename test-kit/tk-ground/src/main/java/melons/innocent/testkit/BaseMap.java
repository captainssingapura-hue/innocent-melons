package melons.innocent.testkit;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.texture.materials.Grounds;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import com.example.fxsuite.vulkan.term.Turns;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A base map for trying things on: level ground with an outline, reaching so many metres from its middle, covered
 * with one of the lab's grounds. From this one description come what is drawn of it (its ground, ruled with a pale
 * line every ten metres each way so that sizes can be judged by eye), and what is in the way: a wall round its edge
 * that nobody sees, higher than anything flies, so that nothing leaves the map on foot or in the air. The ground
 * ends at its edge and nothing is drawn beyond it.
 */
public record BaseMap(Outline outline, double metres, Grounds covered) {

    private static final double METRE = 100;
    private static final double SMALLEST = 10;
    private static final double LARGEST = 5_000;
    /** A line is ruled every so many centimetres each way, so wide. */
    private static final double RULED = 1_000;
    private static final double LINE = 6;
    private static final String PALE = "#e9e6da";
    /** How much of a line's colour shows over the ground. */
    private static final double SHOWN = 0.55;
    /** How thick the wall round the edge is, and how high, in centimetres. */
    private static final double THICK = 200;
    private static final double HIGH = 2_000_000;

    public BaseMap {
        if (!(metres >= SMALLEST && metres <= LARGEST)) {
            throw new IllegalArgumentException("a base map reaches from " + SMALLEST + " to " + LARGEST
                    + " metres from its middle, not " + metres);
        }
    }

    /**
     * The base map the run says: {@code -Dtest.outline=} circle, square, oval or star (a circle unless said),
     * {@code -Dtest.reach=} metres from its middle (1000), and {@code -Dtest.ground=} one of the lab's grounds by
     * its name (turf).
     */
    public BaseMap() {
        this(Outline.valueOf(System.getProperty("test.outline", "circle").trim().toUpperCase(Locale.ROOT)),
                Double.parseDouble(System.getProperty("test.reach", "1000")),
                Grounds.valueOf(System.getProperty("test.ground", "turf").trim().toUpperCase(Locale.ROOT)));
    }

    /** How far it reaches from its middle, in centimetres. */
    public double reach() {
        return metres * METRE;
    }

    /** Its edge: its corners on the ground, in order round its middle. */
    public List<Vec3> edge() {
        return outline.edge(reach());
    }

    /** Whether the spot {@code x} across and {@code z} along from its middle, in centimetres, is on the map. */
    public boolean holds(final double x, final double z) {
        final List<Vec3> edge = edge();
        boolean within = false;
        for (int corner = 0; corner < edge.size(); corner++) {
            final Vec3 from = edge.get(corner);
            final Vec3 to = edge.get((corner + 1) % edge.size());
            if (from.z() > z != to.z() > z
                    && x < from.x() + (z - from.z()) / (to.z() - from.z()) * (to.x() - from.x())) {
                within = !within;
            }
        }
        return within;
    }

    /** Its ground as a mesh: a fan from its middle to its edge, level, where height is nothing. */
    public Mesh formed() {
        final Vec3 up = new Vec3(0, 1, 0);
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final int middle = forming.point(new Vec3(0, 0, 0), up);
        final List<Integer> round = edge().stream().map(corner -> forming.point(corner, up)).toList();
        for (int corner = 0; corner < round.size(); corner++) {
            forming.faced(middle, round.get(corner), round.get((corner + 1) % round.size()));
        }
        return forming.formed();
    }

    /** What its ground is drawn with: the lab's ground it is covered with, ruled every ten metres each way. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Real across = new Turns(at.across().plus(new Real(LINE / 2)).over(RULED), new Real(1 / RULED))
                .pulse(LINE / RULED, seen.covers());
        final Real along = new Turns(at.along().plus(new Real(LINE / 2)).over(RULED), new Real(1 / RULED))
                .pulse(LINE / RULED, seen.covers());
        return covered.surface().marked(new Term("ruled lines", new Colour(PALE),
                across.plus(along).less(across.times(along)).times(SHOWN), LINE));
    }

    /** The wall round its edge, a run for each side: nobody sees it, and nothing goes through or over it. */
    public List<Block> inTheWay() {
        final List<Vec3> edge = edge();
        final List<Block> wall = new ArrayList<>();
        for (int corner = 0; corner < edge.size(); corner++) {
            final Vec3 from = edge.get(corner);
            final Vec3 to = edge.get((corner + 1) % edge.size());
            final Vec3 run = to.minus(from).unit();
            final Vec3 side = new Vec3(run.z(), 0, -run.x());
            final Vec3 out = side.dot(from.plus(to)) > 0 ? side : side.times(-1);
            final Vec3 middle = from.plus(to).times(0.5).plus(out.times(THICK / 2));
            // the engine turns a block as it turns anything about the upright: its own length then runs this way
            final double turned = Math.toDegrees(Math.atan2(-run.z(), run.x()));
            wall.add(new Block(middle.x(), middle.z(), to.minus(from).length() / 2 + THICK, THICK / 2, turned, 0,
                    HIGH));
        }
        return wall;
    }

    /** What it is, in a line of words. */
    public String said() {
        return String.format(Locale.ENGLISH, "The test ground: %s reaching %.0f m from its middle, of %s",
                outline.called(), metres, covered.name().toLowerCase(Locale.ROOT));
    }
}
