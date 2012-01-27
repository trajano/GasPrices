package net.trajano.gasprices;

import java.text.DateFormat;
import java.text.MessageFormat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class GasPricesActivity extends Activity {
	/**
	 * Forced update progress dialog.
	 */
	private ProgressDialog forcedUpdateDialog;

	/**
	 * When the preference change on lastUpdated is detected it will update the
	 * view.
	 */
	private final OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				final SharedPreferences sharedPreferences, final String key) {
			if (!PreferenceAdaptor.isKeyAffectGasPricesView(key)) {
				return;
			}
			updateView();
		}
	};

	/**
	 * Preference data, stored in memory until destruction.
	 */
	private PreferenceAdaptor preferences;

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

		preferences = new PreferenceAdaptor(this);
		setContentView(R.layout.main);
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
			forcedUpdateDialog = ProgressDialog.show(this, "", getResources()
					.getString(R.string.loading), true);
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

	/**
	 * Updates the view with the information stored in the {@link #preferences}
	 * object. It will also dismiss the {@link #forcedUpdateDialog} if it is
	 * visible.
	 */
	private void updateView() {
		if (!preferences.isDataPresent()) {
			forcedUpdateDialog = ProgressDialog.show(this, "", getResources()
					.getString(R.string.loading), true);
			// TODO don't do this! create a new AsyncTask instead.
			final Intent intent = new Intent(this, GasPricesUpdateService.class);
			startService(intent);
			return;
		}
		final GasPricesViewWrapper view = new GasPricesViewWrapper(this,
				new ApplicationProperties(this));
		view.updateView();
		{
			final TextView v = (TextView) findViewById(R.id.LastUpdatedText);
			v.setText(MessageFormat.format(
					getResources().getString(R.string.last_updated),
					DateFormat
							.getDateTimeInstance(DateFormat.LONG,
									DateFormat.LONG)
							.format(preferences.getLastUpdated())
							.replace(' ', '\u00A0')));
		}
		if (forcedUpdateDialog != null) {
			forcedUpdateDialog.dismiss();
			forcedUpdateDialog = null;
		}
	}
}