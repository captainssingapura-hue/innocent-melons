package melons.innocent.glm.enginebench;

import com.example.fxsuite.avatar.sound.Clip;
import com.example.fxsuite.avatar.sound.Engine;
import com.example.fxsuite.avatar.sound.EngineVoice;
import com.example.fxsuite.avatar.sound.Wav;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * The engine heard as it runs, on the speakers: its sound made as it goes by the engines' own voice, from its revs,
 * its throttle and its load, a little at a time, on a thread of its own; a new engine started afresh. Where there are
 * no speakers, or it is asked to keep quiet, it makes its sound all the same and plays none.
 */
public final class EngineSound implements AutoCloseable {

    /** Samples made at a time, about 12 ms; how many blocks the line holds ahead. */
    private static final int BLOCK = 512;
    private static final int AHEAD = 4;

    private volatile Engine engine;
    private volatile double rpm;
    private volatile double throttle;
    private volatile double load;
    private volatile double volume = 0.8;
    private volatile double boost;
    private volatile boolean running = true;
    private final SourceDataLine line;
    private final String device;
    private final Thread thread;

    /** Playing {@code engine}, unless {@code quiet}. */
    public EngineSound(final Engine engine, final boolean quiet) {
        this.engine = engine;
        SourceDataLine opened = null;
        String name = "none: kept quiet";
        if (!quiet) {
            try {
                final AudioFormat format = new AudioFormat(Clip.RATE, 16, 1, true, false);
                opened = AudioSystem.getSourceDataLine(format);
                opened.open(format, BLOCK * 2 * AHEAD);
                opened.start();
                name = "Java Sound";
            } catch (LineUnavailableException | IllegalArgumentException e) {
                opened = null;
                name = "none (" + e.getMessage() + ")";
            }
        }
        this.line = opened;
        this.device = name;
        this.thread = new Thread(this::run, "glm-engine-sound");
        thread.setDaemon(true);
        thread.start();
    }

    /** What it plays on. */
    public String device() {
        return device;
    }

    /** Which engine; a new one starts afresh. */
    public void engine(final Engine playing) {
        this.engine = playing;
    }

    /** What the engine is doing: its revs, its throttle and its load. */
    public void set(final double revs, final double open, final double working) {
        this.rpm = revs;
        this.throttle = open;
        this.load = working;
    }

    /** How loud, nought to one. */
    public void volume(final double loud) {
        this.volume = loud;
    }

    /** How much boost its turbocharger makes, as it plays. */
    public double boost() {
        return boost;
    }

    private void run() {
        final float[] block = new float[BLOCK];
        final byte[] bytes = new byte[BLOCK * 2];
        Engine playing = engine;
        EngineVoice voice = new EngineVoice(playing, 1);
        final long nanos = Math.round(1e9 * BLOCK / Clip.RATE);
        long due = System.nanoTime();
        try {
            while (running) {
                if (engine != playing) {
                    playing = engine;
                    voice = new EngineVoice(playing, 1);
                }
                voice.set(rpm, throttle, load);
                voice.render(block, 0, BLOCK);
                boost = voice.boost();
                if (line != null) {
                    for (int i = 0; i < BLOCK; i++) {
                        final short s = Wav.toShort((float) (block[i] * volume));
                        bytes[2 * i] = (byte) s;
                        bytes[2 * i + 1] = (byte) (s >> 8);
                    }
                    line.write(bytes, 0, bytes.length);
                } else {
                    due += nanos;
                    final long wait = due - System.nanoTime();
                    if (wait > 0) {
                        Thread.sleep(wait / 1_000_000, (int) (wait % 1_000_000));
                    }
                }
            }
        } catch (final InterruptedException stopped) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        running = false;
        thread.interrupt();
        if (line != null) {
            line.stop();
            line.flush();
            line.close();
        }
    }
}
