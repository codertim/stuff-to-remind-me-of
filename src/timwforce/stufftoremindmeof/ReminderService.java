package timwforce.stufftoremindmeof;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class ReminderService extends Service {


		@Override
		public void onCreate() {
			Log.d("ReminderService#onCreate", "Starting ...");
			
		}
		
		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
			Log.d("ReminderService#onStartCommand", "Starting ...");
			// wait requested time to sent notification
			// sendNotification("999");
			// startService(new Intent(null, ReminderService.class));
			
			Bundle extras = intent.getExtras();
			Log.d("ReminderService#onStartCommand", "extras bundle = " + extras);
			Log.d("ReminderService#onStartCommand", "message = " + (String) extras.get(MainActivity.MESSAGE_KEY));
			
			new StartServiceAsyncTask().execute(extras.get(MainActivity.MESSAGE_KEY), getBaseContext());

			return 0;
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
}
