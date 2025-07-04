package com.exemplo.bancoalcatteia.controller;

//import com.cloudinary.Cloudinary;
import com.exemplo.bancoalcatteia.dto.LoginDTO;
import com.exemplo.bancoalcatteia.dto.UsuarioDTO;
import com.exemplo.bancoalcatteia.service.SessionService;
import com.exemplo.bancoalcatteia.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/*import java.io.IOException;
import java.util.HashMap;
import java.util.Map;*/

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    /*@Autowired
    private Cloudinary cloudinary;*/
    
    private final UsuarioService usuarioService;
    private final SessionService sessionService;

    public UsuarioController(UsuarioService usuarioService, SessionService sessionService) {
        this.usuarioService = usuarioService;
        this.sessionService = sessionService;
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
    public ResponseEntity<UsuarioDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        // Debug: verificar o que está chegando
        System.out.println("DEBUG - Email recebido: " + loginDTO.getEmail());
        System.out.println("DEBUG - Senha recebida: " + loginDTO.getSenha());
        System.out.println("DEBUG - LoginDTO completo: " + loginDTO);
        
        UsuarioDTO usuarioEncontrado = usuarioService.login(loginDTO.getEmail(), loginDTO.getSenha(), response);
        return ResponseEntity.ok(usuarioEncontrado);
    }

    @PostMapping("/test-login-data")
    public ResponseEntity<Map<String, String>> testLoginData(@RequestBody LoginDTO loginDTO) {
        Map<String, String> response = new HashMap<>();
        response.put("email", loginDTO.getEmail());
        response.put("senha", loginDTO.getSenha());
        response.put("senhaIsNull", String.valueOf(loginDTO.getSenha() == null));
        response.put("emailIsNull", String.valueOf(loginDTO.getEmail() == null));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        sessionService.removeSession(request, response);
        return ResponseEntity.ok().build();
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
