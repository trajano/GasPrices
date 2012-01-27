package net.trajano.gasprices;

import java.text.DecimalFormat;

import android.app.Activity;
import android.widget.TextView;

@Deprecated
public class GasPricesViewWrapper {
	private final Activity activity;

	private final ApplicationProperties props;

	/**
	 * 
	 * @param activity
	 * @param props
	 *            application properties. This may be <code>null</code> for
	 *            status updates only.
	 */
	public GasPricesViewWrapper(final Activity activity,
			final ApplicationProperties props) {
		this.activity = activity;
		this.props = props;
	}

	public void updateView() {
		final PreferenceAdaptor preferences = new PreferenceAdaptor(activity);

		if (!props.isLoaded()) {
			final TextView v = (TextView) activity
					.findViewById(R.id.LastUpdatedText);
			v.setText("There is no data available.");
			return;
		}

		final CityInfo cityInfo = preferences.getSelectedCityInfo();
		{
			final TextView v = (TextView) activity.findViewById(R.id.city);
			// TODO use GPS to figure out the default location.
			v.setText(preferences.getString("selected_city_name", "Toronto"));
		}
		{
			final TextView v = (TextView) activity
					.findViewById(R.id.PriceTodayText);
			// TODO convert to use resources
			v.setText(new DecimalFormat("##0.0 ' cents/L'").format(cityInfo
					.getCurrentGasPrice()));
		}
		if (cityInfo.isTomorrowsGasPriceAvailable()) {
			final TextView v = (TextView) activity
					.findViewById(R.id.OtherPriceLabelText);
			v.setText("Tomorrow");
			final TextView vp = (TextView) activity
					.findViewById(R.id.OtherPriceText);
			// TODO convert to use resources
			vp.setText(new DecimalFormat("##0.0 ' cents/L'").format(cityInfo
					.getTomorrowsGasPrice()));
		}
		if (cityInfo.isYesterdaysGasPriceAvailable()) {
			final TextView v = (TextView) activity
					.findViewById(R.id.OtherPriceLabelText);
			v.setText("Yesterday");
			final TextView vp = (TextView) activity
					.findViewById(R.id.OtherPriceText);
			// TODO convert to use resources
			vp.setText(new DecimalFormat("##0.0 ' cents/L'").format(cityInfo
					.getYesterdaysGasPrice()));
		}
	}
}