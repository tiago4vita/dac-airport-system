import React, { useState } from 'react';
import axios from 'axios';
import './ConfirmarDeleteFunc.css';

const ConfirmarDeleteFunc = ({ funcionario, onClose, onSuccess }) => {
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleDelete = async () => {
    try {
      setLoading(true);
      setError(null);
      
      console.log('Funcionário a ser inativado:', funcionario);
      
      if (!funcionario.id && !funcionario.codigo) {
        throw new Error('ID do funcionário não encontrado');
      }
      
      // Try with id first, then codigo if id doesn't exist
      const funcionarioId = funcionario.id || funcionario.codigo;
      // deixei como patch para não deletar o funcionário, alterar para delete depois e tratar como PUT no gateway
      await axios.patch(`http://localhost:8080/funcionarios/${funcionarioId}`, { status: "INATIVO" });
      onSuccess();
    } catch (err) {
      console.error('Erro ao inativar funcionário:', err);
      setError('Ocorreu um erro ao inativar o funcionário. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Confirmar Inativação</h2>
        <p>Deseja realmente inativar o funcionário <strong>{funcionario.nome}</strong>?</p>
        <p>Esta ação irá alterar o status do funcionário para "INATIVO".</p>
        
        {error && <div className="error-message">{error}</div>}
        
        <div className="modal-buttons">
          <button 
            className="cancel-btn" 
            onClick={onClose}
            disabled={loading}
          >
            Cancelar
          </button>
          <button 
            className="confirm-btn" 
            onClick={handleDelete}
            disabled={loading}
          >
            {loading ? 'Processando...' : 'Confirmar'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmarDeleteFunc; 