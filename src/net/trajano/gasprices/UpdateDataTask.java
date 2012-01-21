package net.trajano.gasprices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
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
				final JSONArray prices = (JSONArray) object.get("gasprices");

				final double currentLatitude = 43.6669;
				final double currentLongitude = -79.3824;
				double currentDistance = Double.MAX_VALUE;
				JSONObject currentPrices = null;
				for (int i = 0; i < prices.length(); ++i) {
					final JSONObject currentObject = prices.getJSONObject(i);
					if (currentObject.isNull("location_latitude")) {
						continue;
					}
					final double lat = currentObject
							.getDouble("location_latitude");
					final double longitude = currentObject
							.getDouble("location_longitude");
					final double distance = DistanceUtil.distance(
							currentLatitude, currentLongitude, lat, longitude);
					if (distance <= currentDistance) {
						currentDistance = distance;
						currentPrices = currentObject;
					}
				}
				return currentPrices;
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
			v.setText(result.getString("regular") + "\n" + result);
		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
			return;
		}
	}
}
