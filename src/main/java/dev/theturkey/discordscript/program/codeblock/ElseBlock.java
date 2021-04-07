package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class ElseBlock extends CodeBlock
{
	ConditionBlock conditionBlock;
	private List<CodeBlock> internalCodeBlocks = new ArrayList<>();

	public ElseBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertNextToken(TokenEnum.ELSE))
			return false;

		//TODO: Add IF support here

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

		return !stream.hasErrored();
	}

	@Override
	public void execute(Scope scope)
	{
		Scope innerScope = new Scope(scope);
		for(CodeBlock cb : internalCodeBlocks)
		{
			cb.execute(innerScope);
			if(innerScope.isBreaked() || innerScope.isReturned() || innerScope.isContinued())
				break;
		}

		if(innerScope.isReturned())
			scope.setReturned(innerScope.getReturnVal());
		if(innerScope.isContinued())
			scope.setContinued();
	}

	@Override
	public String getBlockString()
	{
		return "Else Statement";
	}
}
