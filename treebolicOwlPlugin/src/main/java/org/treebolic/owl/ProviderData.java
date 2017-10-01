package org.treebolic.owl;

@SuppressWarnings("WeakerAccess")
public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.owl.owlapi.Provider" };
	}

	@SuppressWarnings("SameReturnValue")
	public static String getMimetype()
	{
		return "application/rdf+xml";
	}

	@SuppressWarnings("SameReturnValue")
	public static String getExtensions()
	{
		return "owl,rdf";
	}

	@SuppressWarnings("SameReturnValue")
	public static String getUrlScheme()
	{
		return "owl:";
	}

	@SuppressWarnings("SameReturnValue")
	public static String getStyle()
	{
		return null;
	}
}
