package yeardle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/jogo")
public class JogoServlet extends HttpServlet {

    private JogoDao dao;
    private UploadDao arquivoDao;

    @Override
    public void init() {
        dao = new JogoDao();
        arquivoDao = new UploadDao();
    }

    @Override
    public void destroy() {
        try {
            dao.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao de JogoDao", e);
        }
        try {
            arquivoDao.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao de UploadDao", e);
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
                Jogo jogo = new Jogo();
                jogo.setAno(campos.inteiro("ano", "Ano"));
                validaAno(jogo.getAno());
                Campos.exige(dao.busca(jogo.getAno()) == null, "Já existe um jogo com esse ano.");
                if (campos.preenchido("dataJogada")) {
                    jogo.setDataJogada(converteData(campos.texto("dataJogada")));
                    validaDataLivre(jogo);
                }
                dao.adiciona(jogo);
                sucesso = true;
                mensagem = "Jogo cadastrado com sucesso!";
            } else if ("alterar".equals(acao)) {
                // só o ano é obrigatório: campo vazio mantém o valor salvo
                Jogo jogo = dao.busca(campos.inteiro("ano", "Ano"));
                if (jogo == null) {
                    mensagem = "Nenhum jogo encontrado com esse ano.";
                } else {
                    boolean mudou = false;
                    if (campos.apagar("dataJogada", "Data jogada")) {
                        jogo.setDataJogada(null);
                        mudou = true;
                    } else if (campos.preenchido("dataJogada")) {
                        jogo.setDataJogada(converteData(campos.texto("dataJogada")));
                        validaDataLivre(jogo);
                        mudou = true;
                    }

                    if (!mudou) {
                        mensagem = "Nenhuma alteração informada: preencha ao menos um campo ou marque \"apagar valor salvo\".";
                    } else if (dao.altera(jogo)) {
                        sucesso = true;
                        mensagem = "Jogo alterado com sucesso!";
                        Upload fotoJogo = arquivoDao.buscaPorJogo(jogo.getAno());
                        detalhes = detalhesDe(jogo, fotoJogo);
                    } else {
                        mensagem = "Nenhum jogo encontrado com esse ano.";
                    }
                }
            } else if ("consultar".equals(acao)) {
                Jogo jogo = dao.busca(campos.inteiro("ano", "Ano"));
                if (jogo == null) {
                    mensagem = "Nenhum jogo encontrado com esse ano.";
                } else {
                    sucesso = true;
                    mensagem = "Jogo encontrado com sucesso!";
                    Upload fotoJogo = arquivoDao.buscaPorJogo(jogo.getAno());
                    detalhes = detalhesDe(jogo, fotoJogo);
                }
            } else if ("remover".equals(acao)) {
                if (dao.remove(campos.inteiro("ano", "Ano"))) {
                    sucesso = true;
                    mensagem = "Jogo removido com sucesso!";
                } else {
                    mensagem = "Nenhum jogo encontrado com esse ano.";
                }
            } else if ("listar".equals(acao)) {
                lista = new ArrayList<>();
                Map<Integer, Upload> mapaJogo = arquivoDao.mapaPorJogo();
                for (Jogo jogo : dao.getLista()) {
                    lista.add(detalhesDe(jogo, mapaJogo.get(jogo.getAno())));
                }
                sucesso = true;
                mensagem = lista.isEmpty() ? "Nenhum jogo cadastrado." : lista.size() + " jogo(s) cadastrado(s).";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (Campos.CampoInvalidoException e) {
            mensagem = e.getMessage();
        } catch (IllegalArgumentException e) {
            // NumberFormatException (ano) e data invalida
            mensagem = "Preencha os campos com valores válidos (data no formato aaaa-mm-dd).";
        } catch (RuntimeException e) {
            // erros do banco, ex.: ano duplicado ou fora da regra (ano > 1900)
            log("Erro no banco de dados", e);
            mensagem = Campos.mensagemBanco(e, "ano", "Registro referenciado não existe.");
        }

        req.setAttribute("status", sucesso ? "sucesso" : "erro");
        req.setAttribute("entidade", "Jogo");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", detalhes);
        req.setAttribute("lista", lista);
        req.setAttribute("paginaVoltar", "jogo.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }

    private static void validaAno(int ano) {
        int atual = java.time.LocalDate.now().getYear();
        Campos.exige(ano > 1900 && ano <= atual, "O ano deve estar entre 1901 e " + atual + ".");
    }

    // um jogo por dia: a data nao pode ja pertencer a outro jogo
    private void validaDataLivre(Jogo jogo) {
        Jogo outro = dao.buscaPorDataJogada(jogo.getDataJogada());
        Campos.exige(outro == null || outro.getAno() == jogo.getAno(), "Já existe um jogo (ano " + (outro == null ? "" : outro.getAno()) + ") nessa data.");
    }

    // campos do jogo para a tabela da página de sucesso
    private static Map<String, String> detalhesDe(Jogo jogo, Upload fotoJogo) {
        Map<String, String> d = new LinkedHashMap<>();
        d.put("Ano", String.valueOf(jogo.getAno()));
        d.put("Data jogada", jogo.getDataJogada() == null ? "-" : new Date(jogo.getDataJogada().getTimeInMillis()).toString());
        d.put("Foto marcante (Dica Zero)", fotoJogo != null ? fotoJogo.getNomeOriginal() : "-");
        return d;
    }

    // "aaaa-mm-dd" do input date para Calendar
    private static Calendar converteData(String data) {
        if (!data.trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new Campos.CampoInvalidoException("Data jogada inválida: use o formato aaaa-mm-dd.");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.valueOf(data.trim()));
        return calendar;
    }
}
