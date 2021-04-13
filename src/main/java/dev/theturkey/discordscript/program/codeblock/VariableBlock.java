package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.program.variables.VariableInstance;
import dev.theturkey.discordscript.program.variables.VariableType;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;

public class VariableBlock extends CodeBlock
{
	private VariableType variableType;
	private String name;
	private ExpressionBlock value;
	private boolean isArray;

	public VariableBlock(TokenStream stream)
	{
		super(stream);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		variableType = VariableType.getVariableType(stream);
		isArray = false;
		if(stream.getNextRealToken().getType() == TokenEnum.LEFT_SQUARE_BRACE)
		{
			isArray = true;
			if(!assertNextToken(TokenEnum.RIGHT_SQUARE_BRACE))
				return false;
			stream.getNextRealToken();
		}

		if(!assertCurrentToken(TokenEnum.PLAIN_STRING))
			return false;

		this.name = stream.getTokenStr();

		Token t = stream.getNextRealToken();
		if(t.getType() == TokenEnum.EQUALS)
			value = new ExpressionBlock(stream);

		t = stream.getCurrentToken();

		return !stream.hasErrored() && (t.getType() == TokenEnum.SEMI_COLON || t.getType() == TokenEnum.COMMA || t.getType() == TokenEnum.COLON);
	}

	@Override
	public void execute(Scope scope)
	{
		if(value != null)
			value.execute(scope);

		Object varVal = value != null ? value.getValue() : null;
		if(isArray && varVal instanceof Integer)
			varVal = new ArrayList<>((int) varVal);

		VariableInstance variable = scope.createNewVariable(variableType, name, varVal);
		variable.setScope(scope);
		variable.setIsArray(isArray);
	}

	public VariableType getVarType()
	{
		return variableType;
	}

	public String getVarName()
	{
		return name;
	}

	@Override
	public String getBlockString()
	{
		return "Variable Creation";
	}
}
