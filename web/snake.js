/**
 * Created by Zivert on 12/30/2016.
 */


var JS_SNAKE = {};

JS_SNAKE.equalCoordinates = function (coord1, coord2) {
    return coord1[0] === coord2[0] && coord1[1] === coord2[1];
}

JS_SNAKE.checkCoordinateInArray = function (coord, arr) {
    var isInArray = false;
    $.each(arr, function (index, item) {
        if (JS_SNAKE.equalCoordinates(coord, item)) {
            isInArray = true;
        }
    });
    return isInArray;
};

JS_SNAKE.game = (function () {
    var canvas, ctx;
    var score1;
    var score2;
    var socket = new WebSocket("ws://localhost:8081/snakeServer");
    socket.onmessage = onMessage;
    JS_SNAKE.width = 800;
    JS_SNAKE.height = 400;
    JS_SNAKE.blockSize = 10;
    JS_SNAKE.widthInBlocks = JS_SNAKE.width / JS_SNAKE.blockSize;
    JS_SNAKE.heightInBlocks = JS_SNAKE.height / JS_SNAKE.blockSize;

    function init() {
        var $canvas = $('#jsSnake');
        if ($canvas.length === 0) {
            $('body').append('<canvas id="jsSnake">');
        }
        $canvas = $('#jsSnake');
        $canvas.attr('width', JS_SNAKE.width);
        $canvas.attr('height', JS_SNAKE.height);
        canvas = $canvas[0];
        ctx = canvas.getContext('2d');
        bindEvents();
    }


    function onMessage(event) {

        var gameData = JSON.parse(event.data);
        if (gameData.action === "drawNewPosition") {
            scores = JSON.parse(gameData.scores);
            score1 = scores[0];
            if (scores[1] == null) {
                score2 = 0;
            }
            else {
                score2 = scores[1];
            }
            ctx.clearRect(0, 0, JS_SNAKE.width, JS_SNAKE.height);
            bodies = JSON.parse(gameData.bodies);
            drawBodies(bodies);
            drawBorder();
            drawScore();
        }
        if (gameData.action === "gameOver") {
            gameOver();
        }
    }

    function drawBodies(bodies) {
        for (var i = 0; i < bodies.length; i++) {
            drawBody(bodies[i]);
        }
        ctx.restore();
    }

    function drawBody(posArray) {
        ctx.save();
        ctx.fillStyle = '#38a';
        for (var i = 0; i < posArray.length; i++) {
            drawSection(posArray[i]);
        }
        ctx.restore();
    }

    function drawSection(position) {
        var x = JS_SNAKE.blockSize * position[0];
        var y = JS_SNAKE.blockSize * position[1];
        ctx.fillRect(x, y, JS_SNAKE.blockSize, JS_SNAKE.blockSize);
    }

    function draw() {
        drawBorder();
        drawScore();
    }

    function drawScore() {
        ctx.save();
        ctx.font = 'bold 102px sans-serif';
        ctx.fillStyle = 'rgba(0, 0, 0, 0.3)';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        var centreX = JS_SNAKE.width / 4;
        var centreY = JS_SNAKE.height / 2;
        ctx.fillText(score1.toString(), centreX, centreY);
        var centreX = JS_SNAKE.width * 3 / 4;
        var centreY = JS_SNAKE.height / 2;
        ctx.fillText(score2.toString(), centreX, centreY);
        ctx.restore();
    }

    function gameOver() {
        ctx.save();
        ctx.font = 'bold 30px sans-serif';
        ctx.fillStyle = '#000';
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.strokeStyle = 'white';
        ctx.lineWidth = 2;
        var centreX = JS_SNAKE.width / 2;
        var centreY = JS_SNAKE.height / 2;
        ctx.strokeText('Game Over', centreX, centreY - 10);
        ctx.fillText('Game Over', centreX, centreY - 10);
        ctx.font = 'bold 15px sans-serif';
        ctx.strokeText('Press space to restart', centreX, centreY + 15);
        ctx.fillText('Press space to restart', centreX, centreY + 15);
        ctx.restore();
    }

    function drawBorder() {
        ctx.save();
        ctx.strokeStyle = 'gray';
        ctx.lineWidth = JS_SNAKE.blockSize;
        ctx.lineCap = 'square';
        var offset = ctx.lineWidth / 2;
        var corners = [
            [offset, offset],
            [JS_SNAKE.width - offset, offset],
            [JS_SNAKE.width - offset, JS_SNAKE.height - offset],
            [offset, JS_SNAKE.height - offset]
        ];
        ctx.beginPath();
        ctx.moveTo(corners[3][0], corners[3][1]);
        $.each(corners, function (index, corner) {
            ctx.lineTo(corner[0], corner[1]);
        });
        ctx.stroke();
        ctx.restore();
    }

    function restart() {

        var message = {
            action: "restart",
        };
        socket.send(JSON.stringify(message));
    }

    function bindEvents() {
        var keysToDirections = {
            37: 'left',
            39: 'right',
        };

        $(document).keydown(function (event) {
            var key = event.which;
            var direction = keysToDirections[key];

            if (direction) {
                changeDirection(direction);
                event.preventDefault();
            }
            else if (key === 32) {
                restart();
            }
        });

        function changeDirection(direction) {
            var message = {
                action: "changeDirection",
                direction: direction,
            };

            socket.send(JSON.stringify(message));
        }

        $(canvas).click(restart);
    }

    return {
        init: init
    };
})();

JS_SNAKE.game.init();


