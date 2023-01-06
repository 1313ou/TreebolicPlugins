/*
 * Copyright (c) 2023. Bernard Bou
 */

package org.treebolic.plugin;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class Checker
{
	static private final String TAG = "Checker";

	static private final String TREEBOLIC_APP_PACKAGENAME = "org.treebolic";

	static public void checkFail(@NonNull final Context context) throws ActivityNotFoundException
	{
		final boolean isInstalled = Checker.isAppInstalled(Checker.TREEBOLIC_APP_PACKAGENAME, context);
		if (!isInstalled)
		{
			throw new ActivityNotFoundException();
		}
	}

	static public void check(@NonNull final Activity activity)
	{
		final boolean isInstalled = Checker.isAppInstalled(Checker.TREEBOLIC_APP_PACKAGENAME, activity);
		if (!isInstalled)
		{
			Log.e(TAG, Checker.TREEBOLIC_APP_PACKAGENAME + " is not installed");
			Toast.makeText(activity, Checker.TREEBOLIC_APP_PACKAGENAME + " needed", Toast.LENGTH_LONG).show();
			Checker.install(activity);
			activity.finish();
		}
		// launch if the package is already installed
		// final Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(TREEBOLIC_APP_PACKAGENAME);
		// context.startActivity(launchIntent);
	}

	static private void install(@NonNull final Context context)
	{
		final Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + Checker.TREEBOLIC_APP_PACKAGENAME));
		try
		{
			context.startActivity(goToMarket);
		}
		catch (@NonNull final ActivityNotFoundException ignored)
		{
			Toast.makeText(context, R.string.market_fail_treebolic, Toast.LENGTH_LONG).show();
		}
	}

	static private boolean isAppInstalled(@SuppressWarnings("SameParameterValue") final String packageName, @NonNull final Context context)
	{
		final PackageManager packageManager = context.getPackageManager();
		boolean isInstalled;
		try
		{
			@SuppressWarnings("unused") final PackageInfo ignored = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ? //
					packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES)) : //
					packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			isInstalled = true;
		}
		catch (@NonNull final PackageManager.NameNotFoundException ignored)
		{
			isInstalled = false;
		}
		return isInstalled;
	}
}
