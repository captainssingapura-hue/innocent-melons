package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Stand;

import java.util.List;

/**
 * Which of a wood's stands ({@link Stand}) may be seen: one wholly outside the widest view a picture could have is
 * not, and nor are the far trees in it. Asked patch by patch, so a far wood costs one reckoning for each patch and
 * none for each tree.
 */
final class Stands {

    private final List<Stand> stands;

    /** {@code stands}, by their numbers. */
    Stands(final List<Stand> stands) {
        this.stands = List.copyOf(stands);
    }

    /** Whether each stand may be seen by {@code camera}, by their numbers. */
    boolean[] shown(final Camera camera) {
        final InView view = new InView(camera, 0);
        final boolean[] shown = new boolean[stands.size()];
        for (int stand = 0; stand < stands.size(); stand++) {
            shown[stand] = view.holds(stands.get(stand).middle(), stands.get(stand).reach());
        }
        return shown;
    }
}
