package melons.innocent.plants.bench;

import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.walk.WoodDrawn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The bench's view, on the card: what is on show standing on level ground, drawn by the plants lab's own wood
 * ({@link WoodDrawn}), so a stage looks on the bench as it does in a wood. The bench's six grown trees are sent once,
 * when the view is made, at every stage; what is on show is planted afresh whenever it changes, which sends nothing.
 * The trees cast shadows on the ground and on each other when shadows are asked for; their clumps, being seen
 * through, cast none. Closed, all of it is gone from the card.
 */
public final class PlantsView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how much ground it covers, in centimetres. */
    private static final int PIXELS = 4096;
    private static final double SHADED = 9_000;

    private final Laid laid;
    private final WoodDrawn wood;
    private final Cast cast;
    private final Shaded shadows;
    private final OneMesh oneMesh;
    private final List<Runnable> closing = new ArrayList<>();
    /** What is planted now. */
    private OnShow planted;

    /** The view, on {@code card}, its layouts as {@code laid} says, with {@code first} planted to begin with. */
    public PlantsView(final Card card, final Laid laid, final OnShow first) {
        this.laid = laid;
        try {
            this.wood = new WoodDrawn(card, laid, first.kinds().grown(), first.planted());
            closing.add(wood::close);
            this.cast = new Cast(card, laid);
            closing.add(cast::close);
            this.shadows = new Shaded(card, laid, PIXELS);
            closing.add(shadows::close);
            this.oneMesh = new OneMesh(card, laid, first.kinds().grown());
            closing.add(oneMesh::close);
            this.planted = first;
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** The ways its shadows can be found, on the card it draws on. */
    public List<Shadowing> offered() {
        return shadows.offered();
    }

    /**
     * One frame drawn on {@code target}: {@code shown}, seen by {@code camera} in {@code time}'s light and under its
     * sky; with shadows, found {@code by} that way, where {@code shadowed}.
     */
    public void drawn(final Target target, final OnShow shown, final Camera camera, final Daylight time,
                      final boolean shadowed, final Shadowing by) {
        if (!shown.equals(planted)) {
            wood.replanted(shown.planted());
            planted = shown;
        }
        oneMesh.wanted(shown);
        oneMesh.readied();
        final boolean one = shown.joined() == Joined.AS_ONE;
        final double aspect = (double) target.width() / target.height();
        final Framed framed = new Framed(laid, camera, time.light());
        final Consumer<Pass> casting = pass -> {
            wood.drawn(pass, camera);
            if (one) {
                oneMesh.drawn(pass, camera, shown.where());
            }
        };
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            casting.accept(pass);
            wood.seenThrough(pass, camera);
            if (one) {
                oneMesh.seenThrough(pass, camera, shown.where());
            }
        };
        if (shadowed) {
            final Beam beam = new Beam(new Vec3(0, 0, 0), time.light().sunTowards(), SHADED, SHADED);
            shadows.drawn(target, time.sky(), framed, by, beam, casting, all);
        } else {
            target.drawn(time.sky(), framed.filling(aspect), all);
        }
    }

    /** What is drawn of {@code shown}, in a line: the stage on each side, or the one mesh. */
    public String said(final OnShow shown) {
        final String right = shown.joined() == Joined.AS_ONE ? "as one mesh. " + oneMesh.said()
                : stage(shown.beside() ? shown.planted().size() / 2 : 0);
        return "Drawn: " + (shown.beside() ? "as grown on the left, " + right + " on the right" : right);
    }

    /** The stage tree number {@code tree} on show was last drawn at, in words. */
    private String stage(final int tree) {
        return wood.stages().size() <= tree ? "nothing"
                : wood.stages().get(tree).name().toLowerCase(java.util.Locale.ROOT).replace('_', ' ');
    }

    /** The stage each tree on show was last drawn at, in the order planted: those as grown first, if beside. */
    public List<Stage> stages() {
        return wood.stages();
    }

    @Override
    public void close() {
        closing.reversed().forEach(Runnable::run);
        closing.clear();
    }
}
