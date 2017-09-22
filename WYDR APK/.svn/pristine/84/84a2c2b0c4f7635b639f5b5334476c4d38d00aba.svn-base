package wydr.sellers.registration;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import wydr.sellers.acc.NetSchema;
import wydr.sellers.acc.ValidationUtil;
import wydr.sellers.activities.AppUtil;
import wydr.sellers.activities.ChatProvider;
import wydr.sellers.holder.DataHolder;
import wydr.sellers.slider.ContactsDb;
import wydr.sellers.slider.UserFunctions;

/**
 * Created by aurigait on 24/7/15.
 */
public class
        ContactsSyncService extends IntentService {

    //  public static int syncCounter, getnetCounter, getdetcounter;
    public JSONArray jsonMainArr;
    ArrayList<JSONObject> objects;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Boolean user_cat = true;
    Helper helper = new Helper();

    public ContactsSyncService() {
        super(ContactsSyncService.class.getName());
    }

    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        objects = new ArrayList<>();
        // id = 0;

        //Manoj:1

       objects= DataHolder.getInstance().getList();

        //Manoj:0

       // fetchContacts();

        synccontacts();

        getContentResolver().notifyChange(ChatProvider.BOOK_URI, null, false);
        ////Log.e("cont", helper.getDefaults("Flag", getApplicationContext()));
        helper.setDefaults("Flag", "2", getApplicationContext());
        // GetNetwork();


        stopService(new Intent(getApplicationContext(), ContactsSyncService.class));
    }

    private void synccontacts() {
        final Calendar c = Calendar.getInstance();
        ////Log.e("step ", "3");
        JSONObject table = new JSONObject();
        String status;
        int isShow = 0;
        boolean reg, netstatus;

        try {

            table.put("id", helper.getDefaults("user_id", getApplicationContext()));
            table.put("phone_number", new JSONArray(objects.toString()));
            ////Log.e("table", "" + table);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UserFunctions userFunction = new UserFunctions();
        JSONObject json = null;
        json = userFunction.syncContact(table, getApplicationContext());

        try {
            if (json != null) {
                ////Log.e("AT synccontacts", "json" + json.toString());
                if (json.has("error")) {
                    ////Log.e("AT synccontacts", json.getString("error"));

                    Toast.makeText(getApplicationContext(), json.getString("error"), Toast.LENGTH_LONG).show();
                    //  new AlertDialogManager().showAlertDialog(getApplicationContext(), "Sorry", json.getString("error"));
                } else {
                    jsonMainArr = json.getJSONArray("contact");

                    for (int i = 0; i < jsonMainArr.length(); i++) {
                        JSONObject childJSONObject = jsonMainArr.getJSONObject(i);

                        reg = childJSONObject.getBoolean("registered");
                        if (reg) {
                            netstatus = childJSONObject.getBoolean("network_status");
                            if (netstatus) {
                                status = "2";
                                isShow = 1;

                            } else {
                                status = "1";
                                isShow = 0;
                            }

                            ContentValues cv = new ContentValues();
                            JSONObject phone = childJSONObject.getJSONObject("phone_number");
                            cv.put(NetSchema.USER_PHONE, phone.getString("phone"));
                            cv.put(NetSchema.USER_COMPANY, childJSONObject.getString("company"));
                            cv.put(NetSchema.USER_DISPLAY, helper.ConvertCamel(phone.getString("name")));
                            cv.put(NetSchema.USER_COMPANY_ID, childJSONObject.getString("company_id"));
                            cv.put(NetSchema.USER_ID, childJSONObject.getString("user_id"));
                            cv.put(NetSchema.USER_NET_ID, childJSONObject.getString("user_login") + "@" + AppUtil.SERVER_NAME);
                            if (childJSONObject.has("main_pair")) {
                                cv.put(NetSchema.USER_IMAGE, childJSONObject.getString("main_pair"));
                            } else {
                                cv.put(NetSchema.USER_IMAGE, "");

                            }
                            cv.put(NetSchema.USER_STATUS, isShow);
                            cv.put(NetSchema.USER_NAME, " ");
                            cv.put(NetSchema.USER_CREATED, "" + format.format(c.getTime()));
                            getContentResolver().insert(ChatProvider.NET_URI, cv);
                            getContentResolver().notifyChange(ChatProvider.NET_URI, null, false);


                            ContentValues values = new ContentValues();
                            values.put(ContactsDb.KEY_ID, phone.getString("phone"));
                            values.put(ContactsDb.KEY_NAME, phone.getString("name"));
                            values.put(ContactsDb.KEY_COMPANY, childJSONObject.getString("company"));
                            values.put(ContactsDb.KEY_STATUS, status);
                            if (childJSONObject.has("main_pair")) {
                                values.put(ContactsDb.USER_IMAGE, childJSONObject.getString("main_pair"));
                            } else {
                                values.put(ContactsDb.USER_IMAGE, "");

                            }
                            values.put(ContactsDb.USER_CREATED, "" + format.format(c.getTime()));
                            values.put(ContactsDb.KEY_NET_ID, childJSONObject.getString("user_login") + "@" + AppUtil.SERVER_NAME);
                            values.put(ContactsDb.KEY_COMPANY_ID, childJSONObject.getString("company_id"));
                            values.put(ContactsDb.USER_ID, childJSONObject.getString("user_id"));
                            Uri uri2 = getContentResolver().insert(ChatProvider.BOOK_URI, values);

                        } else {

                            ContentValues values = new ContentValues();
                            JSONObject phone = childJSONObject.getJSONObject("phone_number");
                            values.put(ContactsDb.KEY_ID, phone.getString("phone"));
                            values.put(ContactsDb.KEY_NAME, phone.getString("name"));
                            values.put(ContactsDb.KEY_STATUS, 0);
                            values.put(ContactsDb.USER_CREATED, "" + format.format(c.getTime()));
                            Uri uri = getContentResolver().insert(ChatProvider.BOOK_URI, values);


                        }
                    }
                }

            } else {

                //new AlertDialogManager().showAlertDialog(getApplicationContext(), "Sorry", "Server Error, Try Later");
                Toast.makeText(getApplicationContext(), "Server Error, Try Later", Toast.LENGTH_LONG).show();


            }
        } catch (JSONException e) {
            e.printStackTrace();

        } catch (Exception e) {
            Log.e("synccontcs", e.toString());
        }
    }


    public void fetchContacts() {
        ////Log.e("step ", "1");
        String phoneNumber, phonetype = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?"
                            , new String[]{contact_id}, DISPLAY_NAME);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        String string = phoneNumber.replaceAll("[^\\d]", "");
                        // String where2 = childJSONObject.getString("phone_number");
                        if (string.length() > 10)
                            string = string.substring(string.length() - 10);
                        JSONObject object = new JSONObject();
                        if (ValidationUtil.isValidMobileNumber(string)) {
                            if (string.length() > 2) {
                                try {
                                    object.put("name", helper.ConvertCamel(name));
                                    object.put("phone", Long.parseLong(string.trim()));
                                    objects.add(object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    phoneCursor.close();
                }
            }
            cursor.close();
        }
    }
}