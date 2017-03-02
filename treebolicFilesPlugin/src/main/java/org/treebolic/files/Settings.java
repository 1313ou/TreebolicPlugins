package org.treebolic.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import org.treebolic.TreebolicIface;
import org.treebolic.storage.Storage;

import java.io.File;

/**
 * Settings
 *
 * @author Bernard Bou
 */
public class Settings
{
	/**
	 * Initialized preference name
	 */
	public static final String PREF_INITIALIZED = "pref_initialized";

	/**
	 * Set default initial settings
	 *
	 * @param context
	 *            context
	 */
	@SuppressLint("CommitPrefEdits")
	static public void setDefaults(final Context context)
	{
		final String externalStorage = Storage.getExternalStorage();
		final File treebolicStorage = Storage.getTreebolicStorage(context);
		final Uri uri = Uri.fromFile(treebolicStorage);
		final String treebolicBase = uri.toString() + '/';

		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor = sharedPref.edit();
		editor.putString(TreebolicIface.PREF_SOURCE, externalStorage);
		editor.putString(TreebolicIface.PREF_BASE, treebolicBase);
		editor.putString(TreebolicIface.PREF_IMAGEBASE, treebolicBase);
		editor.commit();
	}

	/**
	 * Get string preference
	 *
	 * @param context
	 *            context
	 * @param key
	 *            key
	 * @return value
	 */
	static public String getStringPref(final Context context, final String key)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPref.getString(key, null);
	}

	/**
	 * Put string preference
	 *
	 * @param context
	 *            context
	 * @param key
	 *            key
	 * @param value
	 *            value
	 */
	@SuppressLint("CommitPrefEdits")
	static public void putStringPref(final Context context, final String key, final String value)
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.edit().putString(key, value).commit();
	}

	// U T I L S

	/**
	 * Application settings
	 *
	 * @param context
	 *            context
	 * @param pkgName
	 *            package name
	 */
	static public void applicationSettings(final Context context, final String pkgName)
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
