package net.trajano.gasprices;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;

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
	 * Widget preference key prefix.
	 */
	private static final String WIDGET_PREFERENCE_KEY_PREFIX = "widget_"; // $NON-NLS-1$

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
	 * This returns the next update date given the last update data. There are
	 * three times for updates: 5pm, 8pm and midnight.
	 * 
	 * @param lastUpdate
	 *            last update time
	 * @return the next update time.
	 */
	public static Date nextUpdateDate(final Date lastUpdate) {
		final Time t = new Time();
		t.set(lastUpdate.getTime());
		if (t.hour < 17) {
			t.hour = 17;
			t.minute = 0;
			t.second = 0;
		} else if (t.hour < 20) {
			t.hour = 20;
			t.minute = 0;
			t.second = 0;
		} else {
			t.monthDay += 1;
			t.hour = 0;
			t.minute = 0;
			t.second = 0;
		}
		return new Date(t.normalize(false));
	}

	/**
	 * This sets the #LAST_UPDATED_KEY preference to the current time.
	 * 
	 * TODO this does not seem right doing it here, this business logic should
	 * be moved to a separate wrapper. Perhaps I would need two versions of the
	 * wrapper, one mutable one not?
	 * 
	 * @param editor
	 */
	public static void setLastUpdatedToNow(final Editor editor) {
		editor.putLong(LAST_UPDATED_KEY, new Date().getTime());
	}

	/**
	 * Sets a widget preference.
	 * 
	 * @param editor
	 *            an editor
	 * @param widgetId
	 *            the widget ID
	 * @param preferenceData
	 *            preference data to store.
	 */
	public static void setWidgetPreference(final Editor editor,
			final int widgetId, final String preferenceData) {
		editor.putString(WIDGET_PREFERENCE_KEY_PREFIX, preferenceData);
	}

	/**
	 * Prevent instantiation of utility class.
	 */
	private PreferenceUtil() {

	}
}
