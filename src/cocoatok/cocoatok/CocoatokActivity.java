package cocoatok.cocoatok;

import java.io.*;   
import java.net.*;

import org.xmlpull.v1.*;

import java.util.ArrayList; 
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;

import android.widget.AdapterView.OnItemClickListener;


public class CocoatokActivity extends Activity {
	ArrayList<String> UserName = new ArrayList<String>();
	ArrayAdapter<String> Adapter;
	ArrayList<String> FriendList= new ArrayList<String>();
	private final int MAX = 30;
	Integer[] RoomNumber = new Integer[MAX];
	String user_name = "";

	//어뎁터 및 채팅리스트

	ListView List=null;
	ListView ChatLi=null;
	String friend_name;
	String user_friend_name_to_send;
	//

	int check= 0 ;
	int room = 0;

	private int i=0;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

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
		setContentView(R.layout.main);  
		if(user_name !=""){
		}
		else
		{
			Intent user_maker = new Intent(this,user_maker.class);
			startActivity(user_maker);
			setContentView(R.layout.main);  
		}
		tabhost();
	}
	public void onStart(){
		super.onStart();
		FriendList.removeAll(FriendList);
		UserName.removeAll(UserName);

		FriendList();

		ChatList();



		Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,FriendList);
		Adapter.notifyDataSetChanged();
		List = (ListView)findViewById(R.id.friendList);
		List.setAdapter(Adapter);




		ArrayAdapter<String> ChatAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,UserName);

		ChatLi = (ListView)findViewById(R.id.chatList);
		ChatLi.setAdapter(ChatAdapter);


		List.setOnItemClickListener(itemClickListenerOfFriendList);
		ChatLi.setOnItemClickListener(itemClickListenerOfChatList);
	}


	public OnItemClickListener itemClickListenerOfFriendList = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id)
		{
			friend_name = ((TextView)clickedView).getText().toString();
			try{
				user_friend_name_to_send = URLEncoder.encode(friend_name,"euc-kr").replace(" ","%20");
				user_friend_name_to_send = URLEncoder.encode(friend_name,"euc-kr").replace("+","%20");
				String url = "http://192.168.1.2/room_maker.php?user_name="+user_name+"&user_friend_name="+user_friend_name_to_send;
				HttpGet request = new HttpGet(url);
				HttpResponse response = new DefaultHttpClient().execute(request);
				String result;
				if(response.getStatusLine().getStatusCode() == 200){
					result = EntityUtils.toString(response.getEntity());

					Intent chat = null;
					chat = new Intent(CocoatokActivity.this,chat.class);
					chat.putExtra("room_number",result);
					chat.putExtra("user_name", user_name);
					startActivity(chat);
				}
			} catch (Exception e) {
				Toast.makeText(CocoatokActivity.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}

		}
	};

	public OnItemClickListener itemClickListenerOfChatList = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id)
		{
			Intent chat = null;
			chat = new Intent(CocoatokActivity.this,chat.class);	
			chat.putExtra("room_number",RoomNumber[pos].toString());
			chat.putExtra("user_name", user_name);
			startActivity(chat);
		}
	};


	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.selectmenu,menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.friendadd:
			Intent intent_add = new Intent(this,friend_add.class);
			startActivity(intent_add);
			return true;
		case R.id.frienddel:

			Intent intent_del = new Intent(this,friend_del.class);
			startActivity(intent_del);
			return true;
		}
		return false;
	}	


	public void tabhost(){
		TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
		tabHost.setup(); 
		TabHost.TabSpec spec; 

		spec = tabHost.newTabSpec("Tab 00"); 
		spec.setIndicator("Friend");        
		spec.setContent(R.id.layout);       
		tabHost.addTab(spec);              

		spec = tabHost.newTabSpec("Tab 01"); 
		spec.setIndicator("Chat");        
		spec.setContent(R.id.layout2);    
		tabHost.addTab(spec);              

		tabHost.setCurrentTab(0);		
	}


	public void FriendList(){
		try {

			URL server = new URL("http://192.168.1.2/show_friend.php?user_name="+user_name);
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			parser.setInput( server.openStream(), "euc-kr" );

			int parserEvent = parser.getEventType();
			boolean inTitle = false;
			boolean inuserfriend = false;
			while (parserEvent != XmlPullParser.END_DOCUMENT ){
				if(parserEvent==XmlPullParser.START_TAG)
				{
					if(parser.getName().equalsIgnoreCase("Friend"))
					{		
						inTitle = true;
					}
					if(parser.getName().equalsIgnoreCase("user_friend")){
						inuserfriend = true;
					}
				}
				else if(parserEvent==XmlPullParser.TEXT)
				{
					if(inTitle==true)
					{
						if(inuserfriend==true){
							FriendList.add(parser.getText());
						}
					}
				}
				else if(parserEvent==XmlPullParser.END_TAG)
				{	
					if(parser.getName().equalsIgnoreCase("Friend"))
					{
						inTitle = false;
					}
					if(parser.getName().equalsIgnoreCase("user_friend")){
						inuserfriend = false;
					}
				}
				parserEvent = parser.next();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public void ChatList(){
		try {

			URL server = new URL("http://192.168.1.2/participated_room.php?user_name="+user_name);
			XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
			XmlPullParser parser = parserCreator.newPullParser();
			parser.setInput( server.openStream(), "euc-kr" );

			int parserEvent = parser.getEventType();
			boolean inTitle = false;
			boolean inroom = false;
			boolean inpart = false;
			while (parserEvent != XmlPullParser.END_DOCUMENT ){
				if(parserEvent==XmlPullParser.START_TAG)
				{
					if(parser.getName().equalsIgnoreCase("participated_room"))
					{		
						inTitle = true;
					}
					if(parser.getName().equalsIgnoreCase("room_number")){
						inroom = true;
					}
					if(parser.getName().equalsIgnoreCase("participated_user_name")){
						inpart = true;
					}
				}
				else if(parserEvent==XmlPullParser.TEXT)
				{
					if(inTitle==true)
					{
						if(inroom==true){
							RoomNumber[room]=Integer.parseInt(parser.getText());
							room++;
						}
						if(inpart == true){
							UserName.add(parser.getText());
						}
					}
				}
				else if(parserEvent==XmlPullParser.END_TAG)
				{	
					if(parser.getName().equalsIgnoreCase("participated_room"))
					{
						inTitle = false;
					}
					if(parser.getName().equalsIgnoreCase("room_number")){
						inroom = false;
					}
					if(parser.getName().equalsIgnoreCase("participated_user_name")){
						inpart = false;
					}
				}
				parserEvent = parser.next();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		room = 0;

	}


}

