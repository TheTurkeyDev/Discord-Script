package dev.theturkey.discordscript.program;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class OutputWrapper
{
	private OutputStreamWriter out;
	private int currentLabel = 0;
	public boolean eaxSaved = false;

	public OutputWrapper(OutputStream outputStream)
	{
		out = new OutputStreamWriter(outputStream);
	}

	public String getNewLabel()
	{
		currentLabel++;
		return "L" + currentLabel;
	}

	public void write(String s)
	{
		try
		{
			out.write(s);
			out.flush();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void writeLine(String s)
	{
		try
		{
			out.write(s + "\n");
			out.flush();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
