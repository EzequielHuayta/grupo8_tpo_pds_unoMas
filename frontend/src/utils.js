export function estadoBadge(estado) {
    const map = {
        'Necesitamos jugadores': 'badge-orange',
        'Partido armado': 'badge-yellow',
        'Confirmado': 'badge-indigo',
        'En juego': 'badge-green',
        'Finalizado': 'badge-green',
        'Cancelado': 'badge-red',
    };
    return map[estado] || 'badge-gray';
}

export function nivelBadge(nivel) {
    const map = { Principiante: 'badge-green', Intermedio: 'badge-blue', Avanzado: 'badge-purple' };
    return map[nivel] || 'badge-gray';
}

export function initials(name = '') {
    return name.split(' ').map(w => w[0]).join('').toUpperCase().slice(0, 2);
}
