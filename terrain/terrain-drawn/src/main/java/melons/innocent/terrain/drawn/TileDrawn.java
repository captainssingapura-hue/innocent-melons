package melons.innocent.terrain.drawn;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.terrain.tiles.Chosen;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A tile on the card: its meshes, made away from the card ({@link TileMeshes}), put on it, each sheet drawn in its
 * layer's look and its water, if any, in the water's; stood at its near left corner on the land. Put on, it may stay
 * on the card while it is not drawn, against its being drawn again. Closed, it is gone from the card.
 */
public final class TileDrawn implements AutoCloseable {

    private final TileMeshes meshes;
    private final Map<Integer, Shaped> sheets = new LinkedHashMap<>();
    /** The layer its water is drawn in, and its water's level sheet: none where no water lies over its ground. */
    private final int waterLayer;
    private final Shaped water;

    /** {@code meshes} put on {@code card}, laid out as {@code laid}; its water, if any, in layer {@code waterLayer}. */
    public TileDrawn(final Card card, final Laid laid, final TileMeshes meshes, final int waterLayer) {
        this.meshes = meshes;
        this.waterLayer = waterLayer;
        meshes.sheets().forEach((layer, sheet) -> sheets.put(layer, new Shaped(card, laid, sheet)));
        this.water = meshes.water() == null ? null : new Shaped(card, laid, meshes.water());
    }

    /** Which tile, and which of its edges meet coarser ones. */
    public Chosen chosen() {
        return meshes.chosen();
    }

    /** About how much room it takes on the card, in bytes. */
    public long carded() {
        return meshes.carded();
    }

    /** Drawn in {@code pass}, in {@code looks}, the land where {@code land} stands it, to {@code camera}. */
    public void drawn(final Pass pass, final Looks looks, final Mat4 land, final Camera camera) {
        final Mat4 stands = land.times(new Mat4(meshes.corner()));
        sheets.forEach((layer, sheet) -> looks.of(layer).drawn(pass, sheet, stands, camera, 1));
        if (water != null) {
            looks.of(waterLayer).drawn(pass, water, stands, camera, 1);
        }
    }

    @Override
    public void close() {
        sheets.values().forEach(Shaped::close);
        if (water != null) {
            water.close();
        }
    }
}
