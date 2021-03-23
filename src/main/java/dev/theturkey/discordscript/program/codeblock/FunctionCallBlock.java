package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class FunctionCallBlock extends CodeBlock
{

	private ExpressionBlock expression;

	public FunctionCallBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.PLAIN_STRING))
			return false;

		if(!assertNextToken(TokenEnum.LEFT_PARENTHESIS))
			return false;

		Token t = stream.getCurrentToken();

		while(t.getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			ArgumentBlock argumentBlock = new ArgumentBlock(stream);
			t = stream.getCurrentToken();
			if(t.getType() == TokenEnum.COMMA)
				t = stream.getNextRealToken();
		}

		return assertCurrentToken(TokenEnum.RIGHT_PARENTHESIS);
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Function Call";
	}
}
