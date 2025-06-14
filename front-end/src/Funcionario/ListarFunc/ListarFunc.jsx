import React, { useState, useEffect } from 'react';
import axiosInstance from '../../api/axiosInstance'; // ajuste o caminho conforme a sua estrutura
import './ListarFunc.css';
import { SideMenuFunc } from '../SideMenuFunc/SideMenuFunc';
import { Link, useNavigate } from 'react-router-dom';
import ConfirmarDeleteFunc from './ConfirmarDeleteFunc';
import AlterarFunc from '../AlterarFunc/AlterarFunc';

const ListarFunc = () => {
  const [funcionarios, setFuncionarios] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(6);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedFuncionario, setSelectedFuncionario] = useState(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedFuncionarioToEdit, setSelectedFuncionarioToEdit] = useState(null);
  const navigate = useNavigate();

  const fetchFuncionarios = async () => {
    try {
      setLoading(true);
      const response = await axiosInstance.get('/funcionarios');
      setFuncionarios(response.data || []);
      setError(null);
    } catch (error) {
      console.error('Erro ao buscar funcionários:', error);
      setError('Erro ao carregar dados. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFuncionarios();
  }, []);

  const formatCpf = (cpf) => {
    if (!cpf) return '111.222.333-00';
    if (cpf.includes('.') || cpf.includes('-')) return cpf;
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  };

  const formatPhone = (phone) => {
    if (!phone) return '(419) 9999-9999';
    if (phone.includes('(') || phone.includes(')')) return phone;

    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 11) {
      return cleaned.replace(/(\d{2})(\d{1})(\d{4})(\d{4})/, '($1) $2$3-$4');
    } else if (cleaned.length === 10) {
      return cleaned.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    }

    return phone;
  };

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = funcionarios.slice(indexOfFirstItem, indexOfLastItem);

  const paginate = (pageNumber) => setCurrentPage(pageNumber);

  const pageNumbers = [];
  for (let i = 1; i <= Math.ceil(funcionarios.length / itemsPerPage); i++) {
    pageNumbers.push(i);
  }

  const handleDeleteClick = (funcionario) => {
    setSelectedFuncionario(funcionario);
    setShowDeleteModal(true);
  };

  const handleCloseModal = () => {
    setShowDeleteModal(false);
    setSelectedFuncionario(null);
  };

  const handleDeleteSuccess = () => {
    setShowDeleteModal(false);
    setSelectedFuncionario(null);
    fetchFuncionarios();
    navigate('/listarfunc');
  };

  const handleEditClick = (funcionario) => {
    setSelectedFuncionarioToEdit(funcionario);
    setShowEditModal(true);
  };

  const handleCloseEditModal = () => {
    setShowEditModal(false);
    setSelectedFuncionarioToEdit(null);
  };

  const handleEditSuccess = () => {
    setShowEditModal(false);
    setSelectedFuncionarioToEdit(null);
    fetchFuncionarios();
  };

  return (
    <div className="listar-func-container">
      <SideMenuFunc />

      <div className="conteudo">
        <section className="etiqueta-funcionario">
          <h2>FUNCIONÁRIO</h2>
        </section>

        <section className="lista-funcionarios">
          <h2>Lista de Funcionários</h2>
          <p>Aqui você pode ver todos os funcionários cadastrados no sistema e gerenciá-los!</p>
          <Link to="/inserirfunc" className="add-btn">+ Novo Funcionário</Link>
        </section>

        <div className="funcionarios-section">
          {loading ? (
            <div className="loading-spinner">Carregando...</div>
          ) : error ? (
            <div className="error-message">{error}</div>
          ) : funcionarios.length === 0 ? (
            <div className="no-data-message">
              <p>Nenhum funcionário cadastrado.</p>
              <Link to="/inserirfunc" className="add-btn-empty">Adicionar Funcionário</Link>
            </div>
          ) : (
            <>
              <table className="funcionarios-table">
                <thead>
                  <tr>
                    <th>NOME</th>
                    <th>CPF</th>
                    <th>EMAIL</th>
                    <th>TELEFONE</th>
                    <th>STATUS</th>
                    <th>AÇÕES</th>
                  </tr>
                </thead>
                <tbody>
                  {currentItems.map((funcionario, index) => (
                    <tr key={index}>
                      <td>{funcionario.nome}</td>
                      <td>{formatCpf(funcionario.cpf)}</td>
                      <td>{funcionario.email}</td>
                      <td>{formatPhone(funcionario.telefone)}</td>
                      <td>
                        <span className={`status-badge ${funcionario.status === 'ATIVO' ? 'status-active' : 'status-inactive'}`}>
                          {funcionario.status || 'ATIVO'}
                        </span>
                      </td>
                      <td className="actions">
                        <button
                          className="edit-btn"
                          onClick={() => handleEditClick(funcionario)}
                        >
                          Editar
                        </button>
                        <button
                          className="remove-btn"
                          onClick={() => handleDeleteClick(funcionario)}
                        >
                          Remover
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {funcionarios.length > itemsPerPage && (
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
              )}
            </>
          )}
        </div>
      </div>

      {showDeleteModal && selectedFuncionario && (
        <ConfirmarDeleteFunc
          funcionario={selectedFuncionario}
          onClose={handleCloseModal}
          onSuccess={handleDeleteSuccess}
        />
      )}

      {showEditModal && selectedFuncionarioToEdit && (
        <AlterarFunc
          funcionario={selectedFuncionarioToEdit}
          onClose={handleCloseEditModal}
          onSuccess={handleEditSuccess}
        />
      )}
    </div>
  );
};

export default ListarFunc;
