package dev.theturkey.discordscript.program.variables;

import dev.theturkey.discordscript.tokenizer.TokenEnum;

public enum PrimitiveType
{
	INT(TokenEnum.INT),
	STRING(TokenEnum.STRING),
	FLOAT(TokenEnum.FLOAT),
	BOOLEAN(TokenEnum.BOOLEAN);

	public final TokenEnum tokenEnum;

	PrimitiveType(TokenEnum tokenEnum)
	{
		this.tokenEnum = tokenEnum;
	}

	public static PrimitiveType getTypeFromToken(TokenEnum token)
	{
		for(PrimitiveType type : PrimitiveType.values())
			if(type.tokenEnum == token)
				return type;

		return null;
	}
}
