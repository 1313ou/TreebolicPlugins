package org.treebolic.files;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class ProviderData
{
	@NonNull
	public static String[] getProviderClasses()
	{
		return new String[]{"treebolic.provider.files.Provider"};
	}

	@NonNull
	@SuppressWarnings("SameReturnValue")
	public static String getMimetype()
	{
		return "inode/directory";
	}

	@Nullable
	@SuppressWarnings("SameReturnValue")
	public static String getExtensions()
	{
		return null;
	}

	@NonNull
	@SuppressWarnings("SameReturnValue")
	public static String getUrlScheme()
	{
		return "directory:";
	}

	@Nullable
	@SuppressWarnings("SameReturnValue")
	public static String getStyle()
	{
		return null;
	}
}
