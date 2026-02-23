import { useState, useEffect, useCallback } from 'react';
import './App.css';
import { api } from './api';
import PartidosList from './components/PartidosList';
import UsuariosList from './components/UsuariosList';
import Dashboard from './components/Dashboard';

const SPORT_ICONS = { 'Fútbol': '⚽', 'Básquet': '🏀', 'Tenis': '🎾', 'Vóley': '🏐', 'Paddle': '🏓' };

export { SPORT_ICONS };

export default function App() {
  const [page, setPage] = useState('dashboard');
  const [partidos, setPartidos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [deportes, setDeportes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [toasts, setToasts] = useState([]);

  const toast = useCallback((msg, type = 'success') => {
    const id = Date.now();
    setToasts(t => [...t, { id, msg, type }]);
    setTimeout(() => setToasts(t => t.filter(x => x.id !== id)), 3500);
  }, []);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [p, u, d] = await Promise.all([api.getPartidos(), api.getUsuarios(), api.getDeportes()]);
      setPartidos(p); setUsuarios(u); setDeportes(d);
    } catch {
      toast('No se pudo conectar al servidor. ¿Está corriendo el backend?', 'error');
    } finally { setLoading(false); }
  }, [toast]);

  useEffect(() => { load(); }, [load]);

  // Ticker content
  const tickerItems = partidos.slice(0, 6).map(p =>
    `${SPORT_ICONS[p.deporte] || '🏟'} ${p.deporte.toUpperCase()} · ${p.ubicacion} · ${p.jugadoresActuales}/${p.cantidadJugadores} jugadores · ${p.estado.toUpperCase()}`
  );

  return (
    <div className="app">
      <header className="header">
        <span className="logo">UNO<span>MÁS</span></span>
        <nav className="nav">
          {[['dashboard', 'INICIO'], ['partidos', 'PARTIDOS'], ['usuarios', 'JUGADORES']].map(([id, label]) => (
            <button key={id} className={`nav-btn${page === id ? ' active' : ''}`} onClick={() => setPage(id)}>{label}</button>
          ))}
        </nav>
        <button className="btn btn-outline btn-sm" onClick={load}>↻ ACTUALIZAR</button>
      </header>

      {tickerItems.length > 0 && (
        <div className="ticker">
          <span className="ticker-label">EN VIVO</span>
          <div className="ticker-items">
            {[...tickerItems, ...tickerItems].map((t, i) => <span key={i}>{t}</span>)}
          </div>
        </div>
      )}

      <main className="main">
        {loading && <div className="spinner" />}
        {!loading && page === 'dashboard' &&
          <Dashboard partidos={partidos} usuarios={usuarios} onNavigate={setPage} />}
        {!loading && page === 'partidos' &&
          <PartidosList partidos={partidos} usuarios={usuarios} deportes={deportes} onRefresh={load} toast={toast} />}
        {!loading && page === 'usuarios' &&
          <UsuariosList usuarios={usuarios} partidos={partidos} onRefresh={load} toast={toast} />}
      </main>

      <div className="toast-container">
        {toasts.map(t => <div key={t.id} className={`toast toast-${t.type}`}>{t.msg}</div>)}
      </div>
    </div>
  );
}
