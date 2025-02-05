package icu.windea.pls.localisation.psi;


import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.*;
import static icu.windea.pls.core.StdlibExtensionsKt.*;
import static icu.windea.pls.localisation.psi.ParadoxLocalisationElementTypes.*;

%%

%public
%class _ParadoxLocalisationLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state WAITING_LOCALE_COLON
%state WAITING_LOCALE_END
%state WAITING_PROPERTY_COLON
%state WAITING_PROPERTY_NUMBER
%state WAITING_PROPERTY_VALUE
%state WAITING_PROPERTY_END
%state WAITING_RICH_TEXT
%state WAITING_PROPERTY_REFERENCE
%state WAITING_PROPERTY_REFERENCE_PARAMETER_TOKEN
%state WAITING_SCRIPTED_VARIABLE_REFERENCE_NAME
%state WAITING_ICON
%state WAITING_ICON_ID_FINISHED
%state WAITING_ICON_FRAME
%state WAITING_ICON_FRAME_FINISHED
%state WAITING_COMMAND
%state WAITING_COMMAND_SCOPE_OR_FIELD
%state WAITING_CONCEPT
%state WAITING_CONCEPT_TEXT
%state WAITING_COLOR_ID
%state WAITING_COLORFUL_TEXT

%state CHECKING_PROPERTY_REFERENCE_START
%state CHECKING_ICON_START
%state CHECKING_COMMAND_START
%state WAITING_CHECK_COLORFUL_TEXT_START
%state WAITING_CHECK_RIGHT_QUOTE

%{	
    private int depth = 0;
    private boolean inConceptText = false;
    private CommandLocation commandLocation = CommandLocation.NORMAL;
    private ReferenceLocation referenceLocation = ReferenceLocation.NORMAL;
	
    public _ParadoxLocalisationLexer() {
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
%}

EOL=\s*\R
BLANK=\s+
WHITE_SPACE=[\s&&[^\r\n]]+
COMMENT=#[^\r\n]*
END_OF_LINE_COMMENT=#[^\"\r\n]* //行尾注释不能包含双引号，否则会有解析冲突

CHECK_PROPERTY_REFERENCE_START=\$([a-zA-Z0-9_.\-'@]?|{CHECK_COMMAND_START})
CHECK_ICON_START=£.?
CHECK_COLORFUL_TEXT_START=§.?
CHECK_RIGHT_QUOTE=\"[^\"\r\n]*\"?

LOCALE_ID=[a-z_]+
NUMBER=\d+
PROPERTY_KEY_CHAR=[a-zA-Z0-9_.\-']
PROPERTY_KEY_TOKEN={PROPERTY_KEY_CHAR}+
VALID_ESCAPE_TOKEN=\\[rnt\"$£§]
INVALID_ESCAPE_TOKEN=\\.
DOUBLE_LEFT_BRACKET=\[\[
PROPERTY_REFERENCE_ID={PROPERTY_KEY_CHAR}+
PROPERTY_REFERENCE_PARAMETER_TOKEN=[^\"$£§\[\r\n\\]+
SCRIPTED_VARIABLE_ID=[a-zA-Z_][a-zA-Z0-9_]*
ICON_ID=[a-zA-Z0-9\-_\\/]+
ICON_FRAME=[1-9][0-9]* // positive integer
COLOR_ID=[a-zA-Z0-9]
STRING_TOKEN=[^\"$£§\[\]\r\n\\]+ //双引号实际上不需要转义

CHECK_COMMAND_START=\[[^\r\n\]]*.?
COMMAND_SCOPE_ID_WITH_SUFFIX=[^\r\n.\[\]]+\.
COMMAND_FIELD_ID_WITH_SUFFIX=[^\r\n.\[\]]+\]
CONCEPT_NAME=[a-zA-Z0-9_]+

%%

//core rules

<YYINITIAL> {
  {WHITE_SPACE} {return WHITE_SPACE; } //继续解析
  {COMMENT} {return COMMENT; } //这里可以有注释
  ^ {LOCALE_ID} ":" \s* $ {
	//本地化文件中可以没有，或者有多个locale - 主要是为了兼容localisation/languages.yml
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
  {PROPERTY_KEY_TOKEN} {
    yybegin(WAITING_PROPERTY_COLON);
    return PROPERTY_KEY_TOKEN;
  }
}
<WAITING_LOCALE_COLON>{
  {WHITE_SPACE} {return WHITE_SPACE; }
  ":" {yybegin(WAITING_LOCALE_END); return COLON; }
}
<WAITING_LOCALE_END>{
  {WHITE_SPACE} {return WHITE_SPACE; }
  {END_OF_LINE_COMMENT} {return COMMENT; }
}
<WAITING_PROPERTY_COLON>{
  {WHITE_SPACE} {return WHITE_SPACE;}
  ":" {yybegin(WAITING_PROPERTY_NUMBER); return COLON; }
}
<WAITING_PROPERTY_NUMBER>{
  {WHITE_SPACE} {yybegin(WAITING_PROPERTY_VALUE); return WHITE_SPACE;}
  {NUMBER} {yybegin(WAITING_PROPERTY_VALUE); return PROPERTY_NUMBER;}
  \" {yybegin(WAITING_RICH_TEXT); return LEFT_QUOTE; }
}
<WAITING_PROPERTY_VALUE> {
  {WHITE_SPACE} {return WHITE_SPACE;}
  \" {yybegin(WAITING_RICH_TEXT); return LEFT_QUOTE; }
}
<WAITING_RICH_TEXT>{
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "$" {referenceLocation=ReferenceLocation.NORMAL; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);}
  "£" {yypushback(yylength()); yybegin(CHECKING_ICON_START);}
  "[" { increaseDepth(); commandLocation=CommandLocation.NORMAL; yybegin(WAITING_COMMAND); return COMMAND_START; }
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;} //允许多余的重置颜色标记
}

//reference rules

<CHECKING_PROPERTY_REFERENCE_START>{
  {CHECK_PROPERTY_REFERENCE_START} {
    //特殊处理
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
}
<WAITING_PROPERTY_REFERENCE>{
  {WHITE_SPACE} {yybegin(nextStateForText()); return WHITE_SPACE;}
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "$" {yybegin(nextStateForPropertyReference()); return PROPERTY_REFERENCE_END;}
  "[" {increaseDepth();commandLocation=CommandLocation.REFERENCE; yybegin(WAITING_COMMAND); return COMMAND_START; }
  "|" {yybegin(WAITING_PROPERTY_REFERENCE_PARAMETER_TOKEN); return PIPE;}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
  "@" {yybegin(WAITING_SCRIPTED_VARIABLE_REFERENCE_NAME); return AT;}
  {PROPERTY_REFERENCE_ID} {return PROPERTY_REFERENCE_ID;}
}
<WAITING_PROPERTY_REFERENCE_PARAMETER_TOKEN>{
  {WHITE_SPACE} {yybegin(nextStateForText()); return WHITE_SPACE; } 
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "$" {yybegin(nextStateForPropertyReference()); return PROPERTY_REFERENCE_END;}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
  {PROPERTY_REFERENCE_PARAMETER_TOKEN} {return PROPERTY_REFERENCE_PARAMETER_TOKEN;}
}
<WAITING_SCRIPTED_VARIABLE_REFERENCE_NAME>{
   {WHITE_SPACE} {yybegin(nextStateForText()); return WHITE_SPACE; }
   \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "$" {yybegin(nextStateForPropertyReference()); return PROPERTY_REFERENCE_END;}
  "[" {increaseDepth();commandLocation=CommandLocation.REFERENCE; yybegin(WAITING_COMMAND);return COMMAND_START; }
  "|" {yybegin(WAITING_PROPERTY_REFERENCE_PARAMETER_TOKEN); return PIPE;}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
  {SCRIPTED_VARIABLE_ID} {return SCRIPTED_VARIABLE_REFERENCE_TOKEN;}
}

//icon rules

<CHECKING_ICON_START>{
  {CHECK_ICON_START} {
    //特殊处理
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
}
<WAITING_ICON>{
  {WHITE_SPACE} {yybegin(nextStateForText()); return WHITE_SPACE; }
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "£" {yybegin(nextStateForText()); return ICON_END;}
  "$" {referenceLocation=ReferenceLocation.ICON; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);}
  "[" {increaseDepth(); commandLocation=CommandLocation.ICON; yybegin(WAITING_COMMAND); return COMMAND_START;}
  "|" {yybegin(WAITING_ICON_FRAME); return PIPE;}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
  {ICON_ID} {yybegin(WAITING_ICON_ID_FINISHED); return ICON_ID;}
}
<WAITING_ICON_ID_FINISHED>{
  {WHITE_SPACE} {yybegin(nextStateForText()); return WHITE_SPACE; }
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "£" {yybegin(nextStateForText()); return ICON_END;}
  "|" {yybegin(WAITING_ICON_FRAME); return PIPE;}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
}
<WAITING_ICON_FRAME>{
  {WHITE_SPACE} {yybegin(nextStateForText()); return WHITE_SPACE; }
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "$" {referenceLocation=ReferenceLocation.ICON_FRAME; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);}
  "£" {yybegin(nextStateForText()); return ICON_END;}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
  {ICON_FRAME} {yybegin(WAITING_ICON_FRAME_FINISHED); return ICON_FRAME;}
}
<WAITING_ICON_FRAME_FINISHED>{
   {WHITE_SPACE} {yybegin(nextStateForText()); return WHITE_SPACE; }
   \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
   "£" {yybegin(nextStateForText()); return ICON_END;}
   "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
   "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
}
 
//command rules
 
<WAITING_COMMAND>{
  {WHITE_SPACE} {return WHITE_SPACE; }
  . {
	  if(yycharat(0) == '\'') {
		  yybegin(WAITING_CONCEPT);
		  return LEFT_SINGLE_QUOTE;
	  } else {
		  yypushback(1);
		  yybegin(WAITING_COMMAND_SCOPE_OR_FIELD);
	  }
  }
}
<WAITING_COMMAND_SCOPE_OR_FIELD>{
  {WHITE_SPACE} {return WHITE_SPACE; }
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "]" {decreaseDepth(); yybegin(nextStateForCommand()); return COMMAND_END;}
  "$" {referenceLocation=ReferenceLocation.COMMAND; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
  "." {yybegin(WAITING_COMMAND_SCOPE_OR_FIELD); return DOT;}
  {COMMAND_SCOPE_ID_WITH_SUFFIX} {yypushback(1); return COMMAND_SCOPE_ID;}
  {COMMAND_FIELD_ID_WITH_SUFFIX} {yypushback(1); return COMMAND_FIELD_ID;}
}
<WAITING_CONCEPT> {
  {WHITE_SPACE} {return WHITE_SPACE; }
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "]" {decreaseDepth();yybegin(nextStateForCommand()); return COMMAND_END;}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
  "'" { return RIGHT_SINGLE_QUOTE; }
  {CONCEPT_NAME} { return CONCEPT_NAME_ID; }
  "," { inConceptText=true; yybegin(WAITING_CONCEPT_TEXT); return COMMA; }
}
<WAITING_CONCEPT_TEXT> {
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "]" {decreaseDepth();yybegin(nextStateForCommand()); return COMMAND_END;}
  "$" {referenceLocation=ReferenceLocation.NORMAL; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);}
  "£" {yypushback(yylength()); yybegin(CHECKING_ICON_START);}
  "[" {increaseDepth();commandLocation=CommandLocation.NORMAL; yybegin(WAITING_COMMAND);return COMMAND_START; }
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
}

//colorful text rules

<WAITING_CHECK_COLORFUL_TEXT_START>{
  {CHECK_COLORFUL_TEXT_START} {
    //特殊处理
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
}
<WAITING_COLOR_ID>{
  {WHITE_SPACE} {yybegin(WAITING_COLORFUL_TEXT); return WHITE_SPACE; }
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
  {COLOR_ID} {yybegin(WAITING_COLORFUL_TEXT); return COLOR_ID;}
  [^] {yypushback(yylength()); yybegin(WAITING_COLORFUL_TEXT); } //提高兼容性
}
<WAITING_COLORFUL_TEXT>{
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
  "$" {referenceLocation=ReferenceLocation.NORMAL; yypushback(yylength()); yybegin(CHECKING_PROPERTY_REFERENCE_START);}
  "£" {yypushback(yylength()); yybegin(CHECKING_ICON_START);}
  "[" {increaseDepth();commandLocation=CommandLocation.NORMAL; yybegin(WAITING_COMMAND);return COMMAND_START; }
  "§" {yypushback(yylength()); yybegin(WAITING_CHECK_COLORFUL_TEXT_START);}
  "§!" {decreaseDepth(); yybegin(nextStateForText()); return COLORFUL_TEXT_END;}
}

<WAITING_PROPERTY_END>{
  {WHITE_SPACE} {return WHITE_SPACE; } //继续解析
  {END_OF_LINE_COMMENT} {return COMMENT; }
  \" {yypushback(yylength()); yybegin(WAITING_CHECK_RIGHT_QUOTE);}
}

<WAITING_CHECK_RIGHT_QUOTE>{
  {CHECK_RIGHT_QUOTE} {
      //特殊处理
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
}

<WAITING_RICH_TEXT, WAITING_COLORFUL_TEXT, WAITING_CONCEPT_TEXT> {
  "]" {
	  if(inConceptText) {
		  inConceptText = false;
          decreaseDepth();
          yybegin(nextStateForCommand());
          return COMMAND_END;
	  }
	  return STRING_TOKEN;
  }
  {VALID_ESCAPE_TOKEN} {return VALID_ESCAPE_TOKEN;}
  {INVALID_ESCAPE_TOKEN} {return INVALID_ESCAPE_TOKEN;}
  {DOUBLE_LEFT_BRACKET} {return DOUBLE_LEFT_BRACKET;}
  {STRING_TOKEN} {return STRING_TOKEN;}
}

{EOL} { depth=0;inConceptText=false; yybegin(YYINITIAL); return WHITE_SPACE; }
[^] {return BAD_CHARACTER; }
