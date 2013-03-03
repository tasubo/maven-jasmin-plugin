/* --- Copyright Jonathan Meyer 1997. All rights reserved. -----------------
 > File:        jasmin/src/jasmin/ScannerUtils.java
 > Purpose:     Various static methods utilized to breakdown strings
 > Author:      Jonathan Meyer, 8 Feb 1997
 */

package jasmin;

abstract class ScannerUtils {

    //
    // Converts a string to a number (int, float, long, or double).
    // (uses smallest format that will hold the number)
    //
    public static Number convertNumber(String str) throws NumberFormatException {
        str = str.toUpperCase();
        if (str.startsWith("0X")) {// hex
            switch (str.charAt(str.length() - 1)) {
            case 'L':
                return Long.parseLong(str.substring(2, str.length() - 1), 16);
            default:
                return Integer.parseInt(str.substring(2), 16);
            }
        }
        if (str.startsWith("0N") || str.startsWith("0P")) {
            if (str.equals("0NAN_F")) {
                return Float.NaN;
            } else if (str.equals("0NAN_D")) {
                return Double.NaN;
            } else if (str.equals("0NEG_INFI_F")) {
                return Float.NEGATIVE_INFINITY;
            } else if (str.equals("0NEG_INFI_D")) {
                return Double.NEGATIVE_INFINITY;
            } else if (str.equals("0POS_INFI_F")) {
                return Float.POSITIVE_INFINITY;
            } else if (str.equals("0POS_INFI_D")) {
                return Double.POSITIVE_INFINITY;
            }
            throw new IllegalArgumentException("malformed number : " + str);
        } else {
            switch (str.charAt(str.length() - 1)) {
            case 'D':
                return Double.parseDouble(str.substring(0, str.length() - 1));
            case 'L':
                return Long.parseLong(str.substring(0, str.length() - 1));
            case 'F':
                return Float.parseFloat(str.substring(0, str.length() - 1));
            default:
                if (str.indexOf('.') >= 0) {
                    return Double.parseDouble(str);
                }
                return Integer.parseInt(str);
            }
        }
    }

    //
    // Maps '.' characters to '/' characters in a string
    //
    public static String convertDots(String orig_name)
    {
        return convertChars(orig_name, ".", '/');
    }

    //
    // Maps chars to toChar in a given String
    //
    public static String convertChars(String orig_name,
                                      String chars, char toChar)
    {
        StringBuffer tmp = new StringBuffer(orig_name);
        int i;
        for (i = 0; i < tmp.length(); i++) {
            if (chars.indexOf(tmp.charAt(i)) != -1) {
                tmp.setCharAt(i, toChar);
            }
        }
        return new String(tmp);
    }

    //
    // Splits a string like:
    //     "a/b/c/d(xyz)v"
    // into three strings:
    //     "a/b/c", "d", "(xyz)v"
    //
    public static String[] splitClassMethodSignature(String name) {
        String result[] = new String[3];
        int i, pos = 0, sigpos = 0;
        for (i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '.' || c == '/') pos = i;
            else if (c == '(') {sigpos = i; break; }
        }
        try {
            result[0] = convertDots(name.substring(0, pos));
            result[1] = name.substring(pos + 1, sigpos);
            result[2] = convertDots(name.substring(sigpos));
        } catch(StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("malformed signature : "+name);
        }
        return result;
    }

    //
    // Splits a string like:
    //    "java/lang/System/out"
    // into two strings:
    //    "java/lang/System" and "out"
    //
    public static String[] splitClassField(String name)
    {
        String result[] = new String[2];
        int i, pos = -1, sigpos = 0;
        for (i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '.' || c == '/') pos = i;
        }
        if (pos == -1) {    // no '/' in string
            result[0] = null;
            result[1] = name;
        } else {
            result[0] = convertDots(name.substring(0, pos));
            result[1] = name.substring(pos + 1);
        }

        return result;
    }

    // Splits a string like:
    //      "main(Ljava/lang/String;)V
    // into two strings:
    //      "main" and "(Ljava/lang/String;)V"
    //
    public static String[] splitMethodSignature(String name)
    {
        String result[] = new String[2];
        int i, sigpos = 0;
        for (i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == '(') {sigpos = i; break; }
        }
        result[0] = name.substring(0, sigpos);
        result[1] = convertDots(name.substring(sigpos));

        return result;
    }

    /**
     * Computes the size of the arguments and of the return value of a method.
     * 
     * @param desc the descriptor of a method.
     * @return the size of the arguments of the method (plus one for the
     *         implicit this argument), argSize, and the size of its return
     *         value, retSize, packed into a single int i =
     *         <tt>(argSize << 2) | retSize</tt> (argSize is therefore equal
     *         to <tt>i >> 2</tt>, and retSize to <tt>i & 0x03</tt>).
     */
    public static int getArgumentsAndReturnSizes(final String desc) {
        int n = 1;
        int c = 1;
        while (true) {
            char car = desc.charAt(c++);
            if (car == ')') {
                car = desc.charAt(c);
                return n << 2
                        | (car == 'V' ? 0 : (car == 'D' || car == 'J' ? 2 : 1));
            } else if (car == 'L') {
                while (desc.charAt(c++) != ';') {
                }
                n += 1;
            } else if (car == '[') {
                while ((car = desc.charAt(c)) == '[') {
                    ++c;
                }
                if (car == 'D' || car == 'J') {
                    n -= 1;
                }
            } else if (car == 'D' || car == 'J') {
                n += 2;
            } else {
                n += 1;
            }
        }
    }
}

/* --- Revision History ---------------------------------------------------
--- Panxiaobo, Feb 15 2012
    add support to ldc infinity and Nan
--- Panxiaobo, Feb 14 2012
    'D'/'F'/'L' in real constant, force double/float/long mode.
--- Iouri Kharon, May 07 2010
    'd' in real constant, force double mode.
*/
