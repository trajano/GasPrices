package net.trajano.gasprices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public final class UpdateDataTask extends
		AsyncTask<Void, Integer, ApplicationProperties> {

	/**
	 * Activity.
	 */
	private final Activity activity;

	public UpdateDataTask(final Activity activity) {
		this.activity = activity;
	}

	@Override
	protected ApplicationProperties doInBackground(final Void... params) {
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
				final ApplicationProperties props = new ApplicationProperties(
						activity);
				props.updateResultData(object);
				props.write();

				return props;
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
	protected void onPostExecute(final ApplicationProperties props) {
		final GasPricesViewWrapper view = new GasPricesViewWrapper(activity,
				props);
		if (props == null) {
			view.setStatus("Update failed");
		} else {
			view.updateView();
		}
	}
}
