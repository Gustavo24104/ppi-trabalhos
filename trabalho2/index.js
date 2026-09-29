
function trocaAluno() {
    var matricula = document.getElementById("matriculaInput");
    var imagem = document.getElementById("imagemAluno");
    switch (matricula.value.toUpperCase()) {
        case "12321BCC001":
            imagem.src='alunos/gustavo.jpeg';
            break;
        case "12221BCC022":
            imagem.src='alunos/bernardo.png';
            break;
        case "12221BCC047":
            imagem.src='alunos/osvaldo.png';
            break;
        case "12221BCC032":
            imagem.src='alunos/caio.jpeg';
            break;
        case "12221BCC018":
            imagem.src='alunos/vinicius.jpg';
            break;
        case "12321BCC030":
            imagem.src='alunos/tiago.jpeg';
            break;
        default:
            alert("Aluno não encontrado! Você digitou corretamente?");
    }
}

