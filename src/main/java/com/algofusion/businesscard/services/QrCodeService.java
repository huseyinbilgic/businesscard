package com.algofusion.businesscard.services;

import java.util.Base64;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;

import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import io.nayuki.qrcodegen.QrCode;

@Service
public class QrCodeService {
    private static final int SIZE = 300; // QR kod boyutu

    // QR kodu byte dizisi olarak döndür (PNG formatında)
    public byte[] generateQRCodeImage(String text) throws Exception {
        QrCode qr = QrCode.encodeText(text, QrCode.Ecc.MEDIUM); // QR kod oluştur
        BufferedImage image = toImage(qr, SIZE, 4); // QR kodu görsel formatına çevir

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream); // PNG formatında çıktı al
        return outputStream.toByteArray();
    }

    // QR kodu Base64 string olarak döndür
    public String generateQRCodeBase64(String text) throws Exception {
        return Base64.getEncoder().encodeToString(generateQRCodeImage(text));
    }

    // QR kodunu BufferedImage'e çevirme fonksiyonu
    private static BufferedImage toImage(QrCode qr, int scale, int border) {
        int size = qr.size + border * 2;
        BufferedImage image = new BufferedImage(size * scale, size * scale, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.setColor(Color.BLACK);

        for (int y = 0; y < qr.size; y++) {
            for (int x = 0; x < qr.size; x++) {
                if (qr.getModule(x, y)) {
                    g.fillRect((x + border) * scale, (y + border) * scale, scale, scale);
                }
            }
        }
        g.dispose();
        return image;
    }
}
