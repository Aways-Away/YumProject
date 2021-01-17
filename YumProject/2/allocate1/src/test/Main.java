package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner cin = new Scanner(new BufferedReader(new FileReader("data/Data2.txt")));
        int iter  = 0;
        int[][] f = new int[19][19];
        while (iter < 19) {
            String line = cin.nextLine().trim();
            String[] lineSplit = line.split("\\s+");
            for (int i = 0; i < lineSplit.length; i++) {
                int fit = Integer.parseInt(lineSplit[i]);
                f[iter][iter + i]  = fit;
            }
            iter++;
        }

        int[][] fitness = new int[20][20];
        for(int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                if(f[i][j] != 0) {
                    fitness[i][j + 1] = fitness[j + 1][i] = f[i][j];
                }
            }
        }

        ArrayList<Integer> customers = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            customers.add(i);
        }

        List<List<Integer>> tables = new ArrayList<>();
        // TODO: five table, four customers (5,4) -> (3,8)
        for(int i = 0; i < 5; i++) {
            tables.add(new ArrayList<>(4));
        }

        for (int i = 0; i < tables.size(); i++) {
            List<Integer> table = tables.get(i);
            Random random = new Random();
            int cidx = random.nextInt(customers.size());
            table.add(customers.get(cidx));
            customers.remove(cidx);
        }

        // greedy insertion
        while (!customers.isEmpty()) {
            int cId = customers.get(0);
            customers.remove(0);

            // fit table id
            int tableId = -1;

            // maximal fitness value
            int fitVal = -1;

            for (int i = 0; i < tables.size(); i++) {
                List<Integer> list = tables.get(i);
                // TODO: list.size=4 -> list.size=8
                if (list.size() == 4) {
                    continue;
                }
                int maxFit = 0;
                for (int j = 0; j < list.size(); j++) {
                    // the customer id which is already allocated
                    int id = list.get(j);
                    maxFit += fitness[id][cId];
                }
                maxFit /= list.size();
                if(maxFit > fitVal) {
                    fitVal = maxFit;
                    tableId = i;
                }
            }
            tables.get(tableId).add(cId);
        }

        int initSol = Utility.calFitness(tables, fitness);
        System.out.println("The total fitness of initial solution is " + initSol);

        iter = 0;
        int noImprove = 0;
        boolean flag = false;
        List<List<Integer>> bestSol = Utility.copySol(tables);
        int best = Utility.calFitness(bestSol, fitness);
        while (iter < 10000) {
            if (noImprove > 10) {
                flag = true;
            }
            Exchange exchange = new Exchange(fitness);
            int[][] tabuList = new int[20][20];
            Exchange.Opt opt = exchange.explore(flag, tables, tabuList, 5, iter, initSol);
            /*if (opt.increase <= 0) {
                noImprove++;
            }
            else {
                noImprove = 0;
                flag = false;
                bestSol = Utility.copySol(tables);
            }*/
            exchange.modify(opt, tabuList, iter);
            int currentValue = Utility.calFitness(tables, fitness);
            if (currentValue > best) {
                best = currentValue;
                noImprove = 0;
                flag = false;
                bestSol = Utility.copySol(tables);
            }
            else {
                noImprove++;
            }
            System.out.println("iteration " + iter + " the best solution fitness value is " + best + " current fitness value is " + currentValue);
            iter++;
        }
        for (int i = 0; i < bestSol.size(); i++) {
            int idx = i + 1;
            StringBuilder sb = new StringBuilder();
            sb.append("Table").append(" ").append(idx).append(" : ");
            for (int j = 0; j < bestSol.get(i).size(); j++) {
                int a = 'A';
                char chr = (char) (bestSol.get(i).get(j) + a);
                sb.append(chr);
            }
            System.out.println(sb.toString());
        }
    }


}

class Exchange{
    public static class Opt{
        public List<Integer> table1;
        public int cid1;
        public List<Integer> table2;
        public int cid2;
        public int increase;
        public int idx1;
        public int idx2;

        public Opt(List<Integer> table1, int cid1, int idx1, List<Integer> table2, int cid2, int idx2, int increase) {
            this.table1 = table1;
            this.cid1 = cid1;
            this.table2 = table2;
            this.cid2 = cid2;
            this.increase = increase;
            this.idx1 = idx1;
            this.idx2 = idx2;
        }
    }

    public int[][] fitness;
    public Random rand;
    public Exchange(int[][] fitness) {
        this.fitness = fitness;
        rand = new Random();
    }

    public Opt explore(boolean random, List<List<Integer>> tables, int[][] tabuList, int tabuTenure, int iter, int best) {
        Opt bestOpt = null;
        int fitValue = Utility.calFitness(tables, fitness);
        ArrayList<Opt> list = new ArrayList<>();
        for (int i = 0; i < tables.size(); i++) {
            List<Integer> table1 = tables.get(i);
            for (int j = 0; j < table1.size(); j++) {
                int cid1 = table1.get(j);
                for (int k = 0; k < tables.size(); k++) {
                    if (k == i) {
                        continue;
                    }
                    List<Integer> table2 = tables.get(k);
                    for (int l = 0; l < table2.size(); l++) {
                        int cid2 = table2.get(l);
                        int increase = Utility.calFitnessIncrease(table1, cid1, table2, cid2, fitness);
                        Opt opt = new Opt(table1, cid1, j, table2, cid2, l, increase);
                        list.add(opt);
                        if (iter - tabuList[cid1][cid2] <= tabuTenure && fitValue + increase < best) {
                            continue;
                        }
                        if (bestOpt == null || increase > bestOpt.increase) {
                            bestOpt = opt;
                        }
                    }
                }
            }
        }
        if (!random || bestOpt != null) {
            return bestOpt;
        }
        else {
            int p = rand.nextInt(list.size());
            return list.get(p);
        }
    }

    public void modify(Opt bestOpt, int[][] tabuList, int iter) {
        tabuList[bestOpt.cid1][bestOpt.cid2] = iter;
        tabuList[bestOpt.cid2][bestOpt.cid1] = iter;
        int cid1 = bestOpt.cid1;
        bestOpt.table1.set(bestOpt.idx1, bestOpt.cid2);
        bestOpt.table2.set(bestOpt.idx2, cid1);
    }
}

class Utility
{
    /**
     * calculate the solution total fitness
     * @param tables the list of tables
     * @param fitness fitness matrix
     * @return total fitness of the solution
     */
    public static int calFitness(List<List<Integer>> tables, int[][] fitness) {
        int totalFitness = 0;
        for (int i = 0; i < tables.size(); i++) {
            List<Integer> table = tables.get(i);
            for (int j = 0; j < table.size() - 1; j++) {
                for (int k = j + 1; k < table.size(); k++) {
                    totalFitness += fitness[table.get(j)][table.get(k)];
                }
            }
        }
        return totalFitness;
    }

    /**
     * calculate he fitness increase value
     * @param table1 table 1
     * @param cid1 customer id in table 1
     * @param table2 table 2
     * @param cid2 customer id in table 2
     * @return increase value
     */
    public static int calFitnessIncrease(List<Integer> table1, int cid1, List<Integer> table2, int cid2, int[][] fit) {
        int fit1 = 0;
        int fit2 = 0;
        int newFit1 = 0;
        int newFit2 = 0;
        for(int cid : table1) {
            if(cid != cid1) {
                fit1 += fit[cid1][cid];
                newFit1 += fit[cid2][cid];
            }
        }
        for(int cid : table2) {
            if (cid != cid2) {
                fit2 += fit[cid2][cid];
                newFit2 += fit[cid1][cid];
            }
        }

        return newFit1 + newFit2 - fit1 - fit2;
    }

    public static List<List<Integer>> copySol(List<List<Integer>> tables) {
        List<List<Integer>> ret = new ArrayList<>();
        for (List<Integer> table : tables) {
            ret.add(new ArrayList<>(table));
        }
        return ret;
    }
}