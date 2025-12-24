# Mail & Cloud Storage Implementation

## ✅ Components Created

### 📧 **Mail Service** (4 files)

**Java Classes:**
- [MailProperties.java](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/java/com/vsa/ecommerce/common/mail/MailProperties.java) - Mail configuration properties
- [MailService.java](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/java/com/vsa/ecommerce/common/mail/MailService.java) - Email service with template support

**Email Templates:**
- [welcome.html](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/resources/templates/email/welcome.html) - Welcome email for new users
- [password-reset.html](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/resources/templates/email/password-reset.html) - Password reset email
- [order-confirmation.html](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/resources/templates/email/order-confirmation.html) - Order confirmation email

### ☁️ **Cloudinary Storage** (5 files)

- [CloudinaryProperties.java](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/java/com/vsa/ecommerce/common/storage/CloudinaryProperties.java) - Cloudinary config properties
- [CloudinaryConfig.java](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/java/com/vsa/ecommerce/common/storage/CloudinaryConfig.java) - Cloudinary bean configuration
- [StorageService.java](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/java/com/vsa/ecommerce/common/storage/StorageService.java) - Storage interface
- [CloudinaryStorageService.java](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/java/com/vsa/ecommerce/common/storage/CloudinaryStorageService.java) - Cloudinary implementation
- [UploadResult.java](file:///d:/a_project/srcbase/java-vsa-monolith-sourcebase/src/main/java/com/vsa/ecommerce/common/storage/UploadResult.java) - Upload result DTO

---

## 📝 Configuration

### **application.yml**

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME:your_email@gmail.com}
    password: ${MAIL_PASSWORD:your_app_password}
    from: ${MAIL_FROM:noreply@vsa-ecommerce.com}

cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME:your_cloud_name}
  api-key: ${CLOUDINARY_API_KEY:your_api_key}
  api-secret: ${CLOUDINARY_API_SECRET:your_api_secret}
  default-folder: vsa-ecommerce
```

### **Environment Variables**

Set these in production:

```bash
# Mail Configuration
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_gmail_app_password
export MAIL_FROM=noreply@vsa-ecommerce.com

# Cloudinary Configuration
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
```

---

## 💡 Usage Examples

### **Send Welcome Email**

```java
@Service
public class UserService {
    private final MailService mailService;
    
    public void registerUser(User user) {
        // Save user to database
        userRepository.save(user);
        
        // Send welcome email
        mailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());
    }
}
```

### **Send Password Reset Email**

```java
@Service
public class AuthService {
    private final MailService mailService;
    
    public void requestPasswordReset(String email) {
        String resetToken = generateResetToken();
        String resetLink = "https://yourapp.com/reset-password?token=" + resetToken;
        
        mailService.sendPasswordResetEmail(email, resetLink);
    }
}
```

### **Send Order Confirmation**

```java
@Service
public class OrderService {
    private final MailService mailService;
    
    public void createOrder(Order order) {
        orderRepository.save(order);
        
        mailService.sendOrderConfirmationEmail(
            order.getUser().getEmail(),
            order.getId().toString(),
            order.getTotalAmount().toString()
        );
    }
}
```

### **Upload File to Cloudinary**

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final StorageService storageService;
    
    @PostMapping("/upload-image")
    public ResponseEntity<UploadResult> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            UploadResult result = storageService.uploadFile(file, "products");
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
```

### **Upload with Image Transformation**

```java
@Service
public class ProductService {
    @Autowired
    private CloudinaryStorageService cloudinaryService;
    
    public String uploadProductImage(MultipartFile file) throws IOException {
        // Upload and resize to 800x800
        UploadResult result = cloudinaryService.uploadImage(file, "products", 800, 800);
        return result.getSecureUrl();
    }
}
```

### **Delete File**

```java
public void deleteProductImage(String publicId) throws IOException {
    storageService.deleteFile(publicId);
}
```

---

## 🎨 Email Template Customization

Email templates are located in `src/main/resources/templates/email/`.

### **Creating New Template**

1. Create HTML file in `templates/email/my-template.html`
2. Use Thymeleaf syntax for variables:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <h1>Hello <span th:text="${username}"></span>!</h1>
    <p th:text="${message}"></p>
</body>
</html>
```

3. Send email in service:

```java
mailService.sendHtmlEmail(
    "user@example.com",
    "Subject",
    "my-template",
    Map.of("username", "John", "message", "Welcome!")
);
```

---

## 🔐 Gmail App Password Setup

1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable 2-Step Verification
3. Go to "App passwords"
4. Generate password for "Mail"
5. Use generated password in `MAIL_PASSWORD`

---

## ☁️ Cloudinary Setup

1. Sign up at [Cloudinary.com](https://cloudinary.com)
2. Get credentials from Dashboard:
   - Cloud Name
   - API Key
   - API Secret
3. Set environment variables

---

## ✅ Features

### **Mail Service**
- ✅ Plain text and HTML emails
- ✅ Thymeleaf template engine
- ✅ Async sending (non-blocking)
- ✅ Pre-built templates (welcome, reset, order)
- ✅ Environment variable support

### **Cloudinary Storage**
- ✅ Upload images, videos, raw files
- ✅ Automatic format detection
- ✅ Unique file naming (UUID)
- ✅ Folder organization
- ✅ Image transformations (resize, crop)
- ✅ Delete files
- ✅ Secure HTTPS URLs

---

## 🚀 Next Steps

1. Set environment variables for production
2. Create additional email templates as needed
3. Test email sending with real SMTP server
4. Test file upload to Cloudinary
5. Add file validation (size, type)
6. Add rate limiting for email sending
