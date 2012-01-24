package net.trajano.gasprices;

import java.util.Date;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * <p>
 * This is a service that performs an update to the Gas Prices
 * {@link SharedPreferences}. This service is called using the
 * {@link Context#startService(Intent)}.
 * </p>
 * <p>
 * This is an {@link IntentService} which means:
 * </p>
 * <ul>
 * <li>
 * we do not have to worry about creating a background task because this will
 * already be running in a background task.</li>
 * <li>we don't have to worry about cleaning up by invoking
 * {@link GasPricesUpdateService#stopSelf()}.</li>
 * </ul>
 * 
 * @author Archimedes Trajano (developer@trajano.net)
 * 
 */
public class GasPricesUpdateService extends IntentService {
	/**
	 * Define the name of the service.
	 */
	public GasPricesUpdateService() {
		super("GasPricesUpdateIntentService");
	}

	/**
	 * <p>
	 * This is the only place in the application where an Internet request is
	 * performed. Once the update is completed, it will
	 * </p>
	 * <ul>
	 * <li>reschedule itself to be started up again using the
	 * {@link AlarmManager}</li>
	 * <li>send an {@link AppWidgetManager#ACTION_APPWIDGET_UPDATE} to all the
	 * widgets.</li>
	 * </ul>
	 * <p>
	 * Note there is no need to use a custom intent because
	 * {@link SharedPreferences#registerOnSharedPreferenceChangeListener(android.content.SharedPreferences.OnSharedPreferenceChangeListener)}
	 * is part of the application and would handle it from there.
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	protected void onHandleIntent(final Intent intent) {
		// put code here to get the files

		final SharedPreferences preferences = PreferenceUtil
				.getPreferences(this);
		final Editor editor = preferences.edit();
		PreferenceUtil.setLastUpdatedToNow(editor);
		editor.apply();

		// schedule the next update.
		{
			final AlarmManager alarmManager = (AlarmManager) getApplicationContext()
					.getSystemService(Context.ALARM_SERVICE);
			final PendingIntent pendingIntent = PendingIntent.getService(this,
					0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarmManager.set(AlarmManager.RTC, new Date().getTime() + 60000,
					pendingIntent);
		}
	}

}
