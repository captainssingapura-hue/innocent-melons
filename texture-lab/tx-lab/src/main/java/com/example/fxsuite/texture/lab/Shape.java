package com.example.fxsuite.texture.lab;

import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Dome;
import com.example.fxsuite.vulkan.form.Ellipsoid;
import com.example.fxsuite.vulkan.form.Mesh;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * The plain shapes a material can be looked at on, on the bench. Which of them stand on it is chosen in the bench's
 * window, any number at a time, and only those are drawn. Each is here for what it shows of a material:
 *
 * <ul>
 *   <li>the wall, three metres long and 2.4 m high, for what is laid or lies flat;</li>
 *   <li>the ball, a metre across, for how a material takes the light as it turns away;</li>
 *   <li>the block, a metre each way, for its edges and its faces square to one another;</li>
 *   <li>the curved sheet, a low dome 1.8 m long, for a sheet that bends both ways at once as a car's bonnet does,
 *       and is seen at every angle from square on to glancing;</li>
 *   <li>the pane, a thin upright sheet 1.6 m long, for what is flat and thin, as glass is;</li>
 *   <li>the car body, a plain saloon 4.4 m long with no wheels ({@link CarBody}), for paint on a body's sheets:
 *       its windows are of glass whatever material is on show, and through them its inside is seen, dark.</li>
 * </ul>
 *
 * <p>Where on its surface a point of a shape is, is where it is in the shape's own frame: so a pattern lies true on
 * the wall's face and on the block's front, and runs as it will over the others.
 */
public enum Shape {
    WALL("Wall", 300, 120, () -> new Cuboid(300, 240, 24).formed()),
    BALL("Ball", 100, 50, () -> new Ellipsoid(100, 100, 100).formed(48, 24)),
    BLOCK("Block", 100, 50, () -> new Cuboid(100, 100, 100).formed()),
    CURVED("Curved sheet", 180, 0, () -> new Dome(180, 90, 130).formed(64, 24)),
    PANE("Pane", 160, 55, () -> new Cuboid(160, 110, 1.2).formed()),
    CAR("Car body", 440, 0, () -> new Sheeted(new CarFaces(new CarBody().skeleton()).body()).formed(),
            () -> Optional.of(new Sheeted(new CarFaces(new CarBody().skeleton()).glass()).formed()),
            () -> Optional.of(new Sheeted(new CarFaces(new CarBody().skeleton()).body())
                    .lining(List.of(new CarFaces(new CarBody().skeleton()).floor()))));

    private final String title;
    private final double across;
    private final double lifted;
    private final Supplier<Mesh> forming;
    private final Supplier<Optional<Mesh>> glazing;
    private final Supplier<Optional<Mesh>> lining;

    Shape(final String title, final double across, final double lifted, final Supplier<Mesh> forming) {
        this(title, across, lifted, forming, Optional::empty, Optional::empty);
    }

    Shape(final String title, final double across, final double lifted, final Supplier<Mesh> forming,
          final Supplier<Optional<Mesh>> glazing, final Supplier<Optional<Mesh>> lining) {
        this.title = title;
        this.across = across;
        this.lifted = lifted;
        this.forming = forming;
        this.glazing = glazing;
        this.lining = lining;
    }

    /** What it is called in the bench's window. */
    public String title() {
        return title;
    }

    /** How much of the row it takes up, from side to side, in centimetres. */
    public double across() {
        return across;
    }

    /** How high the middle of its own frame is when it stands on the ground. */
    public double lifted() {
        return lifted;
    }

    /** Its form, in its own frame: what is of the material on show. */
    public Mesh formed() {
        return forming.get();
    }

    /** Its windows, in the same frame, where it has any: they are of glass whatever material is on show. */
    public Optional<Mesh> glazed() {
        return glazing.get();
    }

    /**
     * Its inside, in the same frame, where it is hollow and can be looked into: what is seen through its windows.
     * It is dark, whatever material is on show.
     */
    public Optional<Mesh> lined() {
        return lining.get();
    }
}
