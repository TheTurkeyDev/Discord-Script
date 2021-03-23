package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExpressionBlock extends CodeBlock
{
	private static final List<TokenEnum> ENDING_TOKENS = Arrays.asList(TokenEnum.SEMI_COLON, TokenEnum.COMMA, TokenEnum.RIGHT_PARENTHESIS, TokenEnum.RIGHT_SQUARE_BRACE);
	private List<Object> toExecuteList;

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
				if(!assertCurrentToken(TokenEnum.RIGHT_SQUARE_BRACE))
					return false;
			}
			else if(t.getType() == TokenEnum.PLAIN_STRING && stream.peekNextRealToken().getType() == TokenEnum.LEFT_PARENTHESIS)
			{
				toExecuteList.add(new FunctionCallBlock(stream));
			}
			else
			{
				toExecuteList.add(t);
			}

			t = stream.getNextToken();
		}
		return true;
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Expression";
	}
}
