package melons.innocent.glm.engine;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * An engine's block, cast in aluminium, bare, to its figures, in its own frame: the crank's axis along z from the
 * block's front. Its points: the crank's axis at its front and at its rear, where the gearbox is bolted; each main
 * bearing; the middle of each bank's deck, where its head is bolted, and each cylinder's middle at its deck, or in a
 * twin's crankcase each barrel's foot ({@link Barrel}); its mounts, right and left, a twin's ahead and behind; and a
 * twin's oil pan's rim on its bottom ({@link OilPan}) and its countershaft at its back end, where the output
 * pulley turns ({@link BeltDrive}), and its shift shaft at its front end, where the gear lever's linkage turns it.
 *
 * @param figures the engine's figures
 */
public record CastBlock(EngineFigures figures) {

    /** It, called as the engine is called. */
    public Part part() {
        final BlockForm form = new BlockForm(figures);
        final List<Point> points = new ArrayList<>();
        points.add(new Point("crank, front", new Vec3(0, 0, 0)));
        points.add(new Point("crank, rear", new Vec3(0, 0, figures.length())));
        final List<Double> mains = form.bulkheads();
        for (int j = 0; j < mains.size(); j++) {
            points.add(new Point("main bearing " + (j + 1), new Vec3(0, 0, mains.get(j))));
        }
        for (int b = 0; b < figures.banks(); b++) {
            if (figures.twin()) {
                points.add(new Point("barrel, bank " + (b + 1), form.up(b).times(form.top())
                        .plus(new Vec3(0, 0, figures.length() / 2))));
                continue;
            }
            final Vec3 deck = form.up(b).times(figures.deck());
            points.add(new Point("deck, bank " + (b + 1), deck.plus(new Vec3(0, 0, figures.length() / 2))));
            for (int k = 0; k < figures.perBank(); k++) {
                points.add(new Point("cylinder " + (b + 1) + "." + (k + 1), deck.plus(new Vec3(0, 0,
                        figures.along(b, k)))));
            }
        }
        // its right, seen from behind as the car's is, is -x in its own frame, its front at nought
        points.add(new Point("mount, right", form.mount(-1)));
        points.add(new Point("mount, left", form.mount(1)));
        if (figures.twin()) {
            points.add(new Point("sump", new OilPan(figures).rim()));
            final Spot shaft = new GearboxFigures(figures).countershaft();
            points.add(new Point("countershaft, right", new Vec3(shaft.u(), shaft.v(), figures.length())));
            final Spot shift = new GearboxFigures(figures).shiftShaft();
            points.add(new Point("shift shaft, left", new Vec3(shift.u(), shift.v(), 0)));
        }
        return new Part(figures.name() + (figures.twin() ? " crankcase" : " block"), Group.ENGINE, false, points,
                form.mesh(), new Made(Material.ALUMINIUM, true)).as(kind());
    }

    /** What kind of block it is: for one bank in line, two in a V, or two lying flat; a twin's crankcase. */
    private Leaf<?> kind() {
        if (figures.twin()) {
            return new EngineParts.TwinCrankcase();
        }
        if (figures.boxer()) {
            return new EngineParts.BoxerBlock();
        }
        return figures.banks() == 1 ? new EngineParts.InlineBlock() : new EngineParts.VeeBlock();
    }
}
