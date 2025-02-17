package cu.axel.smartdock.adapters;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;
import cu.axel.smartdock.R;
import cu.axel.smartdock.icons.IconParserUtilities;
import cu.axel.smartdock.models.App;
import cu.axel.smartdock.utils.Utils;
import java.util.ArrayList;
import android.content.SharedPreferences;
import cu.axel.smartdock.utils.ColorUtils;

public class AppAdapter extends ArrayAdapter<App> {
	private final Context context;
	private int iconBackground;
	private final int iconPadding;
	private ArrayList<App> apps, originalList;
	private AppFilter filter;
	private boolean iconTheming, tabletMode;
	private IconParserUtilities iconParserUtilities;
	private AppRightClickListener rightClickListener;
	private String query;

	public AppAdapter(Context context, AppRightClickListener listener, ArrayList<App> apps) {
		super(context, R.layout.app_entry, apps);
		this.context = context;
		rightClickListener = listener;
		this.apps = apps;
		this.originalList = new ArrayList<App>(apps);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		iconParserUtilities = new IconParserUtilities(context);
		tabletMode = sp.getBoolean("app_menu_fullscreen", false);
		iconPadding = Utils.dpToPx(context, Integer.parseInt(sp.getString("icon_padding", "5")));
		iconTheming = !sp.getString("icon_pack", "").equals("");

		switch (sp.getString("icon_shape", "circle")) {
		case "circle":
			iconBackground = R.drawable.circle;
			break;
		case "round_rect":
			iconBackground = R.drawable.round_square;
			break;
		case "default":
			iconBackground = -1;
			break;
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context)
					.inflate(tabletMode ? R.layout.app_entry_desktop : R.layout.app_entry, null);
			holder = new ViewHolder();
			holder.iconIv = convertView.findViewById(R.id.app_icon_iv);
			holder.nameTv = convertView.findViewById(R.id.app_name_tv);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		final App app = apps.get(position);
		String name = app.getName();

		if (query != null) {
			int spanStart = name.toLowerCase().indexOf(query.toLowerCase());
			int spanEnd = spanStart + query.length();
			if (spanStart != -1) {
				SpannableString spannable = new SpannableString(name);
				spannable.setSpan(new StyleSpan(Typeface.BOLD), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.nameTv.setText(spannable);
			} else {
				holder.nameTv.setText(name);
			}
		} else {
			holder.nameTv.setText(name);
		}

		if (iconTheming)
			holder.iconIv.setImageDrawable(iconParserUtilities.getPackageThemedIcon(app.getPackageName()));
		else
			holder.iconIv.setImageDrawable(app.getIcon());

		if (iconBackground != -1) {
			holder.iconIv.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
			holder.iconIv.setBackgroundResource(iconBackground);
			ColorUtils.applyColor(holder.iconIv, ColorUtils.getDrawableDominantColor(holder.iconIv.getDrawable()));

		}

		convertView.setOnTouchListener((View p1, MotionEvent p2) -> {
			if (p2.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
				rightClickListener.onAppRightClick(app.getPackageName(), p1);
				return true;
			}
			return false;
		});

		return convertView;
	}

	private class ViewHolder {
		ImageView iconIv;
		TextView nameTv;
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new AppFilter();
		return filter;
	}

	private class AppFilter extends Filter {
		@Override
		protected Filter.FilterResults performFiltering(CharSequence p1) {
			FilterResults results = new FilterResults();
			String query = p1.toString().trim().toLowerCase();
			AppAdapter.this.query = query;
			if (query.length() > 1) {
				ArrayList<App> filteredResults = new ArrayList<App>();

				if (query.matches("^[0-9]+(\\.[0-9]+)?[-+/*][0-9]+(\\.[0-9]+)?")) {
					filteredResults.add(new App(Utils.solve(query) + "", context.getPackageName() + ".calc",
							context.getResources().getDrawable(R.drawable.ic_calculator, context.getTheme())));
				}
				for (App app : originalList) {
					if (app.getName().toLowerCase().trim().contains(query)) {
						filteredResults.add(app);
					}
				}
				results.count = filteredResults.size();
				results.values = filteredResults;
			} else {
				synchronized (this) {
					results.values = originalList;
					results.count = originalList.size();
				}
			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence p1, Filter.FilterResults p2) {
			apps = (ArrayList<App>) p2.values;
			notifyDataSetChanged();
			clear();
			for (App app : apps) {
				add(app);
			}
			notifyDataSetInvalidated();
		}
	}

	public interface AppRightClickListener {
		public abstract void onAppRightClick(String app, View view);
	}

}
