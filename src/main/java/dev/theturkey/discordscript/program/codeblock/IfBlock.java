package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class IfBlock extends CodeBlock
{
	ConditionBlock conditionBlock;
	private List<CodeBlock> internalCodeBlocks = new ArrayList<>();

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

		return !stream.hasErrored();
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "If Statement";
	}
}
