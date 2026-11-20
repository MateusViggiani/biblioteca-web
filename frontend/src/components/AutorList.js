import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { autorService } from '../services/api';
import Avatar from './Avatar';

function AutorList() {
  const [autores, setAutores] = useState([]);
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const carregar = () => {
    autorService.listar()
      .then((res) => setAutores(res.data))
      .catch(() => setErro('Erro ao carregar autores. O backend está rodando?'));
  };

  useEffect(carregar, []);

  const excluir = (id) => {
    if (!window.confirm('Deseja excluir este autor?')) return;
    autorService.deletar(id)
      .then(carregar)
      .catch((err) => setErro(err.response?.data?.erro || 'Erro ao excluir autor.'));
  };

  const formatarData = (data) => (data ? data.split('-').reverse().join('/') : '-');

  return (
    <div>
      <div className="cabecalho">
        <div>
          <h2>Autores</h2>
          <p className="subtitulo">{autores.length} {autores.length === 1 ? 'autor cadastrado' : 'autores cadastrados'}</p>
        </div>
        <Link to="/autores/novo" className="btn btn-primario">+ Novo autor</Link>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <div className="card">
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>Nacionalidade</th>
            <th>Nascimento</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {autores.length === 0 && (
            <tr><td colSpan="4" className="vazio">Nenhum autor cadastrado.</td></tr>
          )}
          {autores.map((autor) => (
            <tr key={autor.id}>
              <td>
                <div className="pessoa"><Avatar nome={autor.nome} />{autor.nome}</div>
              </td>
              <td className="secundario">{autor.nacionalidade || '-'}</td>
              <td className="secundario">{formatarData(autor.dataNascimento)}</td>
              <td className="acoes">
                <button className="btn" onClick={() => navigate(`/autores/editar/${autor.id}`)}>Editar</button>
                <button className="btn btn-perigo" onClick={() => excluir(autor.id)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>
    </div>
  );
}

export default AutorList;
