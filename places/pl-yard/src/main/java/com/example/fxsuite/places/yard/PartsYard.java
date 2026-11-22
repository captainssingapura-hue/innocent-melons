package com.example.fxsuite.places.yard;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A car's parts spread out apart ({@link Spread}), as a place to walk and as the card draws it: the frame on the
 * ground, and the smaller parts along a bench of dressed stone.
 *
 * <p>Every part says what it is made of by a leaf of the tree of materials, and is drawn with that material's
 * surface ({@link Shades}); all the parts of one material are one form, drawn in one call.
 */
public final class PartsYard implements Place {

    private final Spread spread = new Spread();
    private final List<Formed> made = new ArrayList<>();
    private final Formed bench;

    /** The parts, on {@code card}, their layouts as {@code laid} says. */
    public PartsYard(final Card card, final Laid laid) {
        final Shades shades = new Shades();
        final Map<Material, Forming> byMaterial = new LinkedHashMap<>();
        for (final Part part : spread.parts().all()) {
            part.formed(byMaterial.computeIfAbsent(part.of(), material -> new Forming(new Vec3(0, 0, 0))),
                    spread.stands(part), spread.turned(part));
        }
        try {
            byMaterial.forEach((material, forming) -> made.add(
                    new Formed(card, new Look(laid, shades.of(material)), forming.formed())));
            final Vec3 size = spread.bench();
            this.bench = new Formed(card, new Look(laid, shades.of(new Materials.DressedStone())),
                    new Cuboid(size.x(), size.y(), size.z()).formed());
            made.add(bench);
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    @Override
    public Obstacles inTheWay() {
        return spread.inTheWay();
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        for (final Formed formed : made) {
            formed.drawn(pass, formed == bench ? new Mat4(spread.benchStands()) : new Mat4(), camera, 1);
        }
    }

    @Override
    public void close() {
        made.reversed().forEach(Formed::close);
        made.clear();
    }
}
