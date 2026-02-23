import { useState } from 'react';
import { api } from '../api';
import { estadoBadge } from '../utils';
import { SPORT_ICONS } from '../App';
import CreatePartidoModal from './CreatePartidoModal';

export default function PartidosList({ partidos, usuarios, deportes, onRefresh, toast }) {
    const [showCreate, setShowCreate] = useState(false);
    const [expanded, setExpanded] = useState(null);
    const [filterEstado, setFilterEstado] = useState('Todos');

    const ESTADOS = ['Todos', 'Necesitamos jugadores', 'Partido armado', 'Confirmado', 'En juego', 'Finalizado', 'Cancelado'];

    const filtered = filterEstado === 'Todos' ? partidos : partidos.filter(p => p.estado === filterEstado);

    const doAction = async (action, id, extra) => {
        try { await action(id, extra); await onRefresh(); toast('ACCIÓN REALIZADA'); }
        catch (e) { toast(e.message, 'error'); }
    };

    return (
        <div>
            <div className="section-header">
                <div>
                    <div className="section-label">Calendario</div>
                    <div className="section-title">Partidos ({filtered.length})</div>
                </div>
                <button className="btn btn-primary" onClick={() => setShowCreate(true)}>+ NUEVO PARTIDO</button>
            </div>

            {/* Filter bar */}
            <div className="filter-bar">
                {ESTADOS.map(e => (
                    <button key={e} className={`btn btn-sm btn-outline${filterEstado === e ? ' active' : ''}`}
                        onClick={() => setFilterEstado(e)}>
                        {e === 'Todos' ? 'TODOS' : e.toUpperCase()}
                    </button>
                ))}
            </div>

            {filtered.length === 0
                ? <div className="empty">— SIN PARTIDOS —</div>
                : <div className="partidos-grid">
                    {filtered.map(p => (
                        <div key={p.id} className="score-card">
                            {/* Header */}
                            <div className="score-card-top">
                                <span style={{ fontFamily: "'Barlow Condensed'", fontWeight: 800, fontSize: '.9rem' }}>
                                    {SPORT_ICONS[p.deporte] || '🏟'} {p.deporte.toUpperCase()} · #{p.id}
                                </span>
                                <span className={`badge ${p.estado === 'En juego' ? 'badge-live' : estadoBadge(p.estado)}`}>
                                    {p.estado === 'En juego' ? '● LIVE' : p.estado.toUpperCase()}
                                </span>
                            </div>

                            {/* Score body */}
                            <div className="score-card-body">
                                <div>
                                    <div className="score-card-sport">📍 {p.ubicacion}</div>
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
                                                <div style={{ display: 'flex', gap: '.35rem', alignItems: 'center' }}>
                                                    <span className={`badge ${j.confirmado ? 'badge-green' : 'badge-yellow'}`}>
                                                        {j.confirmado ? '✓ OK' : 'PENDIENTE'}
                                                    </span>
                                                    {!j.confirmado && p.estado === 'Partido armado' && (
                                                        <button className="btn btn-gold btn-sm"
                                                            onClick={() => {
                                                                const u = usuarios.find(u => u.nombreUsuario === j.nombre);
                                                                if (u) doAction(api.confirmarJugador, p.id, u.id);
                                                            }}>CONFIRMAR</button>
                                                    )}
                                                </div>
                                            </div>
                                        ))
                                    }
                                </div>
                            )}

                            {/* Actions */}
                            <div className="score-card-actions">
                                {p.estado === 'Necesitamos jugadores' && usuarios.length > 0 && (
                                    <select className="form-control" style={{ fontSize: '.78rem', padding: '.3rem .5rem', flex: 1 }}
                                        defaultValue=""
                                        onChange={e => { if (e.target.value) doAction(api.agregarJugador, p.id, e.target.value); e.target.value = ''; }}>
                                        <option value="" disabled>+ AGREGAR JUGADOR</option>
                                        {usuarios.filter(u => !p.jugadores.some(j => j.nombre === u.nombreUsuario))
                                            .map(u => <option key={u.id} value={u.id}>{u.nombreUsuario}</option>)}
                                    </select>
                                )}
                                {p.estado === 'Confirmado' && (
                                    <button className="btn btn-success btn-sm" onClick={() => doAction(api.iniciarPartido, p.id)}>▶ INICIAR</button>
                                )}
                                {p.estado === 'En juego' && (
                                    <button className="btn btn-info btn-sm" onClick={() => doAction(api.finalizarPartido, p.id)}>⏹ FINALIZAR</button>
                                )}
                                {['Necesitamos jugadores', 'Partido armado', 'Confirmado'].includes(p.estado) && (
                                    <button className="btn btn-danger btn-sm" onClick={() => doAction(api.cancelarPartido, p.id)}>✕ CANCELAR</button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            }

            {showCreate && (
                <CreatePartidoModal deportes={deportes}
                    onClose={() => setShowCreate(false)}
                    onCreated={() => { setShowCreate(false); onRefresh(); toast('PARTIDO CREADO'); }}
                    toast={toast} />
            )}
        </div>
    );
}
