/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wydr.sellers.syncadap;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.View;

import wydr.sellers.slider.MyContentProvider;

/**
 * List fragment containing a list of Atom entry objects (articles) stored in the local database.
 * <p/>
 * <p/>
 * <p/>
 * <p>Selecting an item from the displayed list displays the article in the default browser.
 * <p/>
 * <p>If the content provider doesn't return any data, then the first sync hasn't run yet. This sync
 * adapter assumes data exists in the provider once a sync has run. If your app doesn't work like
 * this, you should add a flag that notes if a sync has run, so you can differentiate between "no
 * available data" and "no initial sync", and display this in the UI.
 * <p/>
 * <p>The ActionBar displays a "Refresh" button. When the user clicks "Refresh", the sync adapter
 * runs immediately. An indeterminate ProgressBar element is displayed, showing that the sync is
 * occurring.
 */
public class EntryListFragment extends ListFragment {

    private static final String TAG = "EntryListFragment";
    private static final String[] PROJECTION = new String[]{
            FeedCatalog._ID,
            FeedCatalog.COLUMN_PRODUCT_ID,
            FeedCatalog.COLUMN_TITLE,
            FeedCatalog.COLUMN_CODE,
            FeedCatalog.COLUMN_IMAGEPATH,
            FeedCatalog.COLUMN_GRANDPARENTCAT,
            FeedCatalog.COLUMN_PARENTCAT,
            FeedCatalog.COLUMN_CHILDCAT,

            FeedCatalog.COLUMN_MRP,
            FeedCatalog.COLUMN_SP,
            FeedCatalog.COLUMN_QTY,
            FeedCatalog.COLUMN_MINQTY,
            FeedCatalog.COLUMN_DESC,
            FeedCatalog.COLUMN_VISIBILTY,
            FeedCatalog.COLUMN_GRANDCHILDCAT,
    };
    // Constants representing column positions from PROJECTION.
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_PRODUCT_ID = 1;
    private static final int COLUMN_TITLE = 2;
    private static final int COLUMN_CODE = 3;
    private static final int COLUMN_IMAGEPATH = 4;
    private static final int COLUMN_GRANDPARENTCAT = 5;
    private static final int COLUMN_PARENTCAT = 6;
    private static final int COLUMN_CHILDCAT = 7;
    private static final int COLUMN_MRP = 8;
    private static final int COLUMN_SP = 9;
    private static final int COLUMN_QTY = 10;
    private static final int COLUMN_MINQTY = 11;
    private static final int COLUMN_DESC = 12;
    private static final int COLUMN_VISIBILTY = 13;
    private static final int COLUMN_SERVER_FLAG = 14;
    private static final int COLUMN_STATUS = 15;
    /**
     * Cursor adapter for controlling ListView results.
     */
    private SimpleCursorAdapter mAdapter;
    private Object mSyncObserverHandle;
    private Menu mOptionsMenu;
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {
            getActivity().runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    // Create a handle to the account that was created by
                    // SyncService.CreateSyncAccount(). This will be used to query the system to
                    // see how the sync status has changed.
                    Account account = GenericAccountService.GetAccount(SyncUtils.ACCOUNT_TYPE);
                    if (account == null) {
                        // GetAccount() returned an invalid value. This shouldn't happen, but
                        // we'll set the status to "not refreshing".
                        // setRefreshActionButtonState(false);
                        return;
                    }

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, MyContentProvider.AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, MyContentProvider.AUTHORITY);
                    //setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EntryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        SyncUtils.CreateSyncAccount(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

}