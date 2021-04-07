package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.ExpressionType;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.program.variables.VariableInstance;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class AssignmentBlock extends CodeBlock
{
	private String varName;
	private ExpressionType expressionType;
	private ExpressionBlock expression;

	public AssignmentBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.PLAIN_STRING))
			return false;

		this.varName = stream.getTokenStr();

		Token t = stream.getNextRealToken();
		expressionType = ExpressionType.getExpressionTypeFromToken(t.getType());
		if(t.getType() == TokenEnum.PLUS_PLUS || t.getType() == TokenEnum.MINUS_MINUS)
		{
			stream.getNextToken();
		}
		else if(t.getType() == TokenEnum.EQUALS || t.getType() == TokenEnum.PLUS_EQUALS || t.getType() == TokenEnum.MINUS_EQUALS)
		{
			expression = new ExpressionBlock(stream);
			if(stream.getCurrentToken().getType() != TokenEnum.SEMI_COLON)
				stream.getNextToken();
		}
		else
		{
			stream.throwError(getBlockString(), "++ OR =", t.getType().name());
		}


		return assertCurrentToken(TokenEnum.SEMI_COLON);
	}

	@Override
	public void execute(Scope scope)
	{
		VariableInstance variableInstance = scope.getVariableFromName(varName);

		if(variableInstance == null)
		{
			scope.throwError("VariableDoesNotExistError", "");
			return;
		}

		if(expressionType == ExpressionType.EQUALS)
		{
			variableInstance.setValue(expression.getValue());
			expression.execute(scope);
		}
		else if(expressionType == ExpressionType.PLUS_PLUS)
		{
			variableInstance.setValue((int) variableInstance.value + 1);
		}
		else
		{
			//TODO: Handle ++, --, +=, -=
		}
	}

	@Override
	public String getBlockString()
	{
		return "Assignment";
	}
}
