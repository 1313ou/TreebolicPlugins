package org.treebolic.dot;

public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.graphviz.Provider" };
	}

	public static String getMimetype()
	{
		return "text/vnd.graphviz";
	}

	public static String getExtensions()
	{
		return "dot,graphviz";
	}

	public static String getUrlScheme()
	{
		return "dot:";
	}

	public static String getStyle()
	{
		return null;
	}
}
