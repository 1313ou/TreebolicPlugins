package org.treebolic.files;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.treebolic.TreebolicIface;
import org.treebolic.filechooser.FileChooserActivity;
import org.treebolic.plugin.Checker;
import org.treebolic.storage.Storage;

import java.io.File;

/**
 * Treebolic Files main activity
 *
 * @author Bernard Bou
 */
public class MainActivity extends AppCompatActivity
{
	/**
	 * Log tag
	 */
	static private final String TAG = "TreebolicFilesA";

	/**
	 * Dir request code
	 */
	private static final int REQUEST_DIR_CODE = 1;

	// L I F E C Y C L E

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// layout
		setContentView(R.layout.activity_main);

		// toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// set up the action bar
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			actionBar.setDisplayOptions(ActionBar.DISPLAY_USE_LOGO | ActionBar.DISPLAY_SHOW_TITLE);
		}

		// initialize
		initialize();

		// check
		Checker.check(this);

		// fragment
		if (savedInstanceState == null)
		{
			PlaceholderFragment fragment = new PlaceholderFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
		}
	}

	@Override
	protected void onResume()
	{
		updateButton();
		super.onResume();
	}

	// M E N U

	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		switch (id)
		{
			case R.id.action_places:
				chooseAndSave();
				return true;

			case R.id.action_source:
				requestSource();
				return true;

			case R.id.action_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;

			case R.id.action_run:
				final String fileUri = Settings.getStringPref(this, TreebolicIface.PREF_SOURCE);
				MainActivity.tryStartTreebolic(this, fileUri);
				return true;

			case R.id.action_demo:
				//MainActivity.tryStartTreebolic(this, Environment.getExternalStorageDirectory().getAbsolutePath());
				chooseAndTryStartTreebolic();
				return true;

			case R.id.action_app_settings:
				Settings.applicationSettings(this, "org.treebolic.files");
				return true;

			case R.id.action_finish:
				finish();
				return true;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	// I N I T I A L I Z E

	/**
	 * Initialize
	 */
	@SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
	private void initialize()
	{
		// permissions
		Permissions.check(this);

		// test if initialized
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean initialized = sharedPref.getBoolean(Settings.PREF_INITIALIZED, false);
		if (!initialized)
		{
			// default settings
			Settings.setDefaults(this);

			// flag as initialized
			sharedPref.edit().putBoolean(Settings.PREF_INITIALIZED, true).commit();
		}
	}

	// R E Q U E S T (choose source)

	/**
	 * Request directory source
	 */
	private void requestSource()
	{
		final Intent intent = new Intent(this, org.treebolic.filechooser.FileChooserActivity.class);
		intent.setType("inode/directory");
		intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_INITIAL_DIR, Storage.getExternalStorage());
		intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_CHOOSE_DIR, true);
		intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_EXTENSION_FILTER, new String[]{});
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, MainActivity.REQUEST_DIR_CODE);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent returnIntent)
	{
		// handle selection of input by other activity which returns selected input
		if (resultCode == AppCompatActivity.RESULT_OK)
		{
			final Uri fileUri = returnIntent.getData();
			if (fileUri != null)
			{
				Toast.makeText(this, fileUri.toString(), Toast.LENGTH_SHORT).show();
				switch (requestCode)
				{
					case REQUEST_DIR_CODE:
						Settings.putStringPref(this, TreebolicIface.PREF_SOURCE, fileUri.getPath());

						updateButton();

						// query
						// query());
						break;
					default:
						break;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	abstract class Runnable1
	{
		abstract public void run(final String arg);
	}

	/**
	 * Choose dir and scan
	 */
	private void chooseAndTryStartTreebolic()
	{
		choosePlace(new Runnable1()
		{
			@Override
			public void run(final String arg)
			{
				query(arg + File.separatorChar);
			}
		});
	}

	/**
	 * Choose dir and save
	 */
	private void chooseAndSave()
	{
		choosePlace(new Runnable1()
		{
			@Override
			public void run(final String arg)
			{
				Settings.putStringPref(MainActivity.this, TreebolicIface.PREF_SOURCE, arg);
				updateButton();
			}
		});
	}

	/**
	 * Choose dir to scan
	 *
	 * @param runnable1 what to do
	 */

	private void choosePlace(final Runnable1 runnable1)
	{
		final Pair<CharSequence[], CharSequence[]> result = Storage.getDirectoriesTypesValues(this);
		final CharSequence[] types = result.first;
		final CharSequence[] values = result.second;

		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.title_choose);
		alert.setMessage(R.string.title_choose_directory);

		final RadioGroup input = new RadioGroup(this);
		for (int i = 0; i < types.length && i < values.length; i++)
		{
			final CharSequence type = types[i];
			final CharSequence value = values[i];
			final File dir = new File(value.toString());
			if (dir.exists())
			{
				final RadioButton radioButton = new RadioButton(this);
				radioButton.setText(dir.getAbsolutePath() + ' ' + '[' + type + ']');
				radioButton.setTag(dir.getAbsolutePath());
				input.addView(radioButton);
			}
		}
		alert.setView(input);
		alert.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				dialog.dismiss();

				int childCount = input.getChildCount();
				for (int i = 0; i < childCount; i++)
				{
					final RadioButton radioButton = (RadioButton) input.getChildAt(i);
					if (radioButton.getId() == input.getCheckedRadioButtonId())
					{
						final String sourceFile = radioButton.getTag().toString();
						final File sourceDir = new File(sourceFile);
						if (sourceDir.exists() && sourceDir.isDirectory())
						{
							runnable1.run(sourceFile + File.separatorChar);
						}
						else
						{
							final AlertDialog.Builder alert2 = new AlertDialog.Builder(MainActivity.this);
							alert2.setTitle(sourceFile) //
									.setMessage(getString(R.string.status_fail)) //
									.show();
						}
					}
				}
			}
		});
		alert.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				// canceled.
			}
		});
		alert.show();
	}

	// Q U E R Y

	/**
	 * Query request
	 */
	private void query()
	{
		// get query
		final String query = Settings.getStringPref(this, TreebolicIface.PREF_SOURCE);
		query(query);
	}

	/**
	 * Query request
	 *
	 * @param source source
	 */
	private boolean query(final String source)
	{
		if (source == null || source.isEmpty())
		{
			Toast.makeText(MainActivity.this, R.string.fail_nullquery, Toast.LENGTH_SHORT).show();
			return false;
		}

		MainActivity.tryStartTreebolic(this, source);
		return true;
	}

	// S T A R T

	/**
	 * Start Treebolic plugin activity from root
	 *
	 * @param context context
	 */
	static public void tryStartTreebolic(final Context context)
	{
		final String root = Settings.getStringPref(context, TreebolicIface.PREF_SOURCE);
		tryStartTreebolic(context, root);
	}

	/**
	 * Start Treebolic plugin activity from root
	 *
	 * @param context context
	 * @param root    root directory to explore
	 */
	static public void tryStartTreebolic(final Context context, final String root)
	{
		final File file = new File(root);
		final String source = file.getAbsolutePath();
		final Intent intent = MainActivity.makeTreebolicIntent(context, source);
		Log.d(MainActivity.TAG, "Start treebolic from root " + root);
		context.startActivity(intent);
	}

	/**
	 * Make Treebolic intent
	 *
	 * @param context content
	 * @param source  source
	 * @return intent
	 */
	static public Intent makeTreebolicIntent(final Context context, final String source)
	{
		// parent activity to return to
		final Intent parentIntent = new Intent();
		parentIntent.setClass(context, org.treebolic.files.MainActivity.class);

		// intent
		final Intent intent = new Intent();
		intent.setComponent(new ComponentName(TreebolicIface.PKG_TREEBOLIC, TreebolicIface.ACTIVITY_PLUGIN));

		// model passing
		intent.putExtra(TreebolicIface.ARG_PLUGINPKG, "org.treebolic.files");
		intent.putExtra(TreebolicIface.ARG_PROVIDER, "treebolic.provider.files.Provider");
		intent.putExtra(TreebolicIface.ARG_SOURCE, source);

		// other parameters passing
		intent.putExtra(TreebolicIface.ARG_BASE, (String) null);
		intent.putExtra(TreebolicIface.ARG_IMAGEBASE, (String) null);

		// parent passing
		intent.putExtra(TreebolicIface.ARG_PARENTACTIVITY, parentIntent);
		intent.putExtra(TreebolicIface.ARG_URLSCHEME, "directory:");

		return intent;
	}

	// H E L P E R

	private void updateButton()
	{
		final ImageButton button = (ImageButton) findViewById(R.id.queryButton);
		final TextView sourceText = (TextView) findViewById(R.id.querySource);
		final String source = Settings.getStringPref(this, TreebolicIface.PREF_SOURCE);
		final boolean qualifies = sourceQualifies(source);
		button.setVisibility(qualifies ? View.VISIBLE : View.INVISIBLE);
		sourceText.setVisibility(qualifies ? View.VISIBLE : View.INVISIBLE);
		if (qualifies)
		{
			sourceText.setText(source);
		}
	}

	/**
	 * Whether source qualifies
	 *
	 * @return true if source qualifies
	 */
	private boolean sourceQualifies(final String source)
	{
		if (source != null && !source.isEmpty())
		{
			final File file = new File(source);
			Log.d(MainActivity.TAG, "file=" + file);
			return file.exists() && file.isDirectory();
		}
		return false;
	}

	// C L I C K

	/**
	 * Click listener
	 *
	 * @param view view
	 */
	public void onClick(final View view)
	{
		query();
	}

	// F R A G M E N T

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
		{
			return inflater.inflate(R.layout.fragment_main, container, false);
		}
	}
}
