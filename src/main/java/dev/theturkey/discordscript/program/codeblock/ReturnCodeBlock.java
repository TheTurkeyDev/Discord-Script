package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class ReturnCodeBlock extends CodeBlock
{
	private ExpressionBlock expressionBlock;

	public ReturnCodeBlock(TokenStream wrapper)
	{

		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.RETURN))
			return false;

		expressionBlock = new ExpressionBlock(stream);

		return !stream.hasErrored() && assertCurrentToken(TokenEnum.SEMI_COLON);
	}

	@Override
	public void execute(Scope scope)
	{
		expressionBlock.execute(scope);
		scope.setReturned(expressionBlock.getValue());
	}

	@Override
	public String getBlockString()
	{
		return null;
	}
}
