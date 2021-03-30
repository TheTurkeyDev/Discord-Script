package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.FunctionInstance;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallBlock extends CodeBlock
{
	private String functionName;
	private List<ArgumentBlock> arguments;

	public FunctionCallBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.PLAIN_STRING))
			return false;

		functionName = stream.getTokenStr();

		if(!assertNextToken(TokenEnum.LEFT_PARENTHESIS))
			return false;

		Token t = stream.getCurrentToken();

		arguments = new ArrayList<>();
		while(t.getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			arguments.add(new ArgumentBlock(stream));
			t = stream.getCurrentToken();
			if(t.getType() == TokenEnum.COMMA)
				t = stream.getNextRealToken();
		}

		return assertCurrentToken(TokenEnum.RIGHT_PARENTHESIS);
	}

	@Override
	public void execute(Scope scope)
	{
		FunctionInstance function = scope.getFunctionFromName(functionName);
		Object[] argsToPass = new Object[arguments.size()];
		for(int i = 0; i < arguments.size(); i++)
		{
			ArgumentBlock argumentBlock = arguments.get(i);
			argumentBlock.execute(scope);
			argsToPass[i] = argumentBlock.getValue();
		}

		function.invoke(argsToPass);
		//TODO: Handle the return
	}

	@Override
	public String getBlockString()
	{
		return "Function Call";
	}
}
