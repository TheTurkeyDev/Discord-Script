package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class ArgumentBlock extends CodeBlock
{
	private Object value;
	private ExpressionBlock expressionBlock;

	public ArgumentBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		Token t = stream.peekNextRealToken();
		while(t.getType() != TokenEnum.COMMA && t.getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			if(t.getType() == TokenEnum.LEFT_SQUARE_BRACE)
			{
				ArrayBlock arrayBlock = new ArrayBlock(stream);
				if(!assertCurrentToken(TokenEnum.RIGHT_SQUARE_BRACE))
					return false;
				stream.getNextToken();
			}
			else
			{
				expressionBlock = new ExpressionBlock(stream);
			}
			t = stream.getCurrentToken();
		}

		return true;
	}

	@Override
	public void execute(Scope scope)
	{
		if(expressionBlock == null)
		{
			value = "test";
		}
		else
		{
			expressionBlock.execute(scope);
			value = expressionBlock.getValue();
		}
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
