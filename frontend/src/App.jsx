import { useState, useEffect, useCallback } from 'react';
import './App.css';
import { api } from './api';
import { initials } from './utils';
import PartidosList from './components/PartidosList';
import UsuariosList from './components/UsuariosList';
import Dashboard from './components/Dashboard';
import LoginModal from './components/LoginModal';
import BuscarPartidos from './components/BuscarPartidos';

export const SPORT_ICONS = { 'Fútbol': '⚽', 'Básquet': '🏀', 'Tenis': '🎾', 'Vóley': '🏐', 'Paddle': '🏓' };

const SESSION_KEY = 'unomas_user';

export default function App() {
  const [page, setPage] = useState('dashboard');
  const [partidos, setPartidos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [deportes, setDeportes] = useState([]);
  const [barrios, setBarrios] = useState([]);
  const [loading, setLoading] = useState(false);
  const [toasts, setToasts] = useState([]);
  const [showLogin, setShowLogin] = useState(false);

  // Logged-in user persisted in localStorage
  const [currentUser, setCurrentUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem(SESSION_KEY)); } catch { return null; }
  });

  const toast = useCallback((msg, type = 'success') => {
    const id = Date.now();
    setToasts(t => [...t, { id, msg, type }]);
    setTimeout(() => setToasts(t => t.filter(x => x.id !== id)), 3500);
  }, []);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const [p, u, d, b] = await Promise.all([api.getPartidos(), api.getUsuarios(), api.getDeportes(), api.getBarrios()]);
      setPartidos(p); setUsuarios(u); setDeportes(d); setBarrios(b);
    } catch {
      toast('No se pudo conectar al servidor. ¿Está corriendo el backend?', 'error');
    } finally { setLoading(false); }
  }, [toast]);

  useEffect(() => { load(); }, [load]);

  const handleLogin = (user) => {
    setCurrentUser(user);
    localStorage.setItem(SESSION_KEY, JSON.stringify(user));
    setShowLogin(false);
    toast(`¡BIENVENIDO, ${user.nombreUsuario.toUpperCase()}!`);
  };

  const handleLogout = () => {
    setCurrentUser(null);
    localStorage.removeItem(SESSION_KEY);
    toast('SESIÓN CERRADA');
  };

  // Ticker
  const tickerItems = partidos.slice(0, 6).map(p =>
    `${SPORT_ICONS[p.deporte] || '🏟'} ${p.deporte.toUpperCase()} · ${p.ubicacion} · ${p.jugadoresActuales}/${p.cantidadJugadores} · ${p.estado.toUpperCase()}`
  );

  return (
    <div className="app">
      <header className="header">
        <span className="logo">UNO<span>MÁS</span></span>

        <nav className="nav">
          {[['dashboard', 'INICIO'], ['buscar', 'BUSCAR'], ['partidos', 'MIS PARTIDOS'], ['usuarios', 'JUGADORES']].map(([id, label]) => (
            <button key={id} className={`nav-btn${page === id ? ' active' : ''}`} onClick={() => setPage(id)}>{label}</button>
          ))}
        </nav>

        <div style={{ display: 'flex', gap: '.5rem', alignItems: 'center' }}>
          {currentUser ? (
            <>
              <div style={{ display: 'flex', alignItems: 'center', gap: '.5rem' }}>
                <div className="avatar" style={{ width: 32, height: 32, fontSize: '.75rem' }}>
                  {initials(currentUser.nombreUsuario)}
                </div>
                <span style={{ fontSize: '.85rem', fontFamily: "'Barlow Condensed'", fontWeight: 700 }}>
                  {currentUser.nombreUsuario.toUpperCase()}
                </span>
              </div>
              <button className="btn btn-danger btn-sm" onClick={handleLogout}>SALIR</button>
            </>
          ) : (
            <button className="btn btn-primary btn-sm" onClick={() => setShowLogin(true)}>INICIAR SESIÓN</button>
          )}
          <button className="btn btn-outline btn-sm" onClick={load}>↻</button>
        </div>
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
        {!loading && page === 'buscar' &&
          <BuscarPartidos
            currentUser={currentUser}
            onLoginRequired={() => setShowLogin(true)}
            onRefresh={load}
            toast={toast}
          />}
        {!loading && page === 'partidos' &&
          <PartidosList
            partidos={partidos} usuarios={usuarios} deportes={deportes} barrios={barrios}
            currentUser={currentUser}
            onLoginRequired={() => setShowLogin(true)}
            onRefresh={load} toast={toast}
          />}
        {!loading && page === 'usuarios' &&
          <UsuariosList usuarios={usuarios} partidos={partidos} barrios={barrios} currentUser={currentUser} onRefresh={load} toast={toast} />}
      </main>

      {showLogin && (
        <LoginModal
          usuarios={usuarios}
          barrios={barrios}
          onLogin={handleLogin}
        />
      )}

      <div className="toast-container">
        {toasts.map(t => <div key={t.id} className={`toast toast-${t.type}`}>{t.msg}</div>)}
      </div>
    </div>
  );
}
