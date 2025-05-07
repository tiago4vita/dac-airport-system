import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './ListarFunc.css';
import { SideMenuFunc } from '../SideMenuFunc/SideMenuFunc';
import { Link } from 'react-router-dom';

const ListarFunc = () => {
  const [funcionarios, setFuncionarios] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(6);

  useEffect(() => {
    const fetchFuncionarios = async () => {
      try {
        const response = await axios.get('http://localhost:8080/funcionarios');
        setFuncionarios(response.data);
      } catch (error) {
        console.error('Erro ao buscar funcionários:', error);
      }
    };

    fetchFuncionarios();
  }, []);

  // Get current items
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = funcionarios.slice(indexOfFirstItem, indexOfLastItem);

  // Change page
  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  const pageNumbers = [];
  for (let i = 1; i <= Math.ceil(funcionarios.length / itemsPerPage); i++) {
    pageNumbers.push(i);
  }

  return (
    <div className="listar-func-container">
      <SideMenuFunc />
      
      <div className="conteudo">
        <section class="etiqueta-funcionario">
            <h2>FUNCIONÁRIO</h2>
        </section>   

        <section class="lista-funcionarios">
            <h2>Lista de Funcionários</h2>
            <p>Aqui você pode ver todos os funcionários cadastrados no sistema e gerenciá-los!</p> 
            <Link to="/inserirfunc" className="add-btn">+ Novo Funcionário</Link>  
        </section>

        <div className="funcionarios-section">
          
          
          <table className="funcionarios-table">
            <thead>
              <tr>
                <th>NOME</th>
                <th>CPF</th>
                <th>EMAIL</th>
                <th>TELEFONE</th>
                <th>AÇÕES</th>
              </tr>
            </thead>
            <tbody>
              {currentItems.map((funcionario, index) => (
                <tr key={index}>
                  <td>{funcionario.nome}</td>
                  <td>{funcionario.cpf || '111.222.333-00'}</td>
                  <td>{funcionario.email}</td>
                  <td>{funcionario.telefone || '(419) 9999-9999'}</td>
                  <td className="actions">
                    <button className="edit-btn">Editar</button>
                    <button className="remove-btn">Remover</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          
          <div className="pagination">
            <button 
              className="pagination-arrow" 
              onClick={() => currentPage > 1 && paginate(currentPage - 1)}
              disabled={currentPage === 1}
            >
              &lt;
            </button>
            
            {pageNumbers.map(number => (
              <button
                key={number}
                className={`pagination-number ${currentPage === number ? 'active' : ''}`}
                onClick={() => paginate(number)}
              >
                {number}
              </button>
            ))}
            
            <button 
              className="pagination-arrow" 
              onClick={() => currentPage < pageNumbers.length && paginate(currentPage + 1)}
              disabled={currentPage === pageNumbers.length}
            >
              &gt;
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ListarFunc;

