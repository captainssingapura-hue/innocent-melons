package melons.innocent.terrain.bench;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;

import java.util.HashMap;
import java.util.Map;

/**
 * The painting's looks on the card, each layer's built once, when first wanted, and drawn on every tile there is of
 * that layer; so tiles come and go and no look is built again. On the card's thread only. Closed, all are gone.
 */
public final class Looks implements AutoCloseable {

    private final Card card;
    private final Laid laid;
    private final Painting painting;
    private final Map<Integer, Looked> built = new HashMap<>();

    /** Looks on {@code card}, laid out as {@code laid}, for {@code painting}'s layers. */
    public Looks(final Card card, final Laid laid, final Painting painting) {
        this.card = card;
        this.laid = laid;
        this.painting = painting;
    }

    /** Layer {@code layer}'s look, built if it is not yet. */
    public Looked of(final int layer) {
        return built.computeIfAbsent(layer, l -> new Looked(card, new Look(laid, painting.surface(l))));
    }

    @Override
    public void close() {
        built.values().forEach(Looked::close);
        built.clear();
    }
}
