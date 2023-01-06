/*
 * Copyright (c) 2019-2023. Bernard Bou
 */

package org.treebolic.files;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class StorageExplorer
{
	// private static final String TAG = "StorageExplorer";

	/**
	 * Storage types
	 */
	enum StorageType
	{PRIMARY_EMULATED, PRIMARY_PHYSICAL, SECONDARY}

	/**
	 * Directory type
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	public enum DirType
	{
		AUTO, APP_EXTERNAL_SECONDARY, APP_EXTERNAL_PRIMARY, PUBLIC_EXTERNAL_SECONDARY, PUBLIC_EXTERNAL_PRIMARY, APP_INTERNAL;

		/**
		 * Compare (sort by preference)
		 *
		 * @param type1 type 1
		 * @param type2 type 2
		 * @return order
		 */
		@SuppressWarnings("WeakerAccess")
		static public int compare(@NonNull final DirType type1, @NonNull final DirType type2)
		{
			int i1 = type1.ordinal();
			int i2 = type2.ordinal();
			return Integer.compare(i1, i2);
		}

		@SuppressWarnings("WeakerAccess")
		@NonNull
		public String toDisplay()
		{
			switch (this)
			{
				case AUTO:
					return "auto (internal or adopted)";
				case APP_EXTERNAL_SECONDARY:
					return "secondary";
				case APP_EXTERNAL_PRIMARY:
					return "primary";
				case PUBLIC_EXTERNAL_PRIMARY:
					return "public primary";
				case PUBLIC_EXTERNAL_SECONDARY:
					return "public secondary";
				case APP_INTERNAL:
					return "internal";
			}
			return "unexpected";
		}
	}

	/**
	 * Directory with type
	 *
	 * @author <a href="mailto:1313ou@gmail.com">Bernard Bou</a>
	 */
	@SuppressWarnings("WeakerAccess")
	static public class Directory implements Comparable<Directory>
	{
		private final File file;

		private final DirType type;

		Directory(final File file, final DirType type)
		{
			this.file = file;
			this.type = type;
		}

		DirType getType()
		{
			return this.type;
		}

		@NonNull
		CharSequence getValue()
		{
			if (DirType.AUTO == this.type)
			{
				return DirType.AUTO.toString();
			}
			return this.file.getAbsolutePath();
		}

		@SuppressWarnings("WeakerAccess")
		public File getFile()
		{
			return this.file;
		}

		@Override
		public int hashCode()
		{
			return getType().hashCode() * 7 + getValue().hashCode() * 13;
		}

		@Override
		public boolean equals(Object d2)
		{
			return d2 instanceof Directory && this.getType().equals(((Directory) d2).getType());
		}

		@Override
		public int compareTo(@NonNull final Directory d2)
		{
			int t = DirType.compare(this.getType(), d2.getType());
			if (t != 0)
			{
				return t;
			}
			return this.getValue().toString().compareTo(d2.getValue().toString());
		}
	}

	/**
	 * Get external storage directories
	 *
	 * @param context context
	 * @return map per type of of external storage directories
	 */
	@NonNull
	static public Map<StorageType, String[]> getStorageDirectories(@NonNull final Context context)
	{
		// result set of paths
		final Map<StorageType, String[]> dirs = new EnumMap<>(StorageType.class);

		// P R I M A R Y

		// primary emulated sdcard
		final String emulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
		if (emulatedStorageTarget != null && !emulatedStorageTarget.isEmpty())
		{
			// device has emulated extStorage; external extStorage paths should have userId burned into them.
			final String userId = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 ? "" : getUserId(context);

			// /extStorage/emulated/0[1,2,...]
			if (userId.isEmpty())
			{
				dirs.put(StorageType.PRIMARY_PHYSICAL, new String[]{emulatedStorageTarget});
			}
			else
			{
				dirs.put(StorageType.PRIMARY_PHYSICAL, new String[]{emulatedStorageTarget + File.separatorChar + userId});
			}
		}
		else
		{
			// primary physical sdcard (not emulated)
			final String externalStorage = System.getenv("EXTERNAL_STORAGE");

			// device has physical external extStorage; use plain paths
			if (externalStorage != null && !externalStorage.isEmpty())
			{
				dirs.put(StorageType.PRIMARY_EMULATED, new String[]{externalStorage});
			}
			else
			{
				// EXTERNAL_STORAGE undefined; falling back to default.
				dirs.put(StorageType.PRIMARY_EMULATED, new String[]{"/extStorage/sdcard0"});
			}
		}

		// S E C O N D A R Y

		// all secondary sdcards (all exclude primary) separated by ":"
		final String secondaryStoragesStr = System.getenv("SECONDARY_STORAGE");

		// add all secondary storages
		if (secondaryStoragesStr != null && !secondaryStoragesStr.isEmpty())
		{
			// all secondary sdcards split into array
			final String[] secondaryStorages = secondaryStoragesStr.split(File.pathSeparator);
			if (secondaryStorages.length > 0)
			{
				dirs.put(StorageType.SECONDARY, secondaryStorages);
			}
		}

		return dirs;
	}

	/**
	 * Get directories as types and values
	 *
	 * @param context context
	 * @return pair of types and values
	 */
	@NonNull
	static public Pair<CharSequence[], CharSequence[]> getDirectoriesTypesValues(@NonNull final Context context)
	{
		final List<CharSequence> types = new ArrayList<>();
		final List<CharSequence> values = new ArrayList<>();
		final Collection<Directory> dirs = StorageExplorer.getDirectories(context);
		for (Directory dir : dirs)
		{
			// types
			types.add(dir.getType().toDisplay());

			// value
			values.add(dir.getFile().getAbsolutePath());
		}
		return new Pair<>(types.toArray(new CharSequence[0]), values.toArray(new CharSequence[0]));
	}

	/**
	 * Get list of directories
	 *
	 * @param context context
	 * @return list of storage directories
	 */
	@NonNull
	@TargetApi(Build.VERSION_CODES.KITKAT)
	static private Collection<Directory> getDirectories(@NonNull final Context context)
	{
		final String[] tags = {Environment.DIRECTORY_PODCASTS, Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_ALARMS, Environment.DIRECTORY_NOTIFICATIONS, Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_MOVIES, Environment.DIRECTORY_DOWNLOADS, Environment.DIRECTORY_DCIM};

		final Set<Directory> result = new TreeSet<>();
		File dir;

		// P U B L I C

		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			final File[] externalMediaDirs = context.getExternalMediaDirs();
			if (externalMediaDirs != null)
			{
				for (File mediaStorage : externalMediaDirs)
				{
					//result.add(new Directory(mediaStorage, DirType.PUBLIC_EXTERNAL_SECONDARY));
				}
			}
		}
  	    */

		// top-level public external storage directory
		for (String tag : tags)
		{
			dir = Environment.getExternalStoragePublicDirectory(tag);
			if (dir.exists())
			{
				result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
			}
		}
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
		{
			dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
			if (dir.exists())
			{
				result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
			}
		}

		// top-level public in external
		dir = Environment.getExternalStorageDirectory();
		if (dir != null)
		{
			if (dir.exists())
			{
				result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
			}
		}

		// S E C O N D A R Y

		// all secondary sdcards split into array
		final File[] secondaries = discoverSecondaryExternalStorage();
		if (secondaries != null)
		{
			for (File secondary : secondaries)
			{
				dir = secondary;
				if (dir.exists())
				{
					result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_SECONDARY));
				}
			}
		}

		// P R I M A R Y

		// primary emulated sdcard
		dir = discoverPrimaryEmulatedExternalStorage(context);
		if (dir != null)
		{
			if (dir.exists())
			{
				result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
			}
		}

		dir = discoverPrimaryPhysicalExternalStorage();
		if (dir != null)
		{
			if (dir.exists())
			{
				result.add(new Directory(dir, DirType.PUBLIC_EXTERNAL_PRIMARY));
			}
		}

		result.add(new Directory(new File("/storage"), DirType.PUBLIC_EXTERNAL_PRIMARY));
		return result;
	}

	/**
	 * Discover external storage
	 *
	 * @param context context
	 * @return (cached) external storage directory
	 */
	@Nullable
	static public String discoverExternalStorage(@NonNull final Context context)
	{
		// S E C O N D A R Y

		// all secondary sdcards (all exclude primary) separated by ":"
		final String secondaryStoragesStr = System.getenv("SECONDARY_STORAGE");

		// add all secondary storages
		if (secondaryStoragesStr != null && !secondaryStoragesStr.isEmpty())
		{
			// all secondary sdcards split into array
			final String[] secondaryStorages = secondaryStoragesStr.split(File.pathSeparator);
			if (secondaryStorages.length > 0)
			{
				return secondaryStorages[0];
			}
		}

		// P R I M A R Y E M U L A T E D

		// primary emulated sdcard
		final String emulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
		if (emulatedStorageTarget != null && !emulatedStorageTarget.isEmpty())
		{
			// device has emulated extStorage; external extStorage paths should have userId burned into them.
			final String userId = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 ? "" : getUserId(context);

			// /extStorage/emulated/0[1,2,...]
			if (!userId.isEmpty())
			{
				return emulatedStorageTarget + File.separatorChar + userId;
			}
			return emulatedStorageTarget;
		}

		// P R I M A R Y N O N E M U L A T E D

		// primary physical sdcard (not emulated)
		final String externalStorage = System.getenv("EXTERNAL_STORAGE");

		// device has physical external extStorage; use plain paths.
		if (externalStorage != null && !externalStorage.isEmpty())
		{
			return externalStorage;
		}

		// EXTERNAL_STORAGE undefined; falling back to default.
		// return "/extStorage/sdcard0";

		return null;
	}

	/**
	 * Discover primary emulated external storage directory
	 *
	 * @param context context
	 * @return primary emulated external storage directory
	 */
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Nullable
	static public File discoverPrimaryEmulatedExternalStorage(@NonNull final Context context)
	{
		// primary emulated sdcard
		final String emulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
		if (emulatedStorageTarget != null && !emulatedStorageTarget.isEmpty())
		{
			// device has emulated extStorage
			// external extStorage paths should have userId burned into them
			final String userId = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 ? "" : getUserId(context);

			// /extStorage/emulated/0[1,2,...]
			if (userId.isEmpty())
			{
				return new File(emulatedStorageTarget);
			}
			else
			{
				return new File(emulatedStorageTarget + File.separatorChar + userId);
			}
		}
		return null;
	}

	/**
	 * Discover primary physical external storage directory
	 *
	 * @return primary physical external storage directory
	 */
	@Nullable
	static public File discoverPrimaryPhysicalExternalStorage()
	{
		final String externalStorage = System.getenv("EXTERNAL_STORAGE");
		// device has physical external extStorage; use plain paths.
		if (externalStorage != null && !externalStorage.isEmpty())
		{
			return new File(externalStorage);
		}

		return null;
	}

	/**
	 * Discover secondary external storage directories
	 *
	 * @return secondary external storage directories
	 */
	@Nullable
	static public File[] discoverSecondaryExternalStorage()
	{
		// all secondary sdcards (all except primary) separated by ":"
		String secondaryStoragesEnv = System.getenv("SECONDARY_STORAGE");
		if ((secondaryStoragesEnv == null) || secondaryStoragesEnv.isEmpty())
		{
			secondaryStoragesEnv = System.getenv("EXTERNAL_SDCARD_STORAGE");
		}

		// addItem all secondary storages
		if (secondaryStoragesEnv != null && !secondaryStoragesEnv.isEmpty())
		{
			// all secondary sdcards split into array
			final String[] paths = secondaryStoragesEnv.split(File.pathSeparator);
			final List<File> dirs = new ArrayList<>();
			for (String path : paths)
			{
				final File dir = new File(path);
				if (dir.exists())
				{
					dirs.add(dir);
				}
			}
			return dirs.toArray(new File[0]);
		}
		return null;
	}

	/**
	 * User id
	 *
	 * @param context context
	 * @return user id
	 */
	@NonNull
	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	static private String getUserId(@NonNull final Context context)
	{
		final UserManager manager = (UserManager) context.getSystemService(Context.USER_SERVICE);
		if (null != manager)
		{
			UserHandle user = android.os.Process.myUserHandle();
			long userSerialNumber = manager.getSerialNumberForUser(user);
			// Log.d("USER", "userSerialNumber = " + userSerialNumber);
			return Long.toString(userSerialNumber);
		}
		return "";
	}
}
