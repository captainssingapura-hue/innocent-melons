package melons.innocent.demos.village;

import com.example.fxsuite.texture.lab.CarBody;
import com.example.fxsuite.texture.lab.CarFaces;
import com.example.fxsuite.texture.lab.CarWheels;
import com.example.fxsuite.texture.lab.Sheeted;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * A car park on the card: its sheet of concrete, and the cars standing on it. Every car is the one body, the one
 * inside, the one set of windows and the one set of wheels, each sent to the card once and drawn once for each car.
 * There are five looks for them all: the two paints, each car told its own colour as it is drawn; rubber, for the
 * inside and the tyres; steel, for the wheels; and glass, for the windows. So a hundred cars are no more to build
 * than one.
 *
 * <p>The windows are seen through, so they are drawn apart from the rest and after it ({@link #seenThrough}), the
 * furthest car's first. Closed, all of it is gone from the card.
 */
public final class CarParkDrawn implements AutoCloseable {

    private final CarPark park;
    private final List<Parked> cars;
    private final Formed sheet;
    private final Looked gloss;
    private final Looked metallic;
    private final Looked inside;
    private final Looked glass;
    private final Shaped body;
    private final Shaped lining;
    private final Shaped windows;
    private final Looked steel;
    private final Shaped tyres;
    private final Shaped discs;
    /** What undoes each thing made here, in the order it was made. */
    private final List<Runnable> closing = new ArrayList<>();

    /** {@code park} and {@code elsewhere}, cars parked elsewhere, on {@code card}, laid out as {@code laid} says. */
    public CarParkDrawn(final Card card, final Laid laid, final CarPark park, final List<Parked> elsewhere) {
        this.park = park;
        this.cars = Stream.concat(park.parked().stream(), elsewhere.stream()).toList();
        final CarFaces faces = new CarFaces(new CarBody().skeleton());
        try {
            this.sheet = new Formed(card, new Look(laid, park.paved()), park.sheet());
            closing.add(sheet::close);
            this.gloss = looked(card, laid, new Materials.GlossPaint());
            this.metallic = looked(card, laid, new Materials.MetallicPaint());
            this.inside = looked(card, laid, new Materials.Rubber());
            this.glass = looked(card, laid, new Materials.Glass());
            this.body = shaped(card, laid, new Sheeted(faces.body()));
            this.windows = shaped(card, laid, new Sheeted(faces.glass()));
            final CarWheels wheels = new CarWheels(new CarBody());
            this.lining = new Shaped(card, laid, new Sheeted(faces.body()).lining(
                    Stream.concat(faces.inside(wheels.within()).stream(), wheels.wells().stream()).toList()));
            closing.add(lining::close);
            this.steel = looked(card, laid, new Materials.Steel());
            this.tyres = shaped(card, laid, new Sheeted(wheels.tyres()));
            this.discs = shaped(card, laid, new Sheeted(wheels.discs()));
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    private Looked looked(final Card card, final Laid laid, final Material material) {
        final Looked look = new Looked(card, new Look(laid, new Shades().of(material)));
        closing.add(look::close);
        return look;
    }

    private Shaped shaped(final Card card, final Laid laid, final Sheeted sheets) {
        final Shaped shaped = new Shaped(card, laid, sheets.formed());
        closing.add(shaped::close);
        return shaped;
    }

    /** The concrete, and every car's body, inside and wheels, drawn by {@code pass} as {@code camera} sees them. */
    public void drawn(final Pass pass, final Camera camera) {
        sheet.drawn(pass, new Mat4(park.sheetStands()), camera, 1);
        for (final Parked car : cars) {
            final Mat4 placed = car.placed();
            (car.livery().metallic() ? metallic : gloss).drawn(pass, body, placed, camera, 1, car.livery().colour());
            inside.drawn(pass, lining, placed, camera, 1);
            inside.drawn(pass, tyres, placed, camera, 1);
            steel.drawn(pass, discs, placed, camera, 1);
        }
    }

    /** Every car's windows, drawn by {@code pass} as {@code camera} sees them: the furthest car's first. */
    public void seenThrough(final Pass pass, final Camera camera) {
        final List<Parked> furthestFirst = new ArrayList<>(cars);
        furthestFirst.sort(Comparator.comparingDouble(
                (Parked car) -> car.stands().minus(camera.eye()).length()).reversed());
        for (final Parked car : furthestFirst) {
            glass.drawn(pass, windows, car.placed(), camera, 1);
        }
    }

    @Override
    public void close() {
        closing.reversed().forEach(Runnable::run);
        closing.clear();
    }
}
