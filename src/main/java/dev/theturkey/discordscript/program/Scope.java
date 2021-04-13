package dev.theturkey.discordscript.program;

import dev.theturkey.discordscript.program.codeblock.FunctionBlock;
import dev.theturkey.discordscript.program.variables.VariableInstance;
import dev.theturkey.discordscript.program.variables.VariableType;

import java.util.HashMap;
import java.util.Map;

public class Scope
{
	protected OutputWrapper output;
	private Scope parentScope;
	private Map<String, VariableInstance> variables = new HashMap<>();
	private Map<String, FunctionBlock> functions = new HashMap<>();

	private boolean errored = false;
	private boolean breaked = false;
	private boolean continued = false;
	private boolean returned = false;

	private Object returnVal;

	public Scope(Scope parentScope)
	{
		this.parentScope = parentScope;
		if(parentScope != null)
			this.output = parentScope.output;
	}

	public void throwError(String error, String details)
	{
		errored = true;
		this.output.writeLine(error);
		this.output.writeLine(details);
	}

	public OutputWrapper getOutput()
	{
		return output;
	}

	public VariableInstance createNewVariable(VariableType type, String name)
	{
		return this.createNewVariable(type, name, null);
	}

	public VariableInstance createNewVariable(VariableType type, String name, Object value)
	{
		if(variables.containsKey(name))
			throwError("DuplicateVariableNameError", "");
		VariableInstance variable = new VariableInstance(type, name, value);
		variable.setScope(this);
		variables.put(name, variable);
		return variable;
	}

	public VariableInstance getVariableFromName(String name)
	{
		VariableInstance var = variables.get(name);
		if(var == null && parentScope != null)
			var = parentScope.getVariableFromName(name);
		return var;
	}

	public void registerFunction(FunctionBlock functionBlock)
	{
		functions.put(functionBlock.getName(), functionBlock);
	}

	public FunctionInstance getFunctionFromName(String name)
	{
		FunctionInstance func = new FunctionInstance(this, functions.get(name));
		if(func.getFunctionBlock() == null && parentScope != null)
			func = parentScope.getFunctionFromName(name);
		if(func == null || func.getFunctionBlock() == null)
			return null;
		return func;
	}

	public boolean isErrorred()
	{
		return this.errored;
	}

	public void setErrored()
	{
		this.errored = true;
	}

	public boolean isBreaked()
	{
		return this.breaked;
	}

	public void setBreaked()
	{
		this.breaked = true;
	}

	public boolean isContinued()
	{
		return this.continued;
	}

	public void setContinued()
	{
		this.continued = true;
	}

	public boolean isReturned()
	{
		return this.returned;
	}

	public void setReturned(Object returnVal)
	{
		this.returnVal = returnVal;
		this.returned = true;
	}

	public Object getReturnVal()
	{
		return returnVal;
	}
}
