package melons.innocent.building.bench;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import melons.innocent.forms.Facets;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A building looked into closely, in a window of the card's, drawn by Vulkan as finely as it is built: every solid,
 * however small, a mesh to each material in the texture lab's look for it, where it has one ({@link LabLooks}), lit
 * by the day; the eye going round it as the mouse is dragged and nearer or further as the wheel turns. Asked for from
 * the bench's thread, served on the thread the bench began on, which the card's windows want: one at a time, a new one
 * asked for closing the one open; served till the bench ends.
 */
public final class Inspector {

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 720;
    private static final double FIELD = 45;
    /** How many frames are drawn before what is looked into is saved, so all of it is on the card. */
    private static final int SETTLED = 30;

    private final BlockingQueue<Optional<Asked>> asked = new LinkedBlockingQueue<>();
    private final LabLooks looks = new LabLooks();
    /** A file to save what is first looked into to, and then end: {@code -Dbench.inspectSnapshot=}; none, at first. */
    private final String shot = System.getProperty("bench.inspectSnapshot", "");

    /** A building asked to be looked into: what it is called, and it. */
    private record Asked(String title, Structure structure) {
    }

    /** {@code structure} looked into, in a window called {@code title}: from any thread. */
    public void ask(final String title, final Structure structure) {
        asked.add(Optional.of(new Asked(title, structure)));
    }

    /** No more asked: the one open closed, and the serving ended. */
    public void end() {
        asked.add(Optional.empty());
    }

    /**
     * What is asked, served, one after another, till the end is asked: on the thread the bench began on. A building
     * the card refuses to draw is told of, and the next served all the same, so the bench goes on.
     */
    public void served() throws InterruptedException {
        Optional<Asked> next = asked.take();
        while (next.isPresent()) {
            try {
                next = inspected(next.get());
            } catch (final RuntimeException refused) {
                System.err.println("Not looked into: " + next.get().title());
                refused.printStackTrace();
                next = asked.take();
            }
        }
    }

    /** {@code it} drawn in a window of its own till that is closed or something else is asked; what is asked next. */
    private Optional<Asked> inspected(final Asked it) throws InterruptedException {
        final Facets facets = new Facets(it.structure().solids(), 0);
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window(it.title() + ", drawn on " + card.name(), WIDTH, HEIGHT)) {
            final Laid laid = new Laid();
            final Daylight day = new Daylight();
            final List<Formed> parts = new ArrayList<>();
            // what is seen through, glass, drawn after all that is not, as the card has it
            final List<Map.Entry<Facets.Coat, Facets.Sheet>> sheets = new ArrayList<>(facets.sheets().entrySet());
            sheets.sort(Comparator.comparing(sheet -> looks.surface(sheet.getKey()).seenThrough()));
            sheets.forEach(sheet -> parts.add(new Formed(card, new Look(laid, looks.surface(sheet.getKey())),
                    mesh(sheet.getValue()))));
            Orbit orbit = orbit(facets);
            window.shown(80, 80);
            int frames = 0;
            try {
                while (window.open() && asked.isEmpty()) {
                    desk.heard();
                    final Moved mouse = window.mouse();
                    orbit = mouse.dragged() ? orbit.dragged(mouse.across(), mouse.down()) : orbit;
                    orbit = mouse.scrolled() == 0 ? orbit
                            : orbit.scrolled(mouse.scrolled(), orbit.distance() / 100, orbit.distance() * 100);
                    final Camera camera = orbit.camera(FIELD, 10);
                    drawnOn(window, laid, day, parts, camera);
                    final List<String> said = card.heard();
                    if (!said.isEmpty()) {
                        throw new Refused("the validation layer", String.join("\n", said));
                    }
                    if (!shot.isEmpty() && ++frames == SETTLED) {
                        saved(card, laid, day, parts, camera);
                        return Optional.empty();
                    }
                }
            } finally {
                parts.forEach(Formed::close);
            }
        }
        return asked.take();
    }

    /** {@code parts} drawn on {@code target}, lit by {@code day}, seen by {@code camera}. */
    private void drawnOn(final Target target, final Laid laid, final Daylight day, final List<Formed> parts,
                         final Camera camera) {
        final double aspect = (double) target.width() / target.height();
        target.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(aspect),
                pass -> parts.forEach(part -> part.drawn(pass, new Vec3(0, 0, 0), camera)));
    }

    /** What the window shows, drawn again on a canvas and saved to the file {@link #shot} names. */
    private void saved(final Card card, final Laid laid, final Daylight day, final List<Formed> parts,
                       final Camera camera) {
        try (Canvas canvas = card.canvas(WIDTH, HEIGHT)) {
            drawnOn(canvas, laid, day, parts, camera);
            ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(shot));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Where the eye first looks from: round the middle of {@code facets}, far enough off to see them whole, as far
     * round as {@code -Dbench.turn=} says, 30 degrees if it does not.
     */
    private Orbit orbit(final Facets facets) {
        final double turn = Double.parseDouble(System.getProperty("bench.turn", "30"));
        final double[] low = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        facets.sheets().values().forEach(sheet -> {
            for (int i = 0; i < sheet.points().length; i++) {
                low[i % 3] = Math.min(low[i % 3], sheet.points()[i]);
                high[i % 3] = Math.max(high[i % 3], sheet.points()[i]);
            }
        });
        if (low[0] > high[0]) {
            return new Orbit(new Vec3(0, 0, 0), turn, 20, 1_000);
        }
        final double size = Math.max(high[0] - low[0], Math.max(high[1] - low[1], high[2] - low[2]));
        return new Orbit(new Vec3((low[0] + high[0]) / 2, (low[1] + high[1]) / 2, (low[2] + high[2]) / 2), turn, 20,
                Math.max(500, size * 2));
    }

    /** {@code sheet} as the card's mesh. */
    private Mesh mesh(final Facets.Sheet sheet) {
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        for (int i = 0; i < sheet.points().length; i += 3) {
            points.add(new Vec3(sheet.points()[i], sheet.points()[i + 1], sheet.points()[i + 2]));
            facings.add(new Vec3(sheet.facings()[i], sheet.facings()[i + 1], sheet.facings()[i + 2]));
        }
        final List<Integer> corners = new ArrayList<>();
        for (final int corner : sheet.corners()) {
            corners.add(corner);
        }
        return new Mesh(points, facings, corners);
    }
}
