package com.example.fxsuite.existence;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Meanings;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * Uses: what a storey or a space is for. A branch that is {@link Occupied} says so for every use under it: people
 * spend their time there, so it must be reached. A use that is {@link Louvred} is shut in with louvres, not glazed.
 *
 * <p>Each node is a record, its parent in its type; what each means is beside this class, in {@code Uses.md}.
 */
public final class Uses {

    private Uses() {
    }

    /** The tree, read once from its leaves. */
    public static final Taxonomy TREE = Taxonomy.of("uses", new Use(), List.of(new Homes(), new Penthouses(),
            new ServicedHomes(), new Offices(), new CoWorking(), new Studios(), new MedicalSuites(), new Shops(),
            new Market(), new Banking(), new HotelRooms(), new Restaurant(), new Bar(), new Club(), new Gym(),
            new Pool(), new Garden(), new Observation(), new Gallery(), new Auditorium(), new EventSpace(),
            new Courts(), new Worship(), new Plant(), new Parking(), new Technical(), new Storage(), new Lobby(),
            new SkyLobby(), new Crossing(), new Passage(), new Watch()), Meanings.beside(Uses.class));

    /** The root. What a storey or a space is for. */
    public record Use() implements L0 {
    }

    // Dwelling
    public record Dwelling() implements L1<Use>, Occupied { public Use parent() { return new Use(); } }
    public record Homes() implements Leaf<Dwelling> { public Dwelling parent() { return new Dwelling(); } }
    public record Penthouses() implements Leaf<Dwelling> { public Dwelling parent() { return new Dwelling(); } }
    public record ServicedHomes() implements Leaf<Dwelling> { public Dwelling parent() { return new Dwelling(); } }

    // Work
    public record Work() implements L1<Use>, Occupied { public Use parent() { return new Use(); } }
    public record Offices() implements Leaf<Work> { public Work parent() { return new Work(); } }
    public record CoWorking() implements Leaf<Work> { public Work parent() { return new Work(); } }
    public record Studios() implements Leaf<Work> { public Work parent() { return new Work(); } }
    public record MedicalSuites() implements Leaf<Work> { public Work parent() { return new Work(); } }

    // Trade
    public record Trade() implements L1<Use>, Occupied { public Use parent() { return new Use(); } }
    public record Shops() implements Leaf<Trade> { public Trade parent() { return new Trade(); } }
    public record Market() implements Leaf<Trade> { public Trade parent() { return new Trade(); } }
    public record Banking() implements Leaf<Trade> { public Trade parent() { return new Trade(); } }

    // Hospitality
    public record Hospitality() implements L1<Use>, Occupied { public Use parent() { return new Use(); } }
    public record HotelRooms() implements Leaf<Hospitality> {
        public Hospitality parent() { return new Hospitality(); }
    }
    public record Restaurant() implements Leaf<Hospitality> {
        public Hospitality parent() { return new Hospitality(); }
    }
    public record Bar() implements Leaf<Hospitality> { public Hospitality parent() { return new Hospitality(); } }

    // Leisure
    public record Leisure() implements L1<Use>, Occupied { public Use parent() { return new Use(); } }
    public record Club() implements Leaf<Leisure> { public Leisure parent() { return new Leisure(); } }
    public record Gym() implements Leaf<Leisure> { public Leisure parent() { return new Leisure(); } }
    public record Pool() implements Leaf<Leisure> { public Leisure parent() { return new Leisure(); } }
    public record Garden() implements Leaf<Leisure> { public Leisure parent() { return new Leisure(); } }
    public record Observation() implements Leaf<Leisure> { public Leisure parent() { return new Leisure(); } }
    public record Gallery() implements Leaf<Leisure> { public Leisure parent() { return new Leisure(); } }

    // Assembly
    public record Assembly() implements L1<Use>, Occupied { public Use parent() { return new Use(); } }
    public record Auditorium() implements Leaf<Assembly> { public Assembly parent() { return new Assembly(); } }
    public record EventSpace() implements Leaf<Assembly> { public Assembly parent() { return new Assembly(); } }
    public record Courts() implements Leaf<Assembly> { public Assembly parent() { return new Assembly(); } }
    public record Worship() implements Leaf<Assembly> { public Assembly parent() { return new Assembly(); } }

    // Service
    public record Service() implements L1<Use> { public Use parent() { return new Use(); } }
    public record Plant() implements Leaf<Service>, Louvred { public Service parent() { return new Service(); } }
    public record Parking() implements Leaf<Service>, Louvred { public Service parent() { return new Service(); } }
    public record Technical() implements Leaf<Service>, Louvred { public Service parent() { return new Service(); } }
    public record Storage() implements Leaf<Service> { public Service parent() { return new Service(); } }

    // Movement
    public record Movement() implements L1<Use> { public Use parent() { return new Use(); } }
    public record Lobby() implements Leaf<Movement> { public Movement parent() { return new Movement(); } }
    public record SkyLobby() implements Leaf<Movement> { public Movement parent() { return new Movement(); } }
    public record Crossing() implements Leaf<Movement> { public Movement parent() { return new Movement(); } }
    public record Passage() implements Leaf<Movement> { public Movement parent() { return new Movement(); } }

    // Defence
    public record Defence() implements L1<Use> { public Use parent() { return new Use(); } }
    public record Watch() implements Leaf<Defence> { public Defence parent() { return new Defence(); } }
}
