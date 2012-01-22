package net.trajano.gasprices;

import java.text.DateFormat;
import java.text.DecimalFormat;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.TextView;

public final class LoadDataTask extends
		AsyncTask<Void, Integer, ApplicationProperties> {
	private static final Location TORONTO_LOCATION;
	static {
		TORONTO_LOCATION = new Location("app");
		TORONTO_LOCATION.setLatitude(43.6669);
		TORONTO_LOCATION.setLongitude(-79.3824);
	}

	/**
	 * Activity.
	 */
	private final Activity activity;

	public LoadDataTask(final Activity activity) {
		this.activity = activity;
	}

	@Override
	protected ApplicationProperties doInBackground(final Void... params) {
		return new ApplicationProperties(activity);
	}

	@Override
	protected void onPostExecute(final ApplicationProperties props) {
		if (!props.isLoaded()) {
			final TextView v = (TextView) activity
					.findViewById(R.id.GasPriceText);
			v.setText("there is a problem reading the properties file");
			return;
		}
		final TextView v = (TextView) activity.findViewById(R.id.GasPriceText);
		final CityInfo result = props.getClosestCityInfo(TORONTO_LOCATION);
		v.setText("Last updated on: "
				+ DateFormat.getDateTimeInstance(DateFormat.FULL,
						DateFormat.FULL).format(props.getLastUpdated())
				+ "\n"
				+ "Today: "
				+ new DecimalFormat("##0.0").format(result.getCurrentGasPrice())
				+ "\n"

				+ "Tomorrow: "
				+ new DecimalFormat("##0.0").format(result
						.getTomorrowsGasPrice()) + "\n" + result
				+ "\nUpdating...");

	}
}
