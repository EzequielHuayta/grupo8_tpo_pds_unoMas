package org.example.repository;

import org.example.model.*;
import org.example.nivel.Avanzado;
import org.example.nivel.Intermedio;
import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.example.state.*;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Persists matches in data/partidos.txt
 *
 * Format per line:
 *   id|deporteId|deporteNombre|cantJugadores|duracion|barrio|horario|estadoNombre|nivelMinPeso|nivelMaxPeso|creadorId|jugadores
 *
 * jugadores field: jId1:uId1:confirmado;jId2:uId2:confirmado   (empty string if none)
 */
@Repository
public class PartidoRepositoryImpl implements IPartidoRepository {

    private static final String FILE_PATH = "data/partidos.txt";

    private final List<Partido> partidos = new ArrayList<>();
    private Long nextId = 1L;
    private Long nextJugadorId = 1L;

    /** Injected so we can reconstruct Jugador → Usuario references on load */
    private final IUsuarioRepository usuarioRepository;

    public PartidoRepositoryImpl(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostConstruct
    public void cargar() {
        new File("data").mkdirs();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);

                Long id             = Long.parseLong(p[0]);
                Long deporteId      = Long.parseLong(p[1]);
                String deporteNombre= p[2];
                int cantJugadores   = Integer.parseInt(p[3]);
                int duracion        = Integer.parseInt(p[4]);
                String barrio       = p[5];
                LocalDateTime horario = LocalDateTime.parse(p[6]);
                String estadoNombre = p[7];
                int nivelMinPeso    = Integer.parseInt(p[8]);
                int nivelMaxPeso    = Integer.parseInt(p[9]);
                Long creadorId      = (p.length > 10 && !p[10].trim().isEmpty()) ? Long.parseLong(p[10].trim()) : null;
                String jugadoresData= p.length > 11 ? p[11] : "";

                Deporte deporte = new Deporte(deporteId, deporteNombre);
                Ubicacion ubicacion = new Ubicacion(barrio);

                Partido partido = new Partido(id, deporte, cantJugadores, duracion, ubicacion, horario);
                partido.setEstado(estadoDesde(estadoNombre));
                partido.setNivelMinimo(nivelDesde(nivelMinPeso));
                partido.setNivelMaximo(nivelDesde(nivelMaxPeso));
                partido.setCreadorId(creadorId);

                // Reconstruct jugadores
                if (!jugadoresData.isEmpty()) {
                    for (String entry : jugadoresData.split(";")) {
                        String[] jParts = entry.split(":");
                        Long jId    = Long.parseLong(jParts[0]);
                        Long uId    = Long.parseLong(jParts[1]);
                        boolean conf= Boolean.parseBoolean(jParts[2]);

                        usuarioRepository.buscarPorId(uId).ifPresent(usuario -> {
                            Jugador jugador = new Jugador(jId, usuario);
                            if (conf) jugador.confirmar();
                            partido.getJugadores().add(jugador);
                        });

                        if (jId >= nextJugadorId) nextJugadorId = jId + 1;
                    }
                }

                partidos.add(partido);
                if (id >= nextId) nextId = id + 1;
            }
            System.out.println("[PartidoRepo] Cargados " + partidos.size() + " partidos desde " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("[PartidoRepo] Error al cargar: " + e.getMessage());
        }
    }

    private void persistir() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FILE_PATH), StandardCharsets.UTF_8))) {
            for (Partido p : partidos) {
                // build jugadores string
                StringBuilder jBuilder = new StringBuilder();
                for (Jugador j : p.getJugadores()) {
                    if (jBuilder.length() > 0) jBuilder.append(";");
                    jBuilder.append(j.getIdParticipante())
                            .append(":").append(j.getJugador().getIdUsuario())
                            .append(":").append(j.isConfirmacion());
                }

                pw.println(p.getIdPartido() + "|"
                        + p.getDeporte().getIdDeporte() + "|"
                        + p.getDeporte().getNombre() + "|"
                        + p.getCantidadJugadores() + "|"
                        + p.getDuracionMinutos() + "|"
                        + p.getUbicacion().getBarrio() + "|"
                        + p.getHorario().toString() + "|"
                        + p.getEstado().getNombre() + "|"
                        + p.getNivelMinimo().getPesoNivel() + "|"
                        + p.getNivelMaximo().getPesoNivel() + "|"
                        + (p.getCreadorId() != null ? p.getCreadorId() : "") + "|"
                        + jBuilder.toString());
            }
        } catch (IOException e) {
            System.err.println("[PartidoRepo] Error al persistir: " + e.getMessage());
        }
    }

    // ---- IPartidoRepository ----

    @Override
    public Partido guardar(Partido partido) {
        partidos.removeIf(p -> p.getIdPartido().equals(partido.getIdPartido()));
        partidos.add(partido);
        persistir();
        return partido;
    }

    @Override
    public Optional<Partido> buscarPorId(Long id) {
        return partidos.stream().filter(p -> p.getIdPartido().equals(id)).findFirst();
    }

    @Override
    public List<Partido> listarTodos() {
        return new ArrayList<>(partidos);
    }

    @Override
    public Long generarId() {
        return nextId++;
    }

    @Override
    public Long generarIdJugador() {
        return nextJugadorId++;
    }

    // ---- helpers ----

    private IPartidoState estadoDesde(String nombre) {
        switch (nombre) {
            case "Partido armado":       return new ArmadoState();
            case "Confirmado":           return new ConfirmadoState();
            case "En juego":             return new EnJuegoState();
            case "Finalizado":           return new FinalizadoState();
            case "Cancelado":            return new CanceladoState();
            default:                     return new NecesitamosJugadoresState();
        }
    }

    private NivelState nivelDesde(int peso) {
        switch (peso) {
            case 2:  return new Intermedio();
            case 3:  return new Avanzado();
            default: return new Principiante();
        }
    }
}
