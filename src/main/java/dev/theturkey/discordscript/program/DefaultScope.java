package dev.theturkey.discordscript.program;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.codeblock.FunctionBlock;

public class DefaultScope extends Scope
{
	public DefaultScope(OutputWrapper wrapper)
	{
		super(null);
		this.output = wrapper;

		this.registerFunction(new MessageFunction(wrapper));
	}

	private static class MessageFunction extends FunctionBlock
	{
		public MessageFunction(OutputWrapper wrapper)
		{
			super(null);
			this.name = "msg";
		}

		@Override
		public boolean parse(TokenStream stream)
		{
			return true;
		}

		@Override
		public void execute(Scope scope)
		{
			System.out.println(passedArgs);
			if(passedArgs.length < 1)
			{
				scope.throwError("MissingArgumentError", "");
				return;
			}
			scope.output.writeLine(String.valueOf(passedArgs[0]));
		}
	}

}
