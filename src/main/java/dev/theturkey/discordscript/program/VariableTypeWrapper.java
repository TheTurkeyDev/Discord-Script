package dev.theturkey.discordscript.program;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class VariableTypeWrapper
{
	public VariableTypeWrapper(TokenStream stream)
	{
		Token t;
		if(stream.getCurrentToken().getType().isVarType())
		{
			//TODO: stuff
			return;
		}
		while((t = stream.getNextRealToken()).getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			if(!t.getType().isVarType())
				stream.throwError("Tuple", "Primitive type", t.getType().name());

			if(stream.peekNextRealToken().getType() == TokenEnum.RIGHT_PARENTHESIS)
				continue;

			t = stream.getNextRealToken();
			if(t.getType() != TokenEnum.COMMA)
				stream.throwError("Tuple", ",", t.getType().name());
		}
	}
}
