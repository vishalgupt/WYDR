package wydr.sellers.registration;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by Deepesh_pc on 17-08-2015.
 */

public class ContactsObserver extends ContentObserver {
    static int co = 0;
    Context c;

    public ContactsObserver(Handler handler, Context con) {
        super(handler);
        this.c = con;
    }

    public void observe() {
        ContentResolver resolver = c.getContentResolver();
        resolver.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, false, this); // this innerclass handles onChange

    }

    @Override
    public void onChange(boolean selfChange) {

        super.onChange(selfChange);

        //c.startService(new Intent(c, ContactsSyncService.class));
        if (co == 0) {
            Log.e("once", "syncing contacts");
            c.startService(new Intent(c, ContactsSyncService.class));
        } else
            Log.e("twice", "syncing contacts");

        co++;

    }
}
