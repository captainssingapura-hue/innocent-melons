package com.example.fxsuite.vulkan.device;

import java.util.function.Consumer;

import static org.lwjgl.vulkan.KHRAccelerationStructure.*;
import static org.lwjgl.vulkan.VK13.VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT;

/**
 * What stands in a frame, kept on the card so that a look can ask a ray of it: whether anything stands along a
 * straight line from a point, within so far. Each thing is a mesh already on the card and where it stands; they
 * are gathered by drawing the frame's things to what is handed over, exactly as they are drawn on a picture, and
 * are kept until they are gathered again. Until first gathered, nothing stands. What is seen through stands in the
 * way of no ray, as it leaves nothing in a picture of depths.
 *
 * <p>Only a card that answers rays keeps one ({@link Card#rays}). What the answer means is for whoever asks: a ray
 * toward the sun, for shadows, is one use of it.
 */
public final class Standing implements AutoCloseable {

    /** How many things there is room for to begin with. */
    private static final int FIRST = 64;

    private final Reach reach;
    private final Recording recording;
    private int most;
    private Bytes things;
    private Structured kept;

    Standing(final Reach reach, final Recording recording) {
        this.reach = reach;
        this.recording = recording;
        roomFor(FIRST);
        built(new Gathered());
    }

    /**
     * Gathered afresh, and done, all of it, before this comes back: whatever {@code drawing} draws stands, where
     * it is drawn, and nothing else. What draws is handed to {@code drawing} and draws only until {@code drawing}
     * is done; it draws on no picture. This, for the frame to be drawn asking rays of it.
     */
    public Standing stood(final Consumer<Pass> drawing) {
        final Gathered gathered = new Gathered();
        final Pass pass = new Pass(gathered);
        try {
            drawing.accept(pass);
        } finally {
            pass.ended();
        }
        if (gathered.count() > most) {
            kept.close();
            things.close();
            roomFor(Math.max(gathered.count(), 2 * most));
        }
        built(gathered);
        return this;
    }

    /** Room made for {@code many} things. */
    private void roomFor(final int many) {
        this.most = many;
        this.things = new Bytes(reach, "the things standing in a frame", Gathered.EACH * many,
                VK_BUFFER_USAGE_ACCELERATION_STRUCTURE_BUILD_INPUT_READ_ONLY_BIT_KHR
                        | VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT);
        final long where = things.address();
        this.kept = new Structured(reach, "what stands in a frame", VK_ACCELERATION_STRUCTURE_TYPE_TOP_LEVEL_KHR,
                VK_BUILD_ACCELERATION_STRUCTURE_PREFER_FAST_BUILD_BIT_KHR, many, of -> {
                    of.geometryType(VK_GEOMETRY_TYPE_INSTANCES_KHR);
                    of.geometry().instances().sType$Default().data().deviceAddress(where);
                });
    }

    private void built(final Gathered gathered) {
        things.filled(gathered.written(most));
        kept.built(recording.begun(), gathered.count());
        recording.sent();
    }

    /** What is kept, as a look is set to ask rays of it: another, once there has been more room to make. */
    Kept<Of.Structure> structure() {
        return kept.structure();
    }

    @Override
    public void close() {
        kept.close();
        things.close();
    }
}
