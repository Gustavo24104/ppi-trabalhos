const alunos = [
    { matricula: "12321BCC001",vaga: "isto", descricao: "Descrição de isto 1" },
    { matricula: "1221BCC045",vaga: "isto 2", descricao: "Descrição de isto 2" },
    { matricula: "OSWALDO",vaga: "isto 3", descricao: "Descrição de isto 3" },
    { matricula: "12221BCC032", vaga: "isto 4", descricao: "Descrição de isto 4" },
    { matricula: "VLC", vaga: "isto 5", descricao: "Descrição de isto 5" },
    { matricula: "12321BCC030", vaga: "isto 6", descricao: "Descrição de isto 6" }
]


function buscarAluno() {
    const matriculaDigitada = document.getElementById("inputMatricula").value;

    const resultado = document.getElementById("resultado");

    let alunoEncontrado = null;
    
    //Busca aluno na lista com base na matrícula digitada. Encerra quando acha ou quando chega ao final da lista
    for (let i = 0; i < alunos.length; i++) {
        if (alunos[i].matricula == matriculaDigitada) {
            alunoEncontrado = alunos[i];
            break;
        }
    }

    if(alunoEncontrado != null) {
        resultado.innerHTML = "Matrícula: " + alunoEncontrado.matricula + "<br>" +
                              "Vaga: " + alunoEncontrado.vaga + "<br>" +
                              "Descrição: " + alunoEncontrado.descricao;
    }
    
    else {
        alert("Aluno não encontrado.");
    }
}