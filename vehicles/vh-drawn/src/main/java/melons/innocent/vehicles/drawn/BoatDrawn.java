package melons.innocent.vehicles.drawn;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.afloat.Vessel;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.vehicles.boat.BoatPart;
import melons.innocent.vehicles.boat.RiverBoat;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The river boat as a cruise drives it and the card draws it: its figures from its model, and its parts sent to the
 * card once, each in the material lab's material for its finish (its hull's paints the lab's gloss paint, each in
 * its own colour; its cabin and wheel varnished timber; its fenders rubber; its rails steel; its lanterns lit paper,
 * red), drawn where it floats, its wheel turned about its hub as far as it is put over.
 */
public final class BoatDrawn implements Vessel {

    /** Its top speed: ten knots, about what such a boat makes. */
    private static final double TOP = 514;
    /** How far round the wheel turns, put hard over: a turn and a half. */
    private static final double HARD_OVER = 540;

    private final RiverBoat boat = new RiverBoat();
    private final Map<BoatPart.Finish, Looked> looks = new EnumMap<>(BoatPart.Finish.class);
    private final List<Drawn> drawn = new ArrayList<>();

    /** A part on the card. */
    private record Drawn(BoatPart part, Shaped form) {
    }

    @Override
    public double top() {
        return TOP;
    }

    @Override
    public double draught() {
        return boat.draught();
    }

    @Override
    public double length() {
        return boat.length();
    }

    @Override
    public Vec3 helm() {
        return boat.helm();
    }

    @Override
    public void carded(final Card card, final Laid laid) {
        try {
            for (final BoatPart.Finish finish : BoatPart.Finish.values()) {
                looks.put(finish, new Looked(card, new Look(laid, new Shades().of(material(finish)))));
            }
            for (final BoatPart part : boat.parts()) {
                drawn.add(new Drawn(part, new Shaped(card, laid, part.form())));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** What the lab has for {@code finish}. */
    private Material material(final BoatPart.Finish finish) {
        return switch (finish) {
            case PAINT -> new Materials.GlossPaint();
            case TIMBER -> new Materials.Timber();
            case RUBBER -> new Materials.Rubber();
            case STEEL -> new Materials.Steel();
            case LANTERN -> new Materials.LitPaper();
        };
    }

    @Override
    public void drawn(final Pass pass, final Camera camera, final Mat4 placed, final double wheel) {
        for (final Drawn part : drawn) {
            final Mat4 standing = part.part().wheel() ? turned(placed, wheel) : placed;
            looks.get(part.part().finish()).drawn(pass, part.form(), standing, camera, 1, part.part().colour());
        }
    }

    /** The wheel's frame: at its hub, turned about its axle as far as the wheel is put over. */
    private Mat4 turned(final Mat4 placed, final double wheel) {
        final Vec3 hub = boat.hub();
        final double c = Math.cos(Math.toRadians(-wheel * HARD_OVER));
        final double s = Math.sin(Math.toRadians(-wheel * HARD_OVER));
        return placed.times(new Mat4(new double[] {c, -s, 0, hub.x(), s, c, 0, hub.y(), 0, 0, 1, hub.z(),
            0, 0, 0, 1}));
    }

    @Override
    public void close() {
        drawn.reversed().forEach(part -> part.form().close());
        drawn.clear();
        looks.values().forEach(Looked::close);
        looks.clear();
    }
}
