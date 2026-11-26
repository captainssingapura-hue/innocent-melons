package melons.innocent.vehicles.drawn;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.flight.Airframe;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.vehicles.osprey.Osprey;
import melons.innocent.vehicles.osprey.Part;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * The Osprey as a flight flies it and the card draws it: its figures from its model, and its parts sent to the card
 * once, each in the material lab's material for its finish (its paint the lab's gloss paint in a grey of its own, its
 * windows the lab's glass, its tyres rubber, its hubs gunmetal), drawn where it stands, each nacelle turned up about
 * its pivot and each rotor turned about its hub, the two rotors turning opposite ways. Its windows, being seen
 * through, are drawn last.
 */
public final class OspreyDrawn implements Airframe {

    /** Its grey: the builder's, a mid grey with a little blue in it. */
    private static final double GREY_RED = 0.43;
    private static final double GREY_GREEN = 0.46;
    private static final double GREY_BLUE = 0.48;
    /** Where the pilot's eye is: over the cockpit's floor, a little behind the windscreen. */
    private static final double EYE_UP = 255;
    private static final double EYE_BACK = 175;

    private final Osprey osprey = new Osprey();
    private final Map<Part.Finish, Looked> looks = new EnumMap<>(Part.Finish.class);
    private final List<Drawn> drawn = new ArrayList<>();

    /** A part on the card, and what it turns with. */
    private record Drawn(Part part, Shaped form) {
    }

    @Override
    public double top() {
        return osprey.figures().top();
    }

    @Override
    public double cruise() {
        return osprey.figures().cruise();
    }

    @Override
    public double tilted() {
        return osprey.figures().tilted();
    }

    @Override
    public double belowWheels(final double tilt) {
        return osprey.belowWheels(tilt);
    }

    @Override
    public Vec3 cockpit() {
        return new Vec3(0, EYE_UP, osprey.figures().length() * 0.46 - EYE_BACK);
    }

    @Override
    public void carded(final Card card, final Laid laid) {
        try {
            for (final Part.Finish finish : Part.Finish.values()) {
                looks.put(finish, new Looked(card, new Look(laid, new Shades().of(material(finish)))));
            }
            for (final Part part : osprey.parts()) {
                drawn.add(new Drawn(part, new Shaped(card, laid, part.form())));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** What the lab has for {@code finish}. */
    private Material material(final Part.Finish finish) {
        return switch (finish) {
            case PAINT -> new Materials.GlossPaint();
            case GLASS -> new Materials.Glass();
            case RUBBER -> new Materials.Rubber();
            case METAL -> new Materials.Gunmetal();
        };
    }

    @Override
    public void drawn(final Pass pass, final Camera camera, final Mat4 placed, final double tilt,
                      final double turned) {
        final Vec3 grey = new Vec3(GREY_RED, GREY_GREEN, GREY_BLUE);
        for (final boolean seenThrough : new boolean[] {false, true}) {
            for (final Drawn part : drawn) {
                final Looked look = looks.get(part.part().finish());
                if (look.seenThrough() == seenThrough) {
                    look.drawn(pass, part.form(), standing(part.part().moves(), placed, tilt, turned), camera, 1,
                            grey);
                }
            }
        }
    }

    /** Where a part that turns as {@code moves} says stands: the body's place, and its nacelle's and rotor's turns. */
    private Mat4 standing(final Part.Moves moves, final Mat4 placed, final double tilt, final double turned) {
        return switch (moves) {
            case BODY -> placed;
            case LEFT_NACELLE -> nacelle(placed, -1, tilt);
            case RIGHT_NACELLE -> nacelle(placed, 1, tilt);
            case LEFT_ROTOR -> rotor(nacelle(placed, -1, tilt), turned);
            case RIGHT_ROTOR -> rotor(nacelle(placed, 1, tilt), -turned);
        };
    }

    /** A nacelle's frame: at its pivot, its length turned up from ahead by {@code tilt}. */
    private Mat4 nacelle(final Mat4 placed, final int side, final double tilt) {
        final Vec3 pivot = osprey.pivot(side);
        final double c = Math.cos(Math.toRadians(tilt));
        final double s = Math.sin(Math.toRadians(tilt));
        return placed.times(new Mat4(new double[] {1, 0, 0, pivot.x(), 0, c, s, pivot.y(), 0, -s, c, pivot.z(),
            0, 0, 0, 1}));
    }

    /** A rotor's frame: at its nacelle's hub, turned {@code turned} degrees about its turning. */
    private Mat4 rotor(final Mat4 nacelle, final double turned) {
        final double c = Math.cos(Math.toRadians(turned));
        final double s = Math.sin(Math.toRadians(turned));
        return nacelle.times(new Mat4(new double[] {c, -s, 0, 0, s, c, 0, 0, 0, 0, 1, osprey.hub(), 0, 0, 0, 1}));
    }

    @Override
    public void close() {
        drawn.reversed().forEach(part -> part.form().close());
        drawn.clear();
        looks.values().forEach(Looked::close);
        looks.clear();
    }
}
