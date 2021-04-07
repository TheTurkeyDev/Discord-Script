package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class ConditionBlock extends CodeBlock
{
	private TokenEnum conditionToken;
	private ExpressionBlock leftExpr;
	private ExpressionBlock rightExpr;
	private boolean value = true;
	
	public ConditionBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		Token t = stream.getCurrentToken();
		if(t.getType() == TokenEnum.NOT)
		{
			conditionToken = stream.getCurrentToken().getType();
			leftExpr = new ExpressionBlock(stream);
		}
		else
		{
			leftExpr = new ExpressionBlock(stream);
			conditionToken = stream.getCurrentToken().getType();
			if(stream.getCurrentToken().getType() != TokenEnum.RIGHT_PARENTHESIS)
				rightExpr = new ExpressionBlock(stream);
			else
				rightExpr = null;
		}
		return true;
	}

	@Override
	public void execute(Scope scope)
	{
		if(conditionToken == TokenEnum.NOT)
		{
			leftExpr.execute(scope);
			this.value = !getAsBoolean(leftExpr.getValue());
		}
		else if(rightExpr == null)
		{
			leftExpr.execute(scope);
			this.value = getAsBoolean(leftExpr.getValue());
		}
		else
		{
			leftExpr.execute(scope);
			rightExpr.execute(scope);
			Object left = leftExpr.getValue();
			Object right = rightExpr.getValue();
			switch(conditionToken)
			{
				case NOT_EQUALS:
					value = left != right;
					break;
				case EQUALITY:
					value = left == right;
					break;
				case LESS_THAN:
					if((!isInteger(left) && isDecimal(left)) || (!isInteger(right) && isDecimal(right)))
						scope.throwError("NumberParseException", "");

					this.value = (int) left < (int) right;
					break;
				case LESS_THAN_OR_EQUAL:
					if((!isInteger(left) && isDecimal(left)) || (!isInteger(right) && isDecimal(right)))
						scope.throwError("NumberParseException", "");

					this.value = (int) left <= (int) right;
					break;
				case GREATER_THAN:
					if((!isInteger(left) && isDecimal(left)) || (!isInteger(right) && isDecimal(right)))
						scope.throwError("NumberParseException", "");

					this.value = (int) left > (int) right;
					break;
				case GREATER_THAN_OR_EQUAL:
					if((!isInteger(left) && isDecimal(left)) || (!isInteger(right) && isDecimal(right)))
						scope.throwError("NumberParseException", "");

					this.value = (int) left >= (int) right;
					break;
				case OR:
					this.value = getAsBoolean(left) || getAsBoolean(right);
					break;
				case AND:
					this.value = getAsBoolean(left) && getAsBoolean(right);
					break;
			}
		}
	}

	public boolean getValue(Scope scope)
	{
		execute(scope);
		return value;
	}

	public boolean getAsBoolean(Object o)
	{
		return o.equals(1) || o.equals(true);
	}

	public boolean isInteger(Object o)
	{
		return o instanceof Integer;
	}

	public boolean isDecimal(Object o)
	{
		return o instanceof Double || o instanceof Float;
	}


	@Override
	public String getBlockString()
	{
		return "Expression";
	}
}
