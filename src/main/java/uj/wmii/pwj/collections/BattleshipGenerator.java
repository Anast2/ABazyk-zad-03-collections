package uj.wmii.pwj.collections;

import java.util.Random;

public interface BattleshipGenerator {

    String generateMap();

    static BattleshipGenerator defaultInstance() {
        return new BattleshipGeneratorImpl();
    }
}

class BattleshipGeneratorImpl implements BattleshipGenerator{

    private static final int SIZE = 10;
    private static final char SHIP = '#';
    private static final char WATER = '.';
    private final Random random = new Random();

    private static final int[] SHIP_SIZES = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

    @Override
    public String generateMap() {
        char[][] board = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = WATER;
            }
        }
        for (int shipSize : SHIP_SIZES) {
            placeShip(board, shipSize);
        }
        StringBuilder sb = new StringBuilder(SIZE * SIZE);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    private void placeShip(char[][] board, int shipSize) {
        boolean placed = false;
        while (!placed) {
            boolean horizontal = random.nextBoolean();
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);

            if (canPlace(board, row, col, shipSize, horizontal)) {
                if(horizontal){
                    for(int column = 0; column < shipSize; column++){
                        board[row][col + column] = SHIP;
                    }
                }
                else{
                    for(int r = 0; r < shipSize; r++){
                        board[row + r][col] = SHIP;
                    }
                }
                placed = true;
            }
        }
    }

    private boolean canPlace(char[][] board, int row, int col, int shipSize, boolean horizontal) {
        if (horizontal) {
            if (col + shipSize > SIZE) return false;
        } else {
            if (row + shipSize > SIZE) return false;
        }
        int rowStart = Math.max(0, row - 1);
        int rowEnd   = Math.min(SIZE - 1, row + (horizontal ? 1 : shipSize));
        int colStart = Math.max(0, col - 1);
        int colEnd   = Math.min(SIZE - 1, col + (horizontal ? shipSize : 1));

        for (int r = rowStart; r <= rowEnd; r++) {
            for (int c = colStart; c <= colEnd; c++) {
                if (board[r][c] == SHIP) {
                    return false;
                }
            }
        }
        return true;
    }
}
