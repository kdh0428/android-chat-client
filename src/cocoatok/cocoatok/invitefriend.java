package cocoatok.cocoatok;

import java.io.*; 
import java.net.*;
import java.util.ArrayList; 

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class invitefriend extends Activity{
	protected static final String HttpGet = null;
	String room_number;
	String user_friend_name;
	String user_friend_name_to_send;

	int i = 0;
	@Override
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.invitefriend);

		Intent intent = getIntent();
		room_number = intent.getExtras().get("room_number").toString();
		final EditText friend_name = (EditText)findViewById(R.id.friend_invite_edit);
		Button btnSendUserId = (Button)findViewById(R.id.inviteButton);
		btnSendUserId.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				user_friend_name = friend_name.getText().toString();
				user_friend_name = user_friend_name.trim();
				int result1 = 0;



				if(user_friend_name.getBytes().length <= 0){ //빈값이 넘어올때의 처리

					Toast.makeText(invitefriend.this, "유저 이름을 입력해주세요.",Toast.LENGTH_SHORT).show();
				}
				else{
					try{
						user_friend_name_to_send = URLEncoder.encode(user_friend_name,"euc-kr").replace(" ","%20");
						user_friend_name_to_send = URLEncoder.encode(user_friend_name,"euc-kr").replace("+","%20");
						String url = "http://192.168.1.2/invite_room.php?room_number="+room_number+"&friend_name="+user_friend_name_to_send;
						HttpGet request = new HttpGet(url);
						HttpResponse response = new DefaultHttpClient().execute(request);
						String result;
						if(response.getStatusLine().getStatusCode() == 200){
							result = EntityUtils.toString(response.getEntity());
							Toast.makeText(invitefriend.this, result, Toast.LENGTH_LONG).show();
							onBackPressed();

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}
}

