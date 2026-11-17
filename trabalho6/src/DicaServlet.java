import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String acao = req.getParameter("acao");
        boolean sucesso = false;                             
        String mensagem;
        Map<String, String> detalhes = new LinkedHashMap<>(); 

        try {
            if ("cadastrar".equals(acao)) {
                Dica dica = new Dica();
                dica.setTextoDica(req.getParameter("textoDica"));
                dica.setAnoDica(Integer.parseInt(req.getParameter("anoDica")));
                String id = req.getParameter("dicaID");
                if (id != null && !id.isBlank()) {
                    dica.setDicaID(Integer.parseInt(id));
                }
                dao.adiciona(dica);
                sucesso = true;                              
                mensagem = "Dica cadastrada com sucesso!";
            } else if ("alterar".equals(acao)) {
                Dica dica = new Dica();
                dica.setDicaID(Integer.parseInt(req.getParameter("dicaID")));
                dica.setTextoDica(req.getParameter("textoDica"));
                dica.setAnoDica(Integer.parseInt(req.getParameter("anoDica")));
                if (dao.altera(dica)) {                                
                    sucesso = true;                                     
                    mensagem = "Dica alterada com sucesso!";           
                } else {                                               
                    mensagem = "Nenhuma dica encontrada com esse ID.";  
                }                                                      
            } else if ("consultar".equals(acao)) {
                Dica dica = dao.busca(Integer.parseInt(req.getParameter("dicaID")));
                if (dica == null) {
                    mensagem = "Nenhuma dica encontrada com esse ID.";
                } else {
                    sucesso = true;                                         
                    mensagem = "Dica encontrada com sucesso!";             
                    detalhes.put("ID", String.valueOf(dica.getDicaID()));  
                    detalhes.put("Texto", dica.getTextoDica());            
                    detalhes.put("Ano", String.valueOf(dica.getAnoDica()));
                }
            } else if ("remover".equals(acao)) {
                if (dao.remove(Integer.parseInt(req.getParameter("dicaID")))) { 
                    sucesso = true;                                           
                    mensagem = "Dica removida com sucesso!";                  
                } else {                                                      
                    mensagem = "Nenhuma dica encontrada com esse ID.";          
                }                                                              
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            mensagem = "Erro no banco de dados: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }

        req.setAttribute("status", sucesso ? "sucesso" : "erro");
        req.setAttribute("entidade", "Dica");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", detalhes);
        req.setAttribute("paginaVoltar", "dica.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }
}