package com.example.fxsuite.life.body;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.ModelView;
import com.example.fxsuite.avatar.bench.kit.Shell;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.lang.reflect.RecordComponent;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The wardrobe bench: the one base character ({@link Person}), its body made as its proportions say, any clothes put
 * on it ({@link Wear}) and seen as it stands or walks on the spot, over and over: what is close on a part moving with
 * it, what is fitted bending over the joints, what hangs clearing the legs. Its body's numbers and its skin on sliders
 * and a picker; an outfit to start from; each piece on or off, every number and colour of it on a slider, a tick or a
 * picker. Slowed, or stopped at any point of the stride.
 *
 * <p>{@code -Dwardrobe.outfit} (an outfit's name), {@code -Dwardrobe.speed} (cm/s), {@code -Dwardrobe.at} (0 to 1,
 * stopped there), {@code -Dwardrobe.yaw}, {@code -Dwardrobe.pitch}, {@code -Dwardrobe.distance} (m) what it shows
 * first, {@code -Dwardrobe.look} the height looked at (cm); {@code -Dbench.snapshot=out.png} saves the window after {@code -Dbench.settle} ms and exits.
 */
public final class WardrobeBench extends Application {

    private static final double REPEATS = 50;
    private static final double SOLE = 0.5;

    /** Each number of a piece of clothing: what it is called, and how far it may go. */
    private record Range(String label, double min, double max) {
    }

    private static final Map<String, Range> RANGES = Map.ofEntries(
            Map.entry("Robe.ease", new Range("How loose, cm", 0.5, 5)),
            Map.entry("Robe.hem", new Range("Hem over the ground, cm", 2, 60)),
            Map.entry("Robe.flare", new Range("Skirt flaring", 0, 1.5)),
            Map.entry("Sash.wide", new Range("How wide, cm", 5, 30)),
            Map.entry("Shirt.ease", new Range("How loose, cm", 0.3, 4)),
            Map.entry("Boots.height", new Range("Up the shin, share", 0.05, 1)),
            Map.entry("Belt.wide", new Range("How wide, cm", 1.5, 10)),
            Map.entry("Briefs.rise", new Range("Rise, share of the hips", 0.3, 1)),
            Map.entry("BoxerBriefs.rise", new Range("Rise, share of the hips", 0.3, 1)),
            Map.entry("BoxerBriefs.length", new Range("Down the thigh, share", 0.05, 0.9)),
            Map.entry("Panties.rise", new Range("Rise, share of the hips", 0.3, 1)),
            Map.entry("Undershirt.ease", new Range("How loose, cm", 0.1, 2)),
            Map.entry("Bra.band", new Range("Band, cm", 1, 8)),
            Map.entry("Bra.cups", new Range("Cups, cm out", 0.5, 6)),
            Map.entry("Jacket.ease", new Range("How loose, cm", 0.5, 5)),
            Map.entry("Jacket.hem", new Range("Below the hips, cm", -5, 25)),
            Map.entry("Hoodie.ease", new Range("How loose, cm", 0.5, 4)),
            Map.entry("Jersey.ease", new Range("How loose, cm", 0.3, 3)),
            Map.entry("Trousers.ease", new Range("How loose, cm", 0.5, 4)),
            Map.entry("Trousers.length", new Range("Down the shin, share", 0.3, 1)),
            Map.entry("Shorts.ease", new Range("How loose, cm", 0.5, 4)),
            Map.entry("Shorts.length", new Range("Down the thigh, share", 0.1, 1)),
            Map.entry("Socks.height", new Range("Up the shin, share", 0.05, 1)),
            Map.entry("Apron.length", new Range("Down from the waist, cm", 15, 90)),
            Map.entry("ShortHair.thick", new Range("How deep, cm", 0.2, 4)),
            Map.entry("LongHair.length", new Range("Below the head, cm", 2, 50)));


    private final Bus bus = new Bus();
    private final Shell shell = new Shell("Innocent Melons: the wardrobe", bus);
    private final ModelView view = new ModelView();

    private Person person = Person.BASE;
    private Dressed dressed;
    private Puppet puppet;
    private Walker walker;
    private final Group shown = new Group();
    private final Group hanging = new Group();
    private final Translate lifted = new Translate();

    private final Slider speed = new Slider(0, 250, 90);
    private final Slider slow = new Slider(0.05, 1, 1);
    private final Slider at = new Slider(0, 1, 0);
    private final Slider[] shape = new Slider[6];
    private final ChoiceBox<Outfit> outfits = new ChoiceBox<>();
    private final ChoiceBox<Person> people = new ChoiceBox<>();
    private ColorPicker skinPicker;
    private final TreeView<Object> catalogue = new TreeView<>();
    private final ListView<Wear> worn = new ListView<>();
    private final VBox settings = new VBox(6);
    /** While what is worn is shown afresh: its choosing heard of not. */
    private boolean settingWorn;
    private final Button play = new Button("Pause");
    private boolean playing = true;
    private boolean setting;
    private long last = -1;
    private double fps;
    private int frames;

    @Override
    public void start(final Stage stage) {
        final String who = System.getProperty("wardrobe.person");
        if (who != null) {
            People.all().stream().filter(p -> p.name().equalsIgnoreCase(who)).findFirst().ifPresent(p -> person = p);
        }
        final String asked = System.getProperty("wardrobe.outfit");
        if (asked != null) {
            Outfit.ALL.stream().filter(o -> o.name().equalsIgnoreCase(asked)).findFirst()
                    .ifPresent(o -> person = person.with(o));
        }
        speed.setValue(Double.parseDouble(System.getProperty("wardrobe.speed", "90")));
        for (final Slider s : List.of(speed, slow, at)) {
            s.setPrefWidth(200);
        }
        at.valueProperty().addListener((o, was, is) -> {
            if (!setting) {
                playing(false);
                still();
            }
        });
        play.setOnAction(e -> playing(!playing));
        final FlowPane paces = new FlowPane(6, 6);
        for (final Object[] p : new Object[][] {{"Stand", 0.0}, {"Stroll", 90.0}, {"Walk", 140.0}, {"Hurry", 220.0}}) {
            final Button b = new Button((String) p[0]);
            b.setOnAction(e -> speed.setValue((Double) p[1]));
            paces.getChildren().add(b);
        }
        final GridPane motion = new GridPane();
        motion.setHgap(8);
        motion.setVgap(6);
        int row = 0;
        for (final Object[] k : new Object[][] {{"Speed, cm/s", speed}, {"Time, share of real", slow},
            {"Point of the stride", at}}) {
            motion.add(new Label((String) k[0]), 0, row);
            motion.add((Slider) k[1], 1, row++);
        }

        // the body: the same numbers the body bench has, applied as a slider is let go
        final GridPane body = new GridPane();
        body.setHgap(8);
        body.setVgap(4);
        final String[] names = {"Stature, cm", "Build", "Hip joints up", "Arms long", "Shoulders apart", "Hips apart"};
        final double[][] ranges = {{140, 210}, {0.7, 1.6}, {0.45, 0.62}, {0.36, 0.52}, {0.17, 0.27}, {0.07, 0.14}};
        final Proportions p0 = person.body();
        final double[] now = {p0.stature(), p0.build(), p0.legs(), p0.arms(), p0.shoulders(), p0.hips()};
        for (int k = 0; k < 6; k++) {
            shape[k] = new Slider(ranges[k][0], ranges[k][1], now[k]);
            shape[k].setPrefWidth(180);
            final Label value = new Label(String.format(Locale.ROOT, "%.2f", now[k]));
            final Slider s = shape[k];
            s.valueProperty().addListener((o, was, is) -> value.setText(String.format(Locale.ROOT, "%.2f", is)));
            s.setOnMouseReleased(e -> reshape());
            body.add(new Label(names[k]), 0, k);
            body.add(s, 1, k);
            body.add(value, 2, k);
        }
        final ColorPicker skin = new ColorPicker(Color.web(person.skin()));
        skinPicker = skin;
        skin.setOnAction(e -> {
            person = person.withSkin(hex(skin.getValue()));
            dress();
        });
        body.add(new Label("Skin"), 0, 6);
        body.add(skin, 1, 6);
        final Button papers = new Button("The papers' body");
        papers.setOnAction(e -> {
            final Proportions p = Proportions.PAPERS;
            final double[] v = {p.stature(), p.build(), p.legs(), p.arms(), p.shoulders(), p.hips()};
            for (int k = 0; k < 6; k++) {
                shape[k].setValue(v[k]);
            }
            reshape();
        });

        outfits.getItems().setAll(Outfit.ALL);
        outfits.setValue(Outfit.ALL.stream().filter(o -> o.name().equals(person.outfit().name())).findFirst()
                .orElse(Outfit.BARE));
        outfits.valueProperty().addListener((o, was, is) -> {
            person = person.with(is);
            showWorn(-1);
            dress();
        });

        // anyone of the world as it is: their body, their skin, what they wear
        people.getItems().setAll(People.all());
        people.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(final Person p) {
                return p == null ? "" : p.name();
            }

            @Override
            public Person fromString(final String s) {
                return null;
            }
        });
        people.valueProperty().addListener((o, was, is) -> {
            if (is == null) {
                return;
            }
            person = is;
            final Proportions p = is.body();
            final double[] v = {p.stature(), p.build(), p.legs(), p.arms(), p.shoulders(), p.hips()};
            for (int k = 0; k < 6; k++) {
                shape[k].setValue(v[k]);
            }
            skinPicker.setValue(Color.web(is.skin()));
            showWorn(-1);
            reshape();
        });

        // the catalogue, its kinds as a tree; what is worn, inner first; the settings of the one chosen
        catalogue.setRoot(catalogue(Wear.class));
        catalogue.setPrefHeight(230);
        catalogue.setCellFactory(t -> new TreeCell<>() {
            @Override
            protected void updateItem(final Object item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : said(item));
            }
        });
        catalogue.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                putOn();
            }
        });
        final Button on = new Button("Put on");
        on.setOnAction(e -> putOn());
        worn.setPrefHeight(150);
        worn.setCellFactory(l -> new ListCell<>() {
            @Override
            protected void updateItem(final Wear item, final boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (getIndex() + 1) + "  " + item.name() + "  ("
                        + said(item.getClass().getInterfaces()[0]).toLowerCase(Locale.ROOT) + ")");
            }
        });
        worn.getSelectionModel().selectedIndexProperty().addListener((o, was, is) -> {
            if (!settingWorn) {
                showSettings();
            }
        });
        final Button off = new Button("Take off");
        off.setOnAction(e -> change(worn.getSelectionModel().getSelectedIndex(), 0, true));
        final Button inward = new Button("Inward");
        inward.setOnAction(e -> change(worn.getSelectionModel().getSelectedIndex(), -1, false));
        final Button outward = new Button("Outward");
        outward.setOnAction(e -> change(worn.getSelectionModel().getSelectedIndex(), 1, false));

        final Label hint = new Label("Any piece on any body, any number of them: each is made to the body it is put"
                + " on, over what is under it. Drag to turn the view, right-drag to move it, scroll to come nearer.");
        hint.setWrapText(true);
        final VBox side = new VBox(10, new Label("How fast"), paces, motion, new FlowPane(10, 6, play),
                new Label("Someone of the world"), people, new Label("The body"), body, papers,
                new Label("Wearing, to start from"), outfits,
                new Label("What may be worn"), catalogue, on, new Label("Worn, from the skin out"), worn,
                new FlowPane(6, 6, off, inward, outward), settings, hint);
        side.setPadding(new Insets(8));
        final ScrollPane scroll = new ScrollPane(side);
        scroll.setFitToWidth(true);
        shell.bar(List.of());
        shell.side(new Label("The wardrobe"), scroll, 440);
        shell.middle(view.node());
        view.grid(0, 0, 0, 1_200, 10, 5);
        view.zoom(60, 2_500);
        view.lookAt(0, Double.parseDouble(System.getProperty("wardrobe.look", "95")), 0);
        view.from("wardrobe", Double.parseDouble(System.getProperty("wardrobe.yaw", "25")),
                Double.parseDouble(System.getProperty("wardrobe.pitch", "6")),
                Double.parseDouble(System.getProperty("wardrobe.distance", "3.5")));
        shown.getTransforms().add(lifted);
        view.show(ModelView.Model.moving(shown));
        showWorn(-1);
        reshape();
        final String stopped = System.getProperty("wardrobe.at");
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

    /** Its body made afresh as the sliders say; dressed afresh. */
    private void reshape() {
        try {
            person = person.with(new Proportions(shape[0].getValue(), shape[1].getValue(), shape[2].getValue(),
                    shape[3].getValue(), shape[4].getValue(), shape[5].getValue()));
        } catch (final IllegalArgumentException e) {
            bus.publish(new Bus.Problems(e.getMessage()));
            return;
        }
        walker = new Walker(person.body());
        dress();
    }

    /** Dressed afresh as it is. */
    private void dress() {
        dressed = new Dressed(person);
        puppet = new Puppet(dressed::drawn, person.body(), new V3(-1, -1, -1));
        shown.getChildren().setAll(puppet.node(), hanging);
        draw();
    }

    private void draw() {
        if (walker == null) {
            return;
        }
        final Rig rig = walker.rig();
        puppet.show(rig);
        hanging.getChildren().setAll(puppet.drawnLayers(dressed.hanging(rig)));
        lifted.setY(-(walker.flying(speed.getValue()) + SOLE));
    }

    private void playing(final boolean on) {
        playing = on;
        play.setText(on ? "Pause" : "Play");
        last = -1;
    }

    private void still() {
        walker.at(at.getValue(), speed.getValue(), false);
        draw();
    }

    private void frame(final long t) {
        if (last >= 0 && playing && walker != null) {
            final double real = Math.min(0.05, (t - last) / 1e9);
            fps = fps == 0 ? 1 / Math.max(real, 1e-3) : fps * 0.95 + 0.05 / Math.max(real, 1e-3);
            walker.step(real * slow.getValue(), speed.getValue(), false);
            draw();
            setting = true;
            at.setValue(walker.cycle());
            setting = false;
            view.groundAt(0, walker.ahead() % REPEATS);
        }
        last = t;
        if (++frames % 15 == 0) {
            bus.publish(new Bus.Figures(String.format(Locale.ROOT, "%s, wearing %s%n%s%n%.2f m/s%n%.0f frames a second",
                    person.name(), person.outfit().wear().isEmpty() ? "nothing"
                            : String.join(", ", person.outfit().wear().stream().map(Wear::name).toList())
                                    .toLowerCase(Locale.ROOT),
                    person.body().said(), speed.getValue() / 100, fps)));
        }
    }

    /**
     * The catalogue of what may be worn, as its kinds are families of kinds: each family a branch, each kind a leaf; read
     * from the kinds themselves, so a kind declared is a leaf of it.
     */
    private static TreeItem<Object> catalogue(final Class<?> kind) {
        final TreeItem<Object> item = new TreeItem<>(kind);
        if (kind.isSealed()) {
            for (final Class<?> sub : kind.getPermittedSubclasses()) {
                item.getChildren().add(catalogue(sub));
            }
            item.setExpanded(true);
        }
        return item;
    }

    /** A kind's name as words: {@code Legwear}, {@code Robe}; {@code Wearables} for them all. */
    private static String said(final Object kind) {
        if (kind == Wear.class) {
            return "Wearables";
        }
        return kind instanceof Class<?> c ? c.getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2") : String.valueOf(kind);
    }

    /** The kind chosen in the catalogue, if a kind and not a family, put on over the rest. */
    @SuppressWarnings("unchecked")
    private void putOn() {
        final TreeItem<Object> chosen = catalogue.getSelectionModel().getSelectedItem();
        if (chosen == null || !(chosen.getValue() instanceof Class<?> c) || !c.isRecord()) {
            return;
        }
        person = person.with(person.outfit().with(Wear.first((Class<? extends Wear>) c)));
        showWorn(person.outfit().wear().size() - 1);
        dress();
    }

    /** What is worn, inner first, the one at {@code chosen} chosen and its settings shown. */
    private void showWorn(final int chosen) {
        settingWorn = true;
        worn.getItems().setAll(person.outfit().wear());
        settingWorn = false;
        if (chosen >= 0 && chosen < worn.getItems().size()) {
            worn.getSelectionModel().select(chosen);
        }
        showSettings();
    }

    /** The settings of what is chosen of what is worn. */
    private void showSettings() {
        settings.getChildren().clear();
        final int i = worn.getSelectionModel().getSelectedIndex();
        if (i >= 0) {
            settings.getChildren().addAll(new Label(worn.getItems().get(i).name() + ", worn " + (i + 1) + " of "
                    + worn.getItems().size() + " from the skin"), knobs(i, worn.getItems().get(i)));
        }
    }

    /** The one worn at {@code i} taken off; or moved {@code by} places outward, or inward when less than nothing. */
    private void change(final int i, final int by, final boolean off) {
        if (i < 0) {
            return;
        }
        person = person.with(off ? person.outfit().without(i) : person.outfit().moved(i, by));
        showWorn(off ? Math.min(i, person.outfit().wear().size() - 1) : Math.clamp(i + by, 0,
                person.outfit().wear().size() - 1));
        dress();
    }

    /** A slider, tick or picker for every number, yes-or-no and colour of {@code piece}, worn at {@code at}. */
    private Node knobs(final int at, final Wear piece) {
        final GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(4);
        grid.setPadding(new Insets(0, 0, 0, 20));
        int row = 0;
        for (final RecordComponent c : piece.getClass().getRecordComponents()) {
            final Object now = get(piece, c);
            final String label = label(piece, c);
            if (c.getType() == String.class) {
                final ColorPicker picker = new ColorPicker(Color.web((String) now));
                picker.setOnAction(e -> set(at, piece, c, hex(picker.getValue())));
                grid.add(new Label(label), 0, row);
                grid.add(picker, 1, row++);
            } else if (c.getType() == boolean.class) {
                final CheckBox tick = new CheckBox(label);
                tick.setSelected((Boolean) now);
                tick.selectedProperty().addListener((o, was, is) -> set(at, piece, c, is));
                grid.add(tick, 0, row++, 2, 1);
            } else {
                final Range r = RANGES.getOrDefault(piece.getClass().getSimpleName() + "." + c.getName(),
                        new Range(label, 0, 2 * ((Number) now).doubleValue() + 1));
                final Slider s = new Slider(r.min(), r.max(), ((Number) now).doubleValue());
                s.setPrefWidth(180);
                s.setOnMouseReleased(e -> set(at, piece, c, s.getValue()));
                grid.add(new Label(r.label()), 0, row);
                grid.add(s, 1, row++);
            }
        }
        return grid;
    }

    private static String label(final Wear piece, final RecordComponent c) {
        final Range r = RANGES.get(piece.getClass().getSimpleName() + "." + c.getName());
        if (r != null) {
            return r.label();
        }
        return switch (c.getName()) {
            case "colour" -> "Colour";
            case "trim" -> "Collar and cuffs";
            case "wideSleeves" -> "Wide sleeves";
            case "waistband" -> "Waistband";
            case "sole" -> "Soles";
            case "card" -> "Card";
            case "cord" -> "Cord";
            case "bib" -> "With a bib";
            case "longSleeves" -> "Long sleeves";
            default -> c.getName();
        };
    }

    private static Object get(final Object r, final RecordComponent c) {
        try {
            return c.getAccessor().invoke(r);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /** {@code piece}, worn at {@code at}, with {@code c} set to {@code v}, all else as it was, worn in its place. */
    private void set(final int at, final Wear piece, final RecordComponent c, final Object v) {
        final RecordComponent[] all = piece.getClass().getRecordComponents();
        final Object[] args = new Object[all.length];
        final Class<?>[] types = new Class<?>[all.length];
        for (int k = 0; k < all.length; k++) {
            args[k] = all[k].getName().equals(c.getName()) ? v : get(piece, all[k]);
            types[k] = all[k].getType();
        }
        try {
            final Wear next = (Wear) piece.getClass().getDeclaredConstructor(types).newInstance(args);
            person = person.with(person.outfit().with(at, next));
        } catch (final ReflectiveOperationException e) {
            return;
        }
        showWorn(at);
        dress();
    }

    private static String hex(final Color c) {
        return String.format(Locale.ROOT, "#%02x%02x%02x", Math.round(c.getRed() * 255), Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255));
    }
}
