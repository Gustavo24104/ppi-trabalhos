import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DicaDao {

    // a conexão com o banco de dados
    private Connection connection;

    public DicaDao() {
        this.connection = new ConnectionFactory().getConnection();
    }

    public void adiciona(Dica dica) {
        String sql = "INSERT INTO dica (textoDica, anoDica, dicaID) VALUES (?, ?, ?)";

        try {
            // prepared statement para inserção
            PreparedStatement stmt = connection.prepareStatement(sql);

            // seta os valores
            stmt.setString(1, dica.getTextoDica());
            stmt.setInt(2, dica.getAnoDica());
            stmt.setInt(3, dica.getDicaID());

            // executa
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Dica> getLista() {
        try {
            PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM dica");
            ResultSet rs = stmt.executeQuery();

            List<Dica> dicas = new ArrayList<Dica>();

            while (rs.next()) {
                // criando o objeto Dica
                Dica dica = new Dica();
                dica.setDicaID(rs.getInt("dicaID"));
                dica.setTextoDica(rs.getString("textoDica"));
                dica.setAnoDica(rs.getInt("anoDica"));

                // adicionando o objeto à lista
                dicas.add(dica);
            }

            rs.close();
            stmt.close();

            return dicas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void altera(Dica dica) {
        String sql = "UPDATE dica SET textoDica=? WHERE anoDica=1958";

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, dica.getTextoDica());
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        try {
            PreparedStatement stmt = connection.prepareStatement("DELETE FROM dica");
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
