//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//
//public class MessageDigestHelper {
//    public static byte[] getDigest(String algorithm, byte[] data) {
//        try {
//            return MessageDigest.getInstance(algorithm).digest(data);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static byte[] getFileDigest(String algorithm, String filePath) {
//        File file = new File(filePath);
//        if (!file.exists()) {
//            return null;
//        }
//        try {
//            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
//            try {
//                InputStream is = new FileInputStream(file);
//                byte[] buffer = new byte[Utils.IO_BUFFER_SIZE];
//                while (true) {
//                    try {
//                        int read = is.read(buffer);
//                        if (read <= 0) {
//                            return messageDigest.digest();
//                        }
//                        messageDigest.update(buffer, 0, read);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        return null;
//                    }
//                }
//            } catch (FileNotFoundException e2) {
//                e2.printStackTrace();
//                return null;
//            }
//        } catch (NoSuchAlgorithmException e3) {
//            e3.printStackTrace();
//            return null;
//        }
//    }
//}
