package net.trajano.gasprices;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class GasPricesWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(final Context context,
			final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
		final ComponentName thisWidget = new ComponentName(context,
				GasPricesWidgetProvider.class);
		final int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service
		final Intent intent = new Intent(context.getApplicationContext(),
				GasPricesWidgetUpdateService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
		context.startService(intent);
	}
}
