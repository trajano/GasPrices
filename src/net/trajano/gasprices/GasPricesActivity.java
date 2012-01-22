package net.trajano.gasprices;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GasPricesActivity extends Activity implements OnClickListener {
	/**
	 * The click handler. TODO very yucky having it here, it should be on its
	 * own class per button.
	 */
	public void onClick(final View v) {
		final Button refreshButton = (Button) findViewById(R.id.RefreshButton);
		final Button debugButton = (Button) findViewById(R.id.DebugButton);
		final Button userButton = (Button) findViewById(R.id.UserButton);
		if (v == refreshButton) {
			final GasPricesViewWrapper view = new GasPricesViewWrapper(this,
					null);
			view.setStatus("Forced updating...");
			new UpdateDataTask(this).execute();
		} else if (v == debugButton) {
			setFeedView();
		} else if (v == userButton) {
			setMainView();
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		StrictMode.enableDefaults();
		super.onCreate(savedInstanceState);
		setMainView();
	}

	private void setFeedView() {
		setContentView(R.layout.feed);
		new LoadDataTask(this).execute();
		final Button refreshButton = (Button) findViewById(R.id.RefreshButton);
		final Button userButton = (Button) findViewById(R.id.UserButton);
		refreshButton.setOnClickListener(this);
		userButton.setOnClickListener(this);
	}

	private void setMainView() {
		setContentView(R.layout.main);
		new LoadDataTask(this).execute();
		final Button refreshButton = (Button) findViewById(R.id.RefreshButton);
		final Button debugButton = (Button) findViewById(R.id.DebugButton);
		// final Button userButton = (Button) findViewById(R.id.UserButton);
		refreshButton.setOnClickListener(this);
		debugButton.setOnClickListener(this);
	}
}