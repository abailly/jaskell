  /************************************************************************
 * HASKEL 98 Lexical Analyzer
* Derived from :
-- A Cactus representation of the Haskell grammar from the Haskell 98 report,
-- starting with the Context-Free Syntax from appendix B.4, followed by
-- rules from Lexical Syntax from appendix B.2.
-- (Haskell layout rules are not represented.)
* Author : Arnaud Bailly
* Version : $Id: Yylex 1153 2005-11-24 20:47:55Z nono $^
**************************************************************************/
package fr.lifl.jaskell.parser;
import fr.lifl.jaskell.compiler.core.*;
import java.util.StringTokenizer;
import java.util.logging.Logger;


class Yylex {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	public final int YYEOF = -1;

  /* the parser object we use */
  Yyparser parser; 
  private int yycolumn;
  /** error manager */
  private Logger log = Logger.getLogger("fidl.parser.jaskell");
  /* get line number */
  public int getLine() {
    return yyline;
  }
 /* set start line number */
  public void setLine(int sl) {
	yyline = sl;
  }
  public int getColumn() {
    return yycolumn;
  }
  public void setColumn(int col) {
    yycolumn = col;
    }
public void yyerror(String msg) {
  StringBuffer err = new StringBuffer("jaskell- ");
  err.append(makePosition().toString())
    .append(" : ")
    .append(msg);
    log.severe(err.toString());
}
public Tag makePosition() {
  return new FileAndLineTag(parser.getSourceFile(),yyline,yycolumn);
}
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NOT_ACCEPT,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NOT_ACCEPT,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NOT_ACCEPT,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NOT_ACCEPT,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NOT_ACCEPT,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NOT_ACCEPT,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NOT_ACCEPT,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NOT_ACCEPT,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NOT_ACCEPT,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NOT_ACCEPT,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NOT_ACCEPT,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NOT_ACCEPT,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NOT_ACCEPT,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NOT_ACCEPT,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NOT_ACCEPT,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NOT_ACCEPT,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR,
		/* 170 */ YY_NO_ANCHOR,
		/* 171 */ YY_NO_ANCHOR,
		/* 172 */ YY_NO_ANCHOR,
		/* 173 */ YY_NO_ANCHOR,
		/* 174 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"51:8,66:2,52,51,66,53,51:18,66,44,68,29,58:2,67,57,35,36,58,65,40,28,34,58," +
"59,61:7,56:2,41,39,43,33,42,58,31,22,63:3,26,50,54:2,24,54:4,21,60,54:4,23," +
"54,25,54,62,54:2,37,47,38,58,48,49,13,64,15,3,6,18,20,8,17,55:2,5,1,14,2,12" +
",55,9,16,10,4,19,7,46,11,55,27,45,30,32,51,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,175,
"0,1,2,3,4,5,6,1,7,1,8,9,1:6,10,7,11,7:3,1:3,12,13,14,15:3,16,15,17,18,19,20" +
",21,1,7:3,22:2,7,15,23,1:3,24,1,25,26,1,15:5,27,15:2,28,27,15:4,17,15:4,29," +
"30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,25,46,47,26,48,7,49,50,51,5" +
"2,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,37,71,72,73,74,75,7" +
",76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,10" +
"0,101,102,103,104,105,106,107,108,109,15,110,111,112,113,114,115,116,117,11" +
"8,15,27,119")[0];

	private int yy_nxt[][] = unpackFromString(120,69,
"1,2,77,86,172,125,144,158,172:2,147,172:2,91,174,149,172,94,172:3,3,173,145" +
",173:3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,172,23,24,25,159," +
"26,27:2,173,172,28,80,127,81,173,28,173:2,172,127,29,127,79,-1:70,172,161,1" +
"72:18,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,173:20," +
"35,160,35:4,-1,36:2,-1,36,-1,36,37,-1:6,38,36:4,173,36,-1:2,35,-1:3,35,173:" +
"3,36,173,35,173,35:2,173,36,-1,36:2,-1:28,39,-1:68,88,127,40,127,-1,127:2,-" +
"1:6,127,41,127:3,-1,127,-1:10,127,-1:6,127,-1,127:2,-1:28,92,127,-1,127,-1," +
"127:2,-1:6,127:5,-1,127,-1:10,127,-1:6,127,-1,127:2,-1:28,127:2,-1,127,-1,1" +
"27:2,-1:6,127:5,-1,127,-1:10,127,-1:6,127,-1,127:2,-1:28,127:2,-1,127,-1,12" +
"7:2,-1:6,127,42,127:3,-1,127,-1:10,127,-1:6,127,-1,127:2,-1:28,127:2,-1,127" +
",-1,127,43,-1:6,127:5,-1,127,-1:10,127,-1:6,127,-1,127:2,-1:28,44:2,-1,44,-" +
"1,44:2,-1:6,45,44:4,-1,44,-1:10,44,-1:6,44,-1,44:2,-1:28,46,127,-1,127,-1,1" +
"27:2,-1:6,127:5,-1,127,-1:10,127,-1:6,127,-1,127:2,-1:52,27:2,-1:49,76,-1:2" +
"1,28,-1:2,28,-1,28,-1:73,29,-1:3,172:20,162:6,-1:19,172,-1:3,162,-1:3,162,1" +
"72,162:2,-1,162:5,172,-1:5,172:15,169,172,137,172:2,162:6,-1:19,172,-1:3,16" +
"2,-1:3,162,172,162:2,-1,162:5,172,-1:5,35:26,-1,36:2,-1,36,-1,36,37,-1:6,38" +
",36:4,35,36,-1:2,35,-1:3,35:4,36,35:6,36,-1,36:2,-1:28,36:2,-1,36,-1,36:2,-" +
"1:6,36:5,-1,36,-1:10,36,-1:6,36,-1,36:2,-1,48:20,82:6,-1,36:2,-1,36,-1,36:2" +
",-1:6,36:5,48,36,-1:2,82,-1:3,82,48,-1:2,36,-1,82,-1,82:2,48,36,-1,36:2,-1:" +
"28,38:2,-1,38,-1,38:2,-1:6,38:5,-1,38,-1:10,38,-1:6,38,-1,38:2,-1:29,49,-1:" +
"67,44:2,-1,44,-1,44:2,-1:6,44:5,-1,44,-1:10,44,-1:6,44,-1,44:2,-1,48:26,-1:" +
"19,48,-1:3,48,-1:3,48:4,-1,48:6,-1:10,117,-1:19,117,-1:29,52,-1:2,52,-1,52," +
"-1:66,54,-1,54,-1:10,55,-1:2,55,-1:6,55,-1,55,-1:2,55,-1:3,55,-1:3,55,-1:23" +
",55,-1:5,55,-1:2,55,-1,55,-1,55:2,-1:5,173:20,35:6,-1,36:2,-1,36,-1,36,37,-" +
"1:6,38,36:4,173,36,-1:2,35,-1:3,35,173:3,36,173,35,173,35:2,173,36,-1,36:2," +
"-1,172:4,69,172:3,70,172:11,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1," +
"162:5,172,-1:60,52,-1:2,52,-1,52,-1:8,172:17,30,172:2,162:6,-1:19,172,-1:3," +
"162,-1:3,162,172,162:2,-1,162:5,172,-1:5,173:5,62,173:14,35:6,-1,36:2,-1,36" +
",-1,36,37,-1:6,38,36:4,173,36,-1:2,35,-1:3,35,173:3,36,173,35,173,35:2,173," +
"36,-1,36:2,-1,99:27,79:2,99,79,99,79:2,99:6,79:5,99,95,99:10,79,99:6,79,99," +
"79,98,-1,85:46,90,85:9,-1,85:11,-1:2,93,-1:31,76,-1:11,96,-1:9,28,-1:2,28,9" +
"3,28,96,-1:7,82:26,-1:19,82,-1:3,82,-1:3,82:4,-1,82:6,-1:56,50,-1:72,84,-1:" +
"2,84,-1,84,-1:64,53,-1:12,172,31,172:3,163,172:6,129,172:7,162:6,-1:19,172," +
"-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,173:5,66,173:14,35:6,-1,36:2," +
"-1,36,-1,36,37,-1:6,38,36:4,173,36,-1:2,35,-1:3,35,173:3,36,173,35,173,35:2" +
",173,36,-1,36:2,-1,101:27,88:2,101,88,101,88:2,101:6,88:5,101,88,101:4,50,8" +
"3,101:4,88,101:6,88,101,88:2,-1,35:25,71,-1,36:2,-1,36,-1,36,37,-1:6,38,36:" +
"4,35,36,-1:2,35,-1:3,35:4,36,35:6,36,-1,36:2,-1:2,103,-1:6,85:2,-1:2,85:2,-" +
"1:3,85:2,-1:26,105,85,-1:8,107,85,-1,107,-1,107,-1:2,85,-1:2,85:2,-1,172:15" +
",32,172:4,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:32,12" +
"7:2,51,127,-1,127:2,-1:6,127:5,-1,127,-1:10,127,-1:6,127,-1,127:2,-1,165,17" +
"2:12,33,172:3,34,172:2,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5" +
",172,-1:6,111,-1:6,99:2,-1:2,99:2,-1:3,99:2,-1:8,127:2,-1,127,-1,127:2,-1:6" +
",127:5,113,79,-1:8,99:2,127,99,-1,99,-1:2,99,127,115,79:2,-1,172:9,47,172:1" +
"0,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,99:46,109,9" +
"9:20,56,-1,172:12,57,172:7,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,1" +
"62:5,172,-1:5,101:51,50,83,101:15,-1,172:5,58,172:14,162:6,-1:19,172,-1:3,1" +
"62,-1:3,162,172,162:2,-1,162:5,172,-1:63,85,-1,85,-1:8,172:13,59,172:6,162:" +
"6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:7,119,-1:2,119,-1:6" +
",119,-1,119,-1:2,119,-1:3,119,-1:3,119,-1:23,119,-1:5,119,-1:2,119,-1,119,-" +
"1,119:2,-1:5,172:5,60,172:14,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1" +
",162:5,172,-1:60,107,53,-1,107,-1,107,-1:8,172:5,61,172:14,162:6,-1:19,172," +
"-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:6,111,-1:6,99:2,-1:2,99:2,-1:3," +
"99:2,-1:26,113,99,-1:8,99:2,-1,99,-1,99,-1:2,99,-1,115,99:2,-1,172:5,63,172" +
":14,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:63,99,-1,99" +
",-1:8,172:15,64,172:4,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5," +
"172,-1:7,99,-1:2,99,-1:6,99,-1,99,-1:2,99,-1:3,99,-1:3,99,-1:23,99,-1:5,99," +
"-1:2,99,-1,99,-1,99:2,-1:5,172:20,162:6,-1:19,65,-1:3,162,-1:3,162,172,162:" +
"2,-1,162:5,172,-1:51,99,-1:18,115,-1:3,172:5,67,172:14,162:6,-1:19,172,-1:3" +
",162,-1:3,162,172,162:2,-1,162:5,172,-1:32,121,-1:27,84,-1:2,84,-1,84,-1:3," +
"121,-1:4,172:9,68,172:10,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162" +
":5,172,-1:7,119,-1:2,119,-1:6,119,-1,119,-1:2,119,-1:3,119,-1:3,119,-1:23,1" +
"19,-1:5,119,53,-1,119,-1,119,-1,119:2,-1:5,172:9,72,172:10,162:6,-1:19,172," +
"-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:5,73,172:14,162:6,-1:19,1" +
"72,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:19,74,162:6,-1:19,172," +
"-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:5,75,172:14,162:6,-1:19,1" +
"72,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:5,97,172:14,162:6,-1:1" +
"9,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,173:3,78,173:16,35:6,-1" +
",36:2,-1,36,-1,36,37,-1:6,38,36:4,173,36,-1:2,35,-1:3,35,173:3,36,173,35,17" +
"3,35:2,173,36,-1,36:2,-1,35:24,89,35,-1,36:2,-1,36,-1,36,37,-1:6,38,36:4,35" +
",36,-1:2,35,-1:3,35:4,36,35:6,36,-1,36:2,-1,172:9,100,172:10,162:6,-1:19,17" +
"2,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,173:15,87,173:4,35:6,-1,36:" +
"2,-1,36,-1,36,37,-1:6,38,36:4,173,36,-1:2,35,-1:3,35,173:3,36,173,35,173,35" +
":2,173,36,-1,36:2,-1,172:15,102,172:4,162:6,-1:19,172,-1:3,162,-1:3,162,172" +
",162:2,-1,162:5,172,-1:5,172:5,104,172:14,162:6,-1:19,172,-1:3,162,-1:3,162" +
",172,162:2,-1,162:5,172,-1:5,172:11,106,172:8,162:6,-1:19,172,-1:3,162,-1:3" +
",162,172,162:2,-1,162:5,172,-1:5,172:15,108,172:4,162:6,-1:19,172,-1:3,162," +
"-1:3,162,172,162:2,-1,162:5,172,-1:5,172:8,110,172:11,162:6,-1:19,172,-1:3," +
"162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:15,112,172:4,162:6,-1:19,172,-" +
"1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:16,114,172:3,162:6,-1:19,1" +
"72,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:4,116,172:15,162:6,-1:" +
"19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:8,118,172:11,162:6" +
",-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:4,120,172:15,1" +
"62:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:11,122,172" +
":8,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:13,123" +
",172:6,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:14" +
",124,172:5,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,17" +
"2:4,131,172:15,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:" +
"5,173:8,126,173:11,35:6,-1,36:2,-1,36,-1,36,37,-1:6,38,36:4,173,36,-1:2,35," +
"-1:3,35,173:3,36,173,35,173,35:2,173,36,-1,36:2,-1,35:23,128,35:2,-1,36:2,-" +
"1,36,-1,36,37,-1:6,38,36:4,35,36,-1:2,35,-1:3,35:4,36,35:6,36,-1,36:2,-1,17" +
"2:7,132,172:2,133,172:9,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:" +
"5,172,-1:5,173:4,130,173:15,35:6,-1,36:2,-1,36,-1,36,37,-1:6,38,36:4,173,36" +
",-1:2,35,-1:3,35,173:3,36,173,35,173,35:2,173,36,-1,36:2,-1,172:4,151,172:7" +
",134,172:7,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,17" +
"2:5,135,172:14,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:" +
"5,172:12,136,172:7,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172" +
",-1:5,172:3,138,172:16,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5" +
",172,-1:5,172,139,172:18,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162" +
":5,172,-1:5,172:3,140,172:16,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1" +
",162:5,172,-1:5,172:10,141,172:9,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:" +
"2,-1,162:5,172,-1:5,172:16,142,172:3,162:6,-1:19,172,-1:3,162,-1:3,162,172," +
"162:2,-1,162:5,172,-1:5,172:13,143,172:6,162:6,-1:19,172,-1:3,162,-1:3,162," +
"172,162:2,-1,162:5,172,-1:5,172:7,150,172:12,162:6,-1:19,172,-1:3,162,-1:3," +
"162,172,162:2,-1,162:5,172,-1:5,173:12,148,173:7,35:6,-1,36:2,-1,36,-1,36,3" +
"7,-1:6,38,36:4,173,36,-1:2,35,-1:3,35,173:3,36,173,35,173,35:2,173,36,-1,36" +
":2,-1,35:22,146,35:3,-1,36:2,-1,36,-1,36,37,-1:6,38,36:4,35,36,-1:2,35,-1:3" +
",35:4,36,35:6,36,-1,36:2,-1,172:2,152,172:17,162:6,-1:19,172,-1:3,162,-1:3," +
"162,172,162:2,-1,162:5,172,-1:5,172:8,166,172:8,167,172:2,162:6,-1:19,172,-" +
"1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:6,168,172:13,162:6,-1:19,1" +
"72,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:11,153,172:8,162:6,-1:" +
"19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:16,170,172:3,162:6" +
",-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:12,154,172:7,1" +
"62:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:9,155,172:" +
"10,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:9,171," +
"172:10,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:18" +
",156,172,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5,172:" +
"12,157,172:7,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-1:5," +
"172:5,164,172:14,162:6,-1:19,172,-1:3,162,-1:3,162,172,162:2,-1,162:5,172,-" +
"1:4");

	public int yylex ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {
				return YYEOF;
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -3:
						break;
					case 3:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -4:
						break;
					case 4:
						{yycolumn = yychar;  return Yyparser.L_BRACE;}
					case -5:
						break;
					case 5:
						{yycolumn = yychar;  return Yyparser.MINUS;}
					case -6:
						break;
					case 6:
						{
  Variable qv = new Variable("(" + yytext()+")");
  parser.yylval = qv;
  yycolumn = yychar;  return Yyparser.VARSYM;
}
					case -7:
						break;
					case 7:
						{yycolumn = yychar;  return Yyparser.R_BRACE;}
					case -8:
						break;
					case 8:
						{yycolumn = yychar;  return Yyparser.AT;}
					case -9:
						break;
					case 9:
						{yycolumn = yychar;  return Yyparser.IRREF;}
					case -10:
						break;
					case 10:
						{yycolumn = yychar;  return Yyparser.EQ;}
					case -11:
						break;
					case 11:
						{yycolumn = yychar; return Yyparser.POINT;}
					case -12:
						break;
					case 12:
						{yycolumn = yychar;  return Yyparser.L_PAREN;}
					case -13:
						break;
					case 13:
						{yycolumn = yychar;  return Yyparser.R_PAREN;}
					case -14:
						break;
					case 14:
						{yycolumn = yychar;  return Yyparser.L_BRACKET;}
					case -15:
						break;
					case 15:
						{yycolumn = yychar;  return Yyparser.R_BRACKET;}
					case -16:
						break;
					case 16:
						{yycolumn = yychar;  return Yyparser.SEMICOLON;}
					case -17:
						break;
					case 17:
						{yycolumn = yychar;  return Yyparser.COMMA;}
					case -18:
						break;
					case 18:
						{yycolumn = yychar;  return Yyparser.COLON;}
					case -19:
						break;
					case 19:
						{yycolumn = yychar; return Yyparser.R_ANGLE;}
					case -20:
						break;
					case 20:
						{yycolumn = yychar; return Yyparser.L_ANGLE;}
					case -21:
						break;
					case 21:
						{yycolumn = yychar;  return Yyparser.STRICT;}
					case -22:
						break;
					case 22:
						{yycolumn = yychar;  return Yyparser.BAR;}
					case -23:
						break;
					case 23:
						{yycolumn = yychar;  return Yyparser.LAMBDA;}
					case -24:
						break;
					case 24:
						{yycolumn = yychar;  return Yyparser.WILDCARD;}
					case -25:
						break;
					case 25:
						{yycolumn = yychar;  return Yyparser.INFIXOP;}
					case -26:
						break;
					case 26:
						{System.err.println("Illegal input to Yylex, exiting"); yy_error(0,true); }
					case -27:
						break;
					case 27:
						{yychar = 0; return yylex(); }
					case -28:
						break;
					case 28:
						{ 
  parser.yylval= new IntegerLiteral(Integer.parseInt(yytext()));
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.INTEGER;
}
					case -29:
						break;
					case 29:
						{yycolumn = yychar; return yylex();}
					case -30:
						break;
					case 30:
						{yycolumn = yychar;  return Yyparser.OF;}
					case -31:
						break;
					case 31:
						{yycolumn = yychar;  return Yyparser.DO;}
					case -32:
						break;
					case 32:
						{yycolumn = yychar;  return Yyparser.AS;}
					case -33:
						break;
					case 33:
						{yycolumn = yychar;  return Yyparser.IN;}
					case -34:
						break;
					case 34:
						{yycolumn = yychar;  return Yyparser.IF;}
					case -35:
						break;
					case 35:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedConstructor qv =null;
  if(nidx > -1) {
    qv = new QualifiedConstructor(txt.substring(nidx+1));
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),".");
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedConstructor(txt);
  }
  /* set yylval */
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QCONID;
}
					case -36:
						break;
					case 36:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedVariable qv =null;
  if(nidx > -1) {
    qv = new QualifiedVariable("(" + txt.substring(nidx+1)+")");
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),"."); 
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedVariable(txt);
  }
  parser.yylval = qv; 
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QVARSYM;
}
					case -37:
						break;
					case 37:
						{ yycolumn = yychar;  return Yyparser.QUALIFIED; }
					case -38:
						break;
					case 38:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedConstructor qv =  null;
  if(nidx > -1) {
    qv = new QualifiedConstructor("(" + txt.substring(nidx+1)+")");
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),"."); 
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedConstructor(txt);
  }
  parser.yylval = qv; 
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QCONSYM;
}
					case -39:
						break;
					case 39:
						{yycolumn = yychar;  return Yyparser.OPENCOMMENT;}
					case -40:
						break;
					case 40:
						{yycolumn = yychar;  return Yyparser.CLOSECOMMENT;}
					case -41:
						break;
					case 41:
						{yycolumn = yychar;  return Yyparser.FUNOP;}
					case -42:
						break;
					case 42:
						{yycolumn = yychar;  return Yyparser.CONTEXT_OP;}
					case -43:
						break;
					case 43:
						{yycolumn = yychar;  return Yyparser.ENUM;}
					case -44:
						break;
					case 44:
						{
  Constructor qv = new Constructor("(" + yytext()+")");
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONSYM;
 }
					case -45:
						break;
					case 45:
						{yycolumn = yychar;  return Yyparser.DOUBLECOLON;}
					case -46:
						break;
					case 46:
						{yycolumn = yychar;  return Yyparser.QUALOP;}
					case -47:
						break;
					case 47:
						{yycolumn = yychar;  return Yyparser.LET;}
					case -48:
						break;
					case 48:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedVariable qv = null;
  if(nidx > -1) {
    qv = new QualifiedVariable(txt.substring(nidx+1));
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),".");
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedVariable(txt);
  }
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QVARID;
}
					case -49:
						break;
					case 49:
						{yycolumn = yychar;  return Yyparser.OPENPRAGMA;}
					case -50:
						break;
					case 50:
						{ yycolumn = yychar;  return yylex(); /* single line comments */ }
					case -51:
						break;
					case 51:
						{yycolumn = yychar;  return Yyparser.CLOSEPRAGMA;}
					case -52:
						break;
					case 52:
						{ 
  parser.yylval= new FloatLiteral(Float.parseFloat(yytext()));
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.FLOAT;
}
					case -53:
						break;
					case 53:
						{ 
  parser.yylval= new CharLiteral(yytext().charAt(1));
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CHAR;
}
					case -54:
						break;
					case 54:
						{ 
  parser.yylval= new IntegerLiteral(Integer.parseInt(yytext(),8));
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.INTEGER;
}
					case -55:
						break;
					case 55:
						{ 
  parser.yylval= new IntegerLiteral(Integer.parseInt(yytext().substring(2),16));
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.INTEGER;
}
					case -56:
						break;
					case 56:
						{
parser.yylval = new StringLiteral(yytext().substring(1,yytext().length()-1));
  parser.yylval.putTag(makePosition());
yycolumn = yychar;  return Yyparser.STRING;
}
					case -57:
						break;
					case 57:
						{yycolumn = yychar;  return Yyparser.DATA;}
					case -58:
						break;
					case 58:
						{yycolumn = yychar;  return Yyparser.ELSE;}
					case -59:
						break;
					case 59:
						{yycolumn = yychar;  return Yyparser.THEN;}
					case -60:
						break;
					case 60:
						{yycolumn = yychar;  return Yyparser.TYPE;}
					case -61:
						break;
					case 61:
						{yycolumn = yychar;  return Yyparser.CASE;}
					case -62:
						break;
					case 62:
						{ 
 parser.yylval= new BooleanLiteral(true);
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.BOOLEAN;
}
					case -63:
						break;
					case 63:
						{yycolumn = yychar; return Yyparser.WHERE;}
					case -64:
						break;
					case 64:
						{yycolumn = yychar;  return Yyparser.CLASS;}
					case -65:
						break;
					case 65:
						{yycolumn = yychar;  return Yyparser.INFIX;}
					case -66:
						break;
					case 66:
						{  
  parser.yylval= new BooleanLiteral(false);
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.BOOLEAN;
}
					case -67:
						break;
					case 67:
						{yycolumn = yychar; return Yyparser.MODULE;}
					case -68:
						break;
					case 68:
						{yycolumn = yychar;  return Yyparser.IMPORT;}
					case -69:
						break;
					case 69:
						{yycolumn = yychar;  return Yyparser.INFIXL;}
					case -70:
						break;
					case 70:
						{yycolumn = yychar;  return Yyparser.INFIXR;}
					case -71:
						break;
					case 71:
						{ yycolumn = yychar;  return Yyparser.NATIVE; }
					case -72:
						break;
					case 72:
						{yycolumn = yychar;  return Yyparser.DEFAULT;}
					case -73:
						break;
					case 73:
						{yycolumn = yychar;  return Yyparser.NEWTYPE;}
					case -74:
						break;
					case 74:
						{yycolumn = yychar;  return Yyparser.DERIVING;}
					case -75:
						break;
					case 75:
						{yycolumn = yychar;  return Yyparser.INSTANCE;}
					case -76:
						break;
					case 77:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -77:
						break;
					case 78:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -78:
						break;
					case 79:
						{
  Variable qv = new Variable("(" + yytext()+")");
  parser.yylval = qv;
  yycolumn = yychar;  return Yyparser.VARSYM;
}
					case -79:
						break;
					case 80:
						{System.err.println("Illegal input to Yylex, exiting"); yy_error(0,true); }
					case -80:
						break;
					case 81:
						{ 
  parser.yylval= new IntegerLiteral(Integer.parseInt(yytext()));
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.INTEGER;
}
					case -81:
						break;
					case 82:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedConstructor qv =null;
  if(nidx > -1) {
    qv = new QualifiedConstructor(txt.substring(nidx+1));
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),".");
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedConstructor(txt);
  }
  /* set yylval */
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QCONID;
}
					case -82:
						break;
					case 83:
						{ yycolumn = yychar;  return yylex(); /* single line comments */ }
					case -83:
						break;
					case 84:
						{ 
  parser.yylval= new FloatLiteral(Float.parseFloat(yytext()));
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.FLOAT;
}
					case -84:
						break;
					case 86:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -85:
						break;
					case 87:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -86:
						break;
					case 88:
						{
  Variable qv = new Variable("(" + yytext()+")");
  parser.yylval = qv;
  yycolumn = yychar;  return Yyparser.VARSYM;
}
					case -87:
						break;
					case 89:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedConstructor qv =null;
  if(nidx > -1) {
    qv = new QualifiedConstructor(txt.substring(nidx+1));
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),".");
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedConstructor(txt);
  }
  /* set yylval */
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QCONID;
}
					case -88:
						break;
					case 91:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -89:
						break;
					case 92:
						{
  Variable qv = new Variable("(" + yytext()+")");
  parser.yylval = qv;
  yycolumn = yychar;  return Yyparser.VARSYM;
}
					case -90:
						break;
					case 94:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -91:
						break;
					case 95:
						{
  Variable qv = new Variable("(" + yytext()+")");
  parser.yylval = qv;
  yycolumn = yychar;  return Yyparser.VARSYM;
}
					case -92:
						break;
					case 97:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -93:
						break;
					case 98:
						{
  Variable qv = new Variable("(" + yytext()+")");
  parser.yylval = qv;
  yycolumn = yychar;  return Yyparser.VARSYM;
}
					case -94:
						break;
					case 100:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -95:
						break;
					case 102:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -96:
						break;
					case 104:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -97:
						break;
					case 106:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -98:
						break;
					case 108:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -99:
						break;
					case 110:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -100:
						break;
					case 112:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -101:
						break;
					case 114:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -102:
						break;
					case 116:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -103:
						break;
					case 118:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -104:
						break;
					case 120:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -105:
						break;
					case 122:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -106:
						break;
					case 123:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -107:
						break;
					case 124:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -108:
						break;
					case 125:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -109:
						break;
					case 126:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -110:
						break;
					case 127:
						{
  Variable qv = new Variable("(" + yytext()+")");
  parser.yylval = qv;
  yycolumn = yychar;  return Yyparser.VARSYM;
}
					case -111:
						break;
					case 128:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedConstructor qv =null;
  if(nidx > -1) {
    qv = new QualifiedConstructor(txt.substring(nidx+1));
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),".");
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedConstructor(txt);
  }
  /* set yylval */
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QCONID;
}
					case -112:
						break;
					case 129:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -113:
						break;
					case 130:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -114:
						break;
					case 131:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -115:
						break;
					case 132:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -116:
						break;
					case 133:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -117:
						break;
					case 134:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -118:
						break;
					case 135:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -119:
						break;
					case 136:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -120:
						break;
					case 137:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -121:
						break;
					case 138:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -122:
						break;
					case 139:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -123:
						break;
					case 140:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -124:
						break;
					case 141:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -125:
						break;
					case 142:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -126:
						break;
					case 143:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -127:
						break;
					case 144:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -128:
						break;
					case 145:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -129:
						break;
					case 146:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedConstructor qv =null;
  if(nidx > -1) {
    qv = new QualifiedConstructor(txt.substring(nidx+1));
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),".");
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedConstructor(txt);
  }
  /* set yylval */
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QCONID;
}
					case -130:
						break;
					case 147:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -131:
						break;
					case 148:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -132:
						break;
					case 149:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -133:
						break;
					case 150:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -134:
						break;
					case 151:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -135:
						break;
					case 152:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -136:
						break;
					case 153:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -137:
						break;
					case 154:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -138:
						break;
					case 155:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -139:
						break;
					case 156:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -140:
						break;
					case 157:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -141:
						break;
					case 158:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -142:
						break;
					case 159:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -143:
						break;
					case 160:
						{ 
  String txt = yytext();
  int nidx = txt.lastIndexOf('.');
  QualifiedConstructor qv =null;
  if(nidx > -1) {
    qv = new QualifiedConstructor(txt.substring(nidx+1));
    StringTokenizer st = new StringTokenizer(txt.substring(nidx),".");
    while(st.hasMoreTokens()) 
      qv.addPathElement(st.nextToken());
  } else { 
    qv = new QualifiedConstructor(txt);
  }
  /* set yylval */
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.QCONID;
}
					case -144:
						break;
					case 161:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -145:
						break;
					case 162:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -146:
						break;
					case 163:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -147:
						break;
					case 164:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -148:
						break;
					case 165:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -149:
						break;
					case 166:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -150:
						break;
					case 167:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -151:
						break;
					case 168:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -152:
						break;
					case 169:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -153:
						break;
					case 170:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -154:
						break;
					case 171:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -155:
						break;
					case 172:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -156:
						break;
					case 173:
						{
  Constructor qv = new Constructor(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.CONID;
 }
					case -157:
						break;
					case 174:
						{
  Variable qv = new Variable(yytext());
  parser.yylval = qv;
  parser.yylval.putTag(makePosition());
  yycolumn = yychar;  return Yyparser.VARID;
 }
					case -158:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
