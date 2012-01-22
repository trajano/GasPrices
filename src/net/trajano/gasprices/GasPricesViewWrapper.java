package net.trajano.gasprices;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import android.app.Activity;
import android.location.Location;
import android.widget.TextView;

public class GasPricesViewWrapper {
	private static final Location TORONTO_LOCATION;

	static {
		TORONTO_LOCATION = new Location("app");
		TORONTO_LOCATION.setLatitude(43.6669);
		TORONTO_LOCATION.setLongitude(-79.3824);
	}
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

	public void setStatus(final String status) {
		final TextView v = (TextView) activity
				.findViewById(R.id.GasPriceStatusText);
		v.setText(status);

	}

	public void updateView() {
		if (!props.isLoaded()) {
			final TextView v = (TextView) activity
					.findViewById(R.id.LastUpdatedText);
			v.setText("There is no data available.");
			return;
		}
		final CityInfo result = props.getClosestCityInfo(TORONTO_LOCATION);
		{
			final TextView v = (TextView) activity
					.findViewById(R.id.LastUpdatedText);
			v.setText(MessageFormat.format(
					activity.getResources().getString(R.string.last_updated),
					DateFormat
							.getDateTimeInstance(DateFormat.LONG,
									DateFormat.LONG)
							.format(props.getLastUpdated())
							.replace(' ', '\u00A0')));
		}
		{
			final TextView v = (TextView) activity
					.findViewById(R.id.PriceTodayText);
			// TODO convert to use resources
			v.setText(new DecimalFormat("##0.0 ' cents/L'").format(result
					.getCurrentGasPrice()));
		}
		if (result.isTomorrowsGasPriceAvailable()) {
			final TextView v = (TextView) activity
					.findViewById(R.id.OtherPriceLabelText);
			v.setText("Tomorrow");
			final TextView vp = (TextView) activity
					.findViewById(R.id.OtherPriceText);
			// TODO convert to use resources
			vp.setText(new DecimalFormat("##0.0 ' cents/L'").format(result
					.getTomorrowsGasPrice()));
		}
		if (result.isYesterdaysGasPriceAvailable()) {
			final TextView v = (TextView) activity
					.findViewById(R.id.OtherPriceLabelText);
			v.setText("Yesterday");
			final TextView vp = (TextView) activity
					.findViewById(R.id.OtherPriceText);
			// TODO convert to use resources
			vp.setText(new DecimalFormat("##0.0 ' cents/L'").format(result
					.getYesterdaysGasPrice()));
		}
		setStatus(MessageFormat.format(
				activity.getResources().getString(R.string.next_update),
				DateFormat
						.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG)
						.format(props.getNextUpdateTime())
						.replace(' ', '\u00A0')));
	}
}