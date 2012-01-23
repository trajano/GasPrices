package net.trajano.gasprices;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.RemoteViews;

public class GasPricesWidgetProvider extends AppWidgetProvider {
	@Override
	public void onDeleted(final Context context, final int[] appWidgetIds) {
		final SharedPreferences preferences = context.getSharedPreferences(
				ApplicationProperties.FILE_NAME, Context.MODE_PRIVATE);
		final Editor e = preferences.edit();
		for (final int widgetId : appWidgetIds) {
			e.remove(GasPricesWidgetConfigure.PREF_PREFIX_CITY_ID + widgetId);
		}
	}

	@Override
	public void onUpdate(final Context context,
			final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		Log.v("GasPrices", "onUpdate() received");
		final ComponentName thisWidget = new ComponentName(context,
				GasPricesWidgetProvider.class);

		final RemoteViews remoteViews = new RemoteViews(
				context.getPackageName(), R.layout.widget_layout);

		{
			Log.v("GasPrices", "Set up intent to launch on widget click");
			final PackageManager manager = context.getPackageManager();
			final Intent lintent = manager
					.getLaunchIntentForPackage("net.trajano.gasprices");
			lintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, lintent, PendingIntent.FLAG_UPDATE_CURRENT);

			remoteViews.setOnClickPendingIntent(R.id.thelayout, pendingIntent);
		}
		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
		{
			// Build the intent to call the service
			final Intent intent = new Intent(context.getApplicationContext(),
					GasPricesWidgetUpdateService.class);
			context.startService(intent);
		}
	}
}
