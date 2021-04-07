package dev.theturkey.discordscript.program.variables;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class VariableType
{
	public PrimitiveType type;
	private VariableType secondType;

	public VariableType(PrimitiveType type)
	{
		this(type, null);
	}

	public VariableType(PrimitiveType type, VariableType secondType)
	{
		this.type = type;
		this.secondType = secondType;
	}

	public boolean isTuple()
	{
		return secondType != null;
	}

	public static VariableType getVariableType(TokenStream stream)
	{
		Token t;
		if(stream.getCurrentToken().getType().isVarType())
		{
			return new VariableType(PrimitiveType.getTypeFromToken(stream.getCurrentToken().getType()));
		}

		t = stream.getNextRealToken();
		PrimitiveType firstType = PrimitiveType.getTypeFromToken(t.getType());
		if(firstType == null)
		{
			stream.throwError("Tuple", "primitive type", t.getType().name());
			return null;
		}

		t = stream.getNextRealToken();
		if(t.getType() != TokenEnum.COMMA)
			stream.throwError("Tuple", ",", t.getType().name());

		t = stream.getNextRealToken();
		VariableType toReturn;
		if(t.getType() == TokenEnum.LEFT_PARENTHESIS)
		{
			toReturn = new VariableType(firstType, getVariableType(stream));
		}
		else
		{
			PrimitiveType secondType = PrimitiveType.getTypeFromToken(t.getType());
			toReturn = new VariableType(firstType, new VariableType(secondType));
		}

		t = stream.getNextRealToken();
		if(t.getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			stream.throwError("Tuple", ")", t.getType().name());
			return null;
		}
		return toReturn;
	}
}
