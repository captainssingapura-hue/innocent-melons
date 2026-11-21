package com.example.fxsuite.life.body.muscle;

/** How deep a muscle lies: under the skin; under another muscle; or a tendon's or a band's, not a muscle's. */
public enum Layer {
    SUPERFICIAL("under the skin"),
    DEEP("under other muscles"),
    TENDON("a tendon or a band");

    private final String said;

    Layer(final String said) {
        this.said = said;
    }

    public String said() {
        return said;
    }
}
