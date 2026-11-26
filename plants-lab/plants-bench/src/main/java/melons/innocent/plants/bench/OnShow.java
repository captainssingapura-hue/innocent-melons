package melons.innocent.plants.bench;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.walk.WoodDrawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * What the bench has on show: which subject, of which kinds of tree, at which stage (none, for whatever stage its
 * distance calls for, as in the game), whether the same stands beside it tree by tree, to hold the two against each
 * other, how many trees a patch has along each side, whether what is on show is drawn as copies or as one mesh, and
 * for an area of forest its outline and how far across it is. The one thing the JavaFX window says to the card's
 * thread, as a message on the bench's bus; whole, and never changed once said.
 *
 * @param subject one tree, a patch, or an area of forest
 * @param kinds   which kinds of tree
 * @param stage   the stage it is held at; none, for whatever its distance calls for
 * @param beside  whether the same stands beside it, on its left, tree by tree
 * @param rows    how many trees a patch has along each side, from {@value #FEWEST} to {@value #MOST}
 * @param joined  whether what is on show is drawn as copies or as one mesh
 * @param outline an area's outline
 * @param across  how far across an area is, in metres, from {@value #NARROWEST} to {@value #WIDEST}
 */
public record OnShow(Subject subject, Kinds kinds, Optional<Stage> stage, boolean beside, int rows, Joined joined,
                     Outline outline, int across) {

    /** The fewest and the most trees a patch has along each side. */
    private static final int FEWEST = 2;
    private static final int MOST = 8;
    private static final int USUAL = 4;
    /** The least and the most an area is across, in metres. */
    private static final int NARROWEST = 20;
    private static final int WIDEST = 500;
    private static final int COMMON = 100;
    private static final double METRE = 100;
    /** How much room is left between two areas side by side, in centimetres. */
    private static final double ROOM = 2_000;

    public OnShow {
        if (rows < FEWEST || rows > MOST) {
            throw new IllegalArgumentException("a patch has " + FEWEST + " to " + MOST + " trees a side, not " + rows);
        }
        if (across < NARROWEST || across > WIDEST) {
            throw new IllegalArgumentException("an area is " + NARROWEST + " to " + WIDEST + " m across, not "
                    + across);
        }
    }

    /** A patch of four trees a side, drawn as copies. */
    public OnShow(final Subject subject, final Kinds kinds, final Optional<Stage> stage, final boolean beside) {
        this(subject, kinds, stage, beside, USUAL, Joined.APART);
    }

    /** A tree or a patch; an area, were it one, a circle a hundred metres across. */
    public OnShow(final Subject subject, final Kinds kinds, final Optional<Stage> stage, final boolean beside,
                  final int rows, final Joined joined) {
        this(subject, kinds, stage, beside, rows, joined, Outline.CIRCLE, COMMON);
    }

    /**
     * The trees drawn as copies: the same on the left, if beside (a tree or a patch as grown; an area at whatever
     * stage each tree's distance calls for, since hundreds of trees as grown are too many); and what is on show,
     * unless it is one mesh.
     */
    public List<WoodDrawn.Planted> planted() {
        final List<WoodDrawn.Planted> planted = new ArrayList<>();
        if (beside) {
            planted.addAll(standing(new Vec3(-half(), 0, 0),
                    subject == Subject.AREA ? Optional.empty() : Optional.of(Stage.FINE)));
        }
        if (joined == Joined.APART) {
            planted.addAll(standing(where(), stage));
        }
        return planted;
    }

    /** The trees on show as merged into one mesh: round the middle, before it is set {@link #where}. */
    public List<WoodDrawn.Planted> merged() {
        return standing(new Vec3(0, 0, 0), Optional.of(level()));
    }

    /**
     * The level one mesh of what is on show is made at: the stage it is held at; or, held at none, as grown for a
     * tree or a patch, and at its coarsest for an area.
     */
    public Stage level() {
        return stage.orElse(subject == Subject.AREA ? Stage.WHOLE : Stage.FINE);
    }

    /** What is on show standing round {@code middle}, each tree held at {@code held} if any. */
    private List<WoodDrawn.Planted> standing(final Vec3 middle, final Optional<Stage> held) {
        return subject == Subject.AREA ? new Area(outline, across * METRE).planted(kinds, middle, held)
                : subject.planted(kinds, middle, held, rows);
    }

    /** Where what is on show stands: at the middle; or, beside the same, on the right of it. */
    public Vec3 where() {
        return beside ? new Vec3(half(), 0, 0) : new Vec3(0, 0, 0);
    }

    /** How far two of it stand apart side by side, middle to middle, in centimetres. */
    public double apart() {
        return subject == Subject.AREA ? across * METRE + ROOM : subject.apart(rows);
    }

    private double half() {
        return apart() / 2;
    }

    /** What is on show, in a line. */
    public String said() {
        final String what = switch (subject) {
            case PATCH -> String.format(Locale.ENGLISH, "A patch of %d by %d trees", rows, rows);
            case AREA -> String.format(Locale.ENGLISH, "%s of forest %d m across, %d trees", outline.title(), across,
                    merged().size());
            default -> subject.title();
        };
        final String named = level().name().toLowerCase(Locale.ROOT).replace('_', ' ');
        final String how = joined == Joined.AS_ONE ? "all as one mesh, at " + named
                : stage.map(held -> "held at " + named).orElse("at the stage its distance calls for");
        return what + ", " + kinds.title().toLowerCase(Locale.ROOT) + ", " + how
                + (beside ? "; the same on its left, tree by tree" : "");
    }
}
