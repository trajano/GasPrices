package net.trajano.gasprices;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuInflater;
import android.view.MenuItem;

public class GasPricesActivity extends Activity {
	/**
	 * When the preference change on lastUpdated is detected it will update the
	 * view.
	 */
	private final OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				final SharedPreferences sharedPreferences, final String key) {
			if (key != "last_updated" || key != "selected_city_id") {
				return;
			}
			updateView();
		}
	};

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
		setMainView();
	}

	@Override
	public boolean onCreateOptionsMenu(final android.view.Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle item selection
		if (R.id.UpdateMenuItem == item.getItemId()) {
			final GasPricesViewWrapper view = new GasPricesViewWrapper(this,
					null);
			view.setStatus("Forced update...");

			// TODO don't do this! create a new AsyncTask instead.
			final Intent intent = new Intent(this, GasPricesUpdateService.class);
			startService(intent);
			return true;
		} else if (R.id.ShowFeedData == item.getItemId()) {
			final Intent intent = new Intent(this, GasPricesFeedActivity.class);
			startActivity(intent);
			return true;
		} else if (R.id.CitySelectMenuItem == item.getItemId()) {
			final Intent intent = new Intent(this, CitySelectionActivity.class);
			startActivityForResult(intent, 1);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * When the application pauses, it deregisters itself from listening to
	 * changes.
	 */
	@Override
	protected void onPause() {
		preferences
				.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
		super.onPause();
	}

	/**
	 * When the application resumes it will update the view and wait for any
	 * preference changes.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		updateView();
		preferences
				.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
	}

	private void setMainView() {
		setContentView(R.layout.main);
		new LoadDataTask(this).execute();
		// final Button refreshButton = (Button)
		// findViewById(R.id.RefreshButton);
		// final Button debugButton = (Button) findViewById(R.id.DebugButton);
		// // final Button userButton = (Button) findViewById(R.id.UserButton);
		// refreshButton.setOnClickListener(this);
		// debugButton.setOnClickListener(this);
	}

	/**
	 * Updates the view with the information stored in the {@link #preferences}
	 * object.
	 */
	private void updateView() {
		final GasPricesViewWrapper view = new GasPricesViewWrapper(this,
				new ApplicationProperties(this));
		view.updateView();
	}
}