package org.treebolic.owl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressWarnings("WeakerAccess")
public class ProviderData
{
	@NonNull
	public static String[] getProviderClasses()
	{
		return new String[]{"treebolic.provider.owl.owlapi.Provider"};
	}

	@NonNull
	@SuppressWarnings("SameReturnValue")
	public static String getMimetype()
	{
		return "application/rdf+xml";
	}

	@NonNull
	@SuppressWarnings("SameReturnValue")
	public static String getExtensions()
	{
		return "owl,rdf";
	}

	@NonNull
	@SuppressWarnings("SameReturnValue")
	public static String getUrlScheme()
	{
		return "owl:";
	}

	@Nullable
	@SuppressWarnings("SameReturnValue")
	public static String getStyle()
	{
		return null;
	}
}
