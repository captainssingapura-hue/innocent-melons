package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.texture.materials.Materials;
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
import melons.innocent.plants.tree.Tree;
import melons.innocent.plants.tree.Wooded;

import java.util.List;

/**
 * A tree standing by itself on level ground, to walk round and to fly up into: an oak with no leaves, twelve
 * metres ahead of where the avatar starts, drawn as finely as it is grown, in the lab's bark. The whole tree is
 * one form with one look, drawn in one call. Its trunk is in the avatar's way, as a post; its boughs are not, so
 * the avatar flies through its crown as if it were not there.
 */
public final class TreePlace implements Place {

    private final Tree tree = new Tree();
    private final Vec3 stands = new Vec3(0, 0, 1200);
    private final Formed wood;

    /** The tree, on {@code card}, its layouts as {@code laid} says. */
    public TreePlace(final Card card, final Laid laid) {
        this.wood = new Formed(card, new Look(laid, new Shades().of(new Materials.Bark())),
                new Wooded(tree).formed());
    }

    /** The tree that stands here. */
    public Tree tree() {
        return tree;
    }

    /** Where its foot is. */
    public Vec3 stands() {
        return stands;
    }

    @Override
    public Obstacles inTheWay() {
        final Bough trunk = tree.boughs().getFirst();
        final double thick = trunk.thick().getFirst();
        return new Obstacles(List.of(new Block(stands.x(), stands.z(), thick, thick, 0, 0, trunk.ends().y())));
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        wood.drawn(pass, new Mat4(stands), camera, 1);
    }

    @Override
    public void close() {
        wood.close();
    }
}
