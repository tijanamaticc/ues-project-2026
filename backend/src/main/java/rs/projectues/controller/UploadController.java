package rs.projectues.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "*")
public class UploadController {
    private final Path uploadsDir = Paths.get("src/main/resources/static/uploads");

    public UploadController() throws IOException {
        Files.createDirectories(uploadsDir);
    }

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Fajl je prazan");
        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String name = UUID.randomUUID().toString() + (ext != null ? "." + ext : "");
        Path dest = uploadsDir.resolve(name);
        Files.copy(file.getInputStream(), dest);
        String publicUrl = "/uploads/" + name;
        return ResponseEntity.ok(java.util.Map.of("url", publicUrl));
    }
}
