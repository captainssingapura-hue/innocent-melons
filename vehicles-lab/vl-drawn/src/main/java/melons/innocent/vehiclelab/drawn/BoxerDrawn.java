package melons.innocent.vehiclelab.drawn;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.vehiclelab.motorcycle.Boxer;
import melons.innocent.vehiclelab.motorcycle.Coat;
import melons.innocent.vehiclelab.motorcycle.Livery;
import melons.innocent.vehiclelab.motorcycle.Part;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The boxer on the card: each of its parts sent once, and a look for every kind of finish there is, so whatever its
 * owner chooses is drawn at once, with nothing sent again. Each part is drawn in the material lab's material for the
 * kind of finish its slot is in, painted the colour chosen where the kind takes one; standing where the bike stands,
 * the forks and the front wheel turned about the head tube as steered, and each wheel turned about its axle as it has
 * rolled. What is seen through is drawn apart, after everything else. Closed, it is gone from the card.
 */
public final class BoxerDrawn implements AutoCloseable {

    /** A part on the card. */
    private record Drawn(Part part, Shaped form) {
    }

    private final Boxer boxer = new Boxer();
    private final Map<Coat.Kind, Looked> looks = new EnumMap<>(Coat.Kind.class);
    private final List<Drawn> drawn = new ArrayList<>();

    /** The boxer, on {@code card}, its layouts as {@code laid} says. */
    public BoxerDrawn(final Card card, final Laid laid) {
        try {
            final Shades shades = new Shades();
            for (final Coat.Kind kind : Coat.Kind.values()) {
                looks.put(kind, new Looked(card, new Look(laid, shades.of(material(kind)))));
            }
            for (final Part part : boxer.parts()) {
                drawn.add(new Drawn(part, new Shaped(card, laid, part.form())));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** What the lab has for a finish of {@code kind}. */
    private Material material(final Coat.Kind kind) {
        return switch (kind) {
            case GLOSS_PAINT -> new Materials.GlossPaint();
            case METALLIC_PAINT -> new Materials.MetallicPaint();
            case ENAMEL -> new Materials.Enamel();
            case TITANIUM -> new Materials.Titanium();
            case GUN_METAL -> new Materials.Gunmetal();
            case STEEL -> new Materials.Steel();
            case BRONZE -> new Materials.Bronze();
            case GOLD -> new Materials.Gold();
            case RUBBER -> new Materials.Rubber();
            case GLASS -> new Materials.Glass();
            case LAMP -> new Materials.LitPaper();
        };
    }

    /**
     * What of it is not seen through, drawn by {@code pass} as {@code camera} sees it: standing as {@code placed}
     * says, its bars turned {@code steer} degrees, its wheels rolled {@code roll} degrees, finished as {@code livery}
     * says. What casts a shadow.
     */
    public void drawn(final Pass pass, final Camera camera, final Mat4 placed, final double steer, final double roll,
                      final Livery livery) {
        drawn(pass, camera, placed, steer, roll, livery, false);
    }

    /** What of it is seen through, drawn the same way: after everything else. */
    public void seenThrough(final Pass pass, final Camera camera, final Mat4 placed, final double steer,
                            final double roll, final Livery livery) {
        drawn(pass, camera, placed, steer, roll, livery, true);
    }

    private void drawn(final Pass pass, final Camera camera, final Mat4 placed, final double steer, final double roll,
                       final Livery livery, final boolean seenThrough) {
        for (final Drawn part : drawn) {
            final Coat coat = livery.coat(part.part().slot());
            final Looked look = looks.get(coat.kind());
            if (look.seenThrough() == seenThrough) {
                final double[] rgb = coat.rgb();
                look.drawn(pass, part.form(), standing(part.part().moves(), placed, steer, roll), camera, 1,
                        new Vec3(rgb[0], rgb[1], rgb[2]));
            }
        }
    }

    /** Where a part that turns as {@code moves} says stands: the bike's place, steered and rolled as it turns. */
    private Mat4 standing(final Part.Moves moves, final Mat4 placed, final double steer, final double roll) {
        final Mat4 steered = placed.times(new Turned(boxer.steering(), steer).matrix(Boxer.HEAD_TOP));
        final Vec3 axle = new Vec3(1, 0, 0);
        return switch (moves) {
            case BODY -> placed;
            case STEERED -> steered;
            case FRONT_WHEEL -> steered.times(new Turned(axle, roll).matrix(Boxer.FRONT_AXLE));
            case BACK_WHEEL -> placed.times(new Turned(axle, roll).matrix(Boxer.BACK_AXLE));
        };
    }

    @Override
    public void close() {
        drawn.forEach(d -> d.form().close());
        looks.values().forEach(Looked::close);
        drawn.clear();
        looks.clear();
    }
}
