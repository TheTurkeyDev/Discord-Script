package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class MultiLineCommentBlock extends CodeBlock
{
	public MultiLineCommentBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		while(stream.getNextToken().getType() != TokenEnum.CMULTI_COMMENT_END) ;

		return true;
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Multi Line Comment";
	}
}
