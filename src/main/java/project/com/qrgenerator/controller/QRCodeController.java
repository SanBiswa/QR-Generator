package project.com.qrgenerator.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.WriterException;

import project.com.qrgenerator.model.QRCodeRequest;
import project.com.qrgenerator.service.QRCodeService;

@Controller
public class QRCodeController {

	@Autowired
	private QRCodeService qrCodeService;

	/**
	 * Home page
	 */
	@GetMapping("/")
	public String home() {
		return "index";
	}

	/**
	 * Generate QR Code (Returns base64 image for preview)
	 */
	@PostMapping("/api/generate")
	@ResponseBody
	public ResponseEntity<Map<String, String>> generateQRCode(@ModelAttribute QRCodeRequest request) {
		try {
			byte[] qrCode = qrCodeService.generateQRCode(request);
			String base64Image = Base64.getEncoder().encodeToString(qrCode);

			Map<String, String> response = new HashMap<>();
			response.put("success", "true");
			response.put("image", "data:image/png;base64," + base64Image);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("success", "false");
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
		}
	}

	/**
	 * Download QR Code
	 */
	@PostMapping("/api/download")
	public ResponseEntity<byte[]> downloadQRCode(@ModelAttribute QRCodeRequest request) {
		try {
			byte[] qrCode = qrCodeService.generateQRCode(request);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_PNG);
			headers.setContentDispositionFormData("attachment", "qrcode.png");

			return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
		} catch (WriterException | IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
