import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { livroService } from '../services/api';

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
        <h2>Livros</h2>
        <Link to="/livros/novo" className="btn btn-primario">+ Novo livro</Link>
      </div>

      {erro && <p className="erro">{erro}</p>}

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
              <td>{livro.titulo}</td>
              <td>{livro.autor ? livro.autor.nome : '-'}</td>
              <td>{livro.anoPublicacao || '-'}</td>
              <td>{livro.genero || '-'}</td>
              <td>{livro.isbn || '-'}</td>
              <td className="acoes">
                <button className="btn" onClick={() => navigate(`/livros/editar/${livro.id}`)}>Editar</button>
                <button className="btn btn-perigo" onClick={() => excluir(livro.id)}>Excluir</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LivroList;
