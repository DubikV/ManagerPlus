package com.gmail.vanyadubik.managerplus.service.navigationtrack;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.gps.DirectionsJSONParser;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG;

public class NavigationTrack {

    private Context context;
    private ParamNavigationTrack paramNavigationTrack;
    private NavigationUpdateListener navigationUpdateListener;

    public NavigationTrack(Context context,
                           NavigationUpdateListener navigationUpdateListener) {
        this.context = context;
        this.navigationUpdateListener = navigationUpdateListener;
    }

    public void startReceivingTrack(ParamNavigationTrack paramNavigationTrack) {
        this.paramNavigationTrack = paramNavigationTrack;
        String url = getDirectionsUrl(paramNavigationTrack.getLatLngStart()
                , paramNavigationTrack.getLatLngEnd());
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(new ParamDownloadTask(paramNavigationTrack.getIdTrack(),url));
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb  = new StringBuffer();
            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }
            data = sb.toString();

            br.close();
        }catch(Exception e){
            Log.d(TAGLOG, e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<ParamDownloadTask, Void, ParamDownloadTask> {
        @Override
        protected ParamDownloadTask doInBackground(ParamDownloadTask... param) {
            ParamDownloadTask paramData = param[0];
            try{
                String data = downloadUrl(paramData.getData());
                paramData.setData(data);
            }catch(Exception e){
                Log.d(TAGLOG,e.toString());
            }
            return paramData;
        }
        @Override
        protected void onPostExecute(ParamDownloadTask result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<ParamDownloadTask, Integer, ParamParserTask >{

        @Override
        protected ParamParserTask doInBackground(ParamDownloadTask... data) {

            ParamDownloadTask jsonData = data[0];
            ParamParserTask routesData = new ParamParserTask(jsonData.getId(), null);
            try{
                JSONObject jObject = new JSONObject(jsonData.getData());
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routesData.setData(parser.parse(jObject));
            }catch(Exception e){
                e.printStackTrace();
            }
            return routesData;
        }

        @Override
        protected void onPostExecute(ParamParserTask data) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            List<List<HashMap<String,String>>> result = data.getData();

            if(result == null){
                return;
            }
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions()
                        .width(paramNavigationTrack.getWidthPolyline())
                        .color(context.getResources().getColor(R.color.colorBlue))
                        .geodesic(true);

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
            }

            if(lineOptions != null) {
                navigationUpdateListener.updateNavogationTrack(lineOptions, data.getId());
            }

        }
    }


    protected class ParamDownloadTask{
        private int id;
        private String data;

        public ParamDownloadTask(int id, String data) {
            this.id = id;
            this.data = data;
        }

        public int getId() {
            return id;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    private class ParamParserTask{
        private int id;
        private List<List<HashMap<String,String>>> data;

        public ParamParserTask(int id, List<List<HashMap<String, String>>> data) {
            this.id = id;
            this.data = data;
        }

        public int getId() {
            return id;
        }

        public List<List<HashMap<String, String>>> getData() {
            return data;
        }

        public void setData(List<List<HashMap<String, String>>> data) {
            this.data = data;
        }
    }


}
