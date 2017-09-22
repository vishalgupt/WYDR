package wydr.sellers.acc;

import android.app.backup.FileBackupHelper;
import android.content.Context;

/**
 * Created by surya on 29/12/15.
 */
public class DbBackupHelper extends FileBackupHelper {
    public DbBackupHelper(Context ctx, String dbName) {
        super(ctx, ctx.getDatabasePath(dbName).getAbsolutePath());
    }

}
