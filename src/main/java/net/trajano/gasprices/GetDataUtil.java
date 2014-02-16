package net.trajano.gasprices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	 * the parsed {@link JSONObject}. This will throw an {@link IOException} if
	 * there is an error parsing the data because there isn't anything that can
	 * be done if there is a parse error, but the {@link IOException} is still
	 * thrown for any communication errors.
	 * 
	 * @param cacheDir
	 *            cache directory
	 * 
	 * @return a parsed JSONObject.
	 * @throws IOException
	 *             I/O error.
	 */
	public static JSONObject getGasPricesDataFromInternet(final File cacheDir)
			throws IOException {
		try {
			// Read the JSON data, skip the first character since it
			// breaks the parsing.
			final String jsonData = getRawGasPricesDataFromInternet(cacheDir);
			final Object value = new JSONTokener(jsonData).nextValue();
			if (value instanceof JSONObject) {
				return (JSONObject) value;
			} else {
				throw new IOException("Did not get a proper JSON object");
			}
		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
			throw new IOException(e);
		}
	}

	/**
	 * This connects to the site to get the data as is. Used for the situation
	 * where an error had occured.
	 * 
	 * @param cacheDir
	 *            cache directory
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getRawGasPricesDataFromInternet(final File cacheDir)
			throws IOException {
		final File cacheFile = new File(cacheDir, "cached");
		final HttpURLConnection urlConnection = (HttpURLConnection) new URL(
				"http://www.tomorrowsgaspricetoday.com/mobile/json_mobile_data.php")
				.openConnection();
		FileOutputStream cacheFileStream = null;
		try {
			final InputStream networkStream = urlConnection.getInputStream();
			// Skip the first character.
			networkStream.read();
			cacheFileStream = new FileOutputStream(cacheFile);
			final int c = networkStream.read();
			while (c != -1) {
				cacheFileStream.write(c);
			}
		} catch (final IOException e) {
			// TODO alert the user that a previously cached data is going to
			// be used.
			Log.e("GasPrices", e.getMessage(), e);
		} finally {
			if (cacheFileStream != null) {
				cacheFileStream.close();
			}
			urlConnection.disconnect();
		}
		final FileInputStream cacheInputStream = new FileInputStream(cacheFile);
		try {
			return new Scanner(cacheInputStream).useDelimiter("\\A").next();
		} finally {
			cacheInputStream.close();
		}
	}

	private GetDataUtil() {

	}

}
