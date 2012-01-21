package net.trajano.gasprices;

import java.text.DateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
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
		try {
			final TextView v = (TextView) activity
					.findViewById(R.id.GasPriceText);
			final JSONObject result = props
					.getClosestCityData(TORONTO_LOCATION);
			v.setText("Last updated on: "
					+ DateFormat.getDateTimeInstance(DateFormat.FULL,
							DateFormat.FULL).format(props.getLastUpdated())
					+ "\n" + result.getString("regular") + "\n" + result
					+ "\nUpdating...");

		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
		}
	}
}
