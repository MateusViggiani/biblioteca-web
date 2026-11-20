import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { autorService } from '../services/api';

const vazio = { nome: '', nacionalidade: '', dataNascimento: '' };

function AutorForm() {
  const [autor, setAutor] = useState(vazio);
  const [erro, setErro] = useState('');
  const { id } = useParams();
  const navigate = useNavigate();
  const editando = Boolean(id);

  useEffect(() => {
    if (editando) {
      autorService.buscar(id)
        .then((res) => setAutor({ ...vazio, ...res.data, nacionalidade: res.data.nacionalidade || '', dataNascimento: res.data.dataNascimento || '' }))
        .catch(() => setErro('Autor não encontrado.'));
    }
  }, [id, editando]);

  const alterar = (e) => setAutor({ ...autor, [e.target.name]: e.target.value });

  const salvar = (e) => {
    e.preventDefault();
    const dados = {
      nome: autor.nome,
      nacionalidade: autor.nacionalidade || null,
      dataNascimento: autor.dataNascimento || null,
    };
    const requisicao = editando ? autorService.atualizar(id, dados) : autorService.criar(dados);
    requisicao
      .then(() => navigate('/autores'))
      .catch(() => setErro('Erro ao salvar autor. Verifique os campos.'));
  };

  return (
    <div>
      <h2>{editando ? 'Editar autor' : 'Novo autor'}</h2>
      {erro && <p className="erro">{erro}</p>}

      <form onSubmit={salvar} className="formulario">
        <label>
          Nome *
          <input name="nome" value={autor.nome} onChange={alterar} maxLength="100" required />
        </label>
        <label>
          Nacionalidade
          <input name="nacionalidade" value={autor.nacionalidade} onChange={alterar} maxLength="50" />
        </label>
        <label>
          Data de nascimento
          <input type="date" name="dataNascimento" value={autor.dataNascimento} onChange={alterar} />
        </label>

        <div className="acoes">
          <button type="submit" className="btn btn-primario">Salvar</button>
          <button type="button" className="btn" onClick={() => navigate('/autores')}>Cancelar</button>
        </div>
      </form>
    </div>
  );
}

export default AutorForm;
