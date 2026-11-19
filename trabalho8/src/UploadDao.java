import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadDao {

    private Connection connection;

    public UploadDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    // Insere ou substitui o arquivo do usuário no contexto informado;
    // retorna o nomeSalvo anterior caso tenha havido substituição (para limpeza em disco).
    public String salvaOuAtualiza(Upload arq) {
        Upload existente = buscaPorUsuarioEContexto(arq.getUserID(), arq.getContexto());
        try {
            if (existente == null) {
                String sql = "INSERT INTO upload (userID, contexto, nomeOriginal, nomeSalvo, tipoConteudo, tamanhoBytes) "
                           + "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setInt(1, arq.getUserID());
                stmt.setString(2, arq.getContexto());
                stmt.setString(3, arq.getNomeOriginal());
                stmt.setString(4, arq.getNomeSalvo());
                stmt.setString(5, arq.getTipoConteudo());
                stmt.setLong(6, arq.getTamanhoBytes());
                stmt.execute();
                stmt.close();
                return null;
            } else {
                String sql = "UPDATE upload SET nomeOriginal=?, nomeSalvo=?, tipoConteudo=?, tamanhoBytes=? "
                           + "WHERE userID=? AND contexto=?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, arq.getNomeOriginal());
                stmt.setString(2, arq.getNomeSalvo());
                stmt.setString(3, arq.getTipoConteudo());
                stmt.setLong(4, arq.getTamanhoBytes());
                stmt.setInt(5, arq.getUserID());
                stmt.setString(6, arq.getContexto());
                stmt.executeUpdate();
                stmt.close();
                return existente.getNomeSalvo();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Upload buscaPorUsuarioEContexto(int userID, String contexto) {
        String sql = "SELECT * FROM upload WHERE userID=? AND contexto=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userID);
            stmt.setString(2, contexto);
            ResultSet rs = stmt.executeQuery();

            Upload arq = null;
            if (rs.next()) {
                arq = monta(rs);
            }
            rs.close();
            stmt.close();
            return arq;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Mapeia userID -> Upload para um dado contexto ("perfil" ou "partida")
    public Map<Integer, Upload> mapaPorContexto(String contexto) {
        String sql = "SELECT * FROM upload WHERE contexto=? ORDER BY userID";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, contexto);
            ResultSet rs = stmt.executeQuery();

            Map<Integer, Upload> mapa = new HashMap<>();
            while (rs.next()) {
                Upload arq = monta(rs);
                mapa.put(arq.getUserID(), arq);
            }
            rs.close();
            stmt.close();
            return mapa;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Upload> listaPorUsuario(int userID) {
        String sql = "SELECT * FROM upload WHERE userID=? ORDER BY contexto";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            List<Upload> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(monta(rs));
            }
            rs.close();
            stmt.close();
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fechaConexao() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private Upload monta(ResultSet rs) throws SQLException {
        Upload arq = new Upload();
        arq.setArquivoID(rs.getInt("arquivoID"));
        arq.setUserID(rs.getInt("userID"));
        arq.setContexto(rs.getString("contexto"));
        arq.setNomeOriginal(rs.getString("nomeOriginal"));
        arq.setNomeSalvo(rs.getString("nomeSalvo"));
        arq.setTipoConteudo(rs.getString("tipoConteudo"));
        arq.setTamanhoBytes(rs.getLong("tamanhoBytes"));
        arq.setDataUpload(rs.getTimestamp("dataUpload"));
        return arq;
    }
}