package org.example;

import org.example.controller.PartidoController;
import org.example.controller.UsuarioController;
import org.example.model.*;
import org.example.nivel.*;
import org.example.notification.*;
import org.example.repository.*;
import org.example.service.PartidoService;
import org.example.service.UsuarioService;
import org.example.strategy.EmparejadorNivelStrategy;
import org.example.view.PartidoView;
import org.example.view.UsuarioView;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // --- Inicializar capas MVC ---
        IUsuarioRepository usuarioRepository = new UsuarioRepositoryImpl();
        IPartidoRepository partidoRepository = new PartidoRepositoryImpl();
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);
        PartidoService partidoService = new PartidoService(partidoRepository);
        UsuarioController usuarioController = new UsuarioController(usuarioService);
        PartidoController partidoController = new PartidoController(partidoService);
        PartidoView partidoView = new PartidoView();
        UsuarioView usuarioView = new UsuarioView();

        // --- Deportes ---
        Deporte futbol = new Deporte(1L, "Fútbol");
        Deporte basquet = new Deporte(2L, "Básquet");

        // --- RF1: Registro de usuarios (con contraseña) ---
        System.out.println("========== REGISTRO DE USUARIOS ==========");
        Ubicacion buenosAires = new Ubicacion(-34.6037, -58.3816, "Buenos Aires");
        Usuario juan = usuarioController.registrarUsuario("Juan", "juan@mail.com", "pass123", buenosAires);
        Usuario maria = usuarioController.registrarUsuario("Maria", "maria@mail.com", "pass456", buenosAires);
        Usuario pedro = usuarioController.registrarUsuario("Pedro", "pedro@mail.com", "pass789", buenosAires);

        // Configurar deportes favoritos y niveles
        usuarioController.setDeporteFavorito(juan, futbol);
        usuarioController.setNivel(juan, new Avanzado());
        usuarioController.setDeporteFavorito(maria, futbol);
        usuarioController.setNivel(maria, new Intermedio());
        usuarioController.setDeporteFavorito(pedro, basquet);
        usuarioController.setNivel(pedro, new Principiante());

        // Configurar strategy de notificación (Adapter + Strategy)
        IAdapterEmail adapterEmail = new AdapterJavaEmail();
        juan.cambiarEstrategiaNotificacion(new EmailNotificacionStrategy(adapterEmail));
        maria.cambiarEstrategiaNotificacion(new FirebaseNotificacionStrategy());

        // Configurar strategy de emparejamiento
        juan.cambiarEstrategiaEmparejamiento(new EmparejadorNivelStrategy());

        usuarioView.mostrarUsuario(juan);

        // --- RF3: Creación de partido ---
        System.out.println("\n========== CREAR PARTIDO ==========");
        Partido partido = partidoController.crearPartido(
                futbol, 3, 90,
                buenosAires,
                LocalDateTime.now().plusDays(1),
                new Intermedio(), new Avanzado()
        );
        partidoView.mostrarPartido(partido);

        // --- RF2: Búsqueda de partidos (Strategy) ---
        System.out.println("\n========== BÚSQUEDA DE PARTIDOS ==========");
        System.out.println("Partidos para Juan (Avanzado, strategy por nivel):");
        partidoView.mostrarListaPartidos(partidoController.buscarPartidos(juan));
        System.out.println("Partidos para Pedro (Principiante, strategy por nivel):");
        pedro.cambiarEstrategiaEmparejamiento(new EmparejadorNivelStrategy());
        partidoView.mostrarListaPartidos(partidoController.buscarPartidos(pedro));

        // --- RF4: Estados del partido (State) ---
        System.out.println("\n========== FLUJO DE ESTADOS ==========");

        // Agregar jugadores → transición automática a "Armado"
        partidoController.agregarJugador(partido, juan);
        partidoController.agregarJugador(partido, maria);
        partidoController.agregarJugador(partido, pedro);
        partidoView.mostrarPartido(partido);

        // Confirmar jugadores → transición a "Confirmado"
        System.out.println("\n--- Confirmando jugadores ---");
        partidoController.confirmarJugador(partido, juan);
        partidoController.confirmarJugador(partido, maria);
        partidoController.confirmarJugador(partido, pedro);
        partidoView.mostrarPartido(partido);

        // Iniciar → "En juego"
        System.out.println("\n--- Iniciando partido ---");
        partidoController.iniciarPartido(partido);
        partidoView.mostrarPartido(partido);

        // Finalizar → "Finalizado"
        System.out.println("\n--- Finalizando partido ---");
        partidoController.finalizarPartido(partido);
        partidoView.mostrarPartido(partido);

        // --- Demostrar transición inválida ---
        System.out.println("\n========== TRANSICIÓN INVÁLIDA ==========");
        try {
            partidoController.iniciarPartido(partido);
        } catch (IllegalStateException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

        // --- Demostrar cancelación ---
        System.out.println("\n========== CANCELAR PARTIDO ==========");
        Partido partido2 = partidoController.crearPartido(
                basquet, 2, 60, buenosAires, LocalDateTime.now().plusDays(2));
        partidoController.cancelarPartido(partido2);
        partidoView.mostrarPartido(partido2);

        System.out.println("\n========== FIN DEMO ==========");
    }
}