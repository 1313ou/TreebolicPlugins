package org.treebolic.dot;

@SuppressWarnings("WeakerAccess")
public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.graphviz.Provider" };
	}

	@SuppressWarnings("SameReturnValue")
	public static String getMimetype()
	{
		return "text/vnd.graphviz";
	}

	@SuppressWarnings("SameReturnValue")
	public static String getExtensions()
	{
		return "dot,graphviz";
	}

	@SuppressWarnings("SameReturnValue")
	public static String getUrlScheme()
	{
		return "dot:";
	}

	@SuppressWarnings("SameReturnValue")
	public static String getStyle()
	{
		return null;
	}
}
