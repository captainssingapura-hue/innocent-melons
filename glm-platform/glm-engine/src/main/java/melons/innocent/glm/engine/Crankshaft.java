package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * An engine's crankshaft, forged from titanium, bare, at rest in its block: a main journal in each bulkhead; a pin
 * for each throw, half the stroke out, where its rods' big ends turn, one rod's wide in line, two rods' wide where a
 * V's banks share it, two pins side by side where a V6's are split; webs from each throw's ends to the journals beside
 * it, each with its counterweight swung opposite the pin; the nose at the front for the timing drive, the flange at
 * the rear for the flywheel. Its points: each main journal, and each cylinder's pin under its rod.
 *
 * @param figures the engine's figures
 */
record Crankshaft(EngineFigures figures) {

    private static final int ROUND = 24;
    /** How far a journal reaches either side of its bulkhead; how thin a web may be; how far a counterweight swings. */
    private static final double JOURNAL = 1.3;
    private static final double THINNEST = 0.8;
    private static final double SWING = 70;

    /** It. */
    Part part() {
        final CrankFigures crank = new CrankFigures(figures);
        final List<Double> mains = new BlockForm(figures).bulkheads();
        final List<Point> points = new ArrayList<>();
        for (int j = 0; j < mains.size(); j++) {
            points.add(new Point("main " + (j + 1), new Vec3(0, 0, mains.get(j))));
        }
        for (int b = 0; b < figures.banks(); b++) {
            for (int k = 0; k < figures.perBank(); k++) {
                points.add(new Point("pin " + (b + 1) + "." + (k + 1), crank.pin(b, k)));
            }
        }
        return new Part(figures.name() + " crankshaft", Group.ENGINE, false, points, mesh(crank, mains),
                new Made(Material.TITANIUM, true));
    }

    private Mesh mesh(final CrankFigures crank, final List<Double> mains) {
        final Shapes shapes = new Shapes(ROUND);
        final List<Mesh> made = new ArrayList<>();
        for (final double z : mains) {
            made.add(shapes.drum(new Vec3(0, 0, z - JOURNAL), new Vec3(0, 0, z + JOURNAL), crank.main()));
        }
        for (int k = 0; k < figures.perBank(); k++) {
            final int last = crank.split() ? 1 : 0;
            for (int b = 0; b <= last; b++) {
                final double[] span = crank.span(b, k);
                final Vec3 pin = crank.pin(b, k);
                made.add(shapes.drum(new Vec3(pin.x(), pin.y(), span[0]), new Vec3(pin.x(), pin.y(), span[1]),
                        crank.pin()));
            }
            final double front = crank.span(0, k)[0];
            final double rear = crank.span(last, k)[1];
            final double before = mains.stream().filter(z -> z < front).mapToDouble(z -> z + JOURNAL).max()
                    .orElse(front - THINNEST);
            final double after = mains.stream().filter(z -> z > rear).mapToDouble(z -> z - JOURNAL).min()
                    .orElse(rear + THINNEST);
            made.add(web(crank, crank.angle(0, k), Math.min(before, front - THINNEST), front));
            made.add(web(crank, crank.angle(last, k), rear, Math.max(after, rear + THINNEST)));
        }
        made.add(shapes.drum(new Vec3(0, 0, -5), new Vec3(0, 0, mains.getFirst()), crank.main() * 0.8));
        final double end = figures.length();
        made.add(shapes.drum(new Vec3(0, 0, mains.getLast()), new Vec3(0, 0, end + 1.2), crank.main()));
        made.add(shapes.drum(new Vec3(0, 0, end), new Vec3(0, 0, end + 1.2), crank.main() * 1.7));
        return shapes.joined(made);
    }

    /** A web from {@code from} to {@code to} along the crank, round its pin at {@code angle}, its weight opposite. */
    private Mesh web(final CrankFigures crank, final double angle, final double from, final double to) {
        final double a = Math.toRadians(angle);
        final Spot pin = new Spot(crank.throwing() * Math.sin(a), crank.throwing() * Math.cos(a));
        final List<Spot> spots = new ArrayList<>();
        for (int k = 0; k < 16; k++) {
            final double t = 2 * Math.PI * k / 16;
            spots.add(new Spot(pin.u() + (crank.pin() + 1) * Math.cos(t), pin.v() + (crank.pin() + 1) * Math.sin(t)));
            spots.add(new Spot((crank.main() + 1) * Math.cos(t), (crank.main() + 1) * Math.sin(t)));
        }
        final double weight = crank.throwing() + 1.2;
        for (int k = 0; k <= 8; k++) {
            final double t = a + Math.PI + Math.toRadians(-SWING + 2 * SWING * k / 8);
            spots.add(new Spot(weight * Math.sin(t), weight * Math.cos(t)));
        }
        return new Plate(new Hull(spots).outline(), List.of(), new Vec3(0, 0, (from + to) / 2), new Vec3(1, 0, 0),
                new Vec3(0, 1, 0), to - from).mesh();
    }
}
