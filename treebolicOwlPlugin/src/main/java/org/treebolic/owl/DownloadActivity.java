package org.treebolic.owl;

import android.net.Uri;
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
 * Owl download activity
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
		start(R.string.owl);
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
		final File storage = Storage.getTreebolicStorage(this);

		if (this.expandArchive)
		{
			Deploy.expand(inputStream, Storage.getTreebolicStorage(this), false);
			return true;
		}

		final Uri downloadUri = Uri.parse(this.downloadUrl);
		final String lastSegment = downloadUri.getLastPathSegment();
		if (lastSegment == null)
		{
			return false;
		}
		final File destFile = new File(storage, lastSegment);
		Deploy.copy(inputStream, destFile);
		return true;
	}
}
