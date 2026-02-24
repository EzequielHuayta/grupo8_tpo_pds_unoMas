package org.example.repository;

import org.example.model.Ubicacion;
import org.example.model.Usuario;
import org.example.nivel.Avanzado;
import org.example.nivel.Intermedio;
import org.example.nivel.NivelState;
import org.example.nivel.Principiante;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Persists users in data/usuarios.txt
 * Format per line: id|nombreUsuario|email|contrasena|nivelPeso|barrio
 */
@Repository
public class UsuarioRepositoryImpl implements IUsuarioRepository {

    private static final String FILE_PATH = "data/usuarios.txt";
    private final List<Usuario> usuarios = new ArrayList<>();
    private Long nextId = 1L;

    @PostConstruct
    public void cargar() {
        new File("data").mkdirs();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                // id|nombre|email|contrasena|nivelPeso|barrio
                Long id         = Long.parseLong(p[0]);
                String nombre   = p[1];
                String email    = p[2];
                String contra   = p[3];
                int nivelPeso   = Integer.parseInt(p[4]);
                String barrio   = p.length > 5 ? p[5] : "";

                Usuario u = new Usuario(id, nombre, email, contra);
                u.setUbicacion(new Ubicacion(barrio));
                u.setNivel(nivelDesde(nivelPeso));
                usuarios.add(u);

                if (id >= nextId) nextId = id + 1;
            }
            System.out.println("[UsuarioRepo] Cargados " + usuarios.size() + " usuarios desde " + FILE_PATH);
        } catch (IOException e) {
            System.err.println("[UsuarioRepo] Error al cargar: " + e.getMessage());
        }
    }

    private void persistir() {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Usuario u : usuarios) {
                String barrio = u.getUbicacion() != null ? u.getUbicacion().getBarrio() : "";
                pw.println(u.getIdUsuario() + "|"
                        + u.getNombreUsuario() + "|"
                        + u.getEmail() + "|"
                        + u.getContrasena() + "|"
                        + u.getNivel().getPesoNivel() + "|"
                        + barrio);
            }
        } catch (IOException e) {
            System.err.println("[UsuarioRepo] Error al persistir: " + e.getMessage());
        }
    }

    // ---- IUsuarioRepository ----

    @Override
    public Usuario guardar(Usuario usuario) {
        // update if exists, else add
        usuarios.removeIf(u -> u.getIdUsuario().equals(usuario.getIdUsuario()));
        usuarios.add(usuario);
        persistir();
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarios.stream().filter(u -> u.getIdUsuario().equals(id)).findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    @Override
    public Optional<Usuario> buscarPorNombre(String nombreUsuario) {
        return usuarios.stream()
                .filter(u -> u.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst();
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    @Override
    public Long generarId() {
        return nextId++;
    }

    // ---- helpers ----

    private NivelState nivelDesde(int peso) {
        switch (peso) {
            case 2:  return new Intermedio();
            case 3:  return new Avanzado();
            default: return new Principiante();
        }
    }
}
