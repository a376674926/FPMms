
package com.android.mms.activities;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.android.mms.app.Constants;
import com.android.mms.entitis.Contact;
import com.android.mms.utils.Cn2Spell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.android.mms.R;
import com.android.mms.R.string;

/**
 * 通讯录界面类
 */
public class ContactsListActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener, View.OnClickListener {
    private LinearLayout mAddContactLayout;
    private static final int PHONE_CONTACT_LOADER_ID = 1;
    public static final String INSERT_TYPE = "insert_type" ;
    public static final String INSERT_NUMBER = "insert_number" ;
    public static final String INSERT_NAME = "insert_name" ;
    public static final String INSERT_RECIPIENT = "insert_recipient" ;
    
    private final ContactsLoader mContactsLoaderCallbacks = new ContactsLoader();
    private String mCurFilter;
    private SimpleCursorAdapter mAdapter;
    private TextView mEmptyTextView;
    private ListView mListView;
    private String mSelectedContactName;
    private String mSelectedContactNumber;
    
    //是否选择了添加新联系人
    private boolean mIsSelectedAddNewContact ;
    //插入类型
    private String insertType ; 
    
    private static final String[] PHONES_PROJECTION = new String[] {
            Contacts._ID, Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID
    };

    private ArrayList<Contact> mContactInfos = new ArrayList<Contact>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.contact_header_add);
        View headerView = mAboveViewStub.inflate();

        mMiddleViewStub.setLayoutResource(R.layout.activity_main_listview);
        View listView = mMiddleViewStub.inflate();
        mEmptyTextView = (TextView) listView.findViewById(R.id.empty);

        mAddContactLayout = (LinearLayout) headerView.findViewById(R.id.item_contact_header_add);
        mAddContactLayout.setFocusable(true);
        mAddContactLayout.setFocusableInTouchMode(true);
        mAddContactLayout.setOnFocusChangeListener(new MenuItemFocusChangeListener());

        mAddContactLayout.setOnClickListener(this);

        setBottomKeyClickListener(this);
        setActivityBgResource(0);

        mListView = (ListView) findViewById(R.id.main_list_view);

        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item_type_singleline_text, null,
                new String[] {
                        Phone.DISPLAY_NAME
                }, new int[] {
                        R.id.list_text
                }, 0);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemSelectedListener(new MenuItemSelectedListener());
        mListView.setOnItemClickListener(new MenuItemClickListener());
        mListView.setOnFocusChangeListener(new MenuItemFocusChangeListener());

        getLoaderManager().initLoader(PHONE_CONTACT_LOADER_ID, null, mContactsLoaderCallbacks);
        
        Intent intent = getIntent() ;
        if(intent != null){
        	Bundle bundle = intent.getExtras() ;
        	if(bundle != null){
        		insertType = bundle.getString(INSERT_TYPE) ;
        	}
        }
    }

    @Override
    public Button BuildLeftBtn(Button v) {
        v.setText(R.string.option_ok);
        return v;
    }

    @Override
    public Button BuildMiddleBtn(Button v) {
        return null;
    }

    @Override
    public Button BuildRightBtn(Button v) {
        v.setText(R.string.back);
        return v;
    }

    @Override
    public TextView BuildTopTitle(TextView v) {
        v.setText(R.string.title_contacts);
        return v;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (R.id.item_contact_header_add == v.getId()) {
            Intent intent = new Intent(this, AddNewContactActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onLeftKeyPress() {
        optionOk() ;
    }

	@Override
    public void onMiddleKeyPress() {

    }

    @Override
    public void onRightKeyPress() {
        finish() ;
    }

    private void updateSelectedContact(String name, String number) {
        mSelectedContactName = name;
        mSelectedContactNumber = number;
    }

    /**
     *  确定
     */
    private void optionOk() {
    	if(!mIsSelectedAddNewContact){
    		Intent intent = new Intent(this, CreateNewMmsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.OPTION, CreateNewMmsActivity.ADVANCED_OPTIONS) ;
            if(insertType.equals(INSERT_NAME)){
            	bundle.putString(CreateNewMmsActivity.INSERT_INFO, mSelectedContactName);
            }else if(insertType.equals(INSERT_NUMBER)){
                bundle.putString(CreateNewMmsActivity.INSERT_INFO, mSelectedContactNumber);
            }else{//插入收信人编辑框
            	bundle.putString(Constants.OPTION, CreateNewMmsActivity.ADD_CONTACTS) ;
            	bundle.putString(CreateNewMmsActivity.INSERT_RECIPIENT, mSelectedContactNumber);
            }
            intent.putExtras(bundle);
            startActivity(intent);
    	}else{
    		Intent intent = new Intent(this, AddNewContactActivity.class);
    		startActivity(intent) ;
    	}
	}
    
    private class NameComparator implements Comparator<Contact> {

        @Override
        public int compare(Contact element1, Contact element2) {
            String str1 = Cn2Spell.CN2FirstCharSpell(element1.getName());
            String str2 = Cn2Spell.CN2FirstCharSpell(element2.getName());
            return str1.compareToIgnoreCase(str2);
        }
    }

    private class MenuItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Contact info = mContactInfos.get(position);
            updateSelectedContact(info.getName(), info.getNumber());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private class MenuItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	mIsSelectedAddNewContact = false ;
        	Contact info = mContactInfos.get(position);
            updateSelectedContact(info.getName(), info.getNumber());
        	optionOk() ;
        }
    }

    private class MenuItemFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (v.getId() == R.id.main_list_view && hasFocus) {
            	mIsSelectedAddNewContact = false ;
                int position = ((ListView) v).getSelectedItemPosition();
                if (position > -1) {
                    Contact info = mContactInfos.get(position);
                    updateSelectedContact(info.getName(), info.getNumber());
                }
            } else if (v.getId() == R.id.item_contact_header_add && hasFocus) {
            	mIsSelectedAddNewContact = true ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(PHONE_CONTACT_LOADER_ID);
    }

    private class ContactsLoader implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri baseUri;

            if (mCurFilter != null) {
                baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                        Uri.encode(mCurFilter));
            } else {
                baseUri = Phone.CONTENT_URI;
            }

            String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                    + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                    + Contacts.DISPLAY_NAME + " != '' ))";

            return new CursorLoader(getApplicationContext(), baseUri,
                    PHONES_PROJECTION, select, null,
                    Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
        }

        
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mEmptyTextView.setVisibility((cursor.getCount() > 0) ? View.GONE : View.VISIBLE);

            if ((mContactInfos != null) && (!mContactInfos.isEmpty())) {
                mContactInfos.clear();
            }

            while (cursor.moveToNext()) {
                Contact info = new Contact();
                info.setId(cursor.getInt(cursor.getColumnIndex(Contacts._ID)));
                info.setName(cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME)));
                info.setNumber(cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
                info.setPhotoId(cursor.getInt(cursor.getColumnIndex(Photo.PHOTO_ID)));
                mContactInfos.add(info);
            }

            Collections.sort(mContactInfos, new NameComparator());
            Cursor newCursor = createCursor(mContactInfos);
            mAdapter.changeCursor(newCursor);
        }

        private Cursor createCursor(ArrayList<Contact> infos) {
            MatrixCursor cursor = new MatrixCursor(new String[] {
                    Contacts._ID, Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID
            });
            for (Contact info : infos) {
                cursor.addRow(new Object[] {
                        info.getId(), info.getName(), info.getNumber(), info.getPhotoId()
                });
            }
            return cursor;
        }

        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            mAdapter.swapCursor(null);
        }
    }
}
