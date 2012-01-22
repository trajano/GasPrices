package net.trajano.gasprices;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;

import org.json.JSONException;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.RemoteViews;

public class GasPricesWidgetUpdateService extends IntentService {
	private static final String CPL = "##0.0 '\u00A2/L'";
	private static final String CPL_DOWN = "down ##0.0 '\u00A2/L'";
	private static final String CPL_UP = "up ##0.0 '\u00A2/L'";
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
			if (props.isUpdateRequired()) {
				try {
					props.update();
				} catch (final IOException e) {

				} catch (final JSONException e) {

				}
			}
			final CityInfo info = props.getClosestCityInfo(TORONTO_LOCATION);
			if (info.isTomorrowsGasPriceAvailable()) {
				if (info.getPriceDifference() == 0.00) {
					remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
							R.drawable.myshape);
					remoteViews.setTextViewText(R.id.change, "unchanged");
				} else if (info.getPriceDifference() > 0) {
					remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
							R.drawable.myshape_red);
					remoteViews.setTextViewText(R.id.update, new DecimalFormat(
							CPL_UP).format(info
							.getPriceDifferenceAbsoluteValue()));
				} else {
					remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
							R.drawable.myshape_green);
					remoteViews.setTextViewText(R.id.update, new DecimalFormat(
							CPL_DOWN).format(info
							.getPriceDifferenceAbsoluteValue()));
				}
			} else {
				remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
						R.drawable.myshape);
				remoteViews.setTextViewText(R.id.change,
						"Update at "
						// + DateFormat.getTimeInstance(DateFormat.SHORT)
						// .format(new Date()));
								+ DateFormat.getTimeInstance(DateFormat.SHORT)
										.format(props.getNextUpdateTime()));
			}
			remoteViews.setTextViewText(R.id.update,
					new DecimalFormat(CPL).format(info.getCurrentGasPrice()));
		}
		{
			final AlarmManager alarmManager = (AlarmManager) getApplicationContext()
					.getSystemService(Context.ALARM_SERVICE);
			final PendingIntent pendingIntent = PendingIntent.getService(
					getApplicationContext(), START_NOT_STICKY, intent,
					START_NOT_STICKY);
			// alarmManager.set(AlarmManager.RTC, new Date().getTime() + 10000,
			// pendingIntent);
			alarmManager.set(AlarmManager.RTC, props.getNextUpdateTime()
					.getTime() + 1, pendingIntent);
		}

		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());

		final ComponentName thisWidget = new ComponentName(
				getApplicationContext(), GasPricesWidgetProvider.class);

		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}
}
