package org.treebolic.owl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import org.treebolic.AppCompatCommonPreferenceActivity;
import org.treebolic.TreebolicIface;
import org.treebolic.preference.OpenEditTextPreference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.NavUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.legacy.contrib.Header;

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
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		if (item.getItemId() == android.R.id.home)
		{
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// S E T U P

	@Override
	public void onBuildHeaders(@NonNull final List<Header> target)
	{
		loadHeadersFromResource(R.xml.pref_headers, target);
	}

	// D E T E C T I O N

	@Override
	public boolean isValidFragment(final String fragmentName)
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
	private static boolean isLargeTablet(@NonNull final Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	// S U M M A R Y

	private static final Preference.SummaryProvider<Preference> STRING_SUMMARY_PROVIDER = (preference) -> {

		final SharedPreferences sharedPrefs = preference.getSharedPreferences();
		final String value = sharedPrefs.getString(preference.getKey(), null);
		return value == null ? "" : value;
	};

	// F R A G M E N T S

	public static class GeneralPreferenceFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(@SuppressWarnings("unused") final Bundle savedInstanceState, @SuppressWarnings("unused") final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_general);

			// bind
			Preference pref = findPreference(TreebolicIface.PREF_SOURCE);
			assert pref != null;
			pref.setSummaryProvider(STRING_SUMMARY_PROVIDER);

			pref = findPreference(TreebolicIface.PREF_BASE);
			assert pref != null;
			pref.setSummaryProvider(STRING_SUMMARY_PROVIDER);

			pref = findPreference(TreebolicIface.PREF_IMAGEBASE);
			assert pref != null;
			pref.setSummaryProvider(STRING_SUMMARY_PROVIDER);

			pref = findPreference(TreebolicIface.PREF_SETTINGS);
			assert pref != null;
			pref.setSummaryProvider(STRING_SUMMARY_PROVIDER);

			pref = findPreference(Settings.PREF_DOWNLOAD);
			assert pref != null;
			pref.setSummaryProvider(STRING_SUMMARY_PROVIDER);
		}

		@Override
		public void onDisplayPreferenceDialog(final Preference preference)
		{
			if (!OpenEditTextPreference.onDisplayPreferenceDialog(this, preference))
			{
				super.onDisplayPreferenceDialog(preference);
			}
		}
	}
}
