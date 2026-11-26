package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;

/** Looked at plainly, by an eye going nowhere: every square at the level its distance now calls for. */
public record Plainly() implements Looking {

    @Override
    public Vec3 soon(final Vec3 eye, final Vec3 middle) {
        return eye;
    }

    @Override
    public Stage finest() {
        return Stage.FINE;
    }
}
