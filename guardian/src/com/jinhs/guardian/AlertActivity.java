package com.jinhs.guardian;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.jinhs.helper.AccountInfoHelper;
import com.jinhs.helper.FileReadingHelper;
import com.jinhs.rest.RestClient;

public class AlertActivity extends Activity {
	private GestureDetector mGestureDetector;
	
	private TextView textViewTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		
		mGestureDetector = createGestureDetector(this);
		textViewTimer = (TextView)findViewById(R.id.textView_timer);
	}
	
	@Override
	protected void onResume() {
		Log.d("onResume()","start");
		super.onResume();
		
		new CountDownTimer(10*1000, 1000) {

		     public void onTick(long millisUntilFinished) {
		    	 textViewTimer.setText("" + millisUntilFinished / 1000);
		     }

		     public void onFinish() {
		    	 Toast.makeText(getBaseContext(), "alert is sent", Toast.LENGTH_LONG).show();
		    	 finish();
		     }
		  }.start();
	}

	@Override
	protected void onPause() {
		Log.d("onPause()","start");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d("onStop()","start");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d("onDestroy()","start");
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		Log.d("onBackPressed()","start");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alert, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection. Menu items typically start another
        // activity, start a service, or broadcast another intent.
        switch (item.getItemId()) {
            case R.id.menu_cancel_alert:
            	Toast.makeText(getBaseContext(), "alert is canceled", Toast.LENGTH_LONG).show();
            	finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
	}
	
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		gestureDetector.setBaseListener(new gestureListener());
		return gestureDetector;
	}
	
	private class gestureListener implements GestureDetector.BaseListener{

		@Override
		public boolean onGesture(Gesture gesture) {
			if (gesture == Gesture.TAP) {
				openOptionsMenu();
                return true;
            } else if (gesture == Gesture.TWO_TAP) {
                return true;
            } else if (gesture == Gesture.SWIPE_RIGHT) {
            	new AlertSendingTask().execute();
            	finish();
                return true;
            } else if (gesture == Gesture.SWIPE_LEFT) {
            	new AlertSendingTask().execute();
                finish();
                return true;
            }
			return false;
		}
	}

	private class AlertSendingTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(Void... arg0) {
			Log.d("alert","sent");
			try {
				new RestClient().sendAlert(AccountInfoHelper.getEmail(getBaseContext()));
			} catch (ClientProtocolException e) {
				Log.d("ClientProtocolException","alert send e:"+e.getMessage());
				//return "Alert send failed, check your network connection";
			} catch (IOException e) {
				Log.d("IOException","alert send e:"+e.getMessage());
				//return "Alert send failed, check your network connection";
			}
			return "alert is sent";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
		}
	}
}
