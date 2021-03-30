package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class CommentBlock extends CodeBlock
{
	public CommentBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		while(stream.getNextToken().getType() != TokenEnum.NEW_LINE) ;

		return true;
	}

	@Override
	public void execute(Scope scope)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Comment";
	}
}
