package idatabank.com.custom;

import com.aries.extension.data.EventData;
import com.aries.extension.handler.EventHandler;
import com.aries.extension.util.ConfigUtil;
import com.aries.extension.util.LogUtil;
import idatabank.com.custom.util.NaverSENSClient;
import idatabank.com.custom.util.NaverSENSProperties;

public class NaverSENSAdapter implements EventHandler {
    @Override
    public void on(EventData[] events) {
        NaverSENSProperties naverSENSProperties = new NaverSENSProperties();

        //server_view.conf로 부터 사용자 정의 옵션 사용
        naverSENSProperties.setApiKey(ConfigUtil.getValue("NaverApiKey", null));
        naverSENSProperties.setSecretKey(ConfigUtil.getValue("SecretKey", null));
        naverSENSProperties.setServiceId(ConfigUtil.getValue("ServiceID", null));
        naverSENSProperties.setConTitle(ConfigUtil.getValue("ConTitle", null));
        naverSENSProperties.setFromN(ConfigUtil.getValue("FromNumber", null));
        naverSENSProperties.setToN(ConfigUtil.getValue("ToNumber", null));

        StringBuilder message = new StringBuilder();

        for(EventData data : events) {
            LogUtil.info("Domain ID : " + data.domainId);
            LogUtil.info("Instance Name : " + data.instanceName);
            LogUtil.info("time : " + data.time);
            LogUtil.info("Error Type : " + data.errorType);

            message.append(data.instanceName);
            message.append("\n");
            message.append(data.time);
            message.append("\n");
            message.append(data.errorType);

            LogUtil.info("URL : " + naverSENSProperties.getUrlAPI());

            NaverSENSClient client = new NaverSENSClient(message.toString(), naverSENSProperties);
            String result = client.SMS();

            LogUtil.info("result : " + result.toString());

            if (result == null) { //exception occurred
                LogUtil.info("Error sending the message.");
            } else {
                //Gson gson = new Gson();
                //TelegramResponse response = gson.fromJson(result, TelegramResponse.class);
                //if (!response.isOk()) { //Telegram did not return true:ok
                //    LogUtil.info(String.format("Message was not sent, Status Code From Telegram [%d]. Response Message [%s]", response.getError_code(), response.getDescription()));
                //}
            }
        }
    }
}
