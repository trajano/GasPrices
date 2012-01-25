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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GasPricesActivity extends Activity implements OnClickListener {
	/**
	 * When the preference change on lastUpdated is detected it will update the
	 * view.
	 */
	private final OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				final SharedPreferences sharedPreferences, final String key) {
			if (key != "last_updated") {
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
	 * The click handler. TODO very yucky having it here, it should be on its
	 * own class per button.
	 */
	@Override
	public void onClick(final View v) {
		final Button refreshButton = (Button) findViewById(R.id.RefreshButton);
		final Button debugButton = (Button) findViewById(R.id.DebugButton);
		final Button userButton = (Button) findViewById(R.id.UserButton);
		if (v == refreshButton) {
			final GasPricesViewWrapper view = new GasPricesViewWrapper(this,
					null);
			view.setStatus("Forced update...");
			new UpdateDataTask(this).execute();
		} else if (v == debugButton) {
			setFeedView();
		} else if (v == userButton) {
			setMainView();
		}
	}

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
		preferences
				.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
	}

	@Override
	public boolean onCreateOptionsMenu(final android.view.Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		preferences
				.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.UpdateMenuItem:
			final GasPricesViewWrapper view = new GasPricesViewWrapper(this,
					null);
			view.setStatus("Forced update...");

			// TODO don't do this! create a new AsyncTask instead.
			final Intent startIntent = new Intent(this,
					GasPricesUpdateService.class);
			startService(startIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setFeedView() {
		setContentView(R.layout.feed);
		new LoadDataTask(this).execute();
		final Button refreshButton = (Button) findViewById(R.id.RefreshButton);
		final Button userButton = (Button) findViewById(R.id.UserButton);
		refreshButton.setOnClickListener(this);
		userButton.setOnClickListener(this);
	}

	private void setMainView() {
		setContentView(R.layout.main);
		new LoadDataTask(this).execute();
		final Button refreshButton = (Button) findViewById(R.id.RefreshButton);
		final Button debugButton = (Button) findViewById(R.id.DebugButton);
		// final Button userButton = (Button) findViewById(R.id.UserButton);
		refreshButton.setOnClickListener(this);
		debugButton.setOnClickListener(this);
	}

	/**
	 * Updates the view with the information stored in the {@link #preferences}
	 * object.
	 */
	private void updateView() {
		final GasPricesViewWrapper view = new GasPricesViewWrapper(this,
				new ApplicationProperties(this));
		view.updateView();
		view.setStatus("updated via sharedPreferenceUpdate");
	}
}