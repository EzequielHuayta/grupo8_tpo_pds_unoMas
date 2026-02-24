import { useState, useEffect, useCallback, useRef } from 'react';
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

// ─── BellIcon ───────────────────────────────────────────────────────────────
function BellIcon({ userId }) {
  const [notifs, setNotifs] = useState([]);
  const [open, setOpen] = useState(false);
  const panelRef = useRef(null);

  // Polling each 5s
  useEffect(() => {
    if (!userId) return;
    const poll = async () => {
      try {
        const lista = await api.getNotificaciones(userId);
        setNotifs(lista || []);
      } catch { /* servidor no disponible, silenciar */ }
    };
    poll();
    const t = setInterval(poll, 5000);
    return () => clearInterval(t);
  }, [userId]);

  // Cierra el panel si se hace click fuera
  useEffect(() => {
    const handler = (e) => {
      if (panelRef.current && !panelRef.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const handleOpen = () => {
    setOpen(o => !o);
  };

  const handleLeer = async () => {
    await api.leerNotificaciones(userId);
    setNotifs([]);
    setOpen(false);
  };

  const unread = notifs.length;

  return (
    <div ref={panelRef} style={{ position: 'relative' }}>
      {/* Botón campanita */}
      <button
        onClick={handleOpen}
        title="Notificaciones"
        style={{
          background: 'none', border: 'none', cursor: 'pointer',
          position: 'relative', display: 'flex', alignItems: 'center',
          padding: '4px',
        }}
      >
        <svg width="22" height="22" viewBox="0 0 24 24" fill="none"
          stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>
        {unread > 0 && (
          <span style={{
            position: 'absolute', top: '-2px', right: '-2px',
            background: 'var(--red)', color: 'white',
            borderRadius: '50%', fontSize: '10px', fontWeight: 900,
            minWidth: '16px', height: '16px',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            padding: '0 3px', lineHeight: 1,
            fontFamily: "'Barlow Condensed', sans-serif",
            boxShadow: '0 0 0 2px var(--bg)',
          }}>
            {unread > 99 ? '99+' : unread}
          </span>
        )}
      </button>

      {/* Panel desplegable */}
      {open && (
        <div style={{
          position: 'absolute', top: 'calc(100% + 8px)', right: 0,
          width: '320px', maxHeight: '360px',
          background: 'var(--surface)', border: '1px solid var(--border)',
          borderRadius: 'var(--radius)', boxShadow: '0 8px 24px rgba(0,0,0,.5)',
          zIndex: 1000, display: 'flex', flexDirection: 'column',
          overflow: 'hidden',
        }}>
          {/* Header panel */}
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '.6rem .9rem', borderBottom: '1px solid var(--border)',
          }}>
            <span style={{
              fontFamily: "'Barlow Condensed'", fontWeight: 800,
              fontSize: '.85rem', letterSpacing: '.08em', color: 'var(--text)',
            }}>
              🔔 NOTIFICACIONES {unread > 0 && <span style={{ color: 'var(--red)' }}>({unread})</span>}
            </span>
            {unread > 0 && (
              <button onClick={handleLeer} style={{
                background: 'none', border: '1px solid var(--border)',
                borderRadius: '4px', color: 'var(--muted)',
                fontSize: '.7rem', padding: '2px 6px', cursor: 'pointer',
                fontFamily: "'Barlow Condensed'", fontWeight: 700, letterSpacing: '.05em',
              }}>
                MARCAR LEÍDAS
              </button>
            )}
          </div>

          {/* Lista */}
          <div style={{ overflowY: 'auto', flex: 1 }}>
            {notifs.length === 0
              ? (
                <div style={{
                  color: 'var(--muted)', fontSize: '.8rem', textAlign: 'center',
                  padding: '1.5rem',
                }}>
                  Sin notificaciones nuevas
                </div>
              )
              : notifs.map((msg, i) => (
                <div key={i} style={{
                  padding: '.65rem .9rem',
                  borderBottom: i < notifs.length - 1 ? '1px solid var(--border)' : 'none',
                  fontSize: '.82rem', color: 'var(--text)', lineHeight: 1.45,
                  display: 'flex', gap: '.5rem', alignItems: 'flex-start',
                }}>
                  <span style={{ color: 'var(--red)', flexShrink: 0 }}>●</span>
                  {msg}
                </div>
              ))
            }
          </div>
        </div>
      )}
    </div>
  );
}

// ─── App ─────────────────────────────────────────────────────────────────────
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

  // Ticker: solo partidos "En juego"
  const enJuegoPartidos = partidos.filter(p => p.estado === 'En juego');
  const tickerItems = enJuegoPartidos.map(p =>
    `${SPORT_ICONS[p.deporte] || '🏟'} ${p.deporte.toUpperCase()} · ${p.barrio} · ${p.jugadoresActuales}/${p.cantidadJugadores} · EN JUEGO`
  );

  // La campanita solo aplica a usuarios con estrategia In-App
  const esInApp = currentUser && currentUser.notificacion === 'In-App';

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
              {/* Campanita — solo para usuarios In-App */}
              {esInApp && <BellIcon userId={currentUser.id} />}

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

      <div className="ticker">
        <span className="ticker-label" style={{ flexShrink: 0 }}>EN VIVO</span>
        <div style={{ overflow: 'hidden', flex: 1 }}>
          <div className="ticker-items">
            {(() => {
              const base = tickerItems.length > 0
                ? tickerItems
                : ['📅 No hay ningún partido en juego ahora mismo ¡Andá a MIS PARTIDOS y creá el próximo!'];
              return Array.from({ length: 10 }, () => base).flat()
                .map((t, i) => <span key={i}>{t}</span>);
            })()}
          </div>
        </div>
      </div>

      <main className="main">
        {loading && <div className="spinner" />}
        {!loading && page === 'dashboard' &&
          <Dashboard partidos={partidos} usuarios={usuarios} onNavigate={setPage} />}
        {!loading && page === 'buscar' &&
          <BuscarPartidos
            currentUser={currentUser}
            onLoginRequired={() => setShowLogin(true)}
            onRefresh={load}
            onNavigate={setPage}
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
          <UsuariosList usuarios={usuarios} partidos={partidos} deportes={deportes} barrios={barrios} currentUser={currentUser} onRefresh={load} toast={toast} />}
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
