package net.trajano.gasprices;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
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

		final String[] values = new String[] { "Android", "iPhone",
				"WindowsMobile", "Blackberry", "WebOS", "Ubuntu", "Windows7",
				"Max OS X", "Linux", "OS/2" };
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		final String item = (String) getListAdapter().getItem(position);
		final Intent intent = new Intent();
		intent.putExtra("city", item);
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