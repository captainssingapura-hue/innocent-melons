package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * What turns and runs inside an engine's block, joined as it moves, and the heads over it, each bolted to its bank's
 * deck ({@link CylinderHead}): the crankshaft turning in the block's main
 * bearings; each connecting rod's big end turning on its crankpin and its small end on its gudgeon pin; each gudgeon
 * pin seated in its piston; each piston sliding in its bore. Made once, its parts kept, so it can say how far each has
 * moved from where it was made when the crank has turned so far: the crank turned about its axis, each rod carried
 * with its crankpin and swung about it, each piston and its pin slid up or down its bore.
 */
public final class EngineInternals {

    private static final Vec3 AXIS = new Vec3(0, 0, 1);

    private final EngineFigures figures;
    private final Part crank;
    private final Map<Part, Cylinder> rods = new IdentityHashMap<>();
    private final Map<Part, Cylinder> sliding = new IdentityHashMap<>();
    private final List<Connection> inside = new ArrayList<>();
    private final Map<Part, String> pistons = new LinkedHashMap<>();
    private final List<Part> heads = new ArrayList<>();

    /** Its parts, made to {@code figures}. */
    public EngineInternals(final EngineFigures figures) {
        this.figures = figures;
        this.crank = new Crankshaft(figures).part();
        for (int b = 0; b < figures.banks(); b++) {
            heads.add(new CylinderHead(figures, b).part());
            for (int k = 0; k < figures.perBank(); k++) {
                final Cylinder cylinder = new Cylinder(figures, b, k);
                final Part rod = cylinder.rod();
                final Part pin = cylinder.gudgeon();
                final Part piston = cylinder.piston();
                rods.put(rod, cylinder);
                sliding.put(pin, cylinder);
                sliding.put(piston, cylinder);
                final String at = (b + 1) + "." + (k + 1);
                inside.add(new Connection(cylinder.called() + " big end", crank, "pin " + at, rod, "big end",
                        Joining.HINGE));
                inside.add(new Connection(cylinder.called() + " small end", rod, "small end", pin, "middle",
                        Joining.HINGE));
                inside.add(new Connection(cylinder.called() + " gudgeon pin", pin, "middle", piston, "pin",
                        Joining.SEATED));
                pistons.put(piston, at);
            }
        }
    }

    /** The engine alone, its block the root and all of it inside. */
    public Platform platform() {
        final Part block = new CastBlock(figures).part();
        return new Platform("the " + figures.name() + " engine", block, joined(block));
    }

    /** How what turns and runs is joined to {@code block}, in the order they are walked. */
    public List<Connection> joined(final Part block) {
        final List<Connection> joined = new ArrayList<>();
        final int mains = new BlockForm(figures).bulkheads().size();
        for (int j = 1; j <= mains; j++) {
            joined.add(new Connection(figures.name() + " main bearing " + j, block, "main bearing " + j, crank,
                    "main " + j, Joining.HINGE));
        }
        for (int b = 0; b < heads.size(); b++) {
            joined.add(new Connection(heads.get(b).name() + " on its deck", block, "deck, bank " + (b + 1),
                    heads.get(b), "deck", Joining.BOLTED));
        }
        joined.addAll(inside);
        pistons.forEach((piston, at) -> joined.add(new Connection(figures.name() + " " + at + " in its bore", piston,
                "bore", block, "cylinder " + at, Joining.SLIDING)));
        return joined;
    }

    /**
     * How far {@code part} has moved from where it was made, in the block's frame, when the crank has turned
     * {@code turned} degrees on from rest: nothing, for a part that does not move.
     */
    public Mat4 moved(final Part part, final double turned) {
        if (part == crank) {
            return new Turned(AXIS, -turned).matrix();
        }
        final Cylinder rod = rods.get(part);
        if (rod != null) {
            final Vec3 p0 = rod.bigEnd();
            final Vec3 q0 = rod.smallEnd();
            final Vec3 p = rod.bigEnd(turned);
            final Vec3 q = rod.smallEnd(turned);
            final double swung = Math.toDegrees(Math.atan2(q.y() - p.y(), q.x() - p.x())
                    - Math.atan2(q0.y() - p0.y(), q0.x() - p0.x()));
            return new Mat4(p).times(new Turned(AXIS, swung).matrix()).times(new Mat4(p0.times(-1)));
        }
        final Cylinder slid = sliding.get(part);
        if (slid != null) {
            return new Mat4(slid.smallEnd(turned).minus(slid.smallEnd()));
        }
        return new Mat4();
    }

    /** Whether {@code part} is one of its cylinder heads, cast as its block is. */
    public boolean head(final Part part) {
        return heads.contains(part);
    }

    /** A cylinder's combustion chamber whole, in its own frame, as the charge burning in it fills it. */
    public Mesh flame() {
        return new Chamber(new HeadFigures(figures)).filled();
    }

    /** What carries bank {@code bank}'s cylinder {@code k}'s own frame, its nought at its bore's top, to the block's. */
    public Mat4 frame(final int bank, final int k) {
        return new HeadFigures(figures).frame(bank, k);
    }

    /**
     * Where bank {@code bank}'s cylinder {@code k} burns in its bore, the crank turned {@code turned} degrees on from
     * rest, in the block's frame: from its piston's crown up its bore to the deck, over which its head's chamber is
     * ({@link #flame}); as what carries a drum a unit round and a unit high, from nought straight up, there.
     */
    public Mat4 chamber(final int bank, final int k, final double turned) {
        final Cylinder cylinder = new Cylinder(figures, bank, k);
        final Vec3 up = cylinder.up();
        final Vec3 side = up.cross(AXIS);
        final Vec3 crown = cylinder.smallEnd(turned).plus(up.times(figures.crown()));
        final double high = cylinder.deck().minus(crown).dot(up);
        final double round = figures.bore() / 2 - 0.05;
        return new Mat4(new double[] {side.x() * round, up.x() * high, 0, crown.x(),
            side.y() * round, up.y() * high, 0, crown.y(), side.z() * round, up.z() * high, round, crown.z(),
            0, 0, 0, 1});
    }
}
