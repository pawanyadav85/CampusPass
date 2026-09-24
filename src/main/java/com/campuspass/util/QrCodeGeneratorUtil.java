package com.campuspass.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class QrCodeGeneratorUtil {

    public static byte[] generateQrCodeImageBytes(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate QR Code image", ex);
        }
    }

    public static String generateQrCodeBase64(String text, int width, int height) {
        byte[] bytes = generateQrCodeImageBytes(text, width, height);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
    }
}
