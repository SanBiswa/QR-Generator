package project.com.qrgenerator.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class QRCodeRequest {
	private String qrType; // URL, TEXT, FILE, VCARD, WIFI, EMAIL, etc.
	private String content; // For URL, TEXT, EMAIL
	private MultipartFile file; // For FILE type (PDF, Image, Excel, etc.)

	// vCard fields
	private String name;
	private String phone;
	private String email;
	private String organization;

	// WiFi fields
	private String ssid;
	private String password;
	private String securityType; // WPA, WEP, nopass

	// Customization
	private Integer size; // QR code size (default 300)
	private String foregroundColor; // Default: black
	private String backgroundColor; // Default: white
}
