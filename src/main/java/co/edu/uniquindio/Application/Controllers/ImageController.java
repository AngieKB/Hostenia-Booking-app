package co.edu.uniquindio.Application.Controllers;

import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.Services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO<Map>> upload(@RequestParam("file") MultipartFile image) throws Exception{
        Map response = imageService.upload(image);
        return ResponseEntity.ok( new ResponseDTO<>(false, HttpStatus.OK.value(), response) );
    }

    @PreAuthorize("hasAnyRole('HUESPED', 'ANFITRION')")
    @DeleteMapping
    public ResponseEntity<ResponseDTO<String>> delete(@RequestParam("id") String id) throws Exception{
        imageService.delete(id);
        return ResponseEntity.ok( new ResponseDTO<>(false, HttpStatus.OK.value(), "Imagen eliminada exitosamente") );
    }

}
