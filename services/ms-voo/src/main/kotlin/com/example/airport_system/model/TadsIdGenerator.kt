package com.tads.airport_system.ms_voo.generator

import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.io.Serializable
import java.sql.Connection
import java.sql.SQLException

class TadsIdGenerator : IdentifierGenerator {
    override fun generate(session: SharedSessionContractImplementor, entity: Any): Serializable {
        val connection: Connection = session.jdbcConnectionAccess.obtainConnection()
        
        try {
            // Consulta para obter o próximo número da sequência
            val sql = "SELECT NEXT VALUE FOR hibernate_sequence"
            val statement = connection.prepareStatement(sql)
            val resultSet = statement.executeQuery()
            
            if (resultSet.next()) {
                val nextValue = resultSet.getLong(1)
                return "TADS${String.format("%06d", nextValue)}"
            }
            
            // Se não conseguir obter o próximo valor, gera um baseado no timestamp
            val timestamp = System.currentTimeMillis()
            return "TADS${timestamp}"
        } catch (e: SQLException) {
            // Em caso de erro, usa timestamp como fallback
            val timestamp = System.currentTimeMillis()
            return "TADS${timestamp}"
        } finally {
            try {
                session.jdbcConnectionAccess.releaseConnection(connection)
            } catch (e: SQLException) {
                throw HibernateException("Não foi possível liberar a conexão JDBC", e)
            }
        }
    }
}