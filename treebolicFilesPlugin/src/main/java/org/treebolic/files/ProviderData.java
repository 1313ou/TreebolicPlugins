package org.treebolic.files;

@SuppressWarnings("WeakerAccess")
public class ProviderData
{
	public static String[] getProviderClasses()
	{
		return new String[] { "treebolic.provider.files.Provider" };
	}

	@SuppressWarnings("SameReturnValue")
	public static String getMimetype()
	{
		return "inode/directory";
	}

	@SuppressWarnings("SameReturnValue")
	public static String getExtensions()
	{
		return null;
	}

	@SuppressWarnings("SameReturnValue")
	public static String getUrlScheme()
	{
		return "directory:";
	}

	@SuppressWarnings("SameReturnValue")
	public static String getStyle()
	{
		return null;
	}
}
