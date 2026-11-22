package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.build.Sheet;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The car body the bench shows paint and glass on. Its skeleton: nothing wrong with it as a graph; as long, as
 * wide and as high as the owner said, on the wheelbase the owner said; the same on its left as on its right; the
 * body one piece, each of its six windows a piece apart, and its two axles. Its faces: eleven sheets of metal
 * and six of glass, each named. Body and glass together close it in: no crack between two sheets and no hole a
 * window does not fill, its only open edge the one round its sills and over its wheels. Every window faces out
 * of the car. Its inside is its sheets again facing in, and a floor. And it stands on four wheels, one at each
 * end of each axle, within its width, each with its steel disc facing out.
 */
class CarBodyTest {

    private final Form skeleton = new CarBody().skeleton();
    private final CarFaces faces = new CarFaces(skeleton);

    @Test
    void theSkeletonIsASoundGraphAsLongAsItSaysTheSameBothSidesAndItsWindowsStandApart() {
        assertEquals(List.of(), skeleton.problems());
        final Form.Bounds bounds = skeleton.bounds();
        assertEquals(454.5, bounds.depth(), 1e-6, "from its tail to its nose: 4,545 mm");
        assertEquals(169.5, bounds.width(), 1e-6, "across its flanks at their fullest: 1,695 mm");
        assertEquals(-bounds.leftmost(), bounds.rightmost(), 1e-9, "as wide to its left as to its right");
        assertEquals(20, bounds.low(), 1e-9, "its sills are clear of the ground it stands on");
        assertEquals(255, skeleton.at(skeleton.vertex("front axle, left")).y()
                - skeleton.at(skeleton.vertex("rear axle, left")).y(), 1e-9, "its wheelbase: 2,550 mm");
        assertEquals(9, skeleton.pieces(), "the body, six windows standing apart in it, and two axles");
        // the roof's sheet is crowned above the edges round it: over all, the car is 1,400 mm high and no more
        final Mesh body = Shape.CAR.formed();
        final double top = body.points().stream().mapToDouble(Vec3::y).max().orElseThrow();
        assertEquals(140, top, 0.3, "from the ground to the top of its roof");
        final double widest = body.points().stream().mapToDouble(point -> Math.abs(point.z())).max().orElseThrow();
        assertEquals(169.5, 2 * widest, 0.05, "as wide as its skeleton says");
        for (final Form.Vertex vertex : skeleton.vertices()) {
            if (vertex.name().endsWith(", left")) {
                final Form.Vertex other = skeleton.vertices().get(
                        skeleton.vertex(vertex.name().replace(", left", ", right")));
                assertEquals(-vertex.across(), other.across(), 1e-9, vertex.name());
                assertEquals(vertex.up(), other.up(), 1e-9, vertex.name());
            }
        }
    }

    @Test
    void itsFacesAreElevenSheetsOfMetalAndSixOfGlassEachNamed() {
        final List<Sheet> body = faces.body();
        final List<Sheet> glass = faces.glass();
        assertEquals(11, body.size());
        assertEquals(6, glass.size());
        assertTrue(body.stream().allMatch(sheet -> sheet.kind().equals("body") && sheet.area() > 1000));
        assertTrue(glass.stream().allMatch(sheet -> sheet.kind().equals("glass") && sheet.area() > 1000));
        final List<String> names = new ArrayList<>();
        body.forEach(sheet -> names.add(sheet.name()));
        glass.forEach(sheet -> names.add(sheet.name()));
        assertEquals(17, names.stream().distinct().count(), "no two of one name: " + names);
        assertTrue(names.containsAll(List.of("bonnet", "roof", "boot lid", "flank, left", "windscreen frame",
                "windscreen", "quarter window, right")), names.toString());
    }

    @Test
    void bodyAndGlassTogetherCloseItInWithNoCrackAndItsOnlyOpenEdgeIsRoundItsSillsAndOverItsWheels() {
        final List<Sheet> all = new ArrayList<>(faces.body());
        all.addAll(faces.glass());
        final Mesh whole = new Sheeted(all).formed();
        final Map<String, Integer> sides = new HashMap<>();
        final Map<String, Double> highest = new HashMap<>();
        for (int triangle = 0; triangle < whole.triangles(); triangle++) {
            for (int corner = 0; corner < 3; corner++) {
                final Vec3 one = whole.corner(triangle, corner);
                final Vec3 other = whole.corner(triangle, (corner + 1) % 3);
                final String edge = key(one).compareTo(key(other)) < 0 ? key(one) + key(other) : key(other) + key(one);
                sides.merge(edge, 1, Integer::sum);
                highest.put(edge, Math.max(one.y(), other.y()));
            }
        }
        int open = 0;
        for (final Map.Entry<String, Integer> edge : sides.entrySet()) {
            if (edge.getValue() != 2) {
                assertEquals(1, edge.getValue(), "an edge has a triangle to each side of it, or one: " + edge.getKey());
                assertTrue(highest.get(edge.getKey()) < 65, "an open edge is along its sills or over a wheel: "
                        + edge.getKey());
                open++;
            }
        }
        assertTrue(open >= 2 * 60, "open all round its foot: " + open);
    }

    @Test
    void everyWindowFacesOutOfTheCar() {
        final Mesh glass = Shape.CAR.glazed().orElseThrow();
        final Vec3 inside = new Vec3(0, 40, 0);
        assertTrue(glass.triangles() >= 12);
        for (int triangle = 0; triangle < glass.triangles(); triangle++) {
            final Vec3 centre = glass.corner(triangle, 0).plus(glass.corner(triangle, 1))
                    .plus(glass.corner(triangle, 2)).times(1.0 / 3);
            assertTrue(glass.crossed(triangle).dot(centre.minus(inside)) > 0, "triangle " + triangle);
        }
        assertTrue(Shape.WALL.glazed().isEmpty(), "a wall has no windows");
    }

    @Test
    void itsInsideIsItsSheetsAgainFacingInAndAFloorJustOverItsSillsSoNoGroundIsSeenThroughItsWindows() {
        final Mesh inside = Shape.CAR.lined().orElseThrow();
        final Sheet floor = faces.floor();
        final int skin = Shape.CAR.formed().triangles() + floor.triangles().size();
        final CarWheels wheels = new CarWheels(new CarBody());
        final int plates = wheels.wells().stream().mapToInt(plate -> plate.triangles().size()).sum();
        final List<Sheet> within = faces.inside(wheels.within());
        final int wells = within.stream().mapToInt(sheet -> sheet.triangles().size()).sum() - floor.triangles().size();
        assertEquals(skin + wells + plates, inside.triangles(),
                "every sheet of the body again, the floor, a well over each wheel and a plate across each well");
        assertEquals(8 * 28, plates, "a plate each way behind each of four wheels");
        assertEquals(1 + 8, within.size(), "the floor, and a well with a face each way over each of four wheels");
        assertTrue(wells > 8 * 16, "each well follows its arch round: " + wells + " triangles");
        // a well runs in from the flank as far as its plate and no further, and no higher than the belt
        for (final Sheet well : within.subList(1, within.size())) {
            for (final com.example.fxsuite.avatar.sim.Vec3 point : well.points()) {
                assertTrue(Math.abs(point.x()) >= wheels.within() - 1e-9 && Math.abs(point.x()) < 85
                        && point.z() < 70, well.name() + " at " + point);
            }
        }
        final Vec3 middle = new Vec3(0, 40, 0);
        for (int triangle = 0; triangle < skin; triangle++) {
            final Vec3 centre = inside.corner(triangle, 0).plus(inside.corner(triangle, 1))
                    .plus(inside.corner(triangle, 2)).times(1.0 / 3);
            assertTrue(inside.crossed(triangle).dot(centre.minus(middle)) < 0, "faces in: triangle " + triangle);
        }
        assertTrue(floor.points().stream().allMatch(point -> Math.abs(point.z() - 20.5) < 1e-9),
                "a hair over its sills");
        assertTrue(floor.area() > 0.9 * 440 * 150 && floor.area() < 454.5 * 169.5, "as big as the car's foot: "
                + floor.area());
        assertTrue(Shape.BALL.lined().isEmpty(), "a ball is not looked into");
    }

    @Test
    void itStandsOnFourWheelsOneAtEachEndOfEachAxleWithinItsWidthEachWithItsSteelDiscFacingOut() {
        final CarWheels wheels = new CarWheels(new CarBody());
        assertEquals(12, wheels.tyres().size(), "four tyres, each a tread and two sides");
        assertEquals(4, wheels.discs().size());
        final Mesh tyres = Shape.CAR.fitted().get(new Materials.Rubber());
        final Mesh discs = Shape.CAR.fitted().get(new Materials.Steel());
        assertEquals(0, tyres.points().stream().mapToDouble(Vec3::y).min().orElseThrow(), 1e-9, "on the ground");
        assertEquals(60, tyres.points().stream().mapToDouble(Vec3::y).max().orElseThrow(), 1e-9, "60 cm across");
        final double widest = tyres.points().stream().mapToDouble(point -> Math.abs(point.z())).max().orElseThrow();
        assertTrue(widest < 169.5 / 2 && widest > 75, "within the car's width, near its flanks: " + widest);
        // along the car, the wheels' middles are its wheelbase apart: the tyres reach a wheel's size past each
        final double foremost = tyres.points().stream().mapToDouble(Vec3::x).min().orElseThrow();
        final double hindmost = tyres.points().stream().mapToDouble(Vec3::x).max().orElseThrow();
        assertEquals(255 + 60, hindmost - foremost, 1e-9);
        for (int triangle = 0; triangle < discs.triangles(); triangle++) {
            final Vec3 middle = discs.corner(triangle, 0);
            assertTrue(discs.crossed(triangle).z() * middle.z() > 0, "a disc faces out from the car's middle");
        }
        assertTrue(Shape.WALL.fitted().isEmpty(), "a wall is fitted with nothing");
    }

    /** A place, to a hundredth of a millimetre: what two sheets' points along one edge have alike. */
    private String key(final Vec3 at) {
        return String.format(Locale.ENGLISH, "(%.3f %.3f %.3f)", at.x() + 0.0, at.y() + 0.0, at.z() + 0.0);
    }
}
