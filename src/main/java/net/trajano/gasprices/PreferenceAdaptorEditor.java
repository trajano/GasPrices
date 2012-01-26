package net.trajano.gasprices;

import java.util.Date;
import java.util.Set;

import android.text.format.Time;

/**
 * This is an internal class to adapt the editor to add a few methods to
 * hide knowledge from clients what the keys are.
 * 
 * @author Archimedes Trajano (developer@trajano.net)
 * 
 */
public class PreferenceAdaptorEditor implements
		android.content.SharedPreferences.Editor {
	private final android.content.SharedPreferences.Editor editor;

	public PreferenceAdaptorEditor(final android.content.SharedPreferences.Editor editor) {
		this.editor = editor;
	}

	@Override
	public void apply() {
		editor.apply();
	}

	@Override
	public android.content.SharedPreferences.Editor clear() {
		return editor.clear();
	}

	@Override
	public boolean commit() {
		return editor.commit();
	}

	@Override
	public android.content.SharedPreferences.Editor putBoolean(
			final String key, final boolean value) {
		return editor.putBoolean(key, value);
	}

	@Override
	public android.content.SharedPreferences.Editor putFloat(
			final String key, final float value) {
		return editor.putFloat(key, value);
	}

	@Override
	public android.content.SharedPreferences.Editor putInt(
			final String key, final int value) {
		return editor.putInt(key, value);
	}

	@Override
	public android.content.SharedPreferences.Editor putLong(
			final String key, final long value) {
		return editor.putLong(key, value);
	}

	@Override
	public android.content.SharedPreferences.Editor putString(
			final String key, final String value) {
		return editor.putString(key, value);
	}

	@Override
	public android.content.SharedPreferences.Editor putStringSet(
			final String arg0, final Set<String> arg1) {
		return editor.putStringSet(arg0, arg1);
	}

	@Override
	public android.content.SharedPreferences.Editor remove(final String key) {
		return editor.remove(key);
	}

	/**
	 * Sets the last updated time preference. Internally it will convert it
	 * to a long before storing it in the shared preferences.
	 * 
	 * @param lastUpdated
	 * @return itself
	 */
	public PreferenceAdaptorEditor setLastUpdated(final Date lastUpdated) {
		putLong(PreferenceAdaptor.LAST_UPDATED_KEY, lastUpdated.getTime());
		return this;
	}

	/**
	 * Sets the last updated time preference to right now. Internally it
	 * will convert it to a long before storing it in the shared
	 * preferences.
	 * 
	 * @return itself
	 */
	public PreferenceAdaptorEditor setLastUpdatedToNow() {
		final Time time = new Time();
		time.setToNow();
		putLong(PreferenceAdaptor.LAST_UPDATED_KEY, time.normalize(false));
		return this;
	}

}