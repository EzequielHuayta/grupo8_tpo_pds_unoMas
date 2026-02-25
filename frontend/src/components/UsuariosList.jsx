import { useState } from 'react';
import { api } from '../api';
import { nivelBadge, initials } from '../utils';
import { SPORT_ICONS } from '../App';

const DEFAULT = { nombreUsuario: '', email: '', barrio: '', nivel: 'Principiante', notificacion: 'Email', deporteFavoritoId: '' };
const NIVELES = ['Principiante', 'Intermedio', 'Avanzado'];

export default function UsuariosList({ usuarios, partidos, deportes, barrios, currentUser, onRefresh, toast }) {
    const [showRegister, setShowRegister] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [form, setForm] = useState(DEFAULT);
    const [loading, setLoading] = useState(false);

    const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

    const abrirModalEdicion = (u) => {
        setForm({
            id: u.id,
            nombreUsuario: u.nombreUsuario || '',
            email: u.email || '',
            barrio: u.barrio || '',
            nivel: u.nivel || 'Principiante',
            notificacion: u.notificacion || 'Email',
            deporteFavoritoId: u.deporteFavoritoId || ''
        });
        setIsEditing(true);
        setShowRegister(true);
    };

    const abrirModalRegistro = () => {
        setForm(DEFAULT);
        setIsEditing(false);
        setShowRegister(true);
    };

    const guardarUsuario = async e => {
        e.preventDefault();
        if (!form.nombreUsuario || !form.email || !form.barrio) {
            toast('COMPLETÁ TODOS LOS CAMPOS', 'error'); return;
        }
        setLoading(true);
        try {
            if (isEditing) {
                await api.modificarUsuario(form.id, { ...form });
                toast('JUGADOR MODIFICADO');
            } else {
                await api.registrarUsuario({ ...form, contrasena: '1234' });
                toast('JUGADOR REGISTRADO');
            }
            setShowRegister(false); setForm(DEFAULT);
            await onRefresh();
        } catch (e) { toast(e.message, 'error'); }
        finally { setLoading(false); }
    };

    return (
        <div>
            <div className="section-header">
                <div>
                    <div className="section-label">Plantel</div>
                    <div className="section-title">Jugadores ({usuarios.length})</div>
                </div>
                <button className="btn btn-primary" onClick={abrirModalRegistro}>+ REGISTRAR JUGADOR</button>
            </div>

            {usuarios.length === 0
                ? <div className="empty">— SIN JUGADORES AÚN —</div>
                : <div style={{ display: 'flex', flexDirection: 'column', gap: '.5rem' }}>
                    {/* Table header */}
                    <div style={{ display: 'grid', gridTemplateColumns: '32px 40px 1fr 120px 120px 70px 120px 100px', gap: '.75rem', padding: '.4rem .75rem', fontSize: '.68rem', fontWeight: 800, textTransform: 'uppercase', letterSpacing: '.1em', color: 'var(--muted)', borderBottom: '2px solid var(--red)' }}>
                        <span>#</span><span></span><span>JUGADOR</span><span>BARRIO</span><span>DEPORTE FAV</span><span>PARTIDOS</span><span>NIVEL</span><span style={{ textAlign: 'center' }}>MODIFICAR</span>
                    </div>
                    {[...usuarios].sort((a, b) => Number(a.id) - Number(b.id)).map((u, i) => {
                        const esMiPerfil = currentUser && Number(currentUser.id) === Number(u.id);
                        return (
                            <div key={u.id} className="usuario-row" style={{ display: 'grid', gridTemplateColumns: '32px 40px 1fr 120px 120px 70px 120px 100px', gap: '.75rem', alignItems: 'center' }}>
                                <span className="number">{i + 1}</span>
                                <div className="avatar">{initials(u.nombreUsuario)}</div>
                                <div>
                                    <div className="usuario-name">{u.nombreUsuario}</div>
                                    <div className="usuario-meta">{u.email}</div>
                                </div>
                                <div className="usuario-meta">📍 {u.barrio || '—'}</div>
                                <div className="usuario-meta">{u.deporteFavorito ? (SPORT_ICONS[u.deporteFavorito] || '🏟') + ' ' + u.deporteFavorito : '—'}</div>
                                <span style={{ fontFamily: "'Barlow Condensed'", fontWeight: 700, fontSize: '.95rem', color: 'var(--text)', textAlign: 'center' }}>
                                    {u.cantidadPartidosCompletados || 0}
                                </span>
                                <span className={`badge ${nivelBadge(u.nivel)}`}>{u.nivel.toUpperCase()}</span>
                                {esMiPerfil
                                    ? <button className="btn" style={{ background: 'var(--red)', color: 'white', padding: '0.4rem', borderRadius: '4px', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', border: 'none', margin: '0 auto' }} onClick={() => abrirModalEdicion(u)}>
                                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M17 3a2.828 2.828 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5L17 3z"></path></svg>
                                    </button>
                                    : <span style={{ fontSize: '.75rem', color: 'var(--muted)', textAlign: 'center' }}>—</span>
                                }
                            </div>
                        );
                    })}
                </div>
            }

            {showRegister && (
                <div className="modal-backdrop" onClick={e => e.target === e.currentTarget && setShowRegister(false)}>
                    <div className="modal">
                        <div className="modal-title">{isEditing ? 'MODIFICAR JUGADOR' : 'REGISTRAR JUGADOR'}</div>
                        <form onSubmit={guardarUsuario}>
                            <div className="form-group">
                                <label className="form-label">NOMBRE *</label>
                                <input className="form-control" placeholder="Juan Pérez" value={form.nombreUsuario} onChange={e => set('nombreUsuario', e.target.value)} />
                            </div>
                            <div className="form-group">
                                <label className="form-label">EMAIL *</label>
                                <input className="form-control" type="email" placeholder="juan@mail.com" value={form.email} onChange={e => set('email', e.target.value)} />
                            </div>
                            <div className="form-group">
                                <label className="form-label">BARRIO *</label>
                                <select className="form-control" value={form.barrio} onChange={e => set('barrio', e.target.value)}>
                                    <option value="">Seleccionar barrio…</option>
                                    {(barrios || []).map(b => <option key={b} value={b}>{b}</option>)}
                                </select>
                            </div>
                            <div className="form-group">
                                <label className="form-label">DEPORTE FAVORITO</label>
                                <select className="form-control" value={form.deporteFavoritoId} onChange={e => set('deporteFavoritoId', e.target.value)}>
                                    <option value="">Seleccionar deporte…</option>
                                    {(deportes || []).map(d => <option key={d.id} value={d.id}>{d.nombre}</option>)}
                                </select>
                            </div>
                            <div className="form-group">
                                <label className="form-label">NIVEL</label>
                                <select className="form-control" value={form.nivel} onChange={e => set('nivel', e.target.value)}>
                                    {NIVELES.map(n => <option key={n}>{n}</option>)}
                                </select>
                            </div>
                            <div className="form-group">
                                <label className="form-label">MEDIO DE NOTIFICACIÓN</label>
                                <select className="form-control" value={form.notificacion} onChange={e => set('notificacion', e.target.value)}>
                                    <option value="Email">Email</option>
                                    <option value="In-App">In-App (Firebase)</option>
                                </select>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline" onClick={() => setShowRegister(false)}>CANCELAR</button>
                                <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? (isEditing ? 'MODIFICANDO…' : 'REGISTRANDO…') : 'ACEPTAR'}</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
