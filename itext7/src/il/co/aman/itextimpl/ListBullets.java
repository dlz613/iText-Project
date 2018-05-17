package il.co.aman.itextimpl;

/**
 * Supplies bullets (for unordered lists) and numeration (for ordered lists) for
 * the {@link FakeList} class.
 *
 * @author davidz
 */
public class ListBullets {

    /**
     * The various ways of numerating an ordered list.
     */
    public enum OrderedType {

        /**
         * Numeric (default)
         */
        NUMBER,
        /**
         * Capital letters
         */
        ALPHA_CAPS,
        /**
         * Lower-case letters
         */
        ALPHA,
        /**
         * Capital Roman numerals
         */
        ROMAN_CAPS,
        /**
         * Lower-case Roman numerals
         */
        ROMAN;

        public static OrderedType getOrderedType(String code) {
            if ("1".equals(code)) {
                return OrderedType.NUMBER;
            } else if ("A".equals(code)) {
                return OrderedType.ALPHA_CAPS;
            } else if ("a".equals(code)) {
                return OrderedType.ALPHA;
            } else if ("I".equals(code)) {
                return OrderedType.ROMAN_CAPS;
            } else if ("i".equals(code)) {
                return OrderedType.ROMAN;
            } else {
                return null;
            }
        }

    }

    public static char[] DEFAULT_BULLETS = new char[]{'\u2022', '\u25E6', '\u25AA'}; // black round bullet, white round bullet, small black square bullet
    public static final String PERIOD_SPACE = ". ";
    public static final String SPACE_PERIOD = " .";

    /**
     * Returns the proper bullet or numeration, in accordance with the
     * parameters.
     *
     * @param ordered
     * @param orderedType
     * @param ord The ordinal number (1-based) of the current element.
     * @param level The current hierarchical level.
     * @param bullets
     * @param isRtl
     * @return
     */
    public static String getBullet(boolean ordered, OrderedType orderedType, int ord, int level, char[] bullets, boolean isRtl) {
        if (ordered) {
            switch (orderedType) {
                case NUMBER:
                    return orderedBullet(isRtl, Integer.toString(ord));
                case ALPHA_CAPS:
                    return orderedBullet(isRtl, getAlpha(ord).toUpperCase());
                case ALPHA:
                    return orderedBullet(isRtl, getAlpha(ord));
                case ROMAN_CAPS:
                    return orderedBullet(isRtl, Roman.int2roman(ord));
                case ROMAN:
                    return orderedBullet(isRtl, Roman.int2roman(ord).toLowerCase());
                default:
                    return "";
            }
        } else {
            int index = level;
            if (index >= bullets.length) {
                index = bullets.length - 1;
            }
            return unorderedBullet(isRtl, bullets[index]);
        }
    }

    private static String unorderedBullet(boolean isRtl, char val) {
        if (isRtl) {
            return new String(new char[]{' ', val});
        } else {
            return new String(new char[]{val, ' '});
        }
    }

    private static String orderedBullet(boolean isRtl, String val) {
        if (isRtl) {
            return SPACE_PERIOD + val;
        } else {
            return val + PERIOD_SPACE;
        }
    }

    private static String getAlpha(int num) {
        String result = "";
        while (num > 0) {
            num--; // 1 => a, not 0 => a
            int remainder = num % 26;
            char digit = (char) (remainder + 97);
            result = digit + result;
            num = (num - remainder) / 26;
        }
        return result;
    }

}
