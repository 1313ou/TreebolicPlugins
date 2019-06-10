package org.treebolic.dot;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.view.View;
import android.widget.Toast;

import org.treebolic.download.Deploy;
import org.treebolic.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Dot download activity
 *
 * @author Bernard Bou
 */
public class DownloadActivity extends org.treebolic.download.DownloadActivity
{
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		this.expandArchiveCheckbox.setVisibility(View.VISIBLE);
		this.destDir = Storage.getTreebolicStorage(this);
		this.downloadUrl = Settings.getStringPref(this, Settings.PREF_DOWNLOAD);
		if (this.downloadUrl == null || this.downloadUrl.isEmpty())
		{
			Toast.makeText(this, R.string.error_null_download_url, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public void start()
	{
		start(R.string.dot);
	}

	// P O S T P R O C E S S I N G

	@SuppressWarnings("SameReturnValue")
	@Override
	protected boolean doProcessing()
	{
		return true;
	}

	@SuppressWarnings("SameReturnValue")
	@Override
	protected boolean process(@NonNull final InputStream inputStream) throws IOException
	{
		if (this.expandArchive)
		{
			Deploy.expand(inputStream, Storage.getTreebolicStorage(this), false);
			return true;
		}
		Deploy.copy(inputStream, new File(Storage.getTreebolicStorage(this), this.destUri.getLastPathSegment()));
		return true;
	}
}
