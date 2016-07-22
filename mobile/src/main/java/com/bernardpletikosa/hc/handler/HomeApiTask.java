package com.bernardpletikosa.hc.handler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bernardpletikosa.hc.R;
import com.bernardpletikosa.hc.storage.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HomeApiTask extends AsyncTask<Integer, Void, Boolean> {

    private static final String SERVER_RESPONSE = "ok";

    private final String SERVER_URL;
    private final String API_AUTH;
    private Context context;

    public HomeApiTask(Context context) {
        SERVER_URL = Storage.instance(context).getServerUrl(context);
        API_AUTH = Storage.instance(context).auth();
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Integer... command) {
        if (API_AUTH == null) return false;

        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(SERVER_URL + "/" + command[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", API_AUTH);

            InputStream in = urlConnection.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(in);

            int data = streamReader.read();
            while (data != -1) {
                response += (char) data;
                data = streamReader.read();
            }
        } catch (IOException e) {
            Log.e("HomeApiTask", "IOException");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("HomeApiTask", "Exception");
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }

        return !response.isEmpty() && response.toLowerCase().equals(SERVER_RESPONSE);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            NotificationHandler.vibrate(context);
        } else {
            int warningResId = Storage.instance(context).auth() == null ? R.string.api_task_uuid_missing : R.string.something_went_wrong;
            Toast.makeText(context, warningResId, Toast.LENGTH_LONG).show();
        }
        NotificationHandler.notify(context, result);

        context = null;
    }
}
