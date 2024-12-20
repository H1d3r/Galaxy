package org.m2sec.core.utils;


import org.m2sec.core.common.Constants;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */

public class MacUtil {

    public static final String HMAC_SHA_256 = "HmacSHA256";

    public static byte[] calc(String algorithm, byte[] data, byte[] secret) {
        try {
            Mac mac = Mac.getInstance(algorithm, Constants.CRYPTO_PROVIDER);
            SecretKeySpec keySpec = new SecretKeySpec(secret, algorithm);
            mac.init(keySpec);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static String calcToHex(String algorithm, byte[] data, byte[] secret) {
        return CodeUtil.hexEncodeToString(calc(algorithm, data, secret));
    }

    public static String calcToBase64(String algorithm, byte[] data, byte[] secret) {
        return CodeUtil.b64encodeToString(calc(algorithm, data, secret));
    }
}
