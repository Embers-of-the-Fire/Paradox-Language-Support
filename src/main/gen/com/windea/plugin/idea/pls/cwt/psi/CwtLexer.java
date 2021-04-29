/* The following code was generated by JFlex 1.7.0 tweaked for IntelliJ platform */

package com.windea.plugin.idea.pls.cwt.psi;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static com.windea.plugin.idea.pls.cwt.psi.CwtTypes.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.7.0
 * from the specification file <tt>CwtLexer.flex</tt>
 */
public class CwtLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int WAITING_PROPERTY_KEY = 2;
  public static final int WATIING_PROPERTY_SEPARATOR = 4;
  public static final int WAITING_PROPERTY_VALUE = 6;
  public static final int WAITING_PROPERTY_VALUE_END = 8;
  public static final int WAITING_PROPERTY_END = 10;
  public static final int WAITING_OPTION_KEY = 12;
  public static final int WATIING_OPTION_SEPARATOR = 14;
  public static final int WAITING_OPTION_VALUE = 16;
  public static final int WAITING_OPTION_VALUE_END = 18;
  public static final int WAITING_OPTION_END = 20;
  public static final int WAITING_OPTION = 22;
  public static final int WAITING_DOCUMENTATION = 24;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6,  6,  7,  7, 
     8,  8,  9,  9, 10, 10, 11, 11, 12, 12
  };

  /** 
   * Translates characters to character classes
   * Chosen bits are [7, 7, 7]
   * Total runtime size is 1928 bytes
   */
  public static int ZZ_CMAP(int ch) {
    return ZZ_CMAP_A[(ZZ_CMAP_Y[ZZ_CMAP_Z[ch>>14]|((ch>>7)&0x7f)]<<7)|(ch&0x7f)];
  }

  /* The ZZ_CMAP_Z table has 68 entries */
  static final char ZZ_CMAP_Z[] = zzUnpackCMap(
    "\1\0\103\200");

  /* The ZZ_CMAP_Y table has 256 entries */
  static final char ZZ_CMAP_Y[] = zzUnpackCMap(
    "\1\0\1\1\53\2\1\3\22\2\1\4\37\2\1\3\237\2");

  /* The ZZ_CMAP_A table has 640 entries */
  static final char ZZ_CMAP_A[] = zzUnpackCMap(
    "\10\0\1\6\1\4\1\2\1\5\1\7\1\3\22\0\1\4\1\0\1\12\1\10\7\0\1\21\1\0\1\21\1\24"+
    "\1\0\1\22\11\23\2\0\1\30\1\25\1\31\35\0\1\13\10\0\1\15\10\0\1\17\1\20\3\0"+
    "\1\16\5\0\1\14\1\0\1\26\1\0\1\27\7\0\1\1\32\0\1\11\337\0\1\11\177\0\13\11"+
    "\35\0\2\1\5\0\1\11\57\0\1\11\40\0");

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\14\0\1\1\1\2\1\3\2\4\1\5\1\3\3\2"+
    "\2\6\1\7\1\10\1\11\1\12\2\13\2\4\1\13"+
    "\1\12\1\14\1\15\1\5\1\16\1\17\1\4\1\3"+
    "\3\17\2\20\1\13\3\21\1\22\1\4\1\22\1\23"+
    "\1\24\1\25\1\3\1\26\1\4\1\3\3\26\2\27"+
    "\1\30\1\13\1\31\1\21\1\32\2\4\1\13\1\3"+
    "\3\32\2\33\1\34\1\1\1\13\1\4\1\13\2\0"+
    "\1\5\1\35\3\0\1\2\1\0\1\7\1\2\1\36"+
    "\1\2\1\0\1\12\1\0\1\17\1\0\1\17\1\37"+
    "\1\17\1\0\1\22\1\40\1\0\1\26\1\0\1\26"+
    "\1\41\1\26\5\0\1\32\1\0\1\34\1\32\1\42"+
    "\1\32\1\43\1\2\1\0\1\44\1\12\1\0\1\45"+
    "\1\22\1\0\1\46\1\32\1\0\1\47";

  private static int [] zzUnpackAction() {
    int [] result = new int[138];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\32\0\64\0\116\0\150\0\202\0\234\0\266"+
    "\0\320\0\352\0\u0104\0\u011e\0\u0138\0\u0152\0\u016c\0\u0186"+
    "\0\u01a0\0\u01ba\0\u01d4\0\u01ee\0\u0208\0\u0222\0\u023c\0\u0256"+
    "\0\u016c\0\u016c\0\u016c\0\u0270\0\u016c\0\u028a\0\u02a4\0\u02be"+
    "\0\u02a4\0\u02d8\0\u016c\0\u016c\0\u02f2\0\u016c\0\u030c\0\u0326"+
    "\0\u0340\0\u035a\0\u0374\0\u038e\0\u03a8\0\u03c2\0\u03dc\0\u016c"+
    "\0\u03f6\0\u0410\0\u042a\0\u0444\0\u045e\0\u016c\0\u016c\0\u016c"+
    "\0\u0478\0\u0492\0\u04ac\0\u04c6\0\u04e0\0\u04fa\0\u0514\0\u052e"+
    "\0\u0548\0\u0562\0\u0562\0\u057c\0\u057c\0\u0596\0\u05b0\0\u05ca"+
    "\0\u05b0\0\u05e4\0\u05fe\0\u0618\0\u0632\0\u064c\0\u0666\0\u016c"+
    "\0\u0680\0\u0680\0\u069a\0\u069a\0\u06b4\0\u06ce\0\u06e8\0\u0702"+
    "\0\u01d4\0\u071c\0\u0736\0\u06ce\0\u0750\0\u071c\0\u076a\0\u0152"+
    "\0\u0784\0\u079e\0\u07b8\0\u0340\0\u016c\0\u07d2\0\u07ec\0\u030c"+
    "\0\u0806\0\u0820\0\u083a\0\u016c\0\u04c6\0\u016c\0\u0854\0\u086e"+
    "\0\u0492\0\u0888\0\u08a2\0\u08bc\0\u05e4\0\u08d6\0\u08f0\0\u08bc"+
    "\0\u090a\0\u08d6\0\u0924\0\u0596\0\u093e\0\u016c\0\u06b4\0\u0958"+
    "\0\u0784\0\u016c\0\u0972\0\u0806\0\u016c\0\u098c\0\u0888\0\u08a2"+
    "\0\u09a6\0\u093e";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[138];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\16\1\17\2\20\1\21\1\20\1\16\1\21\1\22"+
    "\1\17\1\23\1\16\1\24\2\16\1\25\1\16\1\26"+
    "\1\27\1\30\1\16\1\31\1\32\1\33\2\16\1\34"+
    "\2\35\1\36\1\37\1\35\1\40\1\41\2\17\1\42"+
    "\12\34\1\17\1\43\1\44\2\34\1\17\2\35\1\36"+
    "\1\37\1\35\1\37\1\41\1\45\14\17\1\46\1\43"+
    "\1\44\2\17\1\47\2\35\1\36\1\37\1\35\1\50"+
    "\1\41\1\22\1\17\1\51\1\47\1\52\2\47\1\53"+
    "\1\47\1\54\1\55\1\56\1\47\1\17\1\43\1\44"+
    "\2\47\1\17\2\35\1\36\1\57\1\35\2\57\1\45"+
    "\15\17\1\43\1\44\3\17\2\60\1\61\1\62\1\60"+
    "\2\62\1\45\15\17\1\43\1\44\2\17\1\63\2\35"+
    "\1\36\1\37\1\35\1\64\1\41\2\17\1\65\12\63"+
    "\1\17\1\66\1\67\2\63\1\17\2\35\1\36\1\37"+
    "\1\35\1\37\1\41\1\45\14\17\1\70\1\66\1\67"+
    "\1\71\1\17\1\72\2\35\1\36\1\37\1\35\1\73"+
    "\1\41\1\22\1\17\1\74\1\72\1\75\2\72\1\76"+
    "\1\72\1\77\1\100\1\101\1\72\1\17\1\66\1\67"+
    "\2\72\1\17\2\35\1\36\1\102\1\35\1\102\1\103"+
    "\1\45\15\17\1\66\1\67\3\17\2\60\1\61\1\104"+
    "\1\60\1\104\1\105\1\45\15\17\1\66\1\67\2\17"+
    "\1\106\2\35\1\36\1\107\1\35\1\110\1\111\1\45"+
    "\1\17\1\112\1\106\1\113\2\106\1\114\1\106\1\115"+
    "\1\116\1\117\1\106\1\120\1\32\1\33\2\106\1\121"+
    "\1\122\1\35\1\36\1\123\1\122\1\123\1\124\22\121"+
    "\1\16\3\0\1\125\1\0\1\16\1\125\1\16\1\0"+
    "\1\126\12\16\1\31\2\0\2\16\34\0\4\20\1\0"+
    "\1\20\24\0\2\20\1\21\1\20\1\125\1\21\15\0"+
    "\1\31\4\0\2\127\2\0\4\127\1\130\21\127\1\131"+
    "\1\132\2\0\1\133\1\132\1\131\1\133\1\131\1\132"+
    "\1\134\1\135\11\131\1\136\2\132\2\131\1\16\3\0"+
    "\1\125\1\0\1\16\1\125\1\16\1\0\1\126\2\16"+
    "\1\137\7\16\1\31\2\0\3\16\3\0\1\125\1\0"+
    "\1\16\1\125\1\16\1\0\1\126\5\16\1\140\4\16"+
    "\1\31\2\0\3\16\3\0\1\125\1\0\1\16\1\125"+
    "\1\16\1\0\1\126\7\16\1\27\1\30\1\16\1\31"+
    "\2\0\3\16\3\0\1\125\1\0\1\16\1\125\1\16"+
    "\1\0\1\126\11\16\1\141\1\31\2\0\3\16\3\0"+
    "\1\125\1\0\1\16\1\125\1\16\1\0\1\126\7\16"+
    "\2\30\1\141\1\31\2\0\2\16\1\34\5\0\1\34"+
    "\1\0\1\34\1\0\13\34\3\0\2\34\2\0\1\35"+
    "\33\0\1\37\1\0\2\37\22\0\1\34\3\0\1\37"+
    "\1\0\1\40\1\37\1\34\1\0\13\34\3\0\2\34"+
    "\1\42\1\142\2\0\2\142\1\42\1\142\1\42\1\142"+
    "\1\34\1\143\11\42\3\142\2\42\2\127\2\0\4\127"+
    "\1\0\21\127\1\47\5\0\1\47\1\0\1\47\2\0"+
    "\12\47\3\0\3\47\3\0\1\37\1\0\1\50\1\37"+
    "\1\47\2\0\12\47\3\0\2\47\2\144\2\0\6\144"+
    "\1\145\1\146\16\144\1\47\5\0\1\47\1\0\1\47"+
    "\2\0\2\47\1\147\7\47\3\0\3\47\5\0\1\47"+
    "\1\0\1\47\2\0\5\47\1\150\4\47\3\0\3\47"+
    "\5\0\1\47\1\0\1\47\2\0\7\47\1\55\1\56"+
    "\1\47\3\0\3\47\5\0\1\47\1\0\1\47\2\0"+
    "\11\47\1\151\3\0\3\47\5\0\1\47\1\0\1\47"+
    "\2\0\7\47\2\56\1\151\3\0\2\47\4\0\1\57"+
    "\1\0\2\57\24\0\1\60\33\0\1\62\1\0\2\62"+
    "\22\0\1\63\5\0\1\63\1\0\1\63\1\0\13\63"+
    "\3\0\3\63\3\0\1\37\1\0\1\64\1\37\1\63"+
    "\1\0\13\63\3\0\2\63\1\65\1\152\2\0\2\152"+
    "\1\65\1\152\1\65\1\152\1\63\1\153\11\65\3\152"+
    "\2\65\31\0\1\154\1\72\5\0\1\72\1\0\1\72"+
    "\2\0\12\72\3\0\3\72\3\0\1\37\1\0\1\73"+
    "\1\37\1\72\2\0\12\72\3\0\2\72\2\155\2\0"+
    "\6\155\1\156\1\157\16\155\1\72\5\0\1\72\1\0"+
    "\1\72\2\0\2\72\1\160\7\72\3\0\3\72\5\0"+
    "\1\72\1\0\1\72\2\0\5\72\1\161\4\72\3\0"+
    "\3\72\5\0\1\72\1\0\1\72\2\0\7\72\1\100"+
    "\1\101\1\72\3\0\3\72\5\0\1\72\1\0\1\72"+
    "\2\0\11\72\1\162\3\0\3\72\5\0\1\72\1\0"+
    "\1\72\2\0\7\72\2\101\1\162\3\0\2\72\4\0"+
    "\1\102\1\0\2\102\26\0\1\104\1\0\2\104\22\0"+
    "\1\106\3\0\1\163\1\0\1\106\1\163\1\106\1\0"+
    "\1\164\12\106\1\120\2\0\2\106\4\0\1\107\1\0"+
    "\2\107\15\0\1\120\4\0\1\106\3\0\1\107\1\0"+
    "\1\110\1\107\1\106\1\0\1\164\12\106\1\120\2\0"+
    "\2\106\1\165\1\166\2\0\1\167\1\166\1\165\1\167"+
    "\1\165\1\166\1\170\1\171\11\165\1\172\2\166\2\165"+
    "\1\106\3\0\1\163\1\0\1\106\1\163\1\106\1\0"+
    "\1\164\2\106\1\173\7\106\1\120\2\0\3\106\3\0"+
    "\1\163\1\0\1\106\1\163\1\106\1\0\1\164\5\106"+
    "\1\174\4\106\1\120\2\0\3\106\3\0\1\163\1\0"+
    "\1\106\1\163\1\106\1\0\1\164\7\106\1\116\1\117"+
    "\1\106\1\120\2\0\3\106\3\0\1\163\1\0\1\106"+
    "\1\163\1\106\1\0\1\164\11\106\1\175\1\120\2\0"+
    "\3\106\3\0\1\163\1\0\1\106\1\163\1\106\1\0"+
    "\1\164\7\106\2\117\1\175\1\120\2\0\2\106\2\121"+
    "\2\0\30\121\2\0\1\123\1\121\2\123\22\121\4\0"+
    "\1\125\1\0\2\125\15\0\1\31\4\0\1\126\3\0"+
    "\1\125\1\0\1\126\1\125\1\126\1\0\13\126\1\31"+
    "\2\0\2\126\2\127\2\0\26\127\10\0\1\176\21\0"+
    "\2\132\2\0\6\132\1\177\1\200\20\132\2\0\1\133"+
    "\1\132\2\133\2\132\1\177\1\200\11\132\1\136\4\132"+
    "\1\131\3\0\1\133\1\0\1\131\1\125\1\131\1\132"+
    "\13\131\1\136\2\132\2\131\1\16\3\0\1\125\1\0"+
    "\1\16\1\125\1\16\1\0\1\126\3\16\1\140\6\16"+
    "\1\31\2\0\3\16\3\0\1\125\1\0\1\16\1\125"+
    "\1\16\1\0\1\126\7\16\2\201\1\16\1\31\2\0"+
    "\2\16\2\142\2\0\6\142\1\202\1\203\16\142\1\42"+
    "\3\0\1\142\1\0\1\42\1\0\1\42\1\142\13\42"+
    "\3\142\2\42\1\144\3\0\1\144\1\0\1\144\1\0"+
    "\22\144\1\47\5\0\1\47\1\0\1\47\2\0\3\47"+
    "\1\150\6\47\3\0\3\47\5\0\1\47\1\0\1\47"+
    "\2\0\7\47\2\204\1\47\3\0\2\47\2\152\2\0"+
    "\6\152\1\205\1\206\16\152\1\65\3\0\1\152\1\0"+
    "\1\65\1\0\1\65\1\152\13\65\3\152\2\65\1\155"+
    "\3\0\1\155\1\0\1\155\1\0\22\155\1\72\5\0"+
    "\1\72\1\0\1\72\2\0\3\72\1\161\6\72\3\0"+
    "\3\72\5\0\1\72\1\0\1\72\2\0\7\72\2\207"+
    "\1\72\3\0\2\72\4\0\1\163\1\0\2\163\15\0"+
    "\1\120\4\0\1\164\3\0\1\163\1\0\1\164\1\163"+
    "\1\164\1\0\13\164\1\120\2\0\2\164\2\166\2\0"+
    "\6\166\1\210\1\211\20\166\2\0\1\167\1\166\2\167"+
    "\2\166\1\210\1\211\11\166\1\172\4\166\1\165\3\0"+
    "\1\167\1\0\1\165\1\163\1\165\1\166\13\165\1\172"+
    "\2\166\2\165\1\106\3\0\1\163\1\0\1\106\1\163"+
    "\1\106\1\0\1\164\3\106\1\174\6\106\1\120\2\0"+
    "\3\106\3\0\1\163\1\0\1\106\1\163\1\106\1\0"+
    "\1\164\7\106\2\212\1\106\1\120\2\0\2\106\1\132"+
    "\3\0\1\132\1\0\1\132\1\0\22\132\1\142\3\0"+
    "\1\142\1\0\1\142\1\0\22\142\1\152\3\0\1\152"+
    "\1\0\1\152\1\0\22\152\1\166\3\0\1\166\1\0"+
    "\1\166\1\0\22\166";

  private static int [] zzUnpackTrans() {
    int [] result = new int[2496];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\14\0\2\1\1\11\11\1\3\11\1\1\1\11\5\1"+
    "\2\11\1\1\1\11\11\1\1\11\5\1\3\11\27\1"+
    "\1\11\4\1\2\0\2\1\3\0\1\1\1\0\4\1"+
    "\1\0\1\1\1\0\1\11\1\0\3\1\1\0\1\1"+
    "\1\11\1\0\1\11\1\0\3\1\5\0\1\1\1\0"+
    "\4\1\1\11\1\1\1\0\1\1\1\11\1\0\1\1"+
    "\1\11\1\0\2\1\1\0\1\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[138];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  public CwtLexer() {
    this((java.io.Reader)null);
  }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public CwtLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    int size = 0;
    for (int i = 0, length = packed.length(); i < length; i += 2) {
      size += packed.charAt(i);
    }
    char[] map = new char[size];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < packed.length()) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      {@code false}, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position {@code pos} from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + ZZ_CMAP(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { yybegin(YYINITIAL); return DOCUMENTATION_TOKEN;
            } 
            // fall through
          case 40: break;
          case 2: 
            { yybegin(WAITING_PROPERTY_VALUE_END); return STRING_TOKEN;
            } 
            // fall through
          case 41: break;
          case 3: 
            { return BAD_CHARACTER;
            } 
            // fall through
          case 42: break;
          case 4: 
            { return WHITE_SPACE;
            } 
            // fall through
          case 43: break;
          case 5: 
            { return COMMENT;
            } 
            // fall through
          case 44: break;
          case 6: 
            { yybegin(WAITING_PROPERTY_VALUE_END); return INT_TOKEN;
            } 
            // fall through
          case 45: break;
          case 7: 
            { yypushback(yylength()); yybegin(WAITING_PROPERTY_KEY);
            } 
            // fall through
          case 46: break;
          case 8: 
            { return LEFT_BRACE;
            } 
            // fall through
          case 47: break;
          case 9: 
            { return RIGHT_BRACE;
            } 
            // fall through
          case 48: break;
          case 10: 
            { yybegin(WATIING_PROPERTY_SEPARATOR); return PROPERTY_KEY_TOKEN;
            } 
            // fall through
          case 49: break;
          case 11: 
            { yybegin(YYINITIAL); return WHITE_SPACE;
            } 
            // fall through
          case 50: break;
          case 12: 
            { yybegin(YYINITIAL); return LEFT_BRACE;
            } 
            // fall through
          case 51: break;
          case 13: 
            { yybegin(YYINITIAL); return RIGHT_BRACE;
            } 
            // fall through
          case 52: break;
          case 14: 
            { yybegin(WAITING_PROPERTY_VALUE); return EQUAL_SIGN;
            } 
            // fall through
          case 53: break;
          case 15: 
            { yybegin(WAITING_PROPERTY_END); return STRING_TOKEN;
            } 
            // fall through
          case 54: break;
          case 16: 
            { yybegin(WAITING_PROPERTY_END); return INT_TOKEN;
            } 
            // fall through
          case 55: break;
          case 17: 
            { yybegin(YYINITIAL);  return WHITE_SPACE;
            } 
            // fall through
          case 56: break;
          case 18: 
            { yybegin(WATIING_OPTION_SEPARATOR); return OPTION_KEY_TOKEN;
            } 
            // fall through
          case 57: break;
          case 19: 
            { yybegin(WAITING_OPTION); return LEFT_BRACE;
            } 
            // fall through
          case 58: break;
          case 20: 
            { yybegin(WAITING_OPTION); return RIGHT_BRACE;
            } 
            // fall through
          case 59: break;
          case 21: 
            { yybegin(WAITING_OPTION_VALUE); return EQUAL_SIGN;
            } 
            // fall through
          case 60: break;
          case 22: 
            { yybegin(WAITING_OPTION_END); return STRING_TOKEN;
            } 
            // fall through
          case 61: break;
          case 23: 
            { yybegin(WAITING_OPTION_END); return INT_TOKEN;
            } 
            // fall through
          case 62: break;
          case 24: 
            { yybegin(WAITING_OPTION); return WHITE_SPACE;
            } 
            // fall through
          case 63: break;
          case 25: 
            { yybegin(WAITING_OPTION);  return WHITE_SPACE;
            } 
            // fall through
          case 64: break;
          case 26: 
            { yybegin(WAITING_OPTION_VALUE_END); return STRING_TOKEN;
            } 
            // fall through
          case 65: break;
          case 27: 
            { yybegin(WAITING_OPTION_VALUE_END); return INT_TOKEN;
            } 
            // fall through
          case 66: break;
          case 28: 
            { yypushback(yylength()); yybegin(WAITING_OPTION_KEY);
            } 
            // fall through
          case 67: break;
          case 29: 
            { yybegin(WAITING_OPTION); return OPTION_START;
            } 
            // fall through
          case 68: break;
          case 30: 
            { yybegin(WAITING_PROPERTY_VALUE_END); return BOOLEAN_TOKEN;
            } 
            // fall through
          case 69: break;
          case 31: 
            { yybegin(WAITING_PROPERTY_END); return BOOLEAN_TOKEN;
            } 
            // fall through
          case 70: break;
          case 32: 
            { yybegin(WAITING_OPTION_VALUE); return NOT_EQUAL_SIGN;
            } 
            // fall through
          case 71: break;
          case 33: 
            { yybegin(WAITING_OPTION_END); return BOOLEAN_TOKEN;
            } 
            // fall through
          case 72: break;
          case 34: 
            { yybegin(WAITING_OPTION_VALUE_END); return BOOLEAN_TOKEN;
            } 
            // fall through
          case 73: break;
          case 35: 
            { yybegin(WAITING_DOCUMENTATION); return DOCUMENTATION_START;
            } 
            // fall through
          case 74: break;
          case 36: 
            { yybegin(WAITING_PROPERTY_VALUE_END); return FLOAT_TOKEN;
            } 
            // fall through
          case 75: break;
          case 37: 
            { yybegin(WAITING_PROPERTY_END); return FLOAT_TOKEN;
            } 
            // fall through
          case 76: break;
          case 38: 
            { yybegin(WAITING_OPTION_END); return FLOAT_TOKEN;
            } 
            // fall through
          case 77: break;
          case 39: 
            { yybegin(WAITING_OPTION_VALUE_END); return FLOAT_TOKEN;
            } 
            // fall through
          case 78: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
