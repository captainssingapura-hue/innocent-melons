package melons.innocent.forms;

/**
 * A colour a surface is given over what it is made of: on what is seen through, as glass is, the colour it is tinted;
 * on anything else, the colour it is painted, hiding what is under it. Red, green and blue, each nothing to one.
 *
 * @param red   how red
 * @param green how green
 * @param blue  how blue
 */
public record Tint(double red, double green, double blue) {

    public Tint {
        if (!(red >= 0 && red <= 1) || !(green >= 0 && green <= 1) || !(blue >= 0 && blue <= 1)) {
            throw new IllegalArgumentException("red, green and blue each nothing to one, not " + red + ", " + green
                    + ", " + blue);
        }
    }

    /** As written {@code #rrggbb}. */
    public Tint(final String web) {
        this(Integer.parseInt(web.substring(1, 3), 16) / 255.0, Integer.parseInt(web.substring(3, 5), 16) / 255.0,
                Integer.parseInt(web.substring(5, 7), 16) / 255.0);
    }
}
