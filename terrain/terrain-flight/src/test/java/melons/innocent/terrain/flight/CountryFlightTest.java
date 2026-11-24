package melons.innocent.terrain.flight;

import com.example.fxsuite.vulkan.bench.Asked;
import com.example.fxsuite.vulkan.bench.Skies;
import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.flight.Aloft;
import com.example.fxsuite.vulkan.flight.Flown;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.LandFeatures;
import melons.innocent.terrain.melon.MelonFeatures;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Flying over a country. What lies below a point is its ground, or its water's top where that is higher. A flight
 * over the Melon country, climbing and flying fast at its mountains, keeps half a metre over whatever is below at
 * every step. And the country is drawn on the card from such a flight, every tile to be shown put on the card, with
 * nothing said by the validation layer; the picture is written to {@code target/card/melon-from-the-air.png}.
 */
class CountryFlightTest {

    private final LandFeatures melon = MelonFeatures.INSTANCE.features();
    private final Grounded below = new Grounded(melon);

    @Test
    void whatLiesBelowIsTheGroundOrTheWaterOverIt() {
        final Extent e = melon.ground().extent();
        int wet = 0;
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 40; j++) {
                final double x = e.x0() + (i + 0.5) * e.across() / 40;
                final double z = e.z0() + (j + 0.5) * e.along() / 40;
                final double water = melon.water(x, z);
                final double ground = melon.ground().height(x, z);
                assertEquals(Double.isNaN(water) ? ground : Math.max(ground, water), below.under(x, z), 1e-9);
                wet += !Double.isNaN(water) && water > ground ? 1 : 0;
            }
        }
        assertTrue(wet > 0, "some of the country is under water");
    }

    @Test
    void aFlightAtTheMountainsKeepsHalfAMetreOverWhateverIsBelow() {
        final Extent e = melon.ground().extent();
        final Flown flight = new Flown(new Scripted("R;R;R;W:20;W+@90:20;W+@90:20"), Optional.empty(),
                Optional.of(new Skies(new Asked(1))), below, new Bot(),
                new Aloft(new Vec3((e.x0() + e.x1()) / 2, 0, (e.z0() + e.z1()) / 2), 0, 3));
        while (!flight.over()) {
            flight.stepped(1.0 / 30);
            final Vec3 feet = flight.shown(1).feet();
            assertTrue(feet.y() >= below.under(feet.x(), feet.z()) + 50 - 1e-6, "over the ground: " + feet);
        }
    }

    @Test
    void theCountryIsDrawnOnTheCardFromTheAirWithNothingSaid() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Extent e = melon.ground().extent();
        final Daylight day = new Daylight();
        try (Card card = new Card(); Canvas canvas = card.canvas(1280, 720)) {
            final Laid laid = new Laid();
            final Flown flight = new Flown(new Scripted("SPACE:4"), Optional.empty(), Optional.empty(), below,
                    new Bot(), new Aloft(new Vec3((e.x0() + e.x1()) / 2, 0, e.z0() + e.along() * 0.3), 0, 4));
            while (!flight.over()) {
                flight.stepped(1.0 / 30);
            }
            try (CountryPlace place = new CountryPlace(card, laid, melon, below)) {
                final Camera camera = flight.camera(1);
                final long began = System.nanoTime();
                place.readied(camera);
                while (!place.settled() && System.nanoTime() - began < 60_000_000_000L) {
                    place.readied(camera);
                }
                assertTrue(place.settled(), "every tile to be shown put on the card: " + place.said());
                canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling(1280.0 / 720),
                        pass -> place.drawn(pass, camera));
                ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png",
                        folder.resolve("melon-from-the-air.png").toFile());
            }
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }
}
