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

	public ExpressionBlock(List<Object> toExecuteList)
	{
		super();
		this.toExecuteList = toExecuteList;
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		toExecuteList = new ArrayList<>();
		Token t = stream.getNextToken();
		while(!ENDING_TOKENS.contains(t.getType()) && !t.getType().isConditional())
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
				if(!assertCurrentToken(TokenEnum.RIGHT_SQUARE_BRACE))
					return false;
			}
			else if(t.getType() == TokenEnum.QUOTE)
			{
				StringBuilder s = new StringBuilder();
				while(stream.peekNextRealToken().getType() != TokenEnum.QUOTE)
				{
					s.append(stream.getTokenStr());
					stream.getNextRealToken();
				}
				stream.getNextRealToken();
				toExecuteList.add(s.toString());
			}
			else if(t.getType() == TokenEnum.LITERAL_QUOTE)
			{
				StringBuilder s = new StringBuilder();
				while(stream.peekNextRealToken().getType() != TokenEnum.LITERAL_QUOTE)
				{
					s.append(stream.getTokenStr());
					stream.getNextRealToken();
				}
				stream.getNextRealToken();
				toExecuteList.add(s.toString());
			}
			else if(t.getType() == TokenEnum.NUMBER)
			{
				int num = Integer.parseInt(stream.getTokenStr());
				while(stream.peekNextRealToken().getType() == TokenEnum.NUMBER)
				{
					num = (num * 10) + Integer.parseInt(stream.getTokenStr());
					stream.getNextRealToken();
				}
				toExecuteList.add(num);
			}
			else if(t.getType() == TokenEnum.PLAIN_STRING && stream.peekNextRealToken().getType() == TokenEnum.LEFT_PARENTHESIS)
			{
				toExecuteList.add(new FunctionCallBlock(stream));
			}
			else if(t.getType() == TokenEnum.PLAIN_STRING)
			{
				VarWrapper varWrapper = new VarWrapper();
				varWrapper.varName = stream.getTokenStr();
				toExecuteList.add(varWrapper);
			}

			t = stream.getNextToken();
		}
		return true;
	}

	@Override
	public void execute(Scope scope)
	{
		for(int i = 0; i < toExecuteList.size(); i++)
		{
			Object o = toExecuteList.get(i);
			if(o instanceof ExpressionBlock)
			{
				ExpressionBlock exp = ((ExpressionBlock) o);
				exp.execute(scope);
				value = exp.getValue();
			}
			else if(o instanceof Integer)
			{
				this.value = o;
			}
			else if(o instanceof VarWrapper)
			{
				VariableInstance var = scope.getVariableFromName(((VarWrapper) o).varName);
				if(var == null)
				{
					scope.throwError("VariableNotDefinedError", "");
					return;
				}
				this.value = var.value;
			}
		}
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
}
