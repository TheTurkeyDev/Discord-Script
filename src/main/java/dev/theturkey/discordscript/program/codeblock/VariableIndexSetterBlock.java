package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

public class VariableIndexSetterBlock extends CodeBlock
{
	public VariableIndexSetterBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertNextToken(TokenEnum.LEFT_SQUARE_BRACE))
			return false;

		ExpressionBlock expressionBlock = new ExpressionBlock(stream);

		if(!assertCurrentToken(TokenEnum.RIGHT_SQUARE_BRACE))
			return false;

		Token t = stream.peekNextRealToken();
		if(t.getType() == TokenEnum.LEFT_SQUARE_BRACE)
		{
			VariableIndexSetterBlock variableIndexSetterBlock = new VariableIndexSetterBlock(stream);
		}
		else if(t.getType() == TokenEnum.EQUALS || t.getType() == TokenEnum.PLUS_EQUALS || t.getType() == TokenEnum.MINUS_EQUALS)
		{
			stream.getNextRealToken();
			ExpressionBlock expressionBlock2 = new ExpressionBlock(stream);
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
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Variable Index Setter";
	}
}
