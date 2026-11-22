package com.example.fxsuite.vulkan.device;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkAccelerationStructureBuildGeometryInfoKHR;
import org.lwjgl.vulkan.VkAccelerationStructureBuildSizesInfoKHR;
import org.lwjgl.vulkan.VkAccelerationStructureCreateInfoKHR;
import org.lwjgl.vulkan.VkAccelerationStructureDeviceAddressInfoKHR;
import org.lwjgl.vulkan.VkAccelerationStructureGeometryKHR;
import org.lwjgl.vulkan.VkDevice;

import java.nio.LongBuffer;
import java.util.Optional;
import java.util.function.Consumer;

import static org.lwjgl.vulkan.KHRAccelerationStructure.*;
import static org.lwjgl.vulkan.VK13.*;

/**
 * Pieces kept on the card so that a ray can be asked of them: the triangles of a mesh, or the things of a frame,
 * each a mesh so kept and where it stands. Vulkan calls it an acceleration structure. It is made with room for so
 * many pieces at most and built, from wherever its pieces are told to be read, as often as they change; what it is
 * built in, and what it is worked out with while it is built, stand in the card's own memory.
 */
final class Structured {

    /** What the address its working memory begins at is rounded up to: no card asks for more. */
    private static final int ROUNDED = 256;

    private final Reach reach;
    private final int kind;
    private final int built;
    private final Consumer<VkAccelerationStructureGeometryKHR> pieces;
    private final Bytes kept;
    private final Kept<Of.Structure> structure;
    private Optional<Bytes> working;

    /**
     * Pieces called {@code name}, of the {@code kind} Vulkan has it as, built the way {@code built} asks, with
     * room for {@code most} of them; where they are read from is what {@code pieces} says.
     */
    Structured(final Reach reach, final String name, final int kind, final int built, final int most,
               final Consumer<VkAccelerationStructureGeometryKHR> pieces) {
        this.reach = reach;
        this.kind = kind;
        this.built = built;
        this.pieces = pieces;
        final VkDevice device = reach.device();
        final int own = VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkAccelerationStructureBuildSizesInfoKHR sizes = VkAccelerationStructureBuildSizesInfoKHR
                    .calloc(stack).sType$Default();
            vkGetAccelerationStructureBuildSizesKHR(device, VK_ACCELERATION_STRUCTURE_BUILD_TYPE_DEVICE_KHR,
                    asked(stack).get(0), stack.ints(most), sizes);
            this.kept = new Bytes(reach, name, Math.toIntExact(sizes.accelerationStructureSize()),
                    VK_BUFFER_USAGE_ACCELERATION_STRUCTURE_STORAGE_BIT_KHR
                            | VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT, own);
            this.working = Optional.of(new Bytes(reach, "what " + name + " is worked out with",
                    Math.toIntExact(sizes.buildScratchSize()) + ROUNDED,
                    VK_BUFFER_USAGE_STORAGE_BUFFER_BIT | VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT, own));
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateAccelerationStructureKHR").of(vkCreateAccelerationStructureKHR(device,
                    VkAccelerationStructureCreateInfoKHR.calloc(stack).sType$Default().buffer(kept.buffer().raw())
                            .size(sizes.accelerationStructureSize()).type(kind), null, made));
            this.structure = reach.owner().kept(name + ", to ask rays of", made.get(0),
                    raw -> vkDestroyAccelerationStructureKHR(device, raw, null));
        }
    }

    /** What it is built from and how, as Vulkan is told it: one kind of piece, read from where they are. */
    private VkAccelerationStructureBuildGeometryInfoKHR.Buffer asked(final MemoryStack stack) {
        final VkAccelerationStructureGeometryKHR.Buffer of = VkAccelerationStructureGeometryKHR.calloc(1, stack)
                .sType$Default().flags(VK_GEOMETRY_OPAQUE_BIT_KHR);
        pieces.accept(of.get(0));
        return VkAccelerationStructureBuildGeometryInfoKHR.calloc(1, stack).sType$Default().type(kind)
                .flags(built).mode(VK_BUILD_ACCELERATION_STRUCTURE_MODE_BUILD_KHR).pGeometries(of)
                .geometryCount(1);
    }

    /** Built afresh from {@code count} pieces, among the commands {@code strokes} writes down. */
    void built(final Strokes strokes, final int count) {
        final Bytes with = working.orElseThrow(() -> new IllegalStateException(
                structure.name() + " is settled, and is not built again"));
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkAccelerationStructureBuildGeometryInfoKHR.Buffer asked = asked(stack);
            asked.get(0).dstAccelerationStructure(structure.raw());
            asked.get(0).scratchData().deviceAddress((with.address() + ROUNDED - 1) / ROUNDED * ROUNDED);
            strokes.built(asked, count);
        }
    }

    /** Never to be built again: what it was worked out with is let go. Only once the building is done. */
    void settled() {
        working.ifPresent(Bytes::close);
        working = Optional.empty();
    }

    /** Where on the card it is, for what keeps it as one of its own pieces. */
    long address() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return vkGetAccelerationStructureDeviceAddressKHR(reach.device(),
                    VkAccelerationStructureDeviceAddressInfoKHR.calloc(stack).sType$Default()
                            .accelerationStructure(structure.raw()));
        }
    }

    Kept<Of.Structure> structure() {
        return structure;
    }

    void close() {
        settled();
        structure.close();
        kept.close();
    }
}
