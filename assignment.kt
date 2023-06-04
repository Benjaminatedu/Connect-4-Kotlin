import kotlin.random.Random

class Minesweeper(private val numRows: Int, private val numCols: Int, private val numMines: Int) {
    private val board: Array<Array<Cell>>
    
    init {
        board = Array(numRows) { row ->
            Array(numCols) { col ->
                Cell(row, col)
            }
        }
        
        placeMines()
        calculateAdjacentMines()
    }
    
    private fun placeMines() {
        val totalCells = numRows * numCols
        val mineIndices = mutableSetOf<Int>()
        
        while (mineIndices.size < numMines) {
            val randomIndex = Random.nextInt(totalCells)
            mineIndices.add(randomIndex)
        }
        
        for (index in mineIndices) {
            val row = index / numCols
            val col = index % numCols
            board[row][col].isMine = true
        }
    }
    
    private fun calculateAdjacentMines() {
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                if (!board[row][col].isMine) {
                    val adjacentMines = getAdjacentCells(row, col)
                        .count { cell -> cell.isMine }
                    board[row][col].adjacentMines = adjacentMines
                }
            }
        }
    }
    
    private fun getAdjacentCells(row: Int, col: Int): List<Cell> {
        val adjacentCells = mutableListOf<Cell>()
        
        for (dRow in -1..1) {
            for (dCol in -1..1) {
                val newRow = row + dRow
                val newCol = col + dCol
                
                if (newRow in 0 until numRows && newCol in 0 until numCols) {
                    adjacentCells.add(board[newRow][newCol])
                }
            }
        }
        
        return adjacentCells
    }
    
    fun displayBoard(revealMines: Boolean = false) {
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                val cell = board[row][col]
                val displayValue = if (revealMines && cell.isMine) "X" else cell.displayValue()
                print("$displayValue ")
            }
            println()
        }
    }
    
    fun revealCell(row: Int, col: Int): Boolean {
        val cell = board[row][col]
        
        if (cell.isMine) {
            return false
        }
        
        cell.isRevealed = true
        
        if (cell.adjacentMines == 0) {
            val adjacentCells = getAdjacentCells(row, col)
                .filter { !it.isRevealed }
            for (adjacentCell in adjacentCells) {
                revealCell(adjacentCell.row, adjacentCell.col)
            }
        }
        
        return true
    }
    
    inner class Cell(val row: Int, val col: Int) {
        var isMine: Boolean = false
        var isRevealed: Boolean = false
        var adjacentMines: Int = 0
        
        fun displayValue(): String {
            return if (isRevealed) {
                if (isMine) "M" else adjacentMines.toString()
            } else {
                "-"
            }
        }
    }
}

fun main() {
    val numRows = 10
    val numCols = 10
    val numMines = 10
    
    val game = Minesweeper(numRows, numCols, numMines)
    game.displayBoard()
    
    while (true) {
        print("Enter the row number (0-$numRows): ")
        val row = readLine()?.toIntOrNull()
        
        print("Enter the column number (0-$numCols): ")
        val col = readLine()?.toIntOrNull()
        
        if (row != null && col != null && row in 0 until numRows && col in 0 until numCols) {
            val success = game.revealCell(row, col)
            
            if (success) {
                game.displayBoard()
            } else {
                println("Game Over! You hit a mine.")
                game.displayBoard(revealMines = true)
                break
            }
        } else {
            println("Invalid input! Please try again.")
        }
    }
}
