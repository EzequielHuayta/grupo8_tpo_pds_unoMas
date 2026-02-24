import { useState } from 'react';
import { api } from '../api';
import NivelRangeSlider from './NivelRangeSlider';

const DEFAULT = {
    deporteId: '', cantidadJugadores: 5, duracionMinutos: 90,
    barrio: '', horario: '',
    nivelMinimo: 'Principiante', nivelMaximo: 'Avanzado',
};

export default function CreatePartidoModal({ deportes, barrios, currentUser, onClose, onCreated, toast }) {
    const [form, setForm] = useState(DEFAULT);
    const [loading, setLoading] = useState(false);

    const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

    const submit = async e => {
        e.preventDefault();
        if (!form.deporteId || !form.barrio || !form.horario) {
            toast('Completá todos los campos requeridos', 'error');
            return;
        }
        setLoading(true);
        try {
            const partido = await api.crearPartido({
                ...form,
                deporteId: Number(form.deporteId),
                cantidadJugadores: Number(form.cantidadJugadores),
                duracionMinutos: Number(form.duracionMinutos),
                creadorId: currentUser ? currentUser.id : null,
            });
            if (currentUser && partido && partido.id) {
                await api.agregarJugador(partido.id, currentUser.id);
            }
            onCreated();
        } catch (e) {
            toast(e.message, 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        /* ← Se quitó el onClick del backdrop; solo se cierra con el botón CANCELAR */
        <div className="modal-backdrop">
            <div className="modal">
                <div className="modal-title">🏟 NUEVO PARTIDO</div>
                <form onSubmit={submit}>
                    <div className="form-group">
                        <label className="form-label">Deporte *</label>
                        <select className="form-control" value={form.deporteId} onChange={e => set('deporteId', e.target.value)}>
                            <option value="">Seleccionar deporte…</option>
                            {deportes.map(d => <option key={d.id} value={d.id}>{d.nombre}</option>)}
                        </select>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Jugadores necesarios</label>
                            <input className="form-control" type="number" min="2" max="22" value={form.cantidadJugadores}
                                onChange={e => set('cantidadJugadores', e.target.value)} />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Duración (min)</label>
                            <input className="form-control" type="number" min="10" value={form.duracionMinutos}
                                onChange={e => set('duracionMinutos', e.target.value)} />
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Barrio *</label>
                        <select className="form-control" value={form.barrio} onChange={e => set('barrio', e.target.value)}>
                            <option value="">Seleccionar barrio…</option>
                            {barrios.map(b => <option key={b} value={b}>{b}</option>)}
                        </select>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Fecha y hora *</label>
                        <input className="form-control" type="datetime-local" value={form.horario}
                            onChange={e => set('horario', e.target.value)} />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Rango de nivel</label>
                        <NivelRangeSlider
                            nivelMin={form.nivelMinimo}
                            nivelMax={form.nivelMaximo}
                            onChange={(min, max) => setForm(f => ({ ...f, nivelMinimo: min, nivelMaximo: max }))}
                        />
                    </div>

                    <div className="modal-footer">
                        <button type="button" className="btn btn-outline" onClick={onClose}>CANCELAR</button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'CREANDO…' : 'CREAR PARTIDO'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
