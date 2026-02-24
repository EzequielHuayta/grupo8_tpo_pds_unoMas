import { useState } from 'react';
import { api } from '../api';
import { estadoBadge } from '../utils';
import { SPORT_ICONS } from '../App';

const ESTRATEGIAS = [
    {
        key: 'NIVEL',
        label: 'Por Nivel',
        icon: '📊',
        descripcion: 'Encuentra partidos donde tu nivel encaja con el rango requerido.',
    },
    {
        key: 'UBICACION',
        label: 'Por Ubicación',
        icon: '📍',
        descripcion: 'Encuentra partidos más cercanos a tu ciudad (radio máximo 50 km).',
    },
    {
        key: 'HISTORIAL',
        label: 'Por Historial',
        icon: '🏆',
        descripcion: 'Encuentra partidos del deporte que ya has jugado antes.',
    },
];

export default function BuscarPartidos({ currentUser, onLoginRequired, onRefresh, toast }) {
    const [estrategiaIdx, setEstrategiaIdx] = useState(0);
    const [resultados, setResultados] = useState(null);
    const [loading, setLoading] = useState(false);
    const [expanded, setExpanded] = useState(null);
    const [buscado, setBuscado] = useState(false);

    const estrategia = ESTRATEGIAS[estrategiaIdx];

    const handleBuscar = async () => {
        if (!currentUser) { onLoginRequired(); return; }
        setLoading(true);
        setBuscado(true);
        try {
            const data = await api.buscarPartidos(currentUser.id, estrategia.key);
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
            // Refrescar resultados
            const data = await api.buscarPartidos(currentUser.id, estrategia.key);
            setResultados(data);
        } catch (e) {
            toast(e.message, 'error');
        }
    };

    return (
        <div>
            <div className="section-header">
                <div>
                    <div className="section-label">Emparejamiento</div>
                    <div className="section-title">Buscar Partido</div>
                </div>
            </div>

            {/* Estrategia selector */}
            <div style={{
                background: 'var(--surface)',
                border: '1px solid var(--border)',
                borderRadius: 12,
                padding: '2rem',
                maxWidth: 560,
                margin: '0 auto 2rem',
                textAlign: 'center',
            }}>
                {!currentUser && (
                    <div style={{
                        background: 'rgba(255,200,0,.1)',
                        border: '1px solid var(--gold)',
                        borderRadius: 8,
                        padding: '.75rem 1rem',
                        marginBottom: '1.5rem',
                        fontSize: '.83rem',
                        color: 'var(--gold)',
                        fontFamily: "'Barlow Condensed'",
                        fontWeight: 700,
                        letterSpacing: '.05em',
                    }}>
                        ⚠ INICIÁ SESIÓN PARA BUSCAR PARTIDOS
                    </div>
                )}

                <div style={{ marginBottom: '1.5rem' }}>
                    <div style={{ fontSize: '2.5rem', marginBottom: '.4rem' }}>{estrategia.icon}</div>
                    <div style={{
                        fontFamily: "'Barlow Condensed'",
                        fontWeight: 800,
                        fontSize: '1.4rem',
                        letterSpacing: '.06em',
                        textTransform: 'uppercase',
                        color: 'var(--accent)',
                    }}>
                        {estrategia.label}
                    </div>
                    <div style={{ fontSize: '.83rem', color: 'var(--muted)', marginTop: '.3rem' }}>
                        {estrategia.descripcion}
                    </div>
                </div>

                {/* Slider */}
                <div style={{ marginBottom: '1.5rem' }}>
                    <input
                        type="range"
                        min={0}
                        max={ESTRATEGIAS.length - 1}
                        step={1}
                        value={estrategiaIdx}
                        onChange={e => {
                            setEstrategiaIdx(Number(e.target.value));
                            setResultados(null);
                            setBuscado(false);
                        }}
                        style={{ width: '100%', accentColor: 'var(--accent)', cursor: 'pointer' }}
                    />
                    <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        marginTop: '.4rem',
                    }}>
                        {ESTRATEGIAS.map((e, i) => (
                            <span key={e.key} style={{
                                fontSize: '.72rem',
                                fontFamily: "'Barlow Condensed'",
                                fontWeight: i === estrategiaIdx ? 800 : 400,
                                color: i === estrategiaIdx ? 'var(--accent)' : 'var(--muted)',
                                textTransform: 'uppercase',
                                letterSpacing: '.04em',
                                cursor: 'pointer',
                            }} onClick={() => { setEstrategiaIdx(i); setResultados(null); setBuscado(false); }}>
                                {e.icon} {e.label}
                            </span>
                        ))}
                    </div>
                </div>

                <button
                    className="btn btn-primary"
                    style={{ width: '100%', fontSize: '1rem', padding: '.7rem', letterSpacing: '.08em' }}
                    onClick={handleBuscar}
                    disabled={loading}
                >
                    {loading ? 'BUSCANDO...' : `🔍 BUSCAR CON ESTRATEGIA ${estrategia.label.toUpperCase()}`}
                </button>
            </div>

            {/* Resultados */}
            {buscado && !loading && resultados !== null && (
                <div>
                    <div style={{
                        fontFamily: "'Barlow Condensed'",
                        fontWeight: 700,
                        fontSize: '1rem',
                        textTransform: 'uppercase',
                        letterSpacing: '.06em',
                        color: 'var(--muted)',
                        marginBottom: '1rem',
                        textAlign: 'center',
                    }}>
                        {resultados.length === 0
                            ? `— SIN RESULTADOS PARA ${estrategia.label.toUpperCase()} —`
                            : `${resultados.length} PARTIDO${resultados.length > 1 ? 'S' : ''} ENCONTRADO${resultados.length > 1 ? 'S' : ''} · ${estrategia.icon} ${estrategia.label.toUpperCase()}`}
                    </div>

                    <div className="partidos-grid">
                        {resultados.map(p => (
                            <div key={p.id} className="score-card">
                                {/* Header */}
                                <div className="score-card-top">
                                    <span style={{ fontFamily: "'Barlow Condensed'", fontWeight: 800, fontSize: '.9rem' }}>
                                        {SPORT_ICONS[p.deporte] || '🏟'} {p.deporte.toUpperCase()} · #{p.id}
                                    </span>
                                    <span className={`badge ${estadoBadge(p.estado)}`}>
                                        {p.estado.toUpperCase()}
                                    </span>
                                </div>

                                {/* Body */}
                                <div className="score-card-body">
                                    <div>
                                        <div className="score-card-sport">📍 {p.ubicacion}</div>
                                        <div style={{ fontSize: '.77rem', color: 'var(--muted)', marginTop: '.2rem' }}>
                                            ⏱ {p.duracionMinutos} min &nbsp;·&nbsp;
                                            🗓 {new Date(p.horario).toLocaleDateString('es-AR')}
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

                                {/* Roster toggle */}
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

                                {/* Acción unirse */}
                                <div className="score-card-actions">
                                    {currentUser ? (
                                        <button
                                            className="btn btn-success btn-sm"
                                            style={{ flex: 1 }}
                                            onClick={() => handleUnirse(p.id)}
                                        >
                                            ✚ UNIRME A ESTE PARTIDO
                                        </button>
                                    ) : (
                                        <button className="btn btn-outline btn-sm" style={{ flex: 1 }} onClick={onLoginRequired}>
                                            INICIAR SESIÓN PARA UNIRTE
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

