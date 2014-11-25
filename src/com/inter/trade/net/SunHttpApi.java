
package com.inter.trade.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.inter.trade.data.SunType;
import com.inter.trade.log.Logger;
import com.inter.trade.parser.Parser;
import com.inter.trade.util.Constants;


/**
 * @author Joe LaPenna (joe@joelapenna.com)
 */
 public class SunHttpApi implements HttpApi {
    protected static final boolean DEBUG = Constants.DEBUG;

    private static final String DEFAULT_CLIENT_VERSION = "com.joelapenna.foursquare";
    private static final String CLIENT_VERSION_HEADER = "User-Agent";
    private static final int TIMEOUT = 30;

    private final DefaultHttpClient mHttpClient;
    private final String mClientVersion;
    
    public SunHttpApi(DefaultHttpClient httpClient, String clientVersion) {
        mHttpClient = httpClient;
        if (clientVersion != null) {
            mClientVersion = clientVersion;
        } else {
            mClientVersion = DEFAULT_CLIENT_VERSION;
        }
    }

    public SunType executeHttpRequest(HttpRequestBase httpRequest,
            Parser<? extends SunType> parser) throws  IOException ,SunException{
        if(DEBUG) Logger.d("SunHttpApi","doHttpRequest: " + httpRequest.getURI());
        HttpResponse response = executeHttpRequest(httpRequest);
        if(DEBUG) Logger.d("SunHttpApi","executed HttpRequest for: "
                + httpRequest.getURI().toString());
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 200:
                String content = EntityUtils.toString(response.getEntity());
//                return JSONUtils.consume(parser, content);
                
            case 400:
                if(DEBUG) Logger.d("SunHttpApi","HTTP Code: 400");
                throw new SunException(
                        response.getStatusLine().toString(),
                        EntityUtils.toString(response.getEntity()));

            case 401:
                response.getEntity().consumeContent();
                if(DEBUG) Logger.d("SunHttpApi","HTTP Code: 401");
                throw new SunException(response.getStatusLine().toString());

            case 404:
                response.getEntity().consumeContent();
                if(DEBUG) Logger.d("SunHttpApi","HTTP Code: 404");
                throw new SunException(response.getStatusLine().toString());

            case 500:
                response.getEntity().consumeContent();
                if(DEBUG) Logger.d("SunHttpApi","HTTP Code: 500");
                throw new SunException("Foursquare is down. Try again later.");

            default:
                if(DEBUG) Logger.d("SunHttpApi", "Default case for status code reached: "
                        + response.getStatusLine().toString());
                response.getEntity().consumeContent();
                throw new SunException("Error connecting to Foursquare: " + statusCode + ". Try again later.");
        }
    }

    public String doHttpPost(String url, NameValuePair... nameValuePairs)
            throws 
            IOException,SunException {
        if(DEBUG) Logger.d("SunHttpApi","doHttpPost: " + url);
        HttpPost httpPost = createHttpPost(url, nameValuePairs);

        HttpResponse response = executeHttpRequest(httpPost);
        if(DEBUG) Logger.d("SunHttpApi", "executed HttpRequest for: " + httpPost.getURI().toString());
        switch (response.getStatusLine().getStatusCode()) {
            case 200:
                try {
                    return EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    throw new SunException(e.getMessage());
                }

            case 401:
                response.getEntity().consumeContent();
                throw new SunException(response.getStatusLine().toString());

            case 404:
                response.getEntity().consumeContent();
                throw new SunException(response.getStatusLine().toString());

            default:
                response.getEntity().consumeContent();
                throw new SunException(response.getStatusLine().toString());
        }
    }

    /**
     * execute() an httpRequest catching exceptions and returning null instead.
     *
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpResponse executeHttpRequest(HttpRequestBase httpRequest) throws IOException {
        if(DEBUG) Logger.d("SunHttpApi","executing HttpRequest for: "
                + httpRequest.getURI().toString());
        try {
            mHttpClient.getConnectionManager().closeExpiredConnections();
            return mHttpClient.execute(httpRequest);
        } catch (IOException e) {
            httpRequest.abort();
            throw e;
        }
    }

    public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        if(DEBUG) Logger.d("SunHttpApi", "creating HttpGet for: " + url);
        String query = URLEncodedUtils.format(stripNulls(nameValuePairs), HTTP.UTF_8);
        HttpGet httpGet = new HttpGet(url + "?" + query);
        httpGet.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        if(DEBUG) Logger.d("SunHttpApi", "Created: " + httpGet.getURI());
        return httpGet;
    }
    public HttpGet createHttpGet(String url){
    	 if(DEBUG) Logger.d("SunHttpApi", "Created: " + "creating HttpGet for: " + url);
    	HttpGet httpGet = new HttpGet(url);
    	return httpGet;
    }
    public HttpPost createHttpPost(String url){
    	 if(DEBUG) Logger.d("SunHttpApi", "Created: " + "creating HttpPost for: " + url);
        HttpPost httpPost = new HttpPost(url);
        return httpPost;
    }
    public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {
        if(DEBUG) Logger.d("SunHttpApi","creating HttpPost for: " + url);
        if(nameValuePairs == null){
        	return createHttpPost(url);
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(CLIENT_VERSION_HEADER, mClientVersion);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(stripNulls(nameValuePairs), HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException("Unable to encode http parameters.");
        }
        if(DEBUG) Logger.d("SunHttpApi","Created: " + httpPost);
        return httpPost;
    }
    
    public HttpURLConnection createHttpURLConnectionPost(URL url, String boundary) 
        throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
        conn.setDoInput(true);        
        conn.setDoOutput(true); 
        conn.setUseCaches(false); 
        conn.setConnectTimeout(TIMEOUT * 1000);
        conn.setRequestMethod("POST");

        conn.setRequestProperty(CLIENT_VERSION_HEADER, mClientVersion);
        conn.setRequestProperty("Connection", "Keep-Alive"); 
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        
        return conn;
    }

    private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (int i = 0; i < nameValuePairs.length; i++) {
            NameValuePair param = nameValuePairs[i];
            if (param.getValue() != null) {
                if(DEBUG) Logger.d("SunHttpApi","Param: " + param);
                params.add(param);
            }
        }
        return params;
    }

    /**
     * Create a thread-safe client. This client does not do redirecting, to allow us to capture
     * correct "error" codes.
     *
     * @return HttpClient
     */
    public static final DefaultHttpClient createHttpClient() {
        // Sets up the http part of the service.
        final SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" protocol scheme, it is required
        // by the default operator to look up socket factories.
        final SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        
        // Set some client http client parameter defaults.
        final HttpParams httpParams = createHttpParams();
        HttpClientParams.setRedirecting(httpParams, false);

        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpParams,
                supportedSchemes);
        return new DefaultHttpClient(ccm, httpParams);
    }

    /**
     * Create the default HTTP protocol parameters.
     */
    private static final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();

        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        return params;
    }
    
    public static InputStream requestHttp(String url1) {
    	try {
			URL url = new URL(url1);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			return is;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }

    
}
