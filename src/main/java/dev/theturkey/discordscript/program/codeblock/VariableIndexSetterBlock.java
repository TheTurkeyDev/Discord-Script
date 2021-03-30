package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.ExpressionType;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.program.variables.VariableInstance;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class VariableIndexSetterBlock extends CodeBlock
{
	private String varName;
	private ExpressionType expressionType;
	private ExpressionBlock indexExpr;
	private ExpressionBlock expressionBlock;

	public VariableIndexSetterBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		this.varName = stream.getTokenStr();

		if(!assertNextToken(TokenEnum.LEFT_SQUARE_BRACE))
			return false;

		indexExpr = new ExpressionBlock(stream);

		if(!assertCurrentToken(TokenEnum.RIGHT_SQUARE_BRACE))
			return false;

		Token t = stream.peekNextRealToken();
		//TODO: Handle N Dimension arrays
		expressionType = ExpressionType.getExpressionTypeFromToken(t.getType());
		if(t.getType() == TokenEnum.LEFT_SQUARE_BRACE)
		{
			VariableIndexSetterBlock variableIndexSetterBlock = new VariableIndexSetterBlock(stream);
		}
		else if(t.getType() == TokenEnum.EQUALS || t.getType() == TokenEnum.PLUS_EQUALS || t.getType() == TokenEnum.MINUS_EQUALS)
		{
			stream.getNextRealToken();
			expressionBlock = new ExpressionBlock(stream);
		}
		else if(t.getType() == TokenEnum.PLUS_PLUS || t.getType() == TokenEnum.MINUS_MINUS)
		{
			stream.getNextToken();
			stream.getNextToken();
		}
		else
		{
			stream.throwError(this.getBlockString(), "[ OR Assignment", t.getType().name());
		}

		return !stream.hasErrored() && assertCurrentToken(TokenEnum.SEMI_COLON);
	}

	@Override
	public void execute(Scope scope)
	{
		VariableInstance variable = scope.getVariableFromName(varName);
		if(variable == null)
		{
			scope.throwError("VariableDoesNotExistError", "");
			return;
		}
		indexExpr.execute(scope);
		Object indexObj = indexExpr.getValue();

		//TODO: Check indexObj is an integer


		if(expressionType == ExpressionType.EQUALS)
		{
			variable.setIndexValue((int) indexObj, expressionBlock.getValue());
		}
		else
		{
			//TODO: Handle ++, --, +=, -=
		}
	}

	@Override
	public String getBlockString()
	{
		return "Variable Index Setter";
	}
}
