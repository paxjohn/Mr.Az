package com.example.contactlistdatabase;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements android.view.View.OnClickListener, OnClickListener {

	ListView lv;
	ArrayList<Contact> list = new ArrayList<Contact>();
	ItemAdapter adapter;
	ContactDatabase db;
	AdapterView.AdapterContextMenuInfo info;
	AlertDialog d , g;
	private Uri imageUri;
	Dialog d4 ,d5 ;
	
	ImageView editImage;
    EditText editName;
    EditText editPhone;
    Uri editImageUri;
    String pastNum;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        this.lv=(ListView) this.findViewById(R.id.listView1);
        
        
        db=new ContactDatabase(this);
        if(db.getAllContact().size()>0){
        	list=db.getAllContact();
        }
        this.adapter=new ItemAdapter(this,list);
        this.lv.setAdapter(adapter);
        this.registerForContextMenu(lv);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

     
    
    
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		int id = item.getItemId();
		switch(id){
		case R.id.edit:
			d5 = new Dialog(this);
			d5.setContentView(R.layout.additem_layout);
						
			d5.setTitle(list.get(info.position).getName());
			editImage = (ImageView) d5.findViewById(R.id.imageView1);
	        editName = (EditText) d5.findViewById(R.id.editText1);
	        editPhone = (EditText) d5.findViewById(R.id.editText2);
	        Button save = (Button) d5.findViewById(R.id.button1);
	        Button cancel = (Button) d5.findViewById(R.id.button2);
			
	        
	        pastNum = list.get(info.position).getTel();
	        editImage.setImageURI(list.get(info.position).getImageUri());
	        editName.setText(list.get(info.position).getName());
	        editPhone.setText(list.get(info.position).getTel());
	        save.setText("Update");
	       
	        
	        editImageUri = list.get(info.position).getImageUri();
	        editImage.setOnClickListener(this);
	        save.setOnClickListener(this);
	        cancel.setOnClickListener(this);

	        d5.show();
			
			break;
		case R.id.view:
			
			ImageView iv = new ImageView(this);
			TextView lblname = new TextView(this);
			TextView lbltel = new TextView(this);
			
			iv.setImageURI(list.get(info.position).getImageUri());
			lblname.setText(list.get(info.position).getName());
			lbltel.setText(list.get(info.position).getTel());
			lbltel.setPadding(10,0,10,10);
			
			LinearLayout l = new LinearLayout(this);
			l.setOrientation(LinearLayout.HORIZONTAL);
			l.addView(iv);
			l.addView(lblname);
			l.addView(lbltel);			
			
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("CONTACT SELECTED");
			b.setView(l);
			b.setNeutralButton("OK", null);
			
			d = b.create();
			d.show();
			
			break;
			
		case R.id.call:
			
			String telephone = list.get(info.position).getTel();
			Uri uri = Uri.parse("tel: "+ telephone);
			Intent intent = new Intent(Intent.ACTION_CALL,uri);
			this.startActivity(intent);
			break;
		case R.id.send:
			Intent in1 = new Intent(android.content.Intent.ACTION_VIEW);
			in1.putExtra("address",list.get(info.position).getTel());
			in1.putExtra("sms_body","");
			in1.setType("vnd.android-dir/mms-sms");
			this.startActivity(in1);
			
			break;
		case R.id.delete:
			
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle("Contact Delete");
			ad.setMessage(String.format("Do you Want to delete %s ? ",list.get(info.position).getName()));
			ad.setPositiveButton("Yes",this);
			ad.setNegativeButton("No", this);
			
			g = ad.create();
			g.show();
		}
		return super.onContextItemSelected(item);
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.contectmenu, menu);
		info = (AdapterContextMenuInfo) menuInfo;
		menu.setHeaderTitle(list.get(info.position).getName());
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(this,AddItemActivity.class);
		this.startActivityForResult(intent, 0);//add new contact
		return super.onOptionsItemSelected(item);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	
	    if(resultCode == Activity.RESULT_OK) {
            if(requestCode == 0) {
                Bundle b = data.getExtras();

                Uri imageUri = b.getParcelable("image");
                String name = b.getString("name");
                String phone = b.getString("phone");

                this.db.addContact(imageUri.toString(), name, phone);
                this.list.add(new Contact(imageUri, name, phone));
                this.adapter.notifyDataSetChanged();
            } else if(requestCode == 200) {
                if(data != null) {
                    editImageUri = data.getData();
                    try {
                        editImage.setImageURI(editImageUri);
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
	
	}
		
		@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int i = arg0.getId();
		
		switch(i){
		case R.id.imageView1:
			Intent in = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            this.startActivityForResult(in, 200);
            break;
		case R.id.button1:
			String setname = editName.getText().toString();
            String setphone = editPhone.getText().toString();

            list.get(info.position).setImageUri(editImageUri);
            list.get(info.position).setName(setname);
            list.get(info.position).setTel(setphone);

            db.deleteContact(pastNum);
            db.addContact(editImageUri.toString(), setname, setphone);
            adapter.notifyDataSetChanged();

            Toast.makeText(this, String.format("%s HAS BEEN EDITED!", setname), Toast.LENGTH_SHORT).show();
	 
	  case R.id.button2:
		  d5.dismiss();
		  Toast.makeText(this, "CANCELED", Toast.LENGTH_SHORT).show();
		  break;
		  
		}
		
	}


		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			switch(arg1){
			case DialogInterface.BUTTON_POSITIVE:

				String phoneNum = list.get(info.position).getTel();
	            list.remove(info.position);
	            db.deleteContact(phoneNum);
	            adapter.notifyDataSetChanged();

	            Toast.makeText(this,"Deleted!", Toast.LENGTH_SHORT).show();
			case DialogInterface.BUTTON_NEGATIVE:
				g.dismiss();
			}
		}
    
    
    
}
