package melons.innocent.demos.village;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.walk.WoodDrawn;

import java.util.List;

/**
 * A wood on the card: each of its few different trees sent to the card once, at every stage, and drawn once for
 * every tree of that kind that stands in the wood, where it stands, as finely as its distance calls for
 * ({@link WoodDrawn}): its wood with the rest of the village, its far haze after it. Closed, all of it is gone from
 * the card.
 */
public final class GroveDrawn implements AutoCloseable {

    private final WoodDrawn wood;

    /** {@code grove}, on {@code card}, its layouts as {@code laid} says. */
    public GroveDrawn(final Card card, final Laid laid, final Grove grove) {
        this(card, laid, List.of(grove));
    }

    /**
     * {@code groves}, all of their trees one wood on {@code card}, their layouts as {@code laid} says: so many
     * woods cost no more draws than one. Every grove grows the same few trees, so the first's are sent.
     */
    public GroveDrawn(final Card card, final Laid laid, final List<Grove> groves) {
        this.wood = new WoodDrawn(card, laid,
                groves.getFirst().kinds().stream().map(grown -> new WoodDrawn.Grown(grown.kind(), grown.tree()))
                        .toList(),
                groves.stream().flatMap(grove -> grove.standing().stream())
                        .map(tree -> new WoodDrawn.Planted(tree.kind(), tree.placed(), tree.times())).toList());
    }

    /** Every tree's wood, drawn by {@code pass} as {@code camera} sees it. */
    public void drawn(final Pass pass, final Camera camera) {
        wood.drawn(pass, camera);
    }

    /** Every tree's far haze, drawn by {@code pass} as {@code camera} sees it, the furthest first. */
    public void seenThrough(final Pass pass, final Camera camera) {
        wood.seenThrough(pass, camera);
    }

    /** The stage each tree of the wood was last drawn at, in the order they stand. */
    public List<Stage> stages() {
        return wood.stages();
    }

    @Override
    public void close() {
        wood.close();
    }
}
