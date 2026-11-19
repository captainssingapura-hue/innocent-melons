package com.example.fxsuite.avatar.bench.kit;

import javafx.geometry.Point3D;
import javafx.scene.input.PickResult;

/**
 * A bench's hold on what its view shows: pressed on something it shows, with the left button, it may take hold of it
 * ({@link #take}), and while it holds it, dragging moves it ({@link #move}), not the view; let go, the view turns
 * again as dragged. Pressed on nothing it takes, or with the right button or shift, the view moves as ever.
 */
public interface Grip {

    /**
     * The line from the eye through the mouse, in the view's frame (JavaFX's: y down): where it starts, and which way
     * it goes, a unit long.
     */
    record Ray(Point3D from, Point3D way) {

        /** Where it crosses the plane through {@code on} square to {@code normal}; none if it runs along it. */
        public Point3D across(final Point3D on, final Point3D normal) {
            final double d = way.dotProduct(normal);
            if (Math.abs(d) < 1e-9) {
                return null;
            }
            final double t = on.subtract(from).dotProduct(normal) / d;
            return from.add(way.multiply(t));
        }
    }

    /** Pressed on {@code picked} along {@code ray}: whether it takes hold. */
    boolean take(PickResult picked, Ray ray, boolean control);

    /** Dragged, holding it, the mouse now along {@code ray}. */
    void move(Ray ray, boolean control);

    /** Let go. */
    void letGo();
}
