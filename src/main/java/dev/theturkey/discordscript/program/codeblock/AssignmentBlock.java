package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class AssignmentBlock extends CodeBlock
{

	private ExpressionBlock expression;

	public AssignmentBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.PLAIN_STRING))
			return false;

		Token t = stream.getNextRealToken();
		if(t.getType() == TokenEnum.PLUS_PLUS || t.getType() == TokenEnum.MINUS_MINUS)
		{
			stream.getNextToken();
		}
		else if(t.getType() == TokenEnum.EQUALS || t.getType() == TokenEnum.PLUS_EQUALS || t.getType() == TokenEnum.MINUS_EQUALS)
		{
			expression = new ExpressionBlock(stream);
			if(stream.getCurrentToken().getType() != TokenEnum.SEMI_COLON)
				stream.getNextToken();
		}
		else
		{
			stream.throwError(getBlockString(), "++ OR =", t.getType().name());
		}


		return assertCurrentToken(TokenEnum.SEMI_COLON);
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Assignment";
	}
}
