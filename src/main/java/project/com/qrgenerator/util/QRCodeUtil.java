package project.com.qrgenerator.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {
	/**
	 * Generate QR Code as byte array
	 */
	public static byte[] generateQRCode(String data, int width, int height, String foregroundColor,
			String backgroundColor) throws WriterException, IOException {

		// Set encoding hints
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

		// Convert hex colors to RGB
		int fgColor = hexToRgb(foregroundColor);
		int bgColor = hexToRgb(backgroundColor);

		MatrixToImageConfig config = new MatrixToImageConfig(fgColor, bgColor);
		BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

		// Convert to byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(qrImage, "PNG", baos);
		return baos.toByteArray();
	}

	/**
	 * Convert hex color to RGB integer
	 */
	private static int hexToRgb(String hexColor) {
		if (hexColor == null || hexColor.isEmpty()) {
			return 0xFF000000; // Default black
		}

		// Remove # if present
		hexColor = hexColor.replace("#", "");

		try {
			return (int) Long.parseLong(hexColor, 16) | 0xFF000000;
		} catch (NumberFormatException e) {
			return 0xFF000000; // Default black on error
		}
	}

	/**
	 * Generate vCard format
	 */
	public static String generateVCard(String name, String phone, String email, String organization) {
		StringBuilder vcard = new StringBuilder();
		vcard.append("BEGIN:VCARD\n");
		vcard.append("VERSION:3.0\n");

		if (name != null && !name.isEmpty()) {
			vcard.append("FN:").append(name).append("\n");
		}
		if (phone != null && !phone.isEmpty()) {
			vcard.append("TEL:").append(phone).append("\n");
		}
		if (email != null && !email.isEmpty()) {
			vcard.append("EMAIL:").append(email).append("\n");
		}
		if (organization != null && !organization.isEmpty()) {
			vcard.append("ORG:").append(organization).append("\n");
		}

		vcard.append("END:VCARD");
		return vcard.toString();
	}

	/**
	 * Generate WiFi format
	 */
	public static String generateWiFi(String ssid, String password, String securityType) {
		return String.format("WIFI:T:%s;S:%s;P:%s;;", securityType != null ? securityType : "WPA", ssid, password);
	}

	/**
	 * Generate email format
	 */
	public static String generateEmail(String email, String subject, String body) {
		StringBuilder emailStr = new StringBuilder("mailto:");
		emailStr.append(email);

		if (subject != null && !subject.isEmpty()) {
			emailStr.append("?subject=").append(subject);
			if (body != null && !body.isEmpty()) {
				emailStr.append("&body=").append(body);
			}
		} else if (body != null && !body.isEmpty()) {
			emailStr.append("?body=").append(body);
		}

		return emailStr.toString();
	}
}
