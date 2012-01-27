package net.trajano.gasprices;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ListView;

public class GasPricesWidgetConfigurationActivity extends ListActivity {
	public static final String PREF_PREFIX_CITY_ID = "widget.city.id_";

	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			final Context context = GasPricesWidgetConfigurationActivity.this;

			// Push widget update to surface with newly set prefix
			final AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			// GasPricesWidgetProvider.updateAppWidget(context,
			// appWidgetManager,
			// mAppWidgetId, 0);

			// Make sure we pass back the original appWidgetId
			final Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					mAppWidgetId);
			saveWidgetPref(context, mAppWidgetId, 133);
			setResult(RESULT_OK, resultValue);
			finish();
		}
	};

	/**
	 * Preference data, stored in memory until destruction.
	 */
	private PreferenceAdaptor preferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.PRODUCT.endsWith("sdk")
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.enableDefaults();
		}
		setResult(RESULT_CANCELED);

		preferences = new PreferenceAdaptor(this);
		setListAdapter(new CityListAdapter(this));

		// listView.set
		final Intent intent = getIntent();
		final Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

	}

	/**
	 * When the item is selected, it updates the selected city preference rather
	 * than sending the data back. It is this activity that is setting what it
	 * wants
	 */
	@Override
	protected void onListItemClick(final ListView l, final View v,
			final int position, final long id) {

		// Push widget update to surface with newly set prefix
		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(this);
		// GasPricesWidgetProvider.updateAppWidget(context,
		// appWidgetManager,
		// mAppWidgetId, 0);

		// Make sure we pass back the original appWidgetId
		final Intent intent = new Intent();
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		final PreferenceAdaptorEditor editor = preferences.edit();
		editor.saveWidgetCityId(mAppWidgetId, id);
		editor.apply();
		setResult(RESULT_OK, intent);
		finish();

	}

	private void saveWidgetPref(final Context context, final int appWidgetId,
			final int cityId) {
		final SharedPreferences.Editor prefs = context.getSharedPreferences(
				ApplicationProperties.FILE_NAME, MODE_PRIVATE).edit();
		prefs.putInt(PREF_PREFIX_CITY_ID + appWidgetId, cityId);
		prefs.commit();
	}
}