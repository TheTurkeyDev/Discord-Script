package dev.theturkey.discordscript.program.variables;

import dev.theturkey.discordscript.program.Scope;

public class VariableInstance
{
	public VariableType type;
	public String name;
	public Object value;
	public boolean isArray = false;

	public Scope scope;

	public VariableInstance(VariableType type, String name)
	{
		this(type, name, null);
	}

	public VariableInstance(VariableType type, String name, Object value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
	}

	public void setScope(Scope scope)
	{
		this.scope = scope;
	}

	public void setIsArray(boolean isArray)
	{
		this.isArray = true;
	}

	public boolean isInitialized()
	{
		return value != null;
	}

	public void setValue(Object object)
	{
		//TODO: Check type
		this.value = object;
	}

	public int getAsInt()
	{
		if(!this.isInitialized())
			scope.throwError("VariableNotInitailizedError", "");
		return (int) value;
	}

	public void setIndexValue(int index, Object value)
	{
		//TODO: Check the value type with this type
		//TODO: Check index is in range
		//TODO: Check that this is an array
		((Object[]) this.value)[index] = value;
	}

	public Object getIndexValue(int index)
	{
		//TODO: Check the value type with this type
		//TODO: Check index is in range
		//TODO: Check that this is an array
		return ((Object[]) this.value)[index];
	}
}
