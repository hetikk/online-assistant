function getAnswers() {
    var result = '-';

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/test', false);
    // xhr.open('POST', 'https://online--assistant.herokuapp.com/test', false);
    xhr.setRequestHeader('Accept', 'text/html; charset=utf-8');
    xhr.setRequestHeader('Content-Type', 'text/html');
    xhr.onreadystatechange = function() {
        if (this.readyState === 4 && this.status === 200) {
            result = JSON.parse(this.responseText);
        }
    };
    xhr.send(document.body.innerHTML);

    return result;
}

function exec() {
    var trs = document.querySelectorAll('#mainPanel > table > tbody > tr');
    if (trs.length > 0) {
        console.debug('страница тестироания');
        console.debug('подождите...');
        var response = getAnswers();
        console.debug(response);

        document.body.oncontextmenu = function (event) {
            if (response !== '-') {
                var questSeqNum = event.target.innerHTML;
                if (typeof response[questSeqNum] !== 'undefined') {
                    for (var i = 0; i < response[questSeqNum].length; i++) {
                        document.getElementById(response[questSeqNum][i]).checked = 'true';
                    }
                } else {
                    console.debug('Вопроса с ключом \'' + questSeqNum + '\' нет');
                }
            }
            return false;
        };
    } else {
        console.debug('это не страница тестироания')
    }
}

exec();