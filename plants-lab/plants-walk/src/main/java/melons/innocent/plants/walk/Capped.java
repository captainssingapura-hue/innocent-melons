package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;

/**
 * A way of looking with a cap over it: all as {@code looking} has it, but nothing drawn finer than {@code cap},
 * nor finer than the looking itself allows.
 *
 * @param looking the way of looking capped
 * @param cap     the finest level anything is drawn at
 */
public record Capped(Looking looking, Stage cap) implements Looking {

    @Override
    public Vec3 soon(final Vec3 eye, final Vec3 middle) {
        return looking.soon(eye, middle);
    }

    @Override
    public Stage finest() {
        return looking.finest().ordinal() > cap.ordinal() ? looking.finest() : cap;
    }
}
