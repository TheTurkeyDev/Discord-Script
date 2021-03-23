package dev.theturkey.discordscript;

import dev.theturkey.discordscript.program.OutputWrapper;
import dev.theturkey.discordscript.program.codeblock.CodeBlock;
import dev.theturkey.discordscript.tokenizer.Token;
import dev.theturkey.discordscript.tokenizer.TokenEnum;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class TokenStream
{
	private OutputWrapper outputWrapper;

	private RandomAccessFile stream;
	private List<Token> tokens = new ArrayList<>();
	private int currentIndex = -1;

	private boolean errored = false;

	public TokenStream(RandomAccessFile stream, OutputWrapper outputWrapper)
	{
		this.outputWrapper = outputWrapper;
		this.stream = stream;
	}

	public void addToken(Token token)
	{
		tokens.add(token);
	}

	public Token getCurrentToken()
	{
		return tokens.get(currentIndex);
	}

	public Token getNextToken()
	{
		currentIndex++;
		if(currentIndex >= tokens.size())
			return null;
		return tokens.get(currentIndex);
	}

	public String getTokenStr()
	{
		try
		{
			Token token = tokens.get(currentIndex);
			stream.seek(token.getPos());
			if(currentIndex + 1 == tokens.size())
				return stream.readLine();
			byte[] bytes = new byte[(int) token.getLength()];
			stream.read(bytes, 0, bytes.length);
			return new String(bytes);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public int getTokenInt()
	{
		try
		{
			Token token = tokens.get(currentIndex);
			stream.seek(token.getPos());
			return stream.readInt();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public Token peekNextRealToken()
	{
		return peekNextRealToken(0);
	}

	public Token peekNextRealToken(int skip)
	{
		Token nextToken = null;
		int tempIndex = currentIndex;
		for(int i = 0; i <= skip; i++)
		{
			do
			{
				tempIndex++;
				if(tempIndex >= tokens.size())
					return null;
				nextToken = tokens.get(tempIndex);
			} while(nextToken.getType().isWhiteSpace());
		}

		return nextToken;
	}

	public Token getNextRealToken()
	{
		Token nextToken;
		while((nextToken = getNextToken()).getType().isWhiteSpace()) ;

		return nextToken;
	}

	public boolean assertNextToken(CodeBlock currentBlock, TokenEnum token, boolean ignoreSpaces)
	{
		TokenEnum nextToken = getNextToken().getType();

		if(ignoreSpaces && nextToken.isWhiteSpace())
			while((nextToken = getNextToken().getType()).isWhiteSpace()) ;

		if(nextToken != token)
		{
			throwError(currentBlock.getBlockString(), token.name(), nextToken == TokenEnum.PLAIN_STRING ? getTokenStr() : nextToken.name());
			return false;
		}
		return true;
	}

	public boolean assertCurrentToken(CodeBlock currentBlock, TokenEnum token)
	{
		TokenEnum nextToken = getCurrentToken().getType();
		if(nextToken != token)
		{
			throwError(currentBlock.getBlockString(), token.name(), nextToken.name());
			return false;
		}
		return true;
	}

	public void throwError(String blockStr, String expected, String got)
	{
		errored = true;
		int line = 1;
		int pos = 0;
		long errorIndex = tokens.get(currentIndex).getPos();
		StringBuilder errorSnippet = new StringBuilder();
		try
		{
			long currentPos = stream.getFilePointer();
			stream.seek(0);
			int indexPos = 0;
			while(indexPos < errorIndex)
			{
				pos++;
				if(this.stream.read() == '\n')
				{
					pos = 0;

					line++;
				}
				indexPos++;
			}
			stream.seek(errorIndex - 15);
			boolean foundNonWhiteSpace = false;
			for(int i = 0; i < 30; i++)
			{
				int c = stream.read();

				if(c == '\n' && i < 15)
				{
					errorSnippet = new StringBuilder();
					continue;
				}

				if(c == '\r')
					continue;
				else if(c == '\n')
					break;
				else if(!foundNonWhiteSpace && Character.isWhitespace(c))
					continue;
				foundNonWhiteSpace = true;
				errorSnippet.append((char) c);
			}
			stream.seek(currentPos);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		outputWrapper.writeLine("```\nERROR on line " + line + ":" + pos);
		outputWrapper.writeLine("\t> " + errorSnippet.toString());
		outputWrapper.writeLine("\t" + blockStr + " expected '" + expected + "', but got '" + got + "' instead!```");
	}

	public boolean hasErrored()
	{
		return errored;
	}
}
