import java.sql.*;

public class UsuarioDao {

    // a conexão com o banco de dados
    private Connection connection;

    public UsuarioDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Usuario usuario) {
        // userID <= 0: deixa o AUTO_INCREMENT gerar o valor
        boolean semId = usuario.getUserID() <= 0;
        String sql = "INSERT INTO users (pontosTotais, nomeReal, nickname, email, cidade, anoFav, categoriaFav"
                + (semId ? ") VALUES (?, ?, ?, ?, ?, ?, ?)" : ", userID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        try {
            // prepared statement para inserção
            PreparedStatement stmt = connection.prepareStatement(sql);

            // seta os valores
            preenche(stmt, usuario);
            if (!semId) {
                stmt.setInt(8, usuario.getUserID());
            }

            // executa
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Usuario busca(int userID) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE userID=?");
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            Usuario usuario = null;
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setUserID(rs.getInt("userID"));
                usuario.setPontosTotais(rs.getInt("pontosTotais"));
                usuario.setNomeReal(rs.getString("nomeReal"));
                usuario.setNickname(rs.getString("nickname"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCidade(rs.getString("cidade"));
                usuario.setAnoFav(rs.getInt("anoFav"));
                usuario.setCategoriaFav(rs.getString("categoriaFav"));
            }

            rs.close();
            stmt.close();
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi alterado
    public boolean altera(Usuario usuario) {
        String sql = "UPDATE users SET pontosTotais=?, nomeReal=?, nickname=?, email=?, cidade=?, "
                + "anoFav=?, categoriaFav=? WHERE userID=?";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            preenche(stmt, usuario);
            stmt.setInt(8, usuario.getUserID());
            int linhas = stmt.executeUpdate();
            stmt.close();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // retorna true se algum registro foi removido
    public boolean remove(int userID) {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE userID=?");
            stmt.setInt(1, userID);
            int linhas = stmt.executeUpdate();
            stmt.close();
            return linhas > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fechaConexao() throws SQLException {
        connection.close();
    }

    // anoFav e categoriaFav são opcionais: sem valor, gravam NULL
    private void preenche(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setInt(1, usuario.getPontosTotais());
        stmt.setString(2, usuario.getNomeReal());
        stmt.setString(3, usuario.getNickname());
        stmt.setString(4, usuario.getEmail());
        stmt.setString(5, usuario.getCidade());
        if (usuario.getAnoFav() > 0) {
            stmt.setInt(6, usuario.getAnoFav());
        } else {
            stmt.setNull(6, Types.INTEGER);
        }
        stmt.setString(7, usuario.getCategoriaFav());
    }
}
