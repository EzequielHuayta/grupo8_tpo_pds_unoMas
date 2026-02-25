import { estadoBadge } from '../utils';
import { SPORT_ICONS } from '../App';

export default function Dashboard({ partidos, usuarios, onNavigate }) {
    const activos = partidos.filter(p => ['Necesitamos jugadores', 'Partido armado', 'Confirmado'].includes(p.estado)).length;
    const enJuego = partidos.filter(p => p.estado === 'En juego').length;
    const finalizados = partidos.filter(p => p.estado === 'Finalizado').length;

    const byEstado = partidos.reduce((acc, p) => { acc[p.estado] = (acc[p.estado] || 0) + 1; return acc; }, {});
    const concluidos = [...partidos]
        .filter(p => p.estado === 'Finalizado' || p.estado === 'Cancelado')
        .reverse()
        .slice(0, 6);

    // Top 10 jugadores por partidos finalizados (viene directo del backend)
    const usuariosConPartidos = [...usuarios]
        .sort((a, b) => (b.cantidadPartidosCompletados || 0) - (a.cantidadPartidosCompletados || 0))
        .slice(0, 10);

    return (
        <div>
            {/* Stats strip */}
            <div className="stats-strip">
                {[
                    { value: partidos.length, label: 'Partidos' },
                    { value: activos, label: 'Activos' },
                    { value: enJuego, label: 'En juego' },
                    { value: usuarios.length, label: 'Jugadores' },
                    { value: finalizados, label: 'Finalizados' },
                ].map(s => (
                    <div className="stat-box" key={s.label}>
                        <div className="value">{s.value}</div>
                        <div className="label">{s.label}</div>
                    </div>
                ))}
            </div>

            <div className="dash-grid">
                {/* Scoreboard */}
                <div>
                    <div className="section-header">
                        <div>
                            <div className="section-label">Actividad reciente</div>
                            <div className="section-title">Últimos partidos concluidos</div>
                        </div>
                        <button className="btn btn-outline btn-sm" onClick={() => onNavigate('partidos')}>VER TODOS →</button>
                    </div>

                    {concluidos.length === 0
                        ? <div className="empty">— AÚN NO HAY PARTIDOS CONCLUIDOS —</div>
                        : <div style={{ display: 'flex', flexDirection: 'column', gap: '.75rem' }}>
                            {concluidos.map(p => (
                                <div key={p.id} className="score-card">
                                    {/* Header */}
                                    <div className="score-card-top">
                                        <span style={{ fontFamily: "'Barlow Condensed'", fontWeight: 800, fontSize: '.9rem' }}>
                                            {SPORT_ICONS[p.deporte] || '🏟'} {p.deporte.toUpperCase()} · #{p.id}
                                        </span>
                                        <span className={`badge ${estadoBadge(p.estado)}`}>{p.estado.toUpperCase()}</span>
                                    </div>

                                    {/* Body */}
                                    <div className="score-card-body">
                                        <div>
                                            <div className="score-card-sport">📍 {p.barrio || p.ubicacion}</div>
                                            <div style={{ fontSize: '.77rem', color: 'var(--muted)', marginTop: '.2rem' }}>
                                                ⏱ {p.duracionMinutos} min &nbsp;·&nbsp;
                                                🗓 {new Date(p.horario).toLocaleDateString('es-AR')} {new Date(p.horario).toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit' })}
                                            </div>
                                            <div style={{ fontSize: '.77rem', color: 'var(--muted)' }}>
                                                📊 {p.nivelMinimo} – {p.nivelMaximo}
                                            </div>
                                        </div>
                                        <div style={{ textAlign: 'right' }}>
                                            <div className="score-card-players">
                                                {p.jugadoresActuales}<span>/{p.cantidadJugadores}</span>
                                            </div>
                                            <div style={{ fontSize: '.7rem', color: 'var(--muted)', textTransform: 'uppercase' }}>jugadores</div>
                                        </div>
                                    </div>

                                    {/* Roster inline */}
                                    {p.jugadores && p.jugadores.length > 0 && (
                                        <div style={{ borderTop: '1px solid var(--border)', paddingTop: '.5rem', marginTop: '.1rem' }}>
                                            <div style={{ fontSize: '.68rem', fontWeight: 800, textTransform: 'uppercase', letterSpacing: '.08em', color: 'var(--muted)', marginBottom: '.35rem' }}>
                                                ROSTER ({p.jugadores.length})
                                            </div>
                                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '.3rem' }}>
                                                {p.jugadores.map(j => (
                                                    <span key={j.id} style={{
                                                        fontSize: '.72rem', fontWeight: 600,
                                                        background: 'var(--bg)', borderRadius: 3,
                                                        padding: '.15rem .45rem',
                                                        border: '1px solid var(--border)',
                                                        color: 'var(--muted)',
                                                    }}>
                                                        {j.nombre}
                                                    </span>
                                                ))}
                                            </div>
                                        </div>
                                    )}
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
                                        {est === 'En juego'
                                            ? <span className="badge badge-live">● LIVE</span>
                                            : <span className={`badge ${estadoBadge(est)}`}>{est}</span>
                                        }
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
                            {usuariosConPartidos.map((u, i) => (
                                <div className="stand-row" key={u.id}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '.6rem' }}>
                                        <span className="number">{i + 1}</span>
                                        <div>
                                            <div style={{ fontWeight: 600, fontSize: '.85rem' }}>{u.nombreUsuario}</div>
                                            <div style={{ fontSize: '.72rem', color: 'var(--muted)' }}>{u.nivel}</div>
                                        </div>
                                    </div>
                                    <div style={{ fontFamily: "'Barlow Condensed'", fontWeight: 800, fontSize: '1rem', color: (u.cantidadPartidosCompletados || 0) > 0 ? 'var(--text)' : 'var(--muted)' }}>
                                        {u.cantidadPartidosCompletados || 0}
                                    </div>
                                </div>
                            ))}
                            {usuariosConPartidos.length === 0 && <div className="empty">— SIN JUGADORES —</div>}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
