package melons.innocent.terrain.flight;

import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.flight.Below;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.terrain.Averaged;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.drawn.ByMaterials;
import melons.innocent.terrain.drawn.TiledLand;
import melons.innocent.terrain.tiles.Choice;
import melons.innocent.terrain.tiles.Tiling;

import java.util.List;

/**
 * A country to fly over: its land in tiles on the card, in the lab's materials, its water in the lab's water, at its
 * true size and height. How fine the land is drawn goes first by how high the eye is over what is below it: no finer
 * anywhere than two pixels cover looking straight down from there, and never finer than a metre; and then by how far
 * off each part is: a tile is cut into four finer ones while it is nearer the spot under the eye than {@link #NEAR}
 * times its side, so a tile far off has its points a few pixels apart. The tiles are chosen again as the eye moves a
 * quarter of a finest tile, or its height calls for another finest level; going finer again only once the eye is a
 * tenth lower than where it went coarser. Each tile's ground is the country's averaged over cells as wide as its
 * points are apart, so nothing finer than a tile can show is drawn, and it is built away from the card and kept.
 *
 * <p>It brings its own land: the walks' flat ground is not drawn under it. Nothing is in anyone's way.
 */
public final class CountryPlace implements Place {

    /** How many cells a tile has each way; how near, of its side, a tile is cut into four. */
    private static final int CELLS = 64;
    private static final double NEAR = 4;
    /** The finest the land is drawn anywhere, its points this far apart, in centimetres. */
    private static final double FINEST = 100;
    /** How much of the view's height a pixel takes, in a picture 1080 pixels high in the walks' view of 48 degrees. */
    private static final double PIXEL = 2 * 0.44523 / 1080;
    /** Going finer again waits till the eye is this much lower than where it went coarser. */
    private static final double BACK = 0.9;
    private static final long MB = 1L << 20;

    private final Below below;
    private final Tiling tiling;
    private TiledLand land;
    private int finest = -1;
    private double atX = Double.NaN;
    private double atZ = Double.NaN;

    /** {@code country} on {@code card}, its layouts as {@code laid} says, what lies below it {@code below}. */
    public CountryPlace(final Card card, final Laid laid, final LandFeatures country, final Below below) {
        this.below = below;
        this.tiling = new Tiling(country.ground().extent(), CELLS);
        this.land = new TiledLand(card, laid, new ByMaterials(country), tiling,
                level -> new Averaged(country.ground(), tiling.spacing(level)), 1, 1_024 * MB, 768 * MB);
    }

    @Override
    public Obstacles inTheWay() {
        return Obstacles.NONE;
    }

    @Override
    public boolean onTheFlat() {
        return false;
    }

    @Override
    public void readied(final Camera camera) {
        final Vec3 eye = camera.eye();
        final double above = Math.max(1, eye.y() - below.under(eye.x(), eye.z()));
        final int may = tiling.level(Math.max(FINEST, 2 * above * PIXEL));
        // coarser at once; finer only where even a tenth higher would still call for it
        final int level = finest < 0 || may <= finest ? may
                : Math.max(finest, tiling.level(Math.max(FINEST, 2 * above / BACK * PIXEL)));
        final double moved = Double.isNaN(atX) ? Double.POSITIVE_INFINITY : Math.hypot(eye.x() - atX, eye.z() - atZ);
        if (level != finest || moved > tiling.side(level) / 4) {
            finest = level;
            atX = eye.x();
            atZ = eye.z();
            land.want(new Choice(tiling, NEAR, level).around(eye.x(), eye.z()), List.of());
        }
        land.kept();
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        land.drawn(pass, new Mat4(), camera);
    }

    /** How many tiles are to be shown, how many are drawn, and how many are not on the card yet. */
    public String said() {
        return land.shown() + " tiles shown, " + land.drawn() + " drawn, " + land.gaps() + " not yet built";
    }

    @Override
    public boolean settled() {
        return filled();
    }

    /** Whether every tile to be shown is on the card. */
    public boolean filled() {
        return land.gaps() == 0 && land.stale() == 0;
    }

    @Override
    public void close() {
        if (land != null) {
            land.close();
            land = null;
        }
    }
}
