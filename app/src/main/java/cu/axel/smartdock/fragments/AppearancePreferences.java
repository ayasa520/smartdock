package cu.axel.smartdock.fragments;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import cu.axel.smartdock.R;
import androidx.preference.Preference;
import android.content.Intent;
import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.SeekBar;
import android.text.TextWatcher;
import android.text.Editable;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.content.DialogInterface;
import cu.axel.smartdock.utils.Utils;
import android.widget.TextView;
import android.widget.TabHost;
import android.content.res.ColorStateList;
import android.widget.GridView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import cu.axel.smartdock.utils.ColorUtils;

public class AppearancePreferences extends PreferenceFragmentCompat {
	private Preference mainColorPref;

	@Override
	public void onCreatePreferences(Bundle arg0, String arg1) {
		setPreferencesFromResource(R.xml.preferences_appearance, arg1);
		mainColorPref = findPreference("theme_main_color");
		mainColorPref.setOnPreferenceClickListener((Preference p1) -> {
				showColorPickerDialog(getActivity(), "main");
				return false;
		});

		findPreference("theme").setOnPreferenceChangeListener((Preference p1, Object p2) -> {
				mainColorPref.setEnabled(p2.toString().equals("custom"));
				return true;
		});

		mainColorPref.setEnabled(mainColorPref.getSharedPreferences().getString("theme", "dark").equals("custom"));
	}

	public void showColorPickerDialog(Context context, final String type) {
		MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(getActivity());
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null);
		final View colorPreview = view.findViewById(R.id.color_preview);
		final EditText colorHexEt = view.findViewById(R.id.color_hex_et);
		final SeekBar alphaSb = view.findViewById(R.id.color_alpha_sb);
		final SeekBar redSb = view.findViewById(R.id.color_red_sb);
		final SeekBar greenSb = view.findViewById(R.id.color_green_sb);
		final SeekBar blueSb = view.findViewById(R.id.color_blue_sb);
		final TextView alphaTv = view.findViewById(R.id.color_alpha_tv);
		redSb.setProgressTintList(ColorStateList.valueOf(Color.RED));
		greenSb.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
		blueSb.setProgressTintList(ColorStateList.valueOf(Color.BLUE));

		final TabHost th = view.findViewById(R.id.dialog_color_th);
		th.setup();
		th.addTab(th.newTabSpec("custom").setIndicator(getString(R.string.custom))
				.setContent(R.id.custom_color_container));
		th.addTab(th.newTabSpec("presets").setIndicator(getString(R.string.presets))
				.setContent(R.id.presets_color_container));
		th.addTab(th.newTabSpec("wallpaper").setIndicator(getString(R.string.wallpaper))
				.setContent(R.id.wallpaper_color_container));

		colorHexEt.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {
			}

			@Override
			public void onTextChanged(CharSequence p1, int p2, int p3, int p4) {
			}

			@Override
			public void afterTextChanged(Editable p1) {
				String hexColor = p1.toString();
				int color;

				if (hexColor.length() == 7 && (color = ColorUtils.toColor(hexColor)) != -1) {
					colorPreview.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
					redSb.setProgress(Color.red(color));
					greenSb.setProgress(Color.green(color));
					blueSb.setProgress(Color.blue(color));
				} else
					colorHexEt.setError(getString(R.string.invalid_color));
			}
		});
		alphaSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
				alphaTv.setText(p2 + "/255");
				colorPreview.getBackground().setAlpha(p2);
			}

			@Override
			public void onStartTrackingTouch(SeekBar p1) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar p1) {
			}
		});
		redSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
				colorHexEt.setText(ColorUtils
						.toHexColor(Color.rgb(redSb.getProgress(), greenSb.getProgress(), blueSb.getProgress())));
			}

			@Override
			public void onStartTrackingTouch(SeekBar p1) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar p1) {
			}
		});
		greenSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
				colorHexEt.setText(ColorUtils
						.toHexColor(Color.rgb(redSb.getProgress(), greenSb.getProgress(), blueSb.getProgress())));
			}

			@Override
			public void onStartTrackingTouch(SeekBar p1) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar p1) {
			}
		});
		blueSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
				colorHexEt.setText(ColorUtils
						.toHexColor(Color.rgb(redSb.getProgress(), greenSb.getProgress(), blueSb.getProgress())));
			}

			@Override
			public void onStartTrackingTouch(SeekBar p1) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar p1) {
			}
		});
		dialog.setNegativeButton(R.string.cancel, null);
		dialog.setPositiveButton(R.string.ok, (DialogInterface p1, int p2) -> {
				String color = colorHexEt.getText().toString();
				if (ColorUtils.toColor(color) != -1) {
					mainColorPref.getSharedPreferences().edit().putString(mainColorPref.getKey(), color).commit();
					mainColorPref.getSharedPreferences().edit().putInt("theme_main_alpha", alphaSb.getProgress())
							.commit();
				}
		});
		dialog.setView(view);
		alphaSb.setProgress(mainColorPref.getSharedPreferences().getInt("theme_main_alpha", 255));
		String hexColor = mainColorPref.getSharedPreferences().getString(mainColorPref.getKey(), "#212121");
		colorHexEt.setText(hexColor);

		GridView presetsGv = view.findViewById(R.id.presets_gv);
		presetsGv.setAdapter(
				new HexColorAdapter(context, context.getResources().getStringArray(R.array.default_color_values)));

		presetsGv.setOnItemClickListener((AdapterView<?> p1, View p2, int p3, long p4) -> {
				colorHexEt.setText(p1.getItemAtPosition(p3).toString());
				th.setCurrentTab(0);
		});

		GridView wallColorsGv = view.findViewById(R.id.wallpaper_colors_gv);
		wallColorsGv.setAdapter(new HexColorAdapter(context, ColorUtils.getWallpaperColors(context)));

		wallColorsGv.setOnItemClickListener((AdapterView<?> p1, View p2, int p3, long p4) -> {
				colorHexEt.setText(p1.getItemAtPosition(p3).toString());
				th.setCurrentTab(0);
		});

		dialog.show();

	}

	class HexColorAdapter extends ArrayAdapter<String> {
		private Context context;

		public HexColorAdapter(Context context, String[] colors) {
			super(context, R.layout.color_entry, colors);
			this.context = context;
		}

		public HexColorAdapter(Context context, ArrayList<String> colors) {
			super(context, R.layout.color_entry, colors);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = LayoutInflater.from(context).inflate(R.layout.color_entry, null);

			((ImageView) convertView.findViewById(R.id.color_entry_iv)).getBackground()
					.setColorFilter(Color.parseColor(getItem(position)), PorterDuff.Mode.SRC_ATOP);

			return convertView;
		}

	}
}
