package org.treebolic.owl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.treebolic.TreebolicIface;
import org.treebolic.storage.Storage;

import java.io.File;

/**
 * Settings
 *
 * @author Bernard Bou
 */
@SuppressWarnings("WeakerAccess")
public class Settings
{
	/**
	 * Demo archive
	 */
	public static final String DEMOZIP = "owl.zip";

	/**
	 * Demo
	 */
	public static final String DEMO = "pizza.owl";

	/**
	 * Initialized preference name
	 */
	public static final String PREF_INITIALIZED = "pref_initialized";

	/**
	 * Download preference name
	 */
	public static final String PREF_DOWNLOAD = "pref_download";

	/**
	 * Set default initial settings
	 *
	 * @param context context
	 */
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	static public void setDefaults(@NonNull final Context context)
	{
		final File treebolicStorage = Storage.getTreebolicStorage(context);
		final Uri uri = Uri.fromFile(treebolicStorage);
		final String treebolicBase = uri.toString() + '/';

		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor = sharedPref.edit();
		editor.putString(TreebolicIface.PREF_SOURCE, Settings.DEMO);
		editor.putString(TreebolicIface.PREF_BASE, treebolicBase);
		editor.putString(TreebolicIface.PREF_IMAGEBASE, treebolicBase);
		editor.commit();
	}

	/**
	 * Save source and base to preferences
	 *
	 * @param context context
	 * @param source  source
	 * @param base    base
	 */
	public static void save(final Context context, final String source, final String base)
	{
		Settings.putStringPref(context, TreebolicIface.PREF_SOURCE, source);
		Settings.putStringPref(context, TreebolicIface.PREF_BASE, base);
	}

	/**
	 * Get string preference
	 *
	 * @param context context
	 * @param key     key
	 * @return value
	 */
	@Nullable
	static public String getStringPref(final Context context, final String key)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(key, null);
	}

	/**
	 * Put string preference
	 *
	 * @param context context
	 * @param key     key
	 * @param value   value
	 */
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	static public void putStringPref(final Context context, final String key, final String value)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putString(key, value).commit();
	}

	// U T I L S

	/**
	 * Application settings
	 *
	 * @param context context
	 * @param pkgName package name
	 */
	static public void applicationSettings(@NonNull final Context context, @SuppressWarnings("SameParameterValue") final String pkgName)
	{
		final int apiLevel = Build.VERSION.SDK_INT;
		final Intent intent = new Intent();

		if (apiLevel >= 9)
		{
			intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.setData(Uri.parse("package:" + pkgName));
		}
		else
		{
			final String appPkgName = apiLevel == 8 ? "pkg" : "com.android.settings.ApplicationPkgName";

			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
			intent.putExtra(appPkgName, pkgName);
		}

		// start activity
		context.startActivity(intent);
	}
}
