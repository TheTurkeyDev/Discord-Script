package dev.theturkey.discordscript.program;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.codeblock.FunctionBlock;

import java.util.List;

public class DefaultScope extends Scope
{
	public DefaultScope(OutputWrapper wrapper)
	{
		super(null);
		this.output = wrapper;

		this.registerFunction(new BaseFunction("msg")
		{
			@Override
			public void execute(Scope scope)
			{
				if(passedArgs.length < 1)
				{
					scope.throwError("MissingArgumentError", "");
					return;
				}
				scope.output.writeLine(String.valueOf(passedArgs[0]));
			}
		});

		this.registerFunction(new BaseFunction("arrayZero")
		{
			@Override
			public void execute(Scope scope)
			{
				if(passedArgs.length < 1)
				{
					scope.throwError("MissingArgumentError", "");
					return;
				}
				super.returnVal = new int[(int) passedArgs[0]];
			}
		});

		this.registerFunction(new BaseFunction("len")
		{
			@Override
			public void execute(Scope scope)
			{
				if(passedArgs.length < 1)
				{
					scope.throwError("MissingArgumentError", "");
					return;
				}
				super.returnVal = ((List<?>) passedArgs[0]).size();
			}
		});
	}

	private static class BaseFunction extends FunctionBlock
	{
		public BaseFunction(String name)
		{
			super(null);
			this.name = name;
		}

		@Override
		public boolean parse(TokenStream stream)
		{
			return true;
		}
	}

}
