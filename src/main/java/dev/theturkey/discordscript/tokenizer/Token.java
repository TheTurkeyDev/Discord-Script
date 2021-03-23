package dev.theturkey.discordscript.tokenizer;

public class Token
{
	private TokenEnum type;
	private long pos;
	private long length;

	public Token(TokenEnum type, long pos, long length)
	{
		this.type = type;
		this.pos = pos;
		this.length = length;
	}

	public TokenEnum getType()
	{
		return type;
	}

	public long getPos()
	{
		return pos;
	}

	public long getLength()
	{
		return length;
	}
}
