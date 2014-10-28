package cocoatok.cocoatok;

import java.io.*;
import java.net.*;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;
import org.xmlpull.v1.*;

import java.util.ArrayList; 
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

public class chat extends Activity{
	LinearLayout topLL;
	String Chat;
	String encodeChat;
	String room_number;
	String user_name;

	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		Intent intent = getIntent();
		room_number = intent.getExtras().get("room_number").toString();
		user_name = intent.getExtras().get("user_name").toString();
		Spread_Chat(room_number,user_name);	
		final EditText userEdit = (EditText)findViewById(R.id.send_chat);
		Button btnSendUserId = (Button)findViewById(R.id.send_chat_btn);
		btnSendUserId.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v)
			{
				Chat = userEdit.getText().toString();
				Chat = Chat.trim();
				String result;
				int result1 = 0;
				if(Chat.getBytes().length <= 0){ //빈값이 넘어올때의 처리

					Toast.makeText(chat.this, "대화를 입력해주세요.",Toast.LENGTH_SHORT).show();
				}
				else{
					try {
						encodeChat = URLEncoder.encode(Chat,"euc-kr").replace(" ","%20");
						encodeChat = URLEncoder.encode(Chat,"euc-kr").replace("+","%20");
						String url = "http://192.168.1.2/chat.php?chat=" + encodeChat + "&user_name=" + user_name + "&room_number=" + room_number;
						HttpGet request = new HttpGet(url);
						HttpResponse response = new DefaultHttpClient().execute(request);
						topLL.removeAllViews();
						Spread_Chat(room_number,user_name);
						if(response.getStatusLine().getStatusCode() == 200){
							result = EntityUtils.toString(response.getEntity());
							Toast.makeText(chat.this, result, Toast.LENGTH_LONG).show();
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(chat.this,room_number, Toast.LENGTH_LONG).show();
					}
				}

			}
		});
	}
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.outchat,menu);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.invitefriend:
			Intent invite = null;
			invite = new Intent(chat.this,invitefriend.class);	
			invite.putExtra("room_number", room_number);
			startActivity(invite);
			return true;
		case R.id.outchat:
			String result;
			try {
				user_name = URLEncoder.encode(user_name,"euc-kr").replace(" ","%20");
				user_name = URLEncoder.encode(user_name,"euc-kr").replace("+","%20");
				String url = "http://192.168.1.2/out_of_room.php?chat=" + encodeChat + "&user_name=" + user_name + "&room_number=" + room_number;
				HttpGet request = new HttpGet(url);
				HttpResponse response = new DefaultHttpClient().execute(request);
				if(response.getStatusLine().getStatusCode() == 200){
					result = EntityUtils.toString(response.getEntity());
					Toast.makeText(chat.this, result, Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}	

	public void Spread_Chat(String room_number,String user_name){
		boolean user_chat = false;
		boolean other_chat = false;
		try {

			URL server = new URL("http://192.168.1.2/spread_chat.php?room_number="+room_number);
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			parser.setInput( server.openStream(), "euc-kr" );

			int parserEvent = parser.getEventType();
			boolean inuserchat = false;
			boolean inchat = false;
			boolean inuser_id = false;
			boolean indate = false;
			while (parserEvent != XmlPullParser.END_DOCUMENT ){
				if(parserEvent==XmlPullParser.START_TAG)
				{
					if(parser.getName().equalsIgnoreCase("user_chat"))
					{		
						inuserchat = true;
					}
					if(parser.getName().equalsIgnoreCase("chat")){
						inchat = true;
					}
					if(parser.getName().equalsIgnoreCase("user_id")){
						inuser_id = true;
					}
					if(parser.getName().equalsIgnoreCase("date")){
						indate = true;
					}
				}
				else if(parserEvent==XmlPullParser.TEXT)
				{
					if(inuserchat==true)
					{
						if(inuser_id==true){
							if(parser.getText().equalsIgnoreCase(user_name)){
								user_chat  = true;
							}
							else {
								int i = 10;
								int padding = 400;
								get_otherchat(parser.getText(),i,padding);

								other_chat = true;
							}
						}
						if(inchat==true){
							if(user_chat == true){
								int i =15;
								int padding = 300;
								get_mychat(parser.getText(),i,padding);
							}
							if(other_chat == true){
								int i =15;
								int padding = 300;
								get_otherchat(parser.getText(),i,padding);

							}
						}


						if(indate == true){
							if(user_chat  == true){
								int i = 8;
								int padding  = 370;
								get_mychat(parser.getText(),i,padding);
								user_chat = false;
							}
							if(other_chat == true){
								int i = 8;
								int padding  = 370;
								get_otherchat(parser.getText(),i,padding);

								other_chat = false;
							}

						}

					}
				}
				else if(parserEvent==XmlPullParser.END_TAG)
				{	
					if(parser.getName().equalsIgnoreCase("user_chat"))
					{		
						inuserchat = false;
					}
					if(parser.getName().equalsIgnoreCase("chat")){
						inchat = false;
					}
					if(parser.getName().equalsIgnoreCase("user_id")){
						inuser_id = false;
					}
					if(parser.getName().equalsIgnoreCase("date")){
						indate = false;
					}
				}
				parserEvent = parser.next();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void get_mychat (String parser,int i,int padding){
		byte[] eucBytes;
		try {
			eucBytes = parser.getBytes("euc-kr");
			parser = new String(eucBytes,"euc-kr");
			topLL = (LinearLayout)findViewById(R.id.Chat);
			TextView topTV1 = new TextView(chat.this);  
			topTV1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));  
			topTV1.setBackgroundColor(Color.parseColor("#00FFFFFF"));  
			topTV1.setPadding(padding,5,0, 0);
			topTV1.setTextColor(Color.parseColor("#FF7200"));  
			topTV1.setTextSize(i);  
			topTV1.setText(parser);  
			topLL.addView(topTV1); 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public void get_otherchat  (String parser,int i,int padding){
		try {
			byte [] eucBytes = parser.getBytes("euc-kr");
			parser = new String(eucBytes,"euc-kr");
			topLL = (LinearLayout)findViewById(R.id.Chat);
			TextView topTV11 = new TextView(chat.this);  
			topTV11.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));  
			topTV11.setBackgroundColor(Color.parseColor("#00FFFFFF"));  
			topTV11.setPadding(0,5,padding,0);
			topTV11.setTextColor(Color.parseColor("#FF7200"));  
			topTV11.setTextSize(i);  
			topTV11.setText(parser);  
			topLL.addView(topTV11); 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}



















