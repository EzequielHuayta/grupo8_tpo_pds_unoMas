import { useState } from 'react';
import { api } from '../api';
import { initials } from '../utils';

export default function LoginModal({ usuarios, onLogin }) {
    const [step, setStep] = useState('pick');   // 'pick' | 'loading'
    const [search, setSearch] = useState('');
    const [error, setError] = useState('');

    const filtered = usuarios.filter(u =>
        u.nombreUsuario.toLowerCase().includes(search.toLowerCase())
    );

    const doLogin = async (usuario) => {
        setStep('loading');
        try {
            const u = await api.loginSimple(usuario.nombreUsuario);
            onLogin(u);
        } catch (e) {
            setError(e.message);
            setStep('pick');
        }
    };

    return (
        <div className="modal-backdrop" style={{ alignItems: 'center' }}>
            <div className="modal" style={{ maxWidth: 400 }}>
                <div style={{ textAlign: 'center', marginBottom: '1.25rem' }}>
                    <div style={{
                        fontFamily: "'Barlow Condensed'", fontSize: '2rem', fontWeight: 900,
                        color: 'var(--red)', letterSpacing: '-1px', marginBottom: '.25rem'
                    }}>
                        UNOMÁS
                    </div>
                    <div className="modal-title" style={{ marginBottom: 0 }}>SELECCIONÁ TU JUGADOR</div>
                    <div style={{ fontSize: '.8rem', color: 'var(--muted)', marginTop: '.25rem' }}>
                        Sin contraseña — elegí tu nombre y jugá
                    </div>
                </div>

                {error && (
                    <div style={{ background: 'rgba(204,0,0,.15)', border: '1px solid var(--red)', borderRadius: 'var(--radius)', padding: '.6rem .9rem', fontSize: '.85rem', color: '#ff5252', marginBottom: '1rem' }}>
                        {error}
                    </div>
                )}

                <div className="form-group">
                    <input
                        className="form-control"
                        placeholder="Buscar jugador…"
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        autoFocus
                    />
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '.4rem', maxHeight: 280, overflowY: 'auto' }}>
                    {filtered.length === 0 && (
                        <div className="empty" style={{ padding: '1.5rem' }}>— SIN RESULTADOS —</div>
                    )}
                    {filtered.map(u => (
                        <button key={u.id}
                            disabled={step === 'loading'}
                            onClick={() => doLogin(u)}
                            style={{
                                background: 'var(--surface)', border: '1px solid var(--border)',
                                borderRadius: 'var(--radius)', padding: '.7rem 1rem',
                                display: 'flex', alignItems: 'center', gap: '.75rem',
                                cursor: 'pointer', color: 'var(--text)', textAlign: 'left',
                                transition: 'background .15s, border-color .15s',
                            }}
                            onMouseEnter={e => { e.currentTarget.style.background = 'var(--surface2)'; e.currentTarget.style.borderColor = 'var(--red)'; }}
                            onMouseLeave={e => { e.currentTarget.style.background = 'var(--surface)'; e.currentTarget.style.borderColor = 'var(--border)'; }}
                        >
                            <div className="avatar" style={{ width: 36, height: 36, fontSize: '.85rem' }}>
                                {initials(u.nombreUsuario)}
                            </div>
                            <div>
                                <div style={{ fontWeight: 700, fontSize: '.9rem' }}>{u.nombreUsuario}</div>
                                <div style={{ fontSize: '.75rem', color: 'var(--muted)' }}>{u.nivel} · {u.ciudad || 'Sin ciudad'}</div>
                            </div>
                            <div style={{ marginLeft: 'auto', color: 'var(--red)', fontSize: '.85rem', fontFamily: "'Barlow Condensed'", fontWeight: 700 }}>
                                ENTRAR →
                            </div>
                        </button>
                    ))}
                </div>

                <div style={{ marginTop: '1rem', paddingTop: '1rem', borderTop: '1px solid var(--border)', fontSize: '.78rem', color: 'var(--muted)', textAlign: 'center' }}>
                    ¿No estás en la lista? Registrate primero en la sección JUGADORES.
                </div>
            </div>
        </div>
    );
}
