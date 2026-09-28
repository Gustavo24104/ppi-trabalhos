
function trocaAluno() {
    var matricula = document.getElementById("matriculaInput");
    var imagem = document.getElementById("imagemAluno");
    //alert(matricula.value.toUpperCase());
    switch (matricula.value.toUpperCase()) {
        case "12321BCC001":
            imagem.src='alunos/gggg.jpeg';
            break;
        case "1221BCC045":
            imagem.src='alunos/bernas.webp';
            break;
        case "oswaldo":
            imagem.src='alunos/oswaldo.webp';
            break;
        case "12221BCC032":
            imagem.src='alunos/caio.webp';
            break;
        case "vlc":
            imagem.src='alunos/vlc.webp';
            break;
        case "12321BCC030":
            imagem.src='alunos/tiago.webp';
            break;
        default:
            alert("Aluno não encontrado! Você digitou corretamente?");
    }
}
