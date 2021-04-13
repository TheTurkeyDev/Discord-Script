package dev.theturkey.discordscript.tokenizer;

import java.util.Arrays;
import java.util.List;

public enum TokenEnum
{
	VOID("void"),
	INT("int"),
	LONG("long"),
	STRING("string"),
	FLOAT("float"),
	BOOLEAN("boolean"),

	WHILE("while"),
	DO("do"),
	FOR("for"),
	IF("if"),
	ELSE("else"),

	CONTINUE("continue"),
	RETURN("return"),
	BREAK("break"),
	TRUE("true"),
	FALSE("false"),

	LEFT_SQUARE_BRACE("["),
	RIGHT_SQUARE_BRACE("]"),
	LEFT_CURLY_BRACE("{"),
	RIGHT_CURLY_BRACE("}"),
	LEFT_PARENTHESIS("("),
	RIGHT_PARENTHESIS(")"),

	PLUS("+"),
	PLUS_PLUS("++"),
	MINUS("-"),
	MINUS_MINUS("--"),
	MULTIPLY("*"),
	DIVIDE("/"),
	MODULUS("%"),
	//POWER("^"),
	EQUALS("="),
	PLUS_EQUALS("+="),
	MINUS_EQUALS("-="),
	NOT_EQUALS("!="),
	NOT("!"),
	EQUALITY("=="),
	LESS_THAN("<"),
	LESS_THAN_OR_EQUAL("<="),
	GREATER_THAN(">"),
	GREATER_THAN_OR_EQUAL(">="),
	OR("||"),
	AND("&&"),

	SEMI_COLON(";"),
	COLON(":"),
	QUOTE("\""),
	LITERAL_QUOTE("'"),
	LITERAL("$"),
	COMMA(","),
	DECIMAL("."),
	COMMENT("//"),
	MULTI_COMMENT_START("/*"),
	CMULTI_COMMENT_END("*/"),
	SPACE(" "),
	TAB("\t"),
	NEW_LINE("\n"),
	CARRIAGE_RETURN("\r"),

	PLAIN_STRING(""),
	NUMBER("");


	private String codeStr;

	TokenEnum(String str)
	{
		this.codeStr = str;
	}

	private String getCodeStr()
	{
		return codeStr;
	}

	public static TokenEnum getTokenFromCodeStr(String codeStr)
	{
		for(TokenEnum keyWord : TokenEnum.values())
			if(keyWord.getCodeStr().equals(codeStr))
				return keyWord;
		return null;
	}

	private static final List<TokenEnum> VAR_TYPES = Arrays.asList(VOID, INT, STRING, FLOAT, BOOLEAN);
	public boolean isVarType()
	{
		return VAR_TYPES.contains(this);
	}

	private static final List<TokenEnum> COND_TYPES = Arrays.asList(NOT_EQUALS, NOT, EQUALITY, LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, OR, AND);
	public boolean isConditional()
	{
		return COND_TYPES.contains(this);
	}

	private static final List<TokenEnum> WHITE_SPACE_TYPES = Arrays.asList(SPACE, NEW_LINE, CARRIAGE_RETURN);
	public boolean isWhiteSpace()
	{
		return WHITE_SPACE_TYPES.contains(this);
	}
}
