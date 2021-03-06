import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;
 
 
public class MyApi {
 
 private String BaseUrl;
 private String ResponderUrl;
 private String responderParameters;
 
 /*
  *Constructor sets BaseUrl, ResponderUrl and ResponderParameters properties
  */
 public MyApi() {
         this.BaseUrl = "http://localhost/clientinfo/";
         this.ResponderUrl = "~api/search/client?action=GET";
         this.responderParameters = "fields=RowNumber%2CId%2CClientSSN%2CClientName%2CPlanNumber%2CPlanType%2CPlanBalance%2CClientContactNumber%2CClientEmail&sortOrder=%2bClientSSN";
 }
 
 public String Login() throws MalformedURLException, IOException {
         /*
          * Open an HTTP Connection to the Logon.ashx page
          */
         HttpURLConnection httpcon = (HttpURLConnection) ((new URL(BaseUrl+"Logon.ashx").openConnection()));
         httpcon.setDoOutput(true);
         httpcon.setRequestProperty("Content-Type", "application/json");
         httpcon.setRequestProperty("Accept", "application/json");
         httpcon.setRequestMethod("POST");
         httpcon.connect();
         /*
          * Output user credentials over HTTP Output Stream
          */
         byte[] outputBytes = "{'username': 'rufus', 'password':'YUNOUSEAPIKEY'}".getBytes("UTF-8");
         OutputStream os = httpcon.getOutputStream();
         os.write(outputBytes);
         os.close();
         /*
          * Call Function setCookie and pass the HttpUrlConnection. Set Function
          * will return a Cookie String used to authenticate user.
          */
         return setCookie(httpcon);
 }
 
 public String setCookie(HttpURLConnection httpcon) {
         
         /*
          * Process the HTTP Response Cookies from successful credentials
          */
         String headerName;
         ArrayList<String> cookies = new ArrayList<String>();
         
         for (int i=1; (headerName = httpcon.getHeaderFieldKey(i))!=null; i++) {
                 
                 if (headerName.equals("Set-Cookie")&& httpcon.getHeaderField(i) != "null") {
                         cookies.add(httpcon.getHeaderField(i));
                 }
         }
         httpcon.disconnect();
         /*
          * Filter cookies, create Session_ID Cookie
          */
         String cookieName = cookies.get(0);
         String cookie2 = cookies.get(1);
         String cookie1 = cookieName.substring(cookieName.indexOf("="), cookieName.indexOf(";")+2);
         cookie2 = cookie2.substring(0, cookie2.indexOf(";"));
         cookieName = cookieName.substring(0 , cookieName.indexOf("="));
         String cookie = cookieName+cookie1+cookie2;
         return cookie;
 }
 
 public void ApiResponder(String cookie) throws MalformedURLException, IOException {
         /*
          * Create a new HTTP Connection request to responder, pass along Session_ID Cookie
          */
         HttpURLConnection httpcon = (HttpURLConnection) ((new URL(this.BaseUrl+this.ResponderUrl).openConnection()));
         httpcon.setDoOutput(true);
         httpcon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         httpcon.setRequestProperty("Accept", "application/json");
         httpcon.setRequestProperty("Cookie", cookie);
         httpcon.setRequestMethod("POST");
         httpcon.connect();
         
         byte[] outputBytes = responderParameters.getBytes("UTF-8");
         OutputStream os = httpcon.getOutputStream();
         os.write(outputBytes);
         os.close();
         
         /*
          * Read/Output response from server
          */
         
         BufferedReader inreader = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
         String decodedString;
         while ((decodedString = inreader.readLine()) != null) {
                 System.out.println(decodedString);
         }
         
         inreader.close();
         httpcon.disconnect();
 }
 
 public static void main(String[] args) throws Exception {
         MyApi api = new MyApi();
         System.out.println(api.Login());
         api.ApiResponder(api.Login());
 }
}
