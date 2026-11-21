package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * A box: how far across, how high and how far along, in centimetres, its middle at its frame's middle. Each of its
 * six faces is flat and has four points of its own, so its edges are sharp.
 */
public record Cuboid(double across, double up, double along) {

    public Cuboid {
        if (!(across > 0 && up > 0 && along > 0)) {
            throw new IllegalArgumentException("a box has some size each way: " + across + " by " + up + " by "
                    + along);
        }
    }

    /** Its six faces as triangles, two to a face. */
    public Mesh formed() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final Vec3 half = new Vec3(across / 2, up / 2, along / 2);
        final List<Vec3> ways = List.of(new Vec3(1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, 1));
        for (int way = 0; way < ways.size(); way++) {
            final Vec3 out = ways.get(way);
            final Vec3 one = ways.get((way + 1) % ways.size());
            final Vec3 other = ways.get((way + 2) % ways.size());
            for (final int side : new int[] {1, -1}) {
                final int first = forming.point(corner(half, out.times(side), one, other, -1, -1), out.times(side));
                forming.point(corner(half, out.times(side), one, other, 1, -1), out.times(side));
                forming.point(corner(half, out.times(side), one, other, 1, 1), out.times(side));
                forming.point(corner(half, out.times(side), one, other, -1, 1), out.times(side));
                forming.triangle(first, first + 1, first + 2);
                forming.triangle(first, first + 2, first + 3);
            }
        }
        return forming.formed();
    }

    /** A corner of the face that faces {@code out}: so far along the face's two ways, each to one side or the other. */
    private Vec3 corner(final Vec3 half, final Vec3 out, final Vec3 one, final Vec3 other, final int alongOne,
                        final int alongOther) {
        final Vec3 unit = out.plus(one.times(alongOne)).plus(other.times(alongOther));
        return new Vec3(unit.x() * half.x(), unit.y() * half.y(), unit.z() * half.z());
    }
}
