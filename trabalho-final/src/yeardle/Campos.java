package yeardle;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// lê os campos do formulário para os servlets.
// na alteração: campo vazio mantém o valor salvo; para gravar NULL, o usuário
// marca o checkbox "apagar valor salvo" (name="limpar", value=nome do campo)
public class Campos {

    private final HttpServletRequest req;
    private final Set<String> limpar = new HashSet<>();

    public Campos(HttpServletRequest req) {
        this.req = req;
        // vários checkboxes com o mesmo name: getParameterValues (slide 39)
        String[] marcados = req.getParameterValues("limpar");
        if (marcados != null) {
            limpar.addAll(Arrays.asList(marcados));
        }
    }

    public String texto(String campo) {
        return req.getParameter(campo);
    }

    public boolean preenchido(String campo) {
        String valor = req.getParameter(campo);
        return valor != null && !valor.isBlank();
    }

    // lê um número inteiro; se for inválido (vazio, texto ou grande demais), a mensagem nomeia o campo
    public int inteiro(String campo, String rotulo) {
        String valor = req.getParameter(campo);
        try {
            return Integer.parseInt(valor == null ? "" : valor.trim());
        } catch (NumberFormatException e) {
            throw new CampoInvalidoException("O campo '" + rotulo + "' deve ser um número inteiro válido.");
        }
    }

    // lança erro de preenchimento se a condição de validação falhar
    public static void exige(boolean valido, String mensagem) {
        if (!valido) {
            throw new CampoInvalidoException(mensagem);
        }
    }

    // traduz erro do banco (RuntimeException que embrulha SQLException) em mensagem amigável
    public static String mensagemBanco(RuntimeException e) {
        return mensagemBanco(e, "dados", "O registro referenciado não existe.");
    }

    // duplicado: nome dos campos que conflitaram; referencia: mensagem da chave estrangeira (1452)
    public static String mensagemBanco(RuntimeException e, String duplicado, String referencia) {
        Throwable causa = e.getCause() != null ? e.getCause() : e;
        if (causa instanceof java.sql.SQLException) {
            java.sql.SQLException sql = (java.sql.SQLException) causa;
            int codigo = sql.getErrorCode();
            if (codigo == 1062) {
                return "Já existe um registro com esse " + duplicado + ".";
            }
            if (codigo == 1452) {
                return referencia;
            }
            if (codigo == 1451) {
                return "Este registro não pode ser removido porque está sendo usado por outros dados.";
            }
            if (codigo == 1265 || codigo == 1366) {
                return "Valor inválido para a categoria.";
            }
            if (codigo == 3819 || codigo == 4025) {
                return "Algum valor informado não respeita as regras do banco (ex.: ano maior que 1900).";
            }
            if (sql instanceof java.sql.SQLIntegrityConstraintViolationException) {
                return "Os dados informados violam uma regra de integridade do banco.";
            }
        }
        return "Não foi possível concluir a operação no banco de dados.";
    }

    // true se o usuário marcou "apagar valor salvo" para o campo
    public boolean apagar(String campo, String rotulo) {
        if (!limpar.contains(campo)) {
            return false;
        }
        if (preenchido(campo)) {
            throw new CampoInvalidoException(rotulo + ": preencha um novo valor ou marque \"apagar valor salvo\", não os dois.");
        }
        return true;
    }

    // erro de preenchimento com mensagem para mostrar ao usuário
    public static class CampoInvalidoException extends RuntimeException {
        public CampoInvalidoException(String mensagem) {
            super(mensagem);
        }
    }
}
