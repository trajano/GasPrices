package net.trajano.gasprices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.location.Location;
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

	private JSONObject getClosestCityData(final Location location)
			throws JSONException {
		float currentDistance = Float.MAX_VALUE;
		JSONObject currentPrices = null;
		final JSONArray cities = (JSONArray) getResultData().get("gasprices");
		for (int i = 0; i < cities.length(); ++i) {
			final JSONObject currentObject = cities.getJSONObject(i);
			if (currentObject.isNull("location_latitude")) {
				continue;
			}
			final Location cityLocation = new Location("app");
			cityLocation.setLatitude(currentObject
					.getDouble("location_latitude"));
			cityLocation.setLongitude(currentObject
					.getDouble("location_longitude"));
			final float distance = cityLocation.distanceTo(location);
			if (distance <= currentDistance) {
				currentDistance = distance;
				currentPrices = currentObject;
			}
		}
		return currentPrices;
	}

	public CityInfo getClosestCityInfo(final Location location) {
		try {
			return new CityInfo(getClosestCityData(location));
		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
			return null;
		} catch (final ParseException e) {
			Log.e("GasPrices", e.getMessage());
			return null;
		}
	}

	public Date getLastUpdated() {
		return new Date(Long.parseLong(prop.getProperty(LAST_UPDATED)));
	}

	/**
	 * If the properties are not loaded then this function will return the
	 * current date/time.
	 * 
	 * The rule is the next update time would be 5pm, 8pm and midnight.
	 * 
	 * @return
	 */
	public Date getNextUpdateTime() {
		if (!loaded) {
			return new Date();
		}
		final Calendar cal = Calendar.getInstance();
		cal.setTime(getLastUpdated());
		if (cal.get(Calendar.HOUR_OF_DAY) < 17) {
			cal.set(Calendar.HOUR_OF_DAY, 20);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		} else if (cal.get(Calendar.HOUR_OF_DAY) < 20) {
			cal.set(Calendar.HOUR_OF_DAY, 20);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
		} else {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.DATE, 1);
		}
		return cal.getTime();
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
