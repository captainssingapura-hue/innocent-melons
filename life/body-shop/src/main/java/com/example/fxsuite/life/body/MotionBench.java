package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.life.body.head.ClayHead;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;

/**
 * The motion bench: a humanoid walking and running on the spot, over and over, to polish how it moves before the game
 * has it ({@link Gait}, {@link Walker}). The ground goes back under it as far as its feet carry it, so a foot down
 * stands still on the grid; a foot that slides is a gait to put right. At any speed, from standing to a sprint, or
 * round a tour of them all and back; slowed, or stopped at any point of its stride. A skeleton, its muscles or not, or a robot
 * exposed or shelled, moving the same. With the Gatling gun slung from its strap ({@link GatlingHold}), aimed up or
 * down, the only weapon it holds for now.
 *
 * <p>{@code -Dmotion.look} (exposed, shelled, skeleton, muscles, skin), {@code -Dskeleton.detail} (mm,
 * a skeleton's grid), {@code -Dmotion.deep=true} (its deep muscles too),
 * {@code -Dmotion.move} (walk, quick, parade), {@code -Dmotion.speed} (cm/s), {@code -Dmotion.tour=true},
 * {@code -Dmotion.gatling=true}, {@code -Dmotion.aim} (degrees), {@code -Dmotion.slow} (a share of real time) what
 * it plays first;
 * {@code -Dmotion.at} (0 to 1) stops it at that point of its stride; {@code -Dmotion.yaw}, {@code -Dmotion.pitch},
 * {@code -Dmotion.distance} (m), {@code -Dmotion.height} (cm) where it is seen from and looks at;
 * {@code -Dhead.reference} (a reference head's path: its skin with the clay head made from it, {@link ClayHead});
 * {@code -Dbench.snapshot=out.png} saves the window after
 * {@code -Dbench.settle} ms and exits.
 */
public final class MotionBench extends Application {

    /** Speeds to go at, at a press, cm/s. */
    private record Pace(String name, double speed) {
    }

    private static final List<Pace> PACES = List.of(new Pace("Stand", 0), new Pace("Stroll", 90),
            new Pace("Walk", 140), new Pace("Brisk", 200), new Pace("Jog", 300), new Pace("Run", 500),
            new Pace("Sprint", 760));
    /** The tour: how long it takes, seconds, and the speeds it goes through, evenly. */
    private static final double TOUR = 24;
    private static final double[] TOURED = {0, 0, 140, 140, 300, 500, 760, 760, 500, 140, 0, 0};
    /** How far its soles stand clear of the ground. */
    private static final double SOLE = 0.5;
    /** How far the treadmill's grid repeats, cm. */
    private static final double REPEATS = 50;

    private final Bus bus = new Bus();
    private final Shell shell = new Shell("Innocent Melons: the motion bench", bus);
    private final ModelView view = new ModelView();
    private Proportions proportions = Proportions.PAPERS;
    private Walker walker = new Walker(proportions);
    private GatlingHold hold = new GatlingHold(proportions);

    private final Group shown = new Group();
    private final Translate lifted = new Translate();
    private final Group gun = new Group();
    private final Group strap = new Group();
    private final Group loads = new Group();
    /** How long an arrow is drawn for a newton, cm. */
    private static final double ARROW = 0.12;
    private final Affine gunAt = new Affine();
    private Puppet puppet;
    private Pose arms = GatlingHold.ready(Pose.REST);

    private final ChoiceBox<Puppet.Kind> look = new ChoiceBox<>();
    private final ChoiceBox<Move> how = new ChoiceBox<>();
    private final Slider speed = new Slider(0, 800, 140);
    private final Label speedSaid = new Label();
    private final CheckBox tour = new CheckBox("Tour: standing to a sprint and back, " + (int) TOUR + " s");
    private final Button play = new Button("Pause");
    private final Slider slow = new Slider(0.05, 1, 1);
    private final Slider at = new Slider(0, 1, 0);
    private final CheckBox gatling = new CheckBox("The Gatling gun in its hands");
    private final Slider aim = new Slider(-30, 30, 0);
    private final CheckBox treadmill = new CheckBox("The ground going back under it");
    private final CheckBox forces = new CheckBox("The forces on the gun, as arrows");
    private final CheckBox deep = new CheckBox("Its deep muscles too");
    private final Label muscleSaid = new Label();
    private final com.example.fxsuite.life.body.skin.Skins skins = new com.example.fxsuite.life.body.skin.Skins();
    private final java.util.Map<String, com.example.fxsuite.life.body.muscle.MuscleBody.Bound> muscleBodies =
            new java.util.concurrent.ConcurrentHashMap<>();
    private final Slider grid = new Slider(2, 16, Double.parseDouble(System.getProperty("skeleton.detail", "8")));
    private final Label gridSaid = new Label();
    private GunStatics.Solved statics;
    private double balance;

    private boolean playing = true;
    private boolean setting;
    private double toured;
    private long last = -1;
    private double frames;
    private double fps;

    @Override
    public void start(final Stage stage) {
        look.getItems().setAll(Puppet.Kind.values());
        look.setValue(kind(System.getProperty("motion.look", "exposed")));
        look.valueProperty().addListener((o, was, is) -> dress());
        how.getItems().setAll(Move.values());
        how.setValue(move(System.getProperty("motion.move", "walk")));
        how.valueProperty().addListener((o, was, is) -> {
            walker.move(is);
            speed.setDisable(is.marching());
            tour.setSelected(false);
            said();
            if (!playing) {
                still();
            }
        });
        speed.setValue(Double.parseDouble(System.getProperty("motion.speed", "140")));
        speed.setPrefWidth(220);
        speed.valueProperty().addListener((o, was, is) -> said());
        tour.setSelected(Boolean.getBoolean("motion.tour"));
        deep.setSelected(Boolean.getBoolean("motion.deep"));
        gatling.setSelected(Boolean.getBoolean("motion.gatling"));
        gatling.selectedProperty().addListener((o, was, is) -> {
            gun.setVisible(is);
            strap.setVisible(is);
            loads.setVisible(is && forces.isSelected());
            arms = GatlingHold.ready(Pose.REST);
            if (!playing) {
                still();
            }
        });
        aim.setValue(Double.parseDouble(System.getProperty("motion.aim", "0")));
        aim.setPrefWidth(220);
        slow.setValue(Double.parseDouble(System.getProperty("motion.slow", "1")));
        slow.setPrefWidth(220);
        at.setPrefWidth(220);
        treadmill.setSelected(true);
        play.setOnAction(e -> playing(!playing));
        at.valueProperty().addListener((o, was, is) -> {
            if (!setting) {
                playing(false);
                still();
            }
        });
        aim.valueProperty().addListener((o, was, is) -> {
            if (!playing) {
                still();
            }
        });

        final FlowPane paces = new FlowPane(6, 6);
        for (final Pace p : PACES) {
            final Button b = new Button(p.name());
            b.setOnAction(e -> {
                tour.setSelected(false);
                speed.setValue(p.speed());
            });
            paces.getChildren().add(b);
        }
        final GridPane knobs = new GridPane();
        knobs.setHgap(8);
        knobs.setVgap(6);
        knobs.add(new Label("Speed, cm/s"), 0, 0);
        knobs.add(speed, 1, 0);
        knobs.add(speedSaid, 1, 1);
        knobs.add(new Label("Time, share of real"), 0, 2);
        knobs.add(slow, 1, 2);
        knobs.add(new Label("Point of its stride"), 0, 3);
        knobs.add(at, 1, 3);
        knobs.add(new Label("Aim, degrees up"), 0, 4);
        knobs.add(aim, 1, 4);
        knobs.add(new Label("A skeleton's grid, mm"), 0, 5);
        knobs.add(grid, 1, 5);
        knobs.add(gridSaid, 1, 6);
        grid.setPrefWidth(220);
        grid.valueProperty().addListener((o, was, is) -> {
            if (!grid.isValueChanging()) {
                regrid();
            }
        });
        grid.valueChangingProperty().addListener((o, was, is) -> {
            if (!is) {
                regrid();
            }
        });

        final Label hint = new Label("A foot down should stand still on the grid while the ground goes back under it."
                + " Stop it and move the point of its stride to look at a moment. Drag to turn the view, right-drag"
                + " to move it, scroll to come nearer.");
        hint.setWrapText(true);
        final VBox side = new VBox(10, new Label("Drawn as"), look, new Label("How it goes"), how,
                new Label("How fast"), paces, tour, knobs, new FlowPane(10, 6, play), deep, muscleSaid, gatling,
                forces, treadmill, hint);
        side.setPadding(new Insets(8));
        final ScrollPane scroll = new ScrollPane(side);
        scroll.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The motion bench"), scroll, 400);
        shell.middle(view.node());
        view.grid(0, 0, 0, 1_200, 10, 5);
        view.zoom(60, 2_000);
        view.lookAt(0, Double.parseDouble(System.getProperty("motion.height", "95")), 0);
        view.from("motion", Double.parseDouble(System.getProperty("motion.yaw", "70")),
                Double.parseDouble(System.getProperty("motion.pitch", "8")),
                Double.parseDouble(System.getProperty("motion.distance", "4")));

        gun.getChildren().add(FigureModel.meshes(GatlingHold.model(GatlingHold.Sling.DEFAULT), Palette.DEFAULT,
                v -> new javafx.geometry.Point3D(v.x(), v.y(), v.z())));
        gun.getTransforms().add(gunAt);
        gun.setVisible(gatling.isSelected());
        strap.setVisible(gatling.isSelected());
        forces.setSelected(!Boolean.getBoolean("motion.noforces"));
        forces.selectedProperty().addListener((o, was, is) -> loads.setVisible(is && gatling.isSelected()));
        shown.getTransforms().add(lifted);
        dress();
        view.show(ModelView.Model.moving(shown));
        said();

        final String stopped = System.getProperty("motion.at");
        if (stopped != null) {
            setting = true;
            at.setValue(Double.parseDouble(stopped));
            setting = false;
            playing(false);
            still();
        }
        new AnimationTimer() {
            @Override
            public void handle(final long now) {
                frame(now);
            }
        }.start();
        shell.show(stage, 1440, 920);
    }

    /** How it goes, as said: walking (and running), the quick march, the parade step. */
    private static Move move(final String said) {
        final String s = said.toLowerCase(Locale.ROOT);
        return s.startsWith("q") ? Move.QUICK_MARCH : s.startsWith("p") ? Move.PARADE_STEP : Move.WALK;
    }

    private static Puppet.Kind kind(final String said) {
        final String s = said.toLowerCase(Locale.ROOT);
        if (s.startsWith("ski")) {
            return Puppet.Kind.SKIN;
        }
        if (s.startsWith("sk")) {
            return Puppet.Kind.SKELETON;
        }
        if (s.startsWith("mu")) {
            return Puppet.Kind.MUSCLES;
        }
        return s.startsWith("s") ? Puppet.Kind.SHELLED : Puppet.Kind.EXPOSED;
    }

    /** Drawn afresh as chosen. */
    private void dress() {
        proportions = Proportions.PAPERS;
        walker = new Walker(proportions);
        walker.move(how.getValue());
        hold = new GatlingHold(proportions);
        puppet = new Puppet(look.getValue(), proportions, RobotDesign.DEFAULT, Palette.DEFAULT, new V3(-1, -1, -1));
        shown.getChildren().setAll(puppet.node(), gun, strap, loads);
        draw(walker.rig(), 0);
        regrid();
        muscled();
        skinned();
    }

    /**
     * A skeleton's muscles found away from the screen, the deep ones if asked, as big as a body its size has them, then
     * drawn over it; kept, to be drawn again without finding them afresh. None for any other kind.
     */
    private void muscled() {
        if (puppet.kind() != Puppet.Kind.MUSCLES) {
            muscleSaid.setText("");
            return;
        }
        final Puppet mine = puppet;
        final boolean withDeep = deep.isSelected();
        final Proportions p = proportions;
        final String key = p + (withDeep ? " deep" : "");
        muscleSaid.setText("finding its muscles");
        new Thread(() -> {
            final long t0 = System.nanoTime();
            final com.example.fxsuite.life.body.muscle.MuscleBody.Bound b = muscleBodies.computeIfAbsent(key,
                    k -> com.example.fxsuite.life.body.fat.SheetBed.INSTANCE.body(p, withDeep, 1));
            final long ms = (System.nanoTime() - t0) / 1_000_000;
            javafx.application.Platform.runLater(() -> {
                if (mine == puppet) {
                    mine.muscles(b);
                    muscleSaid.setText(String.format(Locale.ROOT, "Muscles: %,d points, %,d triangles, found in %,d ms",
                            b.flesh().size(), b.flesh().triangleCount(), ms));
                }
            });
        }, "muscles").start();
    }

    /** Its skin found away from the screen, if it is drawn as one, then drawn; kept, to be drawn again at once. */
    private void skinned() {
        if (puppet.kind() != Puppet.Kind.SKIN) {
            return;
        }
        final Puppet mine = puppet;
        final Proportions p = proportions;
        muscleSaid.setText("finding its skin");
        new Thread(() -> {
            final long t0 = System.nanoTime();
            final com.example.fxsuite.life.body.flesh.Flesh skin = skins.moving(p);
            final Node clay = clay(skin);
            final long ms = (System.nanoTime() - t0) / 1_000_000;
            javafx.application.Platform.runLater(() -> {
                if (mine == puppet) {
                    mine.skin(clay == null ? skin : ClayHead.INSTANCE.headless(skin));
                    if (clay != null) {
                        mine.part(Part.HEAD).getChildren().add(clay);
                    }
                    muscleSaid.setText(String.format(Locale.ROOT, "Skin: %,d points, %,d triangles, found in %,d ms",
                            skin.size(), skin.triangleCount(), ms));
                }
            });
        }, "skin").start();
    }

    /**
     * The clay head made from the reference head {@code -Dhead.reference} names, if it does, as big as the head of the
     * body {@code skin} is, drawn in the head's frame in the skin's colour, to take the place of the skin's own head;
     * none, if it names none or it cannot be read.
     */
    private Node clay(final com.example.fxsuite.life.body.flesh.Flesh skin) {
        final String path = System.getProperty("head.reference");
        if (path == null) {
            return null;
        }
        try {
            final Mesher.Mesh head = ClayHead.INSTANCE.of(java.nio.file.Path.of(path));
            // set where the skull fitted under it sets it, at its own size; else as big as the skin's head
            final V3 at = com.example.fxsuite.life.body.bone.skull.SkullForm.INSTANCE.headAt().orElse(V3.ZERO);
            final double k = com.example.fxsuite.life.body.bone.skull.SkullForm.INSTANCE.headAt().isPresent() ? 1
                    : ClayHead.INSTANCE.scale(head, skin);
            final javafx.scene.Group g = com.example.fxsuite.life.body.bone.BoneView.INSTANCE.mapped(head, Paint.SKIN,
                    v -> v.times(k).plus(at), false);
            final javafx.scene.paint.PhongMaterial m = new javafx.scene.paint.PhongMaterial(
                    javafx.scene.paint.Color.web(com.example.fxsuite.life.body.skin.Skins.COLOUR));
            g.getChildren().forEach(n -> ((javafx.scene.shape.Shape3D) n).setMaterial(m));
            System.out.printf(Locale.ROOT, "clay head made %.3f times as big%n", k);
            return g;
        } catch (final java.io.IOException e) {
            System.out.println("the clay head not made: " + e.getMessage());
            return null;
        }
    }

    /** A skeleton found afresh on the grid asked for, away from the screen, then drawn; how many triangles, said. */
    private void regrid() {
        if (puppet.kind() != Puppet.Kind.SKELETON && puppet.kind() != Puppet.Kind.MUSCLES) {
            gridSaid.setText("for a skeleton");
            return;
        }
        final Puppet mine = puppet;
        final Detail d = new Detail(grid.getValue() / 10);
        gridSaid.setText("finding its bones");
        new Thread(() -> {
            final long n = mine.levels().triangles(d);
            javafx.application.Platform.runLater(() -> {
                if (mine == puppet) {
                    mine.detail(d);
                    gridSaid.setText(String.format(Locale.ROOT, "%.1f mm: %,d triangles", d.cell() * 10, n));
                }
            });
        }, "skeleton").start();
    }

    private void playing(final boolean on) {
        playing = on;
        play.setText(on ? "Pause" : "Play");
        last = -1;
    }

    /** How fast it is asked to go now: as the slider has it, or where the tour has got to. */
    private double speed() {
        if (how.getValue().marching()) {
            return walker.marching();
        }
        if (!tour.isSelected()) {
            return speed.getValue();
        }
        final double u = toured / TOUR * (TOURED.length - 1);
        final int k = Math.min(TOURED.length - 2, (int) Math.floor(u));
        final double t = u - k;
        final double v = TOURED[k] + (TOURED[k + 1] - TOURED[k]) * (t * t * (3 - 2 * t));
        setting = true;
        speed.setValue(v);
        setting = false;
        return v;
    }

    private void frame(final long now) {
        if (last >= 0) {
            final double real = Math.min(0.05, (now - last) / 1e9);
            frames += 1;
            fps = fps == 0 ? 1 / Math.max(real, 1e-3) : fps * 0.95 + 0.05 / Math.max(real, 1e-3);
            if (playing) {
                final double seconds = real * slow.getValue();
                toured = (toured + seconds) % TOUR;
                final double v = speed();
                walker.step(seconds, v, gatling.isSelected());
                setting = true;
                at.setValue(walker.cycle());
                setting = false;
                draw(walker.rig(), walker.flying(v));
            }
        }
        last = now;
        if (frames % 10 == 0) {
            figures();
        }
    }

    /** Stopped at the point of its stride chosen, as it is at the speed asked. */
    private void still() {
        walker.at(at.getValue(), speed(), gatling.isSelected());
        draw(walker.rig(), walker.flying(speed()));
    }

    /** {@code rig} drawn, its arms on the gun if it holds it, raised {@code flying} off the ground. */
    private void draw(final Rig rig, final double flying) {
        Rig shownRig = rig;
        if (gatling.isSelected()) {
            final GatlingHold.Held h = hold.hold(GatlingHold.arms(rig.pose(), arms), aim.getValue());
            arms = h.pose();
            shownRig = new Rig(proportions, h.pose());
            final GatlingHold.Frame f = h.gun();
            puppet.place(gunAt, f.origin(), f.right(), f.up(), f.ahead());
            strap.getChildren().setAll(puppet.drawn(h.strap()));
            statics = GunStatics.solve(proportions, h);
            loads.getChildren().setAll(puppet.drawn(arrows(statics)));
            loads.setVisible(forces.isSelected());
            balance = GunStatics.balance(shownRig, statics);
        } else {
            statics = null;
            loads.getChildren().clear();
            balance = shownRig.margin();
        }
        puppet.show(shownRig);
        lifted.setY(-(flying + SOLE));
        view.groundAt(0, treadmill.isSelected() ? walker.ahead() % REPEATS : 0);
    }

    private void said() {
        if (how.getValue().marching()) {
            speedSaid.setText(String.format(Locale.ROOT, "%.2f m/s, %s, %.0f steps a minute",
                    walker.marching() / 100, how.getValue().label(), how.getValue().cadence() * 120));
            return;
        }
        final double v = speed.getValue();
        final String how = v < 1 ? "standing" : Gait.run(v) <= 0 ? "walking" : Gait.run(v) >= 1 ? "running"
                : "walking into a run";
        speedSaid.setText(String.format(Locale.ROOT, "%.2f m/s, %s", v / 100, how));
    }

    /** Each force on the gun as an arrow from where it acts, as long as it is strong, coloured as what it is. */
    private static List<Surface> arrows(final GunStatics.Solved s) {
        final List<Surface> drawn = new java.util.ArrayList<>();
        for (final GunStatics.Force f : s.forces()) {
            final V3 to = f.at().plus(f.force().times(ARROW));
            if (f.force().length() * ARROW < 0.5) {
                continue;
            }
            final Paint paint = f.name().startsWith("weight") ? Paint.MARK_MASS
                    : f.name().startsWith("strap") ? Paint.MARK_SUPPORT
                    : f.name().startsWith("hip") ? Paint.MARK_TARGET : Paint.MARK_FALLING;
            drawn.add(Shapes.capsule(f.name(), paint, f.at(), to, new double[] {0.9, 0.9}, 1, 0.5, 8));
            drawn.add(Shapes.ellipsoid(f.name() + " head", paint, to, 1.8, 1.8, 1.8, 8, 5));
        }
        return drawn;
    }

    private void figures() {
        final double v = how.getValue().marching() ? walker.marching() : speed.getValue();
        final double cadence = v < 1 ? 0 : walker.cadence(v);
        final int down = walker.down();
        String gunSaid = "";
        if (statics != null) {
            gunSaid = String.format(Locale.ROOT, "%nThe gun, %.1f kg: the strap pulling %.0f N, the hip pushing %.0f N%s%n"
                            + "the right hand holding %.1f kg-force, the left %.1f%n",
                    statics.mass(), statics.strap(), statics.hip().length(), statics.sliding() ? ", sliding" : "",
                    statics.right().length() / GunStatics.G, statics.left().length() / GunStatics.G);
        }
        bus.publish(new Bus.Figures(String.format(Locale.ROOT,
                "%s at %.2f m/s%n%.0f steps a minute, a stride %.2f m (its feet carrying it %.2f m of it)%n"
                        + "each foot down %.0f%% of a stride, in the air %.0f%%%n"
                        + "at %.2f of its stride, %s%n%s"
                        + "its centre of mass %.1f cm inside its feet%s%n%.0f frames a second",
                look.getValue().label(), v / 100, cadence * 120, cadence > 0 ? v / cadence / 100 : 0,
                walker.stride(v) / 100, walker.stance(v) * 100, how.getValue().marching() ? 0 : Gait.flight(v) * 100,
                walker.cycle(),
                down > 0 ? "its right foot down" : down < 0 ? "its left foot down" : "both feet off the ground",
                gunSaid, balance, statics != null ? ", with the gun" : "", fps)));
    }
}
