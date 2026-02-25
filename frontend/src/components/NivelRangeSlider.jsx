/**
 * NivelRangeSlider
 * Lets the user pick a min/max skill level from: Principiante, Intermedio, Avanzado.
 *
 * Props:
 *   nivelMin  {string}  - current minimum level value
 *   nivelMax  {string}  - current maximum level value
 *   onChange  {fn}      - called as onChange(min, max) whenever selection changes
 */

const NIVELES = ['Principiante', 'Intermedio', 'Avanzado'];

const LABEL_MAP = {
    Principiante: '🟢 Principiante',
    Intermedio: '🟡 Intermedio',
    Avanzado: '🔴 Avanzado',
};

export default function NivelRangeSlider({ nivelMin, nivelMax, onChange }) {
    const minIdx = NIVELES.indexOf(nivelMin);
    const maxIdx = NIVELES.indexOf(nivelMax);

    const handleMin = (e) => {
        const newMinIdx = Number(e.target.value);
        // min cannot exceed max
        const newMin = NIVELES[newMinIdx];
        const newMax = newMinIdx > maxIdx ? NIVELES[newMinIdx] : nivelMax;
        onChange(newMin, newMax);
    };

    const handleMax = (e) => {
        const newMaxIdx = Number(e.target.value);
        // max cannot be below min
        const newMax = NIVELES[newMaxIdx];
        const newMin = newMaxIdx < minIdx ? NIVELES[newMaxIdx] : nivelMin;
        onChange(newMin, newMax);
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {/* Visual track with highlighted range */}
            <div style={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
                {NIVELES.map((nivel, idx) => {
                    const active = idx >= minIdx && idx <= maxIdx;
                    return (
                        <div
                            key={nivel}
                            style={{
                                flex: 1,
                                textAlign: 'center',
                                padding: '6px 4px',
                                borderRadius: '8px',
                                fontSize: '.72rem',
                                fontWeight: active ? '700' : '400',
                                background: active
                                    ? 'var(--accent, #6c63ff)'
                                    : 'var(--bg2, rgba(255,255,255,0.07))',
                                color: active ? '#fff' : 'var(--muted, #888)',
                                border: active
                                    ? '1.5px solid var(--accent, #6c63ff)'
                                    : '1.5px solid transparent',
                                transition: 'all .2s',
                                userSelect: 'none',
                            }}
                        >
                            {LABEL_MAP[nivel]}
                        </div>
                    );
                })}
            </div>

            {/* Sliders */}
            <div style={{ display: 'flex', gap: '12px' }}>
                <div style={{ flex: 1 }}>
                    <label style={{ fontSize: '.7rem', color: 'var(--muted, #888)', display: 'block', marginBottom: '4px' }}>
                        Mínimo
                    </label>
                    <input
                        type="range"
                        min={0}
                        max={NIVELES.length - 1}
                        value={minIdx}
                        onChange={handleMin}
                        style={{ width: '100%', accentColor: 'var(--accent, #6c63ff)' }}
                    />
                </div>
                <div style={{ flex: 1 }}>
                    <label style={{ fontSize: '.7rem', color: 'var(--muted, #888)', display: 'block', marginBottom: '4px' }}>
                        Máximo
                    </label>
                    <input
                        type="range"
                        min={0}
                        max={NIVELES.length - 1}
                        value={maxIdx}
                        onChange={handleMax}
                        style={{ width: '100%', accentColor: 'var(--accent, #6c63ff)' }}
                    />
                </div>
            </div>

            {/* Summary text */}
            <div style={{ fontSize: '.75rem', color: 'var(--muted, #888)', textAlign: 'center' }}>
                {nivelMin === nivelMax
                    ? `Solo nivel ${nivelMin}`
                    : `De ${nivelMin} a ${nivelMax}`}
            </div>
        </div>
    );
}
