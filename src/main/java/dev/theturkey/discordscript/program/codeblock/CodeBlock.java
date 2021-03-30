package dev.theturkey.discordscript.program.codeblock;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.Scope;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.util.ArrayList;
import java.util.List;

public abstract class CodeBlock
{
	private TokenStream wrapper;

	public CodeBlock(TokenStream wrapper)
	{
		this.wrapper = wrapper;
		parse(wrapper);
	}

	protected boolean assertNextToken(TokenEnum expected)
	{
		return assertNextToken(expected, true);
	}

	protected boolean assertNextToken(TokenEnum expected, boolean ignoreSpaces)
	{
		return wrapper.assertNextToken(this, expected, ignoreSpaces);
	}

	protected boolean assertCurrentToken(TokenEnum expected)
	{
		return wrapper.assertCurrentToken(this, expected);
	}

	protected List<CodeBlock> parseInternalBlock(TokenStream stream)
	{
		List<CodeBlock> blocks = new ArrayList<>();
		Token token = stream.getNextToken();
		while(token.getType() != TokenEnum.RIGHT_CURLY_BRACE)
		{
			if(token.getType().isWhiteSpace())
			{
				token = stream.getNextToken();
				continue;
			}
			CodeBlock block = parseCode(token, stream);
			if(wrapper.hasErrored())
				return new ArrayList<>();
			if(block != null)
				blocks.add(block);
			token = stream.getNextToken();
		}
		return blocks;
	}

	protected CodeBlock parseCode(Token token, TokenStream stream)
	{
		if(token.getType().isVarType())
		{
			Token nnt = stream.peekNextRealToken(1);
			if(nnt.getType() == TokenEnum.LEFT_PARENTHESIS)
				return new FunctionBlock(stream);
			else
				return new VariableBlock(stream);
		}

		switch(token.getType())
		{
			case WHILE:
				return new WhileLoopBlock(stream);
			case DO:
				return new DoWhileLoopBlock(stream);
			case FOR:
				return new ForLoopBlock(stream);
			case IF:
				return new IfBlock(stream);
			case PLAIN_STRING:
				Token nextToken = stream.peekNextRealToken();
				if(nextToken.getType() == TokenEnum.LEFT_PARENTHESIS)
				{
					FunctionCallBlock functionCallBlock = new FunctionCallBlock(stream);
					assertNextToken(TokenEnum.SEMI_COLON);
					return functionCallBlock;
				}
				else if(nextToken.getType() == TokenEnum.LEFT_SQUARE_BRACE)
				{
					return new VariableIndexSetterBlock(stream);
				}
				else
				{
					return new AssignmentBlock(stream);
				}
			case COMMENT:
				return new CommentBlock(stream);
			case MULTI_COMMENT_START:
				return new MultiLineCommentBlock(stream);
			case LEFT_PARENTHESIS:
				if(stream.hasErrored())
					return null;

				Token nnt = stream.peekNextRealToken(1);
				if(nnt.getType() == TokenEnum.LEFT_PARENTHESIS)
					return new FunctionBlock(stream);
				else
					return new VariableBlock(stream);
			case RETURN:
				return new ReturnCodeBlock(stream);
			case CONTINUE:
				return new ContinueBlock(stream);
			default:
				stream.throwError(this.getBlockString(), "Valid token", token.getType().toString());
				return null;
		}
	}

	public abstract boolean parse(TokenStream stream);

	public abstract void execute(Scope scope);

	public abstract String getBlockString();
}
