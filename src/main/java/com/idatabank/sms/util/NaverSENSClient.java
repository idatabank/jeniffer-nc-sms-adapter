package com.idatabank.sms.util;

import com.aries.extension.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class NaverSENSClient {
    private final int CONNECTION_TIME_OUT	= 5 * 1000;
    private final String ENCODING			= "UTF-8";
    private final int SUCESS_CODE           = 202;

    private NaverSENSProperties naverSENSProperties;
    private String message;
    private String currentTimeMillis;

    public NaverSENSClient(String message, NaverSENSProperties properties) {
        this.message = message;
        this.naverSENSProperties = properties;
        this.currentTimeMillis = Long.toString(System.currentTimeMillis());
        LogUtil.info("Create NaverSENSClient Object..");
    }

    public String SMS() {
        HttpURLConnection connection = null;

        try{
            byte[] postDataBytes = getSMSQuery().getBytes(ENCODING);

            URL url = new URL(naverSENSProperties.getUrl());

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", ENCODING);

            connection.setRequestProperty("x-ncp-apigw-timestamp", currentTimeMillis);
            connection.setRequestProperty("x-ncp-iam-access-key", naverSENSProperties.getApiKey());
            connection.setRequestProperty("x-ncp-apigw-signature-v2", makeSignature());

            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            connection.setConnectTimeout(CONNECTION_TIME_OUT);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(postDataBytes);
            out.flush();
            out.close();

            InputStream in = null;

            if (connection.getResponseCode() == SUCESS_CODE) {
                in = connection.getInputStream();
            } else {
                in = connection.getErrorStream();
            }

            //InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder response = new StringBuilder();
            while ( (line = reader.readLine()) != null )
                response.append(line + "\n");

            reader.close();

            LogUtil.info("Sending NaverSENSClient Message..");
            return response.toString();
        }catch (Exception ex) {
            LogUtil.error("Error while sending the message. Reason : " + ex);
            return null;
        }finally {
            if (connection != null) connection.disconnect();
        }
    }

    public String makeSignature() {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = naverSENSProperties.getUrlAPI();
        String timestamp = currentTimeMillis;
        String accessKey = naverSENSProperties.getApiKey();
        String secretKey = naverSENSProperties.getSecretKey();

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = null;
        try {
            signingKey = new SecretKeySpec(secretKey.getBytes(ENCODING), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes(ENCODING));
            String encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);

            LogUtil.info("Create Signature..");
            return encodeBase64String;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }


    private String getSMSQuery() {
        Gson gson = new Gson();

        JsonObject SMS = new JsonObject();
        JsonObject msg = new JsonObject();
        JsonArray messages = new JsonArray();

        /*
        {
            "type":"SMS",
            "contentType":"COMM",
            "countryCode":"82",
            "from":"01012345678",
            "content":"내용",
            "messages":[
                {
                    "to":"01012345678",
                    "content":"위의 content와 별도로 해당 번호로만 보내는 내용(optional)"
                }
            ]
        }
         */

        SMS.addProperty("type", naverSENSProperties.getTYPE());
        SMS.addProperty("contentType", naverSENSProperties.getCON_TYPE());
        SMS.addProperty("countryCode", naverSENSProperties.getCountryCode());
        SMS.addProperty("from", naverSENSProperties.getFromN());
        SMS.addProperty("content", naverSENSProperties.getConTitle());

        msg.addProperty("to", naverSENSProperties.getToN());
        msg.addProperty("content", message);
        messages.add(msg);

        SMS.add("messages", messages);

        LogUtil.info("Create SMS Query.. : \n" + gson.toJson(SMS));
        return gson.toJson(SMS);
    }
}
