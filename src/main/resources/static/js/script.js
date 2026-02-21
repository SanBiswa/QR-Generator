document.addEventListener('DOMContentLoaded', function() {
    const qrTypeSelect = document.getElementById('qrType');
    const dynamicFields = document.getElementById('dynamicFields');
    const qrForm = document.getElementById('qrForm');
    const qrPreview = document.getElementById('qrPreview');
    const downloadBtn = document.getElementById('downloadBtn');
    const errorMessage = document.getElementById('errorMessage');

    // Field templates for different QR types
    const fieldTemplates = {
        URL: `
            <div class="form-group">
                <label for="content">Website URL:</label>
                <input type="url" id="content" name="content" 
                       placeholder="https://example.com" required>
            </div>
        `,
        TEXT: `
            <div class="form-group">
                <label for="content">Text Content:</label>
                <textarea id="content" name="content" rows="5" 
                          placeholder="Enter your text here..." required></textarea>
            </div>
        `,
        FILE: `
            <div class="form-group">
                <label for="file">Upload File:</label>
                <input type="file" id="file" name="file" 
                       accept=".pdf,.jpg,.jpeg,.png,.xlsx,.xls,.doc,.docx" required>
                <small style="color: #666; display: block; margin-top: 5px;">
                    Supported: PDF, Images, Excel, Word documents
                </small>
            </div>
        `,
        VCARD: `
            <div class="form-group">
                <label for="name">Full Name:</label>
                <input type="text" id="name" name="name" 
                       placeholder="John Doe" required>
            </div>
            <div class="form-group">
                <label for="phone">Phone Number:</label>
                <input type="text" id="phone" name="phone" 
                       placeholder="+1234567890">
            </div>
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" 
                       placeholder="john@example.com">
            </div>
            <div class="form-group">
                <label for="organization">Organization:</label>
                <input type="text" id="organization" name="organization" 
                       placeholder="Company Name">
            </div>
        `,
        WIFI: `
            <div class="form-group">
                <label for="ssid">Network Name (SSID):</label>
                <input type="text" id="ssid" name="ssid" 
                       placeholder="MyWiFiNetwork" required>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="text" id="password" name="password" 
                       placeholder="WiFi Password" required>
            </div>
            <div class="form-group">
                <label for="securityType">Security Type:</label>
                <select id="securityType" name="securityType" required>
                    <option value="WPA">WPA/WPA2</option>
                    <option value="WEP">WEP</option>
                    <option value="nopass">No Password</option>
                </select>
            </div>
        `,
        EMAIL: `
            <div class="form-group">
                <label for="email">Email Address:</label>
                <input type="email" id="email" name="email" 
                       placeholder="contact@example.com" required>
            </div>
        `
    };

    // Update dynamic fields based on QR type selection
    qrTypeSelect.addEventListener('change', function() {
        const selectedType = this.value;
        if (selectedType && fieldTemplates[selectedType]) {
            dynamicFields.innerHTML = fieldTemplates[selectedType];
        } else {
            dynamicFields.innerHTML = '';
        }
        
        // Reset preview
        qrPreview.innerHTML = '<p class="placeholder">Your QR code will appear here</p>';
        downloadBtn.style.display = 'none';
        errorMessage.style.display = 'none';
    });

    // Form submission
    qrForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = new FormData(qrForm);
        
        // Show loading state
        qrPreview.innerHTML = '<div class="loading"></div>';
        errorMessage.style.display = 'none';
        downloadBtn.style.display = 'none';

        // Send AJAX request
        fetch('/api/generate', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success === 'true') {
                // Display QR code
                qrPreview.innerHTML = `<img src="${data.image}" alt="QR Code">`;
                downloadBtn.style.display = 'block';
            } else {
                throw new Error(data.error || 'Failed to generate QR code');
            }
        })
        .catch(error => {
            qrPreview.innerHTML = '<p class="placeholder">Failed to generate QR code</p>';
            errorMessage.textContent = error.message;
            errorMessage.style.display = 'block';
        });
    });

    // Download functionality
    downloadBtn.addEventListener('click', function() {
        const formData = new FormData(qrForm);
        
        // Create a temporary form for download
        const downloadForm = document.createElement('form');
        downloadForm.method = 'POST';
        downloadForm.action = '/api/download';
        
        // Append all form data
        for (let [key, value] of formData.entries()) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            
            if (value instanceof File) {
                // Skip file input for now (handle separately if needed)
                continue;
            }
            
            input.value = value;
            downloadForm.appendChild(input);
        }
        
        document.body.appendChild(downloadForm);
        downloadForm.submit();
        document.body.removeChild(downloadForm);
    });

    // Reset form
    qrForm.addEventListener('reset', function() {
        dynamicFields.innerHTML = '';
        qrPreview.innerHTML = '<p class="placeholder">Your QR code will appear here</p>';
        downloadBtn.style.display = 'none';
        errorMessage.style.display = 'none';
    });
});