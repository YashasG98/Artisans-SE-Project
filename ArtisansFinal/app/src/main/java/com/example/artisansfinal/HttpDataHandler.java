package com.example.artisansfinal;
//completely done by Shrinidhi Anil Varna
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class HttpDataHandler
{
    public HttpDataHandler(){

    }
    public String getHTTPData(String requestURL) throws IOException {
        URL url;
        String response = "";
        try{
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(150);
            conn.setConnectTimeout(150);
            conn.setDoInput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            int responseCode = conn.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader((System.in)));
                while((line = br.readLine())!= NULL)
                    response += (line);
            }
            else
            {
                response = "";
            }
        }catch(ProtocolException e)
        {
            e.printStackTrace();
        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return response;
    }
}
