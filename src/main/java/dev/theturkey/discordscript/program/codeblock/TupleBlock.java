package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class TupleBlock extends CodeBlock
{
	private List<ExpressionBlock> parts;

	public TupleBlock(ExpressionBlock firstPart, TokenStream wrapper)
	{
		this(wrapper);
		parts.add(0, firstPart);
	}

	public TupleBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		parts = new ArrayList<>();
		Token t = stream.getCurrentToken();

		if(t.getType() == TokenEnum.COMMA)
			t = stream.getNextRealToken();

		while(t.getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			parts.add(new ExpressionBlock(stream));
			t = stream.getCurrentToken();
		}
		return false;
	}

	@Override
	public void execute(Scope scope)
	{

	}

	@Override
	public String getBlockString()
	{
		return null;
	}
}
