package net.trajano.gasprices;

import java.text.DateFormat;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

@EActivity(R.layout.main)
@SuppressLint("Registered")
@OptionsMenu(R.menu.menu)
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

	@OptionsItem(R.id.CitySelectMenuItem)
	void citySelectSelected() {
		final Intent intent = new Intent(this, CitySelectionActivity.class);
		startActivityForResult(intent, 1);
	}

	@AfterViews
	void enableStrictMode() {
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}
	}

	@OptionsItem(R.id.UpdateMenuItem)
	void forceUpdateSelected() {
		forcedUpdateDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.loading), true);
		new UpdateTask(this).execute();
	}

	/**
	 * Called when the activity is first created.
	 */
	@AfterViews
	void initialLoad() {
		preferences = new PreferenceAdaptor(this);
		if (!preferences.isDataPresent() || preferences.isUpdateNeeded()) {
			forcedUpdateDialog = ProgressDialog.show(this, "", getResources()
					.getString(R.string.loading), true);
			new UpdateTask(this).execute();
		}
		GasPricesUpdateService.scheduleUpdate(this);
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
	 * preference changes. If it has widget IDs associated with then it will set
	 * the current selected city to the one associated with the widget.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		final Uri widgetIdUri = getIntent().getData();
		if (widgetIdUri != null) {
			Log.d("GasPrices", "Resumed from widget id = " + widgetIdUri);
			final int widgetId = Integer.parseInt(widgetIdUri.getPath());
			final PreferenceAdaptorEditor editor = preferences.edit();
			final long cityId = preferences.getWidgetCityId(widgetId);
			Log.d("GasPrices", "widget id = " + widgetId + " has city "
					+ cityId);
			editor.setSelectedCityId(cityId);
			editor.commit();
		}
		updateView();
		preferences
				.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
	}

	@OptionsItem(R.id.ShowFeedData)
	void showFeedDataSelected() {
		GasPricesFeedActivity_.intent(this).start();
	}

	/**
	 * Updates the view with the information stored in the {@link #preferences}
	 * object. It will also dismiss the {@link #forcedUpdateDialog} if it is
	 * visible.
	 */
	private void updateView() {
		if (preferences.isError() && forcedUpdateDialog != null) {
			forcedUpdateDialog.dismiss();
			forcedUpdateDialog = null;
			return;
		}

		if (!preferences.isDataPresent()) {
			return;
		}
		final CityInfo cityInfo = preferences.getSelectedCityInfo();

		{
			final TextView v = (TextView) findViewById(R.id.LastUpdatedText);
			v.setText(getResources().getString(
					R.string.last_updated,
					DateFormat
							.getDateTimeInstance(DateFormat.LONG,
									DateFormat.LONG)
							.format(preferences.getLastUpdated())
							.replace(' ', '\u00A0')));
		}
		{
			final TextView v = (TextView) findViewById(R.id.city);
			v.setText(cityInfo.getName());
		}
		{
			final TextView v = (TextView) findViewById(R.id.PriceTodayText);
			v.setText(getResources().getString(R.string.cents_per_liter_format,
					cityInfo.getCurrentGasPrice()));
		}
		if (cityInfo.isTomorrowsGasPriceAvailable()) {
			final TextView v = (TextView) findViewById(R.id.OtherPriceLabelText);
			v.setText(R.string.tomorrow);
			final TextView vp = (TextView) findViewById(R.id.OtherPriceText);
			vp.setText(getResources().getString(
					R.string.cents_per_liter_format,
					cityInfo.getTomorrowsGasPrice()));
		}
		if (cityInfo.isYesterdaysGasPriceAvailable()) {
			final TextView v = (TextView) findViewById(R.id.OtherPriceLabelText);
			v.setText(R.string.yesterday);
			final TextView vp = (TextView) findViewById(R.id.OtherPriceText);
			vp.setText(getResources().getString(
					R.string.cents_per_liter_format,
					cityInfo.getYesterdaysGasPrice()));
		}

		{
			final TextView v = (TextView) findViewById(R.id.GasPriceStatusText);
			v.setText(getResources().getString(
					R.string.next_update,
					DateFormat
							.getDateTimeInstance(DateFormat.LONG,
									DateFormat.LONG)
							.format(preferences.getNextUpdateDate())
							.replace(' ', '\u00A0')));

		}
		if (forcedUpdateDialog != null) {
			forcedUpdateDialog.dismiss();
			forcedUpdateDialog = null;
		}
	}
}