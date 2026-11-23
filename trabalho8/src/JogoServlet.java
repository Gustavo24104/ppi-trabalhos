package yeardle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedHashMap; 
import java.util.Map;

@WebServlet("/jogo")
public class JogoServlet extends HttpServlet {

    private JogoDao dao;

    @Override
    public void init() {
        dao = new JogoDao();
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String acao = req.getParameter("acao");
        boolean sucesso = false;           
        String mensagem;
        Map<String, String> detalhes = new LinkedHashMap<>(); 

        try {
            int ano = Integer.parseInt(req.getParameter("ano"));

            if ("cadastrar".equals(acao) || "alterar".equals(acao)) {
                Jogo jogo = new Jogo();
                jogo.setAno(ano);
                jogo.setCategoria(vazioParaNulo(req.getParameter("categoria")));
                String data = req.getParameter("dataJogada");
                if (data != null && !data.isBlank()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Date.valueOf(data));
                    jogo.setDataJogada(calendar);
                }

                if ("cadastrar".equals(acao)) {
                    dao.adiciona(jogo);
                    sucesso = true;                                    
                    mensagem = "Jogo cadastrado com sucesso!";
                } else if (dao.altera(jogo)) {                        
                    sucesso = true;                                     
                    mensagem = "Jogo alterado com sucesso!";           
                } else {                                                
                    mensagem = "Nenhum jogo encontrado com esse ano."; 
                }                                                   
            } else if ("consultar".equals(acao)) {
                Jogo jogo = dao.busca(ano);
                if (jogo == null) {
                    mensagem = "Nenhum jogo encontrado com esse ano.";
                } else {
                    sucesso = true;                                                                              
                    mensagem = "Jogo encontrado com sucesso!";                                                
                    String data = jogo.getDataJogada() == null ? "-" : new Date(jogo.getDataJogada().getTimeInMillis()).toString();
                    detalhes.put("Ano", String.valueOf(jogo.getAno()));                                         
                    detalhes.put("Data jogada", data);                                                          
                    detalhes.put("Categoria", jogo.getCategoria() == null ? "-" : jogo.getCategoria());          
                }
            } else if ("remover".equals(acao)) {
                if (dao.remove(ano)) {                           
                    sucesso = true;                                    
                    mensagem = "Jogo removido com sucesso!";           
                } else {                                            
                    mensagem = "Nenhum jogo encontrado com esse ano."; 
                }                                                
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (IllegalArgumentException e) {
            mensagem = "Preencha os campos com valores válidos.";
        } catch (RuntimeException e) {
            mensagem = "Erro no banco de dados: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }

        req.setAttribute("status", sucesso ? "sucesso" : "erro");
        req.setAttribute("entidade", "Jogo");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", detalhes);
        req.setAttribute("paginaVoltar", "jogo.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }


    private static String vazioParaNulo(String s) {
        return s == null || s.isBlank() ? null : s;
    }
}