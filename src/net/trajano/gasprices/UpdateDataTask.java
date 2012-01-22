package net.trajano.gasprices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public final class UpdateDataTask extends AsyncTask<Void, Integer, JSONObject> {
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

	private ApplicationProperties props;

	public UpdateDataTask(final Activity activity) {
		this.activity = activity;
	}

	@Override
	protected JSONObject doInBackground(final Void... params) {
		try {
			final HttpURLConnection urlConnection = (HttpURLConnection) new URL(
					"http://www.tomorrowsgaspricetoday.com/mobile/json_mobile_data.php")
					.openConnection();
			try {
				// Read the JSON data, skip the first character since it breaks
				// the
				// parsing.
				final String jsonData = new Scanner(
						urlConnection.getInputStream()).useDelimiter("\\A")
						.next().substring(1);

				final JSONObject object = (JSONObject) new JSONTokener(jsonData)
						.nextValue();
				props = new ApplicationProperties(activity);
				props.updateResultData(object);
				props.write();

				return props.getClosestCityData(TORONTO_LOCATION);
			} finally {
				urlConnection.disconnect();
			}
		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
			return null;
		} catch (final IOException e) {
			Log.e("GasPrices", e.getMessage());
			return null;
		}
	}

	@Override
	protected void onPostExecute(final JSONObject result) {
		if (result == null || props == null) {
			return;
		}
		Log.v("ME", "result is =" + result);
		final TextView v = (TextView) activity.findViewById(R.id.GasPriceText);
		try {
			v.setText("Last updated on: "
					+ DateFormat.getDateTimeInstance(DateFormat.FULL,
							DateFormat.LONG).format(props.getLastUpdated())
					+ "\n"
					+ result.getString("regular")
					+ "\n"
					+ result
					+ "\nNext update on: "
					+ DateFormat.getDateTimeInstance(DateFormat.FULL,
							DateFormat.LONG).format(props.getNextUpdateTime()));
		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
			return;
		}
	}
}
