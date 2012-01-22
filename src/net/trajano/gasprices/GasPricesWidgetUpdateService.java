package net.trajano.gasprices;

import java.util.Random;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class GasPricesWidgetUpdateService extends Service {
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

		for (final int widgetId : allWidgetIds) {
			// Create some random data
			final int number = new Random().nextInt(100);

			final RemoteViews remoteViews = new RemoteViews(
					getApplicationContext().getPackageName(),
					R.layout.widget_layout);
			Log.w("WidgetExample", String.valueOf(number));
			// Set the text
			remoteViews.setTextViewText(R.id.update, String.valueOf(number));

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
