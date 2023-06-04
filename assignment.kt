import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.Timer

class Connect4 : JPanel() {
    private var currentPlayer = 1
    private val board: Array<IntArray> = Array(6) { IntArray(7) }

    private var fallingPieceColumn = -1
    private var fallingPieceRow = -1
    private var fallingPieceY = -1

    private var timer: Timer? = null

    init {
        preferredSize = Dimension(700, 600)
        background = Color.BLUE

        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val column = e.x / (width / 7)
                if (isValidMove(column)) {
                    dropPiece(column)
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        drawBoard(g)
    }

    private fun isValidMove(column: Int): Boolean {
        return fallingPieceRow == -1 && column >= 0 && column < 7 && board[0][column] == 0
    }

    private fun dropPiece(column: Int) {
        fallingPieceRow = 0
        fallingPieceColumn = column
        fallingPieceY = 0

        val rowHeight = height / 6

        timer?.stop()
        timer = Timer(10) {
            fallingPieceY += 5

            if (fallingPieceY >= rowHeight * (fallingPieceRow + 1)) {
                fallingPieceRow++
                if (fallingPieceRow == 5 || board[fallingPieceRow + 1][fallingPieceColumn] != 0) {
                    timer?.stop()
                    board[fallingPieceRow][fallingPieceColumn] = currentPlayer

                    if (checkWin()) {
                        val winner = if (currentPlayer == 1) "Player 1" else "Player 2"
                        showWinnerDialog(winner)
                        resetGame()
                    } else if (isBoardFull()) {
                        showTieDialog()
                        resetGame()
                    } else {
                        currentPlayer = if (currentPlayer == 1) 2 else 1
                        fallingPieceRow = -1
                        fallingPieceColumn = -1
                        fallingPieceY = -1
                    }
                }
            }

            repaint()
        }
        timer?.start()
    }

    private fun drawBoard(g: Graphics) {
        val columnWidth = width / 7
        val rowHeight = height / 6

        // Create a Stroke with a thicker width for the border
        val borderStroke = BasicStroke(3f)

        // Draw the tokens
        for (row in 0 until 6) {
            for (col in 0 until 7) {
                val x = col * columnWidth
                val y = row * rowHeight

                // Calculate the diameter for the circle
                val diameter = Math.min(columnWidth, rowHeight)

                // Calculate the offset to center the circle
                val xOffset = (columnWidth - diameter) / 2
                val yOffset = (rowHeight - diameter) / 2

                // Draw the token
                if (row == fallingPieceRow && col == fallingPieceColumn) {
                    // Draw the falling piece
                    g.color = if (currentPlayer == 1) Color.RED else Color.YELLOW
                    g.fillOval(x + xOffset + 2, y + yOffset + 2, diameter - 4, diameter - 4)
                } else if (board[row][col] != 0) {
                    // Draw the placed pieces with a thick black border
                    when (board[row][col]) {
                        1 -> g.color = Color.RED
                        2 -> g.color = Color.YELLOW
                    }
                    g.fillOval(x + xOffset + 2, y + yOffset + 2, diameter - 4, diameter - 4)

                    // Set the Stroke to draw a thicker border
                    val originalStroke = (g as Graphics2D).stroke
                    g.stroke = borderStroke
                    g.color = Color.BLACK
                    g.drawOval(x + xOffset + 2, y + yOffset + 2, diameter - 4, diameter - 4)

                    // Reset the Stroke to the original value
                    g.stroke = originalStroke
                } else {
                    // Draw empty spaces
                    g.color = Color.WHITE
                    g.fillOval(x + xOffset + 2, y + yOffset + 2, diameter - 4, diameter - 4)
                }
            }
        }
    }



    private fun checkWin(): Boolean {
        // Check horizontal
        for (row in 0 until 6) {
            for (col in 0 until 4) {
                if (board[row][col] != 0 && board[row][col] == board[row][col + 1] && board[row][col] == board[row][col + 2] && board[row][col] == board[row][col + 3]) {
                    return true
                }
            }
        }

        // Check vertical
        for (row in 0 until 3) {
            for (col in 0 until 7) {
                if (board[row][col] != 0 && board[row][col] == board[row + 1][col] && board[row][col] == board[row + 2][col] && board[row][col] == board[row + 3][col]) {
                    return true
                }
            }
        }

        // Check diagonal (top-left to bottom-right)
        for (row in 0 until 3) {
            for (col in 0 until 4) {
                if (board[row][col] != 0 && board[row][col] == board[row + 1][col + 1] && board[row][col] == board[row + 2][col + 2] && board[row][col] == board[row + 3][col + 3]) {
                    return true
                }
            }
        }

        // Check diagonal (top-right to bottom-left)
        for (row in 0 until 3) {
            for (col in 3 until 7) {
                if (board[row][col] != 0 && board[row][col] == board[row + 1][col - 1] && board[row][col] == board[row + 2][col - 2] && board[row][col] == board[row + 3][col - 3]) {
                    return true
                }
            }
        }

        return false
    }

    private fun isBoardFull(): Boolean {
        for (row in 0 until 6) {
            for (col in 0 until 7) {
                if (board[row][col] == 0) {
                    return false
                }
            }
        }
        return true
    }

    private fun showWinnerDialog(winner: String) {
        JOptionPane.showMessageDialog(this, "$winner wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun showTieDialog() {
        JOptionPane.showMessageDialog(this, "It's a tie!", "Game Over", JOptionPane.INFORMATION_MESSAGE)
    }

    private fun resetGame() {
        currentPlayer = 1
        board.forEach { row -> row.fill(0) }
        fallingPieceColumn = -1
        fallingPieceRow = -1
        fallingPieceY = -1
        timer?.stop()
        repaint()
    }
}

fun main() {
    val frame = JFrame("Connect 4")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.contentPane.add(Connect4())
    frame.pack()
    frame.isVisible = true
}
