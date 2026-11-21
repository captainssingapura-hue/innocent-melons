package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Grip;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.avatar.bench.kit.Workshop;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.PickResult;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.DoubleConsumer;

/**
 * The body shop's bench: the robot's body, still, to look at through the bench kit's model view and pose by hand, as
 * the interactive model of the papers is (Papers/Body). Its shell, its frame, its pistons and the marks of its balance,
 * each shown or not; its shell solid or seen through. Pressed on, a part is chosen, its joint's ways each on a slider;
 * dragged, it turns at its joint after the mouse, no further than the joint goes (twisting about its own length with
 * the control key held); dragged anywhere else, the view turns. The interactive model's presets at a press; the
 * body's proportions by sliders. Whether it would stand so, read out: how far inside the area its feet hold it up over
 * its centre of mass falls. Each change makes it afresh and shows it in place of what was. Put together of the bench
 * kit's parts.
 *
 * <p>Exposed, its frame and pistons; shelled, its shell. Every colour it is drawn in its own picker.
 *
 * <p>With its centre of gravity kept, its feet on the ground stay where they are, and whatever is done to it, it
 * shifts its weight, its pelvis and its standing legs, to keep its centre of gravity over where it is kept ({@link
 * Balance}); where that is, moved apart from all else, by sliders or by dragging its centre of gravity, the red ball.
 * Its floor seen through, a grid of lines only.
 *
 * <p>{@code -Dbody.preset} (a preset's name), {@code -Dbody.layers} (skin, bones, muscles, marks, by commas),
 * {@code -Dbody.through=true}, {@code -Dbody.keep=true}, {@code -Dbody.select} (a part's name) what it shows first;
 * {@code -Dbody.yaw}, {@code -Dbody.pitch}, {@code -Dbody.distance} (m) where it is seen from, {@code -Dbody.at} how
 * high it is looked at (cm); {@code -Dbench.snapshot=out.png} saves the window after {@code -Dbench.settle} ms and exits.
 */
public final class BodyShopBench extends Application {

    private final Bus bus = new Bus();
    private final Shell shell = new Shell("Innocent Melons: the body shop", bus);
    private final ModelView view = new ModelView();

    private record Asked(Proportions proportions, Pose pose, Set<Body.Layer> layers, boolean through, Part chosen,
                         V3 kept, Palette palette) {
    }

    private record Made(ModelView.Model model, String figures) {
    }

    private final Workshop<Asked, Made> workshop = new Workshop<>("body shop", bus, Asked.class, this::make);

    // what it is, on the screen's thread
    private Proportions proportions = Proportions.PAPERS;
    private Pose pose = Pose.REST;
    private Rig rig = new Rig(proportions, pose);
    private Part chosen;

    private final CheckBox skin = new CheckBox("Shell");
    private final CheckBox through = new CheckBox("Seen through");
    private final CheckBox bones = new CheckBox("Frame");
    private final CheckBox muscles = new CheckBox("Pistons");
    private final CheckBox marks = new CheckBox("Balance");
    private final CheckBox both = new CheckBox("Both sides alike");
    private final Label partName = new Label("None chosen: press on a part");
    private final GridPane joint = new GridPane();
    private final Slider[] shape = new Slider[6];
    /** The part the joint's sliders are for, and they. */
    private Part slidersFor;
    private final java.util.List<Slider> jointSliders = new java.util.ArrayList<>();
    /** While sliders are set from what is: asked nothing. */
    private boolean setting;

    // keeping its centre of gravity: whether, and over where (x, z)
    private final CheckBox keep = new CheckBox("Keep its centre of gravity");
    private final Slider keptRight = new Slider(-40, 40, 0);
    private final Slider keptAhead = new Slider(-40, 40, 0);
    private V3 kept;
    // the colours it is drawn in
    private Palette palette = Palette.DEFAULT;
    private final VBox colours = new VBox(4);

    /** Whether what is held is its centre of gravity, not a part. */
    private boolean holdingCentre;

    // what is held: the part, the point of it held (in its frame), the plane it is dragged across (the scene's)
    private Part held;
    private V3 heldAt;
    private Point3D planeAt;
    private Point3D planeNormal;

    @Override
    public void start(final Stage stage) {
        bus.subscribe(Made.class, this::show, Bus.SCREEN);
        final String layers = System.getProperty("body.layers", "skin,marks").toLowerCase(Locale.ROOT);
        skin.setSelected(layers.contains("skin"));
        bones.setSelected(layers.contains("bones"));
        muscles.setSelected(layers.contains("muscles"));
        marks.setSelected(layers.contains("marks"));
        through.setSelected(Boolean.getBoolean("body.through"));
        for (final CheckBox c : List.of(skin, through, bones, muscles, marks)) {
            c.selectedProperty().addListener((o, was, is) -> ask());
        }

        final FlowPane presets = new FlowPane(6, 6);
        for (final Pose.Named n : Pose.PRESETS) {
            final Button b = new Button(n.name());
            b.setOnAction(e -> {
                // a preset as it is; its centre of gravity kept where it puts it
                pose = n.pose();
                if (kept != null) {
                    kept = ground(new Rig(proportions, pose).centreOfMass());
                }
                ask();
            });
            presets.getChildren().add(b);
        }
        final Button restPart = new Button("Rest this part");
        restPart.setOnAction(e -> {
            if (chosen != null) {
                Pose next = pose.rested(chosen);
                if (both.isSelected()) {
                    next = next.rested(chosen.other());
                }
                change(next, Set.of(chosen, chosen.other()));
            }
        });

        keptRight.setPrefWidth(170);
        keptAhead.setPrefWidth(170);
        final GridPane keeping = new GridPane();
        keeping.setHgap(8);
        keeping.setVgap(4);
        final Label rightValue = new Label();
        final Label aheadValue = new Label();
        keeping.add(new Label("To its right, cm"), 0, 0);
        keeping.add(keptRight, 1, 0);
        keeping.add(rightValue, 2, 0);
        keeping.add(new Label("Ahead, cm"), 0, 1);
        keeping.add(keptAhead, 1, 1);
        keeping.add(aheadValue, 2, 1);
        for (final Slider s : List.of(keptRight, keptAhead)) {
            s.valueProperty().addListener((o, was, is) -> {
                rightValue.setText(String.format(Locale.ROOT, "%.1f", keptRight.getValue()));
                aheadValue.setText(String.format(Locale.ROOT, "%.1f", keptAhead.getValue()));
                if (!setting) {
                    keepOver(new V3(keptRight.getValue(), 0, keptAhead.getValue()));
                }
            });
        }
        keep.selectedProperty().addListener((o, was, is) -> {
            if (!setting) {
                kept = is ? ground(rig.centreOfMass()) : null;
                ask();
            }
        });
        final Button middle = new Button("Over the middle of its feet");
        middle.setOnAction(e -> {
            final List<double[]> support = rig.support();
            if (!support.isEmpty()) {
                double x = 0;
                double z = 0;
                for (final double[] s : support) {
                    x += s[0];
                    z += s[1];
                }
                keepOver(new V3(x / support.size(), 0, z / support.size()));
            }
        });
        joint.setHgap(8);
        joint.setVgap(4);

        final GridPane made = new GridPane();
        made.setHgap(8);
        made.setVgap(4);
        final String[] names = {"Stature, cm", "Build", "Hip joints up", "Arms long", "Shoulders apart",
            "Hips apart"};
        final double[][] ranges = {{100, 230}, {0.7, 1.6}, {0.45, 0.62}, {0.36, 0.52}, {0.17, 0.27}, {0.07, 0.14}};
        for (int k = 0; k < 6; k++) {
            shape[k] = new Slider(ranges[k][0], ranges[k][1], ranges[k][0]);
            shape[k].setPrefWidth(170);
            final Label value = new Label();
            final Slider s = shape[k];
            s.valueProperty().addListener((o, was, is) -> {
                value.setText(String.format(Locale.ROOT, s.getMax() > 10 ? "%.0f" : "%.3f", is.doubleValue()));
                if (!setting) {
                    proportions = new Proportions(shape[0].getValue(), shape[1].getValue(), shape[2].getValue(),
                            shape[3].getValue(), shape[4].getValue(), shape[5].getValue());
                    change(pose, Set.of());
                }
            });
            made.add(new Label(names[k]), 0, k);
            made.add(s, 1, k);
            made.add(value, 2, k);
        }
        final Button papers = new Button("The papers' figure");
        papers.setOnAction(e -> {
            proportions = Proportions.PAPERS;
            ask();
        });

        final Button exposed = new Button("A robot, exposed");
        exposed.setOnAction(e -> layered(false, true, true));
        final Button shelled = new Button("A robot, shelled");
        shelled.setOnAction(e -> layered(true, false, false));
        final Button resetColours = new Button("Its colours as they were");
        resetColours.setOnAction(e -> {
            palette = Palette.DEFAULT;
            showColours();
            ask();
        });

        final Label hint = new Label("Press on a part to choose it; drag it to turn it at its joint, with the control"
                + " key to twist it. Drag the red ball, its centre of gravity, to move where it is kept. Drag"
                + " elsewhere to turn the view, right-drag to move it, scroll to come nearer.");
        hint.setWrapText(true);
        final VBox side = new VBox(10,
                new Label("What it is"), new FlowPane(10, 6, exposed, shelled),
                new Label("Presets"), presets,
                new Label("Shown"), new FlowPane(10, 6, skin, through, bones, muscles, marks),
                new Label("The part chosen"), partName, joint, new FlowPane(10, 6, both, restPart),
                new Label("Its balance"), keep, keeping, middle,
                new Label("Its colours"), colours, resetColours,
                new Label("How it is made"), made, papers,
                hint);
        side.setPadding(new Insets(8));
        side.setPrefWidth(420);
        final ScrollPane scroll = new ScrollPane(side);
        scroll.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The body shop"), scroll, 440);
        shell.middle(view.node());
        // the floor seen through, lines only: every 10 cm, every half metre darker
        view.grid(0, 0, 0, 400, 10, 5);
        view.lookAt(0, Double.parseDouble(System.getProperty("body.at", "95")), 0);
        view.from("body", 25, 8, 3.6);
        view.zoom(40, 2_000);
        view.grip(new Hold());

        final String preset = System.getProperty("body.preset");
        Pose.PRESETS.stream().filter(n -> n.name().equalsIgnoreCase(preset)).findFirst().ifPresent(n -> pose = n.pose());
        final String select = System.getProperty("body.select");
        if (select != null) {
            chosen = Part.valueOf(select.toUpperCase(Locale.ROOT).replace(' ', '_'));
        }
        showColours();
        rig = new Rig(proportions, pose);
        if (Boolean.getBoolean("body.keep")) {
            kept = ground(rig.centreOfMass());
        }
        ask();
        shell.show(stage, 1440, 920);
    }

    @Override
    public void stop() {
        workshop.close();
    }

    /** Its shell, its frame and its pistons each shown or not. */
    private void layered(final boolean withSkin, final boolean withBones, final boolean withMuscles) {
        skin.setSelected(withSkin);
        bones.setSelected(withBones);
        muscles.setSelected(withMuscles);
        through.setSelected(false);
        showColours();
        ask();
    }

    /** A picker for every colour it is drawn in, each changing that colour alone. */
    private void showColours() {
        colours.getChildren().clear();
        final GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(4);
        int row = 0;
        for (final Paint p : Paint.ROBOT) {
            final javafx.scene.control.ColorPicker picker =
                    new javafx.scene.control.ColorPicker(javafx.scene.paint.Color.web(palette.colour(p)));
            picker.setOnAction(e -> {
                final javafx.scene.paint.Color c = picker.getValue();
                palette = palette.with(p, String.format(Locale.ROOT, "#%02x%02x%02x", Math.round(c.getRed() * 255),
                        Math.round(c.getGreen() * 255), Math.round(c.getBlue() * 255)));
                ask();
            });
            grid.add(new Label(p.label()), 0, row);
            grid.add(picker, 1, row++);
        }
        colours.getChildren().add(grid);
    }

    /** {@code p}, on the ground: its x and z, nothing up. */
    private static V3 ground(final V3 p) {
        return new V3(p.x(), 0, p.z());
    }

    /**
     * The body held as {@code next}, by hand: its centre of gravity kept where it is, if it is, its feet that are on
     * the ground kept there but for a leg {@code moving} has a part of.
     */
    private void change(final Pose next, final Set<Part> moving) {
        pose = kept == null ? next : Balance.keep(proportions, pose, next, kept, moving);
        ask();
    }

    /** Its centre of gravity kept over {@code over} from now on, the body shifting its weight there. */
    private void keepOver(final V3 over) {
        kept = ground(over);
        pose = Balance.keep(proportions, pose, pose, kept, Set.of());
        ask();
    }

    /** What is shown, asked of the workshop; the rig to drag by made now, and the sliders set to what is. */
    private void ask() {
        rig = new Rig(proportions, pose);
        final Set<Body.Layer> layers = EnumSet.noneOf(Body.Layer.class);
        if (skin.isSelected()) {
            layers.add(Body.Layer.SKIN);
        }
        if (bones.isSelected()) {
            layers.add(Body.Layer.BONES);
        }
        if (muscles.isSelected()) {
            layers.add(Body.Layer.MUSCLES);
        }
        if (marks.isSelected()) {
            layers.add(Body.Layer.MARKS);
        }
        setSliders();
        bus.publish(new Asked(proportions, pose, layers, through.isSelected(), chosen, kept, palette));
    }

    /** The proportions' sliders and the chosen part's set to what is, asking nothing. */
    private void setSliders() {
        setting = true;
        shape[0].setValue(proportions.stature());
        shape[1].setValue(proportions.build());
        shape[2].setValue(proportions.legs());
        shape[3].setValue(proportions.arms());
        shape[4].setValue(proportions.shoulders());
        shape[5].setValue(proportions.hips());
        keep.setSelected(kept != null);
        keptRight.setDisable(kept == null);
        keptAhead.setDisable(kept == null);
        if (kept != null) {
            keptRight.setValue(kept.x());
            keptAhead.setValue(kept.z());
        }
        if (chosen != null && chosen == slidersFor) {
            // the same part: its sliders moved to what is, not made again under the mouse
            for (int k = 0; k < chosen.dofs().size(); k++) {
                jointSliders.get(k).setValue(pose.get(chosen, chosen.dofs().get(k)));
            }
            setting = false;
            return;
        }
        slidersFor = chosen;
        jointSliders.clear();
        joint.getChildren().clear();
        if (chosen != null) {
            partName.setText(chosen.said() + (chosen.dofs().isEmpty() ? "" : ", turning at its joint:"));
            int row = 0;
            for (final Dof d : chosen.dofs()) {
                final Slider s = new Slider(d.min(), d.max(), pose.get(chosen, d));
                s.setPrefWidth(170);
                final Label value = new Label(String.format(Locale.ROOT, "%.0f°", pose.get(chosen, d)));
                final Part part = chosen;
                final DoubleConsumer set = v -> {
                    Pose next = pose.with(part, d, v);
                    if (both.isSelected() && part.other() != part) {
                        next = next.mirroredTo(part.other());
                    }
                    change(next, Set.of(part, part.other()));
                };
                s.valueProperty().addListener((o, was, is) -> {
                    value.setText(String.format(Locale.ROOT, "%.0f°", is.doubleValue()));
                    if (!setting) {
                        set.accept(is.doubleValue());
                    }
                });
                jointSliders.add(s);
                joint.add(new Label(d.label()), 0, row);
                joint.add(s, 1, row);
                joint.add(value, 2, row++);
            }
        }
        setting = false;
    }

    /** On the workshop's worker: the body made and drawn, the chosen part's skin warmer; what it is, in figures. */
    private Made make(final Asked a) {
        final Body body = new Body(a.proportions(), a.pose(), a.kept());
        final List<Body.Piece> pieces = body.pieces(a.layers());
        final Group drawn = FigureModel.of(pieces, a.through(), a.chosen(), a.palette());
        return new Made(ModelView.Model.touchable(drawn), figures(body, a, FigureModel.triangles(pieces)));
    }

    private static String figures(final Body body, final Asked a, final int triangles) {
        final Rig r = body.rig();
        final double margin = r.margin();
        final long right = r.contacts().subList(0, 5).stream().filter(c -> c.y() < Rig.TOUCHING).count();
        final long left = r.contacts().subList(5, 10).stream().filter(c -> c.y() < Rig.TOUCHING).count();
        final String feet = right > 0 && left > 0 ? "both feet down" : right > 0 ? "on the right foot"
                : left > 0 ? "on the left foot" : "no foot down";
        final String balance = Double.isInfinite(margin) ? "nothing under it"
                : margin >= 0 ? String.format(Locale.ROOT, "Balanced: its centre of mass %.1f cm inside the area its"
                        + " feet hold it up over", margin)
                : String.format(Locale.ROOT, "Would tip over: its centre of mass %.1f cm outside the area its feet"
                        + " hold it up over", -margin);
        final StringBuilder joints = new StringBuilder();
        if (a.chosen() != null) {
            joints.append(String.format(Locale.ROOT, "%n%s:", a.chosen().said()));
            for (final Dof d : a.chosen().dofs()) {
                joints.append(String.format(Locale.ROOT, " %s %.0f°;", d.label().toLowerCase(Locale.ROOT),
                        a.pose().get(a.chosen(), d)));
            }
        }
        final V3 c = r.centreOfMass();
        final String keeping = a.kept() == null ? "" : String.format(Locale.ROOT, "%nKept over %.1f cm to its right,"
                + " %.1f cm ahead; it is %.1f cm from there", a.kept().x(), a.kept().z(),
                Math.hypot(c.x() - a.kept().x(), c.z() - a.kept().z()));
        return String.format(Locale.ROOT, "%s%n%s; %s; its hip joints %.0f cm up, its centre of mass %.0f cm up%s%n"
                        + "%d triangles", a.proportions().said(), balance, feet, r.lift(), c.y(), keeping + joints,
                triangles);
    }

    /** On the screen's thread: shown in place of what was; what it is, in its figures. */
    private void show(final Made m) {
        view.show(m.model());
        bus.publish(new Bus.Figures(m.figures()));
        bus.publish(new Bus.Problems(""));
    }

    /** The bench's hold on the body: a part pressed on chosen and taken hold of, dragged turned at its joint. */
    private final class Hold implements Grip {

        @Override
        public boolean take(final PickResult picked, final Ray ray, final boolean control) {
            if (FigureModel.isCentre(picked.getIntersectedNode())) {
                // its centre of gravity: dragged across the level it is at, kept from now on over where it is let go
                holdingCentre = true;
                held = null;
                planeAt = picked.getIntersectedPoint();
                planeNormal = new Point3D(0, 1, 0);
                if (kept == null) {
                    kept = ground(rig.centreOfMass());
                    ask();
                }
                return true;
            }
            final Part part = FigureModel.partOf(picked.getIntersectedNode());
            if (part == null) {
                return false;
            }
            holdingCentre = false;
            chosen = part;
            held = part;
            final Point3D at = picked.getIntersectedPoint();
            heldAt = rig.holding(part, FigureModel.body(at));
            planeAt = at;
            planeNormal = ray.way();
            ask();
            return true;
        }

        @Override
        public void move(final Ray ray, final boolean control) {
            final Point3D to = ray.across(planeAt, planeNormal);
            if (to == null) {
                return;
            }
            if (holdingCentre) {
                keepOver(FigureModel.body(to));
                return;
            }
            if (held == null) {
                return;
            }
            // where it is drawn to, in the body's frame, hanging free from its pelvis as it is dragged
            final V3 target = FigureModel.body(to).minus(new V3(0, rig.lift(), 0));
            Pose next = Drag.toward(proportions, pose, held, heldAt, target, control);
            if (both.isSelected() && held.other() != held) {
                next = next.mirroredTo(held.other());
            }
            change(next, Set.of(held, held.other()));
        }

        @Override
        public void letGo() {
            held = null;
            holdingCentre = false;
        }
    }
}
