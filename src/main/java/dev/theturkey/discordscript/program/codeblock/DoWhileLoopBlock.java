package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class DoWhileLoopBlock extends CodeBlock
{
	private ConditionBlock conditionBlock;
	private List<CodeBlock> internalCodeBlocks = new ArrayList<>();

	public DoWhileLoopBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.DO))

			return false;

		//TODO: ignore whitespace and new lines

		if(!assertNextToken(TokenEnum.LEFT_CURLY_BRACE))
			return false;

		internalCodeBlocks = parseInternalBlock(stream);

		if(!assertCurrentToken(TokenEnum.RIGHT_CURLY_BRACE))
			return false;

		if(!assertNextToken(TokenEnum.WHILE))
			return false;

		if(!assertNextToken(TokenEnum.LEFT_PARENTHESIS))
			return false;

		conditionBlock = new ConditionBlock(stream);

		if(!assertNextToken(TokenEnum.RIGHT_PARENTHESIS))
			return false;

		return !stream.hasErrored() && assertNextToken(TokenEnum.SEMI_COLON);
	}

	@Override
	public void execute(Scope scope)
	{
		Scope innerScope = new Scope(scope);
		do
		{
			for(CodeBlock cb : internalCodeBlocks)
			{
				cb.execute(innerScope);
				if(innerScope.isBreaked() || innerScope.isReturned() || innerScope.isContinued())
					break;
			}

			if(innerScope.isReturned())
				scope.setReturned(innerScope.getReturnVal());
		} while(conditionBlock.getValue(scope) && !innerScope.isBreaked() && !innerScope.isReturned());
	}

	@Override
	public String getBlockString()
	{
		return "Do While Loop";
	}
}
