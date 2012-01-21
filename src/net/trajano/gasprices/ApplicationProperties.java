package net.trajano.gasprices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

public final class ApplicationProperties {
	private static final String FILE_NAME = "gasprices.properties";

	/**
	 * This is a JSON string that was last retrieved by the system.
	 */
	private static final String LAST_RESULT_DATA = "resultdata";

	/**
	 * This is the time the data was last updated in seconds since epoch to
	 * prevent time zone issues.
	 */
	private static final String LAST_UPDATED = "lastupdated";

	private final Context context;

	private boolean loaded;
	private final Properties prop;

	public ApplicationProperties(final Context ctx) {
		prop = new Properties();
		context = ctx;
		try {
			final InputStream is = ctx.openFileInput(FILE_NAME);
			prop.load(is);
			is.close();
			loaded = true;
		} catch (final FileNotFoundException e) {
			Log.d("GasPrices", e.getMessage());
			loaded = false;
		} catch (final IOException e) {
			Log.e("GasPrices", e.getMessage());
			loaded = false;
		}
	}

	public JSONObject getClosestCityData(final double currentLatitude,
			final double currentLongitude) throws JSONException {
		double currentDistance = Double.MAX_VALUE;
		JSONObject currentPrices = null;
		final JSONArray cities = (JSONArray) getResultData().get("gasprices");
		for (int i = 0; i < cities.length(); ++i) {
			final JSONObject currentObject = cities.getJSONObject(i);
			if (currentObject.isNull("location_latitude")) {
				continue;
			}
			final double lat = currentObject.getDouble("location_latitude");
			final double longitude = currentObject
					.getDouble("location_longitude");
			final double distance = DistanceUtil.distance(currentLatitude,
					currentLongitude, lat, longitude);
			if (distance <= currentDistance) {
				currentDistance = distance;
				currentPrices = currentObject;
			}
		}
		return currentPrices;
	}

	public Date getLastUpdated() {
		return new Date(Long.parseLong(prop.getProperty(LAST_UPDATED)));
	}

	public JSONObject getResultData() throws JSONException {
		return (JSONObject) new JSONTokener(prop.getProperty(LAST_RESULT_DATA))
				.nextValue();
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void updateResultData(final JSONObject o) throws JSONException {
		prop.setProperty(LAST_UPDATED, String.valueOf(new Date().getTime()));
		prop.setProperty(LAST_RESULT_DATA, o.toString());
		Log.v("GasPrices",
				LAST_UPDATED + " is " + prop.getProperty(LAST_UPDATED));
		loaded = true;
	}

	public void write() throws IOException {
		final OutputStream os = context.openFileOutput(FILE_NAME,
				Context.MODE_PRIVATE);
		prop.store(os, "Automatically generated file.");
		os.close();
	}
}
