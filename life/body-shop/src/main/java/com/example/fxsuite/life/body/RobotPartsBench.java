package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.avatar.bench.kit.Workshop;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.lang.reflect.RecordComponent;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The robot parts bench: a robot's own parts ({@link RobotDesign}) refined one at a time, still, through the bench
 * kit's model view: its spine, its chest or its head, alone and near, or on the whole frame where it goes; its head
 * as its frame has it or shelled. Every number of the part's design on a slider or a tick, each change drawing it
 * afresh; every colour of a robot on a picker. What it is read out as code, to be set as the robot's design.
 *
 * <p>{@code -Dparts.part} (spine, chest, head), {@code -Dparts.whole=true}, {@code -Dparts.shelled=true} what it
 * shows first; {@code -Dparts.yaw}, {@code -Dparts.pitch}, {@code -Dparts.distance} (m) where it is seen from;
 * {@code -Dbench.snapshot=out.png} saves the window after {@code -Dbench.settle} ms and exits.
 */
public final class RobotPartsBench extends Application {

    /** A robot's own parts, each refined on its own. */
    enum Which {
        SPINE("Spine", RobotDesign.Spine.class, "spine:"),
        CHEST("Chest", RobotDesign.Chest.class, "chest:"),
        HEAD("Head", RobotDesign.Head.class, "head:");

        final String label;
        final Class<? extends Record> design;
        final String prefix;

        Which(final String label, final Class<? extends Record> design, final String prefix) {
            this.label = label;
            this.design = design;
            this.prefix = prefix;
        }

        Record of(final RobotDesign d) {
            return switch (this) {
                case SPINE -> d.spine();
                case CHEST -> d.chest();
                case HEAD -> d.head();
            };
        }

        RobotDesign with(final RobotDesign d, final Record r) {
            return switch (this) {
                case SPINE -> d.with((RobotDesign.Spine) r);
                case CHEST -> d.with((RobotDesign.Chest) r);
                case HEAD -> d.with((RobotDesign.Head) r);
            };
        }

        /** Where it is looked at on the robot standing at rest, cm up; how far off, cm. */
        double height() {
            return this == HEAD ? 158 : this == CHEST ? 128 : 125;
        }

        double distance() {
            return this == SPINE ? 190 : this == CHEST ? 140 : 90;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    /** Each number of a design: what it is called, and how far it may go. */
    private record Range(String label, double min, double max) {
    }

    private static final Map<String, Range> RANGES = Map.ofEntries(
            Map.entry("Spine.waist", new Range("Vertebrae up the waist", 2, 8)),
            Map.entry("Spine.chest", new Range("Vertebrae up the chest", 4, 14)),
            Map.entry("Spine.neck", new Range("Vertebrae up the neck", 2, 7)),
            Map.entry("Spine.width", new Range("Vertebra width, cm", 2, 10)),
            Map.entry("Spine.depth", new Range("Vertebra depth, cm", 2, 10)),
            Map.entry("Spine.disc", new Range("Disc, share of a vertebra", 0.05, 0.5)),
            Map.entry("Spine.fin", new Range("Fin behind, cm", 0, 6)),
            Map.entry("Spine.arms", new Range("Arms out, share of its width", 0, 1.5)),
            Map.entry("Spine.struts", new Range("Struts up the waist", 0, 1)),
            Map.entry("Spine.strut", new Range("Strut thickness, cm", 0.3, 2.5)),
            Map.entry("Chest.ribs", new Range("Pairs of ribs", 2, 10)),
            Map.entry("Chest.rib", new Range("Rib width, cm", 0.4, 4)),
            Map.entry("Chest.thick", new Range("Rib thickness, cm", 0.3, 3)),
            Map.entry("Chest.broad", new Range("Cage breadth, share of a person's", 0.6, 1.4)),
            Map.entry("Chest.deep", new Range("Cage depth, cm", 5, 14)),
            Map.entry("Chest.plate", new Range("A plate down its front", 0, 1)),
            Map.entry("Chest.core", new Range("A glowing core", 0, 1)),
            Map.entry("Chest.radius", new Range("Core radius, cm", 0.8, 5)),
            Map.entry("Chest.yoke", new Range("A yoke across its back", 0, 1)),
            Map.entry("Head.broad", new Range("Breadth, share of a head", 0.7, 1.4)),
            Map.entry("Head.tall", new Range("Height, share of a head", 0.7, 1.4)),
            Map.entry("Head.deep", new Range("Depth, share of a head", 0.7, 1.4)),
            Map.entry("Head.panels", new Range("Panels round", 5, 32)),
            Map.entry("Head.visor", new Range("Visor width, cm", 4, 16)),
            Map.entry("Head.band", new Range("Visor height, cm", 0.6, 6)),
            Map.entry("Head.pods", new Range("Pod radius, cm", 0.5, 4)),
            Map.entry("Head.chin", new Range("A guard under its chin", 0, 1)),
            Map.entry("Head.crest", new Range("A crest along its top", 0, 1)));

    private final Bus bus = new Bus();
    private final Shell shell = new Shell("Innocent Melons: the robot parts bench", bus);
    private final ModelView view = new ModelView();

    private record Asked(RobotDesign design, Which which, boolean whole, boolean shelled, Palette palette) {
    }

    private record Made(ModelView.Model model, String figures) {
    }

    private final Workshop<Asked, Made> workshop = new Workshop<>("robot parts", bus, Asked.class, this::make);

    private RobotDesign design = RobotDesign.DEFAULT;
    private Palette palette = Palette.DEFAULT;
    private final ChoiceBox<Which> which = new ChoiceBox<>();
    private final CheckBox whole = new CheckBox("On the whole frame");
    private final CheckBox shelled = new CheckBox("Shelled");
    private final GridPane knobs = new GridPane();
    private final VBox colours = new VBox(4);

    @Override
    public void start(final Stage stage) {
        bus.subscribe(Made.class, this::show, Bus.SCREEN);
        which.getItems().setAll(Which.values());
        which.setValue(Which.valueOf(System.getProperty("parts.part", "spine").toUpperCase(Locale.ROOT)));
        whole.setSelected(Boolean.getBoolean("parts.whole"));
        shelled.setSelected(Boolean.getBoolean("parts.shelled"));
        which.valueProperty().addListener((o, was, is) -> {
            showKnobs();
            look();
            ask();
        });
        whole.selectedProperty().addListener((o, was, is) -> ask());
        shelled.selectedProperty().addListener((o, was, is) -> ask());
        knobs.setHgap(8);
        knobs.setVgap(4);

        final Button first = new Button("Back to the robot's design");
        first.setOnAction(e -> {
            design = RobotDesign.DEFAULT;
            showKnobs();
            ask();
        });
        final Button resetColours = new Button("Its colours as they were");
        resetColours.setOnAction(e -> {
            palette = Palette.DEFAULT;
            showColours();
            ask();
        });
        final Label hint = new Label("The design read out below as code: set it as RobotDesign.DEFAULT to make it the"
                + " robot's. Drag to turn the view, right-drag to move it, scroll to come nearer.");
        hint.setWrapText(true);
        final VBox side = new VBox(10,
                new Label("The part"), new FlowPane(10, 6, which, whole, shelled),
                new Label("Its design"), knobs, first,
                new Label("Its colours"), colours, resetColours,
                hint);
        side.setPadding(new Insets(8));
        final ScrollPane scroll = new ScrollPane(side);
        scroll.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The robot parts bench"), scroll, 440);
        shell.middle(view.node());
        view.grid(0, 0, 0, 400, 10, 5);
        view.zoom(20, 1_500);
        showKnobs();
        showColours();
        look();
        ask();
        shell.show(stage, 1440, 920);
    }

    @Override
    public void stop() {
        workshop.close();
    }

    /** Looked at where the part chosen is, from its front, a little from its left and above. */
    private void look() {
        final Which w = which.getValue();
        view.lookAt(0, w.height(), 0);
        view.from("parts", 30, 12, w.distance() / 100);
    }

    private void ask() {
        bus.publish(new Asked(design, which.getValue(), whole.isSelected(), shelled.isSelected(), palette));
    }

    /** A slider or a tick for every number of the chosen part's design, each changing it alone. */
    private void showKnobs() {
        knobs.getChildren().clear();
        final Which w = which.getValue();
        int row = 0;
        for (final RecordComponent c : w.design.getRecordComponents()) {
            final Range r = RANGES.get(w.design.getSimpleName() + "." + c.getName());
            final Object now = get(w.of(design), c);
            if (c.getType() == boolean.class) {
                final CheckBox tick = new CheckBox(r.label());
                tick.setSelected((Boolean) now);
                tick.selectedProperty().addListener((o, was, is) -> set(w, c, is));
                knobs.add(tick, 0, row++, 3, 1);
                continue;
            }
            final boolean whole = c.getType() == int.class;
            final Slider s = new Slider(r.min(), r.max(), ((Number) now).doubleValue());
            s.setPrefWidth(170);
            if (whole) {
                s.setMajorTickUnit(1);
                s.setMinorTickCount(0);
                s.setSnapToTicks(true);
            }
            final Label value = new Label(said(now));
            s.valueProperty().addListener((o, was, is) -> {
                final Object v = whole ? (Object) (int) Math.round(is.doubleValue()) : (Object) is.doubleValue();
                value.setText(said(v));
                set(w, c, v);
            });
            knobs.add(new Label(r.label()), 0, row);
            knobs.add(s, 1, row);
            knobs.add(value, 2, row++);
        }
    }

    private static String said(final Object v) {
        return v instanceof Integer i ? String.valueOf(i) : String.format(Locale.ROOT, "%.2f", (Double) v);
    }

    private static Object get(final Record r, final RecordComponent c) {
        try {
            return c.getAccessor().invoke(r);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /** The chosen part's design with {@code c} set to {@code v}, all else as it was; drawn afresh. */
    private void set(final Which w, final RecordComponent c, final Object v) {
        final Record r = w.of(design);
        final RecordComponent[] all = w.design.getRecordComponents();
        final Object[] args = new Object[all.length];
        final Class<?>[] types = new Class<?>[all.length];
        for (int k = 0; k < all.length; k++) {
            args[k] = all[k].getName().equals(c.getName()) ? v : get(r, all[k]);
            types[k] = all[k].getType();
        }
        try {
            design = w.with(design, (Record) w.design.getDeclaredConstructor(types).newInstance(args));
        } catch (final ReflectiveOperationException | IllegalArgumentException e) {
            return;
        }
        ask();
    }

    private void showColours() {
        colours.getChildren().clear();
        final GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(4);
        int row = 0;
        for (final Paint p : Paint.ROBOT) {
            final ColorPicker picker = new ColorPicker(Color.web(palette.colour(p)));
            picker.setOnAction(e -> {
                final Color c = picker.getValue();
                palette = palette.with(p, String.format(Locale.ROOT, "#%02x%02x%02x", Math.round(c.getRed() * 255),
                        Math.round(c.getGreen() * 255), Math.round(c.getBlue() * 255)));
                ask();
            });
            grid.add(new Label(p.label()), 0, row);
            grid.add(picker, 1, row++);
        }
        colours.getChildren().add(grid);
    }

    /**
     * On the workshop's worker: the robot standing at rest, as designed; the part alone (its frame, or its shell if
     * shelled), or the whole frame and its pistons (and its shell, if shelled); the design as code.
     */
    private Made make(final Asked a) {
        final Body body = new Body(Proportions.PAPERS, Pose.REST, null, Body.Look.ROBOT, a.design());
        final Set<Body.Layer> layers = a.whole() ? EnumSet.of(Body.Layer.BONES, Body.Layer.MUSCLES)
                : EnumSet.of(Body.Layer.BONES);
        if (a.shelled()) {
            layers.add(Body.Layer.SKIN);
            if (!a.whole()) {
                layers.remove(Body.Layer.BONES);
            }
        }
        List<Body.Piece> pieces = body.pieces(layers);
        if (!a.whole()) {
            final String prefix = a.which().prefix;
            pieces = pieces.stream().filter(p -> p.surface().name().startsWith(prefix)
                    || a.which() == Which.CHEST && p.surface().name().startsWith("spine: chest")
                    || a.shelled() && a.which() == Which.HEAD && p.part() == Part.HEAD).toList();
        }
        final Group drawn = FigureModel.of(pieces, false, null, a.palette());
        return new Made(ModelView.Model.of(drawn), String.format(Locale.ROOT, "The robot's %s: %d pieces, %d"
                        + " triangles%n%n%s", a.which().label.toLowerCase(Locale.ROOT), pieces.size(),
                FigureModel.triangles(pieces), a.design().code()));
    }

    private void show(final Made m) {
        view.show(m.model());
        bus.publish(new Bus.Figures(m.figures()));
        bus.publish(new Bus.Problems(""));
    }
}
