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
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.EnumMap;
import java.util.Map;

/**
 * Every kind of finish on the card, a look for each, built once: so a vehicle's parts are drawn in whatever finishes
 * are chosen for them, changed at once, with nothing sent again. Each kind is drawn in the material lab's material
 * for it, a paint or a lamp's light in the colour its finish says, a bare metal or rubber in its own. Closed, it is
 * gone from the card.
 */
public final class Coats implements AutoCloseable {

    private final Map<Coat.Kind, Looked> looks = new EnumMap<>(Coat.Kind.class);

    /** Every kind of finish, on {@code card}, its layouts as {@code laid} says. */
    public Coats(final Card card, final Laid laid) {
        try {
            final Shades shades = new Shades();
            for (final Coat.Kind kind : Coat.Kind.values()) {
                looks.put(kind, new Looked(card, new Look(laid, shades.of(material(kind)))));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** Whether what is finished in {@code coat} is seen through, and so drawn after everything else. */
    public boolean seenThrough(final Coat coat) {
        return looks.get(coat.kind()).seenThrough();
    }

    /** {@code form}, finished in {@code coat}, drawn by {@code pass} as {@code camera} sees it, standing as at says. */
    public void drawn(final Pass pass, final Shaped form, final Mat4 at, final Camera camera, final Coat coat) {
        final double[] rgb = coat.rgb();
        looks.get(coat.kind()).drawn(pass, form, at, camera, 1, new Vec3(rgb[0], rgb[1], rgb[2]));
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
            case ALUMINIUM -> new Materials.Aluminium();
            case BRONZE -> new Materials.Bronze();
            case GOLD -> new Materials.Gold();
            case RUBBER -> new Materials.Rubber();
            case GLASS -> new Materials.Glass();
            case LAMP -> new Materials.LitPaper();
        };
    }

    @Override
    public void close() {
        looks.values().forEach(Looked::close);
        looks.clear();
    }
}
