import models.*;

import java.util.ArrayList;
import java.util.Random;

public class Ai extends Player {

    private int doneActions = 0;
    private final int maxDepth = 3;
    private static int nodeCount =0;
    private static int pruningCount =0;
    public Ai(PlayerType type) {
        super(type);
    }

    @Override
    public Action forceAttack(Game game) {
        int maxValue = Integer.MIN_VALUE;
        Action bestAction = null;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        if (doneActions == 0 && getType() == PlayerType.white) {
            for (Action action : actions) {
                nodeCount++;
                if (action.getType() == Action.ActionType.attack) {
                    Game copyGame = game.copy();
                    if (copyGame.applyActionTwo(this, action, true)) {
                        continue;
                    }
                    Player winner = copyGame.getWinner();
                    if (winner != null) {
                        if (winner.getType() == getType()) {
                            return action;
                        }
                    } else {
                        int temp = Math.max(maxValue, minForceAttack(copyGame, 0,Integer.MIN_VALUE, Integer.MAX_VALUE));                        if (temp >= maxValue) {
                            maxValue = temp;
                            bestAction = action;
                        }
                    }
                }
            }
        } else {
            for (Action action : actions) {
                nodeCount++;
                if (action.getType() == Action.ActionType.attack) {
                    Game copyGame = game.copy();
                    if (copyGame.applyActionTwo(this, action, true)) {
                        continue;
                    }
                    Player winner = copyGame.getWinner();
                    if (winner != null) {
                        if (winner.getType() == getType()) {
                            return action;
                        }
                    } else {
                        int temp = Math.max(maxValue, maxSecondMove(copyGame, 0, Integer.MIN_VALUE, Integer.MAX_VALUE));
                        if (temp >= maxValue) {
                            maxValue = temp;
                            bestAction = action;
                        }
                    }
                }
            }
        }
        doneActions++;
        return bestAction;
    }

    private int eval(Game game) {
        int whitebeads = 0;
        int blackbeads = 0;
        int point = 0;
        int WTzaarsCount = 0;
        int WTzarrasCount = 0;
        int WTottsCount = 0;
        int BTzaarsCount = 0;
        int BTzarrasCount = 0;
        int BTottsCount = 0;
        int WAttacks = 0;
        int BAttacks = 0;
        int BTzaarsAttacked = 0;
        int BTzarrasAttacked = 0;
        int BTottsAttacked= 0;
        int WTzaarsAttacked = 0;
        int WTzarrasAttacked = 0;
        int WTottsAttacked= 0;
        int Wpoints = 0;
        int Bpoints = 0;
        Board board = game.getBoard();


        if(game.getWinner() != null){
            if(game.getWinner().getType() == PlayerType.white)
                return Integer.MAX_VALUE;

            else
                return Integer.MIN_VALUE;
        }
        else {
            for (Board.BoardRow row : board.getRows()) {
                for (Board.BoardCell cell : row.boardCells) {
                    if (cell.bead != null) {
                        if (cell.bead.getPlayer().getType() == PlayerType.white) {
                            whitebeads = +cell.bead.getHeight();

                            switch (cell.bead.getType()) {
                                case Tzaars -> WTzaarsCount++;
                                case Tzarras -> WTzarrasCount++;
                                case Totts -> WTottsCount++;
                            }

                            for (Action action : cell.bead.getActions(cell)) {
                                if (action.getType() == Action.ActionType.attack) {
                                    WAttacks++;
                                    switch (action.getTarget().bead.getType()) {
                                        case Tzaars -> BTzaarsAttacked++;
                                        case Tzarras -> BTzarrasAttacked++;
                                        case Totts -> BTottsAttacked++;
                                    }

                                }
                            }
                        }
                            if (cell.bead.getPlayer().getType() == PlayerType.black) {
                                blackbeads = +cell.bead.getHeight();

                                switch (cell.bead.getType()) {
                                    case Tzaars -> BTzaarsCount++;
                                    case Tzarras -> BTzarrasCount++;
                                    case Totts -> BTottsCount++;
                                }

                                for (Action action : cell.bead.getActions(cell)) {
                                    if (action.getType() == Action.ActionType.attack) {
                                        BAttacks++;
                                        switch (action.getTarget().bead.getType()) {
                                            case Tzaars -> WTzaarsAttacked++;
                                            case Tzarras -> WTzarrasAttacked++;
                                            case Totts -> WTottsAttacked++;
                                        }
                                    }
                                }
                            }
                    }
                }
            }
          //  Wpoints = (whitebeads + 2*WAttacks + 4*( ((1/(BTottsCount))*(BTottsAttacked/2+1)) + ((1/(BTzaarsCount))*(BTzaarsAttacked/2+1)) + ((1/(BTzarrasCount))*(BTzarrasAttacked/2+1)) ) );
          //  Bpoints = (blackbeads + 2*BAttacks + 4*( ((1/(WTottsCount))*(WTottsAttacked/2+1)) + ((1/(WTzaarsCount))*(WTzaarsAttacked/2+1)) + ((1/(WTzarrasCount))*(WTzarrasAttacked/2+1)) ) );

            //int Wpoints2 = (whitebeads + 2*WAttacks + 4*( ((1/(BTottsCount))*((2/(((BTottsAttacked-2)^2)+1))*BTottsAttacked+1)) + ((1/(BTzaarsCount))*((2/(((BTzaarsAttacked-2)^2)+1))*BTzaarsAttacked+1)) + ((1/(BTzarrasCount))*(((2/(((BTzarrasAttacked-2)^2)+1))*BTzarrasAttacked+1))) ) );
            //int Bpoints2 = (blackbeads + 2*BAttacks + 4*( ((1/(WTottsCount))*((2/(((WTottsAttacked-2)^2)+1))*WTottsAttacked+1)) + ((1/(WTzaarsCount))*((2/(((WTzaarsAttacked-2)^2)+1))*WTzaarsAttacked+1)) + ((1/(WTzarrasCount))*(((2/(((WTzarrasAttacked-2)^2)+1))+1)*WTzarrasAttacked)) ) );

            BTottsAttacked = (BTottsAttacked<3)? BTottsAttacked : 3;            //power of attacks in different conditions (limiting)
            BTzaarsAttacked = (BTzaarsAttacked<3)? BTzaarsAttacked : 3;
            BTzarrasAttacked = (BTzarrasAttacked<3)? BTzarrasAttacked : 3;

            WTottsAttacked = (WTottsAttacked<3)? WTottsAttacked : 3;
            WTzaarsAttacked = (WTzaarsAttacked<3)? WTzaarsAttacked : 3;
            WTzarrasAttacked = (WTzarrasAttacked<3)? WTzarrasAttacked : 3;

            Wpoints = (whitebeads + 2*WAttacks + 3*( ((1/(BTottsCount))*(BTottsAttacked+1)) + ((1/(BTzaarsCount))*(BTzaarsAttacked+1)) + ((1/(BTzarrasCount))*(BTzarrasAttacked+1)) ) );
            Bpoints = (blackbeads + 2*BAttacks + 3*( ((1/(WTottsCount))*(WTottsAttacked+1)) + ((1/(WTzaarsCount))*(WTzaarsAttacked+1)) + ((1/(WTzarrasCount))*(WTzarrasAttacked+1)) ) );
            point = Wpoints - Bpoints;
            return point;


        }


    }

    @Override
    public Action secondAction(Game game) {
        int maxValue = Integer.MIN_VALUE;
        Action bestAction = null;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            nodeCount++;
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(this, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType()) {
                    return action;
                }
            } else {
                int temp = Math.max(maxValue, minForceAttack(copyGame, 0, Integer.MIN_VALUE, Integer.MAX_VALUE));
                if (temp > maxValue) {
                    maxValue = temp;
                    bestAction = action;
                }
            }
        }
        doneActions++;
        if (bestAction == null){
            Random random = new Random();
            return actions.get(random.nextInt(actions.size()));
        }
        return bestAction;
    }

    private int maxForceAttack(Game game, int depth , int alpha , int beta) {
        if (depth == maxDepth) {
            return eval(game);
        }
        int maxValue = Integer.MIN_VALUE;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            nodeCount++;
            if (action.getType() == Action.ActionType.attack) {
                Game copyGame = game.copy();
                if (copyGame.applyActionTwo(this, action, true)) {
                    continue;
                }
                Player winner = copyGame.getWinner();
                if (winner != null) {
                    if (winner.getType() == getType()) {
                        return Integer.MAX_VALUE;
                    }
                } else {
                    maxValue = Math.max(maxValue, maxSecondMove(copyGame, depth + 1, alpha, beta));
                    alpha = Math.max(alpha , maxValue);
                    if (alpha >= beta){
                        pruningCount++;
                        break;}
                }
            }
        }
        return maxValue;
    }

    private int maxSecondMove(Game game, int depth, int alpha, int beta) {
        if (depth == maxDepth) {
            return eval(game);
        }
        int maxValue = Integer.MIN_VALUE;
        ArrayList<Action> actions = getAllActions(game.getBoard());
        for (Action action : actions) {
            nodeCount++;
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(this, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType()) {
                    return Integer.MAX_VALUE;
                } else {
                    return Integer.MIN_VALUE;
                }
            } else {
                maxValue = Math.max(maxValue, minForceAttack(copyGame, depth + 1,alpha, beta));
                alpha = Math.max(alpha , maxValue);
                if (alpha >= beta){
                    pruningCount++;
                    break;}
            }
        }
        return maxValue;
    }

    private int minForceAttack(Game game, int depth, int alpha , int beta) {
        if (depth == maxDepth) {
            return eval(game);
        }
        Player opp = getOpp(game);
        int minValue = Integer.MAX_VALUE;
        ArrayList<Action> actions = opp.getAllActions(game.getBoard());
        for (Action action : actions) {
            nodeCount++;
            if (action.getType() == Action.ActionType.attack) {
                Game copyGame = game.copy();
                if (copyGame.applyActionTwo(opp, action, true)) {
                    continue;
                }
                Player winner = copyGame.getWinner();
                if (winner != null) {
                    if (winner.getType() == getType().reverse()) {
                        return Integer.MIN_VALUE;
                    }
                } else {
                    minValue = Math.min(minValue, minSecondMove(copyGame, depth + 1, alpha , beta));
                    beta = Math.min(alpha , minValue);
                    if (alpha >= beta){
                        pruningCount++;
                        break;}
                }
            }
        }
        return minValue;

    }

    private int minSecondMove(Game game, int depth, int alpha, int beta) {
        if (depth == maxDepth) {
            return eval(game);
        }

        Player opp = getOpp(game);
        int minValue = Integer.MAX_VALUE;
        ArrayList<Action> actions = opp.getAllActions(game.getBoard());
        for (Action action : actions) {
            nodeCount++;
            Game copyGame = game.copy();
            if (copyGame.applyActionTwo(opp, action, false)) {
                continue;
            }
            Player winner = copyGame.getWinner();
            if (winner != null) {
                if (winner.getType() == getType().reverse()) {
                    return Integer.MIN_VALUE;
                } else {
                    return Integer.MAX_VALUE;
                }
            } else {
                minValue = Math.min(minValue, maxForceAttack(copyGame, depth + 1, alpha, beta));
                beta = Math.min(alpha , minValue);
                if (alpha >= beta){
                    pruningCount++;
                    break;}
            }
        }

        return minValue;
    }

    public Player getOpp(Game game) {
        if (getType() == PlayerType.white) {
            return game.getBlack();
        } else {
            return game.getWhite();
        }
    }

    public static int getNodeCount() {
        return nodeCount;
    }

    public static int getPruningCount() {
        return pruningCount;
    }
}
