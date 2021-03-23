package dev.theturkey.discordscript.tokenizer;

import dev.theturkey.discordscript.TokenStream;
import dev.theturkey.discordscript.program.OutputWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Tokenizer
{
	private RandomAccessFile stream;

	public Tokenizer(File f) throws FileNotFoundException
	{
		this.stream = new RandomAccessFile(f, "r");
	}

	public TokenStream genTokenStream(OutputWrapper outputWrapper) throws IOException
	{
		TokenStream tokenStream = new TokenStream(stream, outputWrapper);

		while(stream.getFilePointer() < stream.length())
		{
			long pos = stream.getFilePointer();
			int currentChar = stream.read();

			if(currentChar == -1)
				break;

			if(isNumber(currentChar))
			{
				int length = 0;
				while(isNumber(currentChar))
				{
					length++;
					currentChar = stream.read();
					if(currentChar == -1)
						break;
				}
				goBackOne();
				tokenStream.addToken(new Token(TokenEnum.NUMBER, pos, length));
			}
			else
			{
				TokenEnum tokenEnum;
				StringBuilder tokenStr = new StringBuilder();
				if(isChar(currentChar))
				{
					while(isChar(currentChar) || isNumber(currentChar))
					{
						tokenStr.append((char) currentChar);
						currentChar = stream.read();
						if(currentChar == -1)
							break;
					}
					goBackOne();
				}
				else
				{
					tokenStr.append((char) currentChar);
					int read = stream.read();
					if(read != -1)
						tokenStr.append((char) read);
					if(TokenEnum.getTokenFromCodeStr(tokenStr.toString()) == null)
					{
						tokenStr.deleteCharAt(tokenStr.length() - 1);
						goBackOne();
					}
				}
				tokenEnum = TokenEnum.getTokenFromCodeStr(tokenStr.toString());
				if(tokenEnum == null)
					tokenEnum = TokenEnum.PLAIN_STRING;
				tokenStream.addToken(new Token(tokenEnum, pos, tokenStr.length()));
			}
		}

		return tokenStream;
	}

	public void goBackOne() throws IOException
	{
		this.stream.seek(this.stream.getFilePointer() - 1);
	}

	public boolean isWhiteSpace(int character)
	{
		return Character.isWhitespace(character);
	}

	public boolean isChar(int character)
	{
		return (character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z');
	}

	public boolean isNumber(int character)
	{
		return character >= '0' && character <= '9';
	}
}
