package org.treebolic.files;

import android.content.SharedPreferences;
import android.os.Bundle;

import org.treebolic.AppCompatCommonPreferenceActivity;
import org.treebolic.TreebolicIface;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Settings activity
 *
 * @author Bernard Bou
 */
public class SettingsActivity extends AppCompatCommonPreferenceActivity
{
	// S U M M A R Y

	private static final Preference.SummaryProvider<Preference> STRING_SUMMARY_PROVIDER = (preference) -> {

		final SharedPreferences sharedPrefs = preference.getSharedPreferences();
		final String value = sharedPrefs.getString(preference.getKey(), null);
		return value == null ? "" : value;
	};

	// F R A G M E N T S

	@SuppressWarnings("WeakerAccess")
	public static class GeneralPreferenceFragment extends PreferenceFragmentCompat
	{
		@Override
		public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey)
		{
			// inflate
			addPreferencesFromResource(R.xml.pref_general);

			// bind
			Preference pref = findPreference(TreebolicIface.PREF_SOURCE);
			assert pref != null;
			pref.setSummaryProvider(STRING_SUMMARY_PROVIDER);
		}
	}
}
