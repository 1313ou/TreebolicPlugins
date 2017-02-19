package org.treebolic.owl;

public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.owl.owlapi.Provider" }; //$NON-NLS-1$
	}

	public static String getMimetype()
	{
		return "application/rdf+xml"; //$NON-NLS-1$
	}

	public static String getExtensions()
	{
		return "owl,rdf"; //$NON-NLS-1$
	}

	public static String getUrlScheme()
	{
		return "owl:"; //$NON-NLS-1$
	}

	public static String getStyle()
	{
		return null;
	}
}
