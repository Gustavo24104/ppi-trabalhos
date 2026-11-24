package yeardle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/dica")
public class DicaServlet extends HttpServlet {

    private DicaDao dao;

    @Override
    public void init() {
        dao = new DicaDao();
    }

    @Override
    public void destroy() {
        try {
            dao.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        super.service(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String acao = req.getParameter("acao");
        Campos campos = new Campos(req);
        boolean sucesso = false;
        String mensagem;
        Map<String, String> detalhes = new LinkedHashMap<>();
        List<Map<String, String>> lista = null;

        try {
            if ("cadastrar".equals(acao)) {
                Dica dica = new Dica();
                dica.setTextoDica(campos.texto("textoDica"));
                dica.setAnoDica(campos.inteiro("anoDica"));
                if (campos.preenchido("dicaID")) {
                    dica.setDicaID(campos.inteiro("dicaID"));
                }
                dica.setCategoria(campos.texto("categoria"));
                dao.adiciona(dica);
                sucesso = true;
                mensagem = "Dica cadastrada com sucesso!";
            } else if ("alterar".equals(acao)) {
                // só o ID é obrigatório: campo vazio mantém o valor salvo
                Dica dica = dao.busca(campos.inteiro("dicaID"));
                if (dica == null) {
                    mensagem = "Nenhuma dica encontrada com esse ID.";
                } else {
                    boolean mudou = false;
                    // textoDica é NOT NULL no banco: não tem a opção de apagar
                    if (campos.preenchido("textoDica")) {
                        dica.setTextoDica(campos.texto("textoDica"));
                        mudou = true;
                    }
                    if (campos.apagar("anoDica", "Ano do jogo")) {
                        dica.setAnoDica(0); // o DAO grava 0 como NULL
                        mudou = true;
                    } else if (campos.preenchido("anoDica")) {
                        dica.setAnoDica(campos.inteiro("anoDica"));
                        mudou = true;
                    } else if(campos.preenchido("categoria")) {
                        dica.setCategoria(campos.texto("categoria"));
                        mudou = true;
                    }

                    if (!mudou) {
                        mensagem = "Nenhuma alteração informada: preencha ao menos um campo ou marque \"apagar valor salvo\".";
                    } else if (dao.altera(dica)) {
                        sucesso = true;
                        mensagem = "Dica alterada com sucesso!";
                        detalhes = detalhesDe(dica);
                    } else {
                        mensagem = "Nenhuma dica encontrada com esse ID.";
                    }
                }
            } else if ("consultar".equals(acao)) {
                Dica dica = dao.busca(campos.inteiro("dicaID"));
                if (dica == null) {
                    mensagem = "Nenhuma dica encontrada com esse ID.";
                } else {
                    sucesso = true;
                    mensagem = "Dica encontrada com sucesso!";
                    detalhes = detalhesDe(dica);
                }
            } else if ("remover".equals(acao)) {
                if (dao.remove(campos.inteiro("dicaID"))) {
                    sucesso = true;
                    mensagem = "Dica removida com sucesso!";
                } else {
                    mensagem = "Nenhuma dica encontrada com esse ID.";
                }
            } else if ("listar".equals(acao)) {
                lista = new ArrayList<>();
                for (Dica dica : dao.getLista()) {
                    lista.add(detalhesDe(dica));
                }
                sucesso = true;
                mensagem = lista.isEmpty() ? "Nenhuma dica cadastrada." : lista.size() + " dica(s) cadastrada(s).";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (Campos.CampoInvalidoException e) {
            mensagem = e.getMessage();
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: ano inexistente em jogo (chave estrangeira) ou ID duplicado
            mensagem = "Erro no banco de dados: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }

        req.setAttribute("status", sucesso ? "sucesso" : "erro");
        req.setAttribute("entidade", "Dica");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", detalhes);
        req.setAttribute("lista", lista);
        req.setAttribute("paginaVoltar", "dica.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }

    // campos da dica para a tabela da página de sucesso
    private static Map<String, String> detalhesDe(Dica dica) {
        Map<String, String> d = new LinkedHashMap<>();
        d.put("ID", String.valueOf(dica.getDicaID()));
        d.put("Texto", dica.getTextoDica());
        d.put("Ano", dica.getAnoDica() > 0 ? String.valueOf(dica.getAnoDica()) : "-");
        d.put("Categoria", dica.getCategoria() == null ? "-" : dica.getCategoria());
        return d;
    }
}
