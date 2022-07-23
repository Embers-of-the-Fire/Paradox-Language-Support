/* The following code was generated by JFlex 1.7.0 tweaked for IntelliJ platform */

package icu.windea.pls.localisation.psi;

import com.intellij.openapi.project.*;import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.*;
import static icu.windea.pls.localisation.psi.ParadoxLocalisationElementTypes.*;
import static icu.windea.pls.StdlibExtensionsKt.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.7.0
 * from the specification file <tt>ParadoxLocalisationLexer.flex</tt>
 */
public class ParadoxLocalisationLexer implements com.intellij.lexer.FlexLexer {

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
  public static final int WAITING_ICON = 20;
  public static final int WAITING_ICON_ID_FINISHED = 22;
  public static final int WAITING_ICON_FRAME = 24;
  public static final int WAITING_ICON_FRAME_FINISHED = 26;
  public static final int WAITING_COMMAND_SCOPE_OR_FIELD = 28;
  public static final int WAITING_COLOR_ID = 30;
  public static final int WAITING_COLORFUL_TEXT = 32;
  public static final int WAITING_CHECK_PROPERTY_REFERENCE_START = 34;
  public static final int WAITING_CHECK_ICON_START = 36;
  public static final int WAITING_CHECK_COMMAND_START = 38;
  public static final int WAITING_CHECK_COLORFUL_TEXT_START = 40;
  public static final int WAITING_CHECK_RIGHT_QUOTE = 42;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  5,  5,  6,  6,  7,  7, 
     8,  8,  9,  9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 
     7,  7, 16, 16, 17, 17, 18, 18, 19, 19, 20, 20
  };

  /** 
   * Translates characters to character classes
   * Chosen bits are [9, 6, 6]
   * Total runtime size is 4256 bytes
   */
  public static int ZZ_CMAP(int ch) {
    return ZZ_CMAP_A[(ZZ_CMAP_Y[(ZZ_CMAP_Z[ch>>12]<<6)|((ch>>6)&0x3f)]<<6)|(ch&0x3f)];
  }

  /* The ZZ_CMAP_Z table has 272 entries */
  static final char ZZ_CMAP_Z[] = zzUnpackCMap(
    "\1\0\1\1\1\2\1\3\6\4\1\5\4\4\1\6\1\7\1\10\4\4\1\11\6\4\1\12\1\13\361\4");

  /* The ZZ_CMAP_Y table has 768 entries */
  static final char ZZ_CMAP_Y[] = zzUnpackCMap(
    "\1\0\1\1\1\2\26\3\1\4\1\3\1\5\3\3\1\6\5\3\1\7\1\3\1\7\1\3\1\7\1\3\1\7\1\3"+
    "\1\7\1\3\1\7\1\3\1\7\1\3\1\7\1\3\1\7\1\3\1\7\1\3\1\10\1\3\1\10\1\4\4\3\1\6"+
    "\1\10\27\3\1\11\4\3\1\4\1\10\4\3\1\12\1\3\1\10\2\3\1\13\2\3\1\10\1\5\2\3\1"+
    "\13\16\3\1\14\1\15\76\3\1\11\227\3\1\4\12\3\1\10\1\6\2\3\1\16\1\3\1\10\5\3"+
    "\1\5\114\3\1\10\25\3\1\4\56\3\1\7\1\3\1\5\1\17\2\3\1\10\3\3\1\5\5\3\1\10\1"+
    "\3\1\10\5\3\1\10\1\3\1\6\1\5\6\3\1\4\15\3\1\10\67\3\1\4\3\3\1\10\61\3\1\20"+
    "\105\3\1\10\32\3");

  /* The ZZ_CMAP_A table has 1088 entries */
  static final char ZZ_CMAP_A[] = zzUnpackCMap(
    "\11\0\1\1\1\3\2\2\1\3\22\0\1\1\1\31\1\5\1\4\1\10\2\0\1\17\5\0\1\24\1\20\1"+
    "\23\1\16\11\26\1\7\5\0\1\13\32\25\1\12\1\21\1\27\1\0\1\6\1\0\15\30\1\22\3"+
    "\30\1\22\1\30\1\22\6\30\1\0\1\32\10\0\1\2\32\0\1\1\2\0\1\11\3\0\1\14\170\0"+
    "\12\15\106\0\12\15\6\0\12\15\134\0\12\15\40\0\12\15\46\0\1\1\105\0\12\15\60"+
    "\0\12\15\6\0\12\15\46\0\13\1\35\0\2\2\5\0\1\1\57\0\1\1\60\0\12\15\26\0\12"+
    "\15\74\0\12\15\16\0\62\15");

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\25\0\1\1\1\2\2\3\1\4\2\5\1\6\2\7"+
    "\1\10\1\4\1\11\1\12\1\7\1\13\1\14\1\15"+
    "\2\16\1\7\1\17\1\20\1\21\1\22\1\1\1\23"+
    "\1\7\1\24\1\25\1\26\1\27\1\30\1\23\1\31"+
    "\1\32\1\33\1\34\1\35\1\36\1\37\1\1\1\40"+
    "\1\41\1\42\1\43\1\44\1\7\1\22\1\45\1\1"+
    "\1\46\1\47\1\50\1\51\3\0\1\52\1\53\1\54"+
    "\1\55\1\0\1\56\1\57\1\60\1\61\1\46\1\47"+
    "\1\50\1\51\1\62";

  private static int [] zzUnpackAction() {
    int [] result = new int[93];
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
    "\0\0\0\33\0\66\0\121\0\154\0\207\0\242\0\275"+
    "\0\330\0\363\0\u010e\0\u0129\0\u0144\0\u015f\0\u017a\0\u0195"+
    "\0\u01b0\0\u01cb\0\u01e6\0\u0201\0\u021c\0\u0237\0\u0252\0\u0252"+
    "\0\u026d\0\u0288\0\u02a3\0\u02be\0\u02d9\0\u02d9\0\u02f4\0\u0237"+
    "\0\u030f\0\u0237\0\u032a\0\u032a\0\u0345\0\u0237\0\u0237\0\u0360"+
    "\0\u037b\0\u037b\0\u0237\0\u0237\0\u0396\0\u03b1\0\u03cc\0\u03e7"+
    "\0\u03e7\0\u0402\0\u0237\0\u0237\0\u0237\0\u041d\0\u0438\0\u0453"+
    "\0\u0237\0\u0237\0\u0237\0\u0237\0\u0237\0\u046e\0\u0489\0\u0237"+
    "\0\u0237\0\u0237\0\u0237\0\u04a4\0\u04a4\0\u04bf\0\u0237\0\u04da"+
    "\0\u04f5\0\u0510\0\u052b\0\u0546\0\u026d\0\u0561\0\u02f4\0\u0237"+
    "\0\u0237\0\u0237\0\u0237\0\u0489\0\u0237\0\u0237\0\u0237\0\u04da"+
    "\0\u0237\0\u0237\0\u0237\0\u0237\0\u0561";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[93];
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
    "\1\26\1\27\1\30\1\31\1\32\1\26\1\33\7\26"+
    "\3\34\1\26\1\33\1\26\3\34\1\26\1\33\3\26"+
    "\1\35\1\36\1\37\3\26\1\40\24\26\1\35\1\36"+
    "\1\37\1\41\27\26\1\35\1\36\1\37\3\26\1\42"+
    "\24\26\1\43\1\44\1\37\11\26\2\45\7\26\1\45"+
    "\5\26\1\35\1\36\1\37\1\26\1\46\26\26\1\35"+
    "\1\36\1\37\1\41\1\47\25\26\1\50\1\51\1\52"+
    "\1\37\1\50\1\47\2\50\1\53\1\54\1\55\1\50"+
    "\1\56\4\50\1\57\11\50\1\26\1\60\1\61\1\37"+
    "\1\26\1\47\1\62\1\26\1\63\1\26\1\64\1\26"+
    "\1\56\1\26\3\62\1\26\1\62\1\26\3\62\1\26"+
    "\1\62\1\26\1\65\1\66\2\67\1\26\1\66\1\47"+
    "\2\66\1\63\2\26\1\66\1\56\4\66\1\26\11\66"+
    "\1\26\1\60\1\61\1\37\1\26\1\47\1\70\1\26"+
    "\1\71\1\72\1\73\1\26\1\56\1\26\1\70\2\26"+
    "\6\70\1\26\1\70\1\26\1\74\1\26\1\60\1\61"+
    "\1\37\1\26\1\47\3\26\1\72\2\26\1\56\15\26"+
    "\1\74\1\26\1\60\1\61\1\37\1\26\1\47\2\26"+
    "\1\75\1\72\2\26\1\56\11\26\1\76\5\26\1\60"+
    "\1\61\1\37\1\26\1\47\3\26\1\72\2\26\1\56"+
    "\17\26\1\35\1\36\1\37\1\26\1\47\2\77\1\100"+
    "\2\26\1\77\1\56\1\26\1\77\1\26\1\101\1\26"+
    "\1\77\2\26\2\77\1\102\1\77\2\26\1\103\1\104"+
    "\1\105\1\37\1\103\1\47\6\103\1\106\1\103\1\107"+
    "\3\103\1\107\2\103\2\107\1\103\1\107\2\103\10\26"+
    "\1\110\33\26\1\111\33\26\1\112\34\26\1\113\23\26"+
    "\1\114\25\26\34\0\1\27\1\30\1\31\30\0\1\115"+
    "\2\31\27\0\3\32\1\0\27\32\6\0\1\33\1\116"+
    "\6\0\3\34\1\0\1\33\1\0\3\34\1\0\1\33"+
    "\10\0\1\34\7\0\3\34\1\0\1\34\1\0\3\34"+
    "\1\0\1\34\3\0\1\35\1\36\1\37\30\0\1\117"+
    "\2\37\27\0\3\41\1\0\1\41\1\0\25\41\1\0"+
    "\1\43\1\44\1\37\44\0\2\45\7\0\1\45\4\0"+
    "\3\50\1\0\1\50\1\0\2\50\3\0\1\50\1\0"+
    "\4\50\1\0\12\50\1\51\1\52\1\37\1\50\1\0"+
    "\2\50\3\0\1\50\1\0\4\50\1\0\11\50\12\0"+
    "\1\120\51\0\1\121\1\0\2\122\2\0\1\122\1\123"+
    "\2\122\3\123\1\122\1\123\5\122\1\123\10\122\1\0"+
    "\1\60\1\61\1\37\35\0\1\62\7\0\3\62\1\0"+
    "\1\62\1\0\3\62\1\0\1\62\2\0\3\66\1\0"+
    "\1\66\1\0\2\66\3\0\1\66\1\0\4\66\1\0"+
    "\12\66\2\67\1\0\1\66\1\0\2\66\3\0\1\66"+
    "\1\0\4\66\1\0\11\66\6\0\1\70\7\0\1\70"+
    "\2\0\6\70\1\0\1\70\20\0\1\76\7\0\1\76"+
    "\12\0\2\124\3\0\1\124\2\0\1\124\1\0\1\125"+
    "\1\0\1\124\2\0\2\124\1\126\1\124\3\0\1\104"+
    "\1\105\1\37\60\0\1\127\7\0\1\130\7\0\3\130"+
    "\1\0\1\130\1\0\3\130\1\0\1\130\2\0\2\131"+
    "\2\0\27\131\1\132\2\112\1\0\2\132\2\112\3\132"+
    "\1\112\2\132\1\112\1\132\1\112\1\132\1\112\2\132"+
    "\2\112\1\132\1\112\2\132\2\133\2\0\27\133\3\114"+
    "\1\0\1\114\1\134\25\114\1\0\2\116\1\135\27\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[1404];
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
    "\25\0\1\11\11\1\1\11\1\1\1\11\3\1\2\11"+
    "\3\1\2\11\6\1\3\11\3\1\5\11\2\1\4\11"+
    "\3\1\1\11\5\1\3\0\4\11\1\0\3\11\1\1"+
    "\4\11\1\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[93];
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
		
    private boolean noIndent = true;
    private int depth = 0;
    private int commandLocation = 0;
    private int propertyReferenceLocation = 0;
    private boolean inIconName = false;
    
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
    
    private int nextStateForCommand(){
      if(commandLocation == 0) return nextStateForText();
      else if (commandLocation == 1) return WAITING_PROPERTY_REFERENCE;
      else if (commandLocation == 2) return WAITING_ICON;
      else return nextStateForText();
    }
    
    private int nextStateForPropertyReference(){
      if(propertyReferenceLocation == 0) return nextStateForText();
      else if (propertyReferenceLocation == 1) return WAITING_ICON_ID_FINISHED;
      else if (propertyReferenceLocation == 2) return WAITING_ICON_FRAME_FINISHED;
      else if (propertyReferenceLocation == 3) return WAITING_COMMAND_SCOPE_OR_FIELD;
      else return nextStateForText();
    }
    
    private boolean isIconStart(){
		  if(yylength() != 2) return false;
	    char c = yycharat(1);
	    return isExactLetter(c) || isExactDigit(c) || c == '_';
    }
    
    private boolean isCommandStart(){
		  if(yylength() <= 1) return false;
	    return yycharat(yylength()-1) == ']';
    }
    
    private boolean isColorfulTextStart(){
		  if(yylength() != 2) return false;
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
            { return TokenType.BAD_CHARACTER;
            } 
            // fall through
          case 51: break;
          case 2: 
            { noIndent=false; return WHITE_SPACE;
            } 
            // fall through
          case 52: break;
          case 3: 
            { noIndent=true; return WHITE_SPACE;
            } 
            // fall through
          case 53: break;
          case 4: 
            { return COMMENT;
            } 
            // fall through
          case 54: break;
          case 5: 
            { yybegin(WAITING_PROPERTY_COLON); return PROPERTY_KEY_ID;
            } 
            // fall through
          case 55: break;
          case 6: 
            { return WHITE_SPACE;
            } 
            // fall through
          case 56: break;
          case 7: 
            { noIndent=true; yybegin(YYINITIAL); return WHITE_SPACE;
            } 
            // fall through
          case 57: break;
          case 8: 
            { yybegin(WAITING_LOCALE_END); return COLON;
            } 
            // fall through
          case 58: break;
          case 9: 
            { yybegin(WAITING_PROPERTY_NUMBER); return COLON;
            } 
            // fall through
          case 59: break;
          case 10: 
            { yybegin(WAITING_PROPERTY_VALUE); return WHITE_SPACE;
            } 
            // fall through
          case 60: break;
          case 11: 
            { yybegin(WAITING_PROPERTY_VALUE); return PROPERTY_NUMBER;
            } 
            // fall through
          case 61: break;
          case 12: 
            { yybegin(WAITING_RICH_TEXT); return LEFT_QUOTE;
            } 
            // fall through
          case 62: break;
          case 13: 
            { yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);
            } 
            // fall through
          case 63: break;
          case 14: 
            { return STRING_TOKEN;
            } 
            // fall through
          case 64: break;
          case 15: 
            { propertyReferenceLocation=0; yypushback(yylength()); yybegin(WAITING_CHECK_PROPERTY_REFERENCE_START);
            } 
            // fall through
          case 65: break;
          case 16: 
            { yypushback(yylength()); yybegin(WAITING_CHECK_ICON_START);
            } 
            // fall through
          case 66: break;
          case 17: 
            { commandLocation=0; yypushback(yylength()); yybegin(WAITING_CHECK_COMMAND_START);
            } 
            // fall through
          case 67: break;
          case 18: 
            { yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);
            } 
            // fall through
          case 68: break;
          case 19: 
            { yybegin(nextStateForText()); return WHITE_SPACE;
            } 
            // fall through
          case 69: break;
          case 20: 
            { return PROPERTY_REFERENCE_ID;
            } 
            // fall through
          case 70: break;
          case 21: 
            { yybegin(nextStateForPropertyReference()); return PROPERTY_REFERENCE_END;
            } 
            // fall through
          case 71: break;
          case 22: 
            { commandLocation=1; yypushback(yylength()); yybegin(WAITING_CHECK_COMMAND_START);
            } 
            // fall through
          case 72: break;
          case 23: 
            { yybegin(WAITING_PROPERTY_REFERENCE_PARAMETER_TOKEN); return PIPE;
            } 
            // fall through
          case 73: break;
          case 24: 
            { return PROPERTY_REFERENCE_PARAMETER_TOKEN;
            } 
            // fall through
          case 74: break;
          case 25: 
            { yybegin(WAITING_ICON_ID_FINISHED); return ICON_ID;
            } 
            // fall through
          case 75: break;
          case 26: 
            { propertyReferenceLocation=1; yypushback(yylength()); yybegin(WAITING_CHECK_PROPERTY_REFERENCE_START);
            } 
            // fall through
          case 76: break;
          case 27: 
            { yybegin(nextStateForText()); return ICON_END;
            } 
            // fall through
          case 77: break;
          case 28: 
            { commandLocation=2; yypushback(yylength()); yybegin(WAITING_CHECK_COMMAND_START);
            } 
            // fall through
          case 78: break;
          case 29: 
            { yybegin(WAITING_ICON_FRAME); return PIPE;
            } 
            // fall through
          case 79: break;
          case 30: 
            { propertyReferenceLocation=2; yypushback(yylength()); yybegin(WAITING_CHECK_PROPERTY_REFERENCE_START);
            } 
            // fall through
          case 80: break;
          case 31: 
            { yybegin(WAITING_ICON_FRAME_FINISHED); return ICON_FRAME;
            } 
            // fall through
          case 81: break;
          case 32: 
            { propertyReferenceLocation=3; yypushback(yylength()); yybegin(WAITING_CHECK_PROPERTY_REFERENCE_START);
            } 
            // fall through
          case 82: break;
          case 33: 
            { yybegin(WAITING_COMMAND_SCOPE_OR_FIELD); return DOT;
            } 
            // fall through
          case 83: break;
          case 34: 
            { yybegin(nextStateForCommand()); return COMMAND_END;
            } 
            // fall through
          case 84: break;
          case 35: 
            { yypushback(yylength()); yybegin(WAITING_COLORFUL_TEXT);
            } 
            // fall through
          case 85: break;
          case 36: 
            { yybegin(WAITING_COLORFUL_TEXT); return WHITE_SPACE;
            } 
            // fall through
          case 86: break;
          case 37: 
            { yybegin(WAITING_COLORFUL_TEXT); return COLOR_ID;
            } 
            // fall through
          case 87: break;
          case 38: 
            { //特殊处理
    //如果匹配到的字符串的第2个字符存在且为字母、数字或下划线，则认为代表图标的开始
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
          case 88: break;
          case 39: 
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
          case 89: break;
          case 40: 
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
          case 90: break;
          case 41: 
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
          case 91: break;
          case 42: 
            { return DOUBLE_LEFT_BRACKET;
            } 
            // fall through
          case 92: break;
          case 43: 
            { decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;
            } 
            // fall through
          case 93: break;
          case 44: 
            { return INVALID_ESCAPE_TOKEN;
            } 
            // fall through
          case 94: break;
          case 45: 
            { return VALID_ESCAPE_TOKEN;
            } 
            // fall through
          case 95: break;
          case 46: 
            { yypushback(1); return COMMAND_SCOPE_ID;
            } 
            // fall through
          case 96: break;
          case 47: 
            { yypushback(1); return COMMAND_FIELD_ID;
            } 
            // fall through
          case 97: break;
          case 48: 
            { decreaseDepth(); decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;
            } 
            // fall through
          case 98: break;
          case 49: 
            { //特殊处理
    //如果匹配到的字符串长度大于1，且"$"后面的字符可以被识别为PROPERTY_REFERENCE_ID，则认为代表属性引用的开始
	yypushback(yylength()-1);
	yybegin(WAITING_PROPERTY_REFERENCE);
    return PROPERTY_REFERENCE_START;
            } 
            // fall through
          case 99: break;
          case 50: 
            { //同一本地化文件中是可以有多个locale的，这是为了兼容localisation/languages.yml
	//locale应该在之后的冒号和换行符之间没有任何字符或者只有空白字符
    int length = yylength();
    int i = length - 2;
    while(i >= 0){
 	    char c = yycharat(i);
 	    if(c == ':') {
 		    int pushback = length - i;
			yypushback(pushback);
			//locale之前必须没有任何缩进
			if(noIndent){
 		        yybegin(WAITING_LOCALE_COLON);
 		        return LOCALE_ID;
			} else {
				yybegin(WAITING_PROPERTY_COLON);
				return PROPERTY_KEY_ID;
			}
 	    }
 	    i--;
    }
    return TokenType.BAD_CHARACTER; //不期望的结果
            } 
            // fall through
          case 100: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
