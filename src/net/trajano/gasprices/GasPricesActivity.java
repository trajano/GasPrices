package net.trajano.gasprices;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

public class GasPricesActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		StrictMode.enableDefaults();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		new LoadDataTask(this).execute();
		new UpdateDataTask(this).execute();
	}
}