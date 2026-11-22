package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * A car standing parked: where its middle is, on the ground; whether it is turned about, its nose the other way;
 * and its livery. The car is the lab's plain car body, 4.545 m long, 1.695 m wide and 1.4 m high, lying across the
 * world with its nose to the left until it is turned about.
 */
public record Parked(Vec3 stands, boolean about, Livery livery) {

    /** Half its length and half its width, and how high its body and its roof are, in centimetres. */
    private static final double HALF_LONG = 227.25;
    private static final double HALF_WIDE = 84.75;
    private static final double BELT = 90;
    private static final double ROOF = 140;
    /** Its cabin: how far behind the car's middle the cabin's middle is, half its length and half its width. */
    private static final double CABIN_BACK = 20.75;
    private static final double CABIN_HALF_LONG = 118.75;
    private static final double CABIN_HALF_WIDE = 74.5;
    private static final double HALF_TURN = 180;

    /** How far round it is turned, in degrees: not at all, or half way. */
    public double turned() {
        return about ? HALF_TURN : 0;
    }

    /** Where it stands and how it is turned, as one matrix, from the car's own frame into the world. */
    public Mat4 placed() {
        return new Mat4(stands).times(new Turned(new Vec3(0, 1, 0), turned()).matrix());
    }

    /**
     * The car as what is in the way: its body up to where its windows begin, and its cabin on that, which is
     * shorter and narrower and stands a little behind the car's middle.
     */
    public List<Block> inTheWay() {
        final double back = about ? -CABIN_BACK : CABIN_BACK;
        return List.of(new Block(stands.x(), stands.z(), HALF_LONG, HALF_WIDE, 0, 0, BELT),
                new Block(stands.x() + back, stands.z(), CABIN_HALF_LONG, CABIN_HALF_WIDE, 0, BELT, ROOF));
    }
}
