package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.program.VariableTypeWrapper;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class VariableBlock extends CodeBlock
{
	private boolean isArray = false;

	public VariableBlock(CodeBlock codeBlock, VariableTypeWrapper variableTypeWrapper, TokenStream stream)
	{
		super(stream);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(stream.getNextRealToken().getType() == TokenEnum.LEFT_SQUARE_BRACE)
		{
			isArray = true;
			if(!assertNextToken(TokenEnum.RIGHT_SQUARE_BRACE))
				return false;
			stream.getNextRealToken();
		}

		if(!assertCurrentToken(TokenEnum.PLAIN_STRING))
			return false;

		Token t = stream.getNextRealToken();
		if(t.getType() == TokenEnum.EQUALS)
		{
			ExpressionBlock expressionBlock = new ExpressionBlock(stream);
		}

		t = stream.getCurrentToken();

		return !stream.hasErrored() && (t.getType() == TokenEnum.SEMI_COLON || t.getType() == TokenEnum.COMMA || t.getType() == TokenEnum.COLON);
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Variable Creation";
	}
}
