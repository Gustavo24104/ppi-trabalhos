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

@WebServlet("/usuario")
public class UsuarioServlet extends HttpServlet {

    private UsuarioDao dao;
    private UploadDao arquivoDao;

    @Override
    public void init() {
        dao = new UsuarioDao();
        arquivoDao = new UploadDao();
    }

    @Override
    public void destroy() {
        try {
            dao.fechaConexao();
        } catch (Exception e) {
            log("Erro ao fechar a conexao de UsuarioDao", e);
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
                Usuario usuario = new Usuario();
                usuario.setPontosTotais(campos.inteiro("pontosTotais"));
                usuario.setNomeReal(campos.texto("nomeReal"));
                usuario.setNickname(campos.texto("nickname"));
                usuario.setEmail(campos.texto("email"));
                usuario.setCidade(campos.texto("cidade"));
                usuario.setSenha(campos.preenchido("senha") ? campos.texto("senha") : null);
                usuario.setEhAdmin(req.getParameter("ehAdmin") != null);
                if (campos.preenchido("anoFav")) {
                    usuario.setAnoFav(campos.inteiro("anoFav"));
                }
                usuario.setCategoriaFav(campos.preenchido("categoriaFav") ? campos.texto("categoriaFav") : null);
                if (campos.preenchido("userID")) {
                    usuario.setUserID(campos.inteiro("userID"));
                }
                dao.adiciona(usuario);
                sucesso = true;
                mensagem = "Usuário cadastrado com sucesso!";
            } else if ("alterar".equals(acao)) {
                // só o ID é obrigatório: campo vazio mantém o valor salvo
                Usuario usuario = dao.busca(campos.inteiro("userID"));
                if (usuario == null) {
                    mensagem = "Nenhum usuário encontrado com esse ID.";
                } else {
                    boolean mudou = false;
                    // campos NOT NULL no banco: só trocam de valor, não podem ser apagados
                    if (campos.preenchido("pontosTotais")) {
                        usuario.setPontosTotais(campos.inteiro("pontosTotais"));
                        mudou = true;
                    }
                    if (campos.preenchido("nomeReal")) {
                        usuario.setNomeReal(campos.texto("nomeReal"));
                        mudou = true;
                    }
                    if (campos.preenchido("nickname")) {
                        usuario.setNickname(campos.texto("nickname"));
                        mudou = true;
                    }
                    if (campos.preenchido("email")) {
                        usuario.setEmail(campos.texto("email"));
                        mudou = true;
                    }
                    if (campos.preenchido("cidade")) {
                        usuario.setCidade(campos.texto("cidade"));
                        mudou = true;
                    }
                    // campos opcionais: podem ser apagados (NULL)
                    if (campos.apagar("anoFav", "Ano favorito")) {
                        usuario.setAnoFav(0); // o DAO grava 0 como NULL
                        mudou = true;
                    } else if (campos.preenchido("anoFav")) {
                        usuario.setAnoFav(campos.inteiro("anoFav"));
                        mudou = true;
                    }
                    if (campos.apagar("categoriaFav", "Categoria favorita")) {
                        usuario.setCategoriaFav(null);
                        mudou = true;
                    } else if (campos.preenchido("categoriaFav")) {
                        usuario.setCategoriaFav(campos.texto("categoriaFav"));
                        mudou = true;
                    }
                    // senha em branco mantém a senha salva
                    if (campos.preenchido("senha")) {
                        usuario.setSenha(campos.texto("senha"));
                        mudou = true;
                    }
                    // checkbox de administrador: marcado promove; desmarcado mantém o valor salvo
                    if (req.getParameter("ehAdmin") != null && !usuario.isEhAdmin()) {
                        usuario.setEhAdmin(true);
                        mudou = true;
                    }

                    if (!mudou) {
                        mensagem = "Nenhuma alteração informada: preencha ao menos um campo ou marque \"apagar valor salvo\".";
                    } else if (dao.altera(usuario)) {
                        sucesso = true;
                        mensagem = "Usuário alterado com sucesso!";
                        Upload fotoPerfil = arquivoDao.buscaPorUsuarioEContexto(usuario.getUserID(), "perfil");
                        detalhes = detalhesDe(usuario, fotoPerfil);
                    } else {
                        mensagem = "Nenhum usuário encontrado com esse ID.";
                    }
                }
            } else if ("consultar".equals(acao)) {
                Usuario usuario = dao.busca(campos.inteiro("userID"));
                if (usuario == null) {
                    mensagem = "Nenhum usuário encontrado com esse ID.";
                } else {
                    sucesso = true;
                    mensagem = "Usuário encontrado com sucesso!";
                    Upload fotoPerfil = arquivoDao.buscaPorUsuarioEContexto(usuario.getUserID(), "perfil");
                    detalhes = detalhesDe(usuario, fotoPerfil);
                }
            } else if ("remover".equals(acao)) {
                if (dao.remove(campos.inteiro("userID"))) {
                    sucesso = true;
                    mensagem = "Usuário removido com sucesso!";
                } else {
                    mensagem = "Nenhum usuário encontrado com esse ID.";
                }
            } else if ("listar".equals(acao)) {
                lista = new ArrayList<>();
                Map<Integer, Upload> mapaPerfil = arquivoDao.mapaPorContexto("perfil");
                for (Usuario usuario : dao.getLista()) {
                    lista.add(detalhesDe(usuario, mapaPerfil.get(usuario.getUserID())));
                }
                sucesso = true;
                mensagem = lista.isEmpty() ? "Nenhum usuário cadastrado." : lista.size() + " usuário(s) cadastrado(s).";
            } else {
                mensagem = "Ação inválida.";
            }
        } catch (Campos.CampoInvalidoException e) {
            mensagem = e.getMessage();
        } catch (NumberFormatException e) {
            mensagem = "Preencha os campos numéricos com valores válidos.";
        } catch (RuntimeException e) {
            // erros do banco, ex.: apelido ou e-mail duplicado
            mensagem = "Erro no banco de dados: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }

        req.setAttribute("status", sucesso ? "sucesso" : "erro");
        req.setAttribute("entidade", "Usuário");
        req.setAttribute("mensagem", mensagem);
        req.setAttribute("detalhes", detalhes);
        req.setAttribute("lista", lista);
        req.setAttribute("paginaVoltar", "usuario.jsp");
        req.getRequestDispatcher("roteador.jsp").forward(req, resp);
    }

    // campos do usuário para a tabela da página de sucesso
    private static Map<String, String> detalhesDe(Usuario usuario, Upload fotoPerfil) {
        Map<String, String> d = new LinkedHashMap<>();
        d.put("ID", String.valueOf(usuario.getUserID()));
        d.put("Pontos totais", String.valueOf(usuario.getPontosTotais()));
        d.put("Nome", usuario.getNomeReal());
        d.put("Apelido", usuario.getNickname());
        d.put("E-mail", usuario.getEmail());
        d.put("Cidade", usuario.getCidade());
        d.put("Ano favorito", usuario.getAnoFav() > 0 ? String.valueOf(usuario.getAnoFav()) : "-");
        d.put("Categoria favorita", usuario.getCategoriaFav() == null ? "-" : usuario.getCategoriaFav());
        d.put("Admin", usuario.isEhAdmin() ? "Sim" : "Não");
        d.put("Foto de perfil", fotoPerfil != null ? fotoPerfil.getNomeOriginal() : "-");
        return d;
    }
}