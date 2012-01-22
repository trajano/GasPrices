package net.trajano.gasprices;

import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.RemoteViews;

public class GasPricesWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(final Context context,
			final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		final ComponentName thisWidget = new ComponentName(context,
				GasPricesWidgetProvider.class);
		final int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (final int widgetId : allWidgetIds) {
			// Create some random data
			final int number = new Random().nextInt(100);

			final RemoteViews remoteViews = new RemoteViews(
					context.getPackageName(), R.layout.widget_layout);
			Log.w("WidgetExample", String.valueOf(number));
			// Set the text
			remoteViews.setTextViewText(R.id.update, String.valueOf(number));

			final PackageManager manager = context.getPackageManager();
			final Intent intent = manager
					.getLaunchIntentForPackage("net.trajano.gasprices");
			Log.e("GasPricesWidget", "HERE! + " + intent);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			Log.e("GasPricesWidget", "HERE!");
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}
}
