package timwforce.stufftoremindmeof;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener {
	private static final int            TEXT_VIEW_HEIGHT = 50;
	private static final int            TEXT_VIEW_WIDTH  = 200;
	public static final  List<Reminder> reminders        = new ArrayList<Reminder>();
	private static       LinearLayout   ll               = null;
	public static final  String         MESSAGE_KEY      = "MESSAGE";
	public static final  String         MY_PREFS         = "MY_PREFS";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// setContentView(R.layout.activity_main);
		
		// default minutes is 1, which is first in array, position 0
    	SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE).edit();
    	editor.putInt("whichTimeSelected", 0);
    	editor.commit();

		
		ll = new LinearLayout(this);
		setupLayout(ll);
		setContentView(ll);
	}
	
	
	private void setupLayout(LinearLayout ll) {
		// overall layout
		ll.setOrientation(LinearLayout.VERTICAL);
		// ll.setBackgroundColor(getResources().getColor(R.color.main_background));	
		ll.setBackgroundResource(R.drawable.drawable_gradient_background);

		// button for new reminder
		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		buttonLayoutParams.setMargins(10, 20, 10, 2);
		Button newReminderButton = new Button(this);
		newReminderButton.setText(getResources().getText(R.string.new_button_label));
		newReminderButton.setTextColor(getResources().getColor(R.color.button_font));   // set button font color same as app background
		newReminderButton.setOnClickListener(this);
		newReminderButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f);
		ll.addView(newReminderButton, buttonLayoutParams);
		

		// text 
		LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(TEXT_VIEW_WIDTH, TEXT_VIEW_HEIGHT);
		textLayoutParams.setMargins(10, 10, 10, 10);
		
		// add reminder list label
		TextView tvSubtitle = new TextView(this);
		tvSubtitle.setTypeface(null, Typeface.BOLD);
		tvSubtitle.setText("Reminders ...");
		tvSubtitle.setTextColor(getResources().getColor(R.color.text_view_font_color));
		tvSubtitle.setTextSize(16.0f);
		tvSubtitle.setGravity(Gravity.CENTER);
		ll.addView(tvSubtitle, textLayoutParams);
		
		// add individual reminders
		for(Reminder reminder : reminders) {
			reminder.addTextViewToLayout(ll, this, textLayoutParams);
		}
	}
	
	private void askUserForNewReminder() {
		LayoutInflater layoutInflater = this.getLayoutInflater();
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.MyCustomAlertDialog));
		final View dialogView = layoutInflater.inflate(R.layout.dialog_new_message, null);
		alertDialogBuilder.setView(dialogView);
		alertDialogBuilder.setTitle("Select minutes");
		// alertDialogBuilder.setContentView(R.layout.dialog_time_input_view);
		// alertDialogBuilder.setMessage("Enter time in minutes");   // cannot have array and message at same time
		// alertDialogBuilder.setItems(R.array.minutes_array, null);
		Log.d("onClick", "Calling setItems ...");

		alertDialogBuilder.setSingleChoiceItems(R.array.minutes_array, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Log.d("MainActivity SingleChoiceItems#onClick", "which=" + which);
            	SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE).edit();
            	editor.putInt("whichTimeSelected", which);
            	editor.commit();
            }
		});
		Log.d("onClick", "GOT HERE 1");

		alertDialogBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				// do nothing
			}
		
		});
		
		Log.d("onClick", "Setting cancelable");
		alertDialogBuilder.setCancelable(true);		
		
		// final View v = getLayoutInflater().inflate(R.layout.dialog_new_message, null);
		
		alertDialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				Log.d("onClick", "setPositiveButton#onClick - arg1 = " + arg1);
				// final EditText myEditText = (EditText) alertDialog.findViewById(R.id.edit_text_message);
				EditText myEditText = (EditText) dialogView.findViewById(R.id.edit_text_message);
				Log.d("onClick", "myEditText: " + myEditText);
				Log.d("onClick", "myEditText.getText(): " + myEditText.getText());

				String message = myEditText.getText().toString();
				Log.d("onClick", "User entered message: " + message);
				reminders.add(new Reminder(message, 9));
				ll.invalidate();
				dialog.cancel();
				
				Intent serviceIntent = new Intent(MainActivity.this, ReminderService.class);
				serviceIntent.putExtra(MainActivity.MESSAGE_KEY, message);
				startService(serviceIntent);
			}
		
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		Log.d("onClick", "Created AlertDialog");
		
		alertDialog.show();		
	}
	
	
	public void onClick(View v) {
		Log.d("MainActivity", "onClick v.getId() = " + v.getId());
		
		// sendNotification();
		
		askUserForNewReminder();	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		// getMenuInflater().inflate(R.menu.main, menu);
		// return true;
		
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, Prefs.class));
			return true;
		}
		return false;
	}

}
