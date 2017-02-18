package org.treebolic.dot;

import java.util.List;

import org.treebolic.TreebolicIface;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On handset devices, settings are presented as a single list. On tablets, settings
 * are split by category, with category headers shown to the left of the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html"> Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
{
	/**
	 * Determines whether to always show the simplified settings UI, where settings are presented in a single list. When false, settings are shown as a
	 * master/detail two-pane view on tablets. When true, a single pane is shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	// E V E N T S

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(final Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		if (SettingsActivity.isSimplePreferences(this))
		{
			setupSimplePreferencesScreen();
		}
	}

	// S E T U P

	/**
	 * Shows the simplified settings UI if the device configuration if the device configuration dictates that a simplified, single-pane UI should be shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen()
	{
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);
	}

	/** {@inheritDoc} */
	@Override
	public void onBuildHeaders(final List<Header> target)
	{
		if (!SettingsActivity.isSimplePreferences(this))
		{
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	// D E T E C T I O N

	/*
	 * (non-Javadoc)
	 *
	 * @see android.preference.PreferenceActivity#isValidFragment(java.lang.String)
	 */
	@Override
	protected boolean isValidFragment(final String fragmentName)
	{
		if (GeneralPreferenceFragment.class.getName().equals(fragmentName))
			return true;
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane()
	{
		return SettingsActivity.isLargeTablet(this) && !SettingsActivity.isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For example, 10" tablets are extra-large.
	 */
	private static boolean isLargeTablet(final Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device doesn't
	 * have newer APIs like {@link PreferenceFragment}, or the device doesn't have an extra-large screen. In these cases, a single-pane "simplified" settings UI
	 * should be shown.
	 */
	private static boolean isSimplePreferences(final Context context)
	{
		return SettingsActivity.ALWAYS_SIMPLE_PREFS || !SettingsActivity.isLargeTablet(context);
	}

	// L I S T E N E R

	/**
	 * A preference value change listener that updates the preference's summary to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener()
	{
		@Override
		public boolean onPreferenceChange(final Preference preference, final Object value)
		{
			// set the summary to the value's simple string representation.
			final String stringValue = value.toString();
			preference.setSummary(stringValue);
			return true;
		}
	};

	// B I N D

	/**
	 * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary (line of text below the preference
	 * title) is updated to reflect the value. The summary is also immediately updated upon calling this method. The exact display format is dependent on the
	 * type of preference.
	 *
	 * @see #listener
	 */
	private static void bind(final Preference preference)
	{
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(SettingsActivity.listener);

		// Trigger the listener immediately with the preference's current value.
		SettingsActivity.listener.onPreferenceChange(preference,
				PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "")); //$NON-NLS-1$
	}

	// F R A G M E N T S

	public static class GeneralPreferenceFragment extends PreferenceFragment
	{
		/*
		 * (non-Javadoc)
		 *
		 * @see android.preference.PreferenceFragment#onCreate(android.os.Bundle)
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// inflate
			addPreferencesFromResource(R.xml.pref_general);

			// bind
			SettingsActivity.bind(findPreference(TreebolicIface.PREF_SOURCE));
			SettingsActivity.bind(findPreference(TreebolicIface.PREF_BASE));
			SettingsActivity.bind(findPreference(TreebolicIface.PREF_IMAGEBASE));
			SettingsActivity.bind(findPreference(TreebolicIface.PREF_SETTINGS));
			SettingsActivity.bind(findPreference(Settings.PREF_DOWNLOAD));
		}
	}
}
