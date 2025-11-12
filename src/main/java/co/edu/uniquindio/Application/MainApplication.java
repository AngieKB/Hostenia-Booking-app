package co.edu.uniquindio.Application;
import co.edu.uniquindio.Application.Services.impl.ReservaServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class MainApplication implements CommandLineRunner {

    private final ReservaServiceImpl reservaService;

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("➡️ Ejecutando actualización de reservas completadas al iniciar la app...");
        reservaService.actualizarReservasCompletadas();
    }
}

