package net.trajano.gasprices;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.text.Spannable;
import android.widget.TextView;

/**
 * This activity is shown to display the feed data.
 * 
 * @author Archimedes Trajano (developer@trajano.net)
 * 
 */
@SuppressLint("Registered")
@EActivity(R.layout.feed)
@OptionsMenu(R.menu.feedmenu)
public class GasPricesFeedActivity extends Activity {
	/**
	 * Forced update progress dialog.
	 */
	private ProgressDialog forcedUpdateDialog;

	/**
	 * When the preference change on lastUpdated is detected it will update the
	 * view.
	 */
	private final OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				final SharedPreferences sharedPreferences, final String key) {
			if (!PreferenceAdaptor.isKeyAffectGasPricesView(key)) {
				return;
			}
			updateView();
		}
	};

	/**
	 * Preference data, stored in memory until destruction.
	 */
	private PreferenceAdaptor preferences;

	@OptionsItem(R.id.UpdateMenuItem)
	void forceUpdateSelected() {
		forcedUpdateDialog = ProgressDialog.show(this, "", getResources()
				.getString(R.string.loading), true);
		new UpdateTask(this).execute();
	}

	@OptionsItem(android.R.id.home)
	void goHomeSelected() {
		finish();
	}

	/**
	 * Called when the activity is first created. Sets up the adapter and the
	 * action bar.
	 */
	@AfterViews
	public void init() {
		preferences = new PreferenceAdaptor(this);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected void onPause() {
		preferences
				.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateView();
		preferences
				.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
	}

	/**
	 * Updates the view with the information stored in the {@link #preferences}
	 * object.
	 */
	private void updateView() {
		final TextView feedTextView = (TextView) findViewById(R.id.FeedText);
		if (preferences.isError()) {
			feedTextView.setText(preferences.getFeedData(),
					TextView.BufferType.SPANNABLE);
		} else {
			feedTextView.setText(preferences.getJsonDataString(),
					TextView.BufferType.SPANNABLE);
		}
		final Spannable spannableText = (Spannable) feedTextView.getText();
		feedTextView.setText(spannableText);
		if (forcedUpdateDialog != null) {
			forcedUpdateDialog.dismiss();
			forcedUpdateDialog = null;
		}
	}
}