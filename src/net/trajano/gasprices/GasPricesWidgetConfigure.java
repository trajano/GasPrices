package net.trajano.gasprices;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;

public class GasPricesWidgetConfigure extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		if (Build.PRODUCT.endsWith("sdk")
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.enableDefaults();
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.widget_configuration);
		final ListView listView = (ListView) findViewById(R.id.CityList);

		// listView.set
	}
}