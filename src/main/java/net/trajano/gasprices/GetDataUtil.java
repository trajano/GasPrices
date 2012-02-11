package net.trajano.gasprices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public final class GetDataUtil {
	/**
	 * This will connect to the Internet to get the gas price data and return
	 * the parsed {@link JSONObject}. This will return <code>null</code> if
	 * there is an error parsing the data because there isn't anything that can
	 * be done if there is a parse error, but the {@link IOException} is still
	 * thrown for any communication errors.
	 * 
	 * @return a parsed JSONObject.
	 * @throws IOException
	 *             I/O error.
	 */
	public static JSONObject getGasPricesDataFromInternet() throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) new URL(
				"http://www.tomorrowsgaspricetoday.com/mobile/json_mobile_data.php")
				.openConnection();
		try {
			// Read the JSON data, skip the first character since it
			// breaks the parsing.
			final String jsonData = new Scanner(urlConnection.getInputStream())
					.useDelimiter("\\A").next().substring(1);

			final Object value = new JSONTokener(jsonData).nextValue();
			if (value instanceof JSONObject) {
				return (JSONObject) value;
			} else {
				throw new IOException("Did not get a proper JSON object");
			}
		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
			throw new IOException(e);
		} finally {
			urlConnection.disconnect();
		}
	}

	private GetDataUtil() {

	}

}
