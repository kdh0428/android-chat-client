package cocoatok.cocoatok;

import java.io.* ; 
import java.net.*;
import java.util.ArrayList; 

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;
import org.xmlpull.v1.*;

import android.app.*;
import android.content.*;
import android.database.sqlite.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class user_maker extends Activity{


	String user_name;
	String user_name_to_send;
	String result;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_add);


		final EditText userEdit = (EditText)findViewById(R.id.user_maker);
		Button btnSendUserId = (Button)findViewById(R.id.user_make_btn);
		btnSendUserId.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v)
			{
				user_name = userEdit.getText().toString();
				user_name = user_name.trim();
				int result1 = 0;



				if(user_name.getBytes().length <= 0){ //빈값이 넘어올때의 처리

					Toast.makeText(user_maker.this, "유저 이름을 입력해주세요.",Toast.LENGTH_SHORT).show();
				}
				else{
					try {
						user_name_to_send = URLEncoder.encode(user_name,"euc-kr").replace(" ","%20");
						user_name_to_send = URLEncoder.encode(user_name,"euc-kr").replace("+","%20");
						String url = "http://192.168.1.2/user_maker.php?user_name="+user_name_to_send;
						HttpGet request = new HttpGet(url);
						HttpResponse response = new DefaultHttpClient().execute(request);

						if(response.getStatusLine().getStatusCode() == 200){
							result = EntityUtils.toString(response.getEntity());
							Toast.makeText(user_maker.this, result, Toast.LENGTH_LONG).show();
							if(result.equalsIgnoreCase("Succeed!")){
								try{
									FileOutputStream os  = openFileOutput("user_name.txt",MODE_PRIVATE);	
									os.write(user_name_to_send.getBytes());
									os.close();			
								}catch (Exception e) {
									Toast.makeText(user_maker.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
								}

								onBackPressed();
							}
						}
					} catch (Exception e) {
						Toast.makeText(user_maker.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				}
			}
		});

	}
}
