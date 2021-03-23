package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class ArrayBlock extends CodeBlock
{
	private List<String> contents;

	public ArrayBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		contents = new ArrayList<>();
		Token t = stream.getNextToken();
		StringBuilder content = new StringBuilder();
		while(t.getType() != TokenEnum.RIGHT_SQUARE_BRACE)
		{
			if(t.getType() == TokenEnum.COMMA)
			{
				contents.add(content.toString());
			}
			else
			{
				content.append(stream.getTokenStr());
				content = new StringBuilder();
			}

			t = stream.getNextToken();
		}
		contents.add(content.toString());
		return true;
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return null;
	}
}
