/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.treebolic.dot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class ProviderData
{
	@NonNull
	public static String[] getProviderClasses()
	{
		return new String[]{"treebolic.provider.graphviz.Provider"};
	}

	@NonNull
	@SuppressWarnings("SameReturnValue")
	public static String getMimetype()
	{
		return "text/vnd.graphviz";
	}

	@NonNull
	@SuppressWarnings("SameReturnValue")
	public static String getExtensions()
	{
		return "dot,graphviz";
	}

	@NonNull
	@SuppressWarnings("SameReturnValue")
	public static String getUrlScheme()
	{
		return "dot:";
	}

	@Nullable
	@SuppressWarnings("SameReturnValue")
	public static String getStyle()
	{
		return null;
	}
}
