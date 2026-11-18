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

    public int inteiro(String campo) {
        return Integer.parseInt(req.getParameter(campo));
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
