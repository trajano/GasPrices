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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public final class UpdateDataTask extends AsyncTask<Void, Integer, JSONObject> {
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
				props.updateResultData(object);
				props.write();
				final double currentLatitude = 43.6669;
				final double currentLongitude = -79.3824;
				return props.getClosestCityData(currentLatitude,
						currentLongitude);
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
		if (result == null) {
			return;
		}
		Log.v("ME", "result is =" + result);
		final TextView v = (TextView) activity.findViewById(R.id.GasPriceText);
		try {
			v.setText("Last updated on: "
					+ DateFormat.getDateInstance(DateFormat.FULL).format(
							props.getLastUpdated()) + "\n"
					+ result.getString("regular") + "\n" + result);
		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
			return;
		}
	}

	@Override
	protected void onPreExecute() {
		props = new ApplicationProperties(activity);
		if (props.isLoaded()) {
			final double currentLatitude = 43.6669;
			final double currentLongitude = -79.3824;
			try {
				final JSONObject result = props.getClosestCityData(
						currentLatitude, currentLongitude);
				final TextView v = (TextView) activity
						.findViewById(R.id.GasPriceText);
				v.setText("Last updated on: "
						+ DateFormat.getDateInstance(DateFormat.LONG).format(
								props.getLastUpdated()) + "\n"
						+ result.getString("regular") + "\n" + result
						+ "\nUpdating...");

			} catch (final JSONException e) {
				Log.e("GasPrices", e.getMessage());
			}
		} else {
			final TextView v = (TextView) activity
					.findViewById(R.id.GasPriceText);
			v.setText("there is a problem reading the properties file");
		}

	}
}
