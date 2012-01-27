package net.trajano.gasprices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class CityInfo {

	private final float currentGasPrice;

	private final String name;
	private final Date priceDate;
	private final double priceDifference;
	private final double priceDifferenceAbsoluteValue;
	private final float tomorrowsGasPrice;
	private final boolean tomorrowsGasPriceAvailable;
	private final String toStringValue;
	private final float yesterdaysGasPrice;
	private final boolean yesterdaysGasPriceAvailable;

	public CityInfo(final JSONObject city) throws JSONException {
		try {
			priceDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.ENGLISH).parse(city.getString("price_date"));
		} catch (final ParseException e) {
			Log.e("GasPrices", e.getMessage());
			throw new RuntimeException(e);
		}
		{
			final String tempName = city.getString("city_name");
			name = tempName.substring(0, tempName.indexOf(" Gas Prices"));
		}
		priceDifferenceAbsoluteValue = city.getDouble("price_difference");
		priceDifference = priceDifferenceAbsoluteValue
				* ("-".equals(city.getString("price_prefix")) ? -1 : 1);
		final Date currentDate = new Date();
		if (priceDate.after(currentDate)) {
			tomorrowsGasPriceAvailable = true;
			yesterdaysGasPriceAvailable = false;
			yesterdaysGasPrice = Float.NaN;
			tomorrowsGasPrice = (float) city.getDouble("regular");
			currentGasPrice = (float) (city.getDouble("regular") - priceDifference);
		} else {
			tomorrowsGasPriceAvailable = false;
			yesterdaysGasPriceAvailable = true;
			tomorrowsGasPrice = Float.NaN;
			currentGasPrice = (float) city.getDouble("regular");
			yesterdaysGasPrice = (float) (city.getDouble("regular") - priceDifference);
		}
		toStringValue = city.toString();
	}

	public float getCurrentGasPrice() {
		return currentGasPrice;
	}

	public String getName() {
		return name;
	}

	public double getPriceDifference() {
		return priceDifference;
	}

	public double getPriceDifferenceAbsoluteValue() {
		return priceDifferenceAbsoluteValue;
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
}
