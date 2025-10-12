package co.edu.uniquindio.Application.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "respuestas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 20, nullable = false)
    private Long id;

    private String respuesta;

    private LocalDateTime fecha;

    @OneToOne
    @JoinColumn(name = "comentario_id")
    private Comentario comentario;

    @ManyToOne
    @JoinColumn(name= "anfitrion_id")
    private Usuario anfitrion;
}

