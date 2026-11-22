package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Pond;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * A village's small lake: still water within a low kerb of stone, lying on level ground. Its middle is at
 * {@code stands}; it is {@code across} centimetres over all the one way and {@code deep} the other, a rounded
 * shape that is not quite an oval, with a bay here and a point there, the same every time.
 *
 * <p>The ground here is level and cannot be dug, so the water does not lie in a hollow: it lies a little above
 * the ground, held by the kerb, which is as high again. The water itself is the engine's own still water, an
 * outline and how high its top is ({@link #water}); the engine calls that a pond whatever its size. The kerb is
 * runs of wall, as a yard's wall is, each some four metres long, and so gives both what is drawn of it and what is
 * in the way; the water is in nobody's way, since the engine wades in nothing yet.
 */
public record VillageLake(Vec3 stands, double across, double deep) {

    /** How long a run of the kerb is, more or less, and the fewest runs it goes round in. */
    private static final double RUN = 400;
    private static final int FEWEST = 24;
    /** How high and how thick the kerb is, and how high the water's top is, in centimetres. */
    private static final double KERB_HIGH = 30;
    private static final double KERB_THICK = 30;
    private static final double LEVEL = 14;

    public VillageLake {
        if (!(across > 4 * KERB_THICK && deep > 4 * KERB_THICK)) {
            throw new IllegalArgumentException("a lake has water within its kerb: " + across + " by " + deep);
        }
    }

    /** How many straight runs the kerb goes round in: enough that each is some four metres long. */
    public int sides() {
        final double round = Math.PI * (across + deep) / 2;
        return Math.max(FEWEST, 4 * (int) Math.round(round / RUN / 4));
    }

    /**
     * Where the kerb's outer foot is, {@code side} of the way round, brought {@code in} centimetres toward the
     * lake's middle: an oval, pushed out a little here and drawn in a little there.
     */
    private Vec3 rim(final int side, final double in) {
        final double round = 2 * Math.PI * side / sides();
        final double uneven = 1 + 0.07 * Math.sin(2 * round + 1) + 0.05 * Math.sin(3 * round)
                + 0.03 * Math.sin(5 * round + 2);
        final double x = across / 2 * Math.cos(round) * uneven;
        final double z = deep / 2 * Math.sin(round) * uneven;
        final double out = Math.hypot(x, z);
        return stands.plus(new Vec3(x * (out - in) / out, 0, z * (out - in) / out));
    }

    /** The water, as the engine has still water: its edge at the kerb's inner face, and how high its top is. */
    public Pond water() {
        final List<Vec2> edge = new ArrayList<>();
        for (int side = 0; side < sides(); side++) {
            final Vec3 at = rim(side, KERB_THICK);
            edge.add(new Vec2(at.x(), at.z()));
        }
        return new Pond(edge, LEVEL);
    }

    /** The kerb: one run of wall for each side, its outer face outward, each beginning where the last ended. */
    public List<Wall> kerb() {
        final List<Wall> runs = new ArrayList<>();
        final int sides = sides();
        double along = 0;
        for (int side = 0; side < sides; side++) {
            final Vec3 from = rim(side, 0);
            final Vec3 to = rim((side + 1) % sides, 0);
            final Vec3 run = to.minus(from).unit();
            final Vec3 square = new Vec3(run.z(), 0, -run.x());
            final Vec3 out = square.dot(from.plus(to).times(0.5).minus(stands)) > 0 ? square : square.times(-1);
            final Wall wall = new Wall(from, to, out, 0, KERB_HIGH, KERB_THICK, along);
            runs.add(wall);
            along += wall.length();
        }
        return runs;
    }

    /** The kerb's faces added to {@code forming}. */
    public void kerbed(final Forming forming) {
        kerb().forEach(run -> run.formed(forming));
    }

    /**
     * The water's top: one level sheet facing up, reaching a little under the kerb all round so that no ground
     * shows between them. Where on the water a point is, is where it is in the world: across and along.
     */
    public Mesh sheet() {
        final Vec3 top = new Vec3(0, LEVEL, 0);
        final Vec3 up = new Vec3(0, 1, 0);
        final int sides = sides();
        final Forming forming = new Forming(stands.plus(new Vec3(0, -100, 0)));
        final int middle = forming.point(stands.plus(top), up);
        for (int side = 0; side < sides; side++) {
            forming.point(rim(side, KERB_THICK / 2).plus(top), up);
        }
        for (int side = 0; side < sides; side++) {
            forming.triangle(middle, middle + 1 + side, middle + 1 + (side + 1) % sides);
        }
        return forming.formed();
    }

    /** What is in the way: the kerb, run by run. */
    public List<Block> inTheWay() {
        return kerb().stream().map(Wall::inTheWay).toList();
    }
}
