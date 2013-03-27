/**
 *  Copyright Murex S.A.S., 2003-2013. All Rights Reserved.
 * 
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
//### This file created by BYACC 1.8(/Java extension  1.1)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//### Please send bug reports to rjamison@lincom-asg.com
//### static char yysccsid[] = "@(#)yaccpar     1.8 (Berkeley) 01/20/90";
package fr.lifl.jaskell.parser;

//#line 47 "jaskell-core.y"

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.util.*;

import fr.lifl.jaskell.compiler.bytecode.PrimitivesCodeGenerator;
import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;
import fr.lifl.jaskell.compiler.types.*;
import fr.lifl.parsing.*;

//#line 36 "Yyparser.java"


/**
 * Encapsulates yacc() parser functionality in a Java class for quick code development
 */
public class Yyparser implements fr.lifl.parsing.Parser {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

//########## STATE STACK ##########
    static final int YYSTACKSIZE = 500; //maximum stack size
//#### end semantic value section ####
    public static final short QUALIFIED = 257;
    public static final short QCONID = 258;
    public static final short CONID = 259;
    public static final short QVARID = 260;
    public static final short VARID = 261;
    public static final short QVARSYM = 262;
    public static final short VARSYM = 263;
    public static final short QCONSYM = 264;
    public static final short CONSYM = 265;
    public static final short INTEGER = 266;
    public static final short FLOAT = 267;
    public static final short CHAR = 268;
    public static final short STRING = 269;
    public static final short BOOLEAN = 270;
    public static final short MODULE = 271;
    public static final short WHERE = 272;
    public static final short TYPE = 273;
    public static final short DATA = 274;
    public static final short NEWTYPE = 275;
    public static final short CLASS = 276;
    public static final short INSTANCE = 277;
    public static final short DEFAULT = 278;
    public static final short DERIVING = 279;
    public static final short AS = 280;
    public static final short IMPORT = 281;
    public static final short LET = 282;
    public static final short IN = 283;
    public static final short IF = 284;
    public static final short THEN = 285;
    public static final short ELSE = 286;
    public static final short CASE = 287;
    public static final short OF = 288;
    public static final short DO = 289;
    public static final short AT = 290;
    public static final short IRREF = 291;
    public static final short EQ = 292;
    public static final short ENUM = 293;
    public static final short L_BRACE = 294;
    public static final short R_BRACE = 295;
    public static final short L_PAREN = 296;
    public static final short R_PAREN = 297;
    public static final short L_BRACKET = 298;
    public static final short R_BRACKET = 299;
    public static final short L_ANGLE = 300;
    public static final short R_ANGLE = 301;
    public static final short SEMICOLON = 302;
    public static final short COMMA = 303;
    public static final short COLON = 304;
    public static final short DOUBLECOLON = 305;
    public static final short CONTEXT_OP = 306;
    public static final short FUNOP = 307;
    public static final short QUALOP = 308;
    public static final short STRICT = 309;
    public static final short MINUS = 310;
    public static final short BAR = 311;
    public static final short INFIXL = 312;
    public static final short INFIXR = 313;
    public static final short INFIX = 314;
    public static final short LAMBDA = 315;
    public static final short WILDCARD = 316;
    public static final short INFIXOP = 317;
    public static final short OPENPRAGMA = 318;
    public static final short CLOSEPRAGMA = 319;
    public static final short OPENCOMMENT = 320;
    public static final short CLOSECOMMENT = 321;
    public static final short NATIVE = 322;
    public static final short POINT = 323;
    public static final short QOP = 324;
    public static final short UMINUS = 325;
    public static final short YYERRCODE = 256;
    static final short[] yylhs = {
            -1,
            2, 0, 4, 0, 3, 5, 5, 7, 7, 7,
            7, 6, 6, 6, 6, 12, 12, 13, 13, 13,
            13, 10, 10, 18, 18, 18, 15, 15, 22, 22,
            20, 20, 8, 8, 8, 11, 27, 27, 9, 17,
            17, 23, 23, 14, 14, 14, 14, 14, 30, 30,
            28, 28, 28, 28, 28, 24, 24, 25, 33, 34,
            34, 34, 36, 36, 37, 37, 37, 37, 37, 42,
            42, 38, 38, 38, 38, 38, 38, 38, 38, 38,
            38, 38, 38, 38, 38, 48, 48, 47, 41, 41,
            49, 40, 40, 39, 26, 26, 51, 31, 31, 52,
            52, 52, 52, 52, 52, 52, 52, 52, 54, 54,
            53, 55, 55, 55, 57, 57, 56, 56, 56, 61,
            61, 58, 58, 62, 62, 59, 59, 59, 59, 63,
            63, 64, 65, 60, 60, 44, 44, 45, 1, 1,
            16, 16, 43, 43, 66, 32, 32, 67, 67, 21,
            21, 50, 50, 35, 35, 35, 35, 35, 68, 68,
            19, 19, 19, 29, 46, 46, 46, 46, 46,
        };
    static final short[] yylen = {
            2,
            0, 7, 0, 2, 1, 3, 1, 2, 3, 3,
            1, 4, 1, 1, 1, 3, 2, 4, 5, 6,
            5, 3, 1, 1, 2, 3, 2, 1, 2, 1,
            1, 2, 2, 2, 1, 3, 3, 1, 1, 3,
            1, 2, 1, 1, 1, 6, 3, 3, 3, 0,
            1, 2, 2, 3, 3, 2, 3, 2, 1, 3,
            1, 1, 2, 3, 5, 4, 6, 6, 1, 2,
            1, 1, 1, 1, 1, 1, 3, 5, 4, 6,
            5, 5, 4, 4, 4, 2, 0, 2, 3, 1,
            3, 2, 0, 1, 3, 1, 1, 2, 0, 1,
            1, 2, 1, 1, 3, 5, 4, 2, 3, 0,
            2, 2, 3, 4, 3, 3, 2, 2, 2, 1,
            1, 1, 1, 1, 1, 1, 1, 2, 0, 3,
            1, 2, 1, 4, 1, 2, 2, 1, 1, 1,
            1, 3, 1, 3, 1, 1, 3, 1, 3, 1,
            3, 1, 3, 1, 1, 1, 1, 1, 1, 1,
            1, 3, 1, 1, 1, 1, 1, 1, 1,
        };
    static final short[] yydefred = {
            0,
            0, 0, 0, 140, 139, 1, 35, 161, 163, 141,
            165, 166, 167, 168, 169, 0, 0, 0, 0, 104,
            0, 4, 0, 7, 13, 14, 15, 0, 145, 0,
            0, 0, 101, 99, 103, 96, 97, 138, 0, 164,
            0, 0, 0, 43, 39, 51, 0, 44, 45, 0,
            0, 0, 123, 0, 122, 108, 0, 0, 0, 160,
            136, 159, 100, 0, 0, 0, 137, 110, 17, 0,
            0, 0, 0, 0, 33, 146, 0, 34, 0, 0,
            152, 0, 0, 0, 0, 52, 0, 0, 0, 53,
            0, 0, 0, 42, 121, 120, 118, 117, 119, 0,
            0, 112, 0, 0, 142, 0, 105, 0, 110, 162,
            0, 0, 0, 0, 0, 0, 16, 6, 98, 143,
            0, 0, 0, 0, 0, 0, 0, 73, 58, 0,
            0, 62, 71, 0, 72, 74, 75, 76, 0, 0,
            0, 0, 37, 36, 0, 55, 54, 48, 0, 47,
            0, 0, 23, 0, 0, 0, 40, 0, 133, 0,
            0, 127, 131, 125, 124, 0, 0, 0, 113, 111,
            0, 107, 0, 0, 0, 0, 0, 0, 11, 0,
            0, 0, 0, 0, 150, 0, 0, 156, 154, 0,
            0, 0, 157, 155, 0, 0, 63, 93, 148, 158,
            0, 0, 70, 153, 147, 0, 50, 32, 0, 0,
            30, 0, 28, 0, 0, 132, 115, 0, 128, 116,
            0, 114, 106, 109, 18, 0, 0, 8, 0, 0,
            0, 0, 0, 144, 0, 0, 77, 0, 87, 0,
            0, 0, 0, 0, 0, 0, 0, 2, 0, 22,
            29, 27, 26, 130, 0, 19, 0, 21, 9, 66,
            10, 0, 0, 151, 149, 88, 0, 84, 85, 83,
            0, 0, 79, 86, 0, 0, 46, 0, 134, 20,
            0, 0, 0, 90, 78, 82, 81, 0, 65, 49,
            67, 0, 68, 0, 80, 91, 89,
        };
    static final short[] yydgoto = {
            2,
            6, 39, 22, 3, 23, 24, 178, 25, 43, 152,
            26, 27, 71, 44, 212, 128, 45, 153, 29, 155,
            188, 213, 47, 30, 75, 64, 32, 48, 49, 249,
            73, 189, 129, 130, 201, 131, 132, 133, 159, 245,
            283, 134, 135, 33, 34, 35, 274, 244, 284, 193,
            36, 37, 109, 111, 56, 57, 102, 58, 160, 166,
            97, 167, 161, 162, 163, 38, 194, 81,
        };
    static final short[] yysindex = {
            -233,
            -64, 0, -99, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 425, -96, 624, 539, 0,
            -267, 0, -230, 0, 0, 0, 0, 0, 0, -211,
            -229, -212, 0, 0, 0, 0, 0, 0, -150, 0,
            411, 421, -162, 0, 0, 0, -57, 0, 0, -86,
            -252, -252, 0, -252, 0, 0, -206, -275, -114, 0,
            0, 0, 0, -251, -260, -109, 0, 0, 0, 329,
            -129, -99, 745, 900, 0, 0, -74, 0, 745, 745,
            0, -236, 425, 745, -98, 0, -94, -87, -194, 0,
            -101, 74, 425, 0, 0, 0, 0, 0, 0, -36,
            -246, 0, -36, -206, 0, -37, 0, 745, 0, 0,
            -197, 0, 425, 370, -78, -81, 0, 0, 0, 0,
            394, 900, 900, 476, 764, 1063, 745, 0, 0, 302,
            302, 0, 0, 1063, 0, 0, 0, 0, -77, -73,
            -251, -251, 0, 0, -99, 0, 0, 0, 425, 0,
            425, -76, 0, 239, -244, 425, 0, 745, 0, -61,
            -151, 0, 0, 0, 0, -58, -53, -62, 0, 0,
            -192, 0, -56, -18, 425, 425, 168, -253, 0, 0,
            -31, -32, -40, -114, 0, 1063, 16, 0, 0, -180,
            302, 900, 0, 0, -109, -219, 0, 0, 0, 0,
            900, 900, 0, 0, 0, -34, 0, 0, 74, 425,
            0, 239, 0, -1, 74, 0, 0, 745, 0, 0,
            745, 0, 0, 0, 0, -80, -2, 0, -217, 900,
            435, 900, -24, 0, -45, -39, 0, 900, 0, 798,
            185, 832, -14, -178, 689, 302, 302, 0, -177, 0,
            0, 0, 0, 0, -145, 0, 227, 0, 0, 0,
            0, -5, 745, 0, 0, 0, -158, 0, 0, 0,
            -17, 866, 0, 0, 900, -251, 0, 425, 0, 0,
            900, -22, -203, 0, 0, 0, 0, -9, 0, 0,
            0, 900, 0, 745, 0, 0, 0,
        };
    static final short[] yyrindex = {
            286,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 2, 0, 0, 0, 0, 498, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 119, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 638, 0, 0, 0, 0, 0, 0,
            0, 0, -4, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 1011, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 5,
            0, 0, -7, 0, 0, 0, 0, 0, 0, 0,
            0, -266, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 41,
            62, 0, 0, 1, 0, 0, 0, 0, 0, 0,
            3, 573, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 12, 0, 4, 0, 6, 0, 0, 0, 0,
            -156, 0, 0, 0, 0, 0, -8, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 558,
            0, 0, 934, 968, 0, -102, 0, 0, 0, 0,
            -157, 0, 0, 0, 1002, 11, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 14, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 83, 104, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 702, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0,
        };
    static final short[] yygindex = {
            0,
            0, 0, 167, 0, 0, 241, 142, -116, -59, 0,
            0, 0, 0, -19, 94, 28, -25, 121, 363, 113,
            176, -190, -82, 0, 306, -3, 0, 0, 0, 0,
            305, 310, -66, -106, -104, 0, 0, -115, -11, 0,
            0, 0, 0, 319, 384, 406, -46, 103, 52, -28,
            0, -44, 178, 240, 0, 294, 249, 0, 251, 0,
            109, 0, 135, 197, 144, 0, 0, 24,
        };
    static final int YYTABLESIZE = 1361;
    static final short[] yytable = {
            31,
            69, 5, 80, 24, 179, 31, 65, 68, 10, 156,
            197, 12, 60, 25, 10, 89, 91, 191, 203, 192,
            185, 252, 103, 144, 10, 164, 202, 94, 119, 230,
            28, 51, 52, 76, 60, 80, 107, 1, 141, 119,
            59, 66, 108, 50, 55, 63, 63, 54, 231, 50,
            115, 69, 62, 174, 70, 181, 182, 190, 196, 50,
            179, 61, 74, 95, 66, 106, 252, 157, 31, 164,
            197, 72, 214, 242, 62, 141, 142, 259, 96, 96,
            74, 96, 60, 238, 231, 241, 240, 77, 89, 100,
            82, 293, 83, 101, 246, 247, 170, 116, 294, 28,
            63, 172, 148, 64, 223, 173, 63, 63, 149, 143,
            173, 63, 80, 80, 261, 198, 237, 31, 41, 277,
            273, 85, 238, 207, 238, 278, 156, 63, 165, 92,
            63, 208, 156, 246, 211, 63, 94, 66, 285, 59,
            126, 31, 126, 239, 238, 59, 216, 195, 180, 243,
            227, 218, 158, 279, 63, 226, 7, 218, 8, 9,
            98, 10, 99, 260, 10, 262, 11, 12, 13, 14,
            15, 266, 28, 31, 16, 271, 59, 8, 9, 158,
            40, 158, 105, 139, 158, 63, 140, 110, 256, 117,
            251, 17, 211, 4, 5, 145, 18, 150, 19, 50,
            8, 9, 146, 40, 180, 288, 211, 158, 289, 147,
            51, 52, 158, 175, 291, 41, 20, 42, 21, 53,
            139, 8, 9, 176, 10, 296, 54, 31, 210, 11,
            12, 13, 14, 15, 209, 217, 222, 211, 41, 204,
            42, 276, 220, 205, 221, 63, 108, 80, 63, 93,
            225, 282, 290, 232, 17, 233, 234, 235, 180, 18,
            248, 19, 69, 69, 69, 69, 258, 158, 51, 263,
            31, 264, 63, 139, 235, 236, 140, 265, 272, 20,
            281, 286, 282, 69, 292, 69, 69, 56, 69, 295,
            63, 129, 135, 69, 57, 69, 5, 69, 24, 69,
            31, 129, 69, 69, 69, 24, 12, 31, 25, 87,
            69, 206, 118, 12, 24, 25, 31, 69, 229, 257,
            51, 63, 31, 59, 25, 59, 59, 253, 59, 250,
            215, 8, 9, 59, 40, 59, 78, 59, 84, 59,
            79, 267, 59, 59, 61, 297, 61, 61, 171, 61,
            224, 104, 169, 168, 61, 255, 61, 219, 61, 0,
            61, 254, 0, 61, 61, 60, 0, 60, 60, 41,
            60, 42, 0, 0, 0, 60, 0, 60, 46, 60,
            0, 60, 151, 0, 60, 60, 64, 41, 64, 64,
            0, 64, 136, 0, 0, 0, 64, 0, 64, 0,
            64, 0, 64, 46, 46, 64, 64, 0, 0, 46,
            41, 0, 0, 41, 0, 41, 0, 41, 0, 0,
            41, 41, 0, 7, 0, 8, 9, 0, 10, 0,
            0, 0, 46, 11, 12, 13, 14, 15, 0, 0,
            136, 136, 136, 136, 136, 46, 199, 76, 60, 185,
            0, 0, 136, 0, 154, 46, 0, 137, 17, 0,
            0, 177, 228, 18, 0, 19, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 46, 46, 0, 0, 138,
            0, 269, 0, 20, 8, 9, 0, 40, 62, 0,
            0, 0, 0, 0, 200, 280, 8, 9, 0, 40,
            0, 187, 0, 0, 136, 137, 137, 137, 137, 137,
            136, 46, 0, 46, 0, 0, 46, 137, 46, 136,
            136, 0, 41, 0, 42, 0, 0, 138, 138, 138,
            138, 138, 0, 0, 41, 210, 42, 46, 46, 138,
            0, 3, 0, 3, 3, 0, 3, 210, 136, 0,
            136, 3, 3, 3, 3, 3, 136, 0, 136, 3,
            136, 0, 0, 199, 76, 60, 185, 0, 0, 137,
            0, 154, 46, 0, 46, 137, 3, 46, 0, 0,
            0, 3, 0, 3, 137, 137, 8, 9, 46, 112,
            136, 138, 0, 136, 0, 0, 0, 138, 0, 136,
            0, 3, 113, 3, 0, 62, 138, 138, 0, 0,
            136, 200, 0, 137, 0, 137, 0, 0, 187, 46,
            0, 137, 0, 137, 114, 137, 42, 8, 9, 0,
            40, 0, 59, 60, 0, 138, 0, 138, 0, 0,
            46, 0, 0, 138, 0, 138, 0, 138, 0, 7,
            0, 8, 9, 0, 10, 137, 0, 0, 137, 11,
            12, 13, 14, 15, 137, 41, 86, 42, 8, 9,
            0, 40, 87, 62, 60, 137, 88, 138, 8, 9,
            138, 40, 8, 9, 17, 40, 138, 177, 0, 18,
            7, 19, 8, 9, 0, 10, 0, 138, 0, 0,
            11, 12, 13, 14, 15, 0, 41, 86, 42, 20,
            0, 0, 0, 87, 62, 0, 41, 88, 42, 90,
            41, 0, 42, 0, 0, 17, 0, 0, 0, 0,
            18, 0, 19, 8, 9, 120, 10, 183, 184, 60,
            185, 11, 12, 13, 14, 15, 0, 0, 0, 0,
            20, 0, 0, 0, 0, 99, 99, 121, 99, 122,
            100, 100, 123, 99, 99, 99, 99, 99, 0, 0,
            0, 124, 61, 125, 0, 0, 0, 0, 0, 62,
            0, 0, 0, 0, 0, 186, 0, 0, 99, 99,
            127, 0, 187, 99, 0, 99, 8, 9, 0, 10,
            38, 100, 38, 0, 11, 12, 13, 14, 15, 0,
            0, 0, 0, 99, 100, 99, 99, 0, 99, 0,
            100, 100, 0, 99, 99, 99, 99, 99, 0, 17,
            95, 95, 0, 95, 18, 95, 19, 67, 95, 95,
            95, 95, 95, 0, 0, 0, 0, 0, 99, 99,
            0, 0, 0, 99, 20, 99, 0, 0, 0, 0,
            0, 100, 0, 95, 95, 0, 0, 0, 95, 95,
            95, 95, 0, 99, 100, 95, 0, 0, 0, 95,
            0, 8, 9, 0, 10, 0, 59, 60, 95, 11,
            12, 13, 14, 15, 0, 94, 94, 0, 94, 0,
            0, 0, 0, 94, 94, 94, 94, 94, 0, 0,
            0, 0, 0, 0, 17, 0, 0, 0, 0, 18,
            61, 19, 0, 0, 0, 0, 0, 62, 94, 0,
            0, 0, 0, 94, 94, 94, 94, 0, 0, 20,
            94, 0, 0, 0, 94, 0, 8, 9, 0, 10,
            0, 0, 0, 94, 11, 12, 13, 14, 15, 92,
            92, 0, 92, 0, 0, 0, 0, 92, 92, 92,
            92, 92, 0, 0, 0, 0, 0, 0, 0, 17,
            0, 0, 0, 0, 18, 0, 19, 0, 0, 0,
            0, 0, 92, 0, 0, 275, 0, 92, 0, 92,
            0, 0, 8, 9, 20, 10, 0, 0, 92, 0,
            11, 12, 13, 14, 15, 0, 0, 92, 0, 0,
            0, 8, 9, 120, 10, 0, 0, 0, 0, 11,
            12, 13, 14, 15, 0, 17, 0, 0, 0, 0,
            18, 0, 19, 0, 0, 121, 0, 122, 0, 0,
            123, 0, 0, 0, 0, 8, 9, 120, 10, 124,
            20, 125, 67, 11, 12, 13, 14, 15, 0, 0,
            0, 0, 0, 126, 0, 0, 0, 0, 127, 121,
            0, 122, 0, 0, 123, 0, 0, 0, 0, 8,
            9, 120, 10, 124, 268, 125, 0, 11, 12, 13,
            14, 15, 0, 0, 0, 0, 0, 126, 0, 0,
            0, 0, 127, 121, 0, 122, 0, 0, 123, 0,
            0, 0, 0, 8, 9, 120, 10, 124, 0, 125,
            270, 11, 12, 13, 14, 15, 0, 0, 0, 0,
            0, 126, 0, 0, 0, 0, 127, 121, 0, 122,
            0, 0, 123, 0, 0, 0, 0, 8, 9, 120,
            10, 124, 0, 125, 287, 11, 12, 13, 14, 15,
            0, 0, 0, 0, 0, 126, 0, 0, 0, 0,
            127, 121, 0, 122, 0, 0, 123, 0, 0, 0,
            0, 148, 148, 148, 148, 124, 0, 125, 0, 148,
            148, 148, 148, 148, 0, 0, 0, 0, 0, 126,
            0, 0, 0, 0, 127, 148, 0, 148, 0, 0,
            148, 0, 0, 0, 0, 146, 146, 146, 146, 148,
            0, 148, 0, 146, 146, 146, 146, 146, 0, 0,
            0, 0, 0, 148, 0, 0, 0, 0, 148, 146,
            0, 146, 0, 0, 146, 0, 0, 0, 0, 152,
            152, 152, 152, 146, 0, 146, 0, 152, 152, 152,
            152, 152, 0, 102, 102, 0, 0, 146, 0, 0,
            0, 0, 146, 152, 0, 152, 0, 0, 152, 0,
            0, 0, 0, 0, 0, 0, 0, 152, 0, 152,
            0, 0, 102, 0, 0, 0, 0, 102, 0, 102,
            0, 152, 0, 102, 102, 0, 152, 102, 0, 0,
            8, 9, 120, 10, 0, 0, 0, 102, 11, 12,
            13, 14, 15, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 124, 0,
            125,
        };
    static final short[] yycheck = {
            3,
            0, 0, 31, 0, 121, 0, 18, 19, 261, 92,
            126, 0, 264, 0, 261, 41, 42, 124, 134, 124,
            265, 212, 298, 83, 261, 292, 131, 47, 73, 283,
            3, 307, 308, 263, 264, 64, 297, 271, 305, 84,
            0, 18, 303, 296, 17, 18, 19, 323, 302, 296,
            70, 319, 304, 113, 322, 122, 123, 124, 125, 296,
            177, 0, 292, 316, 41, 317, 257, 93, 72, 316,
            186, 302, 317, 293, 304, 79, 80, 295, 51, 52,
            292, 54, 0, 303, 302, 192, 191, 317, 114, 296,
            303, 295, 305, 300, 201, 202, 108, 70, 302, 72,
            73, 299, 297, 0, 297, 303, 79, 80, 303, 82,
            303, 84, 141, 142, 231, 127, 297, 121, 0, 297,
            299, 272, 303, 149, 303, 303, 209, 100, 101, 292,
            103, 151, 215, 240, 154, 108, 156, 114, 297, 297,
            297, 145, 299, 190, 303, 303, 158, 124, 121, 196,
            176, 303, 304, 299, 127, 175, 256, 303, 258, 259,
            52, 261, 54, 230, 261, 232, 266, 267, 268, 269,
            270, 238, 145, 177, 274, 242, 263, 258, 259, 282,
            261, 284, 297, 258, 287, 158, 261, 297, 269, 319,
            210, 291, 212, 258, 259, 294, 296, 299, 298, 296,
            258, 259, 297, 261, 177, 272, 226, 310, 275, 297,
            307, 308, 315, 292, 281, 296, 316, 298, 318, 316,
            258, 258, 259, 305, 261, 292, 323, 231, 309, 266,
            267, 268, 269, 270, 311, 297, 299, 257, 296, 317,
            298, 245, 301, 317, 298, 218, 303, 276, 221, 307,
            269, 263, 278, 285, 291, 288, 297, 259, 231, 296,
            295, 298, 262, 263, 264, 265, 269, 304, 265, 294,
            265, 317, 245, 258, 259, 260, 261, 317, 293, 316,
            286, 299, 294, 283, 307, 285, 286, 292, 288, 299,
            263, 299, 301, 293, 292, 295, 295, 297, 295, 299,
            295, 297, 302, 303, 304, 302, 295, 302, 295, 299,
            310, 145, 72, 302, 311, 302, 311, 317, 177, 226,
            317, 294, 317, 283, 311, 285, 286, 215, 288, 209,
            155, 258, 259, 293, 261, 295, 31, 297, 34, 299,
            31, 239, 302, 303, 283, 294, 285, 286, 109, 288,
            173, 58, 104, 103, 293, 221, 295, 161, 297, -1,
            299, 218, -1, 302, 303, 283, -1, 285, 286, 296,
            288, 298, -1, -1, -1, 293, -1, 295, 16, 297,
            -1, 299, 309, -1, 302, 303, 283, 269, 285, 286,
            -1, 288, 74, -1, -1, -1, 293, -1, 295, -1,
            297, -1, 299, 41, 42, 302, 303, -1, -1, 47,
            292, -1, -1, 295, -1, 297, -1, 299, -1, -1,
            302, 303, -1, 256, -1, 258, 259, -1, 261, -1,
            -1, -1, 70, 266, 267, 268, 269, 270, -1, -1,
            122, 123, 124, 125, 126, 83, 262, 263, 264, 265,
            -1, -1, 134, -1, 92, 93, -1, 74, 291, -1,
            -1, 294, 295, 296, -1, 298, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, 113, 114, -1, -1, 74,
            -1, 297, -1, 316, 258, 259, -1, 261, 304, -1,
            -1, -1, -1, -1, 310, 269, 258, 259, -1, 261,
            -1, 317, -1, -1, 186, 122, 123, 124, 125, 126,
            192, 149, -1, 151, -1, -1, 154, 134, 156, 201,
            202, -1, 296, -1, 298, -1, -1, 122, 123, 124,
            125, 126, -1, -1, 296, 309, 298, 175, 176, 134,
            -1, 256, -1, 258, 259, -1, 261, 309, 230, -1,
            232, 266, 267, 268, 269, 270, 238, -1, 240, 274,
            242, -1, -1, 262, 263, 264, 265, -1, -1, 186,
            -1, 209, 210, -1, 212, 192, 291, 215, -1, -1,
            -1, 296, -1, 298, 201, 202, 258, 259, 226, 261,
            272, 186, -1, 275, -1, -1, -1, 192, -1, 281,
            -1, 316, 274, 318, -1, 304, 201, 202, -1, -1,
            292, 310, -1, 230, -1, 232, -1, -1, 317, 257,
            -1, 238, -1, 240, 296, 242, 298, 258, 259, -1,
            261, -1, 263, 264, -1, 230, -1, 232, -1, -1,
            278, -1, -1, 238, -1, 240, -1, 242, -1, 256,
            -1, 258, 259, -1, 261, 272, -1, -1, 275, 266,
            267, 268, 269, 270, 281, 296, 297, 298, 258, 259,
            -1, 261, 303, 304, 264, 292, 307, 272, 258, 259,
            275, 261, 258, 259, 291, 261, 281, 294, -1, 296,
            256, 298, 258, 259, -1, 261, -1, 292, -1, -1,
            266, 267, 268, 269, 270, -1, 296, 297, 298, 316,
            -1, -1, -1, 303, 304, -1, 296, 307, 298, 299,
            296, -1, 298, -1, -1, 291, -1, -1, -1, -1,
            296, -1, 298, 258, 259, 260, 261, 262, 263, 264,
            265, 266, 267, 268, 269, 270, -1, -1, -1, -1,
            316, -1, -1, -1, -1, 258, 259, 282, 261, 284,
            263, 264, 287, 266, 267, 268, 269, 270, -1, -1,
            -1, 296, 297, 298, -1, -1, -1, -1, -1, 304,
            -1, -1, -1, -1, -1, 310, -1, -1, 291, 292,
            315, -1, 317, 296, -1, 298, 258, 259, -1, 261,
            303, 304, 305, -1, 266, 267, 268, 269, 270, -1,
            -1, -1, -1, 316, 317, 258, 259, -1, 261, -1,
            263, 264, -1, 266, 267, 268, 269, 270, -1, 291,
            258, 259, -1, 261, 296, 263, 298, 299, 266, 267,
            268, 269, 270, -1, -1, -1, -1, -1, 291, 292,
            -1, -1, -1, 296, 316, 298, -1, -1, -1, -1,
            -1, 304, -1, 291, 292, -1, -1, -1, 296, 297,
            298, 299, -1, 316, 317, 303, -1, -1, -1, 307,
            -1, 258, 259, -1, 261, -1, 263, 264, 316, 266,
            267, 268, 269, 270, -1, 258, 259, -1, 261, -1,
            -1, -1, -1, 266, 267, 268, 269, 270, -1, -1,
            -1, -1, -1, -1, 291, -1, -1, -1, -1, 296,
            297, 298, -1, -1, -1, -1, -1, 304, 291, -1,
            -1, -1, -1, 296, 297, 298, 299, -1, -1, 316,
            303, -1, -1, -1, 307, -1, 258, 259, -1, 261,
            -1, -1, -1, 316, 266, 267, 268, 269, 270, 258,
            259, -1, 261, -1, -1, -1, -1, 266, 267, 268,
            269, 270, -1, -1, -1, -1, -1, -1, -1, 291,
            -1, -1, -1, -1, 296, -1, 298, -1, -1, -1,
            -1, -1, 291, -1, -1, 307, -1, 296, -1, 298,
            -1, -1, 258, 259, 316, 261, -1, -1, 307, -1,
            266, 267, 268, 269, 270, -1, -1, 316, -1, -1,
            -1, 258, 259, 260, 261, -1, -1, -1, -1, 266,
            267, 268, 269, 270, -1, 291, -1, -1, -1, -1,
            296, -1, 298, -1, -1, 282, -1, 284, -1, -1,
            287, -1, -1, -1, -1, 258, 259, 260, 261, 296,
            316, 298, 299, 266, 267, 268, 269, 270, -1, -1,
            -1, -1, -1, 310, -1, -1, -1, -1, 315, 282,
            -1, 284, -1, -1, 287, -1, -1, -1, -1, 258,
            259, 260, 261, 296, 297, 298, -1, 266, 267, 268,
            269, 270, -1, -1, -1, -1, -1, 310, -1, -1,
            -1, -1, 315, 282, -1, 284, -1, -1, 287, -1,
            -1, -1, -1, 258, 259, 260, 261, 296, -1, 298,
            299, 266, 267, 268, 269, 270, -1, -1, -1, -1,
            -1, 310, -1, -1, -1, -1, 315, 282, -1, 284,
            -1, -1, 287, -1, -1, -1, -1, 258, 259, 260,
            261, 296, -1, 298, 299, 266, 267, 268, 269, 270,
            -1, -1, -1, -1, -1, 310, -1, -1, -1, -1,
            315, 282, -1, 284, -1, -1, 287, -1, -1, -1,
            -1, 258, 259, 260, 261, 296, -1, 298, -1, 266,
            267, 268, 269, 270, -1, -1, -1, -1, -1, 310,
            -1, -1, -1, -1, 315, 282, -1, 284, -1, -1,
            287, -1, -1, -1, -1, 258, 259, 260, 261, 296,
            -1, 298, -1, 266, 267, 268, 269, 270, -1, -1,
            -1, -1, -1, 310, -1, -1, -1, -1, 315, 282,
            -1, 284, -1, -1, 287, -1, -1, -1, -1, 258,
            259, 260, 261, 296, -1, 298, -1, 266, 267, 268,
            269, 270, -1, 263, 264, -1, -1, 310, -1, -1,
            -1, -1, 315, 282, -1, 284, -1, -1, 287, -1,
            -1, -1, -1, -1, -1, -1, -1, 296, -1, 298,
            -1, -1, 292, -1, -1, -1, -1, 297, -1, 299,
            -1, 310, -1, 303, 304, -1, 315, 307, -1, -1,
            258, 259, 260, 261, -1, -1, -1, 317, 266, 267,
            268, 269, 270, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, 296, -1,
            298,
        };
    static final short YYFINAL = 2;
    static final short YYMAXTOKEN = 325;
    static final String[] yyname = {
            "end-of-file", null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, "QUALIFIED", "QCONID", "CONID", "QVARID", "VARID", "QVARSYM", "VARSYM",
            "QCONSYM", "CONSYM", "INTEGER", "FLOAT", "CHAR", "STRING", "BOOLEAN", "MODULE", "WHERE",
            "TYPE", "DATA", "NEWTYPE", "CLASS", "INSTANCE", "DEFAULT", "DERIVING", "AS", "IMPORT",
            "LET", "IN", "IF", "THEN", "ELSE", "CASE", "OF", "DO", "AT", "IRREF", "EQ", "ENUM",
            "L_BRACE", "R_BRACE", "L_PAREN", "R_PAREN", "L_BRACKET", "R_BRACKET", "L_ANGLE",
            "R_ANGLE", "SEMICOLON", "COMMA", "COLON", "DOUBLECOLON", "CONTEXT_OP", "FUNOP",
            "QUALOP", "STRICT", "MINUS", "BAR", "INFIXL", "INFIXR", "INFIX", "LAMBDA", "WILDCARD",
            "INFIXOP", "OPENPRAGMA", "CLOSEPRAGMA", "OPENCOMMENT", "CLOSECOMMENT", "NATIVE",
            "POINT", "QOP", "UMINUS",
        };
    static final String[] yyrule = {
            "$accept : module",
            "$$1 :",
            "module : MODULE modid $$1 WHERE L_BRACE body R_BRACE",
            "$$2 :",
            "module : $$2 body",
            "body : topdecls",
            "topdecls : topdecls SEMICOLON topdecl",
            "topdecls : topdecl",
            "decls : L_BRACE R_BRACE",
            "decls : L_BRACE decls R_BRACE",
            "decls : decls SEMICOLON decl",
            "decls : decl",
            "topdecl : DATA optype EQ constrs",
            "topdecl : decl",
            "topdecl : vardecl",
            "topdecl : pragma",
            "pragma : OPENPRAGMA directive CLOSEPRAGMA",
            "pragma : OPENPRAGMA CLOSEPRAGMA",
            "directive : NATIVE DATA optype STRING",
            "directive : NATIVE atype EQ atype STRING",
            "directive : NATIVE atype EQ atype con_params STRING",
            "directive : NATIVE var DOUBLECOLON type STRING",
            "constrs : constrs BAR constr",
            "constrs : constr",
            "constr : tycon",
            "constr : tycon con_params",
            "constr : conoptype conop conoptype",
            "con_params : con_params con_param",
            "con_params : con_param",
            "con_param : STRICT atype",
            "con_param : atype",
            "conoptype : btype",
            "conoptype : STRICT atype",
            "decl : funlhs rhs",
            "decl : pat0n rhs",
            "decl : error",
            "vardecl : vars DOUBLECOLON optype",
            "vars : vars COMMA var",
            "vars : var",
            "optype : type",
            "type : btype FUNOP type",
            "type : btype",
            "btype : btype atype",
            "btype : atype",
            "atype : gtycon",
            "atype : tyvar",
            "atype : L_PAREN type COMMA type comma_types R_PAREN",
            "atype : L_BRACKET type R_BRACKET",
            "atype : L_PAREN type R_PAREN",
            "comma_types : comma_types COMMA type",
            "comma_types :",
            "gtycon : tycon",
            "gtycon : L_PAREN R_PAREN",
            "gtycon : L_BRACKET R_BRACKET",
            "gtycon : L_PAREN FUNOP R_PAREN",
            "gtycon : L_PAREN COMMA R_PAREN",
            "funlhs : var apats",
            "funlhs : pat0n varop pat0n",
            "rhs : EQ exp",
            "exp : exp0n",
            "exp0n : exp0n qop exp0n",
            "exp0n : lexp0",
            "exp0n : exp10n",
            "lexp0 : MINUS aexp",
            "lexp0 : lexp0 qop exp0n",
            "exp10n : LAMBDA pat pats FUNOP exp",
            "exp10n : LET decls IN exp",
            "exp10n : IF exp THEN exp ELSE exp",
            "exp10n : CASE exp OF L_BRACE alts R_BRACE",
            "exp10n : fexp",
            "fexp : fexp aexp",
            "fexp : aexp",
            "aexp : qvar",
            "aexp : var",
            "aexp : ucon",
            "aexp : gcon",
            "aexp : literal",
            "aexp : L_PAREN exp R_PAREN",
            "aexp : L_PAREN exp comma_expr comma_exprs R_PAREN",
            "aexp : L_BRACKET exp comma_exprs R_BRACKET",
            "aexp : L_BRACKET exp comma_expr ENUM exp R_BRACKET",
            "aexp : L_BRACKET exp comma_expr ENUM R_BRACKET",
            "aexp : L_BRACKET exp ENUM exp R_BRACKET",
            "aexp : L_BRACKET exp ENUM R_BRACKET",
            "aexp : L_PAREN exp0n qop R_PAREN",
            "aexp : L_PAREN qop exp0n R_PAREN",
            "comma_exprs : comma_exprs comma_expr",
            "comma_exprs :",
            "comma_expr : COMMA exp",
            "alts : alts SEMICOLON alt",
            "alts : alt",
            "alt : pat FUNOP exp",
            "pats : pats pat0n",
            "pats :",
            "pat : pat0n",
            "pat0n : pat0n qconop pat0n",
            "pat0n : pat10n",
            "pat10n : apat",
            "apats : apats apat",
            "apats :",
            "apat : var",
            "apat : ucon",
            "apat : gcon apats",
            "apat : literal",
            "apat : WILDCARD",
            "apat : L_PAREN pat R_PAREN",
            "apat : L_PAREN pat comma_pat comma_pats R_PAREN",
            "apat : L_BRACKET pat comma_pats R_BRACKET",
            "apat : IRREF message",
            "comma_pats : comma_pats COMMA comma_pat",
            "comma_pats :",
            "comma_pat : COMMA pat",
            "message : arrow_method_name message_content",
            "message : port_name arrow_method_name message_content",
            "message : port_name L_BRACKET message_parametres R_BRACKET",
            "message_content : L_PAREN message_parametres R_PAREN",
            "message_content : L_ANGLE exception R_ANGLE",
            "arrow_method_name : QUALOP method_name",
            "arrow_method_name : FUNOP method_name",
            "arrow_method_name : POINT method_name",
            "method_name : var",
            "method_name : WILDCARD",
            "port_name : var",
            "port_name : WILDCARD",
            "exception_name : var",
            "exception_name : WILDCARD",
            "message_parametres : list_message_parametres",
            "message_parametres : return_message",
            "message_parametres : list_message_parametres return_message",
            "message_parametres :",
            "list_message_parametres : list_message_parametres COMMA message_parametre",
            "list_message_parametres : message_parametre",
            "return_message : COLON pat",
            "message_parametre : pat",
            "exception : exception_name L_BRACKET list_message_parametres R_BRACKET",
            "exception : exception_name",
            "ucon : L_PAREN R_PAREN",
            "ucon : L_BRACKET R_BRACKET",
            "gcon : qcon",
            "modid : CONID",
            "modid : QCONID",
            "var : VARID",
            "var : L_PAREN VARSYM R_PAREN",
            "qvar : QVARID",
            "qvar : L_PAREN QVARSYM R_PAREN",
            "qcon : tycon",
            "varop : VARSYM",
            "varop : INFIXOP VARID INFIXOP",
            "qvarop : QVARSYM",
            "qvarop : INFIXOP QVARID INFIXOP",
            "conop : CONSYM",
            "conop : INFIXOP CONID INFIXOP",
            "qconop : gconsym",
            "qconop : INFIXOP QCONID INFIXOP",
            "qop : varop",
            "qop : qvarop",
            "qop : conop",
            "qop : qconop",
            "qop : MINUS",
            "gconsym : COLON",
            "gconsym : QCONSYM",
            "tycon : QCONID",
            "tycon : L_PAREN gconsym R_PAREN",
            "tycon : CONID",
            "tyvar : VARID",
            "literal : INTEGER",
            "literal : FLOAT",
            "literal : CHAR",
            "literal : STRING",
            "literal : BOOLEAN",
        };

//#line 1174 "jaskell-core.y"

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    boolean yydebug; //do I want debug output?
    int yynerrs; //number of errors so far
    int yyerrflag; //was there an error?
    int yychar; //the current working character
    int statestk[], stateptr; //state stack
    int stateptrmax; //highest index of stackptr
    int statemax; //state when highest index reached

//########## SEMANTIC VALUES ##########
//## **user defined:fr.lifl.jaskell.compiler.core.Expression
    String yytext; //user variable to return contextual strings
    fr.lifl.jaskell.compiler.core.Expression yyval; //used to return semantic vals from action routines
    fr.lifl.jaskell.compiler.core.Expression yylval; //the 'lval' (result) I got from yylex()
    fr.lifl.jaskell.compiler.core.Expression[] valstk;
    int valptr;

    /** the lexer we use */
    private Yylex lex;

    /** the current module */
    private Module module;

    /** the list of generated modules */
    private List modules = new ArrayList();

    /** a Map storing name to Equation list equations */
    private Map equations = new HashMap();

    /** constructor for strict parameters */
    private ConstructorDefinition constructor;

    /** identifies source file */
    private String sourcefile;

    /** line offset we are parsing from */
    private int startLine;

    /** column offset */
    private int startColumn;

    /** the reader */
    private java.io.Reader reader;

    /** handle listener objects */
    private ParserListenerDelegate listenerDelegate = new ParserListenerDelegate();

//The following are now global, to aid in error reporting
    int yyn; //next next thing to do
    int yym; //
    int yystate; //current parsing state from state table
    String yys; //current token string

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

//## Constructors ###############################################
    /**
     * Default constructor. Turn off with -Jnoconstruct .
     */
    public Yyparser() {
        //nothing to do
    }

    /**
     * Create a parser, setting the debug to true or false.
     *
     * @param debugMe true for debugging, false for no debug.
     */
    public Yyparser(boolean debugMe) {
        yydebug = debugMe;
    }
//###############################################################

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    public void setStartScope(fr.lifl.parsing.Namespace ns) {
        this.module = (Module) ns;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public List getModules() {
        return modules;
    }

    public Map getEquations() {
        return equations;
    }

    public List getEquation(String name) {
        return (List) equations.get(name);
    }

    public void addEquation(String name, Equation eq) {
        List l = (List) equations.get(name);
        if (l == null) {
            l = new ArrayList();
            equations.put(name, l);
        }
        l.add(eq);
    }

    public void setSourceFile(String sf) {
        this.sourcefile = sf;
    }

    public String getSourceFile() {
        return this.sourcefile;
    }

    public void setStartLine(int line) {
        this.startLine = line;
    }

    public void setStartPosition(ParserPosition pos) {
        this.startLine = pos.getLine();
        this.startColumn = pos.getColumn();
    }

    public void setReader(java.io.Reader reader) {
        this.reader = reader;
    }

    public void start() throws ParserException {
        if (reader == null)
            throw new ParserException("Nothing to parse !");
        parse(reader);
    }

    public void parse(String fname) {
        try {
            FileInputStream fis = new FileInputStream(fname);
            setSourceFile(fname);
            parse(fis);
        } catch (IOException ioex) {
            System.err.println("Invalid file name " + fname);
            throw new ParseException(ioex);
        }
    }

    public void addParserListener(ParserListener list) {
        listenerDelegate.addParserListener(list);
    }

    /**
     * Method parse.
     *
     * @param is
     */
    public void parse(InputStream is) {
        lex = new Yylex(is);
        /* init start line */
        lex.setLine(startLine);
        lex.setColumn(startColumn);
        lex.parser = this;
        /* start parsing */
        this.yyparse();
    }

    /**
     * Method parse.
     *
     * @param rd
     */
    public void parse(Reader rd) {
        lex = new Yylex(rd);
        /* init start line */
        lex.setLine(startLine);
        lex.setColumn(startColumn);
        lex.parser = this;
        /* start parsing */
        this.yyparse();
    }

    public int yylex() {
        try {
            return lex.yylex();
        } catch (IOException ioex) {
            throw new ParseException(ioex);
        }
    }

    public void setParserConfiguration(ParserConfiguration config) {
    }

    public void parseError(String msg) {
        ParserError er = new ParserError();
        er.setPosition(new ParserPosition(lex.getLine(), lex.getColumn()));
        er.setSource(this);
        er.setMessage(msg);
        listenerDelegate.notify(er);
    }

    public void yyerror(String msg) {
        lex.yyerror(msg);
    }

//## run() --- for Thread #######################################
    /**
     * A default run method, used for operating this parser object in the background. It is intended for extending
     * Thread or implementing Runnable. Turn off with -Jnorun .
     */
    public void run() {
        yyparse();
    }
//## end of method run() ########################################

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
    void debug(String msg) {
        if (yydebug)
            System.out.println(msg);
    }

//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
    void state_push(int state) {
        if (stateptr >= YYSTACKSIZE) //overflowed?
            return;
        statestk[++stateptr] = state;
        if (stateptr > statemax) {
            statemax = state;
            stateptrmax = stateptr;
        }
    }

    int state_pop() {
        if (stateptr < 0) //underflowed?
            return -1;
        return statestk[stateptr--];
    }

    void state_drop(int cnt) {
        int ptr;
        ptr = stateptr - cnt;
        if (ptr < 0)
            return;
        stateptr = ptr;
    }

    int state_peek(int relative) {
        int ptr;
        ptr = stateptr - relative;
        if (ptr < 0)
            return -1;
        return statestk[ptr];
    }

//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
    boolean init_stacks() {
        statestk = new int[YYSTACKSIZE];
        stateptr = -1;
        statemax = -1;
        stateptrmax = -1;
        val_init();
        return true;
    }

//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
    void dump_stacks(int count) {
        int i;
        System.out.println("=index==state====value=     s:" + stateptr + "  v:" + valptr);
        for (i = 0; i < count; i++)
            System.out.println(" " + i + "    " + statestk[i] + "      " + valstk[i]);
        System.out.println("======================");
    }

//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
    void val_init() {
        valstk = new fr.lifl.jaskell.compiler.core.Expression[YYSTACKSIZE];
        yyval = null;
        yylval = null;
        valptr = -1;
    }

    void val_push(fr.lifl.jaskell.compiler.core.Expression val) {
        if (valptr >= YYSTACKSIZE)
            return;
        valstk[++valptr] = val;
    }

    fr.lifl.jaskell.compiler.core.Expression val_pop() {
        if (valptr < 0)
            return null;
        return valstk[valptr--];
    }

    void val_drop(int cnt) {
        int ptr;
        ptr = valptr - cnt;
        if (ptr < 0)
            return;
        valptr = ptr;
    }

    fr.lifl.jaskell.compiler.core.Expression val_peek(int relative) {
        int ptr;
        ptr = valptr - relative;
        if (ptr < 0)
            return null;
        return valstk[ptr];
    }

//#line 1028 "Yyparser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
    void yylexdebug(int state, int ch) {
        String s = null;
        if (ch < 0)
            ch = 0;
        if (ch <= YYMAXTOKEN) //check index bounds
            s = yyname[ch]; //now get it
        if (s == null)
            s = "illegal-symbol";
        debug("state " + state + ", reading " + ch + " (" + s + ")");
    }

//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
    int yyparse() {
        boolean doaction;
        init_stacks();
        yynerrs = 0;
        yyerrflag = 0;
        yychar = -1; //impossible char forces a read
        yystate = 0; //initial state
        state_push(yystate); //save it
        while (true) //until parsing is done, either correctly, or w/error
        {
            doaction = true;
            if (yydebug)
                debug("loop");
            //#### NEXT ACTION (from reduction table)
            for (yyn = yydefred[yystate]; yyn == 0; yyn = yydefred[yystate]) {
                if (yydebug)
                    debug("yyn:" + yyn + "  state:" + yystate + "  yychar:" + yychar);
                if (yychar < 0) //we want a char?
                {
                    yychar = yylex(); //get next token
                    if (yydebug)
                        debug(" next yychar:" + yychar);
                    //#### ERROR CHECK ####
                    if (yychar < 0) //it it didn't work/error
                    {
                        yychar = 0; //change it to default string (no -1!)
                        if (yydebug)
                            yylexdebug(yystate, yychar);
                    }
                } //yychar<0
                yyn = yysindex[yystate]; //get amount to shift by (shift index)
                if ((yyn != 0) && ((yyn += yychar) >= 0) && (yyn <= YYTABLESIZE) && (yycheck[yyn] == yychar)) {
                    if (yydebug)
                        debug("state " + yystate + ", shifting to state " + yytable[yyn]);
                    //#### NEXT STATE ####
                    yystate = yytable[yyn]; //we are in a new state
                    state_push(yystate); //save it
                    val_push(yylval); //push our lval as the input for next rule
                    yychar = -1; //since we have 'eaten' a token, say we need another
                    if (yyerrflag > 0) //have we recovered an error?
                        --yyerrflag; //give ourselves credit
                    doaction = false; //but don't process yet
                    break; //quit the yyn=0 loop
                }

                yyn = yyrindex[yystate]; //reduce
                if ((yyn != 0) && ((yyn += yychar) >= 0) && (yyn <= YYTABLESIZE) && (yycheck[yyn] == yychar)) { //we reduced!
                    if (yydebug)
                        debug("reduce");
                    yyn = yytable[yyn];
                    doaction = true; //get ready to execute
                    break; //drop down to actions
                } else //ERROR RECOVERY
                {
                    if (yyerrflag == 0) {
                        yyerror("syntax error");
                        yynerrs++;
                    }
                    if (yyerrflag < 3) //low error count?
                    {
                        yyerrflag = 3;
                        while (true) //do until break
                        {
                            if (stateptr < 0) //check for under & overflow here
                            {
                                yyerror("stack underflow. aborting..."); //note lower case 's'
                                return 1;
                            }
                            yyn = yysindex[state_peek(0)];
                            if ((yyn != 0) && ((yyn += YYERRCODE) >= 0) && (yyn <= YYTABLESIZE) && (yycheck[yyn] == YYERRCODE)) {
                                if (yydebug)
                                    debug("state " + state_peek(0) + ", error recovery shifting to state " + yytable[yyn] + " ");
                                yystate = yytable[yyn];
                                state_push(yystate);
                                val_push(yylval);
                                doaction = false;
                                break;
                            } else {
                                if (yydebug)
                                    debug("error recovery discarding state " + state_peek(0) + " ");
                                if (stateptr < 0) //check for under & overflow here
                                {
                                    yyerror("Stack underflow. aborting..."); //capital 'S'
                                    return 1;
                                }
                                state_pop();
                                val_pop();
                            }
                        }
                    } else //discard this token
                    {
                        if (yychar == 0)
                            return 1; //yyabort
                        if (yydebug) {
                            yys = null;
                            if (yychar <= YYMAXTOKEN)
                                yys = yyname[yychar];
                            if (yys == null)
                                yys = "illegal-symbol";
                            debug("state " + yystate + ", error recovery discards token " + yychar + " (" + yys + ")");
                        }
                        yychar = -1; //read another
                    }
                } //end error recovery
            } //yyn=0 loop
            if (!doaction) //any reason not to proceed?
                continue; //skip action
            yym = yylen[yyn]; //get count of terminals on rhs
            if (yydebug)
                debug("state " + yystate + ", reducing " + yym + " by rule " + yyn + " (" + yyrule[yyn] + ")");
            if (yym > 0) //if count of rhs not 'nil'
                yyval = val_peek(yym - 1); //get current semantic value
            switch (yyn) {

//########## USER-SUPPLIED ACTIONS ##########
            case 1:
//#line 87 "jaskell-core.y"
            {
                String mname = ((Variable) val_peek(0)).getName();
                module = (Module) Module.getToplevels().get(mname);
                if (module == null) {
                    module = new Module(mname, null);
                    module.putTag(lex.makePosition());
                }
                equations = new HashMap();
            }
            break;

            case 2:
//#line 96 "jaskell-core.y"
            {
                /* normalize module equations */
                /* normalize equations */
                Normalizer nz = new Normalizer(module);
                try {
                    nz.normalize(equations);
                } catch (SymbolException ex) {
                    /* notify event */
                    parseError("Cannot normalize equations :" + ex);
                }
                modules.add(module);
            }
            break;

            case 3:
//#line 109 "jaskell-core.y"
            {
                /* default module is Main - ??? */
                if (module == null)
                    module = new Module("Main", null);
                module.putTag(lex.makePosition());
                equations = new HashMap();
            }
            break;

            case 4:
//#line 115 "jaskell-core.y"
            {
/* normalize equations */
                Normalizer nz = new Normalizer(module);
                try {
                    nz.normalize(equations);
                } catch (SymbolException ex) {
                    /* notify event */
                    parseError("Cannot normalize equations :" + ex);
                }
                modules.add(module);
            }
            break;

            case 8:
//#line 133 "jaskell-core.y"
            {
                yyval = new ExpressionList();
            }
            break;

            case 9:
//#line 134 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 10:
//#line 138 "jaskell-core.y"
            {
                if (val_peek(2) instanceof ExpressionList) {
                    ((ExpressionList) val_peek(2)).add(val_peek(0));
                    yyval = val_peek(2);
                } else {
                    ExpressionList decls = new ExpressionList();
                    if (val_peek(2) != null)
                        decls.add(val_peek(2));
                    if (val_peek(0) != null)
                        decls.add(val_peek(0));
                    yyval = decls;
                }
                ;
            }
            break;

            case 11:
//#line 149 "jaskell-core.y"
            {
                ExpressionList el = new ExpressionList();
                if (val_peek(0) != null)
                    el.add(val_peek(0));
                yyval = el;
            }
            break;

            case 12:
//#line 154 "jaskell-core.y"
            {
                Type t = val_peek(2).getType();
                /* bind name in module */
                String lname = null;
                if (t instanceof TypeConstructor)
                    lname = ((TypeConstructor) t).getName();
                else if (t instanceof TypeApplication)
                    lname = ((TypeConstructor) t.getConstructor()).getName();
                else
                    System.err.println("Type of optype :" + val_peek(2).getClass() + "(" + val_peek(2) + ")");
                /* create definition for type */
                DataDefinition ddef = new DataDefinition(lname, t, module);
                ddef.putTag(lex.makePosition());
                /* bind constructors */
                Iterator it = ((ExpressionList) val_peek(0)).iterator();
                while (it.hasNext())
                    ddef.addConstructor((ConstructorDefinition) it.next());
                    /* done */
            }
            break;

            case 13:
//#line 174 "jaskell-core.y"
            {
                if (val_peek(0) != null) {
                    Equation eq = (Equation) val_peek(0);
                    ExpressionList lhs = (ExpressionList) eq.getLhs();
                    String fname = ((Variable) lhs.get(0)).getName();
                    addEquation(fname, eq);
                }
            }
            break;

            case 14:
//#line 183 "jaskell-core.y"
            {
                Iterator it = ((ExpressionList) val_peek(0)).iterator();
                while (it.hasNext()) {
                    Definition def = (Definition) it.next();
                    module.bind(def.getName(), def);
                }
            }
            break;

            case 18:
//#line 199 "jaskell-core.y"
            {
                /* create PrimitiveData object and bind it in given module */
                Type t = val_peek(1).getType();
                String lname = null;
                if (t instanceof TypeConstructor)
                    lname = ((TypeConstructor) t).getName();
                else if (t instanceof TypeApplication)
                    lname = ((TypeConstructor) t.getConstructor()).getName();
                else
                    System.err.println("Type of optype :" + val_peek(1).getClass() + "(" + val_peek(1) + ")");
                /* resolve java class */
                Class cls;
                try {
                    cls = Class.forName(((StringLiteral) val_peek(0)).getString());
                    /* create definition for type */
                    PrimitiveData ddef = new PrimitiveData(lname, t, cls, module);
                } catch (ClassNotFoundException cnfex) {
                    System.err.println("Error in native datatype definition : cannot find class " + ((StringLiteral) val_peek(0)).getString());
                }
            }
            break;

            case 19:
//#line 220 "jaskell-core.y"
            {
                /* resolve data definition */
                Type t = val_peek(3).getType();
                String tname = null;
                if (t instanceof TypeConstructor)
                    tname = ((TypeConstructor) t).getName();
                else if (t instanceof TypeApplication)
                    tname = ((TypeConstructor) t.getConstructor()).getName();
                else
                    System.err.println("Type of optype :" + val_peek(3).getClass() + "(" + val_peek(3) + ")");

                DataDefinition ddef = (DataDefinition) module.resolveType(tname);
                if (ddef != null) {
                    /* create primitive constructor */
                    try {
                        Class cls = Class.forName(((StringLiteral) val_peek(0)).getString());
                        String cname = null;
                        t = val_peek(1).getType();
                        if (t instanceof TypeConstructor)
                            cname = ((TypeConstructor) t).getName();
                        else if (t instanceof TypeApplication)
                            cname = ((TypeConstructor) t.getConstructor()).getName();
                        else
                            System.err.println("Type of optype :" + val_peek(3).getClass() + "(" + val_peek(3) + ")");
                        /* create definition for ctor */
                        PrimitiveConstructor ctor = new PrimitiveConstructor(cname, ddef, new Type[0], cls, module);
                    } catch (ClassNotFoundException cnfex) {
                        System.err.println("Error in native constructor definition : cannot find class " + ((StringLiteral) val_peek(0)).getString());
                    }
                } else
                    System.err.println("Error in native constructor definition : cannot find data definition for " + tname);
            }
            break;

            case 20:
//#line 253 "jaskell-core.y"
            {

                /* resolve data definition */
                /* resolve data definition */
                Type t = val_peek(4).getType();
                String tname = null;
                if (t instanceof TypeConstructor)
                    tname = ((TypeConstructor) t).getName();
                else if (t instanceof TypeApplication)
                    tname = ((TypeConstructor) t.getConstructor()).getName();
                else
                    System.err.println("Type of optype :" + val_peek(4).getClass() + "(" + val_peek(4) + ")");
                DataDefinition ddef = (DataDefinition) module.resolveType(tname);
                if (ddef != null) {
                    /* create primitive constructor */
                    try {
                        Class cls = Class.forName(((StringLiteral) val_peek(0)).getString());
                        String cname = null;
                        t = val_peek(2).getType();
                        if (t instanceof TypeConstructor)
                            cname = ((TypeConstructor) t).getName();
                        else if (t instanceof TypeApplication)
                            cname = ((TypeConstructor) t.getConstructor()).getName();
                        else
                            System.err.println("Type of optype :" + val_peek(4).getClass() + "(" + val_peek(4) + ")");
                        /* create definition for ctor */
                        PrimitiveConstructor cdef = new PrimitiveConstructor(cname, ddef, new Type[0], cls, module);
                        /* range over parameters - if any */
                        if (val_peek(1) != null) {
                            Iterator it = ((ExpressionList) val_peek(1)).iterator();
                            while (it.hasNext()) {
                                TypeExpression tex = (TypeExpression) it.next();
                                if (tex.isStrict())
                                    cdef.addStrictParameter(tex.getType());
                                else
                                    cdef.addParameter(tex.getType());
                            }
                        }
                    } catch (ClassNotFoundException cnfex) {
                        System.err.println("Error in native constructor definition : cannot find class " + ((StringLiteral) val_peek(1)).getString());
                    }
                } else
                    System.err.println("Error in native constructor definition : cannot find data definition for " + tname);
            }
            break;

            case 21:
//#line 298 "jaskell-core.y"
            {
                String vname = ((Variable) val_peek(3)).getName();
                Type t = val_peek(1).getType();
                try {
                    Class cls = Class.forName(((StringLiteral) val_peek(0)).getString());
                    PrimitiveFunction pf = new PrimitiveFunction(vname, module, t, cls);
                    /* register static primitive with bytecode generator */
                    PrimitivesCodeGenerator.registerStaticPrimitive(pf);
                } catch (ClassNotFoundException cnfex) {
                    System.err.println("Error in native function definition : cannot find class " + ((StringLiteral) val_peek(0)).getString());
                }
            }
            break;

            case 22:
//#line 312 "jaskell-core.y"
            {
                ((ExpressionList) val_peek(2)).add(val_peek(0));
                yyval = val_peek(2);
            }
            break;

            case 23:
//#line 313 "jaskell-core.y"
            {
                ExpressionList el = new ExpressionList();
                el.add(val_peek(0));
                yyval = el;
            }
            break;

            case 24:
//#line 316 "jaskell-core.y"
            {
                /* create definition */
                ConstructorDefinition cdef = new ConstructorDefinition(((Variable) val_peek(0)).getName(), null, module);
                cdef.putTag(lex.makePosition());
                yyval = cdef;
            }
            break;

            case 25:
//#line 324 "jaskell-core.y"
            {
                /* create definition */
                ConstructorDefinition cdef = new ConstructorDefinition(((Variable) val_peek(1)).getName(), null, module);
                cdef.putTag(lex.makePosition());
                /* range over parameters - if any */
                if (val_peek(0) != null) {
                    Iterator it = ((ExpressionList) val_peek(0)).iterator();
                    while (it.hasNext()) {
                        TypeExpression tex = (TypeExpression) it.next();
                        if (tex.isStrict())
                            cdef.addStrictParameter(tex.getType());
                        else
                            cdef.addParameter(tex.getType());
                    }
                }
                yyval = cdef;
            }
            break;

            case 26:
//#line 342 "jaskell-core.y"
            {
                /* create definition */
                ConstructorDefinition cdef = new ConstructorDefinition(((Variable) val_peek(1)).getName(), null, module);
                cdef.putTag(lex.makePosition());
                TypeExpression tex = (TypeExpression) val_peek(2);
                if (tex.isStrict())
                    cdef.addStrictParameter(tex.getType());
                else
                    cdef.addParameter(tex.getType());
                tex = (TypeExpression) val_peek(0);
                if (tex.isStrict())
                    cdef.addStrictParameter(tex.getType());
                else
                    cdef.addParameter(tex.getType());
                yyval = cdef;
            }
            break;

            case 27:
//#line 362 "jaskell-core.y"
            {
                if (val_peek(1) != null) {
                    ((ExpressionList) val_peek(1)).add(val_peek(0));
                    yyval = val_peek(1);
                } else {
                    ExpressionList el = new ExpressionList();
                    el.add(val_peek(0));
                    yyval = el;
                }
            }
            break;

            case 28:
//#line 372 "jaskell-core.y"
            {
                ExpressionList el = new ExpressionList();
                el.add(val_peek(0));
                yyval = el;
            }
            break;

            case 29:
//#line 374 "jaskell-core.y"
            {
                ((TypeExpression) val_peek(0)).setStrict(true);
                yyval = val_peek(0);
            }
            break;

            case 30:
//#line 375 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 31:
//#line 377 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 32:
//#line 378 "jaskell-core.y"
            {
                ((TypeExpression) val_peek(0)).setStrict(true);
                yyval = val_peek(0);
            }
            break;

            case 33:
//#line 410 "jaskell-core.y"
            {
                Equation eq = new Equation();
                eq.setLhs(val_peek(1));
                eq.setRhs(val_peek(0));
                yyval = eq;
            }
            break;

            case 34:
//#line 417 "jaskell-core.y"
            {
                Equation eq = new Equation();
                eq.setLhs(val_peek(1));
                eq.setRhs(val_peek(0));
                yyval = eq;
            }
            break;

            case 35:
//#line 423 "jaskell-core.y"
            {
                yyerror("Syntax error");
                yyval = null;
            }
            break;

            case 36:
//#line 428 "jaskell-core.y"
            {
                Iterator it = ((ExpressionList) val_peek(2)).iterator();
                ExpressionList el = new ExpressionList();
                while (it.hasNext()) {
                    Definition def = new Definition();
                    def.setName(((Variable) it.next()).getName());
                    if (val_peek(0) != null)
                        def.setType(val_peek(0).getType());
                    el.add(def);
                }
                yyval = el;
            }
            break;

            case 37:
//#line 442 "jaskell-core.y"
            {
                ((ExpressionList) val_peek(2)).add(val_peek(0));
                yyval = val_peek(2);
            }
            break;

            case 38:
//#line 446 "jaskell-core.y"
            {
                ExpressionList el = new ExpressionList();
                el.add(val_peek(0));
                yyval = el;
            }
            break;

            case 39:
//#line 451 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 40:
//#line 456 "jaskell-core.y"
            {
                yyval = new TypeExpression(Types.fun(val_peek(2).getType(), val_peek(0).getType()));
            }
            break;

            case 41:
//#line 457 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 42:
//#line 461 "jaskell-core.y"
            {
                /* recompute the kind of type constructor */
                Type tcon = val_peek(1).getType().getConstructor();
                if (!(tcon instanceof PrimitiveType))
                    tcon.setKind(null);
                yyval = new TypeExpression(Types.apply(val_peek(1).getType(), val_peek(0).getType()));
            }
            break;

            case 43:
//#line 468 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 44:
//#line 471 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 45:
//#line 473 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 46:
//#line 477 "jaskell-core.y"
            {
                int i = 1;
                List l = new ArrayList();
                l.add(val_peek(4).getType());
                l.add(val_peek(2).getType());
                Iterator it = ((ExpressionList) val_peek(1)).iterator();
                while (it.hasNext()) {
                    l.add(it.next());
                    i++;
                }
                /* make type constructor */
                /* make type defintiion */
                yyval = new TypeExpression(Types.apply(Types.tuple(i), l));
            }
            break;

            case 47:
//#line 492 "jaskell-core.y"
            {
                yyval = new TypeExpression(Types.apply(Primitives.LIST, val_peek(1).getType()));
            }
            break;

            case 48:
//#line 495 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 51:
//#line 500 "jaskell-core.y"
            {
                yyval = new TypeExpression(Types.makeTycon(((Constructor) val_peek(0)).getName()));
            }
            break;

            case 52:
//#line 502 "jaskell-core.y"
            {
                yyval = new TypeExpression(Primitives.UNIT);
            }
            break;

            case 53:
//#line 503 "jaskell-core.y"
            {
                yyval = new TypeExpression(Primitives.LIST);
            }
            break;

            case 54:
//#line 504 "jaskell-core.y"
            {
                yyval = new TypeExpression(Primitives.FUNCTION);
            }
            break;

            case 55:
//#line 505 "jaskell-core.y"
            {
                yyval = new TypeExpression(Primitives.TUPLE_2);
            }
            break;

            case 56:
//#line 542 "jaskell-core.y"
            {
                ExpressionList el = new ExpressionList();
                el.add(val_peek(1));
                if (val_peek(0) != null)
                    el.addAll((ExpressionList) val_peek(0));
                yyval = el;
            }
            break;

            case 57:
//#line 550 "jaskell-core.y"
            {
                ExpressionList el = new ExpressionList();
                el.add(val_peek(1));
                el.add(val_peek(2));
                el.add(val_peek(0));
                yyval = el;
            }
            break;

            case 58:
//#line 561 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 59:
//#line 569 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 60:
//#line 578 "jaskell-core.y"
            {
                Application app = new Application();
                app.putTag(lex.makePosition());
                app.setFunction(val_peek(1));
                app.addArgument(val_peek(2));
                app.addArgument(val_peek(0));
                yyval = app;
            }
            break;

            case 61:
//#line 586 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 62:
//#line 587 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 63:
//#line 591 "jaskell-core.y"
            {
                Application app = new Application();
                app.putTag(lex.makePosition());
                QualifiedVariable qv = new QualifiedVariable("negate");
                qv.putTag(lex.makePosition());
                qv.addPathElement("Prelude");
                app.setFunction(qv);
                app.addArgument(val_peek(0));
                yyval = app;
            }
            break;

            case 64:
//#line 602 "jaskell-core.y"
            {
                Application app = new Application();
                app.putTag(lex.makePosition());
                app.setFunction(val_peek(1));
                app.addArgument(val_peek(2));
                app.addArgument(val_peek(0));
                yyval = app;
            }
            break;

            case 65:
//#line 613 "jaskell-core.y"
            {
                /* build list of patterns */
                List l = null;
                if (val_peek(2) != null) {
                    l = (ExpressionList) val_peek(2);
                } else
                    l = new ArrayList();
                l.add(0, val_peek(3));
                /* build list of variables and abstraction  */
                List vars = new ArrayList();
                Abstraction abs = new Abstraction();
                abs.putTag(lex.makePosition());
                for (int i = 0; i < l.size(); i++) {
                    LocalBinding lb = LocalBinding.freshBinding();
                    lb.putTag(lex.makePosition());
                    abs.bind(lb);
                    vars.add(lb);
                }
                /* build pattern match */
                List pm = new ArrayList();
                PatternMatch pm1 = new PatternMatch();
                pm1.patterns = l;
                pm1.expr = val_peek(0);
                pm.add(pm1);
                /* normalize equation */
                abs.setBody(new Matcher().match(vars, pm, val_peek(0)));
                /* done */
                yyval = abs;
            }
            break;

            case 66:
//#line 643 "jaskell-core.y"
            {
                Let let = new Let();
                let.setBody(val_peek(0));
                /* register all declarations into equations map */
                Map eqs = new HashMap();
                Iterator it = ((ExpressionList) val_peek(2)).iterator();
                while (it.hasNext()) {
                    Equation eq = (Equation) it.next();
                    ExpressionList lhs = (ExpressionList) eq.getLhs();
                    String fname = ((Variable) lhs.get(0)).getName();
                    /* add eq to map */
                    List l = (List) eqs.get(fname);
                    if (l == null) {
                        l = new ArrayList();
                        eqs.put(fname, l);
                    }
                    l.add(eq);
                }
                /* normalize map and store into let construct */
                Normalizer nz = new Normalizer(let);
                try {
                    nz.normalize(eqs);
                } catch (SymbolException ex) {
                    /* notify event */
                    parseError("Cannot normalize equations :" + ex);
                }
                yyval = let;
            }
            break;

            case 67:
//#line 672 "jaskell-core.y"
            {
                Conditional cond = new Conditional();
                cond.putTag(lex.makePosition());
                cond.setCondition(val_peek(4));
                cond.setIfTrue(val_peek(2));
                cond.setIfFalse(val_peek(0));
                yyval = cond;
            }
            break;

            case 68:
//#line 681 "jaskell-core.y"
            {
                Alternative alt = new Alternative();
                alt.putTag(lex.makePosition());
                alt.setExpression(val_peek(4));
                Iterator it = ((ExpressionList) val_peek(1)).iterator();
                while (it.hasNext()) {
                    PatternAlternative pa = (PatternAlternative) it.next();
                    alt.addPattern(pa.getPattern(), pa.getExpr());
                }
                yyval = alt;
            }
            break;

            case 69:
//#line 693 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 70:
//#line 697 "jaskell-core.y"
            {
                if (val_peek(1) instanceof Application) {
                    ((Application) val_peek(1)).addArgument(val_peek(0));
                    yyval = val_peek(1);
                } else {
                    Application app = new Application();
                    app.putTag(lex.makePosition());
                    app.setFunction(val_peek(1));
                    app.addArgument(val_peek(0));
                    yyval = app;
                }
            }
            break;

            case 71:
//#line 709 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 72:
//#line 712 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 73:
//#line 713 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 74:
//#line 714 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 75:
//#line 715 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 76:
//#line 716 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 77:
//#line 717 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 78:
//#line 719 "jaskell-core.y"
            {
                /* construct a tuple the size of the expression list */
                Application app = new Application();
                app.putTag(lex.makePosition());
                StringBuffer tconsb = new StringBuffer("((,");
                for (int i = 0; i < ((ExpressionList) val_peek(1)).size(); i++)
                    tconsb.append(",");
                tconsb.append("))");
                /* data constructor reference */
                QualifiedConstructor qv = new QualifiedConstructor(tconsb.toString());
                qv.putTag(lex.makePosition());
                qv.addPathElement("Prelude");
                app.setFunction(qv);
                /* add all arguments */
                app.addArgument(val_peek(3));
                app.addArgument(val_peek(2));
                for (Iterator it = ((ExpressionList) val_peek(1)).iterator(); it.hasNext();)
                    app.addArgument((Expression) it.next());
                    /* done */
                yyval = app;
            }
            break;

            case 79:
//#line 741 "jaskell-core.y"
            {
                /* construct a tuple the size of the expression list */
                Application app = new Application();
                app.putTag(lex.makePosition());
                Application cur = app;
                /* data constructor reference */
                QualifiedConstructor qv = new QualifiedConstructor("(:)");
                qv.putTag(lex.makePosition());
                qv.addPathElement("Prelude");
                app.setFunction(qv);
                /* add all arguments */
                app.addArgument(val_peek(2));
                for (Iterator it = ((ExpressionList) val_peek(1)).iterator(); it.hasNext();) {
                    Application tmp = new Application();
                    tmp.putTag(lex.makePosition());
                    tmp.setFunction(qv);
                    tmp.addArgument((Expression) it.next());
                    cur.addArgument(tmp);
                    cur = tmp;
                }
                /* done */
                qv = new QualifiedConstructor("([])");
                qv.putTag(lex.makePosition());
                qv.addPathElement("Prelude");
                cur.addArgument(qv);
                yyval = app;
            }
            break;

            case 84:
//#line 773 "jaskell-core.y"
            {
                Application app = new Application();
                app.putTag(lex.makePosition());
                app.setFunction(val_peek(1));
                app.addArgument(val_peek(2));
                yyval = app;
            }
            break;

            case 85:
//#line 781 "jaskell-core.y"
            {
                Application app = new Application();
                app.putTag(lex.makePosition());
                app.setFunction(val_peek(2));
                app.addArgument(val_peek(1));
                yyval = app;
            }
            break;

            case 86:
//#line 792 "jaskell-core.y"
            {
                ((ExpressionList) val_peek(1)).add(val_peek(0));
                yyval = val_peek(1);
            }
            break;

            case 87:
//#line 797 "jaskell-core.y"
            {
                yyval = new ExpressionList();
                /*  ((ExpressionList)$$).add($1); */
            }
            break;

            case 88:
//#line 802 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 89:
//#line 806 "jaskell-core.y"
            {
                if (val_peek(2) instanceof ExpressionList) {
                    ((ExpressionList) val_peek(2)).add(val_peek(0));
                    yyval = val_peek(2);
                } else {
                    ExpressionList decls = new ExpressionList();
                    decls.add(val_peek(2));
                    decls.add(val_peek(0));
                    yyval = decls;
                }
                ;
            }
            break;

            case 90:
//#line 817 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 91:
//#line 819 "jaskell-core.y"
            {
                yyval = new PatternAlternative((Pattern) val_peek(2), val_peek(0));
            }
            break;

            case 92:
//#line 847 "jaskell-core.y"
            {
                if (val_peek(1) instanceof ExpressionList) {
                    ((ExpressionList) val_peek(1)).add(val_peek(0));
                    yyval = val_peek(1);
                } else if (val_peek(1) == null) {
                    ExpressionList decls = new ExpressionList();
                    decls.add(val_peek(0));
                    yyval = decls;
                }
                ;
            }
            break;

            case 93:
//#line 857 "jaskell-core.y"
            {
                yyval = null;
            }
            break;

            case 94:
//#line 861 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 95:
//#line 865 "jaskell-core.y"
            {
                ConstructorPattern pat = new ConstructorPattern();
                pat.putTag(lex.makePosition());
                pat.setConstructor((Constructor) val_peek(1));
                pat.addPattern((Pattern) val_peek(2));
                System.err.println("Type of rh arg : " + val_peek(0).getClass());
                pat.addPattern((Pattern) val_peek(0));
                yyval = pat;
            }
            break;

            case 96:
//#line 875 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 97:
//#line 878 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 98:
//#line 881 "jaskell-core.y"
            {
                if (val_peek(1) instanceof ExpressionList) {
                    ((ExpressionList) val_peek(1)).add(val_peek(0));
                    yyval = val_peek(1);
                } else if (val_peek(1) == null) {
                    ExpressionList decls = new ExpressionList();
                    decls.add(val_peek(0));
                    yyval = decls;
                }
                ;
            }
            break;

            case 99:
//#line 891 "jaskell-core.y"
            {
                yyval = null;
            }
            break;

            case 100:
//#line 894 "jaskell-core.y"
            {
                LocalBinding bind = new LocalBinding(((Variable) val_peek(0)).getName());
                bind.putTag(lex.makePosition());
                yyval = bind;
            }
            break;

            case 101:
//#line 900 "jaskell-core.y"
            { /* list and unit constructors */
                ConstructorPattern pat = new ConstructorPattern();
                pat.putTag(lex.makePosition());
                pat.setConstructor((Constructor) val_peek(0));
                yyval = pat;
            }
            break;

            case 102:
//#line 907 "jaskell-core.y"
            {
                ConstructorPattern pat = new ConstructorPattern();
                pat.putTag(lex.makePosition());
                pat.setConstructor((Constructor) val_peek(1));
                if (val_peek(0) != null) {
                    Iterator it = ((ExpressionList) val_peek(0)).iterator();
                    while (it.hasNext())
                        pat.addPattern((Pattern) it.next());
                }
                yyval = pat;
            }
            break;

            case 103:
//#line 918 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 104:
//#line 919 "jaskell-core.y"
            {
                yyval = LocalBinding.wildcard;
            }
            break;

            case 105:
//#line 920 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 106:
//#line 922 "jaskell-core.y"
            {
                ConstructorPattern pat = new ConstructorPattern();
                pat.putTag(lex.makePosition());
                StringBuffer tconsb = new StringBuffer("((,");
                for (int i = 0; i < ((ExpressionList) val_peek(1)).size(); i++)
                    tconsb.append(",");
                tconsb.append("))");
                /* data constructor reference */
                QualifiedConstructor qv = new QualifiedConstructor(tconsb.toString());
                qv.putTag(lex.makePosition());
                qv.addPathElement("Prelude");
                pat.setConstructor(qv);
                /* add all arguments */
                pat.addPattern((Pattern) val_peek(3));
                pat.addPattern((Pattern) val_peek(2));
                for (Iterator it = ((ExpressionList) val_peek(1)).iterator(); it.hasNext();)
                    pat.addPattern((Pattern) it.next());
                /* done */
                yyval = pat;
            }
            break;

            case 107:
//#line 943 "jaskell-core.y"
            {
                /* construct a tuple the size of the expression list */
                List l = new ArrayList();
                l.add(val_peek(2));
                l.addAll((ExpressionList) val_peek(1));
                ConstructorPattern app = makeListPattern(l);
                yyval = app;
            }
            break;

            case 108:
//#line 951 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 109:
//#line 960 "jaskell-core.y"
            {
                if (val_peek(2) instanceof ExpressionList) {
                    ((ExpressionList) val_peek(2)).add(val_peek(0));
                    yyval = val_peek(2);
                } else if (val_peek(2) == null) {
                    ExpressionList decls = new ExpressionList();
                    decls.add(val_peek(0));
                    yyval = decls;
                }
                ;
            }
            break;

            case 110:
//#line 970 "jaskell-core.y"
            {
                yyval = null;
            }
            break;

            case 111:
//#line 972 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 112:
//#line 976 "jaskell-core.y"
            {
                ConstructorPattern cp = new ConstructorPattern();
                QualifiedConstructor qv = new QualifiedConstructor("Event");
                qv.addPathElement("Prelude");
                cp.setConstructor(qv);
                cp.addPattern(LocalBinding.wildcard);
                ConstructorPattern cp2 = (ConstructorPattern) val_peek(1);
                cp.putTag(lex.makePosition());
                if (val_peek(0) != null) {
                    Iterator it = ((ExpressionList) val_peek(0)).iterator();
                    while (it.hasNext())
                        cp2.addPattern((Pattern) it.next());
                }
                cp.addPattern(cp2);
                yyval = cp;
            }
            break;

            case 113:
//#line 993 "jaskell-core.y"
            {
                ConstructorPattern cp = new ConstructorPattern();
                QualifiedConstructor qv = new QualifiedConstructor("Event");
                qv.addPathElement("Prelude");
                cp.setConstructor(qv);
                cp.addPattern((Pattern) val_peek(2));
                cp.putTag(lex.makePosition());
                ConstructorPattern cp2 = (ConstructorPattern) val_peek(1);
                /* qualify constructor with port name */
                QualifiedConstructor qc = new QualifiedConstructor(cp2.getConstructor().getName());
                qc.addPathElement((String) ((Literal) val_peek(2)).unpack());
                cp2.setConstructor(qc);
                if (val_peek(1) != null) {
                    Iterator it = ((ExpressionList) val_peek(0)).iterator();
                    while (it.hasNext())
                        cp2.addPattern((Pattern) it.next());
                }
                cp.addPattern(cp2);
                yyval = cp;
            }
            break;

            case 114:
//#line 1014 "jaskell-core.y"
            {
                /* TODO */
                yyval = null;
            }
            break;

            case 115:
//#line 1023 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 116:
//#line 1027 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 117:
//#line 1035 "jaskell-core.y"
            {
                ConstructorPattern pat = new ConstructorPattern();
                Constructor ctor = new Constructor("Return$" + ((StringLiteral) val_peek(0)).getString());
                pat.setConstructor(ctor);
                pat.putTag(lex.makePosition());
                ctor.putTag(lex.makePosition());
                yyval = pat;
            }
            break;

            case 118:
//#line 1044 "jaskell-core.y"
            {
                ConstructorPattern pat = new ConstructorPattern();
                Constructor ctor = new Constructor("Call$" + ((StringLiteral) val_peek(0)).getString());
                pat.setConstructor(ctor);
                pat.putTag(lex.makePosition());
                ctor.putTag(lex.makePosition());
                yyval = pat;
            }
            break;

            case 119:
//#line 1053 "jaskell-core.y"
            {
                ConstructorPattern pat = new ConstructorPattern();
                Constructor ctor = new Constructor("Full$" + ((StringLiteral) val_peek(0)).getString());
                pat.setConstructor(ctor);
                pat.putTag(lex.makePosition());
                ctor.putTag(lex.makePosition());
                yyval = pat;
            }
            break;

            case 120:
//#line 1063 "jaskell-core.y"
            {
                yyval = new StringLiteral(((Variable) val_peek(0)).getName());
            }
            break;

            case 121:
//#line 1063 "jaskell-core.y"
            {
                yyval = LocalBinding.wildcard;
            }
            break;

            case 122:
//#line 1065 "jaskell-core.y"
            {
                yyval = new StringLiteral(((Variable) val_peek(0)).getName());
            }
            break;

            case 123:
//#line 1065 "jaskell-core.y"
            {
                yyval = LocalBinding.wildcard;
            }
            break;

            case 124:
//#line 1067 "jaskell-core.y"
            {
                yyval = new StringLiteral(((Variable) val_peek(0)).getName());
            }
            break;

            case 125:
//#line 1067 "jaskell-core.y"
            {
                yyval = LocalBinding.wildcard;
            }
            break;

            case 126:
//#line 1070 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 127:
//#line 1074 "jaskell-core.y"
            {
                ExpressionList el = new ExpressionList();
                el.add(val_peek(0));
                yyval = el;
            }
            break;

            case 128:
//#line 1080 "jaskell-core.y"
            {
                ((ExpressionList) val_peek(1)).add(val_peek(0));
                yyval = val_peek(1);
            }
            break;

            case 129:
//#line 1085 "jaskell-core.y"
            {
                ExpressionList el = new ExpressionList();
                yyval = el;
            }
            break;

            case 130:
//#line 1091 "jaskell-core.y"
            {
                if (val_peek(2) instanceof ExpressionList) {
                    ((ExpressionList) val_peek(2)).add(val_peek(0));
                    yyval = val_peek(2);
                } else if (val_peek(2) == null) {
                    ExpressionList decls = new ExpressionList();
                    decls.add(val_peek(0));
                    yyval = decls;
                }
                ;
            }
            break;

            case 131:
//#line 1102 "jaskell-core.y"
            {
                ExpressionList decls = new ExpressionList();
                decls.add(val_peek(0));
                yyval = decls;
            }
            break;

            case 132:
//#line 1108 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 133:
//#line 1110 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 134:
//#line 1113 "jaskell-core.y"
            {
                yyval = null;
            }
            break;

            case 135:
//#line 1117 "jaskell-core.y"
            {
                yyval = null;
            }
            break;

            case 136:
//#line 1132 "jaskell-core.y"
            {
                Constructor ctor = new Constructor("(())");
                ctor.putTag(lex.makePosition());
                yyval = ctor;
            }
            break;

            case 137:
//#line 1133 "jaskell-core.y"
            {
                Constructor ctor = new Constructor("([])");
                ctor.putTag(lex.makePosition());
                yyval = ctor;
            }
            break;

            case 138:
//#line 1137 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 139:
//#line 1143 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 140:
//#line 1143 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 141:
//#line 1145 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 142:
//#line 1146 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 143:
//#line 1149 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 144:
//#line 1150 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 145:
//#line 1151 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 146:
//#line 1152 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 147:
//#line 1153 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 148:
//#line 1154 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 149:
//#line 1155 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 150:
//#line 1156 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 151:
//#line 1157 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 152:
//#line 1158 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 153:
//#line 1159 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 154:
//#line 1160 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 155:
//#line 1160 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 156:
//#line 1160 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 157:
//#line 1160 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 158:
//#line 1160 "jaskell-core.y"
            {
                Variable var = new Variable("(-)");
                var.putTag(lex.makePosition());
                yyval = var;
            }
            break;

            case 159:
//#line 1161 "jaskell-core.y"
            {
                Constructor ctor = new Constructor("(:)");
                ctor.putTag(lex.makePosition());
                yyval = ctor;
            }
            break;

            case 160:
//#line 1161 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 161:
//#line 1164 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 162:
//#line 1165 "jaskell-core.y"
            {
                yyval = val_peek(1);
            }
            break;

            case 163:
//#line 1166 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 164:
//#line 1167 "jaskell-core.y"
            {
                yyval = new TypeExpression(TypeFactory.makeTypeVariable(((Variable) val_peek(0)).getName()));
            }
            break;

            case 165:
//#line 1171 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 166:
//#line 1171 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 167:
//#line 1171 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 168:
//#line 1171 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;

            case 169:
//#line 1171 "jaskell-core.y"
            {
                yyval = val_peek(0);
            }
            break;
//#line 2441 "Yyparser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
            } //switch
            //#### Now let's reduce... ####
            if (yydebug)
                debug("reduce");
            state_drop(yym); //we just reduced yylen states
            yystate = state_peek(0); //get new state
            val_drop(yym); //corresponding value drop
            yym = yylhs[yyn]; //select next TERMINAL(on lhs)
            if ((yystate == 0) && (yym == 0)) //done? 'rest' state and at first TERMINAL
            {
                debug("After reduction, shifting from state 0 to state " + YYFINAL + "");
                yystate = YYFINAL; //explicitly say we're done
                state_push(YYFINAL); //and save it
                val_push(yyval); //also save the semantic value of parsing
                if (yychar < 0) //we want another character?
                {
                    yychar = yylex(); //get next character
                    if (yychar < 0)
                        yychar = 0; //clean, if necessary
                    if (yydebug)
                        yylexdebug(yystate, yychar);
                }
                if (yychar == 0) //Good exit (if lex returns 0 ;-)
                    break; //quit the loop--all DONE
            } //if yystate
            else //else not done yet
            { //get next state and push, for next yydefred[]
                yyn = yygindex[yym]; //find out where to go
                if ((yyn != 0) && ((yyn += yystate) >= 0) && (yyn <= YYTABLESIZE) && (yycheck[yyn] == yystate))
                    yystate = yytable[yyn]; //get new state
                else
                    yystate = yydgoto[yym]; //else go to new defred
                debug("after reduction, shifting from state " + state_peek(0) + " to state " + yystate + "");
                state_push(yystate); //going again, so push state & val...
                val_push(yyval); //for next action
            }
        } //main loop
        return 0; //yyaccept!!
    }
//## end of method parse() ######################################

    private ConstructorPattern makeListPattern(List l) {
        ConstructorPattern app = null, cur = app;
        QualifiedConstructor qv;
        for (Iterator it = l.iterator(); it.hasNext();) {
            ConstructorPattern tmp = new ConstructorPattern();
            tmp.putTag(lex.makePosition());
            qv = new QualifiedConstructor("(:)");
            qv.putTag(lex.makePosition());
            qv.addPathElement("Prelude");
            tmp.setConstructor(qv);
            tmp.addPattern((Pattern) it.next());
            if (app == null)
                app = tmp;
            else
                cur.addPattern(tmp);
            cur = tmp;
        }
        /* done */
        ConstructorPattern tmp = new ConstructorPattern();
        tmp.putTag(lex.makePosition());
        qv = new QualifiedConstructor("([])");
        qv.putTag(lex.makePosition());
        qv.addPathElement("Prelude");
        tmp.setConstructor(qv);
        cur.addPattern(tmp);
        return app;
    }

    private ConstructorPattern makeEmptyListPattern() {
        /* done */
        ConstructorPattern tmp = new ConstructorPattern();
        tmp.putTag(lex.makePosition());
        QualifiedConstructor qv = new QualifiedConstructor("([])");
        qv.putTag(lex.makePosition());
        qv.addPathElement("Prelude");
        tmp.setConstructor(qv);
        return tmp;
    }

}
//################### END OF CLASS ##############################
