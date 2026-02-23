import { useState } from 'react';
import { api } from '../api';
import { nivelBadge, initials } from '../utils';

const DEFAULT = { nombreUsuario: '', email: '', contrasena: '', ciudad: '', latitud: '', longitud: '', nivel: 'Principiante' };
const NIVELES = ['Principiante', 'Intermedio', 'Avanzado'];

export default function UsuariosList({ usuarios, partidos, onRefresh, toast }) {
    const [showRegister, setShowRegister] = useState(false);
    const [form, setForm] = useState(DEFAULT);
    const [loading, setLoading] = useState(false);

    const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

    const register = async e => {
        e.preventDefault();
        if (!form.nombreUsuario || !form.email || !form.contrasena || !form.ciudad) {
            toast('COMPLETÁ TODOS LOS CAMPOS', 'error'); return;
        }
        setLoading(true);
        try {
            await api.registrarUsuario({ ...form, latitud: Number(form.latitud) || 0, longitud: Number(form.longitud) || 0 });
            setShowRegister(false); setForm(DEFAULT);
            await onRefresh();
            toast('JUGADOR REGISTRADO');
        } catch (e) { toast(e.message, 'error'); }
        finally { setLoading(false); }
    };

    const cambiarNivel = async (id, nivel) => {
        try { await api.cambiarNivel(id, nivel); await onRefresh(); toast('NIVEL ACTUALIZADO'); }
        catch (e) { toast(e.message, 'error'); }
    };

    return (
        <div>
            <div className="section-header">
                <div>
                    <div className="section-label">Plantel</div>
                    <div className="section-title">Jugadores ({usuarios.length})</div>
                </div>
                <button className="btn btn-primary" onClick={() => setShowRegister(true)}>+ REGISTRAR JUGADOR</button>
            </div>

            {usuarios.length === 0
                ? <div className="empty">— SIN JUGADORES AÚN —</div>
                : <div style={{ display: 'flex', flexDirection: 'column', gap: '.5rem' }}>
                    {/* Table header */}
                    <div style={{ display: 'grid', gridTemplateColumns: '32px 40px 1fr 120px 120px 100px', gap: '.75rem', padding: '.4rem .75rem', fontSize: '.68rem', fontWeight: 800, textTransform: 'uppercase', letterSpacing: '.1em', color: 'var(--muted)', borderBottom: '2px solid var(--red)' }}>
                        <span>#</span><span></span><span>JUGADOR</span><span>CIUDAD</span><span>NIVEL</span><span>CAMBIAR</span>
                    </div>
                    {usuarios.map((u, i) => (
                        <div key={u.id} className="usuario-row" style={{ display: 'grid', gridTemplateColumns: '32px 40px 1fr 120px 120px 100px', gap: '.75rem', alignItems: 'center' }}>
                            <span className="number">{i + 1}</span>
                            <div className="avatar">{initials(u.nombreUsuario)}</div>
                            <div>
                                <div className="usuario-name">{u.nombreUsuario}</div>
                                <div className="usuario-meta">{u.email}</div>
                            </div>
                            <div className="usuario-meta">{u.ciudad || '—'}</div>
                            <span className={`badge ${nivelBadge(u.nivel)}`}>{u.nivel.toUpperCase()}</span>
                            <select className="form-control" style={{ fontSize: '.75rem', padding: '.25rem .4rem' }}
                                value={u.nivel}
                                onChange={e => cambiarNivel(u.id, e.target.value)}>
                                {NIVELES.map(n => <option key={n}>{n}</option>)}
                            </select>
                        </div>
                    ))}
                </div>
            }

            {showRegister && (
                <div className="modal-backdrop" onClick={e => e.target === e.currentTarget && setShowRegister(false)}>
                    <div className="modal">
                        <div className="modal-title">REGISTRAR JUGADOR</div>
                        <form onSubmit={register}>
                            <div className="form-group">
                                <label className="form-label">NOMBRE *</label>
                                <input className="form-control" placeholder="Juan Pérez" value={form.nombreUsuario} onChange={e => set('nombreUsuario', e.target.value)} />
                            </div>
                            <div className="form-group">
                                <label className="form-label">EMAIL *</label>
                                <input className="form-control" type="email" placeholder="juan@mail.com" value={form.email} onChange={e => set('email', e.target.value)} />
                            </div>
                            <div className="form-group">
                                <label className="form-label">CONTRASEÑA *</label>
                                <input className="form-control" type="password" value={form.contrasena} onChange={e => set('contrasena', e.target.value)} />
                            </div>
                            <div className="form-group">
                                <label className="form-label">CIUDAD *</label>
                                <input className="form-control" placeholder="Buenos Aires" value={form.ciudad} onChange={e => set('ciudad', e.target.value)} />
                            </div>
                            <div className="form-row">
                                <div className="form-group">
                                    <label className="form-label">LATITUD</label>
                                    <input className="form-control" type="number" step="any" placeholder="-34.60" value={form.latitud} onChange={e => set('latitud', e.target.value)} />
                                </div>
                                <div className="form-group">
                                    <label className="form-label">LONGITUD</label>
                                    <input className="form-control" type="number" step="any" placeholder="-58.38" value={form.longitud} onChange={e => set('longitud', e.target.value)} />
                                </div>
                            </div>
                            <div className="form-group">
                                <label className="form-label">NIVEL</label>
                                <select className="form-control" value={form.nivel} onChange={e => set('nivel', e.target.value)}>
                                    {NIVELES.map(n => <option key={n}>{n}</option>)}
                                </select>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline" onClick={() => setShowRegister(false)}>CANCELAR</button>
                                <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'REGISTRANDO…' : 'REGISTRAR'}</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
