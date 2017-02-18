package org.treebolic.owl;

import java.io.File;
import java.io.IOException;

import org.treebolic.TreebolicIface;
import org.treebolic.filechooser.EntryChooser;
import org.treebolic.filechooser.FileChooserActivity;
import org.treebolic.plugin.Checker;
import org.treebolic.storage.Storage;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Treebolic Owl main activity
 *
 * @author Bernard Bou
 */
public class MainActivity extends Activity
{
	/**
	 * Log tag
	 */
	static private final String TAG = "Treebolic Owl Activity"; //$NON-NLS-1$

	/**
	 * File request code
	 */
	private static final int REQUEST_FILE_CODE = 1;

	/**
	 * Fragment
	 */
	private PlaceholderFragment fragment;

	// L I F E C Y C L E

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// initialize
		initialize();

		// check
		Checker.check(this);

		// view
		setContentView(R.layout.activity_main);

		// fragment
		if (savedInstanceState == null)
		{
			this.fragment = new PlaceholderFragment();
			getFragmentManager().beginTransaction().add(R.id.container, this.fragment).commit();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		// inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		final int id = item.getItemId();
		switch (id)
		{
		case R.id.action_query:
			query();
			return true;

		case R.id.action_source:
			requestSource();
			return true;

		case R.id.action_download:
			startActivity(new Intent(this, DownloadActivity.class));
			return true;

		case R.id.action_demo:
			final Uri archiveFileUri = Storage.copyAssetFile(this, Settings.DEMOZIP);
			if (archiveFileUri != null)
			{
				tryStartTreebolicBundle(archiveFileUri);
			}
			return true;

		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		case R.id.action_app_settings:
			Settings.applicationSettings(this, "org.treebolic.owl"); //$NON-NLS-1$
			return true;

		case R.id.action_finish:
			finish();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Initialize
	 */
	private void initialize()
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		// test if initialized
		final boolean result = sharedPref.getBoolean(Settings.PREF_INITIALIZED, false);
		if (result)
			return;

		// default settings
		Settings.setDefaults(this);

		// deploy
		Storage.expandZipAssetFile(this, Settings.DEMOZIP);

		// flag as initialized
		sharedPref.edit().putBoolean(Settings.PREF_INITIALIZED, true).commit();
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
	 * @param source
	 *            source
	 */
	private boolean query(final String source)
	{
		return query(source, Settings.getStringPref(this, TreebolicIface.PREF_BASE), Settings.getStringPref(this, TreebolicIface.PREF_IMAGEBASE),
				Settings.getStringPref(this, TreebolicIface.PREF_SETTINGS));
	}

	/**
	 * Query request
	 *
	 * @param source
	 *            source
	 * @param base
	 *            doc base
	 * @param imageBase
	 *            image base
	 * @param settings
	 *            settings
	 * @return true if query was made
	 */
	protected boolean query(final String source, final String base, final String imageBase, final String settings)
	{
		if (source == null || source.isEmpty())
		{
			Toast.makeText(MainActivity.this, R.string.fail_nullquery, Toast.LENGTH_SHORT).show();
			return false;
		}

		MainActivity.tryStartTreebolic(this, source, base, imageBase, settings);
		return true;
	}

	// S P E C I F I C R E T U R N S

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent returnIntent)
	{
		switch (requestCode)
		{
		case REQUEST_FILE_CODE:
			if (resultCode == Activity.RESULT_OK)
			{
				final Uri fileUri = returnIntent.getData();
				if (fileUri == null)
				{
					break;
				}

				Toast.makeText(this, fileUri.toString(), Toast.LENGTH_SHORT).show();
				final File file = new File(fileUri.getPath());
				final String parent = file.getParent();
				final File parentFile = new File(parent);
				final Uri parentUri = Uri.fromFile(parentFile);
				final String query = file.getName();
				String base = parentUri.toString();
				if (base != null && !base.endsWith("/")) //$NON-NLS-1$
				{
					base += '/';
				}
				Settings.save(this, query, base);

				// query
				query();
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	/**
	 * Request Owl source
	 */
	private void requestSource()
	{
		final Intent intent = new Intent();
		intent.setComponent(new ComponentName(this, org.treebolic.filechooser.FileChooserActivity.class));
		intent.setType("application/rdf+xml"); //$NON-NLS-1$
		intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_INITIAL_DIR, Settings.getStringPref(this, TreebolicIface.PREF_BASE));
		intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_EXTENSION_FILTER, new String[] { "owl", "rdf" }); //$NON-NLS-1$ //$NON-NLS-2$
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, MainActivity.REQUEST_FILE_CODE);
	}

	// S T A R T

	/**
	 * Try to start Treebolic activity from zipped bundle file
	 *
	 * @param archiveUri
	 *            archive uri
	 */
	private void tryStartTreebolicBundle(final Uri archiveUri)
	{
		try
		{
			// choose bundle entry
			EntryChooser.choose(this, new File(archiveUri.getPath()), new EntryChooser.Callback()
			{
				@Override
				public void onSelect(final String zipEntry)
				{
					final String base = "jar:" + archiveUri.toString() + "!/"; //$NON-NLS-1$ //$NON-NLS-2$
					MainActivity.tryStartTreebolic(MainActivity.this, zipEntry, base, Settings.getStringPref(MainActivity.this, TreebolicIface.PREF_IMAGEBASE),
							Settings.getStringPref(MainActivity.this, TreebolicIface.PREF_SETTINGS));
				}
			});
		}
		catch (final IOException e)
		{
			Log.d(MainActivity.TAG, "Failed to start treebolic from bundle uri " + archiveUri, e); //$NON-NLS-1$
		}
	}

	/**
	 * Start Treebolic plugin activity from uri
	 *
	 * @param context
	 *            context
	 * @param uri
	 *            uri of Owl file
	 */
	static public void tryStartTreebolic(final Context context, final Uri uri)
	{
		final String[] parsed = MainActivity.parse(uri);
		final Intent intent = MainActivity.makeTreebolicIntent(context, parsed[0], parsed[1], Settings.getStringPref(context, TreebolicIface.PREF_IMAGEBASE),
				Settings.getStringPref(context, TreebolicIface.PREF_SETTINGS));
		context.startActivity(intent);
	}

	/**
	 * Start Treebolic plugin activity from source + base
	 *
	 * @param context
	 *            context
	 * @param source
	 *            source
	 * @param base
	 *            base
	 * @param imagebase
	 *            image base
	 * @param settings
	 *            settings
	 */
	static public void tryStartTreebolic(final Context context, final String source, final String base, final String imagebase, final String settings)
	{
		final Intent intent = MainActivity.makeTreebolicIntent(context, source, base, imagebase, settings);
		Log.d(MainActivity.TAG, "Start treebolic from source " + source + " and base " + base); //$NON-NLS-1$ //$NON-NLS-2$
		context.startActivity(intent);
	}

	/**
	 * Split uri into source and base
	 *
	 * @param uri
	 *            uri
	 * @return string[0]=source string[1]=base
	 */
	static private String[] parse(final Uri uri)
	{
		final File file = new File(uri.getPath());
		final String source = file.getName();
		final String base = Uri.fromFile(new File(file.getParent())).toString() + '/';
		return new String[] { source, base };
	}

	/**
	 * Make Treebolic intent
	 *
	 * @param context
	 *            context
	 * @param model
	 *            model
	 * @param base
	 *            base
	 * @param imageBase
	 *            image base
	 * @param settings
	 *            settings
	 * @return intent
	 */
	static public Intent makeTreebolicIntent(final Context context, final String source, final String base, final String imageBase, final String settings)
	{
		// parent activity to return to
		final Intent parentIntent = new Intent();
		parentIntent.setClass(context, org.treebolic.owl.MainActivity.class);

		// intent
		final Intent intent = new Intent();
		intent.setComponent(new ComponentName(TreebolicIface.PKG_TREEBOLIC, TreebolicIface.ACTIVITY_PLUGIN));

		// model passing
		intent.putExtra(TreebolicIface.ARG_PLUGINPKG, "org.treebolic.owl"); //$NON-NLS-1$
		intent.putExtra(TreebolicIface.ARG_PROVIDER, "treebolic.provider.owl.owlapi.Provider"); //$NON-NLS-1$
		intent.putExtra(TreebolicIface.ARG_SOURCE, source);

		// other parameters passing
		intent.putExtra(TreebolicIface.ARG_BASE, base);
		intent.putExtra(TreebolicIface.ARG_IMAGEBASE, imageBase);
		intent.putExtra(TreebolicIface.ARG_SETTINGS, settings);

		// parent passing
		intent.putExtra(TreebolicIface.ARG_PARENTACTIVITY, parentIntent);
		intent.putExtra(TreebolicIface.ARG_URLSCHEME, "owl:"); //$NON-NLS-1$

		return intent;
	}

	// F R A G M E N T

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment
	{
		/**
		 * Constructor
		 */
		public PlaceholderFragment()
		{
			//
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
		{
			final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}
}
