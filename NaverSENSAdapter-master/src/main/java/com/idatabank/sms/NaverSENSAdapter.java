package com.idatabank.sms;

import com.aries.extension.data.EventData;
import com.aries.extension.handler.EventHandler;
import com.aries.extension.util.LogUtil;
import com.idatabank.sms.util.ConfUtil;
import com.idatabank.sms.util.NaverSENSClient;
import com.idatabank.sms.util.NaverSENSProperties;

public class NaverSENSAdapter implements EventHandler {
    @Override
    public void on(EventData[] events) {
        LogUtil.info("NaverSENSAdapter-1.0.1...");

        for(EventData data : events) {
            LogUtil.info("---------------EventData---------------");
            LogUtil.info("Domain ID : " + data.domainId);
            LogUtil.info("domainName : " + data.domainName);
            LogUtil.info("instanceId : " + data.instanceId);
            LogUtil.info("instanceName : " + data.instanceName);
            LogUtil.info("time : " + data.time);
            LogUtil.info("errorType : " + data.errorType);
            LogUtil.info("metricsName : " + data.metricsName);
            LogUtil.info("eventLevel : " + data.eventLevel);
            LogUtil.info("message : " + data.message);
            LogUtil.info("value : " + data.value);
            LogUtil.info("otype : " + data.otype);
            LogUtil.info("detailMessage : " + data.detailMessage);
            LogUtil.info("serviceName : " + data.serviceName);
            LogUtil.info("txid : " + data.txid);
            LogUtil.info("------------------End------------------");

            StringBuilder message = new StringBuilder();

            message.append(data.domainName);
            message.append(".");
            message.append(data.instanceName);
            message.append("\n");
            message.append(data.errorType);

            for (int idx = 1; idx <= Integer.parseInt(ConfUtil.getValue("MaxAddressee")); idx++) {
                NaverSENSProperties naverSENSProperties = ConfUtil.getNaverSENSProperties(idx);

                NaverSENSClient client = new NaverSENSClient(message.toString(), naverSENSProperties);
                String result = client.SMS();

                if (result == null) {
                    LogUtil.info("Error sending the message.");
                } else {
                    LogUtil.info("result : " + result.toString());
                }
            }
        }
    }
}
