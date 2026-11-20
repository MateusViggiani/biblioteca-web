import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { livroService } from '../services/api';
import Avatar from './Avatar';

function LivroList() {
  const [livros, setLivros] = useState([]);
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  const carregar = () => {
    livroService.listar()
      .then((res) => setLivros(res.data))
      .catch(() => setErro('Erro ao carregar livros. O backend está rodando?'));
  };

  useEffect(carregar, []);

  const excluir = (id) => {
    if (!window.confirm('Deseja excluir este livro?')) return;
    livroService.deletar(id)
      .then(carregar)
      .catch(() => setErro('Erro ao excluir livro.'));
  };

  return (
    <div>
      <div className="cabecalho">
        <div>
          <h2>Livros</h2>
          <p className="subtitulo">{livros.length} {livros.length === 1 ? 'livro no acervo' : 'livros no acervo'}</p>
        </div>
        <Link to="/livros/novo" className="btn btn-primario">+ Novo livro</Link>
      </div>

      {erro && <p className="erro">{erro}</p>}

      <div className="card">
      <table>
        <thead>
          <tr>
            <th>Título</th>
            <th>Autor</th>
            <th>Ano</th>
            <th>Gênero</th>
            <th>ISBN</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {livros.length === 0 && (
            <tr><td colSpan="6" className="vazio">Nenhum livro cadastrado.</td></tr>
          )}
          {livros.map((livro) => (
            <tr key={livro.id}>
              <td className="titulo-livro">{livro.titulo}</td>
              <td>
                {livro.autor
                  ? <div className="pessoa"><Avatar nome={livro.autor.nome} pequeno />{livro.autor.nome}</div>
                  : '-'}
              </td>
              <td className="secundario">{livro.anoPublicacao || '-'}</td>
              <td>{livro.genero ? <span className="chip">{livro.genero}</span> : '-'}</td>
              <td className="secundario">{livro.isbn || '-'}</td>
              <td className="acoes">
                <button className="btn" onClick={() => navigate(`/livros/editar/${livro.id}`)}>Editar</button>
                <button className="btn btn-perigo" onClick={() => excluir(livro.id)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>
    </div>
  );
}

export default LivroList;
