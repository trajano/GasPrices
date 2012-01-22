package net.trajano.gasprices;

import java.text.DecimalFormat;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

public final class LoadWidgetTask extends
		AsyncTask<Void, Integer, ApplicationProperties> {
	private static final Location TORONTO_LOCATION;
	static {
		TORONTO_LOCATION = new Location("app");
		TORONTO_LOCATION.setLatitude(43.6669);
		TORONTO_LOCATION.setLongitude(-79.3824);
	}

	/**
	 * Context.
	 */
	private final Context context;

	public LoadWidgetTask(final Context context) {
		this.context = context;
	}

	@Override
	protected ApplicationProperties doInBackground(final Void... params) {
		Log.v("GasPrices", "widget service background");
		return new ApplicationProperties(context);
	}

	@Override
	protected void onPostExecute(final ApplicationProperties props) {
		Log.v("GasPrices", "widget service postexecute");

		final RemoteViews remoteViews = new RemoteViews(
				context.getPackageName(), R.layout.widget_layout);

		if (!props.isLoaded()) {
			remoteViews.setTextViewText(R.id.update, context.getResources()
					.getText(R.string.default_price));
			return;
		}

		// TODO show the difference and adjust the color of the widget if
		// needed
		final CityInfo cityInfo = props.getClosestCityInfo(TORONTO_LOCATION);
		if (cityInfo != null) {
			remoteViews.setTextViewText(R.id.update, new DecimalFormat("##0.0")
					.format(cityInfo.getCurrentGasPrice()));
		} else {
			remoteViews.setTextViewText(R.id.update, context.getResources()
					.getText(R.string.default_price));
		}
	}
}
