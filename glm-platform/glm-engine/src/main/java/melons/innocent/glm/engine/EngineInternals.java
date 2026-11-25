package melons.innocent.glm.engine;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * What turns and runs inside an engine's block, joined as it moves: the crankshaft turning in the block's main
 * bearings; each connecting rod's big end turning on its crankpin and its small end on its gudgeon pin; each gudgeon
 * pin seated in its piston; each piston sliding in its bore. At rest, as the crank stands at nought.
 *
 * @param figures the engine's figures
 */
public record EngineInternals(EngineFigures figures) {

    /** How what turns and runs is joined to {@code block}, in the order they are walked. */
    public List<Connection> joined(final Part block) {
        final Part crank = new Crankshaft(figures).part();
        final List<Connection> joined = new ArrayList<>();
        final int mains = new BlockForm(figures).bulkheads().size();
        for (int j = 1; j <= mains; j++) {
            joined.add(new Connection(figures.name() + " main bearing " + j, block, "main bearing " + j, crank,
                    "main " + j, Joining.HINGE));
        }
        for (int b = 0; b < figures.banks(); b++) {
            for (int k = 0; k < figures.perBank(); k++) {
                final Cylinder cylinder = new Cylinder(figures, b, k);
                final String at = (b + 1) + "." + (k + 1);
                final Part rod = cylinder.rod();
                final Part pin = cylinder.gudgeon();
                final Part piston = cylinder.piston();
                joined.add(new Connection(cylinder.called() + " big end", crank, "pin " + at, rod, "big end",
                        Joining.HINGE));
                joined.add(new Connection(cylinder.called() + " small end", rod, "small end", pin, "middle",
                        Joining.HINGE));
                joined.add(new Connection(cylinder.called() + " gudgeon pin", pin, "middle", piston, "pin",
                        Joining.SEATED));
                joined.add(new Connection(cylinder.called() + " in its bore", piston, "bore", block,
                        "cylinder " + at, Joining.SLIDING));
            }
        }
        return joined;
    }
}
