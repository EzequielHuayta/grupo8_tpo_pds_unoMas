import { estadoBadge } from '../utils';
import { SPORT_ICONS } from '../App';

export default function Dashboard({ partidos, usuarios, onNavigate }) {
    const activos = partidos.filter(p => ['Necesitamos jugadores', 'Partido armado', 'Confirmado'].includes(p.estado)).length;
    const enJuego = partidos.filter(p => p.estado === 'En juego').length;
    const finalizados = partidos.filter(p => p.estado === 'Finalizado').length;

    const byEstado = partidos.reduce((acc, p) => { acc[p.estado] = (acc[p.estado] || 0) + 1; return acc; }, {});
    const recientes = [...partidos].reverse().slice(0, 6);

    return (
        <div>
            {/* Stats strip */}
            <div className="stats-strip">
                {[
                    { value: partidos.length, label: 'Partidos' },
                    { value: activos, label: 'Activos' },
                    { value: enJuego, label: 'En juego', live: enJuego > 0 },
                    { value: usuarios.length, label: 'Jugadores' },
                    { value: finalizados, label: 'Finalizados' },
                ].map(s => (
                    <div className="stat-box" key={s.label}>
                        <div className="value" style={s.live ? { color: '#cc0000' } : {}}>{s.value}</div>
                        <div className="label">
                            {s.live && <span className="badge badge-live" style={{ marginRight: '.35rem' }}>LIVE</span>}
                            {s.label}
                        </div>
                    </div>
                ))}
            </div>

            <div className="dash-grid">
                {/* Scoreboard */}
                <div>
                    <div className="section-header">
                        <div>
                            <div className="section-label">Actividad reciente</div>
                            <div className="section-title">Últimos partidos</div>
                        </div>
                        <button className="btn btn-outline btn-sm" onClick={() => onNavigate('partidos')}>VER TODOS →</button>
                    </div>

                    {recientes.length === 0
                        ? <div className="empty">— SIN PARTIDOS AÚN —</div>
                        : <div style={{ display: 'flex', flexDirection: 'column', gap: '.6rem' }}>
                            {recientes.map(p => (
                                <div key={p.id} className="score-card">
                                    <div className="score-card-top">
                                        <span>{SPORT_ICONS[p.deporte] || '🏟'} {p.deporte}</span>
                                        <span className={`badge ${estadoBadge(p.estado)}`}>{p.estado}</span>
                                    </div>
                                    <div className="score-card-body">
                                        <div>
                                            <div className="score-card-sport">📍 {p.ubicacion}</div>
                                            <div style={{ fontSize: '.77rem', color: 'var(--muted)', marginTop: '.25rem' }}>
                                                {new Date(p.horario).toLocaleDateString('es-AR', { weekday: 'short', day: 'numeric', month: 'short' })}
                                                {'  ·  '}
                                                {new Date(p.horario).toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit' })}
                                            </div>
                                        </div>
                                        <div className="score-card-players">
                                            {p.jugadoresActuales}<span>/{p.cantidadJugadores}</span>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    }
                </div>

                {/* Sidebar standings */}
                <div>
                    <div className="section-header">
                        <div>
                            <div className="section-label">Estadísticas</div>
                            <div className="section-title">Por estado</div>
                        </div>
                    </div>
                    <div style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 'var(--radius)' }}>
                        {Object.entries(byEstado).length === 0
                            ? <div className="empty">— SIN DATOS —</div>
                            : Object.entries(byEstado).map(([est, n]) => (
                                <div className="stand-row" key={est}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '.6rem' }}>
                                        <span className={`badge ${estadoBadge(est)}`}>{est}</span>
                                    </div>
                                    <div style={{ fontFamily: "'Barlow Condensed'", fontWeight: 900, fontSize: '1.2rem' }}>{n}</div>
                                </div>
                            ))
                        }
                    </div>

                    <div style={{ marginTop: '1.5rem' }}>
                        <div className="section-header">
                            <div>
                                <div className="section-label">Registro</div>
                                <div className="section-title">Jugadores</div>
                            </div>
                            <button className="btn btn-outline btn-sm" onClick={() => onNavigate('usuarios')}>VER TODOS →</button>
                        </div>
                        <div style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: 'var(--radius)' }}>
                            {usuarios.slice(0, 6).map((u, i) => (
                                <div className="stand-row" key={u.id}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '.6rem' }}>
                                        <span className="number">{i + 1}</span>
                                        <div>
                                            <div style={{ fontWeight: 600, fontSize: '.85rem' }}>{u.nombreUsuario}</div>
                                            <div style={{ fontSize: '.72rem', color: 'var(--muted)' }}>{u.nivel}</div>
                                        </div>
                                    </div>
                                    <div style={{ fontSize: '.77rem', color: 'var(--muted)' }}>{u.ciudad || '—'}</div>
                                </div>
                            ))}
                            {usuarios.length === 0 && <div className="empty">— SIN JUGADORES —</div>}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
