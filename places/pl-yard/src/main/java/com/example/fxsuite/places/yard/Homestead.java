package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.brick.Brickwork;
import com.example.fxsuite.texture.stone.CoursedStone;
import com.example.fxsuite.texture.stone.Footed;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * A house standing in the yard, as a place to walk: an old Chinese house's walls with no roof on them yet, grey
 * brick laid three lengthwise and an end, on a footing of two courses of stone. It stands ten metres ahead of where
 * the avatar starts, its doorway toward the avatar: 7.2 m across its front and 5.4 m deep, its walls 3 m high and a
 * brick's length thick, its doorway 1.2 m wide and 2.2 m high, which the avatar walks through upright.
 *
 * <p>The whole house is one form with one look, drawn in one call; and what is in the avatar's way is the same walls.
 */
public final class Homestead implements Place {

    /** How high the stone footing stands: two courses of stone and their joints. */
    private static final double FOOTING = 61.6;

    private final Walled house = new Walled(new Vec3(0, 0, 1000), 0, 720, 540, 300, 30, 120, 220);
    private final Formed walls;

    /** The house, on {@code card}, its layouts as {@code laid} says. */
    public Homestead(final Card card, final Laid laid) {
        this.walls = new Formed(card, new Look(laid, walling()), house.formed());
    }

    /** What its walls are laid of. */
    public Surface walling() {
        return new Footed(Brickwork.CHINESE_GREY.wall(), new CoursedStone().wall().surface(), FOOTING).surface();
    }

    /** The house as it is described. */
    public Walled house() {
        return house;
    }

    @Override
    public Obstacles inTheWay() {
        return new Obstacles(house.inTheWay());
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        walls.drawn(pass, new Mat4(), camera, 1);
    }

    @Override
    public void close() {
        walls.close();
    }
}
