package com.valyakinaleksey.simple2048;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    Random random = new Random();
    private Cell[] cells = new Cell[16];
    public final int RIGHT = 1, LEFT = 2, UP = 3, DOWN = 4;
    private ArrayList<Integer> freePositions = new ArrayList<>();//Свободные позиции
    private ArrayList<Integer> canMove = new ArrayList<>();//Клекти, которые могут передвинуться
    private ArrayList<Integer> movedCells = new ArrayList<>();//Передвинутые клетки
    private Integer result = 0;
    private MainActivity.Updater updater; //для обновления Textview

    public String getResult() {
        return result.toString();
    }

    public int getFreePositionsCount() {
        return freePositions.size();
    }

    public void setUpdater(MainActivity.Updater updater) {
        this.updater = updater;
    }

    public Game() {
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell(i, "");
        }
        createFreePositions();
        for (int i = 0; i < cells.length; i++) {
            if (i % 4 == 0) {//левые
                cells[i].left = null;
            } else {
                cells[i].left = cells[i - 1];
            }
            if (i < 4) {//верхние
                cells[i].up = null;
            } else {
                cells[i].up = cells[i - 4];
            }
            if ((i - 3) % 4 == 0) {//правые
                cells[i].right = null;
            } else {
                cells[i].right = cells[i + 1];
            }
            if (i > 11) {//правые
                cells[i].down = null;
            } else {
                cells[i].down = cells[i + 4];
            }
        }
    }

    private void createFreePositions() {
        freePositions.clear();
        for (int i = 0; i < cells.length; i++) {
            freePositions.add(i);
        }
    }

    //Проверка на проигрыш
    public boolean checkEndGame() {
        if (freePositions.size() == 0) {
            for (int i = 0; i < 16; i++) {
                CharSequence value = cells[i].value;
                if (checkAdjustCell(value, cells[i].right)) return false;
                if (checkAdjustCell(value, cells[i].up)) return false;
                if (checkAdjustCell(value, cells[i].down)) return false;
                if (checkAdjustCell(value, cells[i].left)) return false;
            }
            return true;
        }
        return false;
    }

    //Проверка на соединение с соседней клеткой
    private boolean checkAdjustCell(CharSequence value, Cell cell) {
        if (cell != null) {
            if (value == cell.value) {
                return true;
            }
        }
        return false;
    }

    public CharSequence getCellValue(int i) {
        return cells[i].value;
    }

    public boolean moveRight() {
        return isMovesMade(3, 0, 1, RIGHT);
    }

    public boolean moveLeft() {
        return isMovesMade(1, 3, -1, LEFT);
    }

    public boolean moveDown() {
        return isMovesMade(3, 0, 1, DOWN);
    }

    public boolean moveUp() {
        return isMovesMade(1, 3, -1, UP);
    }

    //метод вызывающий движение, и возвращающий были ли они
    private boolean isMovesMade(int start, int end, int increment, int direction) {
        boolean movesMade = false;
        while (move4(start, end, direction)) {
            movesMade = true;
            end += increment;
        }
        if (movesMade) {
            resetCellsChanged();
        }
        return movesMade;
    }

    public void resetMovedCells() {
        movedCells.clear();
    }

    //основной метод для перемещения значений
    private boolean move4(int start, int end, int direction) {
        boolean movesMade = false;
        boolean notEnd = true;
        int increment = 0;
        if (direction == UP || direction == LEFT) {
            increment = 1;
            if (start > end) return false;
        } else if (direction == DOWN || direction == RIGHT) {
            increment = -1;
        }
        for (int i = start; notEnd; i = i + increment) {
            checkMoves(i, direction);
            if (canMove.size() > 0) {
                for (Integer ind : canMove) {
                    Cell adjustCell = direction == UP ? cells[ind].up : direction == DOWN ? cells[ind].down : direction == RIGHT ? cells[ind].right : cells[ind].left;
                    if (adjustCell.value == "") {
                        adjustCell.value = cells[ind].value;
                        if (cells[ind].changed) {
                            cells[ind].changed = false;
                            adjustCell.changed = true;
                        }
                    } else {
                        adjustCell.value = getString(adjustCell.value);
                        adjustCell.changed = true;
                    }
                    freePositions.remove(Integer.valueOf(adjustCell.position));
                    cells[ind].value = "";
                    freePositions.add(ind);
                    movedCells.add(adjustCell.position);
                    movedCells.add(cells[ind].position);
                }
                updater.updateMoved(movedCells);
                resetMovedCells();
                movesMade = true;
            }
            if (i == end) notEnd = false;
        }
        return movesMade;
    }


    //удваивание результата
    @NonNull
    private String getString(CharSequence up) {
        Integer value = Integer.parseInt(up.toString()) * 2;
        setResult(value);
        return value.toString();
    }

    private void setResult(int value) {
        result += value;
    }

    //Проверка на существование ходов
    public void checkMoves(int index, int direction) {
        Cell checked = null;
        canMove.clear();
        int increment;//Для вверх вниз
        int start;//для вверха
        int end;
        if (direction == LEFT || direction == RIGHT) {// для влево вправо
            increment = 4;
            start = index;
            end = 13 + index;
        } else {//Для вверх вниз
            increment = 1;
            start = index * 4;
            end = start + 4;
        }

        for (int i = start; i < end; i = i + increment) {
            if (direction == UP) {
                checked = cells[i].up;
            } else if (direction == DOWN) {
                checked = cells[i].down;
            } else if (direction == LEFT) {
                checked = cells[i].left;
            } else if (direction == RIGHT) {
                checked = cells[i].right;
            }
            if (cells[i].value == "" || null == checked) continue;
            if (!checked.changed && !cells[i].changed)
                if (checked.value.equals("") || cells[i].value.equals(checked.value))
                    canMove.add(i);
        }
    }

    public int newCell() {
        int pos = random.nextInt(freePositions.size());
        int id = freePositions.remove(pos);
        cells[id].value = "2";
        return id;
    }

    public void resetGame() {
        resetCellsValues();
        createFreePositions();
        result = 0;
    }

    private void resetCellsValues() {
        for (Cell cell : cells) {
            cell.value = "";
        }
    }

    private void resetCellsChanged() {
        for (int i = 0; i < 16; i++) {
            cells[i].changed = false;
        }
    }

    private static class Cell {
        final int position;
        CharSequence value;
        Cell up, down, left, right;
        boolean changed = false;

        public Cell(int position, CharSequence value) {
            this.position = position;
            this.value = value;
        }
    }

}
