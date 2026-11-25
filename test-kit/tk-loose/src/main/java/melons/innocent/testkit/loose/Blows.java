package melons.innocent.testkit.loose;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Happened;
import melons.innocent.core.rest.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The blows of things thrown: each thing thrown is followed until it is at rest again, and when it comes down out of
 * the air on something, the blow is reckoned: half its mass times the square of how fast it met what it came down
 * on, in joules. A blow that brings as much as what it struck stands breaks that, if it is a thing that can move at
 * all; and a thing that comes down faster than its own stuff stands breaks itself. So a watermelon thrown hard
 * enough is a weapon: it breaks a crate, and bursts itself if it is thrown harder still. How much each stands is
 * its stuff's.
 */
final class Blows {

    /** The things thrown and not yet at rest again, each with how it was going before the tick. */
    private final Map<String, Spot> going = new TreeMap<>();
    private Optional<String> last = Optional.empty();

    /** The thing called {@code name} has been thrown, and is followed from now. */
    void thrown(final String name) {
        going.put(name, new Spot(0, 0, 0));
    }

    /** Before a tick of {@code world}: how each thing followed is going now. */
    void before(final World world) {
        going.keySet().removeIf(name -> !world.things().contains(name));
        going.replaceAll((name, was) -> world.moved(name).velocity());
    }

    /** After that tick, in which {@code happened}: the names of the things a blow has broken. */
    List<String> after(final World world, final List<Happened> happened, final Listed listed) {
        final List<String> broken = new ArrayList<>();
        for (final Happened one : happened) {
            if (one instanceof Happened.Landed landed && going.containsKey(landed.thing())) {
                final String thrown = landed.thing();
                final boolean struck = world.things().contains(landed.on());
                final Spot against = struck ? world.moved(landed.on()).velocity() : new Spot(0, 0, 0);
                final double speed = going.get(thrown).minus(against).length() / 100;
                final double joules = world.thing(thrown).kilograms() * speed * speed / 2;
                final boolean burst = speed >= listed.stuff(thrown).bursts();
                final boolean broke = struck && !world.thing(landed.on()).unmoving()
                        && joules >= listed.stuff(landed.on()).breaks();
                if (burst) {
                    broken.add(thrown);
                }
                if (broke) {
                    broken.add(landed.on());
                }
                last = Optional.of(String.format(Locale.ENGLISH, "Struck: %s met %s at %.1f m/s with %.0f J%s%s",
                        thrown, landed.on(), speed, joules, broke ? "; " + landed.on() + " broke" : "",
                        burst ? "; " + thrown + " broke" : ""));
            }
            if (one instanceof Happened.Rested rested) {
                going.remove(rested.thing());
            }
        }
        return broken;
    }

    /** What came of the last blow, in words: nothing, where nothing thrown has yet come down. */
    Optional<String> said() {
        return last;
    }

    /** Nothing followed, and nothing to say. */
    void clear() {
        going.clear();
        last = Optional.empty();
    }
}
