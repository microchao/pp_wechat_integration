package iclp.pp.ppsearch.util;

import com.google.gson.Gson;
import iclp.pp.ppsearch.model.GetTokenModel;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

public class WeChatUtil {
    private CloseableHttpClient httpClient;

    private String tokenUrl = "https://api-staging.collinsongroup.com/oauth/v1/token";

    private String lounghInfoUrl = "https://api-staging.collinsongroup.com/travel-experiences/v1/lounges?languagecode=zh-cn&searchterm=";

    private String name;

    /**
     * 查询loungh之前的接口
     *
     * @return GetTokenModel 封装成对象
     */
    public GetTokenModel getToken() {
        httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(tokenUrl);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Postman-Token", "faa218c1-0c5c-49d1-b432-aa21e2e20e07");
        httpPost.setHeader("authorization", "Basic QmhqREE1TXZ3SXE0RzFMOTZobjFHbWdqVU85SHVhdVU6YzRYRUNUb1FjT3l3THFlNA==");
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        String responseStr = null;
        CloseableHttpResponse closeableHttpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
            closeableHttpResponse = httpClient.execute(httpPost);
            responseStr = EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeableHttpResponse.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Gson gson = new Gson();
        GetTokenModel getTokenModel = (GetTokenModel) gson.fromJson(responseStr, GetTokenModel.class);
        return getTokenModel;
    }

    public String getLounghInfo(String name) {
        GetTokenModel getTokenModel = getToken();
        HttpGet httpGet = new HttpGet(lounghInfoUrl + name);
        httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpGet.setHeader("Authorization", "Bearer " + getTokenModel.getAccess_token());
        httpGet.setHeader("Postman-Token", "a947cd89-37f2-4fe4-92fb-7eb939eed201");
        CloseableHttpResponse closeableHttpResponse = null;
        String responseStr = null;
        try {
            closeableHttpResponse = httpClient.execute(httpGet);
            responseStr = EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
                closeableHttpResponse.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseStr;
    }
}
