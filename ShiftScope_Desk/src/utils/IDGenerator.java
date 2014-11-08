package utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 *
 * @author carlos
 */
public class IDGenerator {

    private static SecureRandom random = new SecureRandom();

    public static String nextResourceId() {
        return new BigInteger(130, random).toString(8);
    }

    public static int nextId() {
        return Integer.parseInt(new BigInteger(10, random).toString());
    }
}
