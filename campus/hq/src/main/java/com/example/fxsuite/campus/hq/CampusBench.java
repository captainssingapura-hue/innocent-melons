package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Player;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.avatar.bench.kit.Workshop;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.sim.Num;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec3;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The campus's bench: its main office ({@link MainOffice}) and its offices ({@link Campus#OFFICES}) where they stand
 * round the well, as they are built and furnished; still, a model to look at through the bench kit's model view; its
 * main office and its offices listed in its figures, each by
 * its number, how big, where, which way its door faces. Played, the campus laid on the whole meadow
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

    /** What the workshop made: the offices drawn where they stand. */
    private record Made(ModelView.Model offices) {
    }

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
        // the green between the huts and the main office, east of the well
        final Vec3 well = Village.WELL;
        final double middle = (well.x() + MainOffice.EAST) / 2;
        view.ground(middle, 0, well.z(), 24_000);
        view.lookAt(middle, 300, well.z());
        // from over the huts, the main office at the head of the green beyond them, its porch to the camera
        view.from("campus", 75, 40, 170);
        view.zoom(300, 60_000);
        bus.publish(new Asked());
        shell.show(stage, 1440, 900);
    }

    @Override
    public void stop() {
        workshop.close();
    }

    /** On the workshop's worker: every office drawn where it stands, as one model. */
    private Made make(final Asked a) {
        final List<Solid> all = new ArrayList<>();
        for (final Office o : Campus.OFFICES) {
            all.addAll(placed(o.solids(), o.place()));
        }
        all.addAll(placed(MainOffice.solids(), MainOffice.place()));
        return new Made(ModelView.Model.of(all));
    }

    /** On the screen's thread: the offices shown, their directory read out; played at once if asked. */
    private void show(final Made m) {
        view.show(m.offices());
        final Place main = MainOffice.place();
        final StringBuilder directory = new StringBuilder(String.format(Locale.ROOT,
                "The campus on the meadow: its main office and %d offices round the well%n%n"
                        + "%s: %.0f m across, on a stone plinth, at %d, %d m, its door to %s, to the well%n%n",
                Campus.OFFICES.size(), MainOffice.name(), MainOffice.SIZE.width() / 100, Math.round(main.x() / 100),
                Math.round(main.z() / 100), compass(main.yaw())));
        for (final Office o : Campus.OFFICES) {
            final Place p = o.place();
            directory.append(String.format(Locale.ROOT, "%s: %.1f m across, at %d, %d m, its door to %s%n",
                    o.name(), o.hut().size().width() / 100, Math.round(p.x() / 100), Math.round(p.z() / 100),
                    compass(p.yaw())));
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
