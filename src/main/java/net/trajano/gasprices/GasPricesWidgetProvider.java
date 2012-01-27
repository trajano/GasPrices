package net.trajano.gasprices;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.RemoteViews;

public class GasPricesWidgetProvider extends AppWidgetProvider {
	@Override
	public void onDeleted(final Context context, final int[] appWidgetIds) {
		final PreferenceAdaptor preferences = new PreferenceAdaptor(context);
		final PreferenceAdaptorEditor editor = preferences.edit();
		editor.removeWidgetCityId(appWidgetIds);
		editor.apply();
	}

	@Override
	public void onUpdate(final Context context,
			final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		Log.d("GasPrices", "onUpdate() received");

		final RemoteViews remoteViews = new RemoteViews(
				context.getPackageName(), R.layout.widget_layout);

		for (final int appWidgetId : appWidgetIds) {
			final PreferenceAdaptor preferences = new PreferenceAdaptor(context);
			preferences.getWidgetCityInfo(appWidgetId);
			remoteViews.setTextViewText(R.id.widget_city,
					preferences.getWidgetCityName(appWidgetId));

			final PackageManager manager = context.getPackageManager();
			final Intent lintent = manager
					.getLaunchIntentForPackage("net.trajano.gasprices");
			lintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, lintent, PendingIntent.FLAG_UPDATE_CURRENT);

			remoteViews.setOnClickPendingIntent(R.id.thelayout, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
		// {
		// // Build the intent to call the service
		// final Intent intent = new Intent(context.getApplicationContext(),
		// GasPricesWidgetUpdateService.class);
		// context.startService(intent);
		// }
	}
}
