package com.exemplo.bancoalcatteia.controller;

//import com.cloudinary.Cloudinary;
import com.exemplo.bancoalcatteia.dto.UsuarioDTO;
import com.exemplo.bancoalcatteia.service.UsuarioService;
import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;

/*import java.io.IOException;
import java.util.HashMap;
import java.util.Map;*/

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    /*@Autowired
    private Cloudinary cloudinary;*/
    
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/criar")
    public ResponseEntity<UsuarioDTO> criar(@Valid @RequestBody UsuarioDTO usuarioDTO){
        UsuarioDTO usuarioCriado = usuarioService.criar(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioAtualizado = usuarioService.atualizar(id, usuarioDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/buscar")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@RequestBody UsuarioDTO usuarioDTO){
        UsuarioDTO usuario = usuarioService.buscarPorEmail(usuarioDTO.getEmail());
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioEncontrado = usuarioService.login(usuarioDTO.getEmail(), usuarioDTO.getSenha());
        return ResponseEntity.ok(usuarioEncontrado);
    }

    /*@GetMapping("/imagem/{publicId}")
    public ResponseEntity<String> recuperarImagem(@PathVariable String publicId) {
        String url = "https://res.cloudinary.com/dfykqixct/image/upload/" + publicId + ".jpg";
        return ResponseEntity.ok(url);
    }*/

    @GetMapping("{id}")
    public ResponseEntity<UsuarioDTO> buscar(@PathVariable Integer id){
        UsuarioDTO usuario = usuarioService.buscar(id);
        return ResponseEntity.ok(usuario);
    }

    /*@PostMapping("/upload")
    public ResponseEntity<String> uploadImagem(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), new HashMap<>());
            String url = (String) uploadResult.get("secure_url");
            return ResponseEntity.ok(url);
        }catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao fazer upload: " + e.getMessage());
        }
    }*/
}
