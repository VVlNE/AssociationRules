package ru.vvine.associationrules;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Vector;

public class AssociationRulesManager implements Serializable {
    private String title;
    private Integer[] transactionNumber;
    private String[] element;
    private boolean[][] transaction;
    private char divider;
    private double[][] support;
    private double[][] confidence;
    private double[][] lift;
    private double[][] leverage;
    private double[][] conviction;

    public AssociationRulesManager(String data) {
        divider = '\t';
        selector(data);
    }

    private void selector(String data) {
        title = data.substring(0, data.indexOf('\n'));
        data = data.substring(data.indexOf('\n') + 1, data.length());
        selectTransactionNumbers(data.split("\n"));
        selectElements(data.split("\n"));
        selectTransactions(data.split("\n"));
    }

    private void selectTransactionNumbers(String[] data) {
        Vector<Integer> vector = new Vector<Integer>(0,1);

        for (String str : data) {
            int i = Integer.parseInt(str.substring(0, str.indexOf(divider)));
            if (!vector.contains(i))
                vector.add(i);
        }

        transactionNumber = vector.toArray(new Integer[vector.size()]);
    }

    private void selectElements(String[] data) {
        Vector<String> vector = new Vector<String>(0,1);

        for (String str : data) {
            String i = str.substring(str.indexOf(divider) + 1, str.length());
            if (!vector.contains(i))
                vector.add(i);
        }

        element = vector.toArray(new String[vector.size()]);
    }

    private void selectTransactions(String[] data) {
        transaction = new boolean[transactionNumber.length][element.length];
        for (int i = 0; i < transaction.length; i++) {
            for (int j = 0; j < transaction[i].length; j++) {
                transaction[i][j] = false;
            }
        }
        for (String str : data) {
            int i = Arrays.asList(transactionNumber).indexOf(Integer.parseInt(str.substring(0, str.indexOf(divider)))),
            j = Arrays.asList(element).indexOf(str.substring(str.indexOf(divider) + 1, str.length()));
            transaction[i][j] = true;
        }
    }

    private void calculateSupportMeasure() {
        if (support != null)
            return;

        support = new double[element.length][element.length];

        for (int i = 0; i < element.length; i++) {
            for (int j = i + 1; j < element.length; j++) {
                int counter = 0;
                for (int k = 0; k < transactionNumber.length; k++) {
                    if (transaction[k][i] && transaction[k][j])
                        counter++;
                }

                support[i][j] = (double) counter/transactionNumber.length;
                //support[i][j] = counter;
                //support[i][j] /= transactionNumber.length;
                support[j][i] = support[i][j];
            }

            int counter = 0;
            for (int k = 0; k < transactionNumber.length; k++) {
                if (transaction[k][i])
                    counter++;
            }

            support[i][i] = (double) counter/transactionNumber.length;
        }
    }

    private void calculateConfidenceMeasure() {
        if (confidence != null)
            return;

        confidence = new double[element.length][element.length];

        for (int i = 0; i < element.length; i++) {
            for (int j = 0; j < element.length; j++) {
                int counter = 0, iCounter = 0, jCounter = 0;
                for (int k = 0; k < transactionNumber.length; k++) {
                    if (transaction[k][i] && transaction[k][j])
                        counter++;
                    if (transaction[k][i])
                        iCounter++;
                    if (transaction[k][j])
                        jCounter++;
                }

                confidence[i][j] = (double) counter/iCounter;
                confidence[j][i] = (double) counter/jCounter;
            }

            confidence[i][i] = 1;
        }
    }

    private void calculateLiftMeasure() {
        if (lift != null)
            return;

        calculateSupportMeasure();
        calculateConfidenceMeasure();

        lift = new double[element.length][element.length];

        for (int i = 0; i < element.length; i++) {
            for (int j = 0; j < element.length; j++) {
                lift[i][j] = (double) confidence[i][j]/support[j][j];
            }
        }
    }

    private void calculateLeverageMeasure() {
        if (leverage != null)
            return;

        calculateSupportMeasure();

        leverage = new double[element.length][element.length];

        for (int i = 0; i < element.length; i++) {
            for (int j = 0; j < element.length; j++) {
                leverage[i][j] = (double) (support[i][j] - support[i][i] * support[j][j]) * 100;
            }
        }
    }

    private void calculateConvictionMeasure() {
        if (conviction != null)
            return;

        calculateSupportMeasure();

        conviction = new double[element.length][element.length];

        for (int i = 0; i < element.length; i++) {
            for (int j = 0; j < element.length; j++) {
                conviction[i][j] = (double) support[i][j] / (support[i][i] * support[j][j]);
            }
        }
    }

    public String getSupportMeasure() {
        return getSupportMeasure(getMinSupportMeasure(), getMaxSupportMeasure());
    }

    public String getSupportMeasure(double min, double max) {
        calculateSupportMeasure();

        String data = "";

        int size = 0;
        int num = (element.length + 1) * element.length / 2 - element.length;
        double[] newSupport = new double[num];

        for (int i = 0; i < element.length; i++) {
            for (int j = i + 1; j < element.length; j++) {
                newSupport[size] = support[i][j];
                size++;
            }
        }

        Arrays.sort(newSupport);
        for (int i = 0; i < size / 2; i++) {
            double j = newSupport[i];
            newSupport[i] = newSupport[size - i - 1];
            newSupport[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = i + 1; j < element.length; j++) {
                    if (support[i][j] == newSupport[size]) {
                        if ((support[i][j] >= min) && (support[i][j] <= max))
                            data += "supp(" + element[i] + " ↔ " + element[j] + ") = " + String.format("%.2f", support[i][j]) + "\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        if (data.length() > 0)
            data = data.substring(0, data.length() - 1);

        return data;
    }

    public String getTheBestSupportMeasure(int num) {
        calculateSupportMeasure();

        String data = "";

        int size = (element.length + 1) * element.length / 2 - element.length;
        double[] newSupport = new double[size];
        size = 0;
        for (int i = 0; i < element.length; i++) {
            for (int j = i + 1; j < element.length; j++) {
                newSupport[size] = support[i][j];
                size++;
            }
        }

        Arrays.sort(newSupport);
        for (int i = 0; i < size / 2; i++) {
            double j = newSupport[i];
            newSupport[i] = newSupport[size - i - 1];
            newSupport[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = i + 1; j < element.length; j++) {
                    if (support[i][j] == newSupport[size]) {
                        data += "supp(" + element[i] + " ↔ " + element[j] + ") = " + String.format("%.2f", support[i][j]) + "\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        data += "...";

        return data;
    }

    public String getConfidenceMeasure() {
        return getConfidenceMeasure(getMinConfidenceMeasure(), getMaxConfidenceMeasure());
    }

    public String getConfidenceMeasure(double min, double max) {
        calculateConfidenceMeasure();

        String data = "";

        int size = 0;
        int num = element.length * element.length - element.length;
        double[] newConfidence = new double[num];

        for (int i = 0; i < element.length; i++) {
            for (int j = 0; j < element.length; j++) {
                if (i != j) {
                    newConfidence[size] = confidence[i][j];
                    size++;
                }
            }
        }

        Arrays.sort(newConfidence);
        for (int i = 0; i < size / 2; i++) {
            double j = newConfidence[i];
            newConfidence[i] = newConfidence[size - i - 1];
            newConfidence[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = 0; j < element.length; j++) {
                    if ((confidence[i][j] == newConfidence[size]) && (i != j)) {
                        if ((confidence[i][j] >= min) && (confidence[i][j] <= max))
                            data += "conf(" + element[i] + " → " + element[j] + ") = " + String.format("%.2f", confidence[i][j]) + "\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        if (data.length() > 0)
            data = data.substring(0, data.length() - 1);

        return data;
    }

    public String getTheBestConfidenceMeasure(int num) {
        calculateConfidenceMeasure();

        String data = "";

        int size = element.length * element.length - element.length;
        double[] newConfidence = new double[size];
        size = 0;
        for (int i = 0; i < element.length; i++) {
            for (int j = 0; j < element.length; j++) {
                if (i != j) {
                    newConfidence[size] = confidence[i][j];
                    size++;
                }
            }
        }

        Arrays.sort(newConfidence);
        for (int i = 0; i < size / 2; i++) {
            double j = newConfidence[i];
            newConfidence[i] = newConfidence[size - i - 1];
            newConfidence[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = 0; j < element.length; j++) {
                    if ((confidence[i][j] == newConfidence[size]) && (i != j)) {
                        data += "conf(" + element[i] + " → " + element[j] + ") = " + String.format("%.2f", confidence[i][j]) + "\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        data += "...";

        return data;
    }

    public String getLiftMeasure() {
        return getLiftMeasure(getMinLiftMeasure(), getMaxLiftMeasure());
    }

    public String getLiftMeasure(double min, double max) {
        calculateLiftMeasure();

        String data = "";

        int size = 0;
        int num = element.length * element.length - element.length;
        double[] newLift = new double[num];

        for (int i = 0; i < element.length; i++) {
            for (int j = 0; j < element.length; j++) {
                if (i != j) {
                    newLift[size] = lift[i][j];
                    size++;
                }
            }
        }

        Arrays.sort(newLift);

        for (int i = 0; i < size / 2; i++) {
            double j = newLift[i];
            newLift[i] = newLift[size - i - 1];
            newLift[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = 0; j < element.length; j++) {
                    if ((lift[i][j] == newLift[size]) && (i != j)) {
                        if ((lift[i][j] >= min) && (lift[i][j] <= max))
                            data += "lift(" + element[i] + " → " + element[j] + ") = " + String.format("%.2f", lift[i][j]) + "\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        if (data.length() > 0)
            data = data.substring(0, data.length() - 1);

        return data;
    }

    public String getTheBestLiftMeasure(int num) {
        calculateLiftMeasure();

        String data = "";

        int size = element.length * element.length - element.length;
        double[] newLift = new double[size];
        size = 0;
        for (int i = 0; i < element.length; i++) {
            for (int j = 0; j < element.length; j++) {
                if (i != j) {
                    newLift[size] = lift[i][j];
                    size++;
                }
            }
        }

        Arrays.sort(newLift);

        for (int i = 0; i < size / 2; i++) {
            double j = newLift[i];
            newLift[i] = newLift[size - i - 1];
            newLift[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = 0; j < element.length; j++) {
                    if ((lift[i][j] == newLift[size]) && (i != j)) {
                        data += "lift(" + element[i] + " → " + element[j] + ") = " + String.format("%.2f", lift[i][j]) + "\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        data += "...";

        return data;
    }

    public String getLeverageMeasure() {
        return getLeverageMeasure(getMinLeverageMeasure(), getMaxLeverageMeasure());
    }

    public String getLeverageMeasure(double min, double max) {
        calculateLeverageMeasure();

        String data = "";

        int size = 0;
        int num = (element.length + 1) * element.length / 2 - element.length;
        double[] newLeverage = new double[num];

        for (int i = 0; i < element.length; i++) {
            for (int j = i + 1; j < element.length; j++) {
                newLeverage[size] = leverage[i][j];
                size++;
            }
        }

        Arrays.sort(newLeverage);

        size = newLeverage.length;
        for (int i = 0; i < size / 2; i++) {
            double j = newLeverage[i];
            newLeverage[i] = newLeverage[size - i - 1];
            newLeverage[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = i + 1; j < element.length; j++) {
                    if (leverage[i][j] == newLeverage[size]) {
                        if ((leverage[i][j] >= min) && (leverage[i][j] <= max))
                            data += "levr(" + element[i] + " ↔ " + element[j] + ") = " + String.format("%.2f", leverage[i][j]) + "%\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        if (data.length() > 0)
            data = data.substring(0, data.length() - 1);

        return data;
    }

    public String getTheBestLeverageMeasure(int num) {
        calculateLeverageMeasure();

        String data = "";

        int size = (element.length + 1) * element.length / 2 - element.length;
        double[] newLeverage = new double[size];
        size = 0;
        for (int i = 0; i < element.length; i++) {
            for (int j = i + 1; j < element.length; j++) {
                newLeverage[size] = leverage[i][j];
                size++;
            }
        }

        Arrays.sort(newLeverage);

        size = newLeverage.length;
        for (int i = 0; i < size / 2; i++) {
            double j = newLeverage[i];
            newLeverage[i] = newLeverage[size - i - 1];
            newLeverage[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = i + 1; j < element.length; j++) {
                    if (leverage[i][j] == newLeverage[size]) {
                        data += "levr(" + element[i] + " ↔ " + element[j] + ") = " + String.format("%.2f", leverage[i][j]) + "%\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        data += "...";

        return data;
    }

    public String getConvictionMeasure() {
        return getConvictionMeasure(getMinConvictionMeasure(), getMaxConvictionMeasure());
    }

    public String getConvictionMeasure(double min, double max) {
        calculateConvictionMeasure();

        String data = "";

        int size = 0;
        int num = (element.length + 1) * element.length / 2 - element.length;
        double[] newConviction = new double[num];

        for (int i = 0; i < element.length; i++) {
            for (int j = i + 1; j < element.length; j++) {
                if (i != j) {
                    newConviction[size] = conviction[i][j];
                    size++;
                }
            }
        }

        Arrays.sort(newConviction);

        for (int i = 0; i < size / 2; i++) {
            double j = newConviction[i];
            newConviction[i] = newConviction[size - i - 1];
            newConviction[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = i + 1; j < element.length; j++) {
                    if ((conviction[i][j] == newConviction[size]) && (i != j)) {
                        if ((conviction[i][j] >= min) && (conviction[i][j] <= max))
                            data += "conv(" + element[i] + " ↔ " + element[j] + ") = " + String.format("%.2f", conviction[i][j]) + "\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        if (data.length() > 0)
            data = data.substring(0, data.length() - 1);

        return data;
    }

    public String getTheBestConvictionMeasure(int num) {
        calculateConvictionMeasure();

        String data = "";

        int size = (element.length + 1) * element.length / 2 - element.length;
        double[] newConviction = new double[size];
        size = 0;
        for (int i = 0; i < element.length; i++) {
            for (int j = i + 1; j < element.length; j++) {
                if (i != j) {
                    newConviction[size] = conviction[i][j];
                    size++;
                }
            }
        }

        Arrays.sort(newConviction);

        for (int i = 0; i < size / 2; i++) {
            double j = newConviction[i];
            newConviction[i] = newConviction[size - i - 1];
            newConviction[size - i - 1] = j;
        }

        size = 0;
        while (size < num) {
            int k = 0;
            for (int i = 0; i < element.length; i++) {
                for (int j = i + 1; j < element.length; j++) {
                    if ((conviction[i][j] == newConviction[size]) && (i != j)) {
                        data += "conv(" + element[i] + " ↔ " + element[j] + ") = " + String.format("%.2f", conviction[i][j]) + "\n";
                        k++;
                    }
                }
            }

            size += k;
        }

        data += "...";

        return data;
    }

    public double getMinSupportMeasure() {
        calculateSupportMeasure();

        double min = support[0][1];

        for (int i = 0; i < support.length; i++) {
            for (int j = i + 1; j < support[i].length; j++) {
                if (support[i][j] < min)
                    min = support[i][j];
            }
        }

        return min;
    }

    public double getMaxSupportMeasure() {
        calculateSupportMeasure();

        double max = support[0][1];

        for (int i = 0; i < support.length; i++) {
            for (int j = i + 1; j < support[i].length; j++) {
                if (support[i][j] > max)
                    max = support[i][j];
            }
        }

        return max;
    }

    public double getMinConfidenceMeasure() {
        calculateConfidenceMeasure();

        double min = confidence[0][1];

        for (int i = 0; i < confidence.length; i++) {
            for (int j = 0; j < confidence[i].length; j++) {
                if ((i != j) && (confidence[i][j] < min))
                    min = confidence[i][j];
            }
        }

        return min;
    }

    public double getMaxConfidenceMeasure() {
        calculateConfidenceMeasure();

        double max = confidence[0][1];

        for (int i = 0; i < confidence.length; i++) {
            for (int j = 0; j < confidence[i].length; j++) {
                if ((i != j) && (confidence[i][j] > max))
                    max = confidence[i][j];
            }
        }

        return max;
    }

    public double getMinLiftMeasure() {
        calculateLiftMeasure();

        double min = lift[0][1];

        for (int i = 0; i < lift.length; i++) {
            for (int j = 0; j < lift[i].length; j++) {
                if ((i != j) && (lift[i][j] < min))
                    min = lift[i][j];
            }
        }

        return min;
    }

    public double getMaxLiftMeasure() {
        calculateLiftMeasure();

        double max = lift[0][1];

        for (int i = 0; i < lift.length; i++) {
            for (int j = 0; j < lift[i].length; j++) {
                if ((i != j) && (lift[i][j] > max))
                    max = lift[i][j];
            }
        }

        return max;
    }

    public double getMinLeverageMeasure() {
        calculateLeverageMeasure();

        double min = leverage[0][1];

        for (int i = 0; i < leverage.length; i++) {
            for (int j = i + 1; j < leverage[i].length; j++) {
                if (leverage[i][j] < min)
                    min = leverage[i][j];
            }
        }

        return min;
    }

    public double getMaxLeverageMeasure() {
        calculateLeverageMeasure();

        double max = leverage[0][1];

        for (int i = 0; i < leverage.length; i++) {
            for (int j = i + 1; j < leverage[i].length; j++) {
                if (leverage[i][j] > max)
                    max = leverage[i][j];
            }
        }

        return max;
    }

    public double getMinConvictionMeasure() {
        calculateConvictionMeasure();

        double min = conviction[0][1];

        for (int i = 0; i < conviction.length; i++) {
            for (int j = i + 1; j < conviction[i].length; j++) {
                if (conviction[i][j] < min)
                    min = conviction[i][j];
            }
        }

        return min;
    }

    public double getMaxConvictionMeasure() {
        calculateConvictionMeasure();

        double max = conviction[0][1];

        for (int i = 0; i < conviction.length; i++) {
            for (int j = i + 1; j < conviction[i].length; j++) {
                if (conviction[i][j] > max)
                    max = conviction[i][j];
            }
        }

        return max;
    }

    public int getTransactionNumber() {
        return transactionNumber.length;
    }

    public String getTransactionNumber(int num) {
        return String.format("%08d", transactionNumber[num]);
    }

    public String getTransactionData(int num) {
        String transactionData = "";

        for (int i = 0; i < transaction[num].length; i++) {
            if (transaction[num][i])
                transactionData += element[i] + "\n";
        }
        transactionData = transactionData.substring(0, transactionData.length() - 1);

        return transactionData;
    }

    public String[] getElements() {
        return element;
    }

    public boolean hasRecommendElements(int num) {
        calculateSupportMeasure();
        calculateConfidenceMeasure();
        calculateLiftMeasure();
        calculateLeverageMeasure();
        calculateConvictionMeasure();

        for (int i = 0; i < lift[num].length; i++) {
            if (i != num)
                if (lift[num][i] > 1)
                    return true;
        }

        return false;
    }

    public String[] getRecommendElements(int num) {
        calculateSupportMeasure();
        calculateConfidenceMeasure();
        calculateLiftMeasure();
        calculateLeverageMeasure();
        calculateConvictionMeasure();

        int count = 0;
        for (int i = 0; i < lift[num].length; i++) {
            if (i != num)
                if (lift[num][i] > 1)
                    count++;
        }

        String[] recommendElements = new String[count];
        double[] recommendElementsLift = new double[count];

        count = 0;
        for (int i = 0; i < lift[num].length; i++) {
            if (i != num)
                if (lift[num][i] > 1) {
                    recommendElements[count] = element[i];
                    recommendElementsLift[count] = lift[num][i];
                    count++;
                }
        }

        for (int i = 0; i < recommendElementsLift.length; i++) {
            for (int j = i + 1; j < recommendElementsLift.length; j++) {
                if (recommendElementsLift[i] < recommendElementsLift[j]) {
                    double k = recommendElementsLift[i];
                    recommendElementsLift[i] = recommendElementsLift[j];
                    recommendElementsLift[j] = k;

                    String str = recommendElements[i];
                    recommendElements[i] = recommendElements[j];
                    recommendElements[j] = str;
                }
            }
        }

        return recommendElements;
    }

    public int findElementNumber(String name) {
        for (int i = 0 ; i < element.length; i++) {
            if (name.equals(element[i]))
                return i;
        }

        return -1;
    }

    public String getElement(int num) {
        return element[num];
    }

    public String getSignificanceMeasures(int selectedNum, int recommendNum) {
        calculateSupportMeasure();
        calculateConfidenceMeasure();
        calculateLiftMeasure();
        calculateLeverageMeasure();
        calculateConvictionMeasure();

        return "supp = " + String.format("%.2f", support[selectedNum][recommendNum])
                + "\nconf = " + String.format("%.2f", confidence[selectedNum][recommendNum])
                + "\nlift = " + String.format("%.2f", lift[selectedNum][recommendNum])
                + "\nlevr = " + String.format("%.2f", leverage[selectedNum][recommendNum])
                + "%\nconv = " + String.format("%.2f", confidence[selectedNum][recommendNum]);
    }

    @NonNull
    @Override
    public String toString() {
        String data = title.replace("" + divider, "\t") + ":\n";
        for (int i = 0; i < transaction.length; i++) {
            data += transactionNumber[i] + "\t";
            for (int j = 0; j < transaction[i].length; j++) {
                if (transaction[i][j])
                    data += "#" + element[j] + "#, ";
            }
            data = data.substring(0, data.length() - 2) + "\n";
        }
        data = data.substring(0, data.length() - 3);
        return data;
    }
}