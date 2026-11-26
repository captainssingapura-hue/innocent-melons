package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * A bank's ignition coils, one over each cylinder, moulded in plastic: each a shaft down through its hole in the head
 * cover and the spark plug's tube onto the plug, and over the cover its body and its plug for the wiring. Its point:
 * the middle of the head cover's top, where they sit.
 *
 * @param figures the engine's figures
 * @param bank    which bank, from nought
 */
record CoilPacks(EngineFigures figures, int bank) {

    private static final int ROUND = 16;
    /** How far round a coil's body is and its shaft; how high its body stands over the cover. */
    private static final double BODY = 1.5;
    private static final double SHAFT = 0.75;
    private static final double HIGH = 1.6;

    /** Them. */
    Part part() {
        final HeadFigures head = new HeadFigures(figures);
        final HeadCover cover = new HeadCover(figures, bank);
        final Shapes shapes = new Shapes(ROUND);
        final Vec3 up = head.way(bank, new Vec3(0, 1, 0));
        final Vec3 out = head.way(bank, new Vec3(1, 0, 0));
        final List<Mesh> made = new ArrayList<>();
        for (int k = 0; k < figures.perBank(); k++) {
            final Vec3 top = head.at(bank, k, new Vec3(0, cover.top(), 0));
            made.add(shapes.drum(head.at(bank, k, new Vec3(0, head.ridge() + 1.2, 0)), top, SHAFT));
            made.add(shapes.drum(top.minus(up.times(0.2)), top.plus(up.times(HIGH)), BODY));
            made.add(shapes.box(top.plus(up.times(HIGH - 0.4)).plus(out.times(BODY + 0.6)), 1.6, 0.9, 1.4));
        }
        return new Part(figures.name() + " ignition coils" + (figures.banks() == 2 ? ", bank " + (bank + 1) : ""),
                Group.COVERS, false, List.of(new Point("cover", cover.middle())), shapes.joined(made),
                new Made(Material.PLASTIC, true)).as(new EngineParts.CoilPack());
    }

    /** How high their bodies stand over the head cover's top. */
    double high() {
        return HIGH;
    }
}
