package br.com.fiap.rottracker.exception;

import java.sql.SQLException;

public class SqlExceptionMapper {

    public static RuntimeException map(SQLException e)
    {
        switch (e.getErrorCode()) {

            case 1:
                return new ConflictException(
                        "Registro já existente no banco de dados.");

            case 2292:
                return new ConflictException(
                        "Não é possível excluir este registro, pois existem dados relacionados.");

            case 2291:
                return new BadRequestException(
                        "Um dos registros relacionados não existe.");

            case 1400:
                return new BadRequestException(
                        "Um campo obrigatório não foi informado.");

            case 12899:
                return new BadRequestException(
                        "Um dos campos ultrapassou o tamanho permitido.");

            default:
                return new RuntimeException(
                        "Erro interno ao acessar o banco de dados.", e);
        }
    }
}