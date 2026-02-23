import { useState } from 'react';
import { api } from '../api';

const NIVELES = ['Principiante', 'Intermedio', 'Avanzado'];
const DEFAULT = {
    deporteId: '', cantidadJugadores: 5, duracionMinutos: 90,
    ciudad: '', latitud: '', longitud: '',
    horario: '', nivelMinimo: 'Principiante', nivelMaximo: 'Avanzado',
};

export default function CreatePartidoModal({ deportes, onClose, onCreated, toast }) {
    const [form, setForm] = useState(DEFAULT);
    const [loading, setLoading] = useState(false);

    const set = (k, v) => setForm(f => ({ ...f, [k]: v }));

    const submit = async e => {
        e.preventDefault();
        if (!form.deporteId || !form.ciudad || !form.horario) {
            toast('Completa todos los campos requeridos', 'error');
            return;
        }
        setLoading(true);
        try {
            await api.crearPartido({
                ...form,
                deporteId: Number(form.deporteId),
                cantidadJugadores: Number(form.cantidadJugadores),
                duracionMinutos: Number(form.duracionMinutos),
                latitud: Number(form.latitud) || 0,
                longitud: Number(form.longitud) || 0,
            });
            onCreated();
        } catch (e) {
            toast(e.message, 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-backdrop" onClick={e => e.target === e.currentTarget && onClose()}>
            <div className="modal">
                <div className="modal-title">🏟️ Nuevo partido</div>
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
                            <label className="form-label">Jugadores máx.</label>
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
                        <label className="form-label">Ciudad *</label>
                        <input className="form-control" placeholder="Buenos Aires" value={form.ciudad}
                            onChange={e => set('ciudad', e.target.value)} />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Latitud</label>
                            <input className="form-control" type="number" step="any" placeholder="-34.6037" value={form.latitud}
                                onChange={e => set('latitud', e.target.value)} />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Longitud</label>
                            <input className="form-control" type="number" step="any" placeholder="-58.3816" value={form.longitud}
                                onChange={e => set('longitud', e.target.value)} />
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Fecha y hora *</label>
                        <input className="form-control" type="datetime-local" value={form.horario}
                            onChange={e => set('horario', e.target.value)} />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Nivel mínimo</label>
                            <select className="form-control" value={form.nivelMinimo} onChange={e => set('nivelMinimo', e.target.value)}>
                                {NIVELES.map(n => <option key={n}>{n}</option>)}
                            </select>
                        </div>
                        <div className="form-group">
                            <label className="form-label">Nivel máximo</label>
                            <select className="form-control" value={form.nivelMaximo} onChange={e => set('nivelMaximo', e.target.value)}>
                                {NIVELES.map(n => <option key={n}>{n}</option>)}
                            </select>
                        </div>
                    </div>

                    <div className="modal-footer">
                        <button type="button" className="btn btn-outline" onClick={onClose}>Cancelar</button>
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? 'Creando…' : 'Crear partido'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
