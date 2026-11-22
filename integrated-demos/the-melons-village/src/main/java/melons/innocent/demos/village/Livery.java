package melons.innocent.demos.village;

import com.example.fxsuite.texture.lab.Called;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * The liveries cars are seen in: a colour, and whether the paint is the plain gloss or the one with flakes of
 * metal in it. The paints are the lab's two; the colour is told with each car drawn, so every car of one paint is
 * drawn with the one look. The colours are ordinary ones, written as a page of the web writes a colour.
 */
public enum Livery {
    WHITE("#e8e8e4", false),
    BLACK("#16181a", false),
    RED("#a3151c", false),
    YELLOW("#d9b21f", false),
    CREAM("#e3dcc6", false),
    SILVER("#b9bec4", true),
    GRAPHITE("#5a6068", true),
    BLUE("#1c3f8f", true),
    BURGUNDY("#7a1420", true),
    GREEN("#2f5d50", true),
    CHAMPAGNE("#a48d62", true),
    ICE_BLUE("#9aa7b4", true);

    private final String written;
    private final boolean metallic;

    Livery(final String written, final boolean metallic) {
        this.written = written;
        this.metallic = metallic;
    }

    /** The colour, as much red, green and blue as it has, each from nothing to one. */
    public Vec3 colour() {
        return new Called(written).colour();
    }

    /** Whether the paint has flakes of metal in it. */
    public boolean metallic() {
        return metallic;
    }
}
