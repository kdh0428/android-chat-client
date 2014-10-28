package cocoatok.cocoatok;

import java.io.*; 
import java.net.*;
import java.util.ArrayList; 

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class friend_add extends Activity{
	protected static final String HttpGet = null;
	String user_name;
	String user_friend_name;
	String user_friend_name_to_send;

	int i = 0;
	@Override
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendadd);

		try{

			FileInputStream is = openFileInput("user_name.txt");
			byte buffer [] = new byte[is.available()];
			while(i > -1){
				i = is.read(buffer);
			}
			is.close();
			user_name  = (new String(buffer));
		}catch(Exception e){
		}
		final EditText friend_name = (EditText)findViewById(R.id.friend_add_edit);
		Button btnSendUserId = (Button)findViewById(R.id.addButton);
		btnSendUserId.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				user_friend_name = friend_name.getText().toString();
				try{
					user_friend_name_to_send = URLEncoder.encode(user_friend_name,"euc-kr").replace(" ","%20");
					user_friend_name_to_send = URLEncoder.encode(user_friend_name,"euc-kr").replace("+","%20");
					String url = "http://192.168.1.2/friend_add.php?user_name="+user_name+"&user_friend_name="+user_friend_name_to_send;
					HttpGet request = new HttpGet(url);
					HttpResponse response = new DefaultHttpClient().execute(request);
					String result;
					if(response.getStatusLine().getStatusCode() == 200){
						result = EntityUtils.toString(response.getEntity());
						Toast.makeText(friend_add.this, result, Toast.LENGTH_LONG).show();
						onBackPressed();

					}
				} catch (Exception e) {
					Toast.makeText(friend_add.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		});
	}
}





