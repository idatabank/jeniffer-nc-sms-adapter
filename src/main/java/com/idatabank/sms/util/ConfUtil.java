package com.idatabank.sms.util;

import com.aries.extension.util.PropertyUtil;

public class ConfUtil {
    private static final String ADAPTER_ID = "naversms";

    public static String getValue(String key) {
        return PropertyUtil.getValue(ADAPTER_ID, key);
    }

    public static NaverSENSProperties getNaverSENSProperties(int idx) {
        NaverSENSProperties properties = new NaverSENSProperties();

        properties.setApiKey(getValue("Addressee_" + idx + "_ApiKey"));
        properties.setSecretKey(getValue("Addressee_" + idx + "_SecretKey"));
        properties.setServiceId(getValue("Addressee_" + idx + "_ServiceID"));
        properties.setFromN(getValue("Addressee_" + idx + "_FromNum"));
        properties.setToN(getValue("Addressee_" + idx + "_ToNum"));

        return properties;
    }
}
