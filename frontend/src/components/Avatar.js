import React from 'react';

const cores = ['#6366f1', '#ec4899', '#14b8a6', '#f59e0b', '#8b5cf6', '#0ea5e9', '#ef4444', '#22c55e'];

function Avatar({ nome = '', pequeno = false }) {
  const partes = nome.split(' ').filter(Boolean);
  const iniciais = (partes.length > 1 ? partes[0][0] + partes[partes.length - 1][0] : (partes[0] || '').slice(0, 2)).toUpperCase();
  const cor = cores[[...nome].reduce((soma, c) => soma + c.charCodeAt(0), 0) % cores.length];
  return (
    <span className={`avatar ${pequeno ? 'pequeno' : ''}`} style={{ background: cor }}>
      {iniciais || '?'}
    </span>
  );
}

export default Avatar;
