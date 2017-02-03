package gps.tracker.com.gpstracker;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by bhupendramishra on 01/02/17.
 */

public class Server_thread_service extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public Server_thread_service(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
