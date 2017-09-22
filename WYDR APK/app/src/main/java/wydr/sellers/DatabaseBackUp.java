package wydr.sellers;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Created by surya on 8/1/16.
 */
public class DatabaseBackUp extends BackupAgentHelper {
    static final String File_DB_BACK_PREFRENCES = "wydrPreferences";
    static final String PREFS_BACKUP_KEY = "backup";

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this,
                File_DB_BACK_PREFRENCES);
        addHelper(PREFS_BACKUP_KEY, helper);
    }
    public void requestBackup() {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }
}
