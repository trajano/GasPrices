package net.trajano.gasprices;

import java.text.DecimalFormat;

import org.json.JSONException;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class GasPricesWidgetUpdateService extends Service {
	private static final Location TORONTO_LOCATION;

	static {
		TORONTO_LOCATION = new Location("app");
		TORONTO_LOCATION.setLatitude(43.6669);
		TORONTO_LOCATION.setLongitude(-79.3824);
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return null;
	}

	@Override
	public void onStart(final Intent intent, final int startId) {
		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());

		final int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		final ApplicationProperties props = new ApplicationProperties(this);
		Log.e("GasPrices", "props = " + props);

		for (final int widgetId : allWidgetIds) {
			final RemoteViews remoteViews = new RemoteViews(
					getApplicationContext().getPackageName(),
					R.layout.widget_layout);
			// TODO show the difference and adjust the color of the widget if
			// needed
			try {
				remoteViews.setTextViewText(R.id.update, new DecimalFormat(
						"##0.0").format(props.getClosestCityInfo(
						TORONTO_LOCATION).getCurrentGasPrice()));
			} catch (final JSONException e) {
				remoteViews.setTextViewText(R.id.update, getResources()
						.getText(R.string.default_price));
			}
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
	}
}
