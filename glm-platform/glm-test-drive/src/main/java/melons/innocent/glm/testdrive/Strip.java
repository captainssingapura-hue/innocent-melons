package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * The straight's strip, as a place on the test kit: asphalt 2 km long and 12 m wide, along the kit from its middle
 * each way, laid a hair over the turf; its lines painted on it ({@link Lines}). Flat and open: nothing in the way, the
 * car running on the kit's own level ground. Closed, it is gone from the card.
 */
final class Strip implements Place {

    /** How thick the asphalt is, its top this far over the turf; and the paint over it. */
    private static final double THICK = 2;
    private static final double TOP = 0.5;
    private static final double PAINT = 0.3;

    private final List<Formed> formed = new ArrayList<>();

    /** On {@code card}, its layouts as {@code laid} says. */
    Strip(final Card card, final Laid laid) {
        final Lines lines = new Lines();
        final Shapes shapes = new Shapes(4);
        try {
            formed.add(new Formed(card, new Look(laid, new Shades().of(new Materials.Asphalt())),
                    shapes.box(new Vec3(0, TOP - THICK / 2, 0), lines.wide(), THICK, lines.length())));
            formed.add(new Formed(card, new Look(laid, new Surface(new Colour("#ecebe4"), List.of(),
                    new Colour("#202020"), 8)), lines.painted(shapes, TOP + PAINT / 2, PAINT)));
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    @Override
    public Obstacles inTheWay() {
        return Obstacles.NONE;
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        formed.forEach(f -> f.drawn(pass, new Vec3(0, 0, 0), camera));
    }

    @Override
    public void close() {
        formed.reversed().forEach(Formed::close);
        formed.clear();
    }

    /**
     * Its lines, in centimetres, along the kit from its middle: a solid line along each edge, a dashed line down the
     * middle between its two lanes, a start line across 30 m from its near end, a finish line 1.9 km on, and a short
     * mark at each edge every 100 m between.
     */
    record Lines() {

        private static final double LENGTH = 200_000;
        private static final double WIDE = 1_200;
        private static final double EDGE = 15;
        private static final double DASH = 300;
        private static final double GAP = 900;
        private static final double ACROSS = 50;
        private static final double MARK = 200;
        private static final double EVERY = 10_000;
        private static final double FROM_END = 3_000;
        private static final double RUN = 190_000;

        double length() {
            return LENGTH;
        }

        double wide() {
            return WIDE;
        }

        /** Where the start line is along it, and the finish line. */
        double start() {
            return -LENGTH / 2 + FROM_END;
        }

        double finish() {
            return start() + RUN;
        }

        /** Its paint, {@code high} over the turf and {@code thick}, as one form. */
        Mesh painted(final Shapes shapes, final double high, final double thick) {
            final List<Mesh> made = new ArrayList<>();
            for (final int side : List.of(-1, 1)) {
                made.add(shapes.box(new Vec3(side * (WIDE / 2 - 40), high, 0), EDGE, thick, LENGTH));
            }
            for (double z = -LENGTH / 2; z + DASH < LENGTH / 2; z += DASH + GAP) {
                made.add(shapes.box(new Vec3(0, high, z + DASH / 2), EDGE, thick, DASH));
            }
            for (final double z : List.of(start(), finish())) {
                made.add(shapes.box(new Vec3(0, high, z), WIDE - 80, thick, ACROSS));
            }
            for (double z = start() + EVERY; z < finish(); z += EVERY) {
                for (final int side : List.of(-1, 1)) {
                    made.add(shapes.box(new Vec3(side * (WIDE / 2 - 40 - MARK / 2), high, z), MARK, thick, EDGE * 2));
                }
            }
            return shapes.joined(made);
        }
    }
}
