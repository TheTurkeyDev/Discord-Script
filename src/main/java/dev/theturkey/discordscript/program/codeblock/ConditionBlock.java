package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class ConditionBlock extends CodeBlock
{
	private ExpressionBlock expr;

	public ConditionBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		expr = new ExpressionBlock(stream);
		return true;
	}

	@Override
	public void execute(Scope scope)
	{
		expr.execute(scope);
	}

	public boolean getValue(Scope scope)
	{
		execute(scope);
		Object value = expr.getValue();
		return value.equals(true) || value.equals(1);
	}


	@Override
	public String getBlockString()
	{
		return "Expression";
	}
}
