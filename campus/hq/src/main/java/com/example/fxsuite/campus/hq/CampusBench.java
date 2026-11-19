package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Player;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.avatar.bench.kit.Workshop;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.look.TerrainModel;
import com.example.fxsuite.avatar.sim.Num;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Vec3;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The campus's bench: its quarters' plots ({@link Quarters}), each in its cover's colour; its main office
 * ({@link MainOffice}) and its departments ({@link Department}) where they stand
 * east of the well, as they are built and furnished; still, a model to look at through the bench kit's model view;
 * each listed in its figures, how big, of what, where. Played, the campus laid on the whole meadow
 * ({@link CampusWorld}): what moves is seen there, not here. Put together of the bench kit's parts.
 *
 * <p>{@code -Dcampus.yaw}, {@code -Dcampus.pitch}, {@code -Dcampus.distance} (m) where it is seen from;
 * {@code -Dbench.play=true} plays it at once; {@code -Dbench.snapshot=out.png} saves the window after
 * {@code -Dbench.settle} ms and exits.
 */
public final class CampusBench extends Application {

    private final Bus bus = new Bus();
    private final Shell shell = new Shell("Innocent Melons: the campus", bus);
    private final ModelView view = new ModelView();
    private final Player player = new Player(CampusWorld::new, List.of());
    private boolean playWhenShown = Boolean.getBoolean("bench.play");

    private record Asked() {
    }

    /** What the workshop made: its buildings drawn where they stand; its quarters' plots, each in its cover. */
    private record Made(ModelView.Model buildings, ModelView.Model plots) {
    }

    /** How far over the bench's grass a plot is drawn: told apart from it from as far as the bench is looked from. */
    static final double PLOT_OVER = 5;

    private final Workshop<Asked, Made> workshop = new Workshop<>("campus", bus, Asked.class, this::make);

    @Override
    public void start(final Stage stage) {
        bus.subscribe(Made.class, this::show, Bus.SCREEN);
        final Button play = new Button("Play");
        play.setStyle("-fx-font-weight: bold;");
        play.setOnAction(e -> player.play());
        shell.bar(List.of(play));
        final VBox side = new VBox(10, player.panel());
        side.setPadding(new Insets(8));
        shell.side(new Label("The campus"), side, 420);
        shell.middle(view.node());
        // the meadow round the campus, its outlying plots and all; looked at over its quarters by the green, from high
        // over the south bank, looking north: the platform before the slopes; the outlying plots seen drawn back
        view.ground(0, 0, 5_000, 220_000);
        view.lookAt(3_000, 0, 13_000);
        view.from("campus", 0, 50, 620);
        view.zoom(300, 400_000);
        bus.publish(new Asked());
        shell.show(stage, 1440, 900);
    }

    @Override
    public void stop() {
        workshop.close();
    }

    /**
     * On the workshop's worker: its main office, its departments and its labs' libraries drawn where they stand, as
     * one model.
     */
    private Made make(final Asked a) {
        final List<Solid> all = new ArrayList<>(placed(MainOffice.HUT.solids(), MainOffice.place()));
        for (final Department d : Department.ALL) {
            all.addAll(placed(d.hut().solids(), d.place()));
        }
        for (final LabHut lab : LabHut.ALL) {
            all.addAll(placed(lab.hut().solids(), lab.place()));
        }
        return new Made(ModelView.Model.of(all), ModelView.Model.of(plots()));
    }

    /**
     * Every quarter's plots, each a flat patch over the bench's grass in its cover's colour, as the land draws it
     * ({@link TerrainModel#colour}): a fan of triangles from its first corner, its outline being convex.
     */
    static Group plots() {
        final Group plots = new Group();
        for (final Quarter q : Quarters.ALL) {
            final PhongMaterial look = new PhongMaterial(TerrainModel.colour(q.ground()));
            look.setSpecularColor(Color.web("#3a3f46"));
            for (final Quarter.Plot p : q.plots()) {
                final List<Vec2> o = p.outline();
                final TriangleMesh mesh = new TriangleMesh();
                for (final Vec2 v : o) {
                    mesh.getPoints().addAll((float) v.x(), (float) -PLOT_OVER, (float) v.z());
                }
                mesh.getTexCoords().addAll(0, 0);
                for (int k = 1; k + 1 < o.size(); k++) {
                    mesh.getFaces().addAll(0, 0, k + 1, 0, k, 0);
                }
                final MeshView patch = new MeshView(mesh);
                patch.setMaterial(look);
                patch.setCullFace(CullFace.NONE);
                plots.getChildren().add(patch);
            }
        }
        return plots;
    }

    /** On the screen's thread: its plots and buildings shown, their directory read out; played at once if asked. */
    private void show(final Made m) {
        view.show(m.plots(), m.buildings());
        final Place main = MainOffice.place();
        final StringBuilder directory = new StringBuilder(String.format(Locale.ROOT,
                "The campus on the meadow: %d quarters, laid out; its main office, %d departments round it%n%n",
                Quarters.ALL.size(), Department.ALL.size()));
        for (final Quarter q : Quarters.ALL) {
            directory.append(String.format(Locale.ROOT, "%s, in %s: %s; %s%n", q.name(),
                    q.ground().name().toLowerCase(Locale.ROOT), q.unit(),
                    String.join("; ", q.plots().stream().map(Quarter.Plot::what).toList())));
        }
        directory.append(String.format(Locale.ROOT, "%n"
                        + "%s: %.0f m across, on a stone plinth, at %d, %d m, its door to %s, to the well%n%n",
                MainOffice.name(), MainOffice.SIZE.width() / 100,
                Math.round(main.x() / 100), Math.round(main.z() / 100), compass(main.yaw())));
        for (final Department d : Department.ALL) {
            final Place p = d.place();
            final LuxuryHut.Finish f = d.hut().finish();
            directory.append(String.format(Locale.ROOT, "%s: %.1f m across, its walls %s, its roof %s, at %d, %d m%n",
                    d.name(), d.hut().size().width() / 100, f.walls().title(), f.roof().title(),
                    Math.round(p.x() / 100), Math.round(p.z() / 100)));
        }
        directory.append(String.format(Locale.ROOT, "%n"));
        for (final LabHut lab : LabHut.ALL) {
            final Place p = lab.place();
            directory.append(String.format(Locale.ROOT, "%s, in %s (%s): %.1f m across, at %d, %d m, its door to %s%n",
                    lab.name(), lab.quarter(), lab.library(), lab.hut().size().width() / 100,
                    Math.round(p.x() / 100), Math.round(p.z() / 100), compass(p.yaw())));
        }
        bus.publish(new Bus.Figures(directory.toString().strip()));
        bus.publish(new Bus.Problems(""));
        if (playWhenShown) {
            playWhenShown = false;
            player.play();
        }
    }

    /** {@code solids}, in a building's own frame, where it stands {@code at}: turned as it faces, as high as it is. */
    static List<Solid> placed(final List<Solid> solids, final Place at) {
        final Vec3 right = Num.right(at.yaw());
        final Vec3 ahead = Num.heading(at.yaw());
        return solids.stream().map(s -> new Solid(s.name(), s.role(), s.material(), s.corners().stream()
                .map(q -> new Point(at.x() + right.x() * q.across() + ahead.x() * q.along(),
                        at.z() + right.z() * q.across() + ahead.z() * q.along(), q.up() + at.height()))
                .toList())).toList();
    }

    /** Which way {@code yaw} faces, as a compass says it: north, north-east, and so round. */
    static String compass(final double yaw) {
        final String[] points = {"north", "north-east", "east", "south-east", "south", "south-west", "west",
            "north-west"};
        return points[(int) Math.floorMod(Math.round(yaw / 45), 8)];
    }
}
