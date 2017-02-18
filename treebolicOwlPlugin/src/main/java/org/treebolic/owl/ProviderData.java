package org.treebolic.owl;

public class ProviderData
{
	public static final String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.owl.owlapi.Provider" }; //$NON-NLS-1$
	}

	public static final String getMimetype()
	{
		return "application/rdf+xml"; //$NON-NLS-1$
	}

	public static final String getExtensions()
	{
		return "owl,rdf"; //$NON-NLS-1$
	}

	public static final String getUrlScheme()
	{
		return "owl:"; //$NON-NLS-1$
	}

	public static String getStyle()
	{
		return null;
	}
}
