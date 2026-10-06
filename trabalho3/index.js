var idx = 0;

const imgs = ['alunos/gustavo.jpeg', 'alunos/bernardo.png', 'alunos/osvaldo.png', 'alunos/caio.jpeg', 'alunos/vinicius.jpg', 'alunos/tiago.jpeg']

function trocaAluno() {
    var matricula = document.getElementById("matriculaInput");
    var imagem = document.getElementById("imagemAluno");
    switch (matricula.value.toUpperCase()) {
        case "12321BCC001":
            imagem.src=imgs[0];
            idx = 0;
            break;
        case "12221BCC022":
            imagem.src=imgs[1];
            idx = 1;
            break;
        case "12221BCC047":
            imagem.src=imgs[2];
            idx = 2;
            break;
        case "12221BCC032":
            imagem.src=imgs[3];
            idx = 3;
            break;
        case "12221BCC018":
            imagem.src=imgs[4];
            idx = 4;
            break;
        case "12321BCC030":
            imagem.src=imgs[5];
            idx = 5;
            break;
        default:
            alert("Aluno não encontrado! Você digitou corretamente?");
            return;
    }
    imagem.removeAttribute("hidden");
}


function imagemClick() {
    var imagem = document.getElementById("imagemAluno");
    imagem.removeAttribute("hidden");
    idx = (idx + 1) % imgs.length;
    imagem.src=imgs[idx]
}

