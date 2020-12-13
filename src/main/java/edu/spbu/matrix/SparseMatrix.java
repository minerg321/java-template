package edu.spbu.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/**
 * Разряженная матрица
 */
public class SparseMatrix extends Matrix {
    /**
     * Высота и ширина
     **/
    int height, width;
    /**
     * Массив значений
     **/
    double[] elements;
    /**
     * Массив колличеств элиментов до и включая данную строку и массив столбиков
     **/
    int[] numElInRow, columnIndex;
    /** Непосредственно сама матрица **/
    double[][] matrix;
    private String fileName;
    /**
     * Конструктор
     **/
    public SparseMatrix(int height, int width, double[] elements, int[] numElInRow, int[] columnIndex) {
        this.height = height;
        this.width = width;
        this.elements = elements;
        this.numElInRow = numElInRow;
        this.columnIndex = columnIndex;

        double[][] matrix = new double[this.height][this.width];
        /** Заполнение матрицы нулями **/
        for (int i = 0; i < this.height; i++) {
            Arrays.fill(matrix[i], 0);
        }
        /** Заполнение матрицы **/
        for (int i = 0; i < this.numElInRow.length - 1; i++) {
            int startPoint = this.numElInRow[i], endPoint = this.numElInRow[i + 1];
            for (int j = startPoint; j < endPoint; j++) {
                matrix[i][this.columnIndex[j]] = this.elements[j];
            }
        }
        this.matrix = matrix;
    }
    /** получаем Высоту матрицы **/
    @Override
    public int getHeight() {
        return this.height;
    }
    /** получаем Ширину матрицы **/
    @Override
    public int getWidth() {
        return this.width;
    }
    public SparseMatrix(double[][] matrix) {
        this.height = matrix.length;
        this.width = matrix[0].length;
        this.matrix = matrix;
        /** Создаём листы для всех данных **/
        ArrayList<Double> elements = new ArrayList<>();
        ArrayList<Integer> numElInRow = new ArrayList<>();
        ArrayList<Integer> columnIndex = new ArrayList<>();
        /** Обьявим переменную хранящую в себе колличество ненулевых элиментов в строках до и включительно текущей **/
        int numberOfNoZero = 0;
        numElInRow.add(numberOfNoZero);
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (this.matrix[i][j] != 0) {
                    /** Закидываем значение элимент **/
                    elements.add(matrix[i][j]);
                    /** Закидыванием номер столбика элимента **/
                    columnIndex.add(j);
                    /** Увеличиваем счётчик ненулевх элиментов **/
                    numberOfNoZero++;
                }
            }
            /** Для текущей строки записыванием число ненулевых элиментов до неё и в ней**/
            numElInRow.add(numberOfNoZero);
        }
        /** Теперь приведём данные из листов в обычные массивы**/
        /** Сразу сделать было нельзя, так как нахождение размера для каждого массива довольно проблематичное занятие
         * а, брать с запасом слишком затратно в силу природу спарс матриц **/
        double[] newElements = new double[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            newElements[i] = elements.get(i);
        }
        this.elements = newElements;

        int[] newNumElInRow = new int[numElInRow.size()];
        for (int i = 0; i < numElInRow.size(); i++) {
            newNumElInRow[i] = numElInRow.get(i);
        }
        this.numElInRow = newNumElInRow;

        int[] newColumnIndex = new int[columnIndex.size()];
        for (int i = 0; i < columnIndex.size(); i++) {
            newColumnIndex[i] = columnIndex.get(i);
        }
        this.columnIndex = newColumnIndex;
    }

    public SparseMatrix(String fileName) {

        /** Создаём 3 листа и экземпляр сканера **/
        ArrayList<Double> fileElements = new ArrayList<>();
        ArrayList<Integer> fileNumElInRow = new ArrayList<>();
        ArrayList<Integer> fileColumnIndex = new ArrayList<>();
        Scanner fileData;
        /** Проверка на то создался ли сканер**/
        try {
            fileData = new Scanner(new File(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("Невозможно загрузить матрицу: файл не найден");
            return;
        }
        /** Проверка на то не пуст ли файл**/
        if (!fileData.hasNextLine())
        {
            System.err.println("Невозможно загрузить матрицу: файл пуст");
            return;
        }
            /** Вычленям одну строчку из файла**/
            String[] stringRow = fileData.nextLine().split("\\s+");
            /** Так как в файле есть 0ли, то даже по первой строке можно узнать ширину матрицы**/
            int fileWidth = stringRow.length;
            int fileHeight = 0;
            int numberOfNoZero = 0;
            fileNumElInRow.add(0);
            for (int i = 0; i < fileWidth; i++) {
                /** Запишем ненулевые элименты в наш листик**/
                if (!stringRow[i].equals("0")) {
                    /** Значения элиментов**/
                    fileElements.add(Double.parseDouble(stringRow[i]));
                    /** Номер столбца для каждого элимента**/
                    fileColumnIndex.add(i);
                    /** также увеличим соотвественно колличество не нулевых элиментов до данного**/
                    numberOfNoZero++;
                }
            }
            /** Закинем колличество ненулевых элиментов до текушей строки**/
            fileNumElInRow.add(numberOfNoZero);
            /** И перейдём к следующей строке, тобишь увеличим высоту файла на 1**/
            fileHeight++;
            /** Пока строки не закончатся продолжаем делать**/
            while (fileData.hasNextLine()) {
                stringRow = fileData.nextLine().split("\\s+");
                /** Проверка на ошибочку связанную с разной длинной строчек(действительно,
                 *  если размер какой-то строки не совпадёт с размером первой,
                 *  полученную конструкицю нельзя назвать матрицей)**/
                if (stringRow.length != fileWidth) {
                    throw new RuntimeException("Невозможно загрузить матрицу: строки имеют разную длину");
                }
                /** Такой же цикл как и сверху дублируем и для других строк**/
                for (int i = 0; i < fileWidth; i++) {
                    if (!stringRow[i].equals("0")) {
                        fileElements.add(Double.parseDouble(stringRow[i]));
                        fileColumnIndex.add(i);
                        numberOfNoZero++;
                    }
                }
                fileNumElInRow.add(numberOfNoZero);
                fileHeight++;
            }
            /** В конце выполнения получим конечные значения длины высоты и ширины матрицы**/
            this.width = fileWidth;
            this.height = fileHeight;
            /** Теперь точно также как в методе создания спарс матрицы через масив,
             *  переведём наши данные из листов в вид каноничный для нашего конструктора **/
            double[] fileMatrix = new double[fileElements.size()];
            for (int i = 0; i < fileElements.size(); i++) {
                fileMatrix[i] = fileElements.get(i);
            }
            this.elements = fileMatrix;

            int[] rowsSizes = new int[fileNumElInRow.size()];
            for (int i = 0; i < fileNumElInRow.size(); i++) {
                rowsSizes[i] = fileNumElInRow.get(i);
            }
            this.numElInRow = rowsSizes;

            int[] columnIndex = new int[fileColumnIndex.size()];
            for (int i = 0; i < fileColumnIndex.size(); i++) {
                columnIndex[i] = fileColumnIndex.get(i);
            }
            this.columnIndex = columnIndex;
            /** Заполним массивчик нулями**/
            double[][] matrix = new double[this.height][this.width];
            for (int i = 0; i < this.height; i++) {
                Arrays.fill(matrix[i], 0);
            }
            /** И заполним саму матрицу**/
            for (int i = 0; i < this.numElInRow.length - 1; i++) {
                int startPoint = this.numElInRow[i], endPoint = this.numElInRow[i + 1];
                for (int j = startPoint; j < endPoint; j++) {
                    matrix[i][this.columnIndex[j]] = this.elements[j];
                }
            }
            this.matrix = matrix;


    }
    /** Через матричное представление спарс матрицы транспонируем её и далее по методу из
     * конструктора заново собираем элиментики **/
    public Matrix transpose() {
        double[][] transMatrix = new double[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            Arrays.fill(transMatrix[i], 0);
        }

        for (int i = 0; i < this.numElInRow.length - 1; i++) {
            int startPoint = this.numElInRow[i], endPoint = this.numElInRow[i + 1];
            for (int j = startPoint; j < endPoint; j++) {
                transMatrix[this.columnIndex[j]][i] = this.elements[j];
            }
        }
        return new SparseMatrix(transMatrix);
    }
    @Override
    /** Перевод в строку**/
    public String toString() {
        /** Создадим экземплярчик "строителя строк"**/
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                /** Добавляем к текушей строчке элимент конвертированный в строку и после добавляем пробел**/
                sb.append(this.matrix[i][j]).append(' ');
            }
            /** В конец добавим символ конца файла**/
            sb.append('\n');
        }
        /** И в конце вернём непосредственно классическую строку**/
        return sb.toString();
    }
    @Override
    public Matrix mul(Matrix o) {
        if (this.width != o.getHeight()) {
            System.out.println("Операция невозможна: у матриц нет подходящих размеров");
        } else if (o instanceof DenseMatrix) {
            return this.dmul((DenseMatrix) o);
        } else if (o instanceof SparseMatrix) {
            return this.dmul((SparseMatrix) o);
        }
        return null;
    }
    /** Умножение спарс на дэнс**/
    private Matrix dmul(DenseMatrix o)
    {
        /** Умножаемая матрица**/
        final SparseMatrix matrixF = new SparseMatrix(this.matrix);
        /** Матрица на которую умножают**/
        final DenseMatrix matrixS = new DenseMatrix(o.data);
        int newHeight = this.height, newWidth = o.getWidth();
        /** Результирующая матрица**/
        final double[][] newMatrix = new double[newHeight][newWidth];
        /** Заполнение результирующей матрицы нулями**/
        for (int i = 0; i < this.height; i++) {
            Arrays.fill(newMatrix[i], 0);
        }

        class SDMulThreaded implements Runnable {
            /** Обьявляем границы работы потоков**/
            final int subWidthStart, subWidthEnd, subHeightStart, subHeightEnd;

            public SDMulThreaded(int subWidthF, int subWidthS, int subHeightF, int subHeightS) {
                this.subWidthStart = subWidthF;
                this.subWidthEnd = subWidthS;
                this.subHeightStart = subHeightF;
                this.subHeightEnd = subHeightS;
            }

            @Override
            /** Функция работы с каждым потоком**/
            public void run(){
                for (int i = subWidthStart; i < subWidthEnd; i++) {
                    for (int j = subHeightStart; j < subHeightEnd; j++) {
                        int startPoint = matrixF.numElInRow[j], endPoint = matrixF.numElInRow[j + 1];
                        for (int k = startPoint; k < endPoint; k++){
                            newMatrix[j][i] += matrixF.elements[k]*matrixS.data[matrixF.columnIndex[k]][i];
                        }
                    }
                }
            }
        }
        /** найдём серединки матрицы по ширине и по длине**/
        int midHeight = newHeight / 2, midWidth = newWidth / 2;
        try {
            Thread threadFirst = new Thread(new SDMulThreaded(0, midWidth, 0, midHeight));
            Thread threadSecond = new Thread(new SDMulThreaded(midWidth, newWidth, 0, midHeight));
            Thread threadThird = new Thread(new SDMulThreaded(0, midWidth, midHeight, newHeight));
            Thread threadFourth = new Thread(new SDMulThreaded(midWidth, newWidth, midHeight, newHeight));
            /** Запустим каждый поток, и для каждого выполним join() (Чтобы большой поток ждал выполнения маленьких)**/
            threadFirst.start(); threadSecond.start(); threadThird.start(); threadFourth.start();
            threadFirst.join(); threadSecond.join(); threadThird.join(); threadFourth.join();

        }
        /** анализируем ошибочку**/
        catch (Exception e){
            e.printStackTrace();
        }

        return new SparseMatrix(newMatrix);
    }
    /** Умножение спарс на спарс**/
    private Matrix dmul(SparseMatrix o) {
        /** Умножаемая матрица (спарс)**/
        final SparseMatrix matrixStart = new SparseMatrix(this.matrix);
        /** матрица-множитель матрица (спарс)**/
        /** транспонирование для возможности умножения**/
        final SparseMatrix matrixEnd = (SparseMatrix)o.transpose();
        /**создание новой матрицы**/
        int newHeight = this.height, newWidth = o.getWidth();
        /** заполнение нулями**/
        final double[][] newMatrix = new double[newHeight][newWidth];
        for (int i = 0; i < this.height; i++) {
            Arrays.fill(newMatrix[i], 0);
        }
        /** Обьявляем границы работы потоков**/
        class SSMulThreaded implements Runnable {
            final int subHeightStart, subHeightEnd, subWidthStart, subWidthEnd;

            public SSMulThreaded(int subHeightF, int subHeightS, int subWidthF, int subWidthS) {
                this.subHeightStart = subHeightF;
                this.subHeightEnd = subHeightS;
                this.subWidthStart = subWidthF;
                this.subWidthEnd = subWidthS;
            }

            @Override
            /** Общая функция работы потока с выданной ему частью данных**/
            public void run() {
                /** Принцип тот же что и в обычном умножении, только имеется дробление значений на группы для передачи задач на несколько потоков**/
                /** Конечно можно было разделить на 4 группы строк тем самым не обрезать процесс умножения, но тогда проблема с нахождением элимента
                 * и столбика в котором он лежит в силу особенностей представления спарс матриц **/
                for (int i = subHeightStart; i < subHeightEnd; i++) {
                    int startPointF = matrixStart.numElInRow[i], endPointF = matrixStart.numElInRow[i + 1];
                    for (int j = subWidthStart; j < subWidthEnd; j++) {
                        int startPointS = matrixEnd.numElInRow[j], endPointS = matrixEnd.numElInRow[j + 1];
                        int currentPointF = startPointF, currentPointS = startPointS;

                        while ((currentPointF < endPointF) && (currentPointS < endPointS)) {
                            if (matrixStart.columnIndex[currentPointF] == matrixEnd.columnIndex[currentPointS]) {
                                newMatrix[i][j] += matrixStart.elements[currentPointF] * matrixEnd.elements[currentPointS];
                                currentPointF++;
                                currentPointS++;
                            } else if (matrixStart.columnIndex[currentPointF] < matrixEnd.columnIndex[currentPointS]) {
                                currentPointF++;
                            } else {
                                currentPointS++;
                            }
                        }
                    }
                }
            }
        }

        int midHeight = newHeight / 2, midWidth = newWidth / 2;
        try {
            Thread threadFirst = new Thread(new SSMulThreaded(0, midHeight, 0, midWidth));
            Thread threadSecond = new Thread(new SSMulThreaded(0, midHeight, midWidth, newWidth));
            Thread threadThird = new Thread(new SSMulThreaded(midHeight, newHeight, 0, midWidth));
            Thread threadFourth = new Thread(new SSMulThreaded(midHeight, newHeight, midWidth, newWidth));

            threadFirst.start(); threadSecond.start(); threadThird.start(); threadFourth.start();
            threadFirst.join(); threadSecond.join(); threadThird.join(); threadFourth.join();

        } catch (Exception e){
            e.printStackTrace();
        }

        return new SparseMatrix(newMatrix);
    }


    /**
     * однопоточное умнджение матриц
     * должно поддерживаться для всех 4-х вариантов
     *
     * @param o
     * @return
     */
    @Override
    /** Получение элимента матрицы **/
    public double getElement(int i, int j) {
        int st = numElInRow[i], fn = numElInRow[i + 1];
        for (int w = st; w < fn; w++) {
            if (columnIndex[w] > j) return 0;
            else if (columnIndex[w] == j) return elements[w];
        }
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof DenseMatrix) {
            return this.sdequals(o);
        } else {
            return this.ssequal(o);
        }
    }

    public boolean ssequal(Object o) {
        SparseMatrix s = (SparseMatrix) o;
        if ((this.h != s.h) || (this.w != s.w)) return false;
        return (Arrays.equals(this.elements, s.elements)) && (Arrays.equals(this.columnIndex, s.columnIndex))
                && (Arrays.equals(this.numElInRow, s.numElInRow));
    }

    public boolean sdequals(Object o) {
        DenseMatrix m = (DenseMatrix) o;
        if ((this.height != m.h) || (this.width != m.w)) return false;
        for (int i = 0; i < m.h; i++) {
            for (int j = 0; j < m.w; j++) {
                if (m.data[i][j] != this.getElement(i, j)) return false;
            }
        }
        return true;
    }

}