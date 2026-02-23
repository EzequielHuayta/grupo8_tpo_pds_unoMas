const BASE = 'http://localhost:8080/api';

async function req(path, options = {}) {
  const res = await fetch(`${BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    throw new Error(err.error || 'Error en la solicitud');
  }
  return res.json();
}

export const api = {
  // Partidos
  getPartidos:       ()           => req('/partidos'),
  getPartido:        (id)         => req(`/partidos/${id}`),
  crearPartido:      (body)       => req('/partidos', { method: 'POST', body: JSON.stringify(body) }),
  agregarJugador:    (id, uid)    => req(`/partidos/${id}/jugadores/${uid}`, { method: 'POST' }),
  confirmarJugador:  (id, uid)    => req(`/partidos/${id}/confirmar-jugador/${uid}`, { method: 'PUT' }),
  iniciarPartido:    (id)         => req(`/partidos/${id}/iniciar`, { method: 'PUT' }),
  finalizarPartido:  (id)         => req(`/partidos/${id}/finalizar`, { method: 'PUT' }),
  cancelarPartido:   (id)         => req(`/partidos/${id}/cancelar`, { method: 'PUT' }),
  getDeportes:       ()           => req('/partidos/deportes'),

  // Usuarios
  getUsuarios:       ()           => req('/usuarios'),
  registrarUsuario:  (body)       => req('/usuarios/registro', { method: 'POST', body: JSON.stringify(body) }),
  login:             (body)       => req('/usuarios/login', { method: 'POST', body: JSON.stringify(body) }),
  cambiarNivel:      (id, nivel)  => req(`/usuarios/${id}/nivel`, { method: 'PUT', body: JSON.stringify({ nivel }) }),
};
