let board = []
let score = 0
let hasConflicted = []
let startX = 0
let startY = 0
let endX = 0
let endY = 0
let deltaX = 0
let deltaY = 0

$(document).ready(function () {
    prepareForMobile()
    newGame()
})

function prepareForMobile() {
    
        gridContainerWidth = 200
        cellSlideLength = 40
        cellSpace = 8
    

    $('.grid-container').css('width', gridContainerWidth)
    $('.grid-container').css('height', gridContainerWidth)
    $('.grid-container').css('padding', cellSpace)
    $('.grid-container').css('border-radius', '6px')

    $('.grid-cell').css('width', cellSlideLength)
    $('.grid-cell').css('height', cellSlideLength)
    $('.grid-cell').css('border-radius', '6px')
}

function newGame() {
    // 初始化棋盘格
    init()

    // 随机在两个格子中生成数字
    generateOneNumber()
    generateOneNumber()
}

function init() {
    // 初始化所有格子
    for (let i = 0; i < 4; i++) {
        for (let j = 0; j < 4; j++) {
            let gridCell = $(`#grid-cell-${i}-${j}`)
            gridCell.css('top', getPositionTop(i))
            gridCell.css('left', getPositionLeft(j))
        }
    }

    // 初始化二维数组 board、hasConflicted
    for (let i = 0; i < 4; i++) {
        board[i] = []
        hasConflicted[i] = []
        for (let j = 0; j < 4; j++) {
            board[i][j] = 0
            hasConflicted[i][j] = false
        }
    }

    updateBoardView()
    score = 0
}

function updateBoardView() {
    $('.number-cell').remove()
    for (let i = 0; i < 4; i++) {
        for (let j = 0; j < 4; j++) {
            $('.grid-container').append(`<div class="number-cell" id="number-cell-${i}-${j}"></div>`)
            let numberCell = $(`#number-cell-${i}-${j}`)
            if (board[i][j] === 0) {
                numberCell.css('width', '0px')
                numberCell.css('height', '0px')
                numberCell.css('top', getPositionTop(i) + cellSlideLength / 2)
                numberCell.css('left', getPositionLeft(j) + cellSlideLength / 2)
            } else {
                numberCell.css('width', cellSlideLength)
                numberCell.css('height', cellSlideLength)
                numberCell.css('top', getPositionTop(i))
                numberCell.css('left', getPositionLeft(j))
                numberCell.css('background-color', getNumberBackgroundColor(board[i][j]))
                numberCell.css('color', getNumberColor(board[i][j]))
                numberCell.text(board[i][j])
            }

            // 重置
            hasConflicted[i][j] = false
        }
    }
    $('.number-cell').css('line-height', cellSlideLength + 'px')
    $('.number-cell').css('font-size', 0.4 * cellSlideLength + 'px')
}

function generateOneNumber() {
    if (noSpace(board)) { return false }

    // 随机生成一个位置
    let randomX = parseInt(Math.floor(Math.random() * 4)) // 0, 1, 2, 3
    let randomY = parseInt(Math.floor(Math.random() * 4)) // 0, 1, 2, 3
    // 如果位置上已经有数字，就重新生成随机位置
    let times = 0
    while (times < 20) {
        if (board[randomX][randomY] === 0) { break }
        randomX = parseInt(Math.floor(Math.random() * 4))
        randomY = parseInt(Math.floor(Math.random() * 4))

        times += 1
    }

    // 如果计算机试了 20 次之后，仍没有得到可用的随机位置，就人工生成位置
    if (times === 20) {
        for (let i = 0; i < 4; i++) {
            for (let j = 0; j < 4; j++) {
                if (board[i][j] === 0) {
                    randomX = i
                    randomY = j
                }                
            }
        }
    }

    // 随机生成一个数字：2 或 4，生成概率各为 50%
    let randomNumber = Math.random() < 0.5 ? 2 : 4

    // 在生成的随机位置上显示生成的随机数字
    board[randomX][randomY] = randomNumber
    showNumberWithAnimation(randomX, randomY, randomNumber)
    
    return true
}

document.addEventListener('keydown', (event) => {
    switch (event.keyCode) {
        case 37: // left
            event.preventDefault() // 阻止移动滚动条
            if (moveLeft(board)) { triggerNextActionAfterMoveDone() }
            break
        case 38: // up
            event.preventDefault()
            if (moveUp(board)) { triggerNextActionAfterMoveDone() }
            break
        case 39: // right
            event.preventDefault()
            if (moveRight(board)) { triggerNextActionAfterMoveDone() }
            break
        case 40: // down
            event.preventDefault()
            if (moveDown(board)) { triggerNextActionAfterMoveDone() }
            break
        default:
            break
    }
})

document.addEventListener('touchstart', (event) => {
    startX = event.touches[0].pageX
    startY = event.touches[0].pageY
})

document.addEventListener('touchend', (event) => {
    endX = event.changedTouches[0].pageX
    endY = event.changedTouches[0].pageY

    deltaX = endX - startX
    deltaY = endY - startY

    if (Math.abs(deltaX) < 0.1 * documentWidth && Math.abs(deltaY) < 0.1 * documentWidth) { return }

    if (Math.abs(deltaX) >= Math.abs(deltaY)) {
        // x 轴方向的滑动
        if (deltaX > 0) {
            // 右移
            if (moveRight(board)) {
                triggerNextActionAfterMoveDone()
            }
        } else {
            // 左移
            if (moveLeft(board)) {
                triggerNextActionAfterMoveDone()
            }
        }

    } else {
        // y 轴方向的滑动
        if (deltaY > 0) {
            // 下移
            if (moveDown(board)) {
                triggerNextActionAfterMoveDone()
            }
        } else {
            // 上移
            if (moveUp(board)) {
                triggerNextActionAfterMoveDone()
            }
        }
    }
})

document.querySelector('#new-game-button').addEventListener('click', function () {
    newGame()
})

function moveLeft(board) {
    if (!canMoveLeft(board)) { return false }
    
    // moveLeft
    // 移动具体的元素：哪个元素可移动？可以移动到具体哪个位置？
    for (let i = 0; i < 4; i++) {
        for (let j = 1; j < 4; j++) {
            if (board[i][j] !== 0) {
                for (let k = 0; k < j; k++) {
                    if (board[i][k] === 0 && noBlockHorizontal(i, k, j, board)) {
                        // move
                        showMoveAnimation(i, j, i, k)
                        board[i][k] = board[i][j]
                        board[i][j] = 0
                        continue
                    } else if (board[i][k] === board[i][j]
                        && noBlockHorizontal(i, k, j, board)
                        && !hasConflicted[i][k]) {
                            //move
                            showMoveAnimation(i, j, i, k)
                            //add
                            board[i][k] += board[i][j]
                            board[i][j] = 0
                            // add score
                            score += board[i][k]
                            updateScore(score)
                            hasConflicted[i][k] = true
                            continue
                    }
                }
            }
        }
    }

    // 刷新视图：从 M 到 V，将数据变更反映到视图上
    // 待移动动画 showMoveAnimation 完成后，再刷新视图
    // 否则动画尚未完成就被视图刷新动作覆盖掉了
    updateAfterMoveDone()
    return true
}

function moveUp(board) {
    if (!canMoveUp(board)) { return false }
    
    // moveUp
    for (let i = 1; i < 4; i++) {
        for (let j = 0; j < 4; j++) {
            if (board[i][j] !== 0) {
                for (let k = 0; k < i; k++) {
                    if (board[k][j] === 0 && noBlockVertical(j, k, i, board)) {
                        // move
                        showMoveAnimation(i, j, k, j)
                        board[k][j] = board[i][j]
                        board[i][j] = 0
                        continue
                    } else if (board[k][j] === board[i][j]
                        && noBlockVertical(j, k, i, board)
                        && !hasConflicted[k][j]) {
                            //move
                            showMoveAnimation(i, j, k, j)
                            //add
                            board[k][j] += board[i][j]
                            board[i][j] = 0
                            // add score
                            score += board[k][j]
                            updateScore(score)
                            hasConflicted[k][j] = true
                            continue
                    }
                }
            }
        }
    }

    updateAfterMoveDone()
    return true
}

function moveRight(board) {
    if (!canMoveRight(board)) { return false }
    
    // moveRight
    for (let i = 0; i < 4; i++) {
        // for (let j = 0; j < 3; j++) {
        for (let j = 2; j >= 0; j--) {
            if (board[i][j] !== 0) {
                for (let k = 3; k > j; k--) {
                    if (board[i][k] === 0 && noBlockHorizontal(i, j, k, board)) {
                        // move
                        showMoveAnimation(i, j, i, k)
                        board[i][k] = board[i][j]
                        board[i][j] = 0
                        continue
                    } else if (board[i][k] === board[i][j]
                        && noBlockHorizontal(i, j, k, board)
                        && !hasConflicted[i][k]) {
                        //move
                        showMoveAnimation(i, j, i, k)
                        //add
                        board[i][k] += board[i][j]
                        board[i][j] = 0
                        // add score
                        score += board[i][k]
                        updateScore(score)
                        hasConflicted[i][k] = true
                        continue
                    }
                }
            }
        }
    }

    updateAfterMoveDone()
    return true
}

function moveDown(board) {
    if (!canMoveDown(board)) { return false }
    
    // moveDown
    // for (let i = 0; i < 3; i++) {
    for (let i = 2; i >= 0; i--) {
        for (let j = 0; j < 4; j++) {
            if (board[i][j] !== 0) {
                for (let k = 3; k > i; k--) {
                    if (board[k][j] === 0 && noBlockVertical(j, i, k, board)) {
                        // move
                        showMoveAnimation(i, j, k, j)
                        board[k][j] = board[i][j]
                        board[i][j] = 0
                        continue
                    } else if (board[k][j] === board[i][j]
                        && noBlockVertical(j, i, k, board)
                        && !hasConflicted[k][j]) {
                        //move
                        showMoveAnimation(i, j, k, j)
                        //add
                        board[k][j] += board[i][j]
                        board[i][j] = 0
                        // add score
                        score += board[k][j]
                        updateScore(score)
                        hasConflicted[k][j] = true
                        continue
                    }
                }
            }
        }
    }

    updateAfterMoveDone()
    return true
}

function isGameOver() {
    if (noSpace(board) && noMove(board)) {
        gameOver()
    }
}

function gameOver() {
    if (document.querySelector('.game-over-alert')) { return } // 防止触发多个提示
    let div = document.createElement('div')
    div.classList.add('game-over-alert')
    div.textContent = 'Game Over'
    document.querySelector('body').appendChild(div)
    setTimeout(() => {
        document.querySelector('.game-over-alert').remove()
    }, 2000)
}

function updateAfterMoveDone() {
    setTimeout(updateBoardView, 200)
}

function triggerNextActionAfterMoveDone() {
    setTimeout(generateOneNumber, 210)
    setTimeout(isGameOver, 500)
}