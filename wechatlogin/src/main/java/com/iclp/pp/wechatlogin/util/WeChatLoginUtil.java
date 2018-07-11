package com.iclp.pp.wechatlogin.util;

import com.google.gson.Gson;
import com.iclp.pp.wechatlogin.Model.TokenOpenId;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class WeChatLoginUtil {

    private  String appid = null;

    private  String secret = null;

    private  String code = null;

    private Gson gson = new Gson();

    public WeChatLoginUtil(String code) {
        this.code = code;
        appid = getAppId();
        secret = getSecret();
    }

    public String getUserInfo() {
        String getAccessTokenUrl =  "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid +
                "&secret=" + secret + "&code=" + code  +"&grant_type=authorization_code";
        TokenOpenId tokenOpenId = gson.fromJson(getResponse(getAccessTokenUrl),TokenOpenId.class);

        return userInfo(tokenOpenId.getAccess_token(),tokenOpenId.getOpenid());
    }

    private String userInfo(String accessToken,String openId) {

        String refreshTokenURL = "https://api.weixin.qq.com/sns/userinfo?access_token=" +
                accessToken + "&openid=" + openId;
        return getResponse(refreshTokenURL);

    }

    public  String getAppId() {
        return String.valueOf(getWeChatProperties().get("appid"));
    }

    public  String getSecret() {
        return String.valueOf(getWeChatProperties().get("secret"));
    }

    private  Properties getWeChatProperties() {
        Properties prop = null;
        try {
            InputStream inputStream = new ClassPathResource("com.iclp.pp.wechatlogin.wechat.properties").getInputStream();
            prop = new Properties();
            prop.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    private String getResponse(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse closeableHttpResponse = null;
        String responseStr = null;
        try {
            closeableHttpResponse = httpclient.execute(httpGet);
            responseStr = EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpclient.close();
                closeableHttpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseStr;
    }

    public static void main(String[] args) {
//        System.out.println( getAccessToken(""));
    }
}
