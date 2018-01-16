package org.treebolic.files;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.treebolic.AppCompatCommonPreferenceActivity;
import org.treebolic.TreebolicIface;

import java.util.List;

/**
 * A AppCompatPreferenceActivity that presents a set of application settings.
 */
public class SettingsActivity extends AppCompatCommonPreferenceActivity
{
	// E V E N T S

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		// super
		super.onCreate(savedInstanceState);

		// toolbar
		setupToolbar(R.layout.toolbar, R.id.toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// S E T U P

	@Override
	public void onBuildHeaders(final List<Header> target)
	{
		loadHeadersFromResource(R.xml.pref_headers, target);
	}

	// D E T E C T I O N

	@Override
	protected boolean isValidFragment(final String fragmentName)
	{
		return GeneralPreferenceFragment.class.getName().equals(fragmentName);
	}

	@Override
	public boolean onIsMultiPane()
	{
		return SettingsActivity.isLargeTablet(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For example, 10" tablets are extra-large.
	 */
	private static boolean isLargeTablet(final Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	// L I S T E N E R

	/**
	 * A preference value change listener that updates the preference's summary to reflect its new value.
	 */
	private static final Preference.OnPreferenceChangeListener listener = (preference, value) ->
	{
		// set the summary to the value's simple string representation.
		final String stringValue = value.toString();
		preference.setSummary(stringValue);
		return true;
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
		SettingsActivity.listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
	}

	// F R A G M E N T S

	public static class GeneralPreferenceFragment extends PreferenceFragment
	{
		@Override
		public void onCreate(final Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

			// inflate
			addPreferencesFromResource(R.xml.pref_general);

			// bind
			SettingsActivity.bind(findPreference(TreebolicIface.PREF_SOURCE));
		}
	}
}
