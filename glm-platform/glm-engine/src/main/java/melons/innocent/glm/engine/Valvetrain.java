package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.space.Mat4;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What opens and shuts an engine's valves, in each head: its two camshafts, turning in the head's towers at half the
 * crank's speed; and over each cylinder its four valves, each sliding in its guide as its cam's lobe presses on its
 * bucket, and its spring, seated on the head, squeezed as the valve opens and pushing it shut again. Made once, its
 * parts kept, so it can say how far each has moved when the crank has turned so far: each camshaft turned half as far
 * about its axis; each valve lifted as far as its cylinder's cycle says ({@link ValveTiming}); each spring as much
 * shorter.
 */
final class Valvetrain {

    private final EngineFigures figures;
    private final Firing firing;
    private final ValveTiming timing;
    private final Map<Part, Camshaft> cams = new LinkedHashMap<>();
    private final Map<Part, Valve> valves = new LinkedHashMap<>();
    private final Map<Part, Valve> springs = new LinkedHashMap<>();

    /** Its parts, made to {@code figures}. */
    Valvetrain(final EngineFigures figures) {
        this.figures = figures;
        this.firing = new Firing(figures);
        this.timing = new ValveTiming(figures);
        for (int b = 0; b < figures.banks(); b++) {
            for (final boolean inlet : List.of(true, false)) {
                final Camshaft cam = new Camshaft(figures, b, inlet);
                cams.put(cam.part(), cam);
                for (int k = 0; k < figures.perBank(); k++) {
                    for (final int side : List.of(-1, 1)) {
                        final Valve valve = new Valve(figures, b, k, inlet, side);
                        valves.put(valve.valve(), valve);
                        springs.put(valve.spring(), valve);
                    }
                }
            }
        }
    }

    /** How its parts are joined to the heads {@code heads}, one for each bank, in the order they are walked. */
    List<Connection> joined(final List<Part> heads) {
        final List<Connection> joined = new ArrayList<>();
        cams.forEach((part, cam) -> joined.add(new Connection(part.name() + " in its towers", heads.get(cam.bank()),
                (cam.inlet() ? "inlet" : "exhaust") + " camshaft, front", part, "front", Joining.HINGE)));
        valves.forEach((part, valve) -> joined.add(new Connection(part.name() + " in its guide",
                heads.get(valve.bank()), valve.called(), part, "seat", Joining.SLIDING)));
        springs.forEach((part, valve) -> joined.add(new Connection(part.name() + " on its seat",
                heads.get(valve.bank()), valve.called().replace("valve", "spring seat"), part, "seat",
                Joining.SEATED)));
        return joined;
    }

    /** How far {@code part} has moved, the crank turned {@code turned} degrees on from rest; or none, not its part. */
    Mat4 moved(final Part part, final double turned) {
        final Camshaft cam = cams.get(part);
        if (cam != null) {
            return cam.turned(turned);
        }
        final Valve valve = valves.get(part);
        if (valve != null) {
            return valve.lifted(lift(valve, turned));
        }
        final Valve sprung = springs.get(part);
        return sprung != null ? sprung.squeezed(lift(sprung, turned)) : null;
    }

    /** How far {@code valve} is lifted, the crank turned {@code turned} degrees on from rest. */
    double lift(final Valve valve, final double turned) {
        return timing.lift(valve.inlet(), firing.since(valve.bank(), valve.k(), turned));
    }

    /** Its camshafts. */
    List<Camshaft> camshafts() {
        return List.copyOf(cams.values());
    }

    /** Its valves. */
    List<Valve> valves() {
        return List.copyOf(valves.values());
    }

    /** How many parts it has. */
    int count() {
        return cams.size() + valves.size() + springs.size();
    }

    /** The figures it was made to. */
    EngineFigures figures() {
        return figures;
    }
}
