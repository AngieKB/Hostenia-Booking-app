package co.edu.uniquindio.Application.Services.PruebasUnitarias;

import co.edu.uniquindio.Application.DTO.Alojamiento.*;
import co.edu.uniquindio.Application.DTO.Comentario.ComentarioDTO;
import co.edu.uniquindio.Application.DTO.Reserva.ReservaDTO;
import co.edu.uniquindio.Application.Model.*;
import co.edu.uniquindio.Application.Repository.AlojamientoRepository;
import co.edu.uniquindio.Application.Repository.PerfilAnfitrionRepository;
import co.edu.uniquindio.Application.Repository.UsuarioRepository;
import co.edu.uniquindio.Application.Services.impl.AlojamientoServiceImpl;
import co.edu.uniquindio.Application.Mappers.AlojamientoMapper;
import co.edu.uniquindio.Application.Services.ImageService;
import co.edu.uniquindio.Application.Services.impl.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlojamientoServiceUnitTest {

    @InjectMocks
    private AlojamientoServiceImpl alojamientoService;

    @Mock
    private AlojamientoRepository alojamientoRepository;

    @Mock
    private PerfilAnfitrionRepository perfilAnfitrionRepository;

    @Mock
    private AlojamientoMapper alojamientoMapper;

    @Mock
    private ImageService imageService;

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AuthService authService;

    private Alojamiento alojamiento;
    private AlojamientoDTO alojamientoDTO;
    private EditarAlojamientoDTO editarAlojamientoDTO;
    private ComentarioDTO comentarioDTO;
    private ReservaDTO reservaDTO;
    private UbicacionDTO ubicacionDTO;
    private Usuario usuarioFavoritos;
    private Usuario usuarioAnfitrion;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ubicacionDTO = new UbicacionDTO("Calle 123", "Bogotá", "Colombia", 4.7, -74.1);
        comentarioDTO = new ComentarioDTO(1L, "Hermoso lugar, todo muy limpio y tranquilo.", 5, LocalDateTime.of(2025, 3, 15, 14, 30),"paco","pepe","foto","foto","melo");
        reservaDTO = new ReservaDTO(1L, 1L, 2L, LocalDateTime.of(2025, 5, 10, 15, 0), LocalDateTime.of(2025, 5, 15, 12, 0),
                2, 750.00, EstadoReserva.CONFIRMADA);

//        alojamientoDTO = new AlojamientoDTO(
//                1L,
//                "Cabaña en las montañas",
//                "Una cabaña acogedora con vista al bosque y chimenea incluida.",
//                List.of("WiFi", "Chimenea", "Parqueadero", "Cocina equipada"),
//                null,
//                ubicacionDTO,
//                150.00,
//                4,
//                List.of(comentarioDTO),
//                List.of(reservaDTO),
//                EstadoAlojamiento.ACTIVO
//        );
        editarAlojamientoDTO = new EditarAlojamientoDTO(
                "Cabaña en las montañas",
                "Una cabaña acogedora con vista al bosque y chimenea incluida.",
                List.of("WiFi", "Chimenea", "Parqueadero", "Cocina equipada"),
                null,
                "Armenia",
                "Calle 45 #23-56",
                23.5667,
                -87.1234,
                150000.0,
                4
        );

        // Usuario anfitrión
        Usuario anfitrionUsuario = new Usuario();
        anfitrionUsuario.setId(2L);
        anfitrionUsuario.setNombre("Pedro");
        anfitrionUsuario.setEmail("pedro@mail.com");

        usuarioFavoritos = new Usuario();
        usuarioFavoritos.setId(1L);
        usuarioFavoritos.setFavoritos(new ArrayList<>());


        PerfilAnfitrion perfilAnfitrion = new PerfilAnfitrion();
        perfilAnfitrion.setUsuario(anfitrionUsuario);
        alojamiento = new Alojamiento();
        alojamiento.setUsuariosFavoritos(new ArrayList<>());
        alojamiento.setId(1L);
        alojamiento.setTitulo("Hotel Central");
        alojamiento.setDescripcion("Descripción del hotel");
        alojamiento.setServicios(List.of("WiFi", "Piscina"));
        alojamiento.setGaleria(new ArrayList<>());
        alojamiento.setPrecioNoche(200000.0);
        alojamiento.setCapacidadMax(4);
        alojamiento.setEstado(EstadoAlojamiento.ACTIVO);
        alojamiento.setUbicacion(new Ubicacion(
                "Calle 123",
                "Bogotá",
                "Colombia",
                4.7,
                -74.1
        ));
        alojamiento.setReservas(new ArrayList<>());
        alojamiento.setComentarios(new ArrayList<>());
        alojamiento.setAnfitrion(perfilAnfitrion);
    }

    @Test
    void guardarExitoso() throws Exception {
        // Mock DTO y archivo
        CrearAlojamientoDTO crearDTO = mock(CrearAlojamientoDTO.class);
        MultipartFile file = mock(MultipartFile.class);

        // Usuario autenticado y perfil de anfitrión
        Usuario anfitrionUsuario = new Usuario();
        anfitrionUsuario.setId(1L);
        PerfilAnfitrion perfilAnfitrion = new PerfilAnfitrion();
        perfilAnfitrion.setUsuario(anfitrionUsuario);

        // Stub del authService para devolver el usuario autenticado
        when(authService.getUsuarioAutenticado()).thenReturn(anfitrionUsuario);

        // Stub del repositorio de perfiles para devolver el perfil del usuario
        when(perfilAnfitrionRepository.findByUsuario(anfitrionUsuario))
                .thenReturn(Optional.of(perfilAnfitrion));

        // Stubs del DTO
        when(crearDTO.galeria()).thenReturn(List.of(file));
        when(imageService.upload(file)).thenReturn(Map.of("url", "http://image.com/img1.jpg"));

        // Stub del mapper
        when(alojamientoMapper.toEntity(crearDTO)).thenReturn(alojamiento);
        when(alojamientoMapper.crearUbicacion(crearDTO)).thenReturn(alojamiento.getUbicacion());

        // Ejecutar método
        alojamientoService.guardar(crearDTO);

        // Verificaciones
        verify(alojamientoRepository, times(1)).save(alojamiento);
        assertEquals(List.of("http://image.com/img1.jpg"), alojamiento.getGaleria());
        assertEquals(EstadoAlojamiento.ACTIVO, alojamiento.getEstado());
    }


    @Test
    void editarAlojamientoExitoso() throws Exception {
        // DTOs de edición (pueden ser mocks)
        AlojamientoDTO alojamientoDTO = mock(AlojamientoDTO.class);
        UbicacionDTO ubicacionDTO = mock(UbicacionDTO.class);

        // Stubs del repositorio y authService
        when(alojamientoRepository.findById(alojamiento.getId())).thenReturn(Optional.of(alojamiento));
        when(authService.getUsuarioAutenticado()).thenReturn(alojamiento.getAnfitrion().getUsuario());

        // Ejecutar método
        alojamientoService.editarAlojamiento(alojamiento.getId(), editarAlojamientoDTO, ubicacionDTO);

        // Verificaciones
        verify(alojamientoMapper, times(1)).updateEntity(alojamiento, editarAlojamientoDTO, ubicacionDTO);
        verify(alojamientoRepository, times(1)).save(alojamiento);
    }




    @Test
    void eliminarSinReservasFuturasExitoso() throws Exception {
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));

        alojamientoService.eliminar(1L);

        assertEquals(EstadoAlojamiento.INACTIVO, alojamiento.getEstado());
        verify(alojamientoRepository, times(1)).save(alojamiento);
    }

    @Test
    void eliminarConReservasFuturasLanzaExcepcion() {
        Reserva reservaFutura = new Reserva();
        reservaFutura.setFechaCheckIn(LocalDateTime.now().plusDays(5));
        reservaFutura.setEstado(EstadoReserva.PENDIENTE);
        alojamiento.setReservas(List.of(reservaFutura));

        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> alojamientoService.eliminar(1L));

        assertEquals("No se puede eliminar el alojamiento porque tiene reservas futuras.", exception.getMessage());
        verify(alojamientoRepository, never()).save(any());
    }

    @Test
    void verMetricasExitoso() throws Exception {
        Comentario comentario = new Comentario();
        comentario.setCalificacion(4);
        comentario.setFecha(LocalDateTime.now());
        alojamiento.setComentarios(List.of(comentario));

        Reserva reserva = new Reserva();
        reserva.setFechaCheckIn(LocalDateTime.now().plusDays(1));
        reserva.setFechaCheckOut(LocalDateTime.now().plusDays(2));
        alojamiento.setReservas(List.of(reserva));

        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));

        MetricasDTO metrics = alojamientoService.verMetricas(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3));

        assertEquals(1, metrics.totalReservas());
        assertEquals(4.0, metrics.promedioCalificaciones());
    }


    @Test
    void obtenerPorIdExitoso() throws Exception {
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));
        when(alojamientoMapper.toDTO(alojamiento)).thenReturn(alojamientoDTO);

        AlojamientoDTO dto = alojamientoService.obtenerPorId(1L);

        assertEquals(alojamientoDTO, dto);
    }
    @Test
    void buscarPorCiudadPredictiva() {
        AlojamientoDTO resumenDTO = mock(AlojamientoDTO.class);

        // Simular alojamiento que contiene "Bogotá D.C."
        Alojamiento alojamientoBogotaDC = new Alojamiento();
        alojamientoBogotaDC.setUbicacion(new Ubicacion("Calle 1", "Bogotá D.C.", "Colombia", 4.7, -74.1));
        alojamientoBogotaDC.setEstado(EstadoAlojamiento.ACTIVO);

        Pageable pageable = PageRequest.of(0, 9); // página 0, tamaño 9
        Page<Alojamiento> page = new PageImpl<>(List.of(alojamientoBogotaDC), pageable, 1);

        when(alojamientoRepository.findByUbicacionCiudadContainingIgnoreCaseAndEstado("Bogotá", EstadoAlojamiento.ACTIVO, pageable))
                .thenReturn(page);
        when(alojamientoMapper.toDTO(alojamientoBogotaDC)).thenReturn(resumenDTO);

        Page<AlojamientoDTO> resultados = alojamientoService.buscarPorCiudad("Bogotá", 0, 9);

        assertEquals(1, resultados.getContent().size());
        assertEquals(resumenDTO, resultados.getContent().get(0));
    }

    @Test
    void buscarPorFechasDisponible() {
        AlojamientoDTO resumenDTO = mock(AlojamientoDTO.class);
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusDays(5);

        // Alojamiento sin reservas en el rango → disponible
        Alojamiento disponible = new Alojamiento();
        disponible.setReservas(new ArrayList<>());
        disponible.setEstado(EstadoAlojamiento.ACTIVO);

        Pageable pageable = PageRequest.of(0, 9);
        Page<Alojamiento> page = new PageImpl<>(List.of(disponible), pageable, 1);

        when(alojamientoRepository.findByDate(inicio, fin, EstadoAlojamiento.ACTIVO, pageable))
                .thenReturn(page);

        when(alojamientoMapper.toDTO(disponible)).thenReturn(resumenDTO);

        Page<AlojamientoDTO> resultados = alojamientoService.buscarPorFechas(inicio, fin, 0, 9);

        assertEquals(1, resultados.getContent().size());
        assertEquals(resumenDTO, resultados.getContent().get(0));
    }

    @Test
    void buscarPorPrecioRango() {
        AlojamientoDTO resumenDTO = mock(AlojamientoDTO.class);

        Alojamiento hotel = new Alojamiento();
        hotel.setPrecioNoche(150.0);
        hotel.setEstado(EstadoAlojamiento.ACTIVO);

        Pageable pageable = PageRequest.of(0, 9);
        Page<Alojamiento> page = new PageImpl<>(List.of(hotel), pageable, 1);

        when(alojamientoRepository.findByPrecioNocheBetweenAndEstado(50.0, 200.0, EstadoAlojamiento.ACTIVO, pageable))
                .thenReturn(page);
        when(alojamientoMapper.toDTO(hotel)).thenReturn(resumenDTO);

        Page<AlojamientoDTO> resultados = alojamientoService.buscarPorPrecio(50.0, 200.0, 0, 9);

        assertEquals(1, resultados.getContent().size());
        assertEquals(resumenDTO, resultados.getContent().get(0));
    }

    @Test
    void buscarPorServiciosExitoso() {
        List<String> servicios = List.of("WiFi", "Piscina");
        Alojamiento hotel = new Alojamiento();
        hotel.setServicios(List.of("WiFi", "Piscina", "Mascotas"));
        hotel.setEstado(EstadoAlojamiento.ACTIVO);

        Pageable pageable = PageRequest.of(0, 9);
        Page<Alojamiento> page = new PageImpl<>(List.of(hotel), pageable, 1);

        when(alojamientoRepository.findByServicios(servicios, servicios.size(), pageable))
                .thenReturn(page);
        when(alojamientoMapper.toDTO(hotel)).thenReturn(alojamientoDTO);

        Page<AlojamientoDTO> resultados = alojamientoService.buscarPorServicios(servicios, 0, 9);

        assertEquals(1, resultados.getContent().size());
        assertEquals(alojamientoDTO, resultados.getContent().get(0));
    }

    @Test
    void agregarAFavoritos_deberiaAgregar() throws Exception {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFavoritos));
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));
        when(usuarioRepository.save(any())).thenReturn(usuarioFavoritos);

        alojamientoService.agregarAFavoritos(1L, 1L);

        assertTrue(usuarioFavoritos.getFavoritos().contains(alojamiento));
        assertTrue(alojamiento.getUsuariosFavoritos().contains(usuarioFavoritos));
    }

    @Test
    void quitarDeFavoritos_deberiaRemover() throws Exception {
        usuarioFavoritos.getFavoritos().add(alojamiento);
        alojamiento.getUsuariosFavoritos().add(usuarioFavoritos);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFavoritos));
        when(alojamientoRepository.findById(1L)).thenReturn(Optional.of(alojamiento));
        when(usuarioRepository.save(any())).thenReturn(usuarioFavoritos);

        alojamientoService.quitarDeFavoritos(1L, 1L);

        assertFalse(usuarioFavoritos.getFavoritos().contains(alojamiento));
        assertFalse(alojamiento.getUsuariosFavoritos().contains(usuarioFavoritos));
    }

    @Test
    void listarFavoritos_deberiaDevolverDTOsPaginado() throws Exception {
        usuarioFavoritos.getFavoritos().add(alojamiento);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioFavoritos));
        when(alojamientoMapper.toDTO(alojamiento)).thenReturn(alojamientoDTO);

        Page<AlojamientoDTO> favoritos = alojamientoService.listarFavoritos(1L, 0, 10);

        assertNotNull(favoritos);
        assertEquals(1, favoritos.getContent().size());
        assertEquals(alojamientoDTO, favoritos.getContent().get(0));

        verify(usuarioRepository, times(1)).findById(1L);
        verify(alojamientoMapper, times(1)).toDTO(alojamiento);
    }
}