package melons.innocent.glm.enginebench;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.sound.Engine;
import com.example.fxsuite.avatar.sound.Vehicle;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Moved;
import com.example.fxsuite.vulkan.device.Refused;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.device.Window;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Light;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.application.Platform;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.drive.Driver;
import melons.innocent.glm.drive.DriverKeys;
import melons.innocent.glm.drive.Drivetrain;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.glm.engine.EngineInternals;
import melons.innocent.glm.engine.EngineRange;
import melons.innocent.glm.engine.Firing;
import melons.innocent.glm.model.MadeOf;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The GLM engine bench: one engine of the range running, seen and heard. On the card, its crank turns in its block,
 * its rods swinging and its pistons sliding, each charge seen burning as its cylinder fires, slowed as much as asked
 * so the eye can follow; on the speakers, the same engine at its true revs, in its own voice ({@link EngineVoices}),
 * firing in the order its crank and cam make it, turbocharged if asked. It drives a car
 * as that engine would ({@link Drivetrain}), driven at the card's window by its keys ({@link DriverKeys}): pulling
 * away, changing gear by itself or at the driver's word, held at a speed by its cruise control; its revs, gear and
 * speed drawn over the picture. Two windows, as every bench has: the card's, where the keys drive and the mouse moves
 * the view; and a JavaFX window beside it ({@link EngineConsole}) to set the engine up.
 *
 * <p>For a run with nobody at it, played quietly: {@code -Dengine.pick=} the engine's place in the range from nought;
 * {@code -Dengine.throttle=} holds its throttle open so far, nought to one; {@code -Dengine.turned=} the crank's angle
 * to begin at; {@code -Dengine.actual=true} shows it at its actual speed; {@code -Dengine.covered=true} covers it;
 * {@code -Dengine.block=} GLASS, FINISHED or HIDDEN; {@code -Dbench.yaw=}, {@code -Dbench.pitch=},
 * {@code -Dbench.distance=} in metres; {@code -Dbench.settle=} so many thousandths of a second
 * ends it; {@code -Dbench.snapshot.card=} a file the card's picture is saved to as it ends.
 */
public final class EngineBench {

    private static final int WIDTH = 1000;
    private static final int HEIGHT = 640;
    private static final int LEFT = 30;
    private static final int TOP = 40;
    private static final int BESIDE = 780;
    private static final int EVENED = 30;
    private static final double BILLION = 1e9;
    private static final double MILLION = 1e6;
    private static final double METRE = 100;
    /** How high the crank stands over the floor. */
    private static final Vec3 LIFT = new Vec3(0, 45, 0);
    /**
     * The car each engine of the range drives, by its name, its own engine put in: the boxers the cars nearest the
     * sports cars they are after, the four the light coupe and the six the saloon of its power.
     */
    private static final Map<String, Vehicle> CARS = Map.of("L4", Vehicle.HATCHBACK, "B4", Vehicle.RALLY,
            "L5", Vehicle.RALLY, "V6", Vehicle.SALOON, "B6", Vehicle.SALOON, "V8", Vehicle.MUSCLE,
            "V12", Vehicle.SUPERCAR);

    private final Light noon = new Daylight().light();
    private final Asked asked = new Asked(1, true, new Asked.Sun(noon.sunHigh(), noon.sunRound()));
    private final Skies skies = new Skies(asked);
    private final Bus bus = new Bus();
    private final List<EngineFigures> range = new EngineRange().all();
    private final AtomicReference<EngineRun> run = new AtomicReference<>();
    private final DriverKeys keys = new DriverKeys();
    private final String held = System.getProperty("engine.throttle");
    private final EngineVoices voices = new EngineVoices();
    private Orbit orbit;
    private Drivetrain driven;
    private EngineRun fitted;
    private EngineFigures engine;
    private Firing firing;
    private double turned = said("engine.turned", 0);

    public static void main(final String[] args) throws IOException {
        new EngineBench().run();
    }

    private void run() throws IOException {
        final EngineRun first = first();
        run.set(first);
        bus.subscribe(EngineRun.class, run::set, Bus.HERE);
        final EngineConsole console = new EngineConsole(bus, asked, range);
        final boolean quiet = System.getProperty("bench.settle") != null;
        try (Desk desk = new Desk();
             Card card = new Card(desk);
             Window window = card.window("The GLM engine bench, drawn on " + card.name(), WIDTH, HEIGHT);
             EngineView view = new EngineView(card, new Laid().asking(card.rays()));
             EngineSound sound = new EngineSound(voice(first), quiet)) {
            asked.offered(view.offered());
            Platform.startup(() -> console.shown(LEFT, TOP, first));
            window.shown(LEFT + BESIDE, TOP);
            drawn(desk, card, window, view, sound, console);
        } finally {
            Platform.exit();
        }
    }

    /** How the engine runs to begin with, as the run says. */
    private EngineRun first() {
        final EngineRun plain = new EngineRun();
        return new EngineRun(Integer.getInteger("engine.pick", plain.engine()),
                Boolean.getBoolean("engine.actual") ? 1 : plain.slow(), plain.turbo(), plain.volume(),
                EngineRun.Block.valueOf(System.getProperty("engine.block", plain.block().name())), plain.made(),
                Boolean.getBoolean("engine.covered"));
    }

    /** Frame after frame, until a window is closed, the time asked for is up or the validation layer speaks. */
    private void drawn(final Desk desk, final Card card, final Window window, final EngineView view,
                       final EngineSound sound, final EngineConsole console) throws IOException {
        final long settle = Long.getLong("bench.settle", -1);
        final long began = System.nanoTime();
        long last = began;
        int frames = 0;
        while (window.open() && !asked.stopped()) {
            desk.heard();
            final EngineRun now = run.get();
            fit(now, view, sound);
            orbit = moved(window.mouse());
            final long time = System.nanoTime();
            final double seconds = (time - last) / BILLION;
            last = time;
            driven.step(held != null ? new Driver(Double.parseDouble(held)) : keys.read(window, seconds), seconds);
            turned = (turned + driven.rpm() / 60 * 360 * seconds * now.slow()) % 720;
            sound.set(driven.rpm(), driven.firing(), driven.load());
            sound.volume(now.volume());
            skies.stepped(seconds);
            frame(window, view, now);
            if (++frames % EVENED == 0) {
                bus.publish(new Bus.Figures(figures(now, sound)));
                console.sky(skies.said());
            }
            final List<String> heard = card.heard();
            if (!heard.isEmpty()) {
                throw new Refused("the validation layer", String.join("\n", heard));
            }
            if (settle >= 0 && (time - began) / MILLION >= settle) {
                break;
            }
        }
        final String snapshot = System.getProperty("bench.snapshot.card", "");
        if (!snapshot.isEmpty() && engine != null) {
            try (Canvas canvas = card.canvas(window.width(), window.height())) {
                frame(canvas, view, run.get());
                ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", new File(snapshot));
            }
        }
    }

    /** One frame of the engine as it runs now, drawn on {@code target}. */
    private void frame(final Target target, final EngineView view, final EngineRun now) {
        view.drawn(target, turned, now.block(), new MadeOf().with(Material.TITANIUM, now.made()), LIFT,
                orbit.camera(48, 1), skies.lit(), asked.shadowed(), asked.shadowing(), driven);
    }

    /**
     * The engine {@code now} asks for, fitted: another engine shown afresh, voiced and put in a new car, stopped; the
     * same one covered or uncovered shown afresh, the car going on; turbocharged or made otherwise, voiced afresh, the
     * car going on as it was.
     */
    private void fit(final EngineRun now, final EngineView view, final EngineSound sound) {
        final boolean other = fitted == null || now.engine() != fitted.engine();
        if (other || now.covered() != fitted.covered()) {
            engine = range.get(now.engine());
            firing = new Firing(engine);
            final EngineInternals inside = new EngineInternals(engine);
            final var whole = inside.platform(now.covered());
            view.showing(inside, firing, new Assembly(whole).assembled(), whole.root());
            if (other) {
                // a boxer's middle level with its crank, its banks lying either side of it
                final double high = engine.boxer() ? 0 : engine.deck() / 2;
                final Vec3 middle = LIFT.plus(new Vec3(0, high, engine.length() / 2));
                orbit = new Orbit(middle, said("bench.yaw", 35), said("bench.pitch", 20),
                        METRE * said("bench.distance", 1.6));
            }
        }
        if (other || now.turbo() != fitted.turbo() || now.made() != fitted.made()) {
            final Engine voice = voice(now);
            sound.engine(voice);
            if (other) {
                driven = new Drivetrain(CARS.get(engine.name()).with(voice));
            } else {
                driven.fitted(voice);
            }
        }
        fitted = now;
    }

    /** What the bench says back of the engine as it runs {@code now}, heard on {@code sound}. */
    private String figures(final EngineRun now, final EngineSound sound) {
        return String.format(Locale.ENGLISH, "%s, %.0f cc, in the %s%nFires %s%n%.0f rpm, firing %.0f a second%n"
                        + "Gear %s, %s, %.0f km/h%nBoost %.2f bar%n%s%nPlays on %s", engine.name(),
                engine.displacement(), driven.vehicle().name().toLowerCase(Locale.ENGLISH), firing.written(),
                driven.rpm(), driven.rpm() / 120 * engine.cylinders(), driven.gear(),
                driven.automatic() ? "automatic" : "manual", driven.kmh(), sound.boost(), now.slow() >= 1
                        ? "Shown at its actual speed" : String.format(Locale.ENGLISH, "Shown %.0f times slower",
                        1 / now.slow()), sound.device());
    }

    /** The engine {@code now} picks, in its own voice, what turns in it made as asked, turbocharged if asked. */
    private Engine voice(final EngineRun now) {
        return voices.voiced(range.get(now.engine()), now.made(), now.turbo());
    }

    /** The way of looking, moved as the mouse moved. */
    private Orbit moved(final Moved mouse) {
        final Orbit dragged = !mouse.dragged() ? orbit
                : mouse.other() ? orbit.carried(mouse.across(), mouse.down())
                : orbit.dragged(mouse.across(), mouse.down());
        return dragged.scrolled(mouse.scrolled(), 5, 2_000);
    }

    /** The number the run was told as {@code name}, or {@code otherwise} where it was told none. */
    private double said(final String name, final double otherwise) {
        return Double.parseDouble(System.getProperty(name, String.valueOf(otherwise)));
    }
}
