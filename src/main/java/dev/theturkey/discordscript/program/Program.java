package dev.theturkey.discordscript.program;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.codeblock.CodeBlock;
import dev.theturkey.discordscript.program.codeblock.FunctionBlock;
import dev.theturkey.discordscript.tokenizer.Token;

import java.util.ArrayList;
import java.util.List;

public class Program extends CodeBlock
{
	private List<CodeBlock> codeBlocks;

	public Program(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream wrapper)
	{
		codeBlocks = new ArrayList<>();

		Token t;
		while((t = wrapper.getNextToken()) != null)
		{
			if(t.getType().isWhiteSpace())
				continue;

			CodeBlock b = this.parseCode(t, wrapper);
			if(wrapper.hasErrored())
				return false;
			if(b != null)
				codeBlocks.add(b);
		}
		return true;
	}

	@Override
	public void execute(Scope scope)
	{
		for(CodeBlock cb : codeBlocks)
			if(cb instanceof FunctionBlock)
				scope.registerFunction((FunctionBlock) cb);

		Scope innerScope = new Scope(scope);
		for(CodeBlock cb : codeBlocks)
		{
			if(innerScope.isErrorred())
			{
				scope.setErrored();
				return;
			}
			if(cb instanceof FunctionBlock)
				continue;
			cb.execute(innerScope);
		}
	}

	@Override
	public String getBlockString()
	{
		return "Program";
	}
}
