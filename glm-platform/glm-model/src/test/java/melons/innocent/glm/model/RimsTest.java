package melons.innocent.glm.model;

import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.PartAt;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.wheels.ClassicRim;
import melons.innocent.vehiclelab.wheels.WheelSize;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The cars' rims: the retro saloon on the classic rim and the large SUV on the spoked, each at its car's size, its
 * size held to the platform's range there and nowhere else; and the retro saloon's brakes, discs and calipers, clear
 * of its rims and its tyres.
 */
class RimsTest {

    private final Santana car = new Santana();
    private final Touching touching = new Touching();

    @Test
    void eachCarOnItsOwnRimsAtItsSizeHeldToThePlatformsRangeOnlyThere() {
        assertEquals(car.rim().rim(new Shapes(24)).getClass(), ClassicRim.class);
        assertEquals("225/50 R17", car.proportions().wheel().said());
        assertEquals(new GlmPlatform().platform().connections().stream().filter(c -> c.carried().name()
                .equals("wheel")).count(), 1);
        new WheelSize(165, 80, 13);
        assertThrows(IllegalArgumentException.class, () -> new Proportions(270, 150, 165, 80, 13));
    }

    @Test
    void theRetroSaloonsBrakesClearOfItsRimsAndTyres() {
        final Assembled built = new Assembly(car.platform(null).platform()).assembled();
        assertEquals(List.of(), built.problems(1e-6));
        final List<PartAt> wheels = built.placed().keySet().stream()
                .filter(p -> p.part().group() == Group.WHEELS || p.part().group() == Group.TYRES).toList();
        for (final PartAt brake : built.placed().keySet()) {
            if (brake.part().group() == Group.BRAKES || brake.part().group() == Group.CALIPERS) {
                for (final PartAt wheel : wheels) {
                    assertFalse(touching.touch(brake.shape().form(), built.at(brake), wheel.shape().form(),
                            built.at(wheel)), brake.said() + " meets " + wheel.said());
                }
            }
        }
    }
}
