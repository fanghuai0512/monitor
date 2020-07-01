package com.xinwei.monitor.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.xinwei.monitor.constant.Constant;
import com.xinwei.monitor.pojo.WalmartError;
import com.xinwei.monitor.pojo.WalmartResponseError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpClientUtils {

    /**
     * 沃尔玛公共请求方法
     * @param url 请求url
     * @param requestType 请求类型
     * @param contentType 上下文类型
     * @param accessToken 请求token
     * @param clientId 店铺客户端id
     * @param clientSecret 店铺客户端凭证
     * @param paramMap 请求参数
     * @return
     */
    public static String walmartRequest(String url,
                                        String requestType,
                                        String contentType ,
                                        String  accessToken,
                                        String clientId,
                                        String clientSecret,
                                        Map<String,String> paramMap,
                                        String json) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        RequestBuilder requestBuilder = RequestBuilder.create(requestType).setUri(url);
        try {
            if(paramMap != null){
                List<NameValuePair> paramList = Lists.newArrayList();
                for (String key : paramMap.keySet()) {
                    paramList.add(new BasicNameValuePair(key, paramMap.get(key)));
                }
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramList);
                requestBuilder.setEntity(formEntity);
            }
            if(StringUtils.isNotEmpty(json)){
                requestBuilder.setEntity(new StringEntity(json));
            }
            HttpUriRequest request = requestBuilder.build();
            //专门设置沃尔玛接口的头
            String authorization = new String(Base64.encodeBase64((clientId + ":" + clientSecret).getBytes()));
            request.setHeader("WM_SVC.NAME", "Walmart Marketplace");
            request.setHeader("WM_QOS.CORRELATION_ID", DateUtils.randomDate());
            request.setHeader("Authorization", "Basic " + authorization);
            request.setHeader("WM_SEC.ACCESS_TOKEN", accessToken);
            request.setHeader("Content-Type", contentType);
            request.setHeader("Accept", "application/json");
            request.setHeader("Host", "marketplace.walmartapis.com");
            HttpResponse httpResponse = httpClient.execute(request);
            int responseStatus = httpResponse.getStatusLine().getStatusCode();
            if( responseStatus == 200){
                return EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            }else{
                String responseErrorJson = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                WalmartResponseError walmartResponseError = JSONObject.parseObject(responseErrorJson, WalmartResponseError.class);
                List<WalmartError> error = walmartResponseError.getError();
                error.stream().forEach(walmartError -> {
                    log.error("接口调取失败：状态码【{}】-错误类型【{}】-错误级别【{}】- 错误信息【{}】 -错误详细描述【{}】 "
                            ,walmartError.getCode()
                            ,walmartError.getCategory()
                            ,walmartError.getSeverity()
                            ,walmartError.getInfo()
                            ,walmartError.getDescription());
                });
            }
            log.error("请求沃尔玛接口失败 :" + EntityUtils.toString(httpResponse.getEntity(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("HttpClient error : "+e.getMessage());
        } catch (ClientProtocolException e) {
            log.error("HttpClient error : "+e.getMessage());
        } catch (IOException e) {
            log.error("HttpClient error : "+e.getMessage());
        }
        return null;
    }

}

