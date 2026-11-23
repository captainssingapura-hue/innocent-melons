package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.CageView;
import com.example.fxsuite.life.body.muscle.Measured;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.muscle.Musculature;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The soft bench: a region of the body to be simulated by finite elements ({@link SoftRegion}) and what it takes in
 * ({@link RegionPieces}), the body still, held as the flesh is bound: the region a box, its bounds on sliders, and
 * how much of a muscle must be in it to be taken in; the
 * muscles taken in red, their parts beyond the region darker, fixed where they are cut; the muscles held round them as
 * lines, the bones as they are; where the taken are cut, and where held to their bones; the taken listed, how deep each
 * lies and how much of it is in the region; how much of the region two muscles or more fill, which the simulation is to
 * bring to nothing. Every muscle's surface found once, away from the screen.
 *
 * <p>{@code -Dsoft.box} (six numbers: the lowest corner, then the highest, cm, the body's frame) the region first;
 * {@code -Dsoft.share} (per cent) how much of a muscle must be in the region for it to be taken in, else held;
 * {@code -Dsoft.yaw}, {@code -Dsoft.pitch} and {@code -Dsoft.distance} where it is seen from.</p>
 */
public final class SoftBench extends Application {

    private final Bus bus = new Bus();
    private final ModelView view = new ModelView();
    private final Rig rig = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);
    private final Slider[] bounds = new Slider[6];
    private final Slider share = new Slider(0, 100, Double.parseDouble(System.getProperty("soft.share", "25")));
    private final CheckBox rest = new CheckBox("The muscles held round it");
    private final CheckBox bones = new CheckBox("The bones");
    private final Label said = new Label();
    private final TextArea list = new TextArea();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "soft");
        t.setDaemon(true);
        return t;
    });
    private List<RegionPieces.Piece> pieces;
    private Group skeleton;
    private int asked;

    @Override
    public void start(final Stage stage) {
        final Shell shell = new Shell("Soft bench", bus);
        final ScrollPane side = new ScrollPane(controls(shell));
        side.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The region simulated soft"), side, 400);
        shell.middle(view.node());
        view.zoom(10, 900);
        view.from("soft", Double.parseDouble(System.getProperty("soft.yaw", "150")),
                Double.parseDouble(System.getProperty("soft.pitch", "12")),
                Double.parseDouble(System.getProperty("soft.distance", "1.1")));
        bus.publish(new Bus.Working(true, "muscles"));
        worker.submit(() -> {
            pieces = all();
            skeleton = CageView.INSTANCE.bones(rig, 0, new Detail(0.4), p -> true);
            Platform.runLater(() -> {
                bus.publish(new Bus.Working(false, "muscles"));
                make();
            });
        });
        shell.show(stage, 1400, 920);
    }

    /** The side: the region's bounds, what is shown round it, what it takes in, and how much they share. */
    private Node controls(final Shell shell) {
        final String[] names = {"Left, cm", "Down, cm", "Back, cm", "Right, cm", "Up, cm", "Ahead, cm"};
        final double[] first = box();
        final double[][] range = {{-30, 0}, {20, 70}, {-25, 5}, {0, 30}, {45, 80}, {-5, 20}};
        final GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(4);
        for (int i = 0; i < 6; i++) {
            bounds[i] = new Slider(range[i][0], range[i][1], first[i]);
            bounds[i].setPrefWidth(220);
            bounds[i].valueChangingProperty().addListener((o, was, is) -> {
                if (!is) {
                    make();
                }
            });
            grid.addRow(i, new Label(names[i]), bounds[i]);
        }
        share.setPrefWidth(220);
        share.valueChangingProperty().addListener((o, was, is) -> {
            if (!is) {
                make();
            }
        });
        grid.addRow(6, new Label("Taken in, %"), share);
        rest.setSelected(true);
        bones.setSelected(true);
        rest.setOnAction(e -> make());
        bones.setOnAction(e -> make());
        list.setEditable(false);
        list.setPrefRowCount(22);
        said.setWrapText(true);
        final VBox box = new VBox(8, new Label("The region, the body's frame"), grid, rest, bones, said,
                new Label("Taken in: how deep, the muscle, how much of it in the region"), list, shell.problems());
        box.setPadding(new Insets(10));
        return box;
    }

    /** The region first asked for: as {@code -Dsoft.box} says, else the neck, from the shoulders' tops to the skull. */
    private double[] box() {
        final String[] s = System.getProperty("soft.box", "-11,45,-13,11,62,6").split(",");
        final double[] b = new double[6];
        for (int i = 0; i < 6; i++) {
            b[i] = Double.parseDouble(s[i].trim());
        }
        return b;
    }

    /** Every muscle on both sides, sized as measured, as the body is bound. */
    private List<RegionPieces.Piece> all() {
        final Measured bulk = new Measured(Proportions.DRAWN.stature() / 100, Proportions.DRAWN.mass());
        final List<RegionPieces.Piece> all = new ArrayList<>();
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            for (final boolean left : m.paired() ? List.of(false, true) : List.of(false)) {
                all.add(new RegionPieces.Piece(m, left, MuscleSurface.INSTANCE.surface(m, bulk, rig, left, 1)));
            }
        }
        return all;
    }

    /** The region as the sliders have it, what it takes in found and shown, away from the screen. */
    private void make() {
        if (pieces == null) {
            return;
        }
        final int mine = ++asked;
        final SoftRegion region = new SoftRegion(new V3(bounds[0].getValue(), bounds[1].getValue(),
                bounds[2].getValue()), new V3(bounds[3].getValue(), bounds[4].getValue(), bounds[5].getValue()));
        final boolean withRest = rest.isSelected();
        final double least = share.getValue();
        final boolean withBones = bones.isSelected();
        bus.publish(new Bus.Working(true, "region"));
        worker.submit(() -> {
            final RegionPieces.Taken taken = RegionPieces.INSTANCE.taken(region, pieces, rig, least / 100);
            final RegionPieces.Filled filled = RegionPieces.INSTANCE.filled(region, taken.taken());
            final Group world = SoftScene.INSTANCE.world(region, taken, withRest, withBones ? skeleton : null);
            Platform.runLater(() -> {
                if (mine == asked) {
                    show(world, taken, filled);
                }
                bus.publish(new Bus.Working(false, "region"));
            });
        });
    }

    /** {@code world} shown, stood on its heels; what it takes in listed, how much they share told. */
    private void show(final Group world, final RegionPieces.Taken taken, final RegionPieces.Filled filled) {
        final V3 heel = rig.frame(Part.RIGHT_FOOT).place(new V3(0, -6.4, -4.0));
        world.getTransforms().add(new Translate(0, heel.y(), 0));
        view.show(ModelView.Model.of(world));
        final V3 mid = new V3((bounds[0].getValue() + bounds[3].getValue()) / 2,
                (bounds[1].getValue() + bounds[4].getValue()) / 2, (bounds[2].getValue() + bounds[5].getValue()) / 2);
        view.lookAt(-mid.x(), mid.y() - heel.y(), -mid.z());
        list.setText(SoftScene.INSTANCE.listed(taken, filled));
        said.setText(String.format(java.util.Locale.ROOT, "%d muscles taken in, %d held round them; cut in %,d places,"
                        + " held to their bones in %d. In the region they fill %.0f cm3, %.0f cm3 of it two or more"
                        + " at once (%.0f%%), which the simulation is to bring to nothing.", taken.taken().size(),
                taken.held().size(), taken.cuts().size(), taken.attached().size(), filled.together(), filled.shared(),
                100 * filled.shared() / Math.max(1e-9, filled.together())));
    }
}
