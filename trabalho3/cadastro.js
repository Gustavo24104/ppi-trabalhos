// imagem correspondente a cada opção do <select> de categoria
const imagensCategoria = {
    historia: 'categorias/historia.png',
    cinema: 'categorias/cinema.png',
    literatura: 'categorias/literatura.png',
    musica: 'categorias/musica.png',
    ciencia: 'categorias/ciencia.png',
    esportes: 'categorias/esportes.png'
};

// ao carregar a página, o primeiro campo recebe o foco (dispara o evento 'focus')
function iniciar() {
    document.getElementById("nome").focus();
}

// evento 'focus' do primeiro campo: destaca o campo e mostra uma dica
function focoNome() {
    var nome = document.getElementById("nome");
    nome.style.backgroundColor = "lightyellow";
    nome.style.border = "2px solid orange";
    nome.placeholder = "Ex.: Maria da Silva";
}

// evento 'blur' do primeiro campo: volta ao estilo normal
function sairNome() {
    var nome = document.getElementById("nome");
    nome.style.backgroundColor = "";
    nome.style.border = "";
    nome.placeholder = "Digite seu nome";
}

// verifica todos os campos; em caso de erro avisa o usuário e coloca o foco no campo
function validaFormulario() {
    var myform = document.forms["formCadastro"];

    var campos = [
        { campo: myform.nome, rotulo: "Nome do jogador" },
        { campo: myform.apelido, rotulo: "Apelido" },
        { campo: myform.email, rotulo: "E-mail" },
        { campo: myform.cidade, rotulo: "Cidade" },
        { campo: myform.ano, rotulo: "Ano favorito" }
    ];

    for (var i = 0; i < campos.length; i++) {
        if (campos[i].campo.value.trim() == "") {
            alert("Preencha o campo " + campos[i].rotulo + "!");
            campos[i].campo.focus();
            return false;
        }
    }

    var email = myform.email.value.trim();
    if (email.indexOf("@") < 1 || email.indexOf(".") == -1) {
        alert("Digite um e-mail válido!");
        myform.email.focus();
        return false;
    }

    var ano = parseInt(myform.ano.value);
    if (isNaN(ano) || ano < 1900 || ano > new Date().getFullYear()) {
        alert("Digite um ano entre 1900 e " + new Date().getFullYear() + "!");
        myform.ano.focus();
        return false;
    }

    if (myform.categoria.value == "") {
        alert("Selecione uma categoria favorita!");
        myform.categoria.focus();
        return false;
    }

    return true;
}

// clique do botão: se o formulário for válido, mostra o relatório abaixo do botão
function gerarRelatorio() {
    if (!validaFormulario()) {
        return;
    }

    var myform = document.forms["formCadastro"];
    var categoria = myform.categoria;
    var textoCategoria = categoria.options[categoria.selectedIndex].text;

    var relatorio = document.getElementById("relatorio");
    relatorio.innerHTML =
        "<h2>Relatório do Cadastro</h2>" +
        "<p><b>Nome do jogador:</b> " + myform.nome.value + "</p>" +
        "<p><b>Apelido:</b> " + myform.apelido.value + "</p>" +
        "<p><b>E-mail:</b> " + myform.email.value + "</p>" +
        "<p><b>Cidade:</b> " + myform.cidade.value + "</p>" +
        "<p><b>Ano favorito:</b> " + myform.ano.value + "</p>" +
        "<p><b>Categoria favorita:</b> " + textoCategoria + "</p>" +
        "<figure>" +
        "<img id='imagemCategoria' width='300' height='200'>" +
        "<figcaption>Categoria escolhida: " + textoCategoria + "</figcaption>" +
        "</figure>";

    document.getElementById("imagemCategoria").src = imagensCategoria[categoria.value];
    document.getElementById("imagemCategoria").alt = textoCategoria;
}

// botão Limpar: apaga o relatório e devolve o foco ao primeiro campo
function limparRelatorio() {
    document.getElementById("relatorio").innerHTML = "";
    document.getElementById("nome").focus();
}
