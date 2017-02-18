package org.treebolic.files;

public class ProviderData
{
	public static final String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.files.Provider" }; //$NON-NLS-1$
	}

	public static final String getMimetype()
	{
		return "inode/directory"; //$NON-NLS-1$
	}

	public static final String getExtensions()
	{
		return null;
	}

	public static final String getUrlScheme()
	{
		return "directory:"; //$NON-NLS-1$
	}

	public static String getStyle()
	{
		return null;
	}
}
