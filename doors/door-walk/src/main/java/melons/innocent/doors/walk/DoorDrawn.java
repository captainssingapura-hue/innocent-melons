package melons.innocent.doors.walk;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.mechanism.Swung;
import melons.innocent.doors.swing.DoorPart;
import melons.innocent.doors.swing.Moves;
import melons.innocent.doors.swing.Stood;
import melons.innocent.doors.swing.SwingDoor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * One swing door on the card: each of its parts sent once, and drawn with the look of the material it is made of,
 * told its colour; its fixed parts where the door stands, and its leaf's turned about its hinges as far as it has
 * swung. What is seen through, its glass, is drawn apart, when it is asked for. The looks are not its own: every
 * door of a place is drawn with the same ones.
 */
final class DoorDrawn implements AutoCloseable {

    /** A part on the card, with the look it is drawn with. */
    private record Sent(DoorPart part, Shaped shaped, Looked look) {
    }

    private final List<Sent> sent = new ArrayList<>();

    /** {@code door}'s parts on {@code card}, each with the look {@code looks} has for its material. */
    DoorDrawn(final Card card, final Laid laid, final SwingDoor<?> door, final Function<Material, Looked> looks) {
        try {
            for (final DoorPart part : door.parts()) {
                sent.add(new Sent(part, new Shaped(card, laid, part.mesh()), looks.apply(part.of())));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /**
     * Its parts drawn by {@code pass}, standing as {@code stood} says with its leaf swung as {@code swung} says:
     * those that are seen through where {@code through}, the others where not.
     */
    void drawn(final Pass pass, final Camera camera, final Stood stood, final Swung swung, final boolean through) {
        for (final Sent one : sent) {
            if (one.look().seenThrough() == through) {
                one.look().drawn(pass, one.shaped(), one.part().moves() == Moves.LEAF ? stood.leaf(swung)
                        : stood.frame(), camera, 1, one.part().tint());
            }
        }
    }

    @Override
    public void close() {
        sent.forEach(one -> one.shaped().close());
        sent.clear();
    }
}
