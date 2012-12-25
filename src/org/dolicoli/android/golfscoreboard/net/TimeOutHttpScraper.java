package org.dolicoli.android.golfscoreboard.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

class TimeOutHttpScraper {

	private static final String TAG = "TimeOutHttpScraper";

	private static final int RESPONSE_SUCCESS = 200;
	private static final int TIME_OUT_SECOND = 5000;

	static String scrap(String url, String encoding)
			throws PageLoadFailException {

		HttpGet httpget = new HttpGet(url);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = TIME_OUT_SECOND;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = TIME_OUT_SECOND;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		HttpClient httpclient = new DefaultHttpClient(httpParameters);

		HttpResponse response;
		InputStream instream = null;
		try {
			response = httpclient.execute(httpget);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode != RESPONSE_SUCCESS) {
				throw new PageLoadFailException("Reponse Code: " + statusCode);
			}
			Log.d(TAG, response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				instream = entity.getContent();
				return convertStreamToString(instream, encoding);
			}

		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			try {
				if (instream != null)
					instream.close();
			} catch (IOException e) {
			}

			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}
		return null;
	}

	private static String convertStreamToString(InputStream is, String encoding)
			throws UnsupportedEncodingException {
		BufferedReader reader = null;
		if (encoding == null) {
			reader = new BufferedReader(new InputStreamReader(is));
		} else {
			reader = new BufferedReader(new InputStreamReader(is, encoding));
		}
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
