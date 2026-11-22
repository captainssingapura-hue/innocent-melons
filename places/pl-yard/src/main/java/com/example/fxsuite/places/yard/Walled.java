package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * An oblong of ground walled round, with an opening in the middle of its front: a house with no roof, or a yard.
 * {@code middle} is the middle of the ground inside; it is {@code across} wide along its front and {@code deep}
 * from front to back, outside to outside; its walls stand {@code high} and are {@code thick}; its opening is
 * {@code doorWide} and {@code doorHigh}. Where the opening is lower than the walls the wall goes on over it, as
 * over a doorway; where it is as high, it is open to the sky, as a gateway. Unturned, its front faces back along
 * the world, toward whoever comes at it from the world's middle; {@code turned} degrees turn it about its middle,
 * to the right as they grow, as the engine turns anything. Everything is in centimetres.
 *
 * <p>Its walls are laid round it as one surface: starting at the left of its front and going round to the right,
 * so far round and so far up. The one place the laying does not meet itself is its back left corner, where a
 * bricklayer too would cut a brick to close the course.
 */
public record Walled(Vec3 middle, double turned, double across, double deep, double high, double thick,
                     double doorWide, double doorHigh) {

    public Walled {
        if (!(doorWide < across - 2 * thick && doorHigh <= high && thick > 0 && deep > 2 * thick)) {
            throw new IllegalArgumentException("an opening is within its front, and the walls leave room inside: "
                    + across + " by " + deep + " by " + high + ", walls " + thick + ", opening " + doorWide + " by "
                    + doorHigh);
        }
    }

    /** Its runs of wall: the front either side of the opening, and over it if a doorway; its right, back and left. */
    public List<Wall> walls() {
        final double left = -across / 2;
        final double right = across / 2;
        final double front = -deep / 2;
        final double back = deep / 2;
        final Vec3 toFront = new Vec3(0, 0, -1);
        final List<Wall> runs = new ArrayList<>();
        runs.add(run(left, front, -doorWide / 2, front, toFront, 0, 0));
        if (doorHigh < high) {
            runs.add(run(-doorWide / 2, front, doorWide / 2, front, toFront, doorHigh, -doorWide / 2 - left));
        }
        runs.add(run(doorWide / 2, front, right, front, toFront, 0, doorWide / 2 - left));
        runs.add(run(right, front + thick, right, back - thick, new Vec3(1, 0, 0), 0, across + thick));
        runs.add(run(right, back, left, back, new Vec3(0, 0, 1), 0, across + deep));
        // laid backward from the front's own beginning, so it meets the front and not the back
        runs.add(run(left, back - thick, left, front + thick, new Vec3(-1, 0, 0), 0, -(deep - thick)));
        return runs;
    }

    /**
     * A run from ({@code fromX}, {@code fromZ}) to ({@code toX}, {@code toZ}) about its middle, unturned, facing
     * {@code out}, from {@code low} up to its full height, beginning {@code along} round it: as it stands in the
     * world, turned as the whole is.
     */
    private Wall run(final double fromX, final double fromZ, final double toX, final double toZ, final Vec3 out,
                     final double low, final double along) {
        final Mat4 turn = new Turned(new Vec3(0, 1, 0), turned).matrix();
        return new Wall(middle.plus(turn.clip(new Vec3(fromX, 0, fromZ)).shown()),
                middle.plus(turn.clip(new Vec3(toX, 0, toZ)).shown()), turn.clip(out).shown(), low, high, thick,
                along);
    }

    /** Its walls added to {@code forming}, each point saying how far round it and how far up it is. */
    public void formed(final Forming forming) {
        walls().forEach(wall -> wall.formed(forming));
    }

    /** Its walls as triangles by themselves. */
    public Mesh formed() {
        final Forming forming = new Forming(middle);
        formed(forming);
        return forming.formed();
    }

    /** Its walls as what is in the way of whoever walks there, its opening left open. */
    public List<Block> inTheWay() {
        final List<Block> blocks = new ArrayList<>();
        walls().forEach(wall -> blocks.add(wall.inTheWay()));
        return blocks;
    }
}
