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

    private static final double MEGABYTE = 1024.0 * 1024;

    private final Said said = new Said();
    private final Owner own = new Owner();
    private final Owner theirs = new Owner();
    private final Compiler compiler;
    private final Optional<Desk> desk;
    private final Reach reach;
    private final Recording recording;
    /** A picture of depths with nothing on it: what a frame's looks look things up in when it is given no other. */
    private final Read nothing;
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
            this.nothing = new Read(new Depths(reach, recording, 1),
                    reach.rays() ? Optional.of(new Standing(reach, recording)) : Optional.empty());
        } catch (final RuntimeException refused) {
            theirs.swept();
            own.swept();
            said.closed();
            throw refused;
        }
    }

    /** How much memory is held on the card by what was made on it, and how much of its own the card has. */
    public Memory memory() {
        open();
        return new Memory(reach.holding().carded() / MEGABYTE, reach.holding().reached() / MEGABYTE,
                new Stored(reach).whole());
    }

    /** The card's own name, as its maker has it. */
    public String name() {
        return reach.name();
    }

    /** A picture to draw on with no window, {@code width} pixels by {@code height}. */
    public Canvas canvas(final int width, final int height) {
        return made(() -> new Canvas(reach, recording, nothing, width, height));
    }

    /**
     * A window to draw to, called {@code title}, its inside {@code width} by {@code height} as the screen measures
     * its own windows; hidden until it is shown. Only a card opened with a desk has windows.
     */
    public Window window(final String title, final int width, final int height) {
        final Desk on = desk.orElseThrow(() -> new IllegalStateException(
                "the card was opened to draw pictures only, with no desk for a window to stand on"));
        return made(() -> new Window(reach, recording, nothing, on, title, width, height));
    }

    /**
     * A picture of depths, {@code size} pixels wide and as many tall, to be drawn first and then looked up by the
     * looks of what is drawn after it.
     */
    public Depths depths(final int size) {
        return made(() -> new Depths(reach, recording, size));
    }

    /**
     * Whether the card answers rays: whether a look on it may ask if anything stands along a straight line from a
     * point. So where the card has what that takes, unless the run is told {@code -Dvulkan.rays=false}. Layouts
     * that ask rays ({@link Laid#asking}) are for such a card alone.
     */
    public boolean rays() {
        return reach().rays();
    }

    /** What stands in a frame, kept for rays to be asked of: nothing, until it is gathered. */
    public Standing standing() {
        if (!rays()) {
            throw new IllegalStateException("the card " + name() + " answers no rays, and keeps nothing for them");
        }
        return made(() -> new Standing(reach, recording));
    }

    /** {@code laid}, where the card can build what it asks: layouts that ask rays are refused by one that cannot. */
    private Laid answered(final Laid laid) {
        if (laid.rays() && !rays()) {
            throw new IllegalArgumentException("the card " + name() + " answers no rays, and builds no look that"
                    + " asks any");
        }
        return laid;
    }

    /** The look of {@code vertex} and {@code fragment}, laid out as {@code laid} says, built ready to draw with. */
    public Drawing drawing(final Laid laid, final Shader vertex, final Shader fragment) {
        return made(() -> new Drawing(reach, compiler, answered(laid), vertex, Optional.of(fragment), false));
    }

    /**
     * The same, for what is seen through: what it draws is mixed with what is already drawn. Its fragment shader's
     * fourth number says how much of what was there it hides, from none to all; its colour is added to the rest.
     * It is hidden by whatever is nearer and already drawn, and hides nothing drawn after it, so it is drawn last,
     * and of several such, the furthest first.
     */
    public Drawing mixing(final Laid laid, final Shader vertex, final Shader fragment) {
        return made(() -> new Drawing(reach, compiler, answered(laid), vertex, Optional.of(fragment), true));
    }

    /** The look of {@code vertex} alone, which colours nothing: what a picture of depths is drawn with. */
    public Tracing tracing(final Laid laid, final Shader vertex) {
        return made(() -> new Tracing(reach, compiler, answered(laid), vertex));
    }

    /**
     * Room for {@code most} copies of what is drawn, laid out as {@code laid} lays a copy out, to be filled and drawn
     * many at once; more room is made as more are filled.
     */
    public Copies copies(final Laid laid, final int most) {
        return made(() -> new Copies(reach, laid.copy(), most));
    }

    /** {@code mesh} sent to the card, to be drawn from. */
    public Sent sent(final Packed mesh) {
        return made(() -> new Sent(reach, recording, mesh));
    }

    /**
     * {@code mesh} sent to the card to be drawn from, but standing in the way of no ray: its triangles are not
     * kept for rays, which spares the memory and time that takes. For what is too far off for its shadow to
     * matter. Gathered for rays, it is passed over.
     */
    public Sent sentUnstanding(final Packed mesh) {
        return made(() -> new Sent(reach, recording, mesh, false));
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
        nothing.standing().ifPresent(Standing::close);
        nothing.depths().close();
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
