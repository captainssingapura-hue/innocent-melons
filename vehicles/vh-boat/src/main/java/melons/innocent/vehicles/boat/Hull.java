package melons.innocent.vehicles.boat;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Lettering;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A river boat's hull, lofted between its lines ({@link HullLines}) and painted as the Clarke Quay boats are: its
 * bottom and the lower half of its sides near black; above, a red line, a broad yellow band and red again at the
 * sheer; at the bow a green panel with a white chevron and an eye; its number lettered on the yellow band each side;
 * its deck plated dark grey; its transom dark. A surface between two lines is a point at each share of the way along
 * both ({@code u}, from the transom) and between them ({@code v}). Each colour is one mesh. The livery is copied from
 * a photograph of a Singapore river boat; its colours are the builder's reading of it.
 */
public record Hull(HullLines lines) {

    private static final int ALONG = 64;
    /** Where the bow's green panel begins, of the way along. */
    private static final double BOW = 0.78;
    private static final double PROUD = 0.8;

    /** The hull's colours, by name. */
    public enum Paint {
        DARK(0.13, 0.12, 0.11), RED(0.78, 0.16, 0.12), YELLOW(0.94, 0.77, 0.10), GREEN(0.12, 0.54, 0.29),
        WHITE(0.92, 0.92, 0.90), DECK(0.23, 0.24, 0.25);

        private final double r;
        private final double g;
        private final double b;

        Paint(final double r, final double g, final double b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        /** As much red, green and blue as it has. */
        public Vec3 colour() {
            return new Vec3(r, g, b);
        }
    }

    /** The hull's skin, a mesh for each colour. */
    public Map<Paint, Mesh> painted() {
        final Map<Paint, Forming> by = new LinkedHashMap<>();
        final List<Vec3> sheer = lines.line(HullLine.Kind.SHEER, ALONG);
        final List<Vec3> chine = lines.line(HullLine.Kind.CHINE, ALONG);
        final List<Vec3> keel = lines.line(HullLine.Kind.KEEL, ALONG);
        for (final int side : new int[] {-1, 1}) {
            final Skin bottom = new Skin(keel, chine, side);
            final Skin topsides = new Skin(chine, sheer, side);
            bottom.patch(forming(by, Paint.DARK), 0, 1, 0, 1);
            final double[][] bands = {{0, 0.55}, {0.55, 0.62}, {0.62, 0.88}, {0.88, 1}};
            final Paint[] plain = {Paint.DARK, Paint.RED, Paint.YELLOW, Paint.RED};
            for (int k = 0; k < bands.length; k++) {
                topsides.patch(forming(by, plain[k]), 0, BOW, bands[k][0], bands[k][1]);
            }
            topsides.patch(forming(by, Paint.DARK), BOW, 1, 0, 0.2);
            topsides.patch(forming(by, Paint.GREEN), BOW, 1, 0.2, 0.9);
            topsides.patch(forming(by, Paint.RED), BOW, 1, 0.9, 1);
            decals(by, topsides);
        }
        deck(forming(by, Paint.DECK), sheer);
        transom(forming(by, Paint.DARK), sheer.getFirst(), chine.getFirst(), keel.getFirst());
        final Map<Paint, Mesh> meshes = new LinkedHashMap<>();
        by.forEach((paint, forming) -> meshes.put(paint, forming.formed()));
        return meshes;
    }

    private Forming forming(final Map<Paint, Forming> by, final Paint paint) {
        return by.computeIfAbsent(paint, p -> new Forming(new Vec3(0, 30, 0)));
    }

    /** The chevron, the eye and the number, standing a hair proud of {@code side}. */
    private void decals(final Map<Paint, Forming> by, final Skin side) {
        final double thick = 0.022;
        side.stroke(forming(by, Paint.WHITE), 0.80, 0.28, 0.885, 0.55, thick, PROUD);
        side.stroke(forming(by, Paint.WHITE), 0.885, 0.55, 0.80, 0.82, thick, PROUD);
        side.disc(forming(by, Paint.WHITE), 0.935, 0.55, 20, PROUD);
        side.disc(forming(by, Paint.DARK), 0.935, 0.55, 8, PROUD * 2);
        side.lettered(forming(by, Paint.DARK), new Lettering(), "RC15", 0.365, 0.68, 22, PROUD);
    }

    /** The deck, plated level between the two sheers, from the transom to the stem head. */
    private void deck(final Forming forming, final List<Vec3> sheer) {
        final Vec3 up = new Vec3(0, 1, 0);
        for (int i = 0; i + 1 < sheer.size(); i++) {
            final Vec3 a = sheer.get(i);
            final Vec3 b = sheer.get(i + 1);
            final int p = forming.point(a, up);
            final int q = forming.point(b, up);
            final int r = forming.point(new Vec3(-b.x(), b.y(), b.z()), up);
            final int s = forming.point(new Vec3(-a.x(), a.y(), a.z()), up);
            forming.faced(p, q, r);
            forming.faced(p, r, s);
        }
    }

    /** The flat transom, closing the hull's stern, facing aft. */
    private void transom(final Forming forming, final Vec3 sheer, final Vec3 chine, final Vec3 keel) {
        final Vec3 aft = new Vec3(0, 0, -1);
        final List<Vec3> round = List.of(sheer, chine, keel, new Vec3(-chine.x(), chine.y(), chine.z()),
                new Vec3(-sheer.x(), sheer.y(), sheer.z()));
        final int middle = forming.point(new Vec3(0, (sheer.y() + keel.y()) / 2, sheer.z()), aft);
        for (int k = 0; k < round.size(); k++) {
            forming.faced(middle, forming.point(round.get(k), aft),
                    forming.point(round.get((k + 1) % round.size()), aft));
        }
    }
}
