package org.treebolic.owl;

public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.owl.owlapi.Provider" };
	}

	public static String getMimetype()
	{
		return "application/rdf+xml";
	}

	public static String getExtensions()
	{
		return "owl,rdf";
	}

	public static String getUrlScheme()
	{
		return "owl:";
	}

	public static String getStyle()
	{
		return null;
	}
}
