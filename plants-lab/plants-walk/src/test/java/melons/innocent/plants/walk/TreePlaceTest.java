package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Shadows;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import melons.innocent.plants.tree.Kind;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The place where one tree of each kind stands. The engine's own avatar, running straight at the oak, is stopped by
 * its trunk. And each tree is drawn on the card, whole, from among its boughs, and at its foot close enough to see
 * its bark, with nothing said by the validation layer, its wood standing out against the sky.
 */
class TreePlaceTest {

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();

    @Test
    void theAvatarRunningStraightAtTheOakIsStoppedByItsTrunk() {
        try (Card card = new Card(); TreePlace place = new TreePlace(card, laid)) {
            assertEquals(List.of(Kind.OAK, Kind.WALNUT, Kind.BIRCH),
                    place.standing().stream().map(TreePlace.Standing::kind).toList());
            Avatar avatar = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0), false);
            for (int step = 0; step < 6 * 120; step++) {
                avatar = avatar.step(new Controls(1, 0, 0, 0, true), Clock.STEP, place.inTheWay()).avatar();
            }
            final double z = avatar.body().position().z();
            assertTrue(z > 900 && z < place.standing().getFirst().stands().z(), "it ran up to the trunk: " + z);
            assertEquals(List.of(), card.heard());
        }
    }

    @Test
    void eachTreeIsDrawnOnTheCardWholeFromAmongItsBoughsAndAtItsFootWithNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        try (Card card = new Card(); Canvas canvas = card.canvas(1000, 700);
             TreePlace place = new TreePlace(card, laid); Shadows shadows = new Shadows(card, laid, 2048)) {
            for (final TreePlace.Standing tree : place.standing()) {
                final String name = tree.kind().name().toLowerCase(Locale.ROOT);
                final double high = tree.tree().height();
                final Camera whole = new Orbit(tree.stands().plus(new Vec3(0, high * 0.5, 0)), 25, 4, high * 2.1)
                        .camera(48, 10);
                final Picture far = pictured(canvas, place, tree, whole, shadows);
                ImageIO.write(far.image(), "png", folder.resolve(name + "-whole.png").toFile());
                final Camera among = new Orbit(tree.stands().plus(new Vec3(120, high * 0.55, 60)), 70, -8, 420)
                        .camera(48, 10);
                ImageIO.write(pictured(canvas, place, tree, among, shadows).image(), "png",
                        folder.resolve(name + "-among-its-boughs.png").toFile());
                final Camera foot = new Orbit(tree.stands().plus(new Vec3(0, 110, 0)), 20, 4, 260).camera(48, 10);
                ImageIO.write(pictured(canvas, place, tree, foot, shadows).image(), "png",
                        folder.resolve(name + "-trunk.png").toFile());
                int wood = 0;
                for (int y = 0; y < far.height(); y++) {
                    for (int x = 0; x < far.width(); x++) {
                        wood += far.at(x, y) == 0xffc9dbe8 ? 0 : 1;
                    }
                }
                assertTrue(wood > 3_000, tree.kind() + " stands out against the sky: " + wood + " pixels");
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** The place as {@code camera} sees it by day, with its shadows about {@code tree}. */
    private Picture pictured(final Canvas canvas, final TreePlace place, final TreePlace.Standing tree,
                             final Camera camera, final Shadows shadows) {
        final Framed framed = new Framed(laid, camera, day.light());
        final Beam beam = new Beam(tree.stands(), day.light().sunTowards(), 5000, 5000).settled(2048);
        final Consumer<Pass> all = pass -> place.drawn(pass, camera);
        canvas.drawn(day.sky(), framed.filling(1000.0 / 700, beam, 2048), shadows.cast(framed, beam, all), pass -> {
            all.accept(pass);
            place.seenThrough(pass, camera);
        });
        return new Picture(canvas.width(), canvas.pixels());
    }
}
