package com.tango.experiment.utils;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class MD5Utils {
    public static String encrypt(String password) {
        try {
            // 获取 MD5 MessageDigest 实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 进行加密处理
            byte[] digest = md.digest(password.getBytes());

            // 将字节数组转换为十六进制的字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            // 返回加密后的字符串
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5Utils encrypt error:{}", e.getMessage());
        }
        return null;
    }
}
