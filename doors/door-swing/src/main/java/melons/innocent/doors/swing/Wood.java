package melons.innocent.doors.swing;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * How a door's wood is finished: varnished, its grain showing, or painted a colour. Each is one of the lab's
 * materials, and the colour a thing of it is told.
 */
public sealed interface Wood {

    /** The lab's material it is drawn as. */
    Material leaf();

    /** The colour its parts are told: white, where the material has its own. */
    Vec3 tint();

    /** Varnished timber, its grain running along each piece. */
    record Varnished() implements Wood {

        @Override
        public Material leaf() {
            return new Materials.Timber();
        }

        @Override
        public Vec3 tint() {
            return new Vec3(1, 1, 1);
        }
    }

    /**
     * Wood under gloss paint.
     *
     * @param colour the paint's colour, as red, green and blue in one number: {@code 0x1f4a37}
     */
    record Painted(int colour) implements Wood {

        @Override
        public Material leaf() {
            return new Materials.GlossPaint();
        }

        @Override
        public Vec3 tint() {
            return new Vec3((colour >> 16 & 0xff) / 255.0, (colour >> 8 & 0xff) / 255.0, (colour & 0xff) / 255.0);
        }
    }
}
