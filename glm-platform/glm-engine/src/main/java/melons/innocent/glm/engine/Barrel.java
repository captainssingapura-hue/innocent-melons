package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;

/**
 * A V twin's cylinder barrel, cast in aluminium apart from its crankcase and bolted on it, in the block's frame: a
 * square of a bank's width round its bore, from its foot on the crankcase up to its deck, where its head is bolted;
 * its liner in its bore, down through its foot into the crankcase, short of the crank's swing. Its points: its foot,
 * and its deck and its cylinder's middle there, named as a block's are.
 *
 * @param figures the engine's figures
 * @param bank    the bank it stands in, from nought
 */
record Barrel(EngineFigures figures, int bank) {

    private static final Vec3 ALONG = new Vec3(0, 0, 1);
    private static final int ROUND = 24;

    /** It, called by its bank. */
    Part part() {
        final BlockForm block = new BlockForm(figures);
        final Vec3 up = block.up(bank);
        final Vec3 middle = ALONG.times(figures.length() / 2);
        final List<Point> points = List.of(new Point("foot", up.times(block.top()).plus(middle)),
                new Point("deck, bank " + (bank + 1), up.times(figures.deck()).plus(middle)),
                new Point("cylinder " + (bank + 1) + ".1", up.times(figures.deck()).plus(ALONG.times(
                        figures.along(bank, 0)))));
        return new Part(figures.name() + " cylinder barrel, bank " + (bank + 1), Group.ENGINE, false, points,
                mesh(block), new Made(Material.ALUMINIUM, true)).as(new EngineParts.CylinderBarrel());
    }

    private Mesh mesh(final BlockForm block) {
        final double half = block.half();
        final double along = figures.along(bank, 0);
        final double foot = block.top();
        final List<Spot> square = List.of(new Spot(-half, along - half), new Spot(half, along - half),
                new Spot(half, along + half), new Spot(-half, along + half));
        final List<List<Spot>> bore = List.of(new Outlines(ROUND).circle(new Spot(0, along),
                figures.bore() / 2 + block.liner()));
        final Vec3 up = block.up(bank);
        final Mesh cast = new Plate(square, bore, up.times((foot + figures.deck()) / 2), block.out(bank), ALONG,
                figures.deck() - foot).mesh();
        return new Shapes(ROUND).joined(List.of(cast, block.liner(bank, 0)));
    }
}
