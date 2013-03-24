/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package fr.lifl.jaskell.runtime.modules;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import fr.lifl.jaskell.runtime.types.JObject;
import fr.lifl.jaskell.runtime.types.JValue;
import fr.lifl.jaskell.runtime.types.Tuple_Int_Int;


class JaskellError extends Error {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    JaskellError(String msg) {
        super(msg);
    }

}

/**
 * Standard functions definitions for Jaskell This class contains static functions defining primitives Prelude
 * functions, that is functions which need to be defined in Java, not in Jaskell. Most functions are overloaded.
 *
 * @author  bailly
 * @version $Id: Prelude.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Prelude {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final BufferedReader rdin = new BufferedReader(new InputStreamReader(System.in));

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * (==) :: Int -> Int -> Bool
     */
    public static boolean _3d_3d(int a, int b) {
        return a == b;
    }

    /**
     * (/=) :: Int -> Int -> Bool
     */
    public static boolean _2f_3d(int a, int b) {
        return a != b;
    }

    /**
     * (<=) :: int -> int -> Bool
     */
    public static boolean _3e_3d(int a, int b) {
        return a <= b;
    }

    /**
     * (<) :: int -> int -> Bool
     */
    public static boolean _3e(int a, int b) {
        return a < b;
    }

    /**
     * (>=) :: int -> int -> Bool
     */
    public static boolean _3c_3d(int a, int b) {
        return a >= b;
    }

    /**
     * (>) :: int -> int -> Bool
     */
    public static boolean _3c(int a, int b) {
        return a > b;
    }

    /**
     * max :: int -> int -> int
     */
    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }

    /**
     * min :: int -> int -> Ordering
     */
    public static int min(int a, int b) {
        return (a < b) ? a : b;
    }

    /**
     * (+) :: int -> int -> int
     */
    public static int _2b(int a, int b) {
        return a + b;
    }

    /**
     * (-) :: int -> int -> int
     */
    public static int _2d(int a, int b) {
        return a - b;
    }

    /**
     * (*) :: int -> int -> int
     */
    public static int _2a(int a, int b) {
        return a * b;
    }

    /**
     * negate :: int -> int
     */
    public static int negate(int a) {
        return -a;
    }

    /**
     * abs :: int -> int
     */
    public static int abs(int a) {
        return (a > 0) ? a : -a;
    }

    /**
     * signum :: int -> int
     */
    public static int signum(int a) {
        return (a > 0) ? 1 : ((a == 0) ? 0 : -1);
    }

    /**
     * fromInteger :: Integer -> int
     */

    /**
     * toRational :: int -> Rational
     */

    /**
     * quot :: int -> int -> int
     */
    public static int quot(int a, int b) {
        return a / b;
    }

    /**
     * rem :: int -> int -> int
     */
    public static int rem(int a, int b) {
        return a % b;
    }

    /**
     * div :: int-> int -> int
     */

    /**
     * mod :: int-> int -> int
     */

    /**
     * quotRem :: int-> int -> (int,int)
     */
    public static Tuple_Int_Int quotRem(int a, int b) {
        return new Tuple_Int_Int(a / b, a % b);
    }

    /**
     * divMod :: int-> int -> (int,int)
     */

    /**
     * toInteger :: int -> Integer
     */

    /**
     * succ :: int -> int
     */
    public static int succ(int a) {
        return a + 1;
    }

    /**
     * pred :: int -> int
     */
    public static int pred(int a) {
        return a - 1;
    }

    /**
     * toEnum :: int -> int
     */
    public static int toEnum(int a) {
        return a;
    }

    /**
     * fromEnum :: int -> int
     */
    public static int fromEnum(int a) {
        return a;
    }

    /**
     * minBound :: int
     */
    public static int minBound() {
        return Integer.MIN_VALUE;
    }

    /**
     * minBound :: int
     */
    public static int maxBound() {
        return Integer.MAX_VALUE;
    }

    public static JObject error(String msg) {
        return primError(msg);
    }

    public static JObject ifThenElse(JObject cond, JObject iftrue, JObject iffalse) {
        boolean b = ((JValue) cond.eval()).asBool();
        if (b)
            return iftrue.eval();
        else
            return iffalse.eval();
    }

    /**
     * Sequence function. Evaluates both arguments one after another and returns the second one.
     *
     * @param  a
     * @param  b
     *
     * @return
     */
    public static JObject seq(JObject a, JObject b) {
        a.eval();
        return b.eval();
    }

    /**
     * (^^) :: Bool -> Bool -> Bool
     */
    public static boolean _5e_5e(boolean a, boolean b) {
        return a ^ b;
    }

    /**
     * not :: Bool-> Bool
     *
     * @param  a
     *
     * @return
     */
    public static boolean not(boolean a) {
        return !a;
    }

    private static JObject primError(String msg) {
        throw new JaskellError(msg);
    }

}
