package org.treebolic.files;

public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.files.Provider" }; //$NON-NLS-1$
	}

	public static String getMimetype()
	{
		return "inode/directory"; //$NON-NLS-1$
	}

	public static String getExtensions()
	{
		return null;
	}

	public static String getUrlScheme()
	{
		return "directory:"; //$NON-NLS-1$
	}

	public static String getStyle()
	{
		return null;
	}
}
