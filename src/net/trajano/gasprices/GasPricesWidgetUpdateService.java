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
import android.os.Build;
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

		if (props.isUpdateRequired()) {
			try {
				props.update();
			} catch (final IOException e) {
				remoteViews.setTextViewText(R.id.change, "ERROR");
			} catch (final JSONException e) {
				remoteViews.setTextViewText(R.id.change, "JSONERROR");
			}
		}
		if (props.isLoaded()) {
			final CityInfo info = props.getClosestCityInfo(TORONTO_LOCATION);
			if (info.isTomorrowsGasPriceAvailable()) {
				if (info.getPriceDifference() == 0.00) {
					setBlue(remoteViews);
					remoteViews.setTextViewText(R.id.change, "unchanged");
				} else if (info.getPriceDifference() > 0) {
					setRed(remoteViews);
					remoteViews.setTextViewText(R.id.update, new DecimalFormat(
							CPL_UP).format(info
							.getPriceDifferenceAbsoluteValue()));
				} else {
					setGreen(remoteViews);
					remoteViews.setTextViewText(R.id.update, new DecimalFormat(
							CPL_DOWN).format(info
							.getPriceDifferenceAbsoluteValue()));
				}
			} else {
				setBlue(remoteViews);
				remoteViews.setTextViewText(
						R.id.change,
						"Update at "
								+ DateFormat.getTimeInstance(DateFormat.SHORT)
										.format(props.getNextUpdateTime()));
			}
			remoteViews.setTextViewText(R.id.update,
					new DecimalFormat(CPL).format(info.getCurrentGasPrice()));

		} else {
			remoteViews.setTextViewText(R.id.change, "Problem loading");
		}
		{
			final AlarmManager alarmManager = (AlarmManager) getApplicationContext()
					.getSystemService(Context.ALARM_SERVICE);
			final PendingIntent pendingIntent = PendingIntent.getService(
					getApplicationContext(), START_NOT_STICKY, intent,
					START_NOT_STICKY);
			// alarmManager.set(AlarmManager.RTC, new Date().getTime() + 10000,
			// pendingIntent);
			Log.v("GasPrices", "Scheduling for " + props.getNextUpdateTime());
			alarmManager.set(AlarmManager.RTC, props.getNextUpdateTime()
					.getTime() + 1, pendingIntent);
		}

		final AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getApplicationContext());

		final ComponentName thisWidget = new ComponentName(
				getApplicationContext(), GasPricesWidgetProvider.class);

		appWidgetManager.updateAppWidget(thisWidget, remoteViews);
	}

	private void setBlue(final RemoteViews remoteViews) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
					R.drawable.myshape);
		}
	}

	private void setGreen(final RemoteViews remoteViews) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
					R.drawable.myshape_green);
		}
	}

	private void setRed(final RemoteViews remoteViews) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			remoteViews.setInt(R.id.thelayout, "setBackgroundResource",
					R.drawable.myshape_red);
		}
	}
}
