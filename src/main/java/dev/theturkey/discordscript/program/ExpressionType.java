package dev.theturkey.discordscript.program;

import dev.theturkey.discordscript.tokenizer.TokenEnum;

public enum ExpressionType
{
	EQUALS(TokenEnum.EQUALS),
	PLUS_PLUS(TokenEnum.PLUS_PLUS),
	MINUS_MINUS(TokenEnum.MINUS_MINUS),
	PLUS_EQUALS(TokenEnum.PLUS_EQUALS),
	MINUS_EQUALS(TokenEnum.MINUS_EQUALS);

	private final TokenEnum tokenEnum;

	ExpressionType(TokenEnum tokenEnum)
	{
		this.tokenEnum = tokenEnum;
	}

	public static ExpressionType getExpressionTypeFromToken(TokenEnum tokenEnum)
	{
		for(ExpressionType expressionType : ExpressionType.values())
			if(expressionType.tokenEnum == tokenEnum)
				return expressionType;

		return null;
	}
}
