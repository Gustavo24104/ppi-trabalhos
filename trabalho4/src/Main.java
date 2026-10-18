import java.sql.SQLException;

public class Main {
    static void main() {
        BancoDeDados bd = new BancoDeDados();

        try {
            bd.insereDados();
            bd.imprimeDados();
            bd.alteraDados();

            System.out.println("\n======================================================Dados Alterados!======================================================\n");

            bd.imprimeDados();
            bd.deletaDados();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
