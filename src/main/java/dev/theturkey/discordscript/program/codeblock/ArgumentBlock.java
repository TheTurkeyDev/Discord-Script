package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class ArgumentBlock extends CodeBlock
{
	private Object value;

	public ArgumentBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		Token t = stream.getCurrentToken();
		List<Token> tokenList = new ArrayList<>();
		while(t.getType() != TokenEnum.COMMA && t.getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			if(t.getType() == TokenEnum.LEFT_SQUARE_BRACE)
			{
				ArrayBlock arrayBlock = new ArrayBlock(stream);
				if(!assertCurrentToken(TokenEnum.RIGHT_SQUARE_BRACE))
					return false;
			}
			else
			{
				tokenList.add(t);
			}

			t = stream.getNextToken();
		}
		return true;
	}

	@Override
	public void execute(Scope scope)
	{
		value = "test";
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public String getBlockString()
	{
		return "Argument";
	}
}
