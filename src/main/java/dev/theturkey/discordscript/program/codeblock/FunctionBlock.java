package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.program.variables.VariableType;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public class FunctionBlock extends CodeBlock
{
	protected String name;
	private VariableType returnType;
	private List<CodeBlock> internalCodeBlocks;
	private List<VariableBlock> arguments;

	protected Object[] passedArgs;

	protected Object returnVal;

	public FunctionBlock(TokenStream wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean parse(TokenStream stream)
	{
		this.returnType = VariableType.getVariableType(stream);

		if(!assertNextToken(TokenEnum.PLAIN_STRING))
			return false;

		this.name = stream.getTokenStr();

		if(!assertNextToken(TokenEnum.LEFT_PARENTHESIS))
			return false;

		arguments = new ArrayList<>();
		stream.getNextRealToken();
		while(stream.getCurrentToken().getType() != TokenEnum.RIGHT_PARENTHESIS)
		{
			arguments.add(new VariableBlock(stream));
			if(stream.getCurrentToken().getType() == TokenEnum.COMMA)
				stream.getNextRealToken();
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

		for(int i = 0; i < arguments.size(); i++)
		{
			VariableBlock vb = arguments.get(i);
			vb.execute(scope);
			scope.getVariableFromName(vb.getVarName()).setValue(passedArgs[i]);
		}

		for(CodeBlock cb : internalCodeBlocks)
		{
			cb.execute(innerScope);
			if(innerScope.isReturned())
				break;
		}

		this.returnVal = innerScope.getReturnVal();
	}

	public void execute(Scope scope, Object[] passedArgs)
	{
		this.passedArgs = passedArgs;
		this.execute(scope);
	}

	public Object getReturnVal()
	{
		return returnVal;
	}

	public String getName()
	{
		return name;
	}

	public VariableType getReturnType()
	{
		return returnType;
	}

	@Override
	public String getBlockString()
	{
		return "Function";
	}
}
