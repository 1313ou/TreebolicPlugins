package org.treebolic.dot;

public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.graphviz.Provider" }; //$NON-NLS-1$
	}

	public static String getMimetype()
	{
		return "text/vnd.graphviz"; //$NON-NLS-1$
	}

	public static String getExtensions()
	{
		return "dot,graphviz"; //$NON-NLS-1$
	}

	public static String getUrlScheme()
	{
		return "dot:"; //$NON-NLS-1$
	}

	public static String getStyle()
	{
		return null;
	}
}
