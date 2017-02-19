package org.treebolic.files;

import android.annotation.SuppressLint;
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
public class MainActivity extends Activity
{
	/**
	 * Log tag
	 */
	static private final String TAG = "TreebolicFilesA"; //$NON-NLS-1$

	/**
	 * Dir request code
	 */
	private static final int REQUEST_DIR_CODE = 1;

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
			PlaceholderFragment fragment = new PlaceholderFragment();
			getFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
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
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		case R.id.action_query:
			final String fileUri = Settings.getStringPref(this, TreebolicIface.PREF_SOURCE);
			MainActivity.tryStartTreebolic(this, fileUri);
			return true;

		case R.id.action_choose:
			final Intent intent = new Intent(this, org.treebolic.filechooser.FileChooserActivity.class);
			intent.setType("inode/directory"); //$NON-NLS-1$
			intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_INITIAL_DIR, Storage.getExternalStorage());
			intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_CHOOSE_DIR, true);
			intent.putExtra(FileChooserActivity.ARG_FILECHOOSER_EXTENSION_FILTER, new String[] {});
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(intent, MainActivity.REQUEST_DIR_CODE);
			return true;

		case R.id.action_demo:
			MainActivity.tryStartTreebolic(this, Storage.getExternalStorage() + "/"); //$NON-NLS-1$
			return true;

		case R.id.action_app_settings:
			Settings.applicationSettings(this, "org.treebolic.files"); //$NON-NLS-1$
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
	@SuppressLint("CommitPrefEdits")
	private void initialize()
	{
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		// test if initialized
		final boolean result = sharedPref.getBoolean(Settings.PREF_INITIALIZED, false);
		if (result)
			return;

		// default settings
		Settings.setDefaults(this);

		// flag as initialized
		sharedPref.edit().putBoolean(Settings.PREF_INITIALIZED, true).commit();
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
		// handle selection of input by other activity which returns selected input
		if (resultCode == Activity.RESULT_OK)
		{
			final Uri fileUri = returnIntent.getData();
			if (fileUri != null)
			{
				Toast.makeText(this, fileUri.toString(), Toast.LENGTH_SHORT).show();
				switch (requestCode)
				{
				case REQUEST_DIR_CODE:
					Settings.putStringPref(this, TreebolicIface.PREF_SOURCE, fileUri.getPath());
					MainActivity.tryStartTreebolic(this, fileUri.getPath());
					break;
				default:
					break;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, returnIntent);
	}

	// S T A R T

	/**
	 * Start Treebolic plugin activity from root
	 *
	 * @param context
	 *            context
	 * @param root
	 *            root directory to explore
	 */
	static public void tryStartTreebolic(final Context context, final String root)
	{
		final File file = new File(root);
		final String source = file.getAbsolutePath();
		final String base = null;
		final String imageBase = null;
		final Intent intent = MainActivity.makeTreebolicIntent(context, source, base, imageBase);
		Log.d(MainActivity.TAG, "Start treebolic from root " + root); //$NON-NLS-1$
		context.startActivity(intent);
	}

	/**
	 * Make Treebolic intent
	 *
	 * @param context
	 *            content
	 * @param source
	 *            source
	 * @param base
	 *            base
	 * @param imageBase
	 *            image base
	 * @return intent
	 */
	static public Intent makeTreebolicIntent(final Context context, final String source, final String base, final String imageBase)
	{
		// parent activity to return to
		final Intent parentIntent = new Intent();
		parentIntent.setClass(context, org.treebolic.files.MainActivity.class);

		// intent
		final Intent intent = new Intent();
		intent.setComponent(new ComponentName(TreebolicIface.PKG_TREEBOLIC, TreebolicIface.ACTIVITY_PLUGIN));

		// model passing
		intent.putExtra(TreebolicIface.ARG_PLUGINPKG, "org.treebolic.files"); //$NON-NLS-1$
		intent.putExtra(TreebolicIface.ARG_PROVIDER, "treebolic.provider.files.Provider"); //$NON-NLS-1$
		intent.putExtra(TreebolicIface.ARG_SOURCE, source);

		// other parameters passing
		intent.putExtra(TreebolicIface.ARG_BASE, base);
		intent.putExtra(TreebolicIface.ARG_IMAGEBASE, imageBase);

		// parent passing
		intent.putExtra(TreebolicIface.ARG_PARENTACTIVITY, parentIntent);
		intent.putExtra(TreebolicIface.ARG_URLSCHEME, "directory:"); //$NON-NLS-1$

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
			return inflater.inflate(R.layout.fragment_main, container, false);
		}
	}
}
