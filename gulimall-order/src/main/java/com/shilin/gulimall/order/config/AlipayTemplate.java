package com.shilin.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.shilin.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000116665016";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCDymKY0WzmvfO3BAMvxw0uzdlV7J1SxCBJ07AHYiY16EIZWnbhUXB+uOOZuE+rDyxdxE5rZR8d4y261QslRzS1b30kY40j5uWv5eWHQ2oqaT+vrfiENT0it9FkFznIEk3QJ5Dt+EBCv3ne1KQg7/cgEKENlfWiyWATL+wI7TLekSgOEqSlIW3G8/socXZU7BpWCiA7wubZuDHLUdW4uOy0VG8+1hIT4dDVlPrI6hbEE1oA+JXMHNLyl413Ou6wTNUHlnEzFPKZEFXcRbGQ8H3Sq8xVr4aE8kDRsXdqfUj6TqS/nPYJox1HSrnGxJhj8DWkfucnRNJ0Jp82g83eiH9JAgMBAAECggEABi5+BXD9TK1oOxNjGuCZS7K4GQGqN3Gfmhbl0NLXu/uS7iGCJftt7WO6uUEd64YgrJ1CmGL73KkFQhgwF6WJYWFDW2uX+TEhYOjFpVySJD8fj5v3ZiwhG/nHRoyYVzwUjkjs0VytlvPoQX1z3V+kFcrx4vM/vEz5xqyew6RsmGfhIbKE1LRyuZNH1o2RufGR+u/hmHPQixUwGUKDJgGapC0ZA1r2XqBS66lBd9U3zUi4rs+F7G9rdrguYlxmrMC8x+S6tZ7Vag6TlrMkDnkqbxLNorKhML7BDf3rokc0OJpKxNceu9gF0tUMgXysMImZXnNNsLjts2iHX1H7HDKhAQKBgQDZOacgaIQCXWAJJLPthMMHm3IT2v2mwp7aWND3DaBVEhIQnKkqv0aUZZHvmOQ0iX9Zt8m4Ks3OZPaF0NQQpJgtVwdF218tdtWvpQLUBbTBfBmUyREXsumC1wVKSFb7G/C3+JtCak7s4Di9vNDLk++nGwzX18l2jPmPQpkOOWf0sQKBgQCbULOXn0cF+fvCjzLvSXkxqp2qv0IWCvZuumfaIMpBiqE1dLUBLmgj9Gk3ZsN1F2QI/GHwHx1kUAg0PhIrZjIcukKrOu7625vy5JZqJrJqicSgBdnnS0sMiFUY9puvp4bItNxtlnAy1G0lokFGzIWlBKSD5ipgXz7RrC8rD2m6GQKBgE2EaGKFSY56ednpchfxh5OAYk45mMjYrM1oYnG3OB8DitTIwwmRJoOwV0q0OcORyZNfpamGBNmBaAPKyNKDmhab9ulz5lwYCxtZ/E9jFeTx/0L8GZBmdTQOvUGL3RZRGy7blbQ2saGQRk9XQP9U9HGikou34pnepvP7pRjRXYqRAoGAbufrtmOIRd5CRqYWywP+RNb+i0TTngXp/xuguQQdKTwi7ZcPAW7v8my/DU6WV/CCAGCoVt0BEfaMRTHlq5PoSNdAcCE/MWbML1/HgLltQeJqw5IqDZ2YU7Suf4rplT7dYkiz9pUsPT+vSOZaEoJNFI8CIyWiMPsSUorPi8IPz1ECgYEAkUcEJRsM8NkO8zvegHS/x7XwkL05w4MFRATAbUJXympKn+OFBod4X5N6cw15Oi36JIyn/YzeyPp7sbcRMKlAPz9POK3x3lZK8DmiPE7jlZuiBptbW9EjNB/gbV3nRx0s7VzikCKi2hQ21xB21W1fDQvQ3eUFjUAGKDJ89GJJ278=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkdDAg048E/mzpC5QuuprgAxeXwy5FIcrDpuwxGV3s4kh+wUBO3A9g3PYSfKrQqbv5Az5aVYN4+V1rtVkYuBeuD6c5z6DU+t0XAJs/GuKhrKs58ZAlA7h3c4cVF1auu0sfx2vBHGglwFMwNAyx6AZlpEVYiyF504V/qGGoXBZ6E3biocgAL0cFm+/fZO23W/H/hiUGOGegAkpUBmJeskoxSQHfTFRm2gR/bYkF5TEC3dlgBApYz8AiqpfNVAXbQnocExGafBMlmUtp90vc9i5nkH3976Uhl3UpSPVuQUHIyUYiOB6QUAKQ6ur/8z4MI+bXQ6f0c4R1whUkIf/ywcUwwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
//    private  String notify_url="http://cx4eq26zrq.52http.tech/alipay.trade.wap.pay-JAVA-UTF-8/notify_url.jsp";
    private  String notify_url="http://order.gulimall.com/payOk.html";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url="http://member.gulimall.com/orderList.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private String timeout = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\"" + timeout + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
