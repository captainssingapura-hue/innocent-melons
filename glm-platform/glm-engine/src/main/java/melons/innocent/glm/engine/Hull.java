package melons.innocent.glm.engine;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The smallest outline round some spots, as a band stretched round pins in a board would lie: drawn round to the
 * left, from the lowest of the leftmost.
 *
 * @param spots the spots it is drawn round
 */
public record Hull(List<Spot> spots) {

    public Hull {
        spots = List.copyOf(spots);
    }

    /** It. */
    public List<Spot> outline() {
        final List<Spot> sorted = new ArrayList<>(spots);
        sorted.sort((a, b) -> a.u() != b.u() ? Double.compare(a.u(), b.u()) : Double.compare(a.v(), b.v()));
        final List<Spot> lower = half(sorted);
        final List<Spot> upper = half(sorted.reversed());
        lower.removeLast();
        upper.removeLast();
        lower.addAll(upper);
        return lower;
    }

    private List<Spot> half(final List<Spot> sorted) {
        final List<Spot> made = new ArrayList<>();
        for (final Spot p : sorted) {
            while (made.size() >= 2 && made.get(made.size() - 2).turn(made.getLast(), p) <= 1e-12) {
                made.removeLast();
            }
            made.add(p);
        }
        return made;
    }
}
