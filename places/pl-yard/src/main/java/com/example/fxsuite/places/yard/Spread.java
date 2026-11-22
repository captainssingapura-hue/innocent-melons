package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * A car's parts spread out apart to be looked at: the frame on the ground, and the smaller parts in a row along a
 * bench at table height, each by itself and none joined to another. Six metres ahead of where the avatar starts,
 * the bench a little to the left and the frame to the right.
 *
 * <p>It says where each part stands and how it is turned, and what is in the avatar's way: the bench and the frame,
 * but nothing that lies on the bench. Nothing here is drawn; {@link PartsYard} draws it.
 */
public record Spread(Parts parts) {

    /** How far ahead the row is, and how wide, deep and high the bench is, in centimetres. */
    private static final double AHEAD = 600;
    private static final double BENCH_WIDE = 560;
    private static final double BENCH_DEEP = 90;
    private static final double BENCH_HIGH = 80;
    private static final double BENCH_ACROSS = -20;
    /** Where the frame stands, to the right of the bench. */
    private static final double FRAME_ACROSS = 430;

    /** The parts as they are described, spread out. */
    public Spread() {
        this(new Parts());
    }

    /**
     * Where {@code part} stands: the frame on the ground to the right, lifted to rest on its lowest tube; every
     * other part on the bench, in the order they are listed, lifted to rest on the bench's top.
     */
    public Vec3 stands(final Part part) {
        if (part.name().equals("frame")) {
            return new Vec3(FRAME_ACROSS, part.lifted(), AHEAD);
        }
        final double[] across = {-250, -150, -60, 0, 80, 190};
        final int place = parts.all().indexOf(part) - 1;
        return new Vec3(BENCH_ACROSS + across[place], BENCH_HIGH + part.lifted(), AHEAD);
    }

    /** How far {@code part} is turned where it stands: the rim and the tyre a quarter, to show their faces. */
    public double turned(final Part part) {
        return part.name().equals("rim") || part.name().equals("tyre") ? 90 : 0;
    }

    /** The box round {@code part} where it stands. */
    public Block around(final Part part) {
        return part.inTheWay(stands(part), turned(part));
    }

    /** The bench: how wide, how high and how deep it is. */
    public Vec3 bench() {
        return new Vec3(BENCH_WIDE, BENCH_HIGH, BENCH_DEEP);
    }

    /** The middle of the bench. */
    public Vec3 benchStands() {
        return new Vec3(BENCH_ACROSS, BENCH_HIGH / 2, AHEAD);
    }

    /** What is in the avatar's way: the frame and the bench. */
    public Obstacles inTheWay() {
        return new Obstacles(List.of(around(parts.frame()),
                new Block(BENCH_ACROSS, AHEAD, BENCH_WIDE / 2, BENCH_DEEP / 2, 0, 0, BENCH_HIGH)));
    }
}
