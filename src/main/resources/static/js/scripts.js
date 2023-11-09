'use strict';

function hideDescription() {
    const contentDescription = document.getElementById("content-description");
    const btnShowDescription = document.getElementById("btn-show-description");

    contentDescription.classList.remove("d-block");
    contentDescription.classList.add("d-none");
    btnShowDescription.classList.remove("d-none");
}

function showDescription() {
    const contentDescription = document.getElementById("content-description");
    const btnShowDescription = document.getElementById("btn-show-description");

    contentDescription.classList.remove("d-none");
    contentDescription.classList.add("d-block");
    btnShowDescription.classList.add("d-none");
}


var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, () => {
        console.log("Ocorreu um erro!")
    });
}

function onConnected(frame) {
    stompClient.subscribe('/topic/allMessages', (message) => {
        const isChatRoom = window.location.pathname.includes("support-chat");
    if(isChatRoom){
        showMessage(JSON.parse(message.body));
    }
    else{
        const notificationTitle = document.getElementById('notification-title')
        const notificationText = document.getElementById('notification-text')
        const json = JSON.parse(message.body);
//        const isCurrentUser = json.userId === currentUserId
        notificationTitle.innerText = json.callTitle;
        notificationText.innerText = "O agente "+ json.sender + " respondeu sua chamada";
        showNotification();
    }
    });
}


function showMessage(message) {
    const messagesDiv = document.getElementById('message-container')
    if (messagesDiv.classList.contains("d-none")) {
        messagesDiv.classList.remove("d-none")
        messagesDiv.classList.add("d-block")
    }
    document.getElementById('messages').appendChild(createCard(message));
}

function showNotification() {
    const notification = document.querySelector('.notification');
    notification.style.transform = 'translateY(0)';

    setTimeout(() => {
        closeNotification();
    }, 5000); // Fecha a notificação após 5 segundos (ajuste o tempo conforme necessário)
}

function closeNotification() {
    const notification = document.querySelector('.notification');
    notification.style.transform = 'translateY(200%)';
}

function mostrarAlertaPersonalizado() {
  var modal = document.getElementById("modal");
  modal.style.display = "block";
}

function fecharAlertaPersonalizado() {
  var modal = document.getElementById("modal");
  modal.style.display = "none";
}


function createCard(message) {

    const messageCard = document.createElement("div");
    messageCard.classList.add("pt-1", "mb-2", "message-card");

    const cardBody = document.createElement("div");
    cardBody.classList.add("card-body");

    const messageTitle = document.createElement("p");
    messageTitle.classList.add("d-flex", "card-title", "message-title", "font-weight-bold");
    const usernameSpan = document.createElement("span");
    usernameSpan.classList.add("mr-2");
    usernameSpan.textContent = message.sender;
    const roleSpan = document.createElement("span");
    if (currentUserRole === 'CONSUMER') {
        roleSpan.textContent = message.userId === currentUserId ? '(EU)' : '(TÉCNICO)';
    } else {
        roleSpan.textContent = message.userId === currentUserId ? '(EU)' : '(CLIENTE)';
    }

    if (message.userId !== currentUserId) {
        messageCard.classList.remove("bg-me-message")
        messageCard.classList.add("bg-him-message")
    } else {
        messageCard.classList.remove("bg-him-message")
        messageCard.classList.add("bg-me-message")
    }


    messageTitle.appendChild(usernameSpan);
    messageTitle.appendChild(roleSpan);

    const messageText = document.createElement("p");
    messageText.classList.add("card-text", "message-text");
    messageText.textContent = message.content;

    const textRightDiv = document.createElement("div");
    textRightDiv.classList.add("text-right", "mb-2", "pb-0", "mx-4");
    const smallText = document.createElement("small");
    smallText.textContent = getFormattedDate(message.date);

    textRightDiv.appendChild(smallText);

    cardBody.appendChild(messageTitle);
    cardBody.appendChild(messageText);

    messageCard.appendChild(cardBody);
    messageCard.appendChild(textRightDiv);
    messageCard.scrollIntoView({behavior: "smooth"});
    return messageCard;
}

function sendMessage(event) {
    const message = document.getElementById('prompt-textarea').value;

    const currentDate = new Date();

    let chatMessage = {
        userId: currentUserId,
        agentId: callAgentId,
        callId: tickerId,
        callTitle: callTitle,
        sender: currentUserName,
        content: message,
        date: currentDate
    };

    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
    document.getElementById('prompt-textarea').value = '';
    event.preventDefault();
}

function getFormattedDate(creationDate) {
    const currentDate = new Date();
    const timeDifferenceInMillis = currentDate - creationDate;

    const days = Math.floor(timeDifferenceInMillis / (1000 * 60 * 60 * 24));
    const hours = Math.floor((timeDifferenceInMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((timeDifferenceInMillis % (1000 * 60 * 60)) / (1000 * 60));

    let message;

    if (days > 0) {
        message = `Enviado há ${days} ${days === 1 ? "dia atrás" : "dias atrás"}`;
    } else if (hours > 0) {
        message = `Enviado há ${hours} ${hours === 1 ? "hora atrás" : "horas atrás"}`;
    } else if (minutes > 0) {
        message = `Enviado há ${minutes} ${minutes === 1 ? "minuto atrás" : "minutos atrás"}`;
    } else {
        message = "Enviado agora mesmo";
    }

    return message;
}

window.onload = function () {
    connect();
};

(function () {
    const formChat = document.getElementById("formChat");
    if(formChat){
        formChat.addEventListener('submit', sendMessage, true);
    }
})();


function fetchApiData() {

    const callId = document.getElementById('hidden-call-id').value;

    const apiUrl = 'http://localhost:8080/api/calls/' + callId + '/messages';

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Erro ao buscar os dados da API. Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            data.forEach(message => {
                stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(message));
            });
        })
        .catch(error => {
            console.error('Erro:', error);
        });
}





