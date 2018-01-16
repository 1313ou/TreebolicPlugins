package org.treebolic.plugin;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class Checker
{
	static private final String APP = "org.treebolic";

	static public void check(final Context context)
	{
		final boolean isInstalled = Checker.isAppInstalled(Checker.APP, context);
		if (!isInstalled)
		{
			System.out.println(Checker.APP + " is not installed");
			Toast.makeText(context, Checker.APP + " needed", Toast.LENGTH_LONG).show();
			Checker.install(context);
		}
		// launch if the package is already installed
		// final Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(APP);
		// context.startActivity(launchIntent);
	}

	static private void install(final Context context)
	{
		final Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + Checker.APP));
		try
		{
			context.startActivity(goToMarket);
		}
		catch (final ActivityNotFoundException ignored)
		{
			Toast.makeText(context, R.string.market_fail, Toast.LENGTH_LONG).show();
		}
	}

	static private boolean isAppInstalled(@SuppressWarnings("SameParameterValue") final String uri, final Context context)
	{
		final PackageManager packageManager = context.getPackageManager();
		boolean isInstalled;
		try
		{
			packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			isInstalled = true;
		}
		catch (final PackageManager.NameNotFoundException ignored)
		{
			isInstalled = false;
		}
		return isInstalled;
	}
}
