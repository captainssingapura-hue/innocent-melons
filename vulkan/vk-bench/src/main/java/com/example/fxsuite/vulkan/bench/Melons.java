package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.subject.Watermelon;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.List;

/**
 * The wood of giant watermelons, as a place to walk: one melon on the card, drawn once for each melon of the wood,
 * as big and as turned as that one is; and each in the avatar's way.
 */
public final class Melons implements Place {

    private final Wood wood = new Wood(11, 1100);
    private final Watermelon watermelon = new Watermelon();
    private final List<Mat4> stand = wood.giants().stream().map(giant -> wood.stands(giant, watermelon.rests()))
            .toList();
    private final Formed melon;

    /** The wood, on {@code card}, its layouts as {@code laid} says. */
    public Melons(final Card card, final Laid laid) {
        this.melon = new Formed(card, new Look(laid, watermelon.rind()), watermelon.formed(96));
    }

    @Override
    public Obstacles inTheWay() {
        return wood.obstacles();
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        stand.forEach(stands -> melon.drawn(pass, stands, camera, 1));
    }

    @Override
    public void close() {
        melon.close();
    }
}
