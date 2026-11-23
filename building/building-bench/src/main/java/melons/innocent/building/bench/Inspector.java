package melons.innocent.building.bench;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import javafx.scene.paint.Color;
import melons.innocent.forms.Facets;
import melons.innocent.forms.fx.Hues;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A building looked into closely, in a window of the card's, drawn by Vulkan as finely as it is built: every solid,
 * however small, a mesh to each material in its colour, lit by the day; the eye going round it as the mouse is dragged
 * and nearer or further as the wheel turns. Asked for from the bench's thread, served on the thread the bench began
 * on, which the card's windows want: one at a time, a new one asked for closing the one open; served till the bench
 * ends.
 */
public final class Inspector {

    private static final int WIDTH = 1100;
    private static final int HEIGHT = 720;
    private static final double FIELD = 45;

    private final BlockingQueue<Optional<Asked>> asked = new LinkedBlockingQueue<>();
    private final Hues hues = new Hues();

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

    /** What is asked, served, one after another, till the end is asked: on the thread the bench began on. */
    public void served() throws InterruptedException {
        Optional<Asked> next = asked.take();
        while (next.isPresent()) {
            next = inspected(next.get());
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
            facets.sheets().forEach((material, sheet) -> parts.add(new Formed(card, new Look(laid,
                    surface(hues.of(material))), mesh(sheet))));
            Orbit orbit = orbit(facets);
            window.shown(80, 80);
            try {
                while (window.open() && asked.isEmpty()) {
                    desk.heard();
                    final Moved mouse = window.mouse();
                    orbit = mouse.dragged() ? orbit.dragged(mouse.across(), mouse.down()) : orbit;
                    orbit = mouse.scrolled() == 0 ? orbit
                            : orbit.scrolled(mouse.scrolled(), orbit.distance() / 100, orbit.distance() * 100);
                    final Camera camera = orbit.camera(FIELD, 10);
                    window.drawn(day.sky(), new Framed(laid, camera, day.light()).filling((double) WIDTH / HEIGHT),
                            pass -> parts.forEach(part -> part.drawn(pass, new Vec3(0, 0, 0), camera)));
                    final List<String> said = card.heard();
                    if (!said.isEmpty()) {
                        throw new Refused("the validation layer", String.join("\n", said));
                    }
                }
            } finally {
                parts.forEach(Formed::close);
            }
        }
        return asked.take();
    }

    /** Where the eye first looks from: round the middle of {@code facets}, far enough off to see them whole. */
    private Orbit orbit(final Facets facets) {
        final double[] low = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        facets.sheets().values().forEach(sheet -> {
            for (int i = 0; i < sheet.points().length; i++) {
                low[i % 3] = Math.min(low[i % 3], sheet.points()[i]);
                high[i % 3] = Math.max(high[i % 3], sheet.points()[i]);
            }
        });
        if (low[0] > high[0]) {
            return new Orbit(new Vec3(0, 0, 0), 30, 20, 1_000);
        }
        final double size = Math.max(high[0] - low[0], Math.max(high[1] - low[1], high[2] - low[2]));
        return new Orbit(new Vec3((low[0] + high[0]) / 2, (low[1] + high[1]) / 2, (low[2] + high[2]) / 2), 30, 20,
                Math.max(500, size * 2));
    }

    /** {@code colour} as a plain surface, a little shine. */
    private Surface surface(final Color colour) {
        return new Surface(new Colour(colour.getRed(), colour.getGreen(), colour.getBlue()), List.of(),
                new Colour(0.05, 0.05, 0.05), 8);
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
