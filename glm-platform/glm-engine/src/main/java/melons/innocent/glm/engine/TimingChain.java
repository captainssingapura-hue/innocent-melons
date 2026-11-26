package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
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
 * A bank's timing chain, in steel, running as its figures say ({@link ChainFigures}): its links all alike, each its
 * side plates along the chain's line with a roller across at its leading end, as many as go round evenly. As the
 * crank turns, the chain runs with its sprocket's teeth, as far along as they go round; a link a pitch on is as the
 * one before was, so the chain is drawn as its links, each placed where it has run to. A twin's is the cam chain up
 * to its own head. Its point: the crank's axis in its plane, where it runs on the crank's sprocket.
 *
 * @param figures the engine's figures
 * @param bank    its bank, from nought
 */
record TimingChain(EngineFigures figures, int bank) {

    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    /** How high a link's plates are, across the chain's line; how far round a roller is. */
    private static final double HIGH = 0.5;
    private static final double ROLLER = 0.2;

    /** What it is called. */
    String called() {
        return figures.name() + (figures.twin() ? " cam chain" : " timing chain")
                + (figures.banks() == 2 ? ", bank " + (bank + 1) : "");
    }

    /** It, its links where they are at rest. */
    Part part() {
        final ChainFigures chain = new ChainFigures(figures, bank);
        final Mesh link = link();
        final List<Merged.Placed> placed = places(0).stream().map(m -> new Merged.Placed(link, m)).toList();
        return new Part(called(), Group.ENGINE, false, List.of(new Point("crank", ALONG.times(chain.plane()))),
                new Merged(placed).mesh(), new Made(Material.STEEL, true))
                .as(figures.twin() ? new EngineParts.CamChain() : new EngineParts.TimingChain());
    }

    /** One link, in its own frame: its plates along +x from its roller at nought, across the plane of the chain. */
    Mesh link() {
        final double pitch = new ChainFigures(figures, bank).pitch();
        final Shapes shapes = new Shapes(8);
        return shapes.joined(List.of(shapes.box(new Vec3(pitch / 2, 0, 0), pitch * 0.96, HIGH, ChainFigures.WIDE),
                shapes.drum(new Vec3(0, 0, -ChainFigures.WIDE / 2 - 0.05),
                        new Vec3(0, 0, ChainFigures.WIDE / 2 + 0.05), ROLLER)));
    }

    /**
     * Where each link is, the crank turned {@code turned} degrees on from rest, in the block's frame: run back round
     * its line, the way the crank's sprocket's teeth go, as far as they have gone.
     */
    List<Mat4> places(final double turned) {
        final ChainFigures chain = new ChainFigures(figures, bank);
        final Belt belt = chain.belt();
        final double pitch = chain.pitch();
        final double run = -chain.crank() * Math.toRadians(turned);
        final List<Mat4> places = new ArrayList<>();
        for (int i = 0; i < chain.links(); i++) {
            final Belt.On on = belt.at(i * pitch + run);
            final Belt.On next = belt.at((i + 1) * pitch + run);
            final double heading = Math.atan2(next.at().v() - on.at().v(), next.at().u() - on.at().u());
            places.add(new Mat4(new Vec3(on.at().u(), on.at().v(), chain.plane()))
                    .times(new Turned(ALONG, Math.toDegrees(heading)).matrix()));
        }
        return places;
    }
}
