package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.avatar.bench.kit.Shell;
import com.example.fxsuite.avatar.bench.kit.Workshop;
import com.example.fxsuite.taxonomy.Taxonomy;
import com.example.fxsuite.texture.bench.Swatch;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.LightBoard;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.worked.Picture;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The bench's JavaFX window, beside the card's: laid out by the engine's bench kit as every bench's is. On its
 * left, the light: shadows, the sky by hand or by the almanac and the clock. In its middle: which material is on
 * show, chosen from the tree; which shapes it is looked at on, each ticked or not; what colour they are, for paint;
 * how many times as far off it is to seem; Java's own working of the wall's face in that material, made away from
 * the screen by the kit's workshop, to hold the card's picture against; and what the material is, in words.
 *
 * <p>It draws nothing of the shapes itself: that is the card's window. It says what is on show as a message
 * on the bench's bus ({@link OnShow}) and never calls what draws; and what is drawn says what it has to say back
 * the same way.
 */
public final class Console {

    private static final double WIDE = 660;
    private static final double TALL = 860;
    private static final double LIGHT_WIDE = 350;
    private static final double GAP = 8;
    /** How many times the distance can be doubled, at the slider's far end. */
    private static final int DOUBLINGS = 6;
    /** Java's picture of the wall's face: so many pixels, of a face so big, seen from so far, in centimetres. */
    private static final int PICTURE_WIDE = 270;
    private static final int PICTURE_TALL = 216;
    private static final double FACE_LONG = 300;
    private static final double FACE_HIGH = 240;
    private static final double FROM = 420;

    private final Bus bus;
    private final Asked asked;
    private final OnShow first;
    private final LightBoard light;
    /** What makes Java's picture, once the window is: nothing of JavaFX's is made before JavaFX has begun. */
    private final AtomicReference<Workshop<OnShow, Worked>> workshop = new AtomicReference<>();

    /** Java's own working of the wall's face in the material on show. */
    public record Worked(Picture picture) {
    }

    /** A console on {@code bus}, its light asking {@code asked}, with {@code first} on show to begin with. */
    public Console(final Bus bus, final Asked asked, final OnShow first) {
        this.bus = bus;
        this.asked = asked;
        this.first = first;
        this.light = new LightBoard(asked);
    }

    /** Shown, its top left corner {@code x} across the screen and {@code y} down. To be called on JavaFX's thread. */
    public void shown(final double x, final double y) {
        final Taxonomy tree = new Materials().tree();
        final ComboBox<String> material = new ComboBox<>();
        tree.leaves().forEach(leaf -> material.getItems().add(leaf.title()));
        material.setValue(first.material().title());
        final Label far = new Label(further(first.further()));
        final Slider doubled = new Slider(0, DOUBLINGS, Math.log(first.further()) / Math.log(2));
        // the shapes it stands on: the list that is ticked and unticked here, said afresh each time it changes
        final Map<Shape, CheckBox> ticked = ticks();
        final FlowPane shapes = new FlowPane(2 * GAP, GAP / 2);
        shapes.getChildren().addAll(ticked.values());
        // the colour of the thing itself, which paint takes and nothing else on the bench does
        final ColorPicker painted = new ColorPicker(
                Color.color(first.tint().x(), first.tint().y(), first.tint().z()));
        final Runnable changed = () -> {
            final Color colour = painted.getValue();
            final OnShow now = new OnShow(new Called(material.getValue()).material(),
                    Math.pow(2, doubled.getValue()),
                    Arrays.stream(Shape.values()).filter(shape -> ticked.get(shape).isSelected()).toList(),
                    new Vec3(colour.getRed(), colour.getGreen(), colour.getBlue()));
            far.setText(further(now.further()));
            bus.publish(now);
        };
        // a material chosen brings the shapes that suit it and the colour it begins in; either can then be changed
        material.setOnAction(chosen -> {
            final Suited suited = new Suited(new Called(material.getValue()).material());
            ticked.forEach((shape, box) -> box.setSelected(suited.shapes().contains(shape)));
            painted.setValue(Color.color(suited.colour().x(), suited.colour().y(), suited.colour().z()));
            changed.run();
        });
        doubled.valueProperty().addListener((moved, was, set) -> changed.run());
        ticked.values().forEach(box -> box.setOnAction(set -> changed.run()));
        painted.setOnAction(set -> changed.run());
        final ImageView worked = new ImageView();
        bus.subscribe(Worked.class, made -> worked.setImage(image(made.picture())), Bus.SCREEN);
        workshop.set(swatching());
        final Shell shell = new Shell("The material bench", bus);
        final VBox lit = new VBox(GAP, new Label("The light"), light.shown());
        lit.setPadding(new Insets(GAP));
        lit.setPrefWidth(LIGHT_WIDE);
        final VBox middle = new VBox(GAP, new Label("The material on show"), material,
                new Label("The shapes it is looked at on"), shapes,
                new Label("Their colour, where the material takes it"), painted, far, doubled,
                new Label("Java's own working of the wall's face:"), worked, shell.working(), shell.figures(),
                shell.problems());
        middle.setPadding(new Insets(GAP));
        final Stage stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        stage.setOnCloseRequest(closing -> asked.stop());
        shell.barless().left(new ScrollPane(lit)).middle(middle).show(stage, WIDE, TALL);
        bus.publish(first);
    }

    /** A tick box for each shape the bench has, in their order: ticked where the shape is on show to begin with. */
    private Map<Shape, CheckBox> ticks() {
        final Map<Shape, CheckBox> ticked = new EnumMap<>(Shape.class);
        for (final Shape shape : Shape.values()) {
            final CheckBox box = new CheckBox(shape.title());
            box.setSelected(first.shapes().contains(shape));
            ticked.put(shape, box);
        }
        return ticked;
    }

    /**
     * What makes Java's picture of the wall's face, away from the screen: made again only when the material, its
     * colour or how far off it seems has changed, not when the shapes on show have.
     */
    private Workshop<OnShow, Worked> swatching() {
        final Shades shades = new Shades();
        final AtomicReference<Workshop<OnShow, Worked>.Stage<Picture>> face = new AtomicReference<>();
        final Workshop<OnShow, Worked> made = new Workshop<>("Java's working", bus, OnShow.class,
                shown -> new Worked(face.get().of(shown)));
        face.set(made.stage("the wall's face", shown -> List.of(shown.material(), shown.further(), shown.tint()),
                shown -> new Swatch(shades.of(shown.material()), FACE_LONG, FACE_HIGH).seen(FROM, shown.further(),
                        PICTURE_WIDE, PICTURE_TALL, shown.tint())));
        return made;
    }

    /** How far off the material seems, said in a line. */
    private String further(final double times) {
        return times < 1.05 ? "Seen as it is" : String.format(Locale.ENGLISH, "Seen as from %.1f times as far", times);
    }

    /** {@code picture} as JavaFX shows one. */
    private WritableImage image(final Picture picture) {
        final WritableImage image = new WritableImage(picture.width(), picture.height());
        for (int down = 0; down < picture.height(); down++) {
            for (int across = 0; across < picture.width(); across++) {
                image.getPixelWriter().setArgb(across, down, picture.at(across, down));
            }
        }
        return image;
    }

    /** Told the moment the sky's clock has come to and where the sun and the moon stand. From any thread. */
    public void sky(final String said) {
        light.sky(said);
    }

    /** Its workshop's worker stopped. From any thread. */
    public void close() {
        Optional.ofNullable(workshop.get()).ifPresent(Workshop::close);
    }
}
