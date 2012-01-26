package net.trajano.gasprices;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import org.json.JSONException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.widget.ProgressBar;
import android.widget.TextView;

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

	public void setStatus(final String status) {
		final TextView v = (TextView) activity
				.findViewById(R.id.GasPriceStatusText);
		if (v != null) {
			v.setText(status);
		}

	}

	public void updateView() {
		final SharedPreferences preferences = PreferenceUtil
				.getPreferences(activity);

		if (!props.isLoaded()) {
			final TextView v = (TextView) activity
					.findViewById(R.id.LastUpdatedText);
			v.setText("There is no data available.");
			return;
		}
		final TextView feedView = (TextView) activity
				.findViewById(R.id.FeedText);
		if (feedView != null) {
			try {
				feedView.setText(props.getResultData().toString(3));
			} catch (final JSONException e) {
				feedView.setText(e.toString());
			}
		} else {

			final CityInfo result = props.getCityInfo(preferences.getLong(
					"selected_city_id", 133));
			{
				final TextView v = (TextView) activity
						.findViewById(R.id.LastUpdatedText);
				v.setText(MessageFormat.format(activity.getResources()
						.getString(R.string.last_updated), DateFormat
						.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG)
						.format(props.getLastUpdated()).replace(' ', '\u00A0')));
			}
			{
				final TextView v = (TextView) activity.findViewById(R.id.city);
				// TODO use GPS to figure out the default location.
				v.setText(preferences
						.getString("selected_city_name", "Toronto"));
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
		}
		setStatus(MessageFormat.format(
				activity.getResources().getString(R.string.next_update),
				DateFormat
						.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG)
						.format(PreferenceUtil.nextUpdateDate(preferences
								.getLong("last_updated", 0)))
						.replace(' ', '\u00A0')));
		{
			final ProgressBar vp = (ProgressBar) activity
					.findViewById(R.id.progressBar);
			vp.setProgress(100);
		}
	}
}