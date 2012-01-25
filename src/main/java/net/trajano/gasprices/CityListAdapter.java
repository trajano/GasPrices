package net.trajano.gasprices;

import android.app.ListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class CityListAdapter extends BaseAdapter implements ListAdapter {
	private final ListActivity activity;
	private final LayoutInflater inflater;

	public CityListAdapter(final ListActivity activity) {
		this.activity = activity;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Object getItem(final int position) {
		return "getItem" + position;
	}

	@Override
	public long getItemId(final int position) {
		return 1000 + position;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		final TextView view;
		if (convertView != null) {
			view = (TextView) convertView;
		} else if (isEnabled(position)) {
			view = (TextView) inflater.inflate(
					android.R.layout.simple_expandable_list_item_1, parent,
					false);
		} else {
			view = (TextView) inflater.inflate(
					android.R.layout.simple_list_item_1, parent, false);
		}
		view.setText("position " + position);
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEnabled(final int position) {
		return position % 3 != 0;
	}

}
