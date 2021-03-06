package com.lockscreen;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Settings extends Activity {

	Spinner launchChooser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting);

		launchChooser = (Spinner) findViewById(R.id.spinLauncher);

		PackageManager pm = getPackageManager();
		Intent i = new Intent("android.intent.action.MAIN");
		i.addCategory("android.intent.category.HOME");
		List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
		List<String> launchers = new ArrayList<String>();
		Log.d("com.lockscreen",
				String.format("Is list empty?: %b", lst.isEmpty()));
		if (!lst.isEmpty()) {
			for (ResolveInfo l : lst) {
				if (!l.activityInfo.packageName.equals("com.lockscreen"))
					launchers.add(l.activityInfo.packageName);
				Log.d("com.lockscreen", String.format("component: '%s'",
						l.activityInfo.packageName));
			}
			ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, launchers);
			adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			launchChooser.setAdapter(adp1);
		}

		final SharedPreferences settings = getSharedPreferences(
				LockScreenAppActivity.PREF_FILE, 0);
		String launcher = settings.getString(LockScreenAppActivity.LAUNCHER,
				"launcher");

		if (launchers.contains(launcher)) {
			launchChooser.setSelection(launchers.indexOf(launcher));
		}

		launchChooser.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> v, View selected,
					int pos, long id) {
				String selectedLauncher = v.getItemAtPosition(pos).toString();

				SharedPreferences.Editor editor = settings.edit();
				editor.putString(LockScreenAppActivity.LAUNCHER,
						selectedLauncher);
				editor.commit();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		startService(new Intent(this, MyService.class));

	}

	public void openRecord(View v) {
		Intent intent = new Intent(this, RecordGesture.class);
		startActivity(intent);
	}

	public void testGesture(final View v) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Test Gesture");
		alertDialog
				.setMessage("WARNING: You are about to set the sensitivity of your gesture. A message shows the score of your gestures. Use this as a basis to set your sensitivity.");
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(v.getContext(),
								ConfirmGesture.class);
						startActivityForResult(intent, 2);

					}

				});
		alertDialog.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
