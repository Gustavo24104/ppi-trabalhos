import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    // a conexão com o banco de dados
    private Connection connection;

    public UsuarioDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Usuario usuario) {
        String sql = "INSERT INTO users (pontosTotais, nomeReal, nickname, email, " +
                "cidade, anoFav, categoriaFav, userID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // prepared statement para inserção
            PreparedStatement stmt = connection.prepareStatement(sql);

            // seta os valores
            stmt.setInt(1, usuario.getPontosTotais());
            stmt.setString(2, usuario.getNomeReal());
            stmt.setString(3, usuario.getNickname());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getCidade());
            stmt.setInt(6, usuario.getAnoFav());
            stmt.setString(7, usuario.getCategoriaFav());
            stmt.setInt(8, usuario.getUserID());

            // executa
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Usuario> getLista() {
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM users");
            ResultSet rs = stmt.executeQuery();

            List<Usuario> usuarios = new ArrayList<Usuario>();

            while (rs.next()) {
                // criando o objeto Usuario
                Usuario usuario = new Usuario();
                usuario.setUserID(rs.getInt("userID"));
                usuario.setPontosTotais(rs.getInt("pontosTotais"));
                usuario.setNomeReal(rs.getString("nomeReal"));
                usuario.setNickname(rs.getString("nickname"));
                usuario.setEmail(rs.getString("email"));
                usuario.setCidade(rs.getString("cidade"));
                usuario.setAnoFav(rs.getInt("anoFav"));
                usuario.setCategoriaFav(rs.getString("categoriaFav"));

                // adicionando o objeto à lista
                usuarios.add(usuario);
            }

            rs.close();
            stmt.close();

            return usuarios;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void altera(Usuario usuario) {
        String sql = "UPDATE users SET categoriaFav=?, nickname=? WHERE userID=1";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, usuario.getCategoriaFav());
            stmt.setString(2, usuario.getNickname());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM users");
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void fechaConexao() throws SQLException {
        connection.close();
    }
}
