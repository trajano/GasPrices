package net.trajano.gasprices;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ListView;

public class GasPricesWidgetConfigure extends Activity {
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		public void onClick(final View v) {
			final Context context = GasPricesWidgetConfigure.this;

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
			setResult(RESULT_OK, resultValue);
			finish();
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		if (Build.PRODUCT.endsWith("sdk")
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.enableDefaults();
		}
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.widget_configuration);
		final ListView listView = (ListView) findViewById(R.id.CityList);

		findViewById(R.id.TorontoButton).setOnClickListener(mOnClickListener);

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
}