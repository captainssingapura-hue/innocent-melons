package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.layout.Filling;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Patched;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.tree.Stand;
import melons.innocent.plants.tree.Staging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Where a wood's trees stand, and how each is drawn for a camera: every tree a copy placed from the wood's middle; a
 * tree near enough chosen a stage by its distance ({@link Staging}), unless it or the whole wood is held at one; a
 * tree far off, from a distance of its own chosen once at random between {@value #NEAREST} and {@value #FURTHEST}
 * metres and near again a tenth nearer, drawn as its coarsest stage and not sorted; the far trees found in view or
 * not a patch at a time ({@link Patched}). What it chooses for a camera it chooses once, however many passes of a
 * frame ask. Made afresh whenever the wood is planted afresh.
 */
final class Planting {

    private static final double FIELD = 48;
    private static final int PIXELS = 1080;
    /** Within so many metres of the eye every tree is drawn, in view or not. */
    private static final int KEPT = 60;
    private static final double METRE = 100;
    /** A tree's number, in the low bits of what it is sorted by. */
    private static final long NUMBERED = 0xfffffL;
    /** How far across a patch of the wood is; and the fewest trees one has. */
    private static final double PATCH = 2_500;
    private static final int FEWEST = 4;
    /** Between how many metres off each tree turns far; and what its distance is chosen from. */
    private static final double NEAREST = 350;
    private static final double FURTHEST = 450;
    private static final long CHANCE = 20261124L;
    /** How much nearer than where it turned far a tree turns near again. */
    private static final double BACK = 0.9;
    private static final String COPIED = "copied";

    /** What is chosen for a camera: the near trees by grown tree and stage, the furthest first; the far, by grown. */
    record Chosen(List<List<List<Filling>>> near, List<List<Filling>> far) {
    }

    private final List<WoodDrawn.Planted> planted;
    private final List<Double> heights;
    private final Mat4 middle;
    private final List<Filling> fromTheMiddle = new ArrayList<>();
    private final Staging staging = new Staging(FIELD, PIXELS);
    private final Stage[] at;
    /** The stage each tree is told to be drawn at, in the order planted; none told, where it is empty. */
    private Stage[] told = new Stage[0];
    private final int[] standOf;
    private final Stands stands;
    private final double[] switches;
    private final boolean[] afar;

    /** {@code planted}, of grown trees {@code heights} tall, their copies laid out as {@code laid} says. */
    Planting(final List<WoodDrawn.Planted> planted, final List<Double> heights, final Laid laid) {
        this.planted = List.copyOf(planted);
        this.heights = List.copyOf(heights);
        this.at = new Stage[planted.size()];
        this.afar = new boolean[planted.size()];
        this.switches = new double[planted.size()];
        final Random chance = new Random(CHANCE);
        for (int tree = 0; tree < switches.length; tree++) {
            switches[tree] = (NEAREST + chance.nextDouble() * (FURTHEST - NEAREST)) * METRE;
        }
        this.middle = new Mat4(planted.stream().map(this::foot).reduce(new Vec3(0, 0, 0), Vec3::plus)
                .times(1.0 / Math.max(1, planted.size())));
        final Mat4 back = new Mat4(new Vec3(-middle.at(0, 3), -middle.at(1, 3), -middle.at(2, 3)));
        planted.forEach(one -> fromTheMiddle.add(new Filling(laid.copy()).with(COPIED,
                back.times(one.placed()).columns())));
        final List<Stand> patches = new Patched(PATCH, FEWEST).stands(planted.stream()
                .map(one -> new Patched.Rooted(foot(one), heights.get(one.grown()) * one.times())).toList());
        this.standOf = new int[planted.size()];
        Arrays.fill(standOf, -1);
        for (int stand = 0; stand < patches.size(); stand++) {
            for (final int tree : patches.get(stand).trees()) {
                standOf[tree] = stand;
            }
        }
        this.stands = new Stands(patches);
    }

    /** Where the wood's middle is: every tree's copy is placed from there. */
    Mat4 middle() {
        return middle;
    }

    /**
     * Each tree told the stage it is drawn at, in the order planted, whatever its distance: as a forest of squares
     * tells the trees of a square that cannot be drawn as one, so they change with the squares beside them and not
     * by distances of their own. A tree told its coarsest stage is drawn as a far one. Whether anything changed.
     */
    boolean told(final List<Stage> stages) {
        if (stages.size() != planted.size()) {
            throw new IllegalArgumentException("a stage to each tree planted: " + stages.size() + " for "
                    + planted.size());
        }
        final Stage[] now = stages.toArray(new Stage[0]);
        final boolean changed = !Arrays.equals(now, told);
        told = now;
        return changed;
    }

    /**
     * Each tree chosen for {@code camera}, {@code held} at one stage where the whole wood is, of {@code grown}; no
     * tree left to its distance drawn coarser than {@code coarsest}, and none turned far unless that is the
     * coarsest stage there is. None is drawn finer than {@code finest}, whatever it is held at or called for.
     */
    Chosen chosen(final Camera camera, final Optional<Stage> held, final int grown, final Stage coarsest,
                  final Stage finest) {
        final boolean[] shown = stands.shown(camera);
        final InView view = new InView(camera, KEPT * METRE);
        final List<List<Filling>> far = new ArrayList<>();
        final List<List<List<Filling>>> near = new ArrayList<>();
        for (int tree = 0; tree < grown; tree++) {
            far.add(new ArrayList<>());
            near.add(IntStream.range(0, Stage.values().length)
                    .mapToObj(stage -> (List<Filling>) new ArrayList<Filling>()).toList());
        }
        final double[] off = new double[planted.size()];
        final long[] keys = new long[planted.size()];
        int counted = 0;
        for (int tree = 0; tree < planted.size(); tree++) {
            final WoodDrawn.Planted one = planted.get(tree);
            off[tree] = middleOf(one).minus(camera.eye()).length();
            final boolean free = held.isEmpty() && one.held().isEmpty() && coarsest == Stage.WHOLE;
            afar[tree] = told.length > 0 ? told[tree] == Stage.WHOLE
                    : free && off[tree] > switches[tree] * (afar[tree] ? BACK : 1);
            if (!afar[tree]) {
                // distances are positive, so their bits sort as they do; the tree's number in the low bits
                keys[counted++] = (Double.doubleToLongBits(off[tree]) & ~NUMBERED) | tree;
            } else {
                at[tree] = Stage.WHOLE;
                if (standOf[tree] >= 0 ? shown[standOf[tree]] : view.holds(middleOf(one), tall(one))) {
                    far.get(one.grown()).add(fromTheMiddle.get(tree));
                }
            }
        }
        Arrays.sort(keys, 0, counted);
        for (int k = counted - 1; k >= 0; k--) {
            final int tree = (int) (keys[k] & NUMBERED);
            final WoodDrawn.Planted one = planted.get(tree);
            final Stage left = told.length > 0 ? told[tree] : one.held().or(() -> held).orElseGet(() -> {
                final Stage called = staging.of(tree, off[tree], one.times());
                return called.ordinal() > coarsest.ordinal() ? coarsest : called;
            });
            at[tree] = left.ordinal() < finest.ordinal() ? finest : left;
            if (view.holds(middleOf(one), tall(one))) {
                near.get(one.grown()).get(at[tree].ordinal()).add(fromTheMiddle.get(tree));
            }
        }
        return new Chosen(near, far);
    }

    /** The stage each tree was last drawn at, in the order planted; a tree not yet drawn, as grown. */
    List<Stage> stages() {
        return IntStream.range(0, at.length).mapToObj(tree -> at[tree] == null ? Stage.FINE : at[tree]).toList();
    }

    private double tall(final WoodDrawn.Planted tree) {
        return heights.get(tree.grown()) * tree.times();
    }

    private Vec3 foot(final WoodDrawn.Planted tree) {
        return new Vec3(tree.placed().at(0, 3), tree.placed().at(1, 3), tree.placed().at(2, 3));
    }

    /** The middle of {@code tree}'s height. */
    private Vec3 middleOf(final WoodDrawn.Planted tree) {
        return foot(tree).plus(new Vec3(0, tall(tree) / 2, 0));
    }
}
