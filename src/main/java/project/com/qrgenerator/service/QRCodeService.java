package project.com.qrgenerator.service;

import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.WriterException;

import project.com.qrgenerator.model.QRCodeRequest;
import project.com.qrgenerator.util.QRCodeUtil;

@Service
public class QRCodeService {

	private static final int DEFAULT_SIZE = 300;
	private static final String DEFAULT_FG_COLOR = "000000"; // Black
	private static final String DEFAULT_BG_COLOR = "FFFFFF"; // White

	/**
	 * Generate QR Code based on request
	 */
	public byte[] generateQRCode(QRCodeRequest request) throws WriterException, IOException {
		String data = prepareQRData(request);

		int size = request.getSize() != null ? request.getSize() : DEFAULT_SIZE;
		String fgColor = request.getForegroundColor() != null ? request.getForegroundColor() : DEFAULT_FG_COLOR;
		String bgColor = request.getBackgroundColor() != null ? request.getBackgroundColor() : DEFAULT_BG_COLOR;

		return QRCodeUtil.generateQRCode(data, size, size, fgColor, bgColor);
	}

	/**
	 * Prepare data based on QR type
	 */
	private String prepareQRData(QRCodeRequest request) throws IOException {
		String qrType = request.getQrType();

		switch (qrType.toUpperCase()) {
		case "URL":
			return request.getContent();

		case "TEXT":
			return request.getContent();

		case "FILE":
			return handleFileUpload(request.getFile());

		case "VCARD":
			return QRCodeUtil.generateVCard(request.getName(), request.getPhone(), request.getEmail(),
					request.getOrganization());

		case "WIFI":
			return QRCodeUtil.generateWiFi(request.getSsid(), request.getPassword(), request.getSecurityType());

		case "EMAIL":
			return QRCodeUtil.generateEmail(request.getEmail(), null, null);

		default:
			return request.getContent();
		}
	}

	/**
	 * Handle file upload and convert to base64 data URL
	 */
	private String handleFileUpload(MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File is required for FILE type");
		}

		// Convert file to base64 data URL
		byte[] fileBytes = file.getBytes();
		String base64 = Base64.getEncoder().encodeToString(fileBytes);
		String contentType = file.getContentType();

		// Create data URL
		return "data:" + contentType + ";base64," + base64;
	}
}
