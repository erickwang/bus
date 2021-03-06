/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.notify.provider.aliyun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.notify.AbstractProvider;
import org.aoju.bus.notify.magic.Response;
import org.aoju.bus.notify.metric.Properties;
import org.aoju.bus.notify.metric.Template;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 阿里云抽象类提供者
 *
 * @author Justubborn
 * @version 5.8.1
 * @since JDK1.8+
 */
public class AbstractAliyunProvider<T extends Template, K extends Properties> extends AbstractProvider<T, K> {

    /**
     * 发送成功后返回code
     */
    private static final String SUCCESS_RESULT = "OK";

    public AbstractAliyunProvider(K properties) {
        super(properties);
    }

    /**
     * pop编码
     *
     * @param value 原值
     * @return 编码值
     */
    protected String specialUrlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8")
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new InstrumentException("aliyun specialUrlEncode error");
        }
    }

    /**
     * 构造签名
     *
     * @param params 参数
     * @return 签名值
     */
    protected String getSign(Map<String, String> params) {
        // 4. 参数KEY排序
        TreeMap<String, String> sortParas = new TreeMap<>(params);
        // 5. 构造待签名的字符串
        Iterator<String> it = sortParas.keySet().iterator();
        StringBuilder sortQueryStringTmp = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            sortQueryStringTmp
                    .append(Symbol.AND)
                    .append(specialUrlEncode(key))
                    .append(Symbol.EQUAL)
                    .append(specialUrlEncode(params.get(key)));
        }
        // 去除第一个多余的&符号
        String sortedQueryString = sortQueryStringTmp.substring(1);
        String stringToSign = "GET" + Symbol.AND +
                specialUrlEncode(Symbol.SLASH) + Symbol.AND +
                specialUrlEncode(sortedQueryString);
        return sign(stringToSign);
    }

    /**
     * 密钥签名
     *
     * @param stringToSign 代签名字符串
     * @return 签名后字符串
     */
    protected String sign(String stringToSign) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec((properties.getAppSecret() + Symbol.AND).getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signData);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new InstrumentException("aliyun specialUrlEncode error");
        }
    }

    protected Response checkResponse(String response) {
        JSONObject object = JSON.parseObject(response);
        return Response.builder()
                .result(SUCCESS_RESULT.equals(object.getString("Code")))
                .desc(object.getString("Code")).build();
    }

}
