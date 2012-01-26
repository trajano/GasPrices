package net.trajano.gasprices;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ListView;

/**
 * This activity is shown to display a list of valid cities and allow the user
 * to select one.
 * 
 * @author Archimedes Trajano (developer@trajano.net)
 * 
 */
public class CitySelectionActivity extends ListActivity {
	/**
	 * Preference data, stored in memory until destruction.
	 */
	private SharedPreferences preferences;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.PRODUCT.endsWith("sdk")
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.enableDefaults();
		}
		preferences = PreferenceUtil.getPreferences(this);
		setListAdapter(new CityListAdapter(this));
	}

	/**
	 * When the item is selected, it updates the selected city preference rather
	 * than sending the data back. It is this activity that is setting what it
	 * wants
	 */
	@Override
	protected void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		final Editor editor = preferences.edit();
		editor.putLong("selected_city_id", id);
		editor.putString("selected_city_name", (String) getListAdapter()
				.getItem(position));
		editor.apply();
		final Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}