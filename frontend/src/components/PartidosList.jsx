import { useState } from 'react';
import { api } from '../api';
import { estadoBadge } from '../utils';
import { SPORT_ICONS } from '../App';
import CreatePartidoModal from './CreatePartidoModal';

export default function PartidosList({ partidos, usuarios, deportes, barrios, currentUser, onLoginRequired, onRefresh, toast }) {
    const [showCreate, setShowCreate] = useState(false);
    const [expanded, setExpanded] = useState(null);
    const [filterEstado, setFilterEstado] = useState('Todos');

    const ESTADOS = ['Todos', 'Necesitamos jugadores', 'Partido armado', 'Confirmado', 'En juego', 'Finalizado', 'Cancelado'];

    // Solo los partidos del usuario logueado (creador o jugador inscripto)
    const misPartidos = !currentUser ? [] : partidos.filter(p =>
        (p.creadorId && Number(p.creadorId) === Number(currentUser.id)) ||
        (p.jugadores && p.jugadores.some(j => j.nombre === currentUser.nombreUsuario))
    );
    const filtered = filterEstado === 'Todos' ? misPartidos : misPartidos.filter(p => p.estado === filterEstado);

    // Generic action gated on login
    const doAction = async (fn) => {
        if (!currentUser) { onLoginRequired(); return; }
        try { await fn(); await onRefresh(); toast('ACCIÓN REALIZADA'); }
        catch (e) { toast(e.message, 'error'); }
    };

    // Is the logged-in user the creator of this match?
    const isOwner = (p) => currentUser && p.creadorId && Number(p.creadorId) === Number(currentUser.id);

    return (
        <div>
            <div className="section-header">
                <div>
                    <div className="section-label">Mi Calendario</div>
                    <div className="section-title">Mis Partidos ({filtered.length})</div>
                </div>
                <button className="btn btn-primary" onClick={() => {
                    if (!currentUser) { onLoginRequired(); return; }
                    setShowCreate(true);
                }}>+ NUEVO PARTIDO</button>
            </div>

            {/* Cartel de sesión requerida */}
            {!currentUser && (
                <div style={{
                    border: '1.5px solid var(--gold)',
                    borderRadius: 8,
                    padding: '1rem 1.5rem',
                    color: 'var(--gold)',
                    fontFamily: "'Barlow Condensed'",
                    fontWeight: 700,
                    fontSize: '1rem',
                    letterSpacing: '.06em',
                    textAlign: 'center',
                    marginTop: '1rem',
                    background: 'rgba(212,175,55,.07)',
                }}>
                    ⚠ INICIÁ SESIÓN PARA VER TUS PARTIDOS
                </div>
            )}

            {/* Filter bar */}
            <div className="filter-bar">
                {ESTADOS.map(e => (
                    <button key={e} className={`btn btn-sm btn-outline${filterEstado === e ? ' active' : ''}`}
                        onClick={() => setFilterEstado(e)}>
                        {e === 'Todos' ? 'TODOS' : e.toUpperCase()}
                    </button>
                ))}
            </div>

            {currentUser && filtered.length === 0
                ? <div className="empty">— AÚN NO TENÉS PARTIDOS —</div>
                : currentUser && <div className="partidos-grid">
                    {filtered.map(p => (
                        <div key={p.id} className="score-card">
                            {/* Header */}
                            <div className="score-card-top">
                                <span style={{ fontFamily: "'Barlow Condensed'", fontWeight: 800, fontSize: '.9rem' }}>
                                    {SPORT_ICONS[p.deporte] || '🏟'} {p.deporte.toUpperCase()} · #{p.id}
                                </span>
                                <div style={{ display: 'flex', gap: '.35rem', alignItems: 'center' }}>
                                    {isOwner(p) && <span style={{ fontSize: '.65rem', color: 'var(--gold)', fontFamily: "'Barlow Condensed'", fontWeight: 800, textTransform: 'uppercase', letterSpacing: '.05em' }}>👑 TUYO</span>}
                                    <span className={`badge ${p.estado === 'En juego' ? 'badge-live' : estadoBadge(p.estado)}`}>
                                        {p.estado === 'En juego' ? '● LIVE' : p.estado.toUpperCase()}
                                    </span>
                                </div>
                            </div>

                            {/* Score body */}
                            <div className="score-card-body">
                                <div>
                                    <div className="score-card-sport">📍 {p.barrio}</div>
                                    <div style={{ fontSize: '.77rem', color: 'var(--muted)', marginTop: '.2rem' }}>
                                        ⏱ {p.duracionMinutos} min &nbsp;·&nbsp;
                                        🗓 {new Date(p.horario).toLocaleDateString('es-AR')}
                                    </div>
                                    <div style={{ fontSize: '.77rem', color: 'var(--muted)' }}>
                                        📊 {p.nivelMinimo} – {p.nivelMaximo}
                                    </div>
                                </div>
                                <div style={{ textAlign: 'right' }}>
                                    <div className="score-card-players">{p.jugadoresActuales}<span>/{p.cantidadJugadores}</span></div>
                                    <div style={{ fontSize: '.7rem', color: 'var(--muted)', textTransform: 'uppercase' }}>jugadores</div>
                                </div>
                            </div>

                            {/* Players toggle */}
                            <button className="btn btn-outline btn-sm"
                                onClick={() => setExpanded(expanded === p.id ? null : p.id)}>
                                {expanded === p.id ? '▲ OCULTAR' : `▼ ROSTER (${p.jugadoresActuales})`}
                            </button>

                            {expanded === p.id && (
                                <div className="jugadores-list">
                                    {p.jugadores.length === 0
                                        ? <div style={{ fontSize: '.78rem', color: 'var(--muted)', padding: '.4rem', textAlign: 'center' }}>SIN JUGADORES</div>
                                        : p.jugadores.map(j => (
                                            <div key={j.id} className="jugador-row">
                                                <span style={{ fontWeight: 600 }}>{j.nombre}</span>
                                                <span className={`badge ${j.confirmado ? 'badge-green' : 'badge-yellow'}`}>
                                                    {j.confirmado ? '✓ OK' : 'PENDIENTE'}
                                                </span>
                                            </div>
                                        ))
                                    }
                                </div>
                            )}

                            {/* Actions */}
                            <div className="score-card-actions">
                                {/* UNIRME: estado Necesitamos jugadores */}
                                {p.estado === 'Necesitamos jugadores' && (() => {
                                    if (!currentUser) return (
                                        <button className="btn btn-outline btn-sm" style={{ flex: 1 }} onClick={onLoginRequired}>
                                            + INICIA SESIÓN PARA UNIRTE
                                        </button>
                                    );
                                    const yaInscripto = p.jugadores.some(j => j.nombre === currentUser.nombreUsuario);
                                    if (yaInscripto) return (
                                        <span style={{ fontSize: '.75rem', color: 'var(--muted)', fontFamily: "'Barlow Condensed'", textTransform: 'uppercase' }}>
                                            ✓ YA ESTÁS INSCRIPTO
                                        </span>
                                    );
                                    if (isOwner(p)) return null;
                                    return (
                                        <button className="btn btn-success btn-sm" style={{ flex: 1 }}
                                            onClick={() => doAction(() => api.agregarJugador(p.id, currentUser.id))}>
                                            ✚ UNIRME
                                        </button>
                                    );
                                })()}

                                {/* CONFIRMAR MI ASISTENCIA: estado Partido armado, jugador logueado inscripto y no confirmado */}
                                {p.estado === 'Partido armado' && currentUser && (() => {
                                    const miJugador = p.jugadores.find(j => j.nombre === currentUser.nombreUsuario);
                                    if (!miJugador || miJugador.confirmado) return null;
                                    return (
                                        <button className="btn btn-gold btn-sm" style={{ flex: 1 }}
                                            onClick={() => doAction(() => api.confirmarJugador(p.id, currentUser.id))}>
                                            ✓ CONFIRMAR MI ASISTENCIA
                                        </button>
                                    );
                                })()}

                                {/* Transiciones de estado: solo owner */}
                                {isOwner(p) && p.estado === 'Confirmado' && (
                                    <button className="btn btn-success btn-sm"
                                        onClick={() => doAction(() => api.iniciarPartido(p.id, currentUser.id))}>▶ INICIAR</button>
                                )}
                                {isOwner(p) && p.estado === 'En juego' && (
                                    <button className="btn btn-info btn-sm"
                                        onClick={() => doAction(() => api.finalizarPartido(p.id, currentUser.id))}>⏹ FINALIZAR</button>
                                )}
                                {isOwner(p) && ['Necesitamos jugadores', 'Partido armado', 'Confirmado'].includes(p.estado) && (
                                    <button className="btn btn-danger btn-sm"
                                        onClick={() => doAction(() => api.cancelarPartido(p.id, currentUser.id))}>✕ CANCELAR</button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            }

            {showCreate && (
                <CreatePartidoModal deportes={deportes} barrios={barrios}
                    currentUser={currentUser}
                    onClose={() => setShowCreate(false)}
                    onCreated={() => { setShowCreate(false); onRefresh(); toast('PARTIDO CREADO'); }}
                    toast={toast} />
            )}
        </div>
    );
}
