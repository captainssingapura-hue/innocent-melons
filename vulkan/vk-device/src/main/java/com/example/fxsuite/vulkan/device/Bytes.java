package com.example.fxsuite.vulkan.device;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkBufferDeviceAddressInfo;
import org.lwjgl.vulkan.VkDevice;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK13.*;

/**
 * So many bytes on the card, in memory Java can reach: filled whole from Java's bytes, or read whole back into
 * them. Filling it with more or fewer bytes than it holds is refused.
 */
final class Bytes {

    private final Reach reach;
    private final int size;
    private final Kept<Of.Buffer> buffer;
    private final Kept<Of.Memory> memory;

    /** {@code size} bytes called {@code name}, to be used as {@code use} says, where Java can reach them. */
    Bytes(final Reach reach, final String name, final int size, final int use) {
        this(reach, name, size, use, VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);
    }

    /** The same, in memory of the kind {@code wanted}: the card's own, say, which Java does not reach. */
    Bytes(final Reach reach, final String name, final int size, final int use, final int wanted) {
        this.reach = reach;
        this.size = size;
        final VkDevice device = reach.device();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final VkBufferCreateInfo asked = VkBufferCreateInfo.calloc(stack).sType$Default()
                    .size(size).usage(use).sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            final LongBuffer made = stack.mallocLong(1);
            new Checked("vkCreateBuffer").of(vkCreateBuffer(device, asked, null, made));
            this.buffer = reach.owner().kept(name, made.get(0), raw -> vkDestroyBuffer(device, raw, null));
        }
        this.memory = new Stored(reach).under(buffer, wanted,
                (use & VK_BUFFER_USAGE_SHADER_DEVICE_ADDRESS_BIT) != 0);
    }

    /** Filled with {@code from}'s bytes, which are as many as it holds. */
    void filled(final ByteBuffer from) {
        if (from.remaining() != size) {
            throw new IllegalArgumentException(buffer.name() + " holds " + size + " bytes, not " + from.remaining());
        }
        reached().put(from.duplicate());
        vkUnmapMemory(reach.device(), memory.raw());
    }

    /** Its bytes, read back: Java's own copy of them, from the first. */
    ByteBuffer read() {
        final ByteBuffer copy = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        copy.put(reached()).rewind();
        vkUnmapMemory(reach.device(), memory.raw());
        return copy;
    }

    /** Its memory, where Java can reach it; to be let go again by whoever asks. */
    private ByteBuffer reached() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            final PointerBuffer at = stack.mallocPointer(1);
            new Checked("vkMapMemory").of(vkMapMemory(reach.device(), memory.raw(), 0, size, 0, at));
            return MemoryUtil.memByteBuffer(at.get(0), size);
        }
    }

    /** Where on the card its first byte is, for what is told where to read instead of being handed a buffer. */
    long address() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return vkGetBufferDeviceAddress(reach.device(),
                    VkBufferDeviceAddressInfo.calloc(stack).sType$Default().buffer(buffer.raw()));
        }
    }

    Kept<Of.Buffer> buffer() {
        return buffer;
    }

    int size() {
        return size;
    }

    void close() {
        buffer.close();
        memory.close();
    }
}
