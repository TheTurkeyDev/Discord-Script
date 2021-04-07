package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class WhileLoopBlock extends CodeBlock
{
	private ConditionBlock conditionBlock;
	private List<CodeBlock> internalCodeBlocks;

	public WhileLoopBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.WHILE))

			return false;

		if(!assertNextToken(TokenEnum.LEFT_PARENTHESIS))
			return false;

		conditionBlock = new ConditionBlock(stream);

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
		return "While Loop";
	}
}
