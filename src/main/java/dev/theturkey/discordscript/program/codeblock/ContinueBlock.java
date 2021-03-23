package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class ContinueBlock extends CodeBlock
{
	public ContinueBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.CONTINUE))
			return false;
		return assertNextToken(TokenEnum.SEMI_COLON);
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Continue";
	}
}
