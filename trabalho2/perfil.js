var alunos = [
    { matricula: "12321BCC001", vaga: "Desenvolvedor Front-end", descricao: "Cria e estiliza as telas dos sites e sistemas." },
    { matricula: "12221BCC022", vaga: "Desenvolvedor Back-end", descricao: "Programa as regras e o banco de dados dos sistemas." },
    { matricula: "12221BCC047", vaga: "Analista de Suporte", descricao: "Ajuda os usuários a resolver problemas de TI." },
    { matricula: "12221BCC032", vaga: "Administrador de Redes", descricao: "Configura e mantém a rede de computadores." },
    { matricula: "12221BCC018", vaga: "Analista de Testes", descricao: "Testa os sistemas em busca de erros." },
    { matricula: "12321BCC030", vaga: "Desenvolvedor Mobile", descricao: "Cria aplicativos para celular." }
]


function buscarAluno() {
    var matriculaDigitada = document.getElementById("inputMatricula").value;

    var vagaElemento = document.getElementById("vagaAluno");
    var descricaoElemento = document.getElementById("descricaoAluno");

    var alunoEncontrado = null;

    //Busca aluno na lista com base na matrícula digitada. Encerra quando acha ou quando chega ao final da lista
    for (var i = 0; i < alunos.length; i++) {
        if (alunos[i].matricula == matriculaDigitada) {
            alunoEncontrado = alunos[i];
            break;
        }
    }

    if (alunoEncontrado != null) {
        vagaElemento.innerHTML = alunoEncontrado.vaga;
        descricaoElemento.innerHTML = alunoEncontrado.descricao;
    }

    else {
        alert("Aluno não encontrado.");
    }
}
