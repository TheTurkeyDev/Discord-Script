package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.program.VariableTypeWrapper;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.List;

public class FunctionBlock extends CodeBlock
{
	List<CodeBlock> internalCodeBlocks;

	public FunctionBlock(TokenStream wrapper, VariableTypeWrapper returnType)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		if(!assertNextToken(TokenEnum.PLAIN_STRING))
			return false;

		if(!assertNextToken(TokenEnum.LEFT_PARENTHESIS))
			return false;

		//TODO: Move to arguments block
		int misMatch = 0;
		while(stream.getNextRealToken().getType() != TokenEnum.RIGHT_PARENTHESIS || misMatch > 0)
		{
			if(stream.getCurrentToken().getType() == TokenEnum.LEFT_PARENTHESIS)
				misMatch++;
			else if(stream.getCurrentToken().getType() == TokenEnum.RIGHT_PARENTHESIS)
				misMatch--;
		}


		if(!assertCurrentToken(TokenEnum.RIGHT_PARENTHESIS))
			return false;
		if(!assertNextToken(TokenEnum.LEFT_CURLY_BRACE))
			return false;


		internalCodeBlocks = parseInternalBlock(stream);

		return !stream.hasErrored() && assertCurrentToken(TokenEnum.RIGHT_CURLY_BRACE);
	}

	@Override
	public void execute(OutputWrapper out)
	{

	}

	@Override
	public String getBlockString()
	{
		return "Function";
	}
}
