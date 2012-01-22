package net.trajano.gasprices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class CityInfo {

	private final float currentGasPrice;

	private final Date priceDate;
	private final double priceDifference;
	private final float tomorrowsGasPrice;
	private final boolean tomorrowsGasPriceAvailable;
	private final String toStringValue;
	private final float yesterdaysGasPrice;
	private final boolean yesterdaysGasPriceAvailable;

	public CityInfo(final JSONObject closestCityData) throws JSONException,
			ParseException {
		priceDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
				.parse(closestCityData.getString("price_date"));
		priceDifference = closestCityData.getDouble("price_difference");
		final Date currentDate = new Date();
		if (priceDate.after(currentDate)) {
			tomorrowsGasPriceAvailable = true;
			yesterdaysGasPriceAvailable = false;
			yesterdaysGasPrice = Float.NaN;
			tomorrowsGasPrice = (float) closestCityData.getDouble("regular");
			if ("+".equals(closestCityData.getString("price_prefix"))) {
				currentGasPrice = (float) (closestCityData.getDouble("regular") - getPriceDifference());
			} else if ("-".equals(closestCityData.getString("price_prefix"))) {
				currentGasPrice = (float) (closestCityData.getDouble("regular") + getPriceDifference());
			} else {
				currentGasPrice = tomorrowsGasPrice;
			}
		} else {
			tomorrowsGasPriceAvailable = false;
			yesterdaysGasPriceAvailable = true;
			tomorrowsGasPrice = Float.NaN;
			currentGasPrice = (float) closestCityData.getDouble("regular");
			if ("+".equals(closestCityData.getString("price_prefix"))) {
				yesterdaysGasPrice = (float) (closestCityData
						.getDouble("regular") - getPriceDifference());
			} else if ("-".equals(closestCityData.getString("price_prefix"))) {
				yesterdaysGasPrice = (float) (closestCityData
						.getDouble("regular") + getPriceDifference());
			} else {
				yesterdaysGasPrice = currentGasPrice;
			}
		}
		toStringValue = closestCityData.toString();
	}

	public float getCurrentGasPrice() {
		return currentGasPrice;
	}

	public float getTomorrowsGasPrice() {
		return tomorrowsGasPrice;
	}

	public float getYesterdaysGasPrice() {
		return yesterdaysGasPrice;
	}

	public boolean isTomorrowsGasPriceAvailable() {
		return tomorrowsGasPriceAvailable;
	}

	public boolean isYesterdaysGasPriceAvailable() {
		return yesterdaysGasPriceAvailable;
	}

	@Override
	public String toString() {
		return toStringValue;
	}

	public double getPriceDifference() {
		return priceDifference;
	}
}
