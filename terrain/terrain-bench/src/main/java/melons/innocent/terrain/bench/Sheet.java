package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Ground;

import java.util.ArrayList;
import java.util.List;

/**
 * The ground as the card draws it: a sheet of triangles over its extent, its points {@code every} centimetres apart
 * each way (the last row and column at the extent's far edges), each as high as the ground is there and facing as
 * the ground slopes there, so the sheet is lit smooth; two triangles to a square, each facing up out of the ground.
 * The sheet's frame is the map's own, so where a point is on the sheet is where it is on the map.
 */
public record Sheet() implements StatelessFunctionalObject {

    public static final Sheet INSTANCE = new Sheet();

    /** How far either side of a point the ground's slope there is taken over, as a share of the points' spacing. */
    private static final double SLOPE_SPAN = 0.5;

    /** {@code ground} as a sheet, its points about {@code every} centimetres apart. */
    public Mesh of(final Ground ground, final double every) {
        if (!(every > 0)) {
            throw new IllegalArgumentException("points some way apart: " + every);
        }
        final Extent extent = ground.extent();
        final int across = (int) Math.ceil(extent.across() / every) + 1;
        final int along = (int) Math.ceil(extent.along() / every) + 1;
        final List<Vec3> points = new ArrayList<>(across * along);
        final List<Vec3> facings = new ArrayList<>(across * along);
        for (int j = 0; j < along; j++) {
            final double z = Math.min(extent.z0() + j * every, extent.z1());
            for (int i = 0; i < across; i++) {
                final double x = Math.min(extent.x0() + i * every, extent.x1());
                points.add(new Vec3(x, ground.height(x, z), z));
                facings.add(facing(ground, x, z, every * SLOPE_SPAN));
            }
        }
        return new Mesh(points, facings, corners(across, along));
    }

    /** The way the ground faces at ({@code x}, {@code z}), its slope taken {@code span} either side, kept within it. */
    Vec3 facing(final Ground ground, final double x, final double z, final double span) {
        final Extent e = ground.extent();
        final double xl = Math.max(x - span, e.x0());
        final double xr = Math.min(x + span, e.x1());
        final double zn = Math.max(z - span, e.z0());
        final double zf = Math.min(z + span, e.z1());
        final double acrossSlope = (ground.height(xr, z) - ground.height(xl, z)) / (xr - xl);
        final double alongSlope = (ground.height(x, zf) - ground.height(x, zn)) / (zf - zn);
        return new Vec3(-acrossSlope, 1, -alongSlope).unit();
    }

    /**
     * The triangles over a grid {@code across} points by {@code along}: in each square, from its near left corner,
     * one by its far left and its near right corners and one by its near right, far left and far right, each wound
     * to face up.
     */
    List<Integer> corners(final int across, final int along) {
        final List<Integer> corners = new ArrayList<>(6 * (across - 1) * (along - 1));
        for (int j = 0; j + 1 < along; j++) {
            for (int i = 0; i + 1 < across; i++) {
                final int near = j * across + i;
                final int far = near + across;
                corners.addAll(List.of(near, far, near + 1, near + 1, far, far + 1));
            }
        }
        return corners;
    }
}
