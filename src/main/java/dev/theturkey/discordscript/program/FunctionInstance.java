package dev.theturkey.discordscript.program;

import dev.theturkey.discordscript.program.codeblock.FunctionBlock;

public class FunctionInstance
{
	private Scope scope;
	private FunctionBlock functionBlock;

	public FunctionInstance(Scope scope, FunctionBlock functionBlock)
	{
		this.scope = scope;
		this.functionBlock = functionBlock;
	}

	public Object invoke(Object[] args)
	{
		this.functionBlock.execute(scope, args);
		return this.functionBlock.getReturnVal();
	}

	public FunctionBlock getFunctionBlock()
	{
		return functionBlock;
	}
}
