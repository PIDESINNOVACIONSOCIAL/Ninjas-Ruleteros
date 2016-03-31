package com.me.hackeourbano.ui.utils;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Condesa on 08/03/16.
 */
public class ParseRoutesAsyncTask extends AsyncTask<Void, Void, Void> {

    private ProcessPointsCallback callback;

    public interface ProcessPointsCallback{
        //void processFinish(List<Object> points);
    }

    public ParseRoutesAsyncTask(ProcessPointsCallback callback){
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Gson gson = new Gson();
        // Call JsonUtils for retrieving data from assets json file
        //BookResponse bookResponse = gson.fromJson(jsonResponse.toString(), BookResponse.class);
        //return bookResponse.getResults();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(callback != null){
            //callback.processFinish(books);
        }
    }
}
