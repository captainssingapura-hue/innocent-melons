package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;

/**
 * How a forest's squares are looked at, for whoever looks. Everything in sight is drawn, at the level it needs or
 * one below and never lower; what the looking says is where the eye will be when it is nearest a square, within
 * the while it looks ahead, so the square can be drawn as it will be needed there before the eye has come; and the
 * finest level anything is drawn at, which is every twig unless the eye is going too fast to make twigs out.
 */
public interface Looking {

    /** Where the eye, now at {@code eye}, will be when it is nearest the square at {@code middle}, shortly. */
    Vec3 soon(Vec3 eye, Vec3 middle);

    /** The finest level anything is drawn at: the finest there is, or the one below it. */
    Stage finest();
}
