package org.treebolic.owl;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bbou.donate.DonateActivity;
import com.bbou.others.OthersActivity;
import com.bbou.rate.AppRate;

import org.treebolic.AppCompatCommonActivity;
import org.treebolic.TreebolicIface;
import org.treebolic.filechooser.EntryChooser;
import org.treebolic.filechooser.FileChooserActivity;
import org.treebolic.plugin.Checker;
import org.treebolic.storage.Storage;
import org.treebolic.storage.Deployer;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

/**
 * Treebolic Owl main activity
 *
 * @author Bernard Bou
 */
public class MainActivity extends AppCompatCommonActivity
{
	/**
	 * Log tag
	 */
	static private final String TAG = "PluginOwlA";

	/**
	 * File request code
	 */
	private static final int REQUEST_FILE_CODE = 1;

	// L I F E C Y C L E

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// rate
		AppRate.invoke(this);

		// layout
		setContentView(R.layout.activity_main);

		// toolbar
		final Toolbar toolbar = findViewById(org.treebolic.download.R.id.toolbar);
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

	@SuppressWarnings("SameReturnValue")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull final MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		if (R.id.action_query == id)
		{
			query();
			return true;
		}
		else if (R.id.action_source == id)
		{
			requestSource();
			return true;
		}
		else if (R.id.action_download == id)
		{
			startActivity(new Intent(this, DownloadActivity.class));
			return true;
		}
		else if (R.id.action_demo == id)
		{
			final Uri archiveFileUri = Deployer.copyAssetFile(this, Settings.DEMOZIP);
			if (archiveFileUri != null)
			{
				tryStartTreebolicBundle(archiveFileUri);
			}
			return true;
		}
		else if (R.id.action_others == id)
		{
			startActivity(new Intent(this, OthersActivity.class));
			return true;
		}
		else if (R.id.action_donate == id)
		{
			startActivity(new Intent(this, DonateActivity.class));
			return true;
		}
		else if (R.id.action_rate == id)
		{
			AppRate.rate(this);
			return true;
		}
		else if (R.id.action_settings == id)
		{
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		else if (R.id.action_app_settings == id)
		{
			Settings.applicationSettings(this, "org.treebolic.owl");
			return true;
		}
		else if (R.id.action_finish == id)
		{
			finish();
			return true;
		}
		else if (R.id.action_kill == id)
		{
			Process.killProcess(Process.myPid());
			return true;
		}
		else
		{
			return super.onOptionsItemSelected(item);
		}
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

		// deploy
		final File dir = Storage.getTreebolicStorage(this);
		if (dir.isDirectory())
		{
			final String[] dirContent = dir.list();
			if (dirContent == null || dirContent.length == 0)
			{
				// deploy
				Deployer.expandZipAssetFile(this, Settings.DEMOZIP);
			}
		}
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
	@SuppressWarnings("UnusedReturnValue")
	private boolean query(final String source)
	{
		return query(source, Settings.getStringPref(this, TreebolicIface.PREF_BASE), Settings.getStringPref(this, TreebolicIface.PREF_IMAGEBASE), Settings.getStringPref(this, TreebolicIface.PREF_SETTINGS));
	}

	/**
	 * Query request
	 *
	 * @param source    source
	 * @param base      doc base
	 * @param imageBase image base
	 * @param settings  settings
	 * @return true if query was made
	 */
	@SuppressWarnings("WeakerAccess")
	protected boolean query(@Nullable final String source, final String base, final String imageBase, final String settings)
	{
		if (source == null || source.isEmpty())
		{
			Toast.makeText(MainActivity.this, R.string.fail_nullquery, Toast.LENGTH_SHORT).show();
			return false;
		}

		MainActivity.tryStartTreebolic(this, source, base, imageBase, settings);
		return true;
	}

	// R E Q U E S T (choose source)

	/**
	 * Request Owl source
	 */
	private void requestSource()
	{
		final Intent intent = new Intent();
		intent.setComponent(new ComponentName(this, org.treebolic.filechooser.FileChooserActivity.class));
		intent.setType("application/rdf+xml");
		intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_INITIAL_DIR, Settings.getStringPref(this, TreebolicIface.PREF_BASE));
		intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_EXTENSION_FILTER, new String[]{"owl", "rdf"});
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, MainActivity.REQUEST_FILE_CODE);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent returnIntent)
	{
		if (requestCode == REQUEST_FILE_CODE)
		{
			if (resultCode == AppCompatActivity.RESULT_OK && returnIntent != null)
			{
				final Uri fileUri = returnIntent.getData();
				if (fileUri != null)
				{
					Toast.makeText(this, fileUri.toString(), Toast.LENGTH_SHORT).show();
					final String path = fileUri.getPath();
					if (path != null)
					{
						final File file = new File(path);
						final String parent = file.getParent();
						if (parent != null)
						{
							final File parentFile = new File(parent);
							final Uri parentUri = Uri.fromFile(parentFile);
							final String query = file.getName();
							String base = parentUri.toString();
							if (!base.endsWith("/"))
							{
								base += '/';
							}
							Settings.save(this, query, base);

							updateButton();

							// query
							// query();
						}
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	// S T A R T

	/**
	 * Try to start Treebolic activity from zipped bundle file
	 *
	 * @param archiveUri archive uri
	 */
	private void tryStartTreebolicBundle(@NonNull final Uri archiveUri)
	{
		try
		{
			final String path = archiveUri.getPath();
			if (path != null)
			{
				// choose bundle entry
				EntryChooser.choose(this, new File(path), zipEntry -> {
					final String base = "jar:" + archiveUri.toString() + "!/";
					MainActivity.tryStartTreebolic(MainActivity.this, zipEntry, base, Settings.getStringPref(MainActivity.this, TreebolicIface.PREF_IMAGEBASE), Settings.getStringPref(MainActivity.this, TreebolicIface.PREF_SETTINGS));
				});
			}
		}
		catch (@NonNull final IOException e)
		{
			Log.d(MainActivity.TAG, "Failed to start treebolic from bundle uri " + archiveUri, e);
		}
	}

	/**
	 * Start Treebolic plugin activity from uri
	 *
	 * @param context context
	 * @param uri     uri of Owl file
	 */
	static public void tryStartTreebolic(@NonNull final Context context, @NonNull final Uri uri)
	{
		try
		{
			Checker.checkFail(context);
		}
		catch (ActivityNotFoundException e)
		{
			return;
		}
		final String[] parsed = MainActivity.parse(uri);
		if (parsed != null)
		{
			final Intent intent = MainActivity.makeTreebolicIntent(context, parsed[0], parsed[1], Settings.getStringPref(context, TreebolicIface.PREF_IMAGEBASE), Settings.getStringPref(context, TreebolicIface.PREF_SETTINGS));
			context.startActivity(intent);
		}
	}

	/**
	 * Start Treebolic plugin activity from source + base
	 *
	 * @param context   context
	 * @param source    source
	 * @param base      base
	 * @param imagebase image base
	 * @param settings  settings
	 */
	@SuppressWarnings("WeakerAccess")
	static public void tryStartTreebolic(@NonNull final Context context, final String source, final String base, final String imagebase, final String settings)
	{
		try
		{
			Checker.checkFail(context);
		}
		catch (ActivityNotFoundException e)
		{
			return;
		}
		final Intent intent = MainActivity.makeTreebolicIntent(context, source, base, imagebase, settings);
		Log.d(MainActivity.TAG, "Start treebolic from source " + source + " and base " + base);
		context.startActivity(intent);
	}

	/**
	 * Split uri into source and base
	 *
	 * @param uri uri
	 * @return string[0]=source string[1]=base
	 */
	@Nullable
	static private String[] parse(@NonNull final Uri uri)
	{
		final String path = uri.getPath();
		if (path != null)
		{
			final File file = new File(path);
			final String source = file.getName();
			final String parent = file.getParent();
			if (parent != null)
			{
				final String base = Uri.fromFile(new File(parent)).toString() + '/';
				return new String[]{source, base};
			}
		}
		return null;
	}

	/**
	 * Make Treebolic intent
	 *
	 * @param context   context
	 * @param source    source
	 * @param base      base
	 * @param imageBase image base
	 * @param settings  settings
	 * @return intent
	 */
	@NonNull
	@SuppressWarnings("WeakerAccess")
	static public Intent makeTreebolicIntent(@NonNull final Context context, final String source, final String base, final String imageBase, final String settings)
	{
		// parent activity to return to
		final Intent parentIntent = new Intent();
		parentIntent.setClass(context, org.treebolic.owl.MainActivity.class);

		// intent
		final Intent intent = new Intent();
		intent.setComponent(new ComponentName(TreebolicIface.PKG_TREEBOLIC, TreebolicIface.ACTIVITY_PLUGIN));

		// model passing
		intent.putExtra(TreebolicIface.ARG_PLUGINPKG, "org.treebolic.owl");
		intent.putExtra(TreebolicIface.ARG_PROVIDER, "treebolic.provider.owl.owlapi.Provider");
		intent.putExtra(TreebolicIface.ARG_SOURCE, source);

		// other parameters passing
		intent.putExtra(TreebolicIface.ARG_BASE, base);
		intent.putExtra(TreebolicIface.ARG_IMAGEBASE, imageBase);
		intent.putExtra(TreebolicIface.ARG_SETTINGS, settings);

		// parent passing
		intent.putExtra(TreebolicIface.ARG_PARENTACTIVITY, parentIntent);
		intent.putExtra(TreebolicIface.ARG_URLSCHEME, "owl:");

		return intent;
	}

	// H E L P E R

	private void updateButton()
	{
		final ImageButton button = findViewById(R.id.queryButton);
		final TextView sourceText = findViewById(R.id.querySource);
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
	private boolean sourceQualifies(@Nullable final String source)
	{
		final String base = Settings.getStringPref(this, TreebolicIface.PREF_BASE);
		if (source != null && !source.isEmpty())
		{
			final Uri baseUri = Uri.parse(base);
			final String path = baseUri.getPath();
			if (path != null)
			{
				final File baseFile = base == null ? null : new File(path);
				final File file = new File(baseFile, source);
				Log.d(MainActivity.TAG, "file=" + file);
				return file.exists();
			}
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
	@SuppressWarnings("WeakerAccess")
	public static class PlaceholderFragment extends Fragment
	{
		@Override
		public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
		{
			return inflater.inflate(R.layout.fragment_main, container, false);
		}
	}
}
