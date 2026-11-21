package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.form.Packed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Shader;
import org.lwjgl.vulkan.VkInstance;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK13.vkDeviceWaitIdle;

/**
 * The graphics card, opened: the one way anything is drawn with Vulkan. The thread that opens it is the only thread
 * that may use it, or anything made on it. The Khronos validation layer is on from its opening, and it is not opened
 * without the layer.
 *
 * <p>What is made on it (a look, a mesh, a picture, a window) is closed by whoever asked for it. Closing the card
 * closes what was left open, newest first, and then says so: anything left open, and anything the validation layer
 * said that nobody heard, is an exception from {@link #close}. Something that fails half made is unmade again
 * before the failure is passed on.
 *
 * <p>Which card is opened, where the computer has more than one, is the one of its own before the one built into
 * the processor; {@code -Dvulkan.card=} with part of a card's name has that card and no other.
 */
public final class Card implements AutoCloseable {

    private final Said said = new Said();
    private final Owner own = new Owner();
    private final Owner theirs = new Owner();
    private final Compiler compiler;
    private final Optional<Desk> desk;
    private final Reach reach;
    private final Recording recording;
    private boolean closed;

    /** The card, to draw pictures on with no window; its shaders compiled by the standard compiler. */
    public Card() {
        this(new Standard());
    }

    /** The card, to draw pictures on with no window; its shaders compiled by {@code compiler}. */
    public Card(final Compiler compiler) {
        this(compiler, Optional.empty());
    }

    /** The card, to draw in windows on {@code desk} as well; its shaders compiled by the standard compiler. */
    public Card(final Desk desk) {
        this(new Standard(), Optional.of(desk));
    }

    private Card(final Compiler compiler, final Optional<Desk> desk) {
        this.compiler = compiler;
        this.desk = desk;
        try {
            final Opening opening = new Opening(said, desk.map(Desk::needed).orElse(List.of()));
            final VkInstance instance = opening.opened(own);
            opening.hearing(instance, own);
            final List<String> shown = desk.isPresent() ? List.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME) : List.of();
            final String wanted = System.getProperty("vulkan.card", "");
            this.reach = new Reach(new Choosing(instance, wanted).chosen(), shown, own, theirs);
            this.recording = new Recording(reach, own);
        } catch (final RuntimeException refused) {
            own.swept();
            said.closed();
            throw refused;
        }
    }

    /** The card's own name, as its maker has it. */
    public String name() {
        return reach.name();
    }

    /** A picture to draw on with no window, {@code width} pixels by {@code height}. */
    public Canvas canvas(final int width, final int height) {
        return made(() -> new Canvas(reach, recording, width, height));
    }

    /**
     * A window to draw to, called {@code title}, its inside {@code width} by {@code height} as the screen measures
     * its own windows; hidden until it is shown. Only a card opened with a desk has windows.
     */
    public Window window(final String title, final int width, final int height) {
        final Desk on = desk.orElseThrow(() -> new IllegalStateException(
                "the card was opened to draw pictures only, with no desk for a window to stand on"));
        return made(() -> new Window(reach, recording, on, title, width, height));
    }

    /** The look of {@code vertex} and {@code fragment}, laid out as {@code laid} says, built ready to draw with. */
    public Drawing drawing(final Laid laid, final Shader vertex, final Shader fragment) {
        return made(() -> new Drawing(reach, compiler, laid, vertex, fragment));
    }

    /** {@code mesh} sent to the card, to be drawn from. */
    public Sent sent(final Packed mesh) {
        return made(() -> new Sent(reach, mesh));
    }

    /** The card as the kit reaches it: for the kit, and for its tests to misuse the card with. */
    Reach reach() {
        open();
        return reach;
    }

    /** What the validation layer has said since it was last asked; once heard here, it is not said again. */
    public List<String> heard() {
        return said.heard();
    }

    /** What {@code making} makes; or, where it fails part way, what it had made unmade and the failure passed on. */
    private <T> T made(final Supplier<T> making) {
        open();
        final int before = theirs.alive();
        try {
            return making.get();
        } catch (final RuntimeException refused) {
            theirs.unmade(before);
            throw refused;
        }
    }

    private void open() {
        own.mine();
        if (closed) {
            throw new IllegalStateException("the card is closed, and nothing more is made on it");
        }
    }

    @Override
    public void close() {
        own.mine();
        if (closed) {
            return;
        }
        closed = true;
        new Checked("vkDeviceWaitIdle").of(vkDeviceWaitIdle(reach.device()));
        final List<String> left = theirs.swept();
        own.swept();
        final List<String> unheard = said.heard();
        said.closed();
        if (!left.isEmpty()) {
            throw new IllegalStateException("left open when the card was closed, and closed with it: " + left);
        }
        if (!unheard.isEmpty()) {
            throw new Refused("the validation layer", String.join("\n", unheard));
        }
    }
}
