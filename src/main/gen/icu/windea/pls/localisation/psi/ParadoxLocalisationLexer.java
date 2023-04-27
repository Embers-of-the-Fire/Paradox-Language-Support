// Generated by JFlex 1.9.1 http://jflex.de/  (tweaked for IntelliJ platform)
// source: ParadoxLocalisationLexer.flex

package icu.windea.pls.localisation.psi;


import com.intellij.lexer.*;
import com.intellij.psi.tree.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static icu.windea.pls.core.StdlibExtensionsKt.isExactDigit;
import static icu.windea.pls.core.StdlibExtensionsKt.isExactLetter;
import static icu.windea.pls.localisation.psi.ParadoxLocalisationElementTypes.*;


public class ParadoxLocalisationLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int WAITING_LOCALE_COLON = 2;
  public static final int WAITING_LOCALE_END = 4;
  public static final int WAITING_PROPERTY_COLON = 6;
  public static final int WAITING_PROPERTY_NUMBER = 8;
  public static final int WAITING_PROPERTY_VALUE = 10;
  public static final int WAITING_PROPERTY_END = 12;
  public static final int WAITING_RICH_TEXT = 14;
  public static final int WAITING_PROPERTY_REFERENCE = 16;
  public static final int WAITING_PROPERTY_REFERENCE_PARAMETER_TOKEN = 18;
  public static final int WAITING_SCRIPTED_VARIABLE_REFERENCE_NAME = 20;
  public static final int WAITING_ICON = 22;
  public static final int WAITING_ICON_ID_FINISHED = 24;
  public static final int WAITING_ICON_FRAME = 26;
  public static final int WAITING_ICON_FRAME_FINISHED = 28;
  public static final int WAITING_COMMAND_SCOPE_OR_FIELD = 30;
  public static final int WAITING_COLOR_ID = 32;
  public static final int WAITING_COLORFUL_TEXT = 34;
  public static final int CHECKING_PROPERTY_REFERENCE_START = 36;
  public static final int CHECKING_ICON_START = 38;
  public static final int CHECKING_COMMAND_START = 40;
  public static final int WAITING_CHECK_COLORFUL_TEXT_START = 42;
  public static final int WAITING_CHECK_RIGHT_QUOTE = 44;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6,  6,  7,  7,  8,  8, 
     9,  9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 
    17, 17,  8,  8, 18, 18, 19, 19, 20, 20, 21, 21, 22, 22
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\5\u0100\1\u0200\1\u0300\1\u0100\5\u0400\1\u0500\1\u0600"+
    "\1\u0700\5\u0100\1\u0800\1\u0900\1\u0a00\1\u0b00\1\u0c00\1\u0d00"+
    "\1\u0e00\3\u0100\1\u0f00\17\u0100\1\u1000\165\u0100\1\u0600\1\u0100"+
    "\1\u1100\1\u1200\1\u1300\1\u1400\54\u0100\10\u1500\37\u0100\1\u0a00"+
    "\4\u0100\1\u1600\10\u0100\1\u1700\2\u0100\1\u1800\1\u1900\1\u1400"+
    "\1\u0100\1\u0500\1\u0100\1\u1a00\1\u1700\1\u0900\3\u0100\1\u1300"+
    "\1\u1b00\114\u0100\1\u1c00\1\u1300\153\u0100\1\u1d00\11\u0100\1\u1e00"+
    "\1\u1400\6\u0100\1\u1300\u0f16\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
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
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\1\2\2\3\1\4\22\0\1\1\1\5"+
    "\1\6\1\7\1\10\2\0\1\11\2\12\3\0\1\13"+
    "\1\14\1\15\1\16\11\17\1\20\5\0\1\21\32\22"+
    "\1\23\1\24\1\25\1\0\1\26\1\0\15\27\1\30"+
    "\3\27\1\30\1\27\1\30\6\27\1\0\1\31\10\0"+
    "\1\3\32\0\1\1\2\0\1\32\3\0\1\33\u01b8\0"+
    "\12\34\206\0\12\34\306\0\12\34\234\0\12\34\166\0"+
    "\12\34\140\0\12\34\166\0\12\34\106\0\12\34\u0116\0"+
    "\12\34\106\0\12\34\346\0\1\1\u015f\0\12\34\46\0"+
    "\12\34\u012c\0\12\34\200\0\12\34\246\0\12\34\6\0"+
    "\12\34\266\0\12\34\126\0\12\34\206\0\12\34\6\0"+
    "\12\34\246\0\13\1\35\0\2\3\5\0\1\1\57\0"+
    "\1\1\240\0\1\1\u01cf\0\12\34\46\0\12\34\306\0"+
    "\12\34\26\0\12\34\126\0\12\34\u0196\0\12\34\6\0"+
    "\u0100\35\240\0\12\34\206\0\12\34\u012c\0\12\34\200\0"+
    "\12\34\74\0\12\34\220\0\12\34\166\0\12\34\146\0"+
    "\12\34\206\0\12\34\106\0\12\34\266\0\12\34\u0164\0"+
    "\62\34\100\0\12\34\266\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[7936];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
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
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\27\0\1\1\2\2\1\3\2\4\1\2\2\5\1\6"+
    "\1\3\1\7\1\10\1\5\1\11\1\12\1\13\2\14"+
    "\1\5\1\15\1\16\1\1\1\17\1\20\1\21\1\5"+
    "\1\22\1\23\1\24\1\25\1\26\1\27\1\21\1\30"+
    "\1\31\1\32\1\33\1\34\1\35\1\36\1\37\1\1"+
    "\1\2\1\5\1\13\1\40\1\41\1\42\1\20\1\43"+
    "\1\44\1\5\1\45\1\20\1\46\1\47\1\50\1\51"+
    "\1\52\3\0\1\53\1\54\1\55\1\56\1\0\1\57"+
    "\1\60\1\56\1\61\2\46\1\47\1\50\1\51\1\52"+
    "\2\62\1\63";

  private static int [] zzUnpackAction() {
    int [] result = new int[104];
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
    "\0\0\0\36\0\74\0\132\0\170\0\226\0\264\0\322"+
    "\0\360\0\u010e\0\u012c\0\u014a\0\u0168\0\u0186\0\u01a4\0\u01c2"+
    "\0\u01e0\0\u01fe\0\u021c\0\u023a\0\u0258\0\u0276\0\u0294\0\u02b2"+
    "\0\u02d0\0\u02ee\0\u030c\0\u032a\0\u0348\0\u0366\0\u0384\0\u0366"+
    "\0\u02b2\0\u03a2\0\u02b2\0\u03c0\0\u03c0\0\u03de\0\u02b2\0\u02b2"+
    "\0\u03fc\0\u041a\0\u041a\0\u02b2\0\u0438\0\u0456\0\u02b2\0\u0474"+
    "\0\u0492\0\u0492\0\u02b2\0\u04b0\0\u02b2\0\u02b2\0\u02b2\0\u04ce"+
    "\0\u04ec\0\u050a\0\u02b2\0\u0528\0\u02b2\0\u02b2\0\u02b2\0\u02b2"+
    "\0\u0546\0\u0564\0\u0582\0\u0582\0\u0564\0\u0564\0\u02b2\0\u02b2"+
    "\0\u05a0\0\u02b2\0\u05be\0\u05be\0\u02b2\0\u05dc\0\u05fa\0\u0618"+
    "\0\u0636\0\u0654\0\u0672\0\u02ee\0\u0690\0\u0384\0\u02b2\0\u02b2"+
    "\0\u02b2\0\u02b2\0\u0564\0\u02b2\0\u02b2\0\u0564\0\u02b2\0\u02b2"+
    "\0\u06ae\0\u02b2\0\u02b2\0\u02b2\0\u02b2\0\u0690\0\u06cc\0\u0690";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[104];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length() - 1;
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpacktrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\30\1\31\1\32\1\31\1\32\2\30\1\33\1\30"+
    "\1\34\1\30\2\34\1\30\2\34\2\30\1\34\3\30"+
    "\3\34\6\30\1\31\1\32\1\31\1\32\2\30\1\33"+
    "\1\30\1\34\1\30\2\34\1\30\2\34\2\30\1\34"+
    "\3\30\3\35\6\30\1\36\1\37\1\40\1\37\13\30"+
    "\1\41\16\30\1\36\1\37\1\40\1\37\2\30\1\42"+
    "\27\30\1\36\1\37\1\40\1\37\13\30\1\43\16\30"+
    "\1\44\1\37\1\45\1\37\11\30\2\46\14\30\1\46"+
    "\2\30\1\36\1\37\1\40\1\37\1\30\1\47\30\30"+
    "\1\36\1\37\1\40\1\37\1\30\1\50\1\42\26\30"+
    "\1\51\1\52\1\37\1\53\1\37\1\51\1\50\1\51"+
    "\1\54\12\51\1\55\1\56\5\51\1\57\1\60\2\51"+
    "\1\30\1\61\1\37\1\62\1\37\1\30\1\50\1\30"+
    "\1\63\1\64\1\30\2\64\1\30\2\64\1\30\1\65"+
    "\1\64\1\66\2\30\3\64\1\67\1\30\1\60\2\30"+
    "\1\70\1\71\1\30\1\71\1\30\1\70\1\50\1\70"+
    "\1\63\12\70\2\30\5\70\1\30\1\60\2\70\1\30"+
    "\1\61\1\37\1\62\1\37\1\30\1\50\1\30\1\63"+
    "\11\30\1\72\1\66\2\30\3\72\1\67\1\30\1\60"+
    "\3\30\1\61\1\37\1\62\1\37\1\30\1\50\1\30"+
    "\1\73\2\30\1\74\1\30\3\74\2\30\1\74\1\75"+
    "\1\74\1\30\3\74\1\76\1\77\1\60\3\30\1\61"+
    "\1\37\1\62\1\37\1\30\1\50\22\30\1\76\1\77"+
    "\1\60\3\30\1\61\1\37\1\62\1\37\1\30\1\50"+
    "\1\30\1\100\6\30\1\101\12\30\1\77\1\60\3\30"+
    "\1\61\1\37\1\62\1\37\1\30\1\50\23\30\1\77"+
    "\1\60\2\30\1\102\1\103\1\37\1\104\1\37\1\102"+
    "\1\105\1\102\1\106\3\102\1\107\6\102\1\30\1\102"+
    "\1\110\5\102\1\111\2\102\1\112\1\113\1\37\1\114"+
    "\1\37\1\112\1\50\7\112\2\115\2\112\1\115\4\112"+
    "\2\115\2\112\1\116\2\112\10\30\1\117\57\30\1\120"+
    "\26\30\1\121\45\30\1\122\10\30\1\123\27\30\37\0"+
    "\1\31\1\32\1\31\1\32\32\0\1\124\3\32\31\0"+
    "\2\33\1\0\1\33\1\0\31\33\11\0\1\34\1\0"+
    "\2\34\1\0\2\34\2\0\1\34\3\0\3\34\16\0"+
    "\1\34\1\0\2\34\1\0\2\34\1\125\1\0\1\34"+
    "\3\0\3\35\6\0\1\36\1\37\1\40\1\37\32\0"+
    "\1\126\3\37\31\0\2\42\1\0\1\42\1\0\1\42"+
    "\1\0\27\42\1\0\1\44\1\37\1\45\1\37\47\0"+
    "\2\46\14\0\1\46\1\0\2\51\1\0\1\51\1\0"+
    "\1\51\1\0\1\51\1\0\12\51\2\0\5\51\2\0"+
    "\3\51\1\52\1\37\1\53\1\37\1\51\1\0\1\51"+
    "\1\0\12\51\2\0\5\51\2\0\2\51\23\0\1\127"+
    "\12\0\2\130\3\0\1\130\1\131\1\130\1\131\17\130"+
    "\1\131\1\130\2\131\1\130\6\0\1\132\31\0\1\61"+
    "\1\37\1\62\1\37\42\0\1\64\1\0\2\64\1\0"+
    "\2\64\2\0\1\64\3\0\3\64\5\0\2\70\1\0"+
    "\1\70\1\0\1\70\1\0\1\70\1\0\12\70\2\0"+
    "\5\70\2\0\3\70\1\71\1\0\1\71\1\0\1\70"+
    "\1\0\1\70\1\0\12\70\2\0\5\70\2\0\2\70"+
    "\16\0\2\72\2\0\1\72\3\0\3\72\20\0\1\74"+
    "\1\0\3\74\2\0\1\74\1\0\1\74\1\0\3\74"+
    "\23\0\2\101\16\0\2\133\1\0\1\133\1\0\7\133"+
    "\1\134\6\133\1\0\1\133\1\135\11\133\1\103\1\37"+
    "\1\104\1\37\7\133\1\134\6\133\1\0\1\133\1\135"+
    "\12\133\1\0\1\133\1\0\1\136\6\133\1\134\6\133"+
    "\1\0\1\133\1\135\10\133\1\0\1\113\1\37\1\114"+
    "\1\37\36\0\1\137\41\0\1\140\1\0\2\140\1\0"+
    "\2\140\1\0\2\140\1\141\2\0\3\140\5\0\2\142"+
    "\3\0\30\142\1\0\1\143\1\121\1\0\1\121\1\0"+
    "\4\143\2\121\1\143\1\121\1\143\5\121\3\143\4\121"+
    "\3\143\1\0\2\144\3\0\30\144\1\0\2\123\1\0"+
    "\1\123\1\0\1\123\1\145\27\123\1\0\1\125\2\146"+
    "\1\147\31\0\1\140\1\141\1\0\1\141\1\0\4\140"+
    "\2\141\1\140\1\141\1\140\5\141\3\140\4\141\3\140"+
    "\2\0\1\125\1\150\1\146\1\147\31\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[1770];
    int offset = 0;
    offset = zzUnpacktrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpacktrans(String packed, int offset, int [] result) {
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
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\27\0\1\11\10\1\1\11\1\1\1\11\3\1\2\11"+
    "\3\1\1\11\2\1\1\11\3\1\1\11\1\1\3\11"+
    "\3\1\1\11\1\1\4\11\6\1\2\11\1\1\1\11"+
    "\2\1\1\11\6\1\3\0\4\11\1\0\2\11\1\1"+
    "\2\11\1\1\4\11\3\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[104];
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

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** Number of newlines encountered up to the start of the matched text. */
  @SuppressWarnings("unused")
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  @SuppressWarnings("unused")
  protected int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  @SuppressWarnings("unused")
  private boolean zzEOFDone;

  /* user code: */
    private int depth = 0;
    private CommandLocation commandLocation = CommandLocation.NORMAL;
    private ReferenceLocation referenceLocation = ReferenceLocation.NORMAL;
    
    public ParadoxLocalisationLexer() {
        this((java.io.Reader)null);
    }
	
    private void increaseDepth(){
	    depth++;
    }
    
    private void decreaseDepth(){
	    if(depth > 0) depth--;
    }
    
    private int nextStateForText(){
      return depth <= 0 ? WAITING_RICH_TEXT : WAITING_COLORFUL_TEXT;
    }
    
	private enum CommandLocation {
		NORMAL, REFERENCE, ICON;
	}
	
    private int nextStateForCommand(){
		return switch(commandLocation) {
			case NORMAL -> nextStateForText();
			case REFERENCE -> WAITING_PROPERTY_REFERENCE;
			case ICON -> WAITING_ICON;
		};
    }
	
	private enum ReferenceLocation {
		NORMAL, ICON, ICON_FRAME, COMMAND;
	}
    
    private int nextStateForPropertyReference(){
		return switch(referenceLocation) {
			case NORMAL -> nextStateForText();
			case ICON -> WAITING_ICON_ID_FINISHED;
			case ICON_FRAME -> WAITING_ICON_FRAME_FINISHED;
			case COMMAND -> WAITING_COMMAND_SCOPE_OR_FIELD;
		};
    }
    
	private boolean isReferenceStart(){
	    if(yylength() <= 1) return false;
	    return true;
	}
	
    private boolean isIconStart(){
	    if(yylength() <= 1) return false;
	    char c = yycharat(1);
	    return isExactLetter(c) || isExactDigit(c) || c == '_' || c == '$';
    }
    
    private boolean isCommandStart(){
		if(yylength() <= 1) return false;
	    return yycharat(yylength()-1) == ']';
    }
    
    private boolean isColorfulTextStart(){
		if(yylength() <= 1) return false;
	    return isExactLetter(yycharat(1));
    }
    
    private boolean isRightQuote(){
		if(yylength() == 1) return true;
	    return yycharat(yylength()-1) != '"';
    }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public ParadoxLocalisationLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** Returns the maximum size of the scanner buffer, which limits the size of tokens. */
  private int zzMaxBufferLen() {
    return Integer.MAX_VALUE;
  }

  /**  Whether the scanner buffer can grow to accommodate a larger token. */
  private boolean zzCanGrow() {
    return true;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
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
  public IElementType advance() throws java.io.IOException
  {
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

      if (zzMarkedPosL > zzStartRead) {
        switch (zzBufferL.charAt(zzMarkedPosL-1)) {
        case '\n':
        case '\u000B':  // fall through
        case '\u000C':  // fall through
        case '\u0085':  // fall through
        case '\u2028':  // fall through
        case '\u2029':  // fall through
          zzAtBOL = true;
          break;
        case '\r': 
          if (zzMarkedPosL < zzEndReadL)
            zzAtBOL = zzBufferL.charAt(zzMarkedPosL) != '\n';
          else if (zzAtEOF)
            zzAtBOL = false;
          else {
            boolean eof = zzRefill();
            zzMarkedPosL = zzMarkedPos;
            zzEndReadL = zzEndRead;
            zzBufferL = zzBuffer;
            if (eof) 
              zzAtBOL = false;
            else 
              zzAtBOL = zzBufferL.charAt(zzMarkedPosL) != '\n';
          }
          break;
        default:
          zzAtBOL = false;
        }
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      if (zzAtBOL)
        zzState = ZZ_LEXSTATE[zzLexicalState+1];
      else
        zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
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
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
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
            { return BAD_CHARACTER;
            }
          // fall through
          case 52: break;
          case 2:
            { return WHITE_SPACE;
            }
          // fall through
          case 53: break;
          case 3:
            { return COMMENT;
            }
          // fall through
          case 54: break;
          case 4:
            { yybegin(WAITING_PROPERTY_COLON);
    return PROPERTY_KEY_TOKEN;
            }
          // fall through
          case 55: break;
          case 5:
            { yybegin(YYINITIAL); return WHITE_SPACE;
            }
          // fall through
          case 56: break;
          case 6:
            { yybegin(WAITING_LOCALE_END); return COLON;
            }
          // fall through
          case 57: break;
          case 7:
            { yybegin(WAITING_PROPERTY_NUMBER); return COLON;
            }
          // fall through
          case 58: break;
          case 8:
            { yybegin(WAITING_PROPERTY_VALUE); return WHITE_SPACE;
            }
          // fall through
          case 59: break;
          case 9:
            { yybegin(WAITING_PROPERTY_VALUE); return PROPERTY_NUMBER;
            }
          // fall through
          case 60: break;
          case 10:
            { yybegin(WAITING_RICH_TEXT); return LEFT_QUOTE;
            }
          // fall through
          case 61: break;
          case 11:
            { yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);
            }
          // fall through
          case 62: break;
          case 12:
            { return STRING_TOKEN;
            }
          // fall through
          case 63: break;
          case 13:
            { referenceLocation=ReferenceLocation.NORMAL; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);
            }
          // fall through
          case 64: break;
          case 14:
            { commandLocation=CommandLocation.NORMAL; yypushback(yylength()); yybegin(CHECKING_COMMAND_START);
            }
          // fall through
          case 65: break;
          case 15:
            { yypushback(yylength()); yybegin(CHECKING_ICON_START);
            }
          // fall through
          case 66: break;
          case 16:
            { yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);
            }
          // fall through
          case 67: break;
          case 17:
            { yybegin(nextStateForText()); return WHITE_SPACE;
            }
          // fall through
          case 68: break;
          case 18:
            { yybegin(nextStateForPropertyReference()); return PROPERTY_REFERENCE_END;
            }
          // fall through
          case 69: break;
          case 19:
            { return PROPERTY_REFERENCE_ID;
            }
          // fall through
          case 70: break;
          case 20:
            { yybegin(WAITING_SCRIPTED_VARIABLE_REFERENCE_NAME); return AT;
            }
          // fall through
          case 71: break;
          case 21:
            { commandLocation=CommandLocation.REFERENCE; yypushback(yylength()); yybegin(CHECKING_COMMAND_START);
            }
          // fall through
          case 72: break;
          case 22:
            { yybegin(WAITING_PROPERTY_REFERENCE_PARAMETER_TOKEN); return PIPE;
            }
          // fall through
          case 73: break;
          case 23:
            { return PROPERTY_REFERENCE_PARAMETER_TOKEN;
            }
          // fall through
          case 74: break;
          case 24:
            { return SCRIPTED_VARIABLE_REFERENCE_ID;
            }
          // fall through
          case 75: break;
          case 25:
            { referenceLocation=ReferenceLocation.ICON; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);
            }
          // fall through
          case 76: break;
          case 26:
            { yybegin(WAITING_ICON_ID_FINISHED); return ICON_ID;
            }
          // fall through
          case 77: break;
          case 27:
            { commandLocation=CommandLocation.ICON; yypushback(yylength()); yybegin(CHECKING_COMMAND_START);
            }
          // fall through
          case 78: break;
          case 28:
            { yybegin(WAITING_ICON_FRAME); return PIPE;
            }
          // fall through
          case 79: break;
          case 29:
            { yybegin(nextStateForText()); return ICON_END;
            }
          // fall through
          case 80: break;
          case 30:
            { referenceLocation=ReferenceLocation.ICON_FRAME; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);
            }
          // fall through
          case 81: break;
          case 31:
            { yybegin(WAITING_ICON_FRAME_FINISHED); return ICON_FRAME;
            }
          // fall through
          case 82: break;
          case 32:
            { referenceLocation=ReferenceLocation.COMMAND; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);
            }
          // fall through
          case 83: break;
          case 33:
            { yybegin(WAITING_COMMAND_SCOPE_OR_FIELD); return DOT;
            }
          // fall through
          case 84: break;
          case 34:
            { yybegin(nextStateForCommand()); return COMMAND_END;
            }
          // fall through
          case 85: break;
          case 35:
            { yypushback(yylength()); yybegin(WAITING_COLORFUL_TEXT);
            }
          // fall through
          case 86: break;
          case 36:
            { yybegin(WAITING_COLORFUL_TEXT); return WHITE_SPACE;
            }
          // fall through
          case 87: break;
          case 37:
            { yybegin(WAITING_COLORFUL_TEXT); return COLOR_ID;
            }
          // fall through
          case 88: break;
          case 38:
            { //特殊处理
    //如果匹配到的字符串长度大于1，且"$"后面的字符可以被识别为PROPERTY_REFERENCE_ID或者command，或者是@，则认为代表属性引用的开始
    boolean isReferenceStart = isReferenceStart();
	yypushback(yylength()-1);
	if(isReferenceStart){
		yybegin(WAITING_PROPERTY_REFERENCE);
        return PROPERTY_REFERENCE_START;
	} else {
        yybegin(nextStateForText());
        return STRING_TOKEN;
    }
            }
          // fall through
          case 89: break;
          case 39:
            { //特殊处理
    //如果匹配到的字符串的第2个字符存在且为字母、数字或下划线或者$，则认为代表图标的开始
    //否则认为是常规字符串
    boolean isIconStart = isIconStart();
    yypushback(yylength()-1);
    if(isIconStart){
    	  yybegin(WAITING_ICON);
    	  return ICON_START;
    }else{
        yybegin(nextStateForText());
        return STRING_TOKEN;
    }
            }
          // fall through
          case 90: break;
          case 40:
            { //特殊处理
    //除了可以通过连续的两个左方括号转义之外
    //如果匹配到的字符串长度大于1，且最后一个字符为右方括号，则认为代表命令的开始
    //否则认为是常规字符串
    boolean isCommandStart = isCommandStart();
    yypushback(yylength()-1);
    if(isCommandStart){
	    yybegin(WAITING_COMMAND_SCOPE_OR_FIELD);
	    return COMMAND_START;
    } else {
	    yybegin(nextStateForText());
	    return STRING_TOKEN;
    }
            }
          // fall through
          case 91: break;
          case 41:
            { //特殊处理
    //如果匹配到的字符串的第2个字符存在且为字母，则认为代表彩色文本的开始
    //否则认为是常规字符串
    boolean isColorfulTextStart = isColorfulTextStart();
    yypushback(yylength()-1);
    if(isColorfulTextStart){
        yybegin(WAITING_COLOR_ID);
        increaseDepth();
        return COLORFUL_TEXT_START;
    }else{
        yybegin(nextStateForText());
        return STRING_TOKEN;
    }
            }
          // fall through
          case 92: break;
          case 42:
            { //特殊处理
      //如果匹配到的字符串长度为1，或者最后一个字符不是双引号，则认为代表本地化富文本的结束
      //否则认为是常规字符串
      boolean isRightQuote = isRightQuote();
      yypushback(yylength()-1);
      if(isRightQuote){
          yybegin(WAITING_PROPERTY_END);
          return RIGHT_QUOTE;
      }else{
          yybegin(nextStateForText());
          return STRING_TOKEN;
      }
            }
          // fall through
          case 93: break;
          case 43:
            { return DOUBLE_LEFT_BRACKET;
            }
          // fall through
          case 94: break;
          case 44:
            { return INVALID_ESCAPE_TOKEN;
            }
          // fall through
          case 95: break;
          case 45:
            { return VALID_ESCAPE_TOKEN;
            }
          // fall through
          case 96: break;
          case 46:
            { decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;
            }
          // fall through
          case 97: break;
          case 47:
            { yypushback(1); return COMMAND_SCOPE_ID;
            }
          // fall through
          case 98: break;
          case 48:
            { yypushback(1); return COMMAND_FIELD_ID;
            }
          // fall through
          case 99: break;
          case 49:
            { decreaseDepth(); decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;
            }
          // fall through
          case 100: break;
          case 50:
            // lookahead expression with fixed lookahead length
            zzMarkedPos = Character.offsetByCodePoints
                (zzBufferL, zzMarkedPos, -1);
            { //本地化文件中可以没有，或者有多个locale - 主要是为了兼容localisation/languages.yml
	//locale之前必须没有任何缩进
	//locale之后的冒号和换行符之间应当没有任何字符或者只有空白字符
	//采用最简单的实现方式，尽管JFlex手册中说 "^" "$" 性能不佳
	int n = 1;
	int l = yylength();
	while(Character.isWhitespace(yycharat(l - n))) {
		n++;
	}
	yypushback(n);
    yybegin(WAITING_LOCALE_COLON);
    return LOCALE_ID;
            }
          // fall through
          case 101: break;
          case 51:
            // lookahead expression with fixed lookahead length
            zzMarkedPos = Character.offsetByCodePoints
                (zzBufferL, zzMarkedPos, -2);
            { //本地化文件中可以没有，或者有多个locale - 主要是为了兼容localisation/languages.yml
	//locale之前必须没有任何缩进
	//locale之后的冒号和换行符之间应当没有任何字符或者只有空白字符
	//采用最简单的实现方式，尽管JFlex手册中说 "^" "$" 性能不佳
	int n = 1;
	int l = yylength();
	while(Character.isWhitespace(yycharat(l - n))) {
		n++;
	}
	yypushback(n);
    yybegin(WAITING_LOCALE_COLON);
    return LOCALE_ID;
            }
          // fall through
          case 102: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
