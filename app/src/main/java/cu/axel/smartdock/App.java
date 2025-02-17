package cu.axel.smartdock;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import cu.axel.smartdock.activities.DebugActivity;
import cu.axel.smartdock.utils.Utils;

public class App extends Application {
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	@Override
	public void onCreate() {
		uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable exception) {

                    String report = "Exception: " + exception + "\n";
                    
                    for (StackTraceElement element : exception.getStackTrace())
                        report += element.toString() + "\n";

                    Throwable cause = exception.getCause();

                    if (cause != null) {
                        report += "Cause: " + cause + "\n";
                        for (StackTraceElement element:cause.getStackTrace())
                            report += element.toString() + "\n";
                        
                    }

                    String message=exception.getMessage();
                    if (message != null) 
                        report += "Message: " + message;
                        
                    Intent intent = new Intent(getApplicationContext(), DebugActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("report", report);
                    //startActivity(intent);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(2);
                    uncaughtExceptionHandler.uncaughtException(thread, exception);
                }
            });
		super.onCreate();
Logger.initialize(this);
;
	}

}
