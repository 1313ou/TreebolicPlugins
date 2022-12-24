package org.treebolic.owl;

import android.content.SharedPreferences;
import android.os.Bundle;

import org.treebolic.AppCompatCommonPreferenceActivity;
import org.treebolic.TreebolicIface;
import org.treebolic.preference.OpenEditTextPreference;

import androidx.annotation.NonNull;
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
		assert sharedPrefs != null;
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
		public void onDisplayPreferenceDialog(@NonNull final Preference preference)
		{
			if (!OpenEditTextPreference.onDisplayPreferenceDialog(this, preference))
			{
				super.onDisplayPreferenceDialog(preference);
			}
		}
	}
}
