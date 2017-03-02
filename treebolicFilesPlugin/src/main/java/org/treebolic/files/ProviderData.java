package org.treebolic.files;

public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.files.Provider" };
	}

	public static String getMimetype()
	{
		return "inode/directory";
	}

	public static String getExtensions()
	{
		return null;
	}

	public static String getUrlScheme()
	{
		return "directory:";
	}

	public static String getStyle()
	{
		return null;
	}
}
