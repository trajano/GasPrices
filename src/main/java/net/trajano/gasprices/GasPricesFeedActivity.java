package net.trajano.gasprices;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * This activity is shown to display the feed data.
 * 
 * @author Archimedes Trajano (developer@trajano.net)
 * 
 */
public class GasPricesFeedActivity extends Activity {
	/**
	 * When the preference change on lastUpdated is detected it will update the
	 * view.
	 */
	private final OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {

		@Override
		public void onSharedPreferenceChanged(
				final SharedPreferences sharedPreferences, final String key) {
			if (key != "resultdata") {
				return;
			}
			updateView();
		}
	};

	/**
	 * Preference data, stored in memory until destruction.
	 */
	private SharedPreferences preferences;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.PRODUCT.endsWith("sdk")
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.enableDefaults();
		}
		preferences = PreferenceUtil.getPreferences(this);
		setContentView(R.layout.feed);
	}

	@Override
	public boolean onCreateOptionsMenu(final android.view.Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.feedmenu, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setHomeButtonEnabled(true);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		// Handle item selection
		if (R.id.UpdateMenuItem == item.getItemId()) {
			// TODO don't do this! create a new AsyncTask instead.
			final Intent intent = new Intent(this, GasPricesUpdateService.class);
			startService(intent);
			return true;
		} else if (android.R.id.home == item.getItemId()) {
			// app icon in action bar clicked; go home
			final Intent intent = new Intent(this, GasPricesActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
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
		try {
			feedTextView.setText(
					new JSONObject(preferences.getString("resultdata", null))
							.toString(3), TextView.BufferType.SPANNABLE);
			final Spannable spannableText = (Spannable) feedTextView.getText();
			spannableText.setSpan(new ForegroundColorSpan(0xFFEEFF00), 10, 20,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			feedTextView.setText(spannableText);
		} catch (final JSONException e) {
			feedTextView.setText("unable to parse JSON data");
		}
		// final GasPricesViewWrapper view = new GasPricesViewWrapper(this,
		// new ApplicationProperties(this));
		// view.updateView();
		// view.setStatus("updated via sharedPreferenceUpdate");
	}
}