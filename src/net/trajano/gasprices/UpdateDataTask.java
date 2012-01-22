package net.trajano.gasprices;

import java.io.IOException;

import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public final class UpdateDataTask extends
		AsyncTask<Void, Integer, ApplicationProperties> {

	/**
	 * Activity.
	 */
	private final Activity activity;

	public UpdateDataTask(final Activity activity) {
		this.activity = activity;
	}

	@Override
	protected ApplicationProperties doInBackground(final Void... params) {
		try {
			final ApplicationProperties props = new ApplicationProperties(
					activity);
			props.update();
			return props;
		} catch (final JSONException e) {
			Log.e("GasPrices", e.getMessage());
			return null;
		} catch (final IOException e) {
			Log.e("GasPrices", e.getMessage());
			return null;
		}
	}

	@Override
	protected void onPostExecute(final ApplicationProperties props) {
		final GasPricesViewWrapper view = new GasPricesViewWrapper(activity,
				props);
		if (props == null) {
			view.setStatus("Update failed");
		} else {
			view.updateView();
		}
	}
}
