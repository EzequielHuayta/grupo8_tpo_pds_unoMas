import { useState } from 'react';
import { api } from '../api';
import { estadoBadge } from '../utils';
import { SPORT_ICONS } from '../App';

const ESTRATEGIAS = [
    {
        key: 'NIVEL',
        label: 'Por Nivel',
        icon: '📊',
        descripcion: '¡Encontrá partidos donde tu nivel encaja con el requerido!',
    },
    {
        key: 'UBICACION',
        label: 'Por Ubicación',
        icon: '📍',
        descripcion: '¡Encontrá partidos más cercanos a tu barrio!',
    },
    {
        key: 'HISTORIAL',
        label: 'Por Historial',
        icon: '🏆',
        descripcion: '¡Encontrá partidos con jugadores de participación similar a la tuya!',
    },
];

export default function BuscarPartidos({ currentUser, onLoginRequired, onRefresh, onNavigate, toast }) {
    const [selected, setSelected] = useState(null); // null = ninguna seleccionada
    const [resultados, setResultados] = useState(null);
    const [loading, setLoading] = useState(false);
    const [expanded, setExpanded] = useState(null);
    const [buscado, setBuscado] = useState(false);

    const estrategia = selected !== null ? ESTRATEGIAS[selected] : null;

    const handleBuscar = async () => {
        if (!currentUser) { onLoginRequired(); return; }
        if (selected === null) return;
        setLoading(true);
        setBuscado(true);
        try {
            // 1. Guarda la estrategia elegida en el usuario
            await api.setEstrategiaBusqueda(currentUser.id, estrategia.key);
            // 2. Busca usando la estrategia ya seteada en el usuario
            const data = await api.buscarPartidos(currentUser.id);
            setResultados(data);
        } catch (e) {
            toast(e.message, 'error');
            setResultados([]);
        } finally {
            setLoading(false);
        }
    };

    const handleUnirse = async (partidoId) => {
        if (!currentUser) { onLoginRequired(); return; }
        try {
            await api.agregarJugador(partidoId, currentUser.id);
            await onRefresh();
            toast('¡TE UNISTE AL PARTIDO!');
            onNavigate('partidos');
        } catch (e) {
            toast(e.message, 'error');
        }
    };

    const handleSelect = (idx) => {
        setSelected(idx);
        setResultados(null);
        setBuscado(false);
    };

    return (
        <div>
            <div className="section-header">
                <div>
                    <div className="section-label">Emparejamiento</div>
                    <div className="section-title">Buscar Partido</div>
                </div>
            </div>

            {/* Aviso sin sesión */}
            {!currentUser && (
                <div style={{
                    background: 'rgba(255,200,0,.08)', border: '1px solid var(--gold)',
                    borderRadius: 6, padding: '.7rem 1rem', marginBottom: '1.25rem',
                    fontSize: '.83rem', color: 'var(--gold)',
                    fontFamily: "'Barlow Condensed'", fontWeight: 700, letterSpacing: '.05em',
                }}>
                    ⚠ INICIÁ SESIÓN PARA BUSCAR PARTIDOS
                </div>
            )}

            {/* Tarjetas de estrategia */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '.75rem', maxWidth: 560, margin: '0 auto' }}>
                {ESTRATEGIAS.map((e, i) => {
                    const isActive = selected === i;
                    return (
                        <div
                            key={e.key}
                            onClick={() => handleSelect(i)}
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: '1rem',
                                background: isActive ? 'rgba(204,0,0,.08)' : 'var(--surface)',
                                border: `1px solid ${isActive ? 'var(--red)' : 'var(--border)'}`,
                                borderRadius: 8,
                                padding: '1rem 1.25rem',
                                cursor: 'pointer',
                                transition: 'background .15s, border-color .15s',
                                userSelect: 'none',
                            }}
                        >
                            {/* Emoji */}
                            <div style={{ fontSize: '1.8rem', flexShrink: 0 }}>{e.icon}</div>

                            {/* Textos */}
                            <div style={{ flex: 1 }}>
                                <div style={{
                                    fontFamily: "'Barlow Condensed'", fontWeight: 800,
                                    fontSize: '1.05rem', textTransform: 'uppercase',
                                    letterSpacing: '.06em',
                                    color: isActive ? 'var(--text)' : 'var(--muted)',
                                    transition: 'color .15s',
                                }}>
                                    {e.label}
                                </div>
                                <div style={{ fontSize: '.8rem', color: 'var(--muted)', marginTop: '.2rem', lineHeight: 1.4 }}>
                                    {e.descripcion}
                                </div>
                            </div>

                            {/* Radio circle */}
                            <div style={{
                                flexShrink: 0, width: 22, height: 22,
                                borderRadius: '50%',
                                border: `2px solid ${isActive ? 'var(--red)' : 'var(--border)'}`,
                                display: 'flex', alignItems: 'center', justifyContent: 'center',
                                transition: 'border-color .15s',
                            }}>
                                {isActive && (
                                    <div style={{
                                        width: 12, height: 12,
                                        borderRadius: '50%',
                                        background: 'var(--red)',
                                    }} />
                                )}
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* Botón buscar — solo aparece cuando hay algo seleccionado */}
            {selected !== null && (
                <div style={{ maxWidth: 560, margin: '1.25rem auto 0' }}>
                    <button
                        className="btn btn-primary"
                        style={{ width: '100%', fontSize: '1rem', padding: '.75rem', letterSpacing: '.08em' }}
                        onClick={handleBuscar}
                        disabled={loading}
                    >
                        {loading ? '⏳ BUSCANDO...' : '🔍 BUSCAR PARTIDOS'}
                    </button>
                </div>
            )}

            {/* Resultados */}
            {buscado && !loading && resultados !== null && (
                <div style={{ marginTop: '2rem' }}>
                    <div style={{
                        fontFamily: "'Barlow Condensed'", fontWeight: 700, fontSize: '1rem',
                        textTransform: 'uppercase', letterSpacing: '.06em',
                        color: 'var(--muted)', marginBottom: '1rem', textAlign: 'center',
                    }}>
                        {resultados.length === 0
                            ? `— SIN RESULTADOS AL BUSCAR ${estrategia.label.toUpperCase()} —`
                            : `${resultados.length} PARTIDO${resultados.length > 1 ? 'S' : ''} ENCONTRADO${resultados.length > 1 ? 'S' : ''} · ${estrategia.icon} ${estrategia.label.toUpperCase()}`}
                    </div>

                    <div className="partidos-grid">
                        {resultados.map(p => (
                            <div key={p.id} className="score-card">
                                <div className="score-card-top">
                                    <span style={{ fontFamily: "'Barlow Condensed'", fontWeight: 800, fontSize: '.9rem' }}>
                                        {SPORT_ICONS[p.deporte] || '🏟'} {p.deporte.toUpperCase()} · #{p.id}
                                    </span>
                                    <span className={`badge ${estadoBadge(p.estado)}`}>{p.estado.toUpperCase()}</span>
                                </div>

                                <div className="score-card-body">
                                    <div>
                                        <div className="score-card-sport">📍 {p.barrio || '—'}</div>
                                        <div style={{ fontSize: '.77rem', color: 'var(--muted)', marginTop: '.2rem' }}>
                                            ⏱ {p.duracionMinutos} min &nbsp;·&nbsp;
                                            🗓 {new Date(p.horario).toLocaleDateString('es-AR')} {new Date(p.horario).toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit' })}
                                        </div>
                                        <div style={{ fontSize: '.77rem', color: 'var(--muted)' }}>
                                            📊 Nivel: {p.nivelMinimo} – {p.nivelMaximo}
                                        </div>
                                    </div>
                                    <div style={{ textAlign: 'right' }}>
                                        <div className="score-card-players">
                                            {p.jugadoresActuales}<span>/{p.cantidadJugadores}</span>
                                        </div>
                                        <div style={{ fontSize: '.7rem', color: 'var(--muted)', textTransform: 'uppercase' }}>jugadores</div>
                                    </div>
                                </div>

                                <button className="btn btn-outline btn-sm"
                                    onClick={() => setExpanded(expanded === p.id ? null : p.id)}>
                                    {expanded === p.id ? '▲ OCULTAR' : `▼ ROSTER (${p.jugadoresActuales})`}
                                </button>

                                {expanded === p.id && (
                                    <div className="jugadores-list">
                                        {p.jugadores.length === 0
                                            ? <div style={{ fontSize: '.78rem', color: 'var(--muted)', padding: '.4rem', textAlign: 'center' }}>SIN JUGADORES AÚN</div>
                                            : p.jugadores.map(j => (
                                                <div key={j.id} className="jugador-row">
                                                    <span style={{ fontWeight: 600 }}>{j.nombre}</span>
                                                    <span className={`badge ${j.confirmado ? 'badge-green' : 'badge-yellow'}`}>
                                                        {j.confirmado ? '✓ OK' : 'PENDIENTE'}
                                                    </span>
                                                </div>
                                            ))}
                                    </div>
                                )}

                                <div className="score-card-actions">
                                    {currentUser ? (
                                        <button className="btn btn-success btn-sm" style={{ flex: 1 }} onClick={() => handleUnirse(p.id)}>
                                            ✚ UNIRME A ESTE PARTIDO
                                        </button>
                                    ) : (
                                        <button className="btn btn-outline btn-sm" style={{ flex: 1 }} onClick={onLoginRequired}>
                                            INICIÁ SESIÓN PARA UNIRTE
                                        </button>
                                    )}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}
