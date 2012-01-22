package net.trajano.gasprices;

import org.json.JSONException;
import org.json.JSONObject;

public class CityInfo {

	private final float currentGasPrice;

	private final float tomorrowsGasPrice;

	public CityInfo(final JSONObject closestCityData) throws JSONException {
		tomorrowsGasPrice = (float) closestCityData.getDouble("regular");
		if ("+".equals(closestCityData.getString("price_prefix"))) {
			currentGasPrice = (float) (closestCityData.getDouble("regular") + closestCityData
					.getDouble("price_difference"));
		} else if ("-".equals(closestCityData.getString("price_prefix"))) {
			currentGasPrice = (float) (closestCityData.getDouble("regular") - closestCityData
					.getDouble("price_difference"));
		} else {
			currentGasPrice = getTomorrowsGasPrice();
		}
	}

	public float getCurrentGasPrice() {
		return currentGasPrice;
	}

	public float getTomorrowsGasPrice() {
		return tomorrowsGasPrice;
	}
}
