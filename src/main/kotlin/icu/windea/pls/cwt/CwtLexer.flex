package icu.windea.pls.cwt.psi;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static icu.windea.pls.cwt.psi.CwtElementTypes.*;

%%

%public
%class _CwtLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state WAITING_PROPERTY_KEY
%state WATIING_PROPERTY_SEPARATOR
%state WAITING_PROPERTY_VALUE
%state WAITING_PROPERTY_VALUE_END
%state WAITING_PROPERTY_END
%state WAITING_OPTION
%state WAITING_OPTION_TOP_STRING
%state WAITING_OPTION_KEY
%state WATIING_OPTION_SEPARATOR
%state WAITING_OPTION_VALUE
%state WAITING_OPTION_VALUE_TOP_STRING
%state WAITING_OPTION_VALUE_END
%state WAITING_OPTION_END
%state WAITING_DOCUMENTATION

%{
    private int optionDepth = 0;
    
    public _CwtLexer() {
      this((java.io.Reader)null);
    }
    
    public boolean nextCharIs(char c) {
	    try {
			return yycharat(yylength()) == c;
		} catch(Exception e) {
	        return false;
		}
    }
%}

EOL=\s*\R
BLANK=\s+
WHITE_SPACE=[\s&&[^\r\n]]+

DOCUMENTATION_COMMENT_START=###
OPTION_COMMENT_START=##
COMMENT=(#)|(#[^#\r\n][^\r\n]*)
RELAX_COMMENT=#[^\r\n]*

CHECK_PROPERTY_KEY=({PROPERTY_KEY_TOKEN})?({WHITE_SPACE})?((=)|(\!=)|(<>))
CHECK_OPTION_KEY=({OPTION_KEY_TOKEN})?({WHITE_SPACE})?((=)|(\!=)|(<>))

PROPERTY_KEY_TOKEN=([^#={}\s\"][^#={}\s]*)|(\"([^\"\\\r\n]|\\.)*\"?)
OPTION_KEY_TOKEN=([^#={}\s\"][^={}\s]*)|(\"([^\"\\\r\n]|\\.)*\"?)
BOOLEAN_TOKEN=(yes)|(no)
INT_TOKEN=[+-]?[0-9]+ //leading zero is permitted
FLOAT_TOKEN=[+-]?[0-9]*(\.[0-9]+) //leading zero is permitted
STRING_TOKEN=([^#={}\s\"][^#={}\s]*)|(\"([^\"\\\r\n]|\\.)*\"?)
TOP_STRING_TOKEN=([^\s])|([^={}\s][^={}\r\n]*[^={}\s]) //顶级的optionValue可以包含空格
DOCUMENTATION_TOKEN=[^\s][^\r\n]*

%%
<YYINITIAL> {
  {BLANK} { return WHITE_SPACE; }

  "{" {return LEFT_BRACE;}
  "}" {return RIGHT_BRACE;}
  
  {DOCUMENTATION_COMMENT_START} { yybegin(WAITING_DOCUMENTATION); return DOCUMENTATION_START; }
  {OPTION_COMMENT_START} { if(!nextCharIs('#')) { yybegin(WAITING_OPTION); return OPTION_START; } }
  {COMMENT} { return COMMENT; }
  
  {CHECK_PROPERTY_KEY} {yypushback(yylength()); yybegin(WAITING_PROPERTY_KEY);}
  
  {BOOLEAN_TOKEN} { yybegin(WAITING_PROPERTY_VALUE_END); return BOOLEAN_TOKEN; }
  {INT_TOKEN} { yybegin(WAITING_PROPERTY_VALUE_END); return INT_TOKEN; }
  {FLOAT_TOKEN} { yybegin(WAITING_PROPERTY_VALUE_END); return FLOAT_TOKEN; }
  {STRING_TOKEN} {yybegin(WAITING_PROPERTY_VALUE_END); return STRING_TOKEN;}
} 
<WAITING_PROPERTY_KEY>{
  {EOL} { yybegin(YYINITIAL); return WHITE_SPACE;}
  {WHITE_SPACE} {return WHITE_SPACE;}
  
  "{" {yybegin(YYINITIAL); return LEFT_BRACE;}
  "}" {yybegin(YYINITIAL); return RIGHT_BRACE;}
  
  {PROPERTY_KEY_TOKEN} {yybegin(WATIING_PROPERTY_SEPARATOR); return PROPERTY_KEY_TOKEN;}
}
<WATIING_PROPERTY_SEPARATOR>{
  {EOL} { yybegin(YYINITIAL); return WHITE_SPACE;}
  {WHITE_SPACE} {return WHITE_SPACE;}
  
  "{" {yybegin(YYINITIAL); return LEFT_BRACE;}
  "}" {yybegin(YYINITIAL); return RIGHT_BRACE;}
  "="|"==" {yybegin(WAITING_PROPERTY_VALUE); return EQUAL_SIGN;}
  "!="|"<>" {yybegin(WAITING_PROPERTY_VALUE); return NOT_EQUAL_SIGN;}
    
  {COMMENT} {return COMMENT;}
}
<WAITING_PROPERTY_VALUE>{
  {EOL} { yybegin(YYINITIAL); return WHITE_SPACE;}
  {WHITE_SPACE} {return WHITE_SPACE;}
  
  "{" {yybegin(YYINITIAL); return LEFT_BRACE;}
  "}" {yybegin(YYINITIAL); return RIGHT_BRACE;}
  
  {DOCUMENTATION_COMMENT_START} { yybegin(WAITING_DOCUMENTATION); return DOCUMENTATION_START; }
  {OPTION_COMMENT_START} { if(!nextCharIs('#')) { yybegin(WAITING_OPTION); return OPTION_START; } }
  {COMMENT} { return COMMENT; }

  {CHECK_PROPERTY_KEY} {yypushback(yylength()); yybegin(WAITING_PROPERTY_KEY);}
  {BOOLEAN_TOKEN} { yybegin(WAITING_PROPERTY_END); return BOOLEAN_TOKEN; }
  {INT_TOKEN} { yybegin(WAITING_PROPERTY_END); return INT_TOKEN; }
  {FLOAT_TOKEN} { yybegin(WAITING_PROPERTY_END); return FLOAT_TOKEN; }
  {STRING_TOKEN} {yybegin(WAITING_PROPERTY_END); return STRING_TOKEN;}
}
<WAITING_PROPERTY_VALUE_END>{
  {BLANK} { yybegin(YYINITIAL); return WHITE_SPACE;}
  
  "{" {yybegin(YYINITIAL); return LEFT_BRACE;}
  "}" {yybegin(YYINITIAL); return RIGHT_BRACE;}
    
  {RELAX_COMMENT} {return COMMENT;}
}
<WAITING_PROPERTY_END>{
  {BLANK} { yybegin(YYINITIAL);  return WHITE_SPACE;}
  
  "{" {yybegin(YYINITIAL); return LEFT_BRACE;}
  "}" {yybegin(YYINITIAL); return RIGHT_BRACE;}
  
  {RELAX_COMMENT} {return COMMENT;}
}

<WAITING_OPTION>{
  {EOL} {yybegin(YYINITIAL); return WHITE_SPACE;}    
  {WHITE_SPACE} {return WHITE_SPACE;}
      
  "{" { optionDepth++; return LEFT_BRACE;}
  "}" { optionDepth--; return RIGHT_BRACE;}

  {RELAX_COMMENT} {return COMMENT; }
  
  {CHECK_OPTION_KEY} {yypushback(yylength()); yybegin(WAITING_OPTION_KEY);}
  
  {BOOLEAN_TOKEN} { yybegin(WAITING_OPTION_VALUE_END); return BOOLEAN_TOKEN; }
  {INT_TOKEN} { yybegin(WAITING_OPTION_VALUE_END); return INT_TOKEN; }
  {FLOAT_TOKEN} { yybegin(WAITING_OPTION_VALUE_END); return FLOAT_TOKEN; }
  {STRING_TOKEN} {
    if(optionDepth==0){
    	yypushback(yylength()); yybegin(WAITING_OPTION_TOP_STRING);
   	}else{
    	yybegin(WAITING_OPTION_VALUE_END); return STRING_TOKEN;
    }
  }
}
<WAITING_OPTION_TOP_STRING>{
  {TOP_STRING_TOKEN} {yybegin(WAITING_OPTION_VALUE_END); return STRING_TOKEN;}
}
<WAITING_OPTION_KEY>{
  {EOL} { yybegin(YYINITIAL); return WHITE_SPACE;}
  {WHITE_SPACE} {return WHITE_SPACE;}
  
  "{" {yybegin(WAITING_OPTION); optionDepth++; return LEFT_BRACE;}
  "}" {yybegin(WAITING_OPTION); optionDepth--; return RIGHT_BRACE;}
  
  {RELAX_COMMENT} {return COMMENT; }
  
  {OPTION_KEY_TOKEN} {yybegin(WATIING_OPTION_SEPARATOR); return OPTION_KEY_TOKEN;} //option.value可以无需双引号直接包含空格
}
<WATIING_OPTION_SEPARATOR>{
  {EOL} { yybegin(YYINITIAL); return WHITE_SPACE;}
  {WHITE_SPACE} {return WHITE_SPACE;}
  
  "{" {yybegin(WAITING_OPTION); optionDepth++; return LEFT_BRACE;}
  "}" {yybegin(WAITING_OPTION); optionDepth--; return RIGHT_BRACE;}
  "="|"==" {yybegin(WAITING_OPTION_VALUE); return EQUAL_SIGN;}
  "!="|"<>" {yybegin(WAITING_OPTION_VALUE); return NOT_EQUAL_SIGN;}
  
  {RELAX_COMMENT} {return COMMENT; }
}

<WAITING_OPTION_VALUE>{
  {EOL} { yybegin(YYINITIAL); return WHITE_SPACE;}
  {WHITE_SPACE} {return WHITE_SPACE;}
  
  "{" {yybegin(WAITING_OPTION); optionDepth++; return LEFT_BRACE;}
  "}" {yybegin(WAITING_OPTION); optionDepth--; return RIGHT_BRACE;}
  
  {DOCUMENTATION_COMMENT_START} { yybegin(WAITING_DOCUMENTATION); return DOCUMENTATION_START; }
  {OPTION_COMMENT_START} { if(!nextCharIs('#')) { yybegin(WAITING_OPTION); return OPTION_START; } }
  {COMMENT} { return COMMENT; }
  
  {CHECK_OPTION_KEY} {yypushback(yylength()); yybegin(WAITING_OPTION_KEY);}
  
  {BOOLEAN_TOKEN} { yybegin(WAITING_OPTION_END); return BOOLEAN_TOKEN; }
  {INT_TOKEN} { yybegin(WAITING_OPTION_END); return INT_TOKEN; }
  {FLOAT_TOKEN} { yybegin(WAITING_OPTION_END); return FLOAT_TOKEN; }
  {STRING_TOKEN} {
    if(optionDepth==0){
    	yypushback(yylength()); yybegin(WAITING_OPTION_VALUE_TOP_STRING);
   	}else{
    	yybegin(WAITING_OPTION_END); return STRING_TOKEN;
    }
  }
}

<WAITING_OPTION_VALUE_TOP_STRING>{
  {TOP_STRING_TOKEN} {yybegin(WAITING_OPTION_END); return STRING_TOKEN;}
}

<WAITING_OPTION_END>{
  {EOL} { yybegin(YYINITIAL);  return WHITE_SPACE;}
  {WHITE_SPACE} {yybegin(WAITING_OPTION);  return WHITE_SPACE;}
  
  "{" {yybegin(WAITING_OPTION); optionDepth++; return LEFT_BRACE;}
  "}" {yybegin(WAITING_OPTION); optionDepth--; return RIGHT_BRACE;}
 
  {RELAX_COMMENT} {return COMMENT; }
}

<WAITING_OPTION_VALUE_END>{
  {EOL} { yybegin(YYINITIAL); return WHITE_SPACE;}
  {WHITE_SPACE} {yybegin(WAITING_OPTION); return WHITE_SPACE;}
  
  "{" {yybegin(WAITING_OPTION); optionDepth++; return LEFT_BRACE;}
  "}" {yybegin(WAITING_OPTION); optionDepth--; return RIGHT_BRACE;}
 
  {RELAX_COMMENT} {return COMMENT; }
}

<WAITING_DOCUMENTATION>{
  {EOL} {yybegin(YYINITIAL); return WHITE_SPACE;}
  {WHITE_SPACE} { return WHITE_SPACE;}
    
  {DOCUMENTATION_TOKEN} { yybegin(YYINITIAL); return DOCUMENTATION_TOKEN;}
}

[^] { return BAD_CHARACTER; }
