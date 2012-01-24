package net.trajano.gasprices;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * <p>
 * This is a utility class that is used to manage the {@link SharedPreferences}
 * used by the application. A utility class pattern was used rather than
 * employing the Wrapper pattern to prevent creation of new objects which are
 * more expensive on a mobile device at the expense of readability.
 * </p>
 * <p>
 * A non-private {@link SharedPreferences} was used because we need it to be
 * accessed by a widget which is not running in the same user space as the
 * application therefore won't be able to get it by using
 * {@link Activity#getPreferences(int)}.
 * </p>
 * 
 * @author Archimedes Trajano (developer@trajano.net)
 * 
 */
public final class PreferenceUtil {
	/**
	 * Last updated in seconds since epoch.
	 */
	private static final String LAST_UPDATED_KEY = "last_updated"; // $NON-NLS-1$

	/**
	 * {@link SharedPreferences} file name.
	 */
	private static final String SHARED_PREFERENCES_NAME = "gasprices.properties"; // $NON-NLS-1$

	/**
	 * Gets the {@link SharedPreferences} object used by the application as
	 * specified by {@link #SHARED_PREFERENCES_NAME}.
	 * 
	 * @param context
	 *            context.
	 * @return shared pr
	 */
	public static final SharedPreferences getPreferences(final Context context) {
		return context.getSharedPreferences(SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
	}

	/**
	 * This sets the #LAST_UPDATED_KEY preference to the current time.
	 * 
	 * @param editor
	 */
	public static void setLastUpdatedToNow(final Editor editor) {
		editor.putLong(LAST_UPDATED_KEY, new Date().getTime());
	}

	/**
	 * Prevent instantiation of utility class.
	 */
	private PreferenceUtil() {

	}
}
