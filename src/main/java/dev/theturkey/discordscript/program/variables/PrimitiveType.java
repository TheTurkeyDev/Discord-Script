package dev.theturkey.discordscript.program.variables;

import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.Arrays;
import java.util.List;

public enum PrimitiveType
{
	INT(TokenEnum.INT),
	LONG(TokenEnum.LONG),
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

	private static final List<PrimitiveType> NUMBER_TYPES = Arrays.asList(INT, LONG, FLOAT, BOOLEAN);
	public boolean isNumber()
	{
		return NUMBER_TYPES.contains(this);
	}
}
