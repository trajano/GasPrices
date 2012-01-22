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

	public GasPricesViewWrapper(final Activity activity,
			final ApplicationProperties props) {
		this.activity = activity;
		this.props = props;
	}

	public void updateView() {
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
					.findViewById(R.id.GasPriceText);
			v.setText("Today: "
					+ new DecimalFormat("##0.0").format(result
							.getCurrentGasPrice())
					+ "\n"
					+ "Tomorrow: "
					+ new DecimalFormat("##0.0").format(result
							.getTomorrowsGasPrice())
					+ "\n"
					+ "Yesterday: "
					+ new DecimalFormat("##0.0").format(result
							.getYesterdaysGasPrice()) + "\n" + result);
		}
		{
			final TextView v = (TextView) activity
					.findViewById(R.id.GasPriceStatusText);
			v.setText(MessageFormat.format(
					activity.getResources().getString(R.string.next_update),
					DateFormat
							.getDateTimeInstance(DateFormat.LONG,
									DateFormat.LONG)
							.format(props.getNextUpdateTime())
							.replace(' ', '\u00A0')));
		}

	}
}