var documentWidth = document.documentElement.clientWidth
var gridContainerWidth = 0.92 * documentWidth
var cellSlideLength = 0.18 * documentWidth
var cellSpace = 0.04 * documentWidth

function getPositionTop(i) {
    return cellSpace + i * (cellSlideLength + cellSpace)
}

function getPositionLeft(j) {
    return cellSpace + j * (cellSlideLength + cellSpace)
}

function getNumberBackgroundColor(number) {
    switch (number) {
        case 2: return '#eee4da'; break;
        case 4: return '#ede0c8'; break;
        case 8: return '#f2b179'; break;
        case 16: return '#f59563'; break;
        case 32: return '#f67c5f'; break;
        case 64: return '#f65e3b'; break;
        case 128: return '#edcf72'; break;
        case 256: return '#edcc61'; break;
        case 512: return '#99cc00'; break;
        case 1024: return '#33b5e5'; break;
        case 2048: return '#0099cc'; break;
        case 4096: return '#aa66cc'; break;
        case 8192: return '#9933cc'; break;
        default: return '#000000'
    }
}

function getNumberColor(number) {
    if (number <= 4) { return '#776e65' }
    return '#ffffff'
}

function noSpace(board) {
    for (let i = 0; i < 4; i++) {
        for (let j = 0; j < 4; j++) {
            if (board[i][j] === 0) { return false }
        }
    }
    return true
}

function noMove(board) {
    if (canMoveLeft(board) ||
        canMoveUp(board) ||
        canMoveRight(board) ||
        canMoveDown(board)) {
            return false
        }
    return true
}

// 仅判断是否存在可以移动的元素
function canMoveLeft(board) {
    for (let i = 0; i < 4; i++) {
        // j 从 1 开始，因为第 0 列肯定不能再往左移了
        for (let j = 1; j < 4; j++) {
            // 如果当前格子存在数字
            if (board[i][j] !== 0) {
                // 如果左侧格子为空或与左侧格子数字相等（可合并），即可左移
                if (board[i][j - 1] === 0 || board[i][j - 1] === board[i][j]) {
                    return true
                }
            }
        }
    }
    return false
}

function canMoveUp(board) {
    // i 从 1 开始，因为第 0 行肯定不能再往上移了
    for (let i = 1; i < 4; i++) {
        for (let j = 0; j < 4; j++) {
            if (board[i][j] !== 0) {
                // 如果上侧格子为空或与上侧格子数字相等（可合并），即可上移
                if (board[i - 1][j] === 0 || board[i - 1][j] === board[i][j]) {
                    return true
                }
            }
        }
    }
    return false
}

function canMoveRight(board) {
    for (let i = 0; i < 4; i++) {
        // j 小于 3，因为第 3 列（列数从 0 开始）肯定不能再往右移了
        // for (let j = 0; j < 3; j++) {
        for (let j = 2; j >= 0; j--) {
            if (board[i][j] !== 0) {
                // 如果右侧格子为空或与右侧格子数字相等（可合并），即可右移
                if (board[i][j + 1] === 0 || board[i][j + 1] === board[i][j]) {
                    return true
                }
            }
        }
    }
    return false
}

function canMoveDown(board) {
    // i 小于 3，因为第 3 行（行数从 0 开始）肯定不能再往下移了
    // for (let i = 0; i < 3; i++) {
    for (let i = 2; i >= 0; i--) {
        for (let j = 0; j < 4; j++) {
            if (board[i][j] !== 0) {
                // 如果下侧格子为空或与下侧格子数字相等（可合并），即可下移
                if (board[i + 1][j] === 0 || board[i + 1][j] === board[i][j]) {
                    return true
                }
            }
        }
    }
    return false
}

function noBlockHorizontal(row, column1, column2, board) {
    for (let i = column1 + 1; i < column2; i++) {
        if (board[row][i] !== 0) { return false }
    }
    return true
}

function noBlockVertical(column, row1, row2, board) {
    for (let i = row1 + 1; i < row2; i++) {
        if (board[i][column] !== 0) { return false }
    }
    return true
}