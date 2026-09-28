
function trocaAluno() {
    var matricula = document.getElementById("matriculaInput");
    var imagem = document.getElementById("imagemAluno");
    //alert("mpgp");
    switch (matricula.value) {
        case "12321BCC001":
            alert("gggg");
            imagem.src='alunos/gggg.jpeg';
            break;
        default:
            alert("Mensagem apropriada!");
    }
}
