package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.program.variables.VariableInstance;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExpressionBlock extends CodeBlock
{
	private static final List<TokenEnum> ENDING_TOKENS = Arrays.asList(TokenEnum.SEMI_COLON, TokenEnum.COMMA, TokenEnum.RIGHT_PARENTHESIS, TokenEnum.RIGHT_SQUARE_BRACE);
	private List<Object> toExecuteList;

	private Object value;

	public ExpressionBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		toExecuteList = new ArrayList<>();
		Token t = stream.getNextToken();
		while(!ENDING_TOKENS.contains(t.getType()))
		{
			if(t.getType() == TokenEnum.LEFT_PARENTHESIS)
			{
				ExpressionBlock expression = new ExpressionBlock(stream);
				Token currentToken = stream.getCurrentToken();

				if(currentToken.getType() == TokenEnum.COMMA)
					toExecuteList.add(new TupleBlock(expression, stream));
				else
					toExecuteList.add(expression);

				if(!assertCurrentToken(TokenEnum.RIGHT_PARENTHESIS))
					return false;
			}
			else if(t.getType() == TokenEnum.LEFT_SQUARE_BRACE)
			{
				toExecuteList.add(new ExpressionBlock(stream));
				Token nextUp = stream.getCurrentToken();
				if(nextUp.getType() == TokenEnum.COMMA)
				{
					ArrayWrapper arrayWrapper = new ArrayWrapper();
					arrayWrapper.expressions.add((ExpressionBlock) toExecuteList.remove(toExecuteList.size() - 1));
					while(stream.getCurrentToken().getType() == TokenEnum.COMMA)
						arrayWrapper.expressions.add(new ExpressionBlock(stream));
					toExecuteList.add(arrayWrapper);
				}

				if(!assertCurrentToken(TokenEnum.RIGHT_SQUARE_BRACE))
					return false;
			}
			else if(t.getType() == TokenEnum.QUOTE)
			{
				StringBuilder s = new StringBuilder();
				while(stream.getNextToken().getType() != TokenEnum.QUOTE)
					s.append(stream.getTokenStr());
				toExecuteList.add(s.toString());
			}
			else if(t.getType() == TokenEnum.LITERAL_QUOTE)
			{
				StringBuilder s = new StringBuilder();
				while(stream.getNextToken().getType() != TokenEnum.LITERAL_QUOTE)
					s.append(stream.getTokenStr());
				LiteralStringWrapper literalString = new LiteralStringWrapper();
				literalString.contents = s.toString();
				toExecuteList.add(literalString);
			}
			else if(t.getType() == TokenEnum.NUMBER)
			{
				int num = Integer.parseInt(stream.getTokenStr());
				while(stream.peekNextRealToken().getType() == TokenEnum.NUMBER)
				{
					num = (num * 10) + Integer.parseInt(stream.getTokenStr());
					stream.getNextRealToken();
				}
				Object prev = toExecuteList.size() > 0 ? toExecuteList.get(toExecuteList.size() - 1) : null;
				if(prev instanceof Token && ((Token) prev).getType() == TokenEnum.DECIMAL)
				{
					Object prevPrev = toExecuteList.get(toExecuteList.size() - 2);
					if(prevPrev instanceof Integer)
					{
						int wholeNumb = (int) prevPrev;
						toExecuteList.remove(toExecuteList.size() - 1);
						toExecuteList.remove(toExecuteList.size() - 1);
						toExecuteList.add(Float.parseFloat(wholeNumb + "." + num));
					}
					else
					{
						stream.throwError(this.getBlockString(), "Integer", prevPrev.toString());
					}
				}
				else
				{
					toExecuteList.add(num);
				}
			}
			else if(t.getType() == TokenEnum.PLAIN_STRING && stream.peekNextRealToken().getType() == TokenEnum.LEFT_PARENTHESIS)
			{
				toExecuteList.add(new FunctionCallBlock(stream));
			}
			else if(t.getType() == TokenEnum.PLAIN_STRING)
			{
				VarWrapper varWrapper = new VarWrapper();
				varWrapper.varName = stream.getTokenStr();
				if(stream.peekNextRealToken().getType() == TokenEnum.LEFT_SQUARE_BRACE)
				{

				}
				toExecuteList.add(varWrapper);
			}
			else if(!t.getType().isWhiteSpace())
			{
				toExecuteList.add(t);
			}

			t = stream.getNextToken();
		}
		return true;
	}

	@Override
	public void execute(Scope scope)
	{
		if(scope.isErrorred())
			return;
		//Order Of Operations

		//1. NestedExpressions
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof ExpressionBlock))
				continue;
			ExpressionBlock expr = (ExpressionBlock) o;
			expr.execute(scope);
			toExecuteList.set(i, expr.value);
		}

		//2. ++ and --
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof Token))
				continue;
			Token t = ((Token) o);
			if(t.getType() == TokenEnum.PLUS_PLUS || t.getType() == TokenEnum.MINUS_MINUS)
			{
				Object varObj = toExecuteList.get(i - 1);
				if(!(varObj instanceof VarWrapper))
				{
					scope.throwError("OperationException", "");
					return;
				}

				//TODO: Other numbers
				VariableInstance var = scope.getVariableFromName(((VarWrapper) varObj).varName);
				if(var.value instanceof Integer)
				{
					scope.throwError("OperationException", "");
					return;
				}
				var.setValue((int) var.value + (t.getType() == TokenEnum.PLUS_PLUS ? 1 : -1));
				toExecuteList.remove(i);
				i--;
			}
		}

		//3. !
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof Token))
				continue;
			Token t = ((Token) o);
			if(t.getType() == TokenEnum.NOT)
			{
				boolean toNot = getBooleanValueForObject(scope, toExecuteList.get(i + 1));
				toExecuteList.remove(i + 1);
				toExecuteList.set(i, !toNot);
			}
		}

		//4. *, /, %
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof Token))
				continue;
			Token t = ((Token) o);
			if(t.getType() == TokenEnum.MULTIPLY || t.getType() == TokenEnum.DIVIDE || t.getType() == TokenEnum.MODULUS)
			{
				Number left = getNumberValueForObj(scope, toExecuteList.get(i - 1));
				Number right = getNumberValueForObj(scope, toExecuteList.get(i + 1));
				toExecuteList.remove(i);
				toExecuteList.remove(i);

				toExecuteList.set(i - 1, t.getType() == TokenEnum.MULTIPLY ? multNumbers(left, right) : (t.getType() == TokenEnum.DIVIDE ? divNumbers(left, right) : modNumbers(left, right)));
				i--;
			}
		}

		//4. +, -, string concat
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof Token))
				continue;
			Token t = ((Token) o);
			if(t.getType() == TokenEnum.PLUS || t.getType() == TokenEnum.MINUS)
			{
				Object leftObj = toExecuteList.get(i - 1);
				Object rightObj = toExecuteList.get(i + 1);
				toExecuteList.remove(i);
				toExecuteList.remove(i);
				if(leftObj instanceof String || rightObj instanceof String)
				{
					if(leftObj instanceof VarWrapper)
						leftObj = scope.getVariableFromName(((VarWrapper)leftObj).varName).value;
					if(rightObj instanceof VarWrapper)
						rightObj = scope.getVariableFromName(((VarWrapper)rightObj).varName).value;
					toExecuteList.set(i - 1, String.valueOf(leftObj) + rightObj);
				}
				else
				{
					Number left = getNumberValueForObj(scope, leftObj);
					Number right = getNumberValueForObj(scope, rightObj);
					toExecuteList.set(i - 1, t.getType() == TokenEnum.PLUS ? addNumbers(left, right) : subNumbers(left, right));
				}
				i--;
			}
		}

		//5. <, <=, >, >=
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof Token))
				continue;
			Token t = ((Token) o);
			if(t.getType() == TokenEnum.LESS_THAN || t.getType() == TokenEnum.LESS_THAN_OR_EQUAL || t.getType() == TokenEnum.GREATER_THAN || t.getType() == TokenEnum.GREATER_THAN_OR_EQUAL)
			{
				Number left = getNumberValueForObj(scope, toExecuteList.get(i - 1));
				Number right = getNumberValueForObj(scope, toExecuteList.get(i + 1));
				toExecuteList.remove(i);
				toExecuteList.remove(i);
				toExecuteList.set(i - 1, compareNumbers(left, right, t.getType()));
				i--;
			}
		}

		//6. ==, !=
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof Token))
				continue;
			Token t = ((Token) o);
			if(t.getType() == TokenEnum.EQUALITY || t.getType() == TokenEnum.NOT_EQUALS)
			{
				Number left = getNumberValueForObj(scope, toExecuteList.get(i - 1));
				Number right = getNumberValueForObj(scope, toExecuteList.get(i + 1));
				toExecuteList.remove(i);
				toExecuteList.remove(i);
				toExecuteList.set(i - 1, compareNumbers(left, right, t.getType()));
				i--;
			}
		}

		//6. &&
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof Token))
				continue;
			Token t = ((Token) o);
			if(t.getType() == TokenEnum.AND)
			{
				boolean left = getBooleanValueForObject(scope, toExecuteList.get(i - 1));
				boolean right = getBooleanValueForObject(scope, toExecuteList.get(i + 1));
				toExecuteList.remove(i);
				toExecuteList.remove(i);
				toExecuteList.set(i - 1, left && right);
				i--;
			}
		}

		//7. ||
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(!(o instanceof Token))
				continue;
			Token t = ((Token) o);
			if(t.getType() == TokenEnum.AND)
			{
				boolean left = getBooleanValueForObject(scope, toExecuteList.get(i - 1));
				boolean right = getBooleanValueForObject(scope, toExecuteList.get(i + 1));
				toExecuteList.remove(i);
				toExecuteList.remove(i);
				toExecuteList.set(i - 1, left || right);
				i--;
			}
		}

		if(toExecuteList.size() != 1)
		{
			StringBuilder builder = new StringBuilder("Execution didn't come to a singular value!\nLeft:\n\t");
			for(Object o : toExecuteList)
				builder.append(o.toString()).append(", ");
			scope.throwError("Error", builder.toString());
			return;
		}

		Object lastVal = toExecuteList.get(0);
		if(lastVal instanceof ExpressionBlock)
		{
			((ExpressionBlock) lastVal).execute(scope);
			value = ((ExpressionBlock) lastVal).getValue();
		}
		else if(lastVal instanceof FunctionCallBlock)
		{
			value = ((FunctionCallBlock) lastVal).executeAndGetReturn(scope);
		}
		else if(lastVal instanceof VarWrapper)
		{
			String varName = ((VarWrapper) lastVal).varName;
			VariableInstance var = scope.getVariableFromName(varName);
			if(var == null)
			{
				scope.throwError("VariableNotDefinedException", varName + " is not defined!");
				return;
			}
			value = var.value;
		}
		else if(lastVal instanceof ArrayWrapper)
		{
			ArrayWrapper array = ((ArrayWrapper) lastVal);
			List<Object> values = new ArrayList<>();
			for(ExpressionBlock e : array.expressions)
			{
				e.execute(scope);
				values.add(e.value);
			}
			value = values;
		}
		else if(lastVal instanceof LiteralStringWrapper)
		{
			LiteralStringWrapper literalString = ((LiteralStringWrapper) lastVal);
			int indexOf = literalString.contents.indexOf("${");
			while(indexOf != -1)
			{
				String toReplace = literalString.contents.substring(indexOf + 2, literalString.contents.indexOf("}", indexOf));
				//TODO:
			}
			value = literalString.contents;
		}
		else
		{
			value = lastVal;
		}
	}

	private Number getNumberValueForObj(Scope scope, Object object)
	{
		if(object instanceof VarWrapper)
		{
			return scope.getVariableFromName(((VarWrapper) object).varName).getAsNumber();
		}
		else if(object instanceof Number)
		{
			return (Number) object;
		}
		else if(object instanceof FunctionCallBlock)
		{
			FunctionCallBlock func = ((FunctionCallBlock) object);
			if(!func.getFunctionReturnType(scope).isNumber())
				scope.throwError("CastException", "");

			return (Number) func.executeAndGetReturn(scope);
		}
		else
		{
			return null;
		}
	}

	public boolean getBooleanValueForObject(Scope scope, Object object)
	{
		if(object instanceof VarWrapper)
		{
			return objToBoolean(scope.getVariableFromName(((VarWrapper) object).varName).value);
		}
		else if(object instanceof Number)
		{
			return objToBoolean(object);
		}
		else if(object instanceof FunctionCallBlock)
		{
			FunctionCallBlock func = ((FunctionCallBlock) object);
			if(!func.getFunctionReturnType(scope).isNumber())
				scope.throwError("CastException", "");

			return objToBoolean(func.executeAndGetReturn(scope));
		}
		else return object instanceof Token && ((Token) object).getType() == TokenEnum.TRUE;
	}

	private boolean objToBoolean(Object obj)
	{
		return obj.equals(true) || obj.equals(1);
	}

	public static Number addNumbers(Number a, Number b)
	{
		if(a instanceof Double || b instanceof Double)
			return a.doubleValue() + b.doubleValue();
		else if(a instanceof Float || b instanceof Float)
			return a.floatValue() + b.floatValue();
		else if(a instanceof Long || b instanceof Long)
			return a.longValue() + b.longValue();
		else
			return a.intValue() + b.intValue();
	}

	public static Number subNumbers(Number a, Number b)
	{
		if(a instanceof Double || b instanceof Double)
			return a.doubleValue() - b.doubleValue();
		else if(a instanceof Float || b instanceof Float)
			return a.floatValue() - b.floatValue();
		else if(a instanceof Long || b instanceof Long)
			return a.longValue() - b.longValue();
		else
			return a.intValue() - b.intValue();
	}

	public static Number multNumbers(Number a, Number b)
	{
		if(a instanceof Double || b instanceof Double)
			return a.doubleValue() * b.doubleValue();
		else if(a instanceof Float || b instanceof Float)
			return a.floatValue() * b.floatValue();
		else if(a instanceof Long || b instanceof Long)
			return a.longValue() * b.longValue();
		else
			return a.intValue() * b.intValue();
	}

	public static Number divNumbers(Number a, Number b)
	{
		if(a instanceof Double || b instanceof Double)
			return a.doubleValue() / b.doubleValue();
		else if(a instanceof Float || b instanceof Float)
			return a.floatValue() / b.floatValue();
		else if(a instanceof Long || b instanceof Long)
			return a.longValue() / b.longValue();
		else
			return a.intValue() / b.intValue();
	}

	public static Number modNumbers(Number a, Number b)
	{
		if(a instanceof Double || b instanceof Double)
			return a.doubleValue() % b.doubleValue();
		else if(a instanceof Float || b instanceof Float)
			return a.floatValue() % b.floatValue();
		else if(a instanceof Long || b instanceof Long)
			return a.longValue() % b.longValue();
		else
			return a.intValue() % b.intValue();
	}

	private boolean compareNumbers(Number n1, Number n2, TokenEnum tokenEnum)
	{
		float diff = n1.floatValue() - n2.floatValue();
		if(tokenEnum == TokenEnum.LESS_THAN)
			return diff < 0;
		if(tokenEnum == TokenEnum.LESS_THAN_OR_EQUAL)
			return diff <= 0;
		if(tokenEnum == TokenEnum.GREATER_THAN)
			return diff > 0;
		if(tokenEnum == TokenEnum.GREATER_THAN_OR_EQUAL)
			return diff >= 0;
		if(tokenEnum == TokenEnum.EQUALITY)
			return diff == 0;
		if(tokenEnum == TokenEnum.NOT_EQUALS)
			return diff != 0;
		return false;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public String getBlockString()
	{
		return "Expression";
	}

	private static class VarWrapper
	{
		public String varName;
	}

	private static class ArrayWrapper
	{
		public List<ExpressionBlock> expressions = new ArrayList<>();
	}

	private static class LiteralStringWrapper
	{
		public String contents;
	}
}
