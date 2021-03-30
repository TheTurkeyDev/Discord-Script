package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;

public class ConditionBlock extends CodeBlock
{
	private ExpressionBlock expressionBlock;

	public ConditionBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		expressionBlock = new ExpressionBlock(stream);
		return true;
	}

	@Override
	public void execute(Scope scope)
	{
		expressionBlock.execute(scope);
	}

	public boolean getValue(Scope scope)
	{
		execute(scope);
		Object val = expressionBlock.getValue();
		return val.equals(1) || val.equals(true);
	}


	@Override
	public String getBlockString()
	{
		return "Expression";
	}
}
