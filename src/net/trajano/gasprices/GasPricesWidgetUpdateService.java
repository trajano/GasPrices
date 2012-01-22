package net.trajano.gasprices;

import java.io.IOException;
import java.text.DecimalFormat;

import org.json.JSONException;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.RemoteViews;

public class GasPricesWidgetUpdateService extends IntentService {
	private static final Location TORONTO_LOCATION;

	static {
		TORONTO_LOCATION = new Location("app");
		TORONTO_LOCATION.setLatitude(43.6669);
		TORONTO_LOCATION.setLongitude(-79.3824);
	}

	public GasPricesWidgetUpdateService() {
		super("GasPricesWidgetUpdateIntentService");
	}

	@Override
	protected void onHandleIntent(final Intent intent) {
		Log.v("GasPrices", "handleIntent");
		final RemoteViews remoteViews = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.widget_layout);

		final ApplicationProperties props = new ApplicationProperties(
				getApplicationContext());

		if (props.isLoaded()) {
			final CityInfo info = props.getClosestCityInfo(TORONTO_LOCATION);
			remoteViews.setTextViewText(R.id.update, new DecimalFormat(
					"##0.0 '\u00A2/L'").format(info.getCurrentGasPrice()));
		} else {
			remoteViews.setTextViewText(R.id.update, new DecimalFormat(
					"##0.0 '\u00A2/L'").format(42.0));
		}

		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());

		final ComponentName thisWidget = new ComponentName(
				getApplicationContext(), GasPricesWidgetProvider.class);

		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}

	protected void xonHandleIntent(final Intent intent) {
		Log.v("GasPrices", "handleIntent");
		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());

		final RemoteViews remoteViews = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.widget_layout);
		final ApplicationProperties props = new ApplicationProperties(
				getApplicationContext());

		Log.v("GasPrices", "properties loaded");
		if (props.isUpdateRequired()) {
			try {
				props.update();
			} catch (final JSONException e) {
				Log.e("GasPrices", e.getMessage());
				return;
			} catch (final IOException e) {
				Log.e("GasPrices", e.getMessage());
				return;
			}
		}
		// ComponentName thisWidget = new ComponentName(getApplicationContext(),
		// MyWidget.class);
		// remoteViews.setTextViewText(R.id.my_text_view, "myText" +
		// System.currentTimeMillis());
		// appWidgetManager.updateAppWidget(thisWidget, remoteViews);

	}

	public int xonStartCommand(final Intent intent, final int flags,
			final int startId) {
		Log.v("GasPrices", "widget service start");
		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());

		final int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		final RemoteViews remoteViews = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.widget_layout);

		new LoadWidgetTask(getApplicationContext()).execute();

		for (final int widgetId : allWidgetIds) {
			// TODO this looks wrong over here I think it should've been in the
			// provider itself.
			final PackageManager manager = getApplicationContext()
					.getPackageManager();
			final Intent lintent = manager
					.getLaunchIntentForPackage("net.trajano.gasprices");
			lintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					getApplicationContext(), 0, lintent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

		}
		return START_NOT_STICKY;
	}
}
