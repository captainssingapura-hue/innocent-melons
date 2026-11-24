package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Replayed;
import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Build;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * The engine's own avatar, moved by the engine's simulation, on its feet and on its jets: the walks' first way of
 * moving. Its lead starts where it is told, which is where {@code -Dbench.at=} says unless a bench says otherwise,
 * and is drawn as the bot {@code -Dbench.bot=} says (the man unless said); a companion comes with it where {@code -Dbench.companion.script=} or {@code
 * -Dbench.companion.replay=} is told, starting where {@code -Dbench.companion.at=} says, drawn as {@code
 * -Dbench.companion.bot=} (the woman unless said).
 */
public record Afoot(String at) implements Roamer {

    /** Its lead starting where the run says. */
    public Afoot() {
        this(System.getProperty("bench.at", ""));
    }

    @Override
    public Roaming roaming(final Source source, final Optional<Keeping> keeping, final Skies skies) {
        return new Company(new Run(source, keeping, Optional.of(skies),
                new Start(at).body()),
                new Bot(new Said(System.getProperty("bench.bot", "man")).build()), companion());
    }

    @Override
    public List<String> keys() {
        return List.of("In the card's window:", "  mouse: turn (Esc lets it go)", "  W, S: ahead and back",
                "  A, D: sideways", "  Shift: walk", "  Space: jump",
                "  F: fly, or land; flying, Space climbs and Ctrl sinks", "  Q, E: swing the view round; C: back behind",
                "  scroll: nearer or further");
    }

    /** The companion the run is told of, if any. */
    private Optional<Company.Companion> companion() {
        final String replay = System.getProperty("bench.companion.replay", "");
        final String script = System.getProperty("bench.companion.script", "");
        if (replay.isEmpty() && script.isEmpty()) {
            return Optional.empty();
        }
        final Source moves = !replay.isEmpty() ? new Replayed(Path.of(replay)) : new Scripted(script);
        return Optional.of(new Company.Companion(new Run(moves, Optional.empty(), Optional.empty(),
                new Start(System.getProperty("bench.companion.at", "")).body()),
                new Bot(new Said(System.getProperty("bench.companion.bot", "woman")).build())));
    }

    /** A bot's build as a run is told it: {@code man} or {@code woman}, any case. */
    public record Said(String said) {

        /** The build said. */
        public Build build() {
            return Build.valueOf(said.trim().toUpperCase(Locale.ROOT));
        }
    }
}
