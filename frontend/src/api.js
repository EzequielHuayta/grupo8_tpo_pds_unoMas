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
  if (res.status === 204 || res.headers.get('content-length') === '0') return null;
  return res.json();
}

export const api = {
  // Partidos
  getPartidos: () => req('/partidos'),
  getPartido: (id) => req(`/partidos/${id}`),
  crearPartido: (body) => req('/partidos', { method: 'POST', body: JSON.stringify(body) }),
  agregarJugador: (id, uid) => req(`/partidos/${id}/jugadores/${uid}`, { method: 'POST' }),
  confirmarJugador: (id, uid) => req(`/partidos/${id}/confirmar-jugador/${uid}`, { method: 'PUT' }),
  iniciarPartido: (id, creadorId) => req(`/partidos/${id}/iniciar${creadorId ? `?creadorId=${creadorId}` : ''}`, { method: 'PUT' }),
  finalizarPartido: (id, creadorId) => req(`/partidos/${id}/finalizar${creadorId ? `?creadorId=${creadorId}` : ''}`, { method: 'PUT' }),
  cancelarPartido: (id, creadorId) => req(`/partidos/${id}/cancelar${creadorId ? `?creadorId=${creadorId}` : ''}`, { method: 'PUT' }),
  getDeportes: () => req('/partidos/deportes'),
  getBarrios: () => req('/partidos/barrios'),
  buscarPartidos: (usuarioId) => req(`/partidos/buscar?usuarioId=${usuarioId}`),

  // Usuarios
  getUsuarios: () => req('/usuarios'),
  registrarUsuario: (body) => req('/usuarios/registro', { method: 'POST', body: JSON.stringify(body) }),
  login: (body) => req('/usuarios/login', { method: 'POST', body: JSON.stringify(body) }),
  loginSimple: (nombre) => req('/usuarios/login-simple', { method: 'POST', body: JSON.stringify({ nombreUsuario: nombre }) }),
  cambiarNivel: (id, nivel) => req(`/usuarios/${id}/nivel`, { method: 'PUT', body: JSON.stringify({ nivel }) }),
  modificarUsuario: (id, body) => req(`/usuarios/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
  setEstrategiaBusqueda: (id, estrategia) => req(`/usuarios/${id}/estrategia-busqueda`, { method: 'PUT', body: JSON.stringify({ estrategia }) }),
  getNotificaciones: (id) => req(`/usuarios/${id}/notificaciones`),
  leerNotificaciones: (id) => req(`/usuarios/${id}/notificaciones`, { method: 'DELETE' }).catch(() => { }),
};
