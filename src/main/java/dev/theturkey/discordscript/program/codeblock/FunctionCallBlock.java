package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.FunctionInstance;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.program.variables.VariableType;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallBlock extends CodeBlock
{
	private String functionName;
	private List<ExpressionBlock> arguments;

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

		Token t = stream.peekNextRealToken();

		arguments = new ArrayList<>();
		while(t.getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			arguments.add(new ExpressionBlock(stream));
			t = stream.getCurrentToken();
			if(t.getType() != TokenEnum.RIGHT_PARENTHESIS && !assertCurrentToken(TokenEnum.COMMA))
				return false;
		}

		return assertCurrentToken(TokenEnum.RIGHT_PARENTHESIS);
	}

	@Override
	public void execute(Scope scope)
	{
		executeAndGetReturn(scope);
	}

	public Object executeAndGetReturn(Scope scope)
	{
		FunctionInstance function = scope.getFunctionFromName(functionName);
		if(function == null)
		{
			scope.throwError("FunctionNotDefinedException", functionName + " is not a defined function name!");
			return null;
		}

		Object[] argsToPass = new Object[arguments.size()];
		for(int i = 0; i < arguments.size(); i++)
		{
			ExpressionBlock argumentBlock = arguments.get(i);
			argumentBlock.execute(scope);
			argsToPass[i] = argumentBlock.getValue();
		}

		return function.invoke(argsToPass);
	}

	@Override
	public String getBlockString()
	{
		return "Function Call";
	}

	public VariableType getFunctionReturnType(Scope scope)
	{
		return scope.getFunctionFromName(functionName).getFunctionBlock().getReturnType();
	}
}
