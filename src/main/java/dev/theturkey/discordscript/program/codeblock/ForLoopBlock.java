package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class ForLoopBlock extends CodeBlock
{
	private VariableBlock variableBlock;
	private ConditionBlock conditionBlock;
	private ExpressionBlock expressionBlock;
	private List<CodeBlock> internalCodeBlocks = new ArrayList<>();

	public ForLoopBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.FOR))
			return false;

		if(!assertNextToken(TokenEnum.LEFT_PARENTHESIS))
			return false;

		stream.getNextRealToken();
		variableBlock = new VariableBlock(stream);

		Token t = stream.getCurrentToken();
		if(t.getType() == TokenEnum.COLON)
		{
			if(!assertNextToken(TokenEnum.PLAIN_STRING))
			return false;
			stream.getNextRealToken();
		}
		else
		{
			if(!assertCurrentToken(TokenEnum.SEMI_COLON))
				return false;

			stream.getNextRealToken();
			conditionBlock = new ConditionBlock(stream);

			if(!assertCurrentToken(TokenEnum.SEMI_COLON))
				return false;

			stream.getNextRealToken();
			expressionBlock = new ExpressionBlock(stream);
		}

		if(!assertCurrentToken(TokenEnum.RIGHT_PARENTHESIS))
			return false;


		if(!assertNextToken(TokenEnum.LEFT_CURLY_BRACE))
			return false;

		internalCodeBlocks = parseInternalBlock(stream);

		return !stream.hasErrored() && assertCurrentToken(TokenEnum.RIGHT_CURLY_BRACE);
	}

	@Override
	public void execute(Scope scope)
	{
		Scope innerScope = new Scope(scope);
		while(conditionBlock.getValue(scope) && !innerScope.isBreaked() && !innerScope.isReturned())
		{
			for(CodeBlock cb : internalCodeBlocks)
			{
				cb.execute(innerScope);
				if(innerScope.isBreaked() || innerScope.isReturned() || innerScope.isContinued())
					break;
			}

			if(innerScope.isReturned())
				scope.setReturned(innerScope.getReturnVal());
		}
	}

	@Override
	public String getBlockString()
	{
		return "For Loop";
	}
}
