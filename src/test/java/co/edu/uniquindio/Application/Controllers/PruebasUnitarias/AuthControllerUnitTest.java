package co.edu.uniquindio.Application.Controllers.PruebasUnitarias;

import co.edu.uniquindio.Application.Controllers.AuthController;
import co.edu.uniquindio.Application.DTO.Anfitrion.CrearAnfitrionDTO;
import co.edu.uniquindio.Application.DTO.ResponseDTO;
import co.edu.uniquindio.Application.DTO.TokenDTO;
import co.edu.uniquindio.Application.DTO.Usuario.*;
import co.edu.uniquindio.Application.Services.PerfilAnfitrionService;
import co.edu.uniquindio.Application.Services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerUnitTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PerfilAnfitrionService perfilAnfitrionService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_Exitoso() throws Exception {
        LoginDTO loginDTO = new LoginDTO("correo@test.com", "password123");
        TokenDTO token = new TokenDTO("jwtTokenEjemplo");
        when(usuarioService.login(loginDTO)).thenReturn(token);

        ResponseEntity<ResponseDTO<TokenDTO>> response = authController.login(loginDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(token, Objects.requireNonNull(response.getBody()).content());
        verify(usuarioService, times(1)).login(loginDTO);
    }

    @Test
    void login_ErrorCredencialesInvalidas() throws Exception {
        LoginDTO loginDTO = new LoginDTO("correo@test.com", "claveIncorrecta");
        when(usuarioService.login(loginDTO)).thenThrow(new Exception("Credenciales inválidas"));

        Exception exception = assertThrows(Exception.class, () -> authController.login(loginDTO));
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioService, times(1)).login(loginDTO);
    }

    @Test
    void registroUsuario_Exitoso() throws Exception {
        CrearUsuarioDTO userDTO = new CrearUsuarioDTO("Juan", "juan@test.com", "123456789", "1234", null, null);

        doNothing().when(usuarioService).create(userDTO);

        ResponseEntity<ResponseDTO<String>> response = authController.create(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("El registro ha sido exitoso", Objects.requireNonNull(response.getBody()).content());
        verify(usuarioService, times(1)).create(userDTO);
    }

    @Test
    void registroUsuario_ErrorCorreoExistente() throws Exception {
        CrearUsuarioDTO userDTO = new CrearUsuarioDTO("Juan", "yaExiste@test.com", "123456789", "1234", null, null);
        doThrow(new Exception("El correo ya está registrado")).when(usuarioService).create(userDTO);

        Exception exception = assertThrows(Exception.class, () -> authController.create(userDTO));
        assertEquals("El correo ya está registrado", exception.getMessage());
        verify(usuarioService, times(1)).create(userDTO);
    }

    @Test
    void crearPerfilAnfitrion_Exitoso() {
        CrearAnfitrionDTO dto = new CrearAnfitrionDTO("Descripcion", null, 1L);
        doNothing().when(perfilAnfitrionService).crearPerfil(dto);

        ResponseEntity<ResponseDTO<String>> response = authController.crearPerfilAnfitrion(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Perfil creado exitosamente", Objects.requireNonNull(response.getBody()).content());
        verify(perfilAnfitrionService, times(1)).crearPerfil(dto);
    }

    @Test
    void crearPerfilAnfitrion_ErrorUsuarioNoExiste() {
        CrearAnfitrionDTO dto = new CrearAnfitrionDTO("Descripcion", null, 99L);
        doThrow(new RuntimeException("Usuario no encontrado")).when(perfilAnfitrionService).crearPerfil(dto);

        assertThrows(RuntimeException.class, () -> authController.crearPerfilAnfitrion(dto));
        verify(perfilAnfitrionService, times(1)).crearPerfil(dto);
    }

    @Test
    void forgotPassword_Exitoso() throws Exception {
        ForgotPasswordDTO dto = new ForgotPasswordDTO("correo@test.com");
        doNothing().when(usuarioService).sendVerificationCode(dto);

        ResponseEntity<ResponseDTO<String>> response = authController.sendVerificationCode(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Código enviado", Objects.requireNonNull(response.getBody()).content());
        verify(usuarioService, times(1)).sendVerificationCode(dto);
    }

    @Test
    void forgotPassword_ErrorCorreoNoExiste() throws Exception {
        ForgotPasswordDTO dto = new ForgotPasswordDTO("noexiste@test.com");
        doThrow(new Exception("Correo no encontrado")).when(usuarioService).sendVerificationCode(dto);

        Exception exception = assertThrows(Exception.class, () -> authController.sendVerificationCode(dto));
        assertEquals("Correo no encontrado", exception.getMessage());
        verify(usuarioService, times(1)).sendVerificationCode(dto);
    }

    @Test
    void resetPassword_Exitoso() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO("correo@example","codigo123", "nuevaClave");
        doNothing().when(usuarioService).resetPassword(dto);

        ResponseEntity<ResponseDTO<String>> response = authController.changePassword(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Contraseña cambiada", Objects.requireNonNull(response.getBody()).content());
        verify(usuarioService, times(1)).resetPassword(dto);
    }

    @Test
    void resetPassword_ErrorCodigoInvalido() throws Exception {
        ResetPasswordDTO dto = new ResetPasswordDTO("correo@example","codigoErroneo", "clave");
        doThrow(new Exception("Código inválido")).when(usuarioService).resetPassword(dto);

        Exception exception = assertThrows(Exception.class, () -> authController.changePassword(dto));
        assertEquals("Código inválido", exception.getMessage());
        verify(usuarioService, times(1)).resetPassword(dto);
    }
}
