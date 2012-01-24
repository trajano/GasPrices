package net.trajano.gasprices;

import android.app.AlarmManager;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

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
	 * <li>send an custom intent to tell the application to update itself.</li>
	 * </ul>
	 * {@inheritDoc}
	 */
	@Override
	protected void onHandleIntent(final Intent intent) {
	}

}
