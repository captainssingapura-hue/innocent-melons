package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Bough;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.tree.Tree;
import melons.innocent.plants.tree.Wooded;

import java.util.ArrayList;
import java.util.List;

/**
 * Trees standing by themselves on level ground, to walk round and to fly up into, one of each kind there is, with
 * no leaves: the oak twelve metres ahead of where the avatar starts, the walnut off to its left and the birch off
 * to its right, each far enough from the others that their crowns do not meet. Each is drawn as finely as it is
 * grown, in its own kind's bark from the lab, as one form with one look, in one call. A trunk is in the avatar's
 * way, as a post; boughs are not, so the avatar flies through a crown as if it were not there.
 */
public final class TreePlace implements Place {

    /** How far apart the trees stand across the ground, and how far ahead of the oak the others stand. */
    private static final double APART = 2200;
    private static final double BEYOND = 400;

    /** One tree standing here: its kind, the tree, and where its foot is. */
    public record Standing(Kind kind, Tree tree, Vec3 stands) {
    }

    private final List<Standing> standing = List.of(
            new Standing(Kind.OAK, Kind.OAK.tree(1), new Vec3(0, 0, 1200)),
            new Standing(Kind.WALNUT, Kind.WALNUT.tree(1), new Vec3(-APART, 0, 1200 + BEYOND)),
            new Standing(Kind.BIRCH, Kind.BIRCH.tree(1), new Vec3(APART, 0, 1200 + BEYOND)));
    private final List<Formed> woods = new ArrayList<>();

    /** The trees, on {@code card}, their layouts as {@code laid} says. */
    public TreePlace(final Card card, final Laid laid) {
        try {
            for (final Standing tree : standing) {
                woods.add(new Formed(card, new Look(laid, new Shades().of(new Barks().of(tree.kind()))),
                        new Wooded(tree.tree()).formed()));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** The trees that stand here, the oak first. */
    public List<Standing> standing() {
        return standing;
    }

    @Override
    public Obstacles inTheWay() {
        final List<Block> posts = new ArrayList<>();
        for (final Standing tree : standing) {
            final Bough trunk = tree.tree().boughs().getFirst();
            final double thick = trunk.thick().getFirst();
            posts.add(new Block(tree.stands().x(), tree.stands().z(), thick, thick, 0, 0, trunk.ends().y()));
        }
        return new Obstacles(posts);
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        for (int tree = 0; tree < standing.size(); tree++) {
            woods.get(tree).drawn(pass, new Mat4(standing.get(tree).stands()), camera, 1);
        }
    }

    @Override
    public void close() {
        woods.reversed().forEach(Formed::close);
        woods.clear();
    }
}
