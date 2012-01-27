package net.trajano.gasprices;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

public class GasPricesWidgetProvider extends AppWidgetProvider {
	private static void setBlue(final RemoteViews remoteViews) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
					R.drawable.myshape);
		}
	}

	private static void setGreen(final RemoteViews remoteViews) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
					R.drawable.myshape_green);
		}
	}

	private static void setRed(final RemoteViews remoteViews) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
					R.drawable.myshape_red);
		}
	}

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

		final PreferenceAdaptor preferences = new PreferenceAdaptor(context);

		for (final int appWidgetId : appWidgetIds) {
			final CityInfo city = preferences.getWidgetCityInfo(appWidgetId);
			remoteViews.setTextViewText(R.id.widget_city, city.getName());
			remoteViews.setTextViewText(
					R.id.widget_price,
					context.getResources().getString(
							R.string.widget_price_format,
							city.getCurrentGasPrice()));
			setBlue(remoteViews);
			if (city.isTomorrowsGasPriceAvailable()) {
				if (city.isTomorrowsGasPriceUp()) {
					setRed(remoteViews);
					remoteViews.setTextViewText(
							R.id.widget_price_change,
							context.getResources().getString(
									R.string.widget_price_change_up_format,
									city.getPriceDifferenceAbsoluteValue()));
				} else if (city.isTomorrowsGasPriceDown()) {
					setGreen(remoteViews);
					remoteViews.setTextViewText(
							R.id.widget_price_change,
							context.getResources().getString(
									R.string.widget_price_change_down_format,
									city.getPriceDifferenceAbsoluteValue()));
				} else {
					remoteViews.setTextViewText(
							R.id.widget_price_change,
							context.getResources().getString(
									R.string.widget_price_unchanged));
				}
			}

			final PackageManager manager = context.getPackageManager();
			final Intent lintent = manager
					.getLaunchIntentForPackage("net.trajano.gasprices");
			lintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			final PendingIntent pendingIntent = PendingIntent.getActivity(
					context, 0, lintent, PendingIntent.FLAG_UPDATE_CURRENT);

			remoteViews.setOnClickPendingIntent(R.id.thelayout, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
		if (preferences.isUpdateNeeded()) {
			// Build the intent to call the service
			final Intent intent = new Intent(context.getApplicationContext(),
					GasPricesUpdateService.class);
			context.startService(intent);
		}
	}

}
