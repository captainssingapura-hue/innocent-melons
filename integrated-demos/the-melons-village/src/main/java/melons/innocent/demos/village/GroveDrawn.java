package melons.innocent.demos.village;

import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Tree;
import melons.innocent.plants.tree.Wooded;

import java.util.ArrayList;
import java.util.List;

/**
 * A wood on the card: each of its few different trees sent to the card once, and drawn once for every tree of that
 * kind that stands in the wood, where it stands. One look serves them all, the lab's bark. Every tree is drawn as
 * finely as it is grown, however far off it is: the coarser forms a far tree would be drawn with are not built.
 * Closed, all of it is gone from the card.
 */
public final class GroveDrawn implements AutoCloseable {

    private final List<Grove.Standing> standing;
    private final List<Shaped> kinds = new ArrayList<>();
    private Looked bark;

    /** {@code grove}, on {@code card}, its layouts as {@code laid} says. */
    public GroveDrawn(final Card card, final Laid laid, final Grove grove) {
        this.standing = grove.standing();
        try {
            this.bark = new Looked(card, new Look(laid, new Shades().of(new Materials.Bark())));
            for (final Tree kind : grove.kinds()) {
                kinds.add(new Shaped(card, laid, new Wooded(kind).formed()));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** Every tree of the wood, drawn by {@code pass} as {@code camera} sees it. */
    public void drawn(final Pass pass, final Camera camera) {
        for (final Grove.Standing tree : standing) {
            bark.drawn(pass, kinds.get(tree.kind()), tree.placed(), camera, 1);
        }
    }

    @Override
    public void close() {
        kinds.reversed().forEach(Shaped::close);
        kinds.clear();
        if (bark != null) {
            bark.close();
            bark = null;
        }
    }
}
