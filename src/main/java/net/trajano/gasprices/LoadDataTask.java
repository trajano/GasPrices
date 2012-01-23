package net.trajano.gasprices;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;

public final class LoadDataTask extends
		AsyncTask<Void, Integer, ApplicationProperties> {
	private static final Location TORONTO_LOCATION;
	static {
		TORONTO_LOCATION = new Location("app");
		TORONTO_LOCATION.setLatitude(43.6669);
		TORONTO_LOCATION.setLongitude(-79.3824);
	}

	/**
	 * Activity.
	 */
	private final Activity activity;

	public LoadDataTask(final Activity activity) {
		this.activity = activity;
	}

	@Override
	protected ApplicationProperties doInBackground(final Void... params) {
		return new ApplicationProperties(activity);
	}

	@Override
	protected void onPostExecute(final ApplicationProperties props) {
		final GasPricesViewWrapper view = new GasPricesViewWrapper(activity,
				props);
		view.updateView();
		if (props.isUpdateRequired()) {
			view.setStatus("Automatically updating...");
			new UpdateDataTask(activity).execute();
		}
	}
}
