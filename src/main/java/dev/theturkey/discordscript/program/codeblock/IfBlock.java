package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class IfBlock extends CodeBlock
{
	private ConditionBlock conditionBlock;
	private List<CodeBlock> internalCodeBlocks = new ArrayList<>();
	private ElseBlock elseBlock = null;

	public IfBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertCurrentToken(TokenEnum.IF))

			return false;

		if(!assertNextToken(TokenEnum.LEFT_PARENTHESIS))
			return false;

		conditionBlock = new ConditionBlock(stream);

		if(!assertCurrentToken(TokenEnum.RIGHT_PARENTHESIS))
			return false;

		Token t = stream.getNextRealToken();

		if(t.getType() == TokenEnum.LEFT_CURLY_BRACE)
		{
			internalCodeBlocks = parseInternalBlock(stream);
			if(!assertCurrentToken(TokenEnum.RIGHT_CURLY_BRACE))
				return false;
		}
		else
		{
			internalCodeBlocks = new ArrayList<>();
			internalCodeBlocks.add(parseCode(stream.getCurrentToken(), stream));
		}

		if(stream.peekNextRealToken().getType() == TokenEnum.ELSE)
			elseBlock = new ElseBlock(stream);

		return !stream.hasErrored();
	}

	@Override
	public void execute(Scope scope)
	{
		Scope innerScope = new Scope(scope);
		if(conditionBlock.getValue(innerScope) && !innerScope.isBreaked() && !innerScope.isReturned() && !innerScope.isContinued())
		{
			for(CodeBlock cb : internalCodeBlocks)
			{
				cb.execute(innerScope);
				if(innerScope.isBreaked() || innerScope.isReturned() || innerScope.isContinued())
					break;
			}
		}
		else
		{
			elseBlock.execute(scope);
		}

		if(innerScope.isReturned())
			scope.setReturned(innerScope.getReturnVal());
		if(innerScope.isContinued())
			scope.setContinued();
	}

	@Override
	public String getBlockString()
	{
		return "If Statement";
	}
}
